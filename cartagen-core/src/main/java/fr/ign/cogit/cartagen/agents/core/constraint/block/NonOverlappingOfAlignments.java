/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockAlignmentsDeletionCongestionAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public class NonOverlappingOfAlignments extends GeographicObjectConstraintImpl {

  /**
   */
  double overlappedAlignmentsRatio;

  /**
   * @param agent
   * @param importance
   * @param rate the rate of initial density to compute the goal density
   */
  public NonOverlappingOfAlignments(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    IBlockAgent ai = (BlockAgent) this.getAgent();
    this.overlappedAlignmentsRatio = ai.getOverlappedAlignmentsRatio();
  }

  @Override
  public void computePriority() {
    this.setPriority(12);
  }

  @Override
  public void computeSatisfaction() {
    if (((IBlockAgent) this.getAgent()).isColored()) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();
    int s = 100 - (int) (100.0 * overlappedAlignmentsRatio);
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    BlockAgent ai = (BlockAgent) this.getAgent();

    // Suppression of too constrained alignments
    Action actionToPropose = new BlockAlignmentsDeletionCongestionAction(ai,
        this, 2.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

    return actionProposals;
  }

}
