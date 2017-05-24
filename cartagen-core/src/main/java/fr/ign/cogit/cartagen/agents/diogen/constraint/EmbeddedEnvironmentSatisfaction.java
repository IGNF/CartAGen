package fr.ign.cogit.cartagen.agents.diogen.constraint;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.EmbeddedEnvironmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;

public class EmbeddedEnvironmentSatisfaction extends GeographicConstraintImpl {

  public EmbeddedEnvironmentSatisfaction(IGeographicAgent agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public double getSatisfaction() {

    double sat = 100;
    for (IDiogenAgent a : ((IDiogenAgent) this.getAgent()).getEncapsulatedEnv()
        .getContainedAgents()) {
      if (a instanceof EmbeddedEnvironmentAgent) {
        ((EmbeddedEnvironmentAgent) a).instantiateConstraints();
        sat -= 100 - a.getSatisfaction();
      }
    }

    if (sat < 0)
      return 0;
    return sat;
  }

  @Override
  public void computePriority() {
    this.setPriority(50);
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
