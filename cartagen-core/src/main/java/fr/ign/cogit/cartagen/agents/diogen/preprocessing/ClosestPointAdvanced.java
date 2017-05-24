package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class ClosestPointAdvanced {

  /**
   * * Return the point of g which are closest of p, a point inside g, and in
   * the good side (left or right) of l.
   * @param point
   * @param polygon
   * @param left
   * @return
   */
  public static IDirectPosition getClosestPointsInDirection(
      IDirectPosition point, IDirectPosition previousPoint,
      IDirectPosition nextPoint, IGeometry geom, boolean left) {

    double minDistance = Double.MAX_VALUE;
    IDirectPosition c0;
    IDirectPosition c2;

    if (previousPoint != null) {
      c0 = previousPoint;
      c2 = point;
    } else {
      c0 = point;
      c2 = nextPoint;
    }
    IDirectPosition toReturn = null;
    for (IDirectPosition p : geom.coord()) {
      double distance = point.distance2D(p);
      if (distance < minDistance) {
        // Calculation of the angle (c0, p, c2) in a radian value between -pi
        // and pi.

        // Calculation of the coordinate of c1-c0 vector
        double x10 = c0.getX() - p.getX();
        double y10 = c0.getY() - p.getY();

        // Calculation of the coordinate of c1-c0 vector
        double x12 = c2.getX() - p.getX();
        double y12 = c2.getY() - p.getY();

        double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);

        // angle = angle % (2 * Math.PI);
        if ((left && (angle >= 0)) || (!left && (angle <= 0))) {
          minDistance = distance;
          toReturn = p;
        }

      }
    }

    return toReturn;

  }
}
