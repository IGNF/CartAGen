package fr.ign.cogit.cartagen.agents.diogen.constraint.points;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IGeographicPointAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;

public class BalanceConstraint extends GeographicConstraintImpl {

  private double threshold = 10.;

  public BalanceConstraint(IGeographicPointAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public double getSatisfaction() {
    IGeographicPointAgent agent = (IGeographicPointAgent) this.getAgent();
    agent.computeForces();
    double result;
    if (agent.getDistancesFromBalance() <= this.threshold) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else {
      result = CartacomSpecifications.SATISFACTION_1;
    }
    return result;
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
