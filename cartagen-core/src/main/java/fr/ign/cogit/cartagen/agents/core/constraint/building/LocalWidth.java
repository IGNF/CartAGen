package fr.ign.cogit.cartagen.agents.core.constraint.building;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

public class LocalWidth extends GeographicObjectConstraintImpl {

  public LocalWidth(GeographicAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public void computePriority() {
    this.setPriority(0);
  }

  @Override
  public void computeSatisfaction() {
    this.setSatisfaction(100);
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computeCurrentValue() {
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
