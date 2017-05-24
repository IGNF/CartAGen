package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class RubberSheeting {

  // TODO not finished

  /**
   * The sections to move.
   */
  private Set<INetworkSection> sections;
  /**
   * The points used to move the sections, associated with the base vector of
   * pushing.
   */
  private Map<IDirectPosition, Double> points;

  /**
   * Constructor for the algorithm
   * @param sections the sections to move
   * @param points the points used to move the sections, associated with the
   *          base vector of pushing
   */
  public RubberSheeting(Set<INetworkSection> sections,
      Map<IDirectPosition, Double> points) {
    this.sections = sections;
    this.points = points;
  }

  /**
   * Method to execute the algorithm.
   */
  public void compute() {
    // for each section, we call the method to move it according to the points
    // and vectors.
    for (INetworkSection section : sections) {
      this.moveSection(section);
    }
  }

  private void moveSection(INetworkSection section) {

    // parameters for raytracing
    double buildingSize = Math
        .sqrt(GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double basicDistance = 2 * buildingSize;

    // the new point list
    IDirectPositionList newCoords = new DirectPositionList();

    // for each point of the section, compute the vector and move the point.
    for (IDirectPosition pointToMove : section.getGeom().coord()) {
      IDirectPosition newPoint = (IDirectPosition) pointToMove.clone();
      // for each point used as referent for the move
      for (IDirectPosition basePoint : points.keySet()) {
        // the base vector
        Vector2D baseVector = new Vector2D(basePoint, pointToMove);
        // Vector2D baseVector = points.get(basePoint);
        // the distance between the pushing and pushed point
        double distance = pointToMove.distance(basePoint);
        // the pushing vector is adapted depending of the position of the point
        // to move
        double lambda = 2 * (basicDistance - points.get(basePoint));
        lambda = lambda * lambda / (distance * points.size());
        Vector2D moveVector = baseVector.changeNorm(lambda);
        // translate the point
        newPoint = moveVector.translate(newPoint);
      }
      newCoords.add(newPoint);
    }
    section.getGeom().coord().setList(newCoords.getList());
  }
  // private void intersectionOfRoadAndPolygon() {
  //
  // }
  //
  // private IPolygon createRectangleFromLine(ILineString line, double height,
  // boolean clockWise) {
  //
  // ILineString points = (ILineString) line.clone();
  // for (IDirectPosition point : points.coord()) {
  //
  // }
  // points.addControlPoint(value);
  // IPolygon toReturn = new GM_Polygon();
  // IRing exterior = new GM_Ring(points);
  // toReturn.setExterior(exterior);
  //
  // return toReturn;
  //
  // }

}
