package fr.ign.cogit.cartagen.algorithms.network.roads;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class SlideDeadEnd {

  private static Logger logger = LogManager.getLogger(SlideDeadEnd.class.getName());

  /**
   * The dead end group the algorithm has to slide
   */
  private DeadEndGroup deadEnd;

  /**
   * the network section that will be used as a slider to displace the dead end.
   */
  private INetworkSection sectionToSlideOn;
  /**
   * the curvilinear abscisse that represents the amount of sliding of the dead
   * end on the network section.
   */
  private double curvAbsc;
  /**
   * The result of the algorithm, a map of the modified objects with their new
   * geometry as key
   */
  private HashMap<IGeneObj, IGeometry> newGeometries;
  /**
   * In the case of a newly created section, this is the geometry to build the
   * new INetworkSection
   */
  private ILineString newSectionGeometry;

  /**
   * The translation vector computed by the algorithm
   */
  private Vector2D translationVector;

  public SlideDeadEnd(DeadEndGroup deadEnd, INetworkSection sectionToSlideOn,
      double curvAbsc) {
    super();
    this.deadEnd = deadEnd;
    this.sectionToSlideOn = sectionToSlideOn;
    this.curvAbsc = curvAbsc;
  }

  /**
   * Execute the algorithm that slides the dead end on a network section. If the
   * method returns true, a new INetworkSection has to be built to maintain
   * connections in the network. The new object's geometry is stored in
   * newSectionGeometry.
   * 
   * @return
   * @author GTouya
   */
  public boolean execute() {
    newGeometries = new HashMap<IGeneObj, IGeometry>();
    // particular case with curvelinear abscisse bigger than section length
    if (curvAbsc > sectionToSlideOn.getGeom().length()) {
      // then move the dead end to section final point
      IDirectPosition root = deadEnd.getRootNode().getPosition();
      IDirectPosition endPoint = sectionToSlideOn.getGeom().coord().get(0);

      if (endPoint.equals2D(root))
        endPoint = sectionToSlideOn.getGeom().coord().get(
            sectionToSlideOn.getGeom().coord().size() - 1);
      Vector2D vect = new Vector2D(root, endPoint);
      // now loop on the dead end features to translate them
      for (INetworkSection section : deadEnd.getFeatures()) {
        ILineString newLine = CommonAlgorithms.translation(section.getGeom(),
            vect.getX(), vect.getY());
        newGeometries.put(section, newLine);
      }
      this.setTranslationVector(vect);
      // update the sections connections
      INetworkSection rootSection = deadEnd.getRoot();
      boolean in = true;
      if (deadEnd.getRootNode().getOutSections().contains(rootSection))
        in = false;
      if (in)
        deadEnd.getRootNode().getInSections().remove(rootSection);
      else
        deadEnd.getRootNode().getOutSections().remove(rootSection);
      INetworkNode newRoot = sectionToSlideOn.getFinalNode();
      if (newRoot.equals(deadEnd.getRootNode()))
        newRoot = sectionToSlideOn.getInitialNode();
      if (in) {
        newRoot.getInSections().add(rootSection);
        rootSection.setFinalNode(newRoot);
      } else {
        newRoot.getOutSections().add(rootSection);
        rootSection.setInitialNode(newRoot);
      }

      return false;
    }

    // compute the displacement vector
    IDirectPosition root = deadEnd.getRootNode().getPosition();
    ILineString line = sectionToSlideOn.getGeom();

    if (!line.coord().get(0).equals2D(root))
      line = line.reverse();
    IDirectPosition endPoint = Operateurs.pointEnAbscisseCurviligne(line,
        curvAbsc);
    Vector2D vect = new Vector2D(root, endPoint);

    // now loop on the dead end features to translate them
    for (INetworkSection section : deadEnd.getFeatures()) {
      ILineString newLine = CommonAlgorithms.translation(section.getGeom(),
          vect.getX(), vect.getY());
      newGeometries.put(section, newLine);
    }
    this.setTranslationVector(vect);
    // now updates the network sections connected to the dead end
    // three cases : 0,2 or >2 sections connected
    // first get the connected sections

    // first easy case
    if (deadEnd.getFeaturesConnectedToRoot().size() < 2)
      return false;

    // classical case with 2 connected sections
    if (deadEnd.getFeaturesConnectedToRoot().size() == 2) {
      // shorten the sectionToSlideOn
      int id = 0;
      for (int i = 1; i < line.coord().size(); i++) {
        if (Operateurs.abscisseCurviligne(line, i) > curvAbsc)
          break;
        id++;
      }
      IDirectPositionList shortenList = new DirectPositionList();
      shortenList.add(endPoint);
      logger.debug("Add point " + endPoint + " to shorten list.");
      for (int i = id + 1; i < line.coord().size(); i++) {
        shortenList.add(line.coord().get(i));
        logger.debug("Add point " + line.coord().get(i) + " to shorten list.");
      }
      // shortenList.add(endPoint);
      newGeometries.put(sectionToSlideOn, new GM_LineString(shortenList));

      // lengthen the other section
      Iterator<INetworkSection> iter = deadEnd.getFeaturesConnectedToRoot()
          .iterator();
      INetworkSection otherSection = iter.next();
      if (otherSection.equals(sectionToSlideOn))
        otherSection = iter.next();
      IDirectPositionList lengthenList = new DirectPositionList();
      boolean start = true;
      if (otherSection.getFinalNode().equals(deadEnd.getRootNode()))
        start = false;
      if (start) {
        // lengthen the geometry from the start
        lengthenList.addAll(otherSection.getGeom().coord());
        for (int i = 1; i < id + 1; i++) {
          lengthenList.add(0, line.coord().get(i));
          logger.debug("Add point " + line.coord().get(i)
              + " to lengthen list.");
        }
        lengthenList.add(0, endPoint);
        logger.debug("Add point " + endPoint + " to lengthen list.");
      } else {
        // lengthen the geometry at the end
        lengthenList.addAll(otherSection.getGeom().coord());
        for (int i = 1; i < id; i++) {
          lengthenList.add(line.coord().get(i));
          logger.debug("Add point " + line.coord().get(i)
              + " to lengthen list.");
        }
        lengthenList.add(endPoint);
        logger.debug("Add point " + endPoint + " to lengthen list.");
      }
      newGeometries.put(otherSection, new GM_LineString(lengthenList));

      // update the root node
      newGeometries.put(deadEnd.getRootNode(), new GM_Point(endPoint));

      return false;
    }

    // final case with more than 2 connected sections
    if (deadEnd.getFeaturesConnectedToRoot().size() > 2) {
      // in this case, only the sectionToSlideOn is shortened and a new section
      // has to be built
      // shorten the sectionToSlideOn
      int id = 0;
      for (int i = 1; i < line.coord().size(); i++) {
        if (Operateurs.abscisseCurviligne(line, i) > curvAbsc)
          break;
        id++;
      }
      IDirectPositionList shortenList = new DirectPositionList();
      shortenList.add(endPoint);
      for (int i = id + 1; i < line.coord().size(); i++)
        shortenList.add(line.coord().get(i));
      shortenList.add(endPoint);
      newGeometries.put(sectionToSlideOn, new GM_LineString(shortenList));

      // builds the geometry for the new section
      IDirectPositionList newGeomList = new DirectPositionList();
      for (int i = 0; i < id + 1; i++)
        newGeomList.add(line.coord().get(i));
      newGeomList.add(endPoint);
      this.newSectionGeometry = new GM_LineString(newGeomList);
      return true;
    }
    return false;
  }

  public DeadEndGroup getDeadEnd() {
    return deadEnd;
  }

  public void setDeadEnd(DeadEndGroup deadEnd) {
    this.deadEnd = deadEnd;
  }

  public INetworkSection getSectionToSlideOn() {
    return sectionToSlideOn;
  }

  public void setSectionToSlideOn(INetworkSection sectionToSlideOn) {
    this.sectionToSlideOn = sectionToSlideOn;
  }

  public double getCurvAbsc() {
    return curvAbsc;
  }

  public void setCurvAbsc(double curvAbsc) {
    this.curvAbsc = curvAbsc;
  }

  public HashMap<IGeneObj, IGeometry> getNewGeometries() {
    return newGeometries;
  }

  public void setNewGeometries(HashMap<IGeneObj, IGeometry> newGeometries) {
    this.newGeometries = newGeometries;
  }

  public ILineString getNewSectionGeometry() {
    return newSectionGeometry;
  }

  public void setNewSectionGeometry(ILineString newSectionGeometry) {
    this.newSectionGeometry = newSectionGeometry;
  }

  public Vector2D getTranslationVector() {
    return translationVector;
  }

  public void setTranslationVector(Vector2D translationVector) {
    this.translationVector = translationVector;
  }

}
