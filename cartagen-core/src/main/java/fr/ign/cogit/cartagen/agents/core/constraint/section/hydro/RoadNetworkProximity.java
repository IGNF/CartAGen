/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.core.constraint.section.hydro;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.gael.field.action.HydroSectionDeformationAction;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author julien Gaffuri 23 mars 08
 * 
 */
public class RoadNetworkProximity extends GeographicObjectConstraintImpl {
  /**
   */
  private double tauxSuperpositionReseauRoutier;

  public RoadNetworkProximity(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    this.tauxSuperpositionReseauRoutier = ((IHydroSectionAgent) this.getAgent())
        .getTauxSuperpositionRoutier();
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(5);
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    this.setSatisfaction(100 - (int) (this.tauxSuperpositionReseauRoutier
        / GeneralisationSpecifications.TAUX_SUPERPOSITION_HYDRO_ROUTIER));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new HydroSectionDeformationAction(
        (IHydroSectionAgent) this.getAgent(), this, 1.0, 20);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
