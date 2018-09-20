/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.shapefile.ShapeFileClass;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.osm.schema.ShapeOSMToLayerMapping.OsmTagFilter;
import fr.ign.cogit.cartagen.osm.schema.ShapeOSMToLayerMapping.ShapeOSMToLayerMatching;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.CRSConversion;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class OSMShapefileLoader extends SwingWorker<Void, Void> {

  private Logger logger = Logger.getLogger(OSMShapefileLoader.class.getName());

  // the global tags of OSM files
  public static final String TAG_BOUNDS = "bounds";
  public static final String TAG_MIN_LAT = "minlat";
  public static final String TAG_MAX_LAT = "maxlat";
  public static final String TAG_MIN_LON = "minlon";
  public static final String TAG_MAX_LON = "maxlon";

  public enum OsmLoadingTask {
    POINTS, LINES, RELATIONS, OBJECTS, PARSING, MULTIGEOMETRIES;

    public String getLabel() {
      if (this.equals(POINTS))
        return "OSM Points";
      else if (this.equals(LINES))
        return "OSM Lines";
      else if (this.equals(RELATIONS))
        return "OSM Relations";
      else if (this.equals(PARSING))
        return "Parsing";
      else if (this.equals(OBJECTS))
        return "Objects";
      else if (this.equals(MULTIGEOMETRIES))
        return "Multiple Geometries";
      return "";
    }
  }

  private File[] files;
  /**
   * true if the geometries have to be projected.
   */
  private boolean project = false;
  private String epsg;
  private OsmDataset dataset;
  private ShapeOSMToLayerMapping mapping;
  private JDialog dialog;
  private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;
  private Runnable fillLayersTask;
  double xMin, yMin, xMax, yMax;
  double xCentr, yCentr;
  double surf;
  private int nbNoeuds = 0, nbWays = 0, nbRels = 0, nbResources = 0;

  public OSMShapefileLoader(File[] files, OsmDataset dataset,
      Runnable fillLayersTask, String epsg, ShapeOSMToLayerMapping mapping) {
    this.files = files;
    this.dataset = dataset;
    this.fillLayersTask = fillLayersTask;
    this.mapping = mapping;
    this.epsg = epsg;
  }

  @SuppressWarnings("unchecked")
  public void importOsmData()
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchFieldException, SecurityException {
    for (File file : files) {
      ShapeOSMToLayerMatching matching = mapping.getMatchingFromFile(file);

      // Open the shapefile
      ShapefileReader shr = null;
      DbaseFileReader dbr = null;

      try {
        ShpFiles shpf = new ShpFiles(file.getAbsolutePath());
        shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
        dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
      } catch (FileNotFoundException e) {
        if (logger.isDebugEnabled()) {
          logger.debug("fichier " + file.getAbsolutePath() + " non trouve.");
        }
        e.printStackTrace();
        continue;
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("files opened");

      if (logger.isInfoEnabled()) {
        logger.info("Loading: " + file.getAbsolutePath());
      }

      // Create the population
      IGeneObj elementDef = (IGeneObj) matching.getCreationMethod()
          .invoke(mapping.getGeneObjImplementation().getCreationFactory());
      String elementClass = (String) elementDef.getClass()
          .getField("FEAT_TYPE_NAME").get(null);
      IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) dataset
          .getCartagenPop(dataset.getPopNameFromObj(elementDef), elementClass);

      IGeometry geom = null;
      int w = 0;
      // Create Calac object for each postGIS object
      try {
        while (shr.hasNext()) {
          Record objet = shr.nextRecord();
          System.out.println(w);

          // Get the geometry
          try {
            geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
            // put it in the proper CRS
            if (project)
              geom = CRSConversion.changeCRS(geom, "4326", epsg, false, true,
                  true);
          } catch (Exception e) {
            e.printStackTrace();
            continue;
          }

          // Break done the geometry if it is a multiple geometry
          List<IGeometry> listGeom = new ArrayList<IGeometry>();
          if (geom.isMultiSurface() || geom.isMultiCurve()) {
            IMultiPrimitive<?> multiGeom = ((IMultiPrimitive<?>) geom);
            for (Integer i = 0; i < multiGeom.size(); i++) {
              IGeometry geomPart = multiGeom.get(i);
              listGeom.add(geomPart);
            }
          } else
            listGeom.add(geom);

          // get the fields
          Object[] champs = null;
          try {
            champs = dbr.readEntry();
          } catch (IOException e) {
            // skip the feature
            System.out.println("got the exception");
            continue;
          }
          Map<String, Object> fields = new HashMap<String, Object>();
          Map<String, String> tags = new HashMap<String, String>();
          for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
            fields.put(dbr.getHeader().getFieldName(i), champs[i]);
            String key = matching.getListTags()
                .get(dbr.getHeader().getFieldName(i));
            tags.put(key, champs[i].toString());
          }

          // check the tag filter
          if (matching.getFilter() != null) {
            OsmTagFilter filter = matching.getFilter();
            String tagValue = tags.get(filter.getKey());
            if (!filter.getValues().contains(tagValue))
              continue;
          }

          // For each part, create a Java element
          for (Integer i = 0; i < listGeom.size(); i++) {
            IGeometry geomListPart = listGeom.get(i);
            // Create the Calac object
            OsmGeneObj element = (OsmGeneObj) matching.getCreationMethod()
                .invoke(
                    mapping.getGeneObjImplementation().getCreationFactory());
            // affecte la géométrie
            element.setGeom(geomListPart);
            // get the tags of the object
            element.setTags(tags);

            // set the OSM id
            element.setOsmId(Long.valueOf((String) fields.get("osm_id")));

            // Get the geometry type
            Class<? extends IGeometry> geomType = null;
            if (geom.isPoint())
              geomType = IPoint.class;
            else if (geom.isLineString() || geom.isMultiCurve())
              geomType = ILineString.class;
            else if (geom.isPolygon() || geom.isMultiSurface())
              geomType = IPolygon.class;

            // Create geoClass
            CartAGenDB database = dataset.getCartAGenDB();
            ShapeFileClass geoClass = new ShapeFileClass(database,
                file.getAbsolutePath(), elementClass, geomType);
            if (!database.getClasses().contains(geoClass)) {
              database.addClass(geoClass);
            }

            // Add this object to the population
            pop.add(element);
          }
          w = w + 1;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  protected Void doInBackground() throws Exception {
    importOsmData();
    return null;
  }

  @Override
  protected void done() {
    // this.dialog.setVisible(false);
    super.done();
    SwingUtilities.invokeLater(fillLayersTask);
  }

  public OsmLoadingTask getCurrentTask() {
    return currentTask;
  }

  public void setCurrentTask(OsmLoadingTask currentTask) {
    this.currentTask = currentTask;
  }

  public JDialog getDialog() {
    return dialog;
  }

  public void setDialog(JDialog dialog) {
    this.dialog = dialog;
  }

  public void setProgressForBar(int i) {
    this.setProgress(i);
  }

  public int getNbNoeuds() {
    return nbNoeuds;
  }

  public void setNbNoeuds(int nbNoeuds) {
    this.nbNoeuds = nbNoeuds;
  }

  public int getNbWays() {
    return nbWays;
  }

  public void setNbWays(int nbWays) {
    this.nbWays = nbWays;
  }

  public int getNbRels() {
    return nbRels;
  }

  public void setNbRels(int nbRels) {
    this.nbRels = nbRels;
  }

  public int getNbResources() {
    return nbResources;
  }

  public void setNbResources(int nbResources) {
    this.nbResources = nbResources;
  }

  public boolean isProject() {
    return project;
  }

  public void setProject(boolean project) {
    this.project = project;
  }

}
