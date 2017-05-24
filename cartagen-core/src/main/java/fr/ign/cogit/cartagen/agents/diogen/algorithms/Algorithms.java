package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class Algorithms {

  private static Logger logger = Logger.getLogger(Algorithms.class.getName());

  public static Set<ICurveSegment> getParallelLines(Vector2D vector,
      Set<INetworkSection> linesSet) {
    return Algorithms.getParallelLines(vector, linesSet, Math.PI / 6);
  }

  public static Set<ICurveSegment> getParallelLines(Vector2D vector,
      Set<INetworkSection> linesSet, double threshold) {
    Algorithms.logger.debug("Vector 1 " + vector);
    Set<ICurveSegment> toReturn = new HashSet<ICurveSegment>();
    for (INetworkSection section : linesSet) {
      Algorithms.logger.debug("Section " + section);
      Algorithms.logger.debug("Feature " + section.getGeom());
      for (ICurveSegment segment : (section.getGeom()).getSegment()) {

        Algorithms.logger.debug("Segment " + segment.coord());
        Vector2D vectorFromSegment = new Vector2D(segment.coord().get(0),
            segment.coord().get(1));

        Algorithms.logger.debug("Vector 2 " + vectorFromSegment);
        double angle = vectorFromSegment.angleVecteur(vector).getValeur();

        Algorithms.logger.debug("Angle " + angle);
        if ((Math.abs(angle) < threshold)
            || (Math.abs(angle - Math.PI) < threshold)) {
          toReturn.add(segment);
        }
      }
    }
    return toReturn;
  }

  /**
   * Return the projection of m on the line passing by a and b
   * @param m
   * @param a
   * @param b
   * @return
   */
  public static IDirectPosition orthogonalProjection(IDirectPosition m,
      IDirectPosition a, IDirectPosition b) {

    double ea = -(b.getY() - a.getY());
    double eb = b.getX() + a.getX();

    double ec = -(ea * a.getX() + eb * a.getY());

    double denom = ea * ea + eb * eb;

    double xm = (eb * eb * m.getX() - ea * m.getY() - ea * ec) / denom;
    double ym = (ea * ea * m.getY() - ea * m.getX() - eb * ec) / denom;

    return new DirectPosition(xm, ym, Double.NaN);

  }

  /**
   * Return true if the point is on the left side of the line, false elsewhere.
   * @param element
   * @param line
   * @return
   */
  public static boolean isOnTheLeftSide(IGeneObj element, IGeneObjLin line) {
    IDirectPosition center = element.getGeom().centroid();

    IDirectPosition projectedPoint = null;
    IDirectPosition endPoint = null;

    double minDistance = Double.MAX_VALUE;

    for (ICurveSegment segment : line.getGeom().getSegment()) {
      IDirectPosition start = segment.startPoint();
      IDirectPosition end = segment.endPoint();

      IDirectPosition projection = Algorithms.orthogonalProjection(center,
          start, end);

      IDirectPosition result = CommonAlgorithmsFromCartAGen.projection(center,
          (ILineString) segment, new Vector2D(center, projection));

      if (result != null) {
        double distance = result.distance(center);
        if (distance < minDistance) {
          projectedPoint = result;
          endPoint = end;
          minDistance = distance;
        }
      }
    }

    double angle = Algorithms.angle(projectedPoint, endPoint, center);

    if (angle > Math.PI) {
      return false;
    }
    return true;

  }

  /**
   * 
   * @param o
   * @param a
   * @param b
   * @return
   */
  public static double angle(IDirectPosition o, IDirectPosition a,
      IDirectPosition b) {

    // Calculation of the coordinate of oa vector
    double xoa = o.getX() - a.getX();
    double yoa = o.getY() - a.getY();

    // Calculation of the coordinate of ob vector
    double xob = o.getX() - b.getX();
    double yob = o.getY() - b.getY();

    double angle = Math.atan2(xoa * yob - yoa * xob, xoa * xob + yoa * yob);
    if (angle < 0) {
      angle = angle + 2 * Math.PI;
    }

    return angle;

  }
  //
  // public static INetworkSection getConnectedRoadToDirection(
  // IEmbeddedDeadEndArea edea, boolean left) {
  //
  // INetworkSection toReturn = null;
  //
  // // get the point of the other side of the root section
  // IDirectPosition point1;
  // IDirectPosition startTest = edea.getDeadEnd().getRoot().getGeom()
  // .startPoint();
  // IDirectPosition endTest = edea.getDeadEnd().getRoot().getGeom().endPoint();
  //
  // if (edea.getRootDirectPosition().distance(startTest) > edea
  // .getRootDirectPosition().distance(endTest)) {
  // point1 = edea.getDeadEnd().getRoot().getGeom()
  // .getSegment(edea.getDeadEnd().getRoot().getGeom().sizeSegment() - 1)
  // .startPoint();
  // } else {
  // point1 = edea.getDeadEnd().getRoot().getGeom().getSegment(0).endPoint();
  // }
  //
  // // Calculation of the coordinate of point1-root vector
  // double x10 = point1.getX() - edea.getRootDirectPosition().getX();
  // double y10 = point1.getY() - edea.getRootDirectPosition().getY();
  //
  // double thresholdtAngle;
  // if (!left) {
  // thresholdtAngle = Double.MAX_VALUE;
  // } else {
  // thresholdtAngle = Double.MIN_VALUE;
  // }
  // // for each connected element from the network, find the angle
  // for (INetworkSection section : edea.getConnectedNetwork()) {
  // IDirectPosition start = section.getGeom().startPoint();
  // IDirectPosition end = section.getGeom().endPoint();
  //
  // ICurveSegment segment;
  // IDirectPosition point3;
  //
  // if (edea.getRootDirectPosition().distance(start) > edea
  // .getRootDirectPosition().distance(end)) {
  // segment = section.getGeom().getSegment(
  // section.getGeom().sizeSegment() - 1);
  // point3 = segment.startPoint();
  // // pointForSegment.put(section, segment.endPoint());
  // } else {
  // segment = section.getGeom().getSegment(0);
  // point3 = segment.endPoint();
  // // pointForSegment.put(section, segment.startPoint());
  // }
  //
  // // compute the angles
  //
  // // Calculation of the coordinate of root-point3 vector
  // double x12 = edea.getRootDirectPosition().getX() - point3.getX();
  // double y12 = edea.getRootDirectPosition().getY() - point3.getY();
  //
  // double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);
  // if (angle < 0) {
  // angle = angle + 2 * +Math.PI;
  // }
  //
  // // Absolute value of the angle
  // // angle = Math.abs(angle);
  //
  // // segmentToSupress.put(section, segment);
  // // pointForSegment.put(section, point3);
  //
  // Algorithms.logger.debug("Angle between dead end and " + section + " = "
  // + angle);
  //
  // if (((angle > thresholdtAngle) && (left))
  // || ((angle < thresholdtAngle) && (!left))) {
  // thresholdtAngle = angle;
  // toReturn = section;
  // // toGo = point3;
  // }
  //
  // }
  //
  // return toReturn;
  // }

}
