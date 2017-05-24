package fr.ign.cogit.cartagen.agents.diogen.constraint;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.diogen.relation.ProximityWaterCarryingRoad;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

public class RoadToWaterAreaProximity extends MicroMicroRelationalConstraint {

  public RoadToWaterAreaProximity(ICartAComAgentGeneralisation ag,
      ProximityWaterCarryingRoad rel, double importance) {
    super(ag, rel, importance);
  }

  @Override
  public Set<ActionProposal> getActions() {
    // TODO Auto-generated method stub
    return null;
  }

}
