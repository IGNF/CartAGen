package fr.ign.cogit.cartagen.appli.plugins.perso;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRandom;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObj;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class PluginGuillaume extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public PluginGuillaume(String title) {
    super(title);
    CartAGenPlugin.getInstance().getApplication();

    JMenu menuTuto = new JMenu("Tutorials");
    menuTuto.add(new JMenuItem(new AlgoTutoAction()));
    this.add(menuTuto);
    this.addSeparator();

    JMenu menuLund = new JMenu("Lund dataset");
    menuLund.add(new JMenuItem(new ExportShapeAction()));
    this.add(menuLund);
    this.addSeparator();

    this.add(new JMenuItem(new QuickTestAction()));
  }

  /**
   * Action for quick tests, code is replaced each time a new test is carried
   * out.
   * 
   * @author GTouya
   * 
   */
  class QuickTestAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      // DO NOTHING
    }

    public QuickTestAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Quick test of a misc code");
      this.putValue(Action.NAME, "Quick Test");
    }
  }

  /**
   * Identifies the roundabouts in the selected road layer, and creates a new
   * layer with the roundabouts.
   * 
   * @author GTouya
   * 
   */
  class AlgoTutoAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset()
          .getSld();
      SLDUtilCartagen.changeSymbolisationScale(30000.0, sld);

      // get the network sections that delimit groups (i.e. roads and rivers)
      IFeatureCollection<IFeature> sections = new FT_FeatureCollection<>();
      sections.addAll(dataset.getRoads());
      sections.addAll(dataset.getWaterLines());

      // create the groups with a 25.0 m threshold for building buffering
      Collection<IUrbanBlock> groups = UrbanEnrichment
          .createBuildingGroups(sections, 25.0, 10.0, 12, 2.0, 5000.0);

      for (IUrbanBlock group : groups) {

        for (IUrbanElement building : group.getUrbanElements()) {
          // in this case, we only added buildings as urban elements, so no need
          // to check (there can be parks, squares, sports fields...)
          // the class GeneralisationSpecifications is used: it contains
          // standard values for classical generalisation specifications, such
          // as the minimum size of a building in map mm²

          // first enlarge the building geometry
          // compute the goal area
          double area = building.getGeom().area();
          double goalArea = area;
          double aireMini = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
              * Legend.getSYMBOLISATI0N_SCALE()
              * Legend.getSYMBOLISATI0N_SCALE() / 1000000.0;
          if (area <= aireMini) {
            goalArea = aireMini;
          }
          // compute the homothety of the building geometry
          IPolygon geom = CommonAlgorithms.homothetie(
              (IPolygon) building.getGeom(), Math.sqrt(goalArea / area));

          // then simplify the building
          IGeometry simplified = SimplificationAlgorithm.simplification(geom,
              GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);

          // apply the new geometry to the building
          building.setGeom(simplified);
        }

        // trigger the displacement of the enlarged and simplified features
        BuildingDisplacementRandom.compute(group);
      }
    }

    public AlgoTutoAction() {
      this.putValue(Action.NAME, "Algorithms tutorial");
    }
  }

  class ExportShapeAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Class<? extends IGeometry> geomType = ILineString.class;

      // get the features to export
      Collection<? extends IFeature> iterable = CartAGenDoc.getInstance()
          .getCurrentDataset().getRoads();

      IFeatureCollection<IFeature> features = new FT_FeatureCollection<IFeature>();
      for (IFeature obj : iterable) {
        if (!(obj instanceof IGeneObj)) {
          features.add(obj);
          continue;
        }
        if (!((IGeneObj) obj).isEliminated()) {
          features.add((IGeneObj) obj);
        }
      }

      // write the shapefile
      write(features, geomType,
          "D:\\Donnees\\OSM\\Lund_workshop\\derived layers\\roads.shp",
          "roads");
    }

    public ExportShapeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Export roads to shapefile with all tags");
      this.putValue(Action.NAME, "Export roads to shapefile");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <Feature extends IFeature> void write(
        IFeatureCollection<IFeature> featurePop,
        Class<? extends IGeometry> geomType, String shpName, String layerName) {
      if (featurePop == null) {
        return;
      }
      if (featurePop.isEmpty()) {
        return;
      }
      String shapefileName = shpName;
      try {
        if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
          shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
        }
        ShapefileDataStore store = new ShapefileDataStore(
            new File(shapefileName).toURI().toURL());

        // specify the geometry type
        String specs = "the_geom:"; //$NON-NLS-1$
        specs += "LineString";

        // specify the attributes: there is only one the MRDB link
        specs += "," + "osm_id" + ":" + "String";
        specs += "," + "fclass" + ":" + "String";
        specs += "," + "name" + ":" + "String";

        SimpleFeatureType type = DataUtilities.createType(layerName, specs);
        store.createSchema(type);
        ContentFeatureStore featureStore = (ContentFeatureStore) store
            .getFeatureSource(layerName);
        Transaction t = new DefaultTransaction();
        Collection features = new HashSet<>();
        int i = 1;
        for (IFeature feature : featurePop) {
          if (feature.isDeleted()) {
            continue;
          }
          List<Object> liste = new ArrayList<Object>(0);
          // change the CRS if needed
          IGeometry geom = feature.getGeom();
          if ((geom instanceof ILineString) && (geom.coord().size() < 2))
            continue;

          liste.add(AdapterFactory.toGeometry(new GeometryFactory(), geom));
          // liste.add(feature.getId());
          // put the attributes in the list, after the geometry
          liste.add(String.valueOf(((OsmGeneObj) feature).getOsmId()));
          liste.add(
              String.valueOf(((OsmGeneObj) feature).getTags().get("highway")));
          liste.add(
              String.valueOf(((OsmGeneObj) feature).getTags().get("name")));
          SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type,
              liste.toArray(), String.valueOf(i++));
          System.out.println(i);
          System.out.println(liste);
          features.add(simpleFeature);
        }
        featureStore.addFeatures(features);
        t.commit();
        t.close();
        store.dispose();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SchemaException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

}
