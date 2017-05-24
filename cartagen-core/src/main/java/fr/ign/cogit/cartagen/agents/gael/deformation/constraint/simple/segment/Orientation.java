package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that forces a segment to have a goal orientation
 * 
 * @author JGaffuri
 * 
 */
public class Orientation extends SubmicroSimpleConstraint {

  private double orientationBut;

  public Orientation(GAELSegment s, double importance) {
    super(s, importance);
    this.orientationBut = s.getP1().getInitialOrientation(s.getP2());
  }

  public Orientation(GAELSegment s, double importance, double orientationBut) {
    super(s, importance);
    this.orientationBut = orientationBut;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = (GAELSegment) this.getSubmicro();
    double angle = alpha * s.getOrientationEcart(this.orientationBut);
    double cos = Math.cos(angle), sin = Math.sin(angle);
    double dx = 0.5 * (s.getP2().getX() - s.getP1().getX()
        + cos * (s.getP1().getX() - s.getP2().getX())
        + sin * (s.getP1().getY() - s.getP2().getY()));
    double dy = 0.5 * (s.getP2().getY() - s.getP1().getY()
        - sin * (s.getP1().getX() - s.getP2().getX())
        + cos * (s.getP1().getY() - s.getP2().getY()));
    if (p == s.getP1()) {
      new PointAgentDisplacementAction(p, this, dx, dy);
    } else if (p == s.getP2()) {
      new PointAgentDisplacementAction(p, this, -dx, -dy);
    }
  }

  @Override
  public void computeCurrentValue() {
    this.setCurrentValue(((GAELSegment) this.getSubmicro()).getOrientation());
  }

  @Override
  public void computeGoalValue() {
    this.setGoalValue(orientationBut);
  }

  // private double threshold5 = Math.PI / 20.;
  private double threshold4 = Math.PI / 20.;
  private double threshold3 = Math.PI / 10.;
  private double threshold2 = Math.PI / 8.;
  private double threshold1 = Math.PI / 4.;

  @Override
  public void computeSatisfaction() {
    this.computeGoalValue();
    this.computeCurrentValue();

    // compute the difference value
    double diff = this.getCurrentValue() - this.getGoalValue();

    // guarantee the value is between -Pi and Pi
    if (diff < -Math.PI) {
      diff += 2 * Math.PI;
    } else if (diff > Math.PI) {
      diff -= 2 * Math.PI;
    }

    double result;
    if (Math.abs(diff) >= this.threshold1) {
      result = CartacomSpecifications.SATISFACTION_1;
    } else if (Math.abs(diff) >= this.threshold2) {
      result = CartacomSpecifications.SATISFACTION_2;
    } else if (Math.abs(diff) >= this.threshold3) {
      result = CartacomSpecifications.SATISFACTION_3;
    } else if (Math.abs(diff) >= this.threshold4) {
      result = CartacomSpecifications.SATISFACTION_4;
    } else {
      result = CartacomSpecifications.SATISFACTION_5;
    }

    this.setSatisfaction(result);
  }

  @Override
  public void computePriority() {
  }

}
