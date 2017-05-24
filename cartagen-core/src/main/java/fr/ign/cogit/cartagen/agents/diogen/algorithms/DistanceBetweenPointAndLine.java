package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class DistanceBetweenPointAndLine {

  private ICurveSegment line;

  private IPolygon polygon;

  private double distance;

  public double getDistance() {
    return distance;
  }

  public DistanceBetweenPointAndLine(ICurveSegment line, IPolygon polygon) {
    this.line = line;
    this.polygon = (IPolygon) polygon.clone();
  }

  public void compute() {
    // get the angle between the line and abscissa axis.
    double xa = line.startPoint().getX();
    double ya = line.startPoint().getY();
    double xo = line.endPoint().getX();
    double yo = line.endPoint().getY();
    double angle = 0.0;
    if (xa == xo) {
      if (ya > yo) {
        angle = Math.PI / 2;
      } else {
        angle = 3 * Math.PI / 2;
      }
    } else {
      angle = Math.atan((ya - yo) / (xa - xo));
    }
    polygon = CommonAlgorithms.translation(polygon, -xo, -yo);
    polygon = CommonAlgorithms.rotation(polygon, angle);
    // get a point with the maximum y.

    IDirectPosition farestPoint = null;

    for (IDirectPosition point : this.polygon.exteriorCoord().getList()) {
      if (farestPoint == null) {
        farestPoint = point;
      } else if (farestPoint.getY() < point.getY()) {
        farestPoint = point;
      }
    }
    this.distance = farestPoint.getY();
  }

}
