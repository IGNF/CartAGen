package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.angle;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;

/**
 * Constraint that forces the angle value toward a goal value.
 * 
 * @author JGaffuri
 * 
 */
public class Value extends SubmicroSimpleConstraint {

  private double goalValue;

  public Value(GAELAngle a, double importance, double valeurBut) {
    super(a, importance);
    this.goalValue = valeurBut;
  }

  public Value(GAELAngle a, double importance) {
    this(a, importance, a.getInitialValue());
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELAngle a = (GAELAngle) this.getSubmicro();
    double angle = -0.5 * alpha * a.getValueDifference(this.goalValue);
    double cos = Math.cos(angle), sin = Math.sin(angle);
    if (p == a.getP()) {
      double dx1 = a.getP().getX() - a.getP1().getX()
          + cos * (a.getP1().getX() - a.getP().getX())
          - sin * (a.getP1().getY() - a.getP().getY());
      double dy1 = a.getP().getY() - a.getP1().getY()
          + sin * (a.getP1().getX() - a.getP().getX())
          + cos * (a.getP1().getY() - a.getP().getY());
      double dx2 = a.getP().getX() - a.getP2().getX()
          + cos * (a.getP2().getX() - a.getP().getX())
          + sin * (a.getP2().getY() - a.getP().getY());
      double dy2 = a.getP().getY() - a.getP2().getY()
          - sin * (a.getP2().getX() - a.getP().getX())
          + cos * (a.getP2().getY() - a.getP().getY());
      new PointAgentDisplacementAction(a.getP(), this, -(dx1 + dx2) * 0.5,
          -(dy1 + dy2) * 0.5);
    } else if (p == a.getP1()) {
      double dx1 = a.getP().getX() - a.getP1().getX()
          + cos * (a.getP1().getX() - a.getP().getX())
          - sin * (a.getP1().getY() - a.getP().getY());
      double dy1 = a.getP().getY() - a.getP1().getY()
          + sin * (a.getP1().getX() - a.getP().getX())
          + cos * (a.getP1().getY() - a.getP().getY());
      new PointAgentDisplacementAction(a.getP1(), this, dx1, dy1);
    } else if (p == a.getP2()) {
      double dx2 = a.getP().getX() - a.getP2().getX()
          + cos * (a.getP2().getX() - a.getP().getX())
          + sin * (a.getP2().getY() - a.getP().getY());
      double dy2 = a.getP().getY() - a.getP2().getY()
          - sin * (a.getP2().getX() - a.getP().getX())
          + cos * (a.getP2().getY() - a.getP().getY());
      new PointAgentDisplacementAction(a.getP2(), this, dx2, dy2);
    } else {
    }
  }

  @Override
  public void computeCurrentValue() {
    this.setCurrentValue(((GAELAngle) this.getSubmicro()).getValue());
  }

  @Override
  public void computeGoalValue() {
    this.setGoalValue(goalValue);
  }

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
