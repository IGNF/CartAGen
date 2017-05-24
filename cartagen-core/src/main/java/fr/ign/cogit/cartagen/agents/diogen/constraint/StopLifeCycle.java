package fr.ign.cogit.cartagen.agents.diogen.constraint;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;

public class StopLifeCycle extends GeographicConstraintImpl {

  public StopLifeCycle(IGeographicAgent agent) {
    super(agent, 0);
  }

  private boolean satisfactionStatus = false;

  public void activate() {
    satisfactionStatus = true;
  }

  public void unactivate() {
    satisfactionStatus = false;
  }

  @Override
  public double getSatisfaction() {
    return (satisfactionStatus ? 100 : 0);
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
