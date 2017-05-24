package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that forces a segment to have a goal length
 * 
 * @author JGaffuri
 * 
 */
public class Length extends SubmicroSimpleConstraint {

  private double goalLength;

  public Length(GAELSegment s, double importance, double goalLength) {
    super(s, importance);
    this.goalLength = goalLength;
  }

  public Length(GAELSegment s, double importance) {
    this(s, importance, s.getLongueurInitiale());
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = (GAELSegment) this.getSubmicro();
    double dist = s.getLength();
    double dx, dy;
    // cas peu probable mais on ne sait jamais
    if (dist == 0) {
      double a = 0.5 * alpha * this.goalLength / s.getLongueurInitiale();
      dx = a * (s.getP1().getXIni() - s.getP2().getXIni());
      dy = a * (s.getP1().getYIni() - s.getP2().getYIni());
    } else {
      double a = 0.5 * alpha * (this.goalLength / dist - 1);
      dx = a * (s.getP1().getX() - s.getP2().getX());
      dy = a * (s.getP1().getY() - s.getP2().getY());
    }
    if (p == s.getP1()) {
      new PointAgentDisplacementAction(p, this, dx, dy);
    } else if (p == s.getP2()) {
      new PointAgentDisplacementAction(p, this, -dx, -dy);
    }
  }

  @Override
  public void computeCurrentValue() {
    this.setCurrentValue(((GAELSegment) this.getSubmicro()).getLength());
  }

  @Override
  public void computeGoalValue() {
    this.setGoalValue(this.goalLength);
  }

  private double threshold = 0.9;

  @Override
  public void computeSatisfaction() {
    this.computeGoalValue();
    this.computeCurrentValue();

    // System.out.println(this.getCurrentValue());
    // System.out.println(this.getGoalValue());

    // compute the difference value
    double diff = this.getCurrentValue() / this.getGoalValue();
    if (diff > 1) {
      diff = 1 / diff;
    }
    // System.out.println(diff);

    double result;
    if (diff >= this.threshold) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else {
      result = CartacomSpecifications.SATISFACTION_1;
    }

    this.setSatisfaction(result);
  }

  @Override
  public void computePriority() {
  }

}
