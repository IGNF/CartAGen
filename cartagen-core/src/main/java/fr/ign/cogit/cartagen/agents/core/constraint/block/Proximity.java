/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDeletionProximityAction;
import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDisplacementGAELAction;
import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDisplacementRuasAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public class Proximity extends GeographicObjectConstraintImpl {
  /**
   */
  private double overlappingRatesMean;

  public Proximity(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    this.overlappingRatesMean = ((BlockAgent) this.getAgent())
        .getBuildingsOverlappingRateMean();
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
    int s = 100 - (int) (this.overlappingRatesMean / 0.005);
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // proposer deplacement et ou agregation et ou suppression/recentrage

    actionToPropose = new BlockBuildingsDisplacementRuasAction(
        (BlockAgent) this.getAgent(), this, 4.0,
        GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT, 2);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 4.0));

    actionToPropose = new BlockBuildingsDisplacementGAELAction(
        (BlockAgent) this.getAgent(), this, 3.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 3.0));

    // actionToPropose = new BlockBuildingsAggregationAction((BlockAgent)
    // getAgent(), this, 2.0);
    // actionProposals.add(new ActionProposal(this, true, actionToPropose,
    // 2.0));
    //
    // actionToPropose = new
    // BlockMostConflictedBuildingDeletionAction((BlockAgent)getAgent(), this,
    // GeneralisationSpecifications.SEUIL_TAUX_SUPERPOSITION_SUPPRESSION, 1.0);
    // actionProposals.add(new ActionProposal(this, true, actionToPropose,
    // 1.0));

    actionToPropose = new BlockBuildingsDeletionProximityAction(
        (BlockAgent) this.getAgent(), this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));

    return actionProposals;
  }
}
