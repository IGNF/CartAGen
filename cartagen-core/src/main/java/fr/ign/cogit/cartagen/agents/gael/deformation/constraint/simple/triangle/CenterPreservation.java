package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;

/**
 * Constraint that forces the center of the triangle to be preserved. It is
 * equivalent to fix some segment length constraints to the 3 segments linking
 * each point with the center point
 * 
 * @author JGaffuri
 * 
 */
public class CenterPreservation extends SubmicroSimpleConstraint {

  public CenterPreservation(GAELTriangle t, double importance) {
    super(t, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELTriangle t = (GAELTriangle) this.getSubmicro();
    if (t.istReverted()) {
      return; // faire autre chose?
    }

    double xG = (t.getP1().getX() + t.getP2().getX() + t.getP3().getX()) / 3,
        yG = (t.getP1().getY() + t.getP2().getY() + t.getP3().getY()) / 3.0;
    double xG_ini = (t.getP1().getXIni() + t.getP2().getXIni()
        + t.getP3().getXIni()) / 3.0,
        yG_ini = (t.getP1().getYIni() + t.getP2().getYIni()
            + t.getP3().getYIni()) / 3.0;
    double lg;

    lg = Math.sqrt(
        (xG - p.getX()) * (xG - p.getX()) + (yG - p.getY()) * (yG - p.getY()));
    if (lg == 0.0) {
      new PointAgentDisplacementAction(p, this, alpha * (p.getX() - xG),
          alpha * (p.getY() - yG));
    } else {
      double lg_ini = Math.sqrt((xG_ini - p.getXIni()) * (xG_ini - p.getXIni())
          + (yG_ini - p.getYIni()) * (yG_ini - p.getYIni()));
      new PointAgentDisplacementAction(p, this,
          alpha * (1 - lg_ini / lg) * (xG - p.getX()),
          alpha * (1 - lg_ini / lg) * (yG - p.getY()));
    }
  }

  @Override
  public void computeCurrentValue() {

  }

  @Override
  public void computeGoalValue() {
    // TODO
  }

  private double threshold = 10;

  @Override
  public void computeSatisfaction() {

    GAELTriangle t = (GAELTriangle) this.getSubmicro();

    double xG = (t.getP1().getX() + t.getP2().getX() + t.getP3().getX()) / 3,
        yG = (t.getP1().getY() + t.getP2().getY() + t.getP3().getY()) / 3.0;
    double xG_ini = (t.getP1().getXIni() + t.getP2().getXIni()
        + t.getP3().getXIni()) / 3.0,
        yG_ini = (t.getP1().getYIni() + t.getP2().getYIni()
            + t.getP3().getYIni()) / 3.0;

    double distance = Math
        .sqrt(Math.pow(xG_ini - xG, 2) + Math.pow(yG_ini - yG, 2));

    double result;
    if (distance >= this.threshold) {
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
