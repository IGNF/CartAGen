/*
 * Créé le 13 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;

/**
 * Submicro constraint on a submicro center position. It forces the submicro to
 * move its center toward a goal position.
 * 
 * @author JGaffuri
 */
public class CenterPosition extends SubmicroSimpleConstraint {

  /**
   * The goal coordinates
   */
  private double xGoal;
  /**
   * The goal coordinates
   */
  private double yGoal;

  /**
   * The constructor
   * 
   * @param sm
   * @param importance
   * @param xGoal
   * @param yGoal
   */
  public CenterPosition(ISubMicro sm, double importance, double xGoal,
      double yGoal) {
    super(sm, importance);
    this.xGoal = xGoal;
    this.yGoal = yGoal;
  }

  /**
   * The constructor for a center position preservation
   * 
   * @param sm
   * @param importance
   */
  public CenterPosition(ISubMicro sm, double importance) {
    this(sm, importance, sm.getXIni(), sm.getYIni());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.constraint.SubmicroConstraint#
   * proposeDisplacement (fr.ign.cogit.gaeldeformation.PointAgent, double)
   */
  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    new PointAgentDisplacementAction(p, this,
        alpha * (this.xGoal - this.getSubmicro().getX()),
        alpha * (this.yGoal - this.getSubmicro().getY()));
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

    double xG_ini = (t.getP1().getXIni() + t.getP2().getXIni()
        + t.getP3().getXIni()) / 3.0,
        yG_ini = (t.getP1().getYIni() + t.getP2().getYIni()
            + t.getP3().getYIni()) / 3.0;

    double distance = Math
        .sqrt(Math.pow(xG_ini - xGoal, 2) + Math.pow(yG_ini - yGoal, 2));

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
