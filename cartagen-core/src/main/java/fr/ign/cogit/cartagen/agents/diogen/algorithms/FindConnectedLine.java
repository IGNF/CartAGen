package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class FindConnectedLine {

  private INetworkSection sectionToTest;
  // = edea.getDeadEnd().getRoot()
  private boolean left;

  private Set<INetworkSection> connectSections;

  private INetworkSection connectedSection;

  public INetworkSection getConnectedSection() {
    return connectedSection;
  }

  public FindConnectedLine(INetworkSection sectionToTest, boolean left,
      Set<INetworkSection> connectSections) {
    this.sectionToTest = sectionToTest;
    this.left = left;
    this.connectSections = connectSections;
  }

  public void compute() {
    if (connectSections == null) {
      return;
    }
    if (connectSections.isEmpty()) {
      return;
    }
    if (connectSections.size() == 1) {
      this.connectedSection = connectSections.iterator().next();
      return;
    }
    ILineString lineToDetect = sectionToTest.getGeom();
    // get the point of the other side of the root section
    IDirectPosition root = lineToDetect.startPoint();
    IDirectPosition A = lineToDetect.endPoint();

    // choose the good end of the section
    // if (edea.getRootDirectPosition().distance(startTest) > edea
    // .getRootDirectPosition().distance(endTest)) {
    // point1 = edea.getDeadEnd().getRoot().getGeom()
    // .getSegment(edea.getDeadEnd().getRoot().getGeom().sizeSegment() - 1)
    // .startPoint();
    // } else {
    // point1 = edea.getDeadEnd().getRoot().getGeom().getSegment(0).endPoint();
    // }

    // Calculation of the coordinate of point0-root vector
    double xOA = root.getX() - root.getX();
    double yOA = A.getY() - root.getY();

    double thresholdtAngle;
    if (!left) {
      thresholdtAngle = Double.MAX_VALUE;
    } else {
      thresholdtAngle = Double.MIN_VALUE;
    }

    // for each connected element from the network, find the angle
    for (INetworkSection section : connectSections) {
      IDirectPosition start = section.getGeom().startPoint();
      IDirectPosition end = section.getGeom().endPoint();

      ICurveSegment segment;
      IDirectPosition B;

      if (root.distance(start) > root.distance(end)) {
        segment = section.getGeom().getSegment(
            section.getGeom().sizeSegment() - 1);
        B = segment.startPoint();
        // pointForSegment.put(section, segment.endPoint());
      } else {
        segment = section.getGeom().getSegment(0);
        B = segment.endPoint();
        // pointForSegment.put(section, segment.startPoint());
      }

      // compute the angles

      // Calculation of the coordinate of root-point3 vector
      double xOB = root.getX() - B.getX();
      double yOB = root.getY() - B.getY();

      double angle = Math.atan2(xOA * yOB - yOA * xOB, xOA * xOB + yOA * yOB);
      if (angle < 0) {
        angle = angle + 2 * Math.PI;
      }

      if (((angle > thresholdtAngle) && (left))
          || ((angle < thresholdtAngle) && (!left))) {
        thresholdtAngle = angle;
        this.connectedSection = section;
        // toGo = point3;
      }

    }

    return;
  }

}
