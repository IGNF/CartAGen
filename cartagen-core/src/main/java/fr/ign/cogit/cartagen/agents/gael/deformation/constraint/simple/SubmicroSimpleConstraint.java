package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * A constraint on only one submicro object
 * 
 * @author JGaffuri
 * 
 */
public abstract class SubmicroSimpleConstraint extends SubmicroConstraint
    implements GeographicConstraint {

  private ISubMicro submicro;

  /**
   * @return The submicro object the constraint is on
   */
  public ISubMicro getSubmicro() {
    return this.submicro;
  }

  /**
   * The constructor
   * 
   * @param submicro
   * @param importance
   */
  public SubmicroSimpleConstraint(ISubMicro submicro, double importance) {
    super(importance);

    // link between the constraint and its submicro
    this.submicro = submicro;
    submicro.getSubmicroConstraints().add(this);

    for (IPointAgent p : submicro.getPointAgents()) {
      // update point agent total importance
      p.incrementerSommeImportances(importance);

      // link point agent - constraint
      p.getConstraints().add(this);

    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!this.getClass().getSimpleName()
        .equals(obj.getClass().getSimpleName())) {
      return false;
    }
    return (((SubmicroSimpleConstraint) obj).getSubmicro()
        .equals(this.submicro));
  }

  @Override
  public int hashCode() {
    return this.getClass().getSimpleName().hashCode();
  }

  private IGeographicAgent agent;

  @Override
  public IGeographicAgent getAgent() {
    return agent;
  }

  private double satisfaction = 0.;

  @Override
  public double getSatisfaction() {
    return satisfaction;
  }

  protected void setSatisfaction(double satisfaction) {
    this.satisfaction = satisfaction;
  }

  private double priority = 0.;

  @Override
  public double getPriority() {
    return priority;
  }

  private double currentValue = 0.;

  protected double getCurrentValue() {
    return currentValue;
  }

  protected void setCurrentValue(double currentValue) {
    this.currentValue = currentValue;
  }

  private double goalValue = 0.;

  protected double getGoalValue() {
    return goalValue;
  }

  protected void setGoalValue(double goalValue) {
    this.goalValue = goalValue;
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
