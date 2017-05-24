package fr.ign.cogit.cartagen.agents.diogen.constraint;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;

public class NonOverlappingLeftSide extends GeographicConstraintImpl {

  public NonOverlappingLeftSide(IGeographicAgent agent, double importance) {
    super(agent, importance);
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getSatisfaction() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Set<ActionProposal> getActions() {
    // TODO Auto-generated method stub
    return null;
  }

}
