/*
 * Created on 26 may 2006
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.singletonpoint;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;

/**
 * Constraint that forces a point to move toward a specified position
 * 
 * @author julien Gaffuri
 * 
 */
public class Position extends SubmicroSimpleConstraint {

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
   * @param p
   * @param importance
   * @param xGoal
   * @param yGoal
   */
  public Position(GAELPointSingleton p, double importance, double xGoal,
      double yGoal) {
    super(p, importance);
    this.xGoal = xGoal;
    this.yGoal = yGoal;
  }

  /**
   * The constructor for a position preservation
   * 
   * @param p
   * @param importance
   */
  public Position(GAELPointSingleton p, double importance) {
    this(p, importance, p.getXIni(), p.getY());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.constraint.SubmicroConstraint#
   * proposeDisplacement (fr.ign.cogit.gaeldeformation.PointAgent, double)
   */
  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    new PointAgentDisplacementAction(p, this, alpha * (this.xGoal - p.getX()),
        alpha * (this.yGoal - p.getY()));
  }

  @Override
  public void computeCurrentValue() {
    // TODO
  }

  @Override
  public void computeGoalValue() {
    // TODO
  }

  private double threshold = 10;

  @Override
  public void computeSatisfaction() {
    this.computeGoalValue();
    this.computeCurrentValue();

    // System.out.println("xGoal " + this.xGoal);
    // System.out.println("yGoal " + this.yGoal);
    //
    // System.out.println("((GAELPointSingleton) this.getSubmicro()).getX() "
    // + ((GAELPointSingleton) this.getSubmicro()).getX());
    // System.out.println("((GAELPointSingleton) this.getSubmicro()).getY() "
    // + ((GAELPointSingleton) this.getSubmicro()).getY());

    double distance = Math.sqrt(Math
        .pow(this.xGoal - ((GAELPointSingleton) this.getSubmicro()).getX(), 2)
        + Math.pow(
            this.yGoal - ((GAELPointSingleton) this.getSubmicro()).getY(), 2));

    double result;
    if (distance <= this.threshold) {
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
