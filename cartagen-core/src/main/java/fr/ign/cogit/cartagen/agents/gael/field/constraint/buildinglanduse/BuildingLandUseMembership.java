package fr.ign.cogit.cartagen.agents.gael.field.constraint.buildinglanduse;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.ObjectFieldRelationnalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public class BuildingLandUseMembership
    extends ObjectFieldRelationnalConstraint {

  public BuildingLandUseMembership(GeographicAgentGeneralisation ag,
      RelationImpl rel, double importance) {
    super(ag, rel, importance);
  }

  @Override
  public void computePriority() {
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
