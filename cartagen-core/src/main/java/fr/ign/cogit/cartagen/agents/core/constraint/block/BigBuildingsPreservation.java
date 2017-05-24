package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * Contrainte qui incite a preserver la part des grands bâtiment d'un ilot
 * 
 * @author patrick Taillandier 5 févr. 2008
 * 
 */
public class BigBuildingsPreservation extends GeographicObjectConstraintImpl {
  /**
   */
  private Set<Integer> grandBatiId = null;
  /**
   */
  private final double tailleGdBati = GeneralisationSpecifications.GRANDS_BATIMENTS_AIRE;
  /**
   */
  private int nbBatiGrandsBatis = 0;

  public BigBuildingsPreservation(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computePriority() {
  }

  @Override
  public void computeSatisfaction() {
    if (this.grandBatiId == null) {
      this.computeGoalValue();
    }
    if (this.grandBatiId.size() == 0) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();
    double proportionSupp = 100.0
        * (this.grandBatiId.size() - this.nbBatiGrandsBatis)
        / this.grandBatiId.size();
    this.setSatisfaction(Math.max((int) (100 - 2 * proportionSupp), 1));
  }

  @Override
  public void computeGoalValue() {
    this.grandBatiId = new HashSet<Integer>();
    BlockAgent ai = (BlockAgent) this.getAgent();
    for (GeographicObjectAgent ago : ai.getComponents()) {
      if (ago.getInitialGeom().area() > this.tailleGdBati) {
        this.grandBatiId.add(Integer.valueOf(ago.getId()));
      }
    }
  }

  @Override
  public void computeCurrentValue() {
    BlockAgent ai = (BlockAgent) this.getAgent();
    this.nbBatiGrandsBatis = 0;
    for (GeographicObjectAgent ago : ai.getComponents()) {
      if (this.grandBatiId.contains(Integer.valueOf(ago.getId()))
          && !ago.isDeleted()) {
        this.nbBatiGrandsBatis++;
      }
    }

  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
