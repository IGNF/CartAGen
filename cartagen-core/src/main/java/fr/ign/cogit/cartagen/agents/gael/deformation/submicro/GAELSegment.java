package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Horizontality;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Length;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.LinkedFeaturesProximity;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Orientation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Outflow;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.PositionPreservation;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

/**
 * The submicro segment class.
 * 
 * @author julien Gaffuri 28 juin 2006
 * 
 */
public class GAELSegment extends SubMicro implements TriangulationSegment {
  static Logger logger = Logger.getLogger(GAELSegment.class.getName());

  private IPointAgent p1;
  private IPointAgent p2;

  /**
   * @return The first point of the segment
   */
  public IPointAgent getP1() {
    return this.p1;
  }

  /**
   * @return The second point of the segment
   */
  public IPointAgent getP2() {
    return this.p2;
  }

  /**
   * The constructor
   * 
   * @param p1
   * @param p2
   */
  public GAELSegment(IPointAgent p1, IPointAgent p2) {

    // links between the object and its point agent
    this.p1 = p1;
    this.p2 = p2;

    this.getPointAgents().add(p1);
    this.getPointAgents().add(p2);

    p1.getSubmicros().add(this);
    p2.getSubmicros().add(this);

    // links between the points
    p1.addAgentPointAccointants(p2);
    p2.addAgentPointAccointants(p1);

    // build the geometry
    super.setGeom(new GM_LineSegment(p1.getPosition(), p2.getPosition()));

  }

  /**
   * The constructor
   * 
   * @param def
   * @param p1
   * @param p2
   */
  public GAELSegment(GAELDeformable def, IPointAgent p1, IPointAgent p2) {
    this(p1, p2);
    def.getSegments().add(this);
  }

  @Override
  public ILineSegment getGeom() {
    return (ILineSegment) super.getGeom();
  }

  @Override
  public double getX() {
    return (this.getP1().getX() + this.getP2().getX()) * 0.5;
  }

  @Override
  public double getY() {
    return (this.getP1().getY() + this.getP2().getY()) * 0.5;
  }

  @Override
  public void clean() {
    super.clean();
    this.p1 = null;
    this.p2 = null;
    this.setGeom(null);
  }

  /**
   * The segment value This is a value affected to the segment for some needs.
   * (it is not the length)
   */
  private double value = 0;

  /**
   * @return
   */
  public double getValue() {
    return this.value;
  }

  /**
   * @param value
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * @return The segment length
   */
  public double getLength() {
    return this.getP1().getDistance(this.getP2());
  }

  /**
   * the segment initial length
   */
  protected double initialLength = -1.0;

  public double getLongueurInitiale() {
    if (this.initialLength == -1.0) {
      this.initialLength = this.getP1().getDistanceInitiale(this.getP2());
    }
    return this.initialLength;
  }

  /**
   * @return The segment orientation, from p1 to p2, between -PI and PI, in
   *         radians
   */
  public double getOrientation() {
    return this.getP1().getOrientation(this.getP2());
  }

  /**
   * The segment initial orientation, from p1 to p2, between -PI and PI, in
   * radians
   */
  private double initialOrientation = -999.9;

  /**
   * @return
   */
  public double getInitialOrientation() {
    if (this.initialOrientation == -999.9) {
      this.initialOrientation = this.getP1()
          .getInitialOrientation(this.getP2());
    }
    return this.initialOrientation;
  }

  /**
   * Difference to a given orientation The given orientation is between -Pi et
   * Pi, in radians The output too.
   * 
   * @param orientation
   * @return
   */
  public double getOrientationEcart(double orientation) {
    // compute the difference. The result is between -2Pi and 2Pi
    double diff = this.getP1().getOrientation(this.getP2()) - orientation;

    // garantees the output is between -Pi and Pi
    if (diff < -Math.PI) {
      return diff + 2.0 * Math.PI;
    } else if (diff > Math.PI) {
      return diff - 2.0 * Math.PI;
    } else {
      return diff;
    }
  }

  /**
   * @return Difference to the initial orientation, between -Pi et Pi, in
   *         radians
   */
  public double getOrientationEcart() {
    return this
        .getOrientationEcart(this.getP1().getInitialOrientation(this.getP2()));
  }

  /**
   * Return the slope azimutal orientation under the segment. Between -Pi and Pi
   * along the (O,x) axis, in radians It is a weighted mean, depanding on the
   * azimutal and zenital slope orientations. Returns 999.9 if the DTM is not
   * defined under the segment.
   * 
   * @return
   */
  public double getSlopeOrientationToFlowDown() {
    // number of points for the sampling
    final int NB = 10;

    double slopeZenitalOrientationSum = 0.0;
    double slopeAzimutalOrientationSum = 0.0;
    double slopeZenitalOrientation, slopeAzimutalOrientation;
    for (double t = 0; t <= 1; t += 1.0 / NB) {
      // get a point on the segment
      IDirectPosition pos = new DirectPosition(
          (1 - t) * this.getP1().getX() + t * this.getP2().getX(),
          (1 - t) * this.getP1().getY() + t * this.getP2().getY());

      // get the slope azimutal orientation
      slopeAzimutalOrientation = ((ReliefFieldAgent) AgentUtil
          .getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
                  .getSlopeAzimutalOrientation(pos);

      // 999.9 if the DTM is not defined
      if (slopeAzimutalOrientation == 999.9) {
        continue;
      }

      // -999.9 if the DTM is horizontal
      if (slopeAzimutalOrientation == -999.9) {
        continue;
      }

      // the slope is defined
      slopeZenitalOrientation = ((ReliefFieldAgent) AgentUtil
          .getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
                  .getZenitalOrientation(pos);
      slopeAzimutalOrientationSum += slopeAzimutalOrientation
          * slopeZenitalOrientation;
      slopeZenitalOrientationSum += slopeZenitalOrientation;
    }
    // no slope vector is defined under the segment: return 999.9
    if (slopeZenitalOrientationSum == 0.0) {
      return 999.9;
    }

    // returns the mean
    return slopeAzimutalOrientationSum / slopeZenitalOrientationSum;
  }

  /**
   * Difference between the slope azimutal orientation and the segment's one.
   * The output is between -Pi and Pi, in radians Return 0.0 if the slope is not
   * defined (horizontal or no DTM)
   * 
   * @return
   */
  public double getSlopeAzimutalOrientationDifference() {
    // number of points for the sampling
    final double NB = 10;

    // get the segment azimutal orientation
    double orientation = this.getOrientation();

    double diffSum = 0.0;
    double slopeAzimutalOrientation, diff;
    for (double t = 0; t <= 1; t += 1 / NB) {
      // get a point on the segment
      IDirectPosition pos = new DirectPosition(
          (1 - t) * this.getP1().getX() + t * this.getP2().getX(),
          (1 - t) * this.getP1().getY() + t * this.getP2().getY());

      // get the slope azimutal orientation of that point
      slopeAzimutalOrientation = ((ReliefFieldAgent) AgentUtil
          .getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
                  .getSlopeAzimutalOrientation(pos);

      // 999.9: if the DTM is not defined, and -999.9 if it is horizontal
      if (slopeAzimutalOrientation != 999.9
          && slopeAzimutalOrientation != -999.9) {
        // get the difference between -Pi and Pi
        diff = orientation - slopeAzimutalOrientation;
        if (diff < -Math.PI) {
          diff += 2.0 * Math.PI;
        } else if (diff > Math.PI) {
          diff -= 2.0 * Math.PI;
        }
        diffSum += diff;
      }
    }
    // return the mean value
    return diffSum / NB;
  }

  /**
   * Returns the mean slope zenital orientation, in radan, between -pi/2 and
   * pi/2 That is the angle between the segment and the horizontal plan. This
   * value is Pi/2 - zenital angle, or even the angle batween the mean normal
   * vector to the DTM and the vertical, going up. 0: the segment is horizontal
   * between 0 and pi/2: the segment goes up (pi/2: le segment is vertical,
   * going up) between 0 and -pi/2: le segment goes down (-pi/2: le segment is
   * vertical, going down) if the DTM is not defined, returns 999.9;
   * 
   * @return
   */
  public double getSlopeZenitalOrientation() {
    // number of points for the sampling
    final double NB = 10;

    boolean found = false;
    double slopeZenitalOrientation = 0.0, slopeZenitalOrientationSum = 0.0;
    for (double t = 0; t <= 1; t += 1.0 / NB) {
      // get a point on the segment
      IDirectPosition pos = new DirectPosition(
          (1 - t) * this.getP1().getX() + t * this.getP2().getX(),
          (1 - t) * this.getP1().getY() + t * this.getP2().getY());
      slopeZenitalOrientation = ((ReliefFieldAgent) AgentUtil
          .getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
                  .getZenitalOrientation(pos);

      // 999.9: the slope is not defined
      if (slopeZenitalOrientation == 999.9) {
        continue;
      }

      // the slope is defined
      found = true;
      slopeZenitalOrientationSum += slopeZenitalOrientation;
    }
    if (!found) {
      return 999.9;
    }
    // return the mean value
    return slopeZenitalOrientationSum / NB;
  }

  /**
   * @return Orientation difference of the non-oriented segment to be and the
   *         isoline one, in radan, between -pi/2 and pi/2.
   */
  public double getOrientationDifferenceToBeHorizontal() {
    double diff = this.getSlopeAzimutalOrientationDifference();
    if (diff >= Math.PI * 0.5) {
      return diff - Math.PI * 0.5;
    } else if (diff > 0) {
      return diff - Math.PI * 0.5;
    } else if (diff <= -Math.PI * 0.5) {
      return diff + Math.PI * 0.5;
    } else if (diff <= 0) {
      return diff + Math.PI * 0.5;
    } else {
      GAELSegment.logger.error(
          "Error in getOrienationDifferenceToBeHorizontal. diff=" + diff);
      return 0.0;
    }
  }

  /**
   * @return The outflow indicator value, between 0 and 1. 1: great outflow. 0:
   *         awful outflow (goes up)
   */
  public double getOutflowIndicator() {
    // slope zenital orientation, between 0 and Pi/2
    double phi = this.getSlopeZenitalOrientation();

    // the segment is horizontal
    if (phi == 0.0) {
      return 1.0;
    }

    // the slope is not defined under the segment
    if (phi == 999.9) {
      return 1.0;
    }

    // return the indicator value
    return 1 - Math.abs(this.getSlopeAzimutalOrientationDifference()) * phi * 2
        / (Math.PI * Math.PI);
  }

  /**
   * Add a constraint on the segment length
   * 
   * @param importance
   */
  public void addLengthConstraint(double importance) {
    new Length(this, importance);
  }

  /**
   * Add a constraint on the segment length
   * 
   * @param importance
   * @param goalLength
   */
  public void addLengthConstraint(double importance, double goalLength) {
    new Length(this, importance, goalLength);
  }

  /**
   * Add a constraint on the segment orientation
   * 
   * @param importance
   */
  public void addOrientationConstraint(double importance) {
    new Orientation(this, importance);
  }

  /**
   * Add a constraint on the segment orientation
   * 
   * @param importance
   * @param orientationBut
   */
  public void addOrientationConstraint(double importance,
      double orientationBut) {
    new Orientation(this, importance, orientationBut);
  }

  /**
   * Add a constraint on the segment position
   * 
   * @param importance
   */
  public void addPositionPreservationConstraint(double importance) {
    new PositionPreservation(this, importance);
  }

  /**
   * Add a constraint on the segment outflow
   * 
   * @param importance
   */
  public void addOutflowConstraint(double importance) {
    new Outflow(this, importance);
  }

  /**
   * Add a constraint on the segment slope
   * 
   * @param importance
   */
  public void addHorizontalityConstraint(double importance) {
    new Horizontality(this, importance);
  }

  /**
   * Add a constraint on the linked features proximity
   * 
   * @param importance
   * @param minimumDistance
   */
  public void addLinkedFeaturesProximityConstraint(double importance,
      double minimumDistance) {
    new LinkedFeaturesProximity(this, importance, minimumDistance);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GAELSegment)) {
      return false;
    }
    GAELSegment s = (GAELSegment) obj;
    // 2 segments are equal when they are linked both to the same point agents.
    return this.getP1() == s.getP1() && this.getP2() == s.getP2()
        || this.getP1() == s.getP2() && this.getP2() == s.getP1();
  }

  @Override
  public String toString() {
    return this.getP1() + " " + this.getP2();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public TriangulationPoint getPoint1() {
    return this.p1;
  }

  @Override
  public TriangulationPoint getPoint2() {
    return this.p2;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  @Override
  public Set<IFeature> getGeoObjects() {
    return null;
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
  }

  @Override
  public INode getInitialNode() {
    return this.p1;
  }

  @Override
  public void setInitialNode(INode node) {
  }

  @Override
  public INode getFinalNode() {
    return this.p2;
  }

  @Override
  public void setFinalNode(INode node) {
  }

  @Override
  public Set<INode> getNodes() {
    Set<INode> pts = new HashSet<INode>();
    pts.add(this.p1);
    pts.add(this.p2);
    return pts;
  }

  @Override
  public double getWeight() {
    return this.value;
  }

  @Override
  public void setWeight(double weight) {
    this.setValue(weight);
  }

  @Override
  public void setGeom(ICurve geom) {
    super.setGeom(geom);
  }

  @Override
  public IGraph getGraph() {
    return null;
  }

  @Override
  public void setGraph(IGraph graph) {
  }

  @Override
  public boolean isOriented() {
    return false;
  }

}
