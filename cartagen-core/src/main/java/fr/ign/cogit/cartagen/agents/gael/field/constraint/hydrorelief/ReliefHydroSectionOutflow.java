package fr.ign.cogit.cartagen.agents.gael.field.constraint.hydrorelief;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.DecomposedObjectDeformationAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.ObjectFieldRelationnalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public class ReliefHydroSectionOutflow
    extends ObjectFieldRelationnalConstraint {

  public ReliefHydroSectionOutflow(GeographicAgentGeneralisation agent,
      RelationImpl rel, double importance) {
    super(agent, rel, importance);
  }

  @Override
  public void computePriority() {
    this.setPriority(0);
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new DecomposedObjectDeformationAction(
        this.getAgent(), this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
