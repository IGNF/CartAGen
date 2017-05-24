package fr.ign.cogit.cartagen.agents.diogen.constraint.points;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.Relation;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraintImpl;

public class RoadNonOverlappingPoint extends RelationalConstraintImpl {

  public RoadNonOverlappingPoint(IGeographicAgent agent, Relation relation,
      double importance) {
    super(agent, relation, importance);
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
