package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;

/**
 * Constraint that forces a triangle to change its size toward a goal area.
 * 
 * @author julien Gaffuri
 * 
 */
public class Area extends SubmicroSimpleConstraint {

  private double goalArea;

  public Area(GAELTriangle t, double importance) {
    super(t, importance);
    this.goalArea = t.getInitialArea();
  }

  public Area(GAELTriangle t, double aireBut, double importance) {
    super(t, importance);
    this.goalArea = aireBut;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELTriangle t = (GAELTriangle) this.getSubmicro();
    IPointAgent p1 = null, p2 = null;
    if (p == t.getP1()) {
      p1 = t.getP2();
      p2 = t.getP3();
    } else if (p == t.getP2()) {
      p1 = t.getP3();
      p2 = t.getP1();
    } else if (p == t.getP3()) {
      p1 = t.getP1();
      p2 = t.getP2();
    } else {
      System.out.println(
          "Erreur dans proposeDeplacement de contrainte aire triangle");
      return;
    }
    double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
    double k = alpha * 2 * (this.goalArea - t.getArea())
        / (3 * (dx * dx + dy * dy));
    new PointAgentDisplacementAction(p, this, -k * dy, k * dx);
  }

  @Override
  public void computeCurrentValue() {
    this.setCurrentValue(((GAELTriangle) this.getSubmicro()).getArea());
  }

  @Override
  public void computeGoalValue() {
    this.setGoalValue(goalArea);
  }

  private double threshold = 0.90;

  @Override
  public void computeSatisfaction() {
    this.computeGoalValue();
    this.computeCurrentValue();

    // compute the difference value
    double diff = 1 - this.getCurrentValue() / this.getGoalValue();

    double result;
    if (Math.abs(diff) >= this.threshold) {
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
