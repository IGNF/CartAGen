/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.urbanalignment.HomogeneousSpatialRepartitionAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JRenard
 * 
 */
public class SpatialRepartition extends GeographicObjectConstraintImpl {

  /**
   */
  private double SpatialRepartitionSigma;

  public SpatialRepartition(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    this.SpatialRepartitionSigma = ((UrbanAlignmentAgent) this.getAgent())
        .getBuildingsCentroidsDistanceSigma();
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(2);
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    int s = 100 - (int) (this.SpatialRepartitionSigma / 5);
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new HomogeneousSpatialRepartitionAction(
        (UrbanAlignmentAgent) this.getAgent(), this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }
}
