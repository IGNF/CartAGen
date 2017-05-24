/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDeletionCongestionAction;
import fr.ign.cogit.cartagen.agents.core.action.block.BlockGrayingAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public class Density extends GeographicObjectConstraintImpl {
  /**
   */
  private double simulatedDensity;

  /**
   * Getter for simulatedDensity.
   * @return the simulatedDensity
   * @author AMaudet
   */
  public double getSimulatedDensity() {
    return this.simulatedDensity;
  }

  /**
   * Getter for rate.
   * @return the rate
   * @author AMaudet
   */
  double rate;

  public double getRate() {
    return this.rate;
  }

  /**
     */
  private double goalDensity;

  /**
   * @param agent
   * @param importance
   * @param rate the rate of initial density to compute the goal density
   */
  public Density(GeographicObjectAgentGeneralisation agent, double importance,
      double rate) {
    super(agent, importance);
    ((IBlockAgent) this.getAgent()).computeInitialDensity();
    this.rate = rate;
  }

  public Density(GeographicObjectAgentGeneralisation agent, double importance) {
    this(agent, importance, GeneralisationSpecifications.RATIO_BLOCK_DENSITY);
  }

  @Override
  public void computeCurrentValue() {
    this.simulatedDensity = (((IBlockAgent) this.getAgent())
        .getSimulatedDensity());
  }

  @Override
  public void computeGoalValue() {
    this.goalDensity = this.rate
        * ((IBlockAgent) this.getAgent()).getInitialDensity();
    if (this.goalDensity > 1.0) {
      this.goalDensity = 1.0;
    }
  }

  @Override
  public void computePriority() {
    this.setPriority(9);
  }

  @Override
  public void computeSatisfaction() {
    if (((IBlockAgent) this.getAgent()).isColored()) {
      this.setSatisfaction(100);
      return;
    }

    this.computeCurrentValue();
    this.computeGoalValue();

    double dDensity = Math.abs(this.getSimulatedDensity() - this.goalDensity);
    int s = 100 - (int) (dDensity / 0.003);
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
    Action actionToPropose = null;

    // Guillaume: commenting this because not generic
    /*
     * if (ai.getInitialSimulatedDensity() >
     * GeneralisationSpecifications.DENSITE_LIMITE_GRISAGE_ILOT) { // propose de
     * griser totalement l'ilot actionToPropose = new
     * BlockGrayingAction((BlockAgent) this.getAgent(), this, 2.0);
     * actionProposals.add(new ActionProposal(this, true, actionToPropose,
     * 2.0)); }
     */
    if (ai.isGrayingNecessary()) {
      actionToPropose = new BlockGrayingAction((BlockAgent) this.getAgent(),
          this, 2.0);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));
      return actionProposals;
    }

    if (this.getSimulatedDensity() > ai.getInitialDensity()) {
      // propose de supprimer des batiments de l'ilot
      actionToPropose = new BlockBuildingsDeletionCongestionAction(
          (BlockAgent) this.getAgent(), this, 1,
          GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE, this.rate, 2.0);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));
    }

    return actionProposals;
  }

}
