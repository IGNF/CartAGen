package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.deletion.MostConflictedBuildingDeletion;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 5 févr. 2009
 * 
 */
public class BlockMostConflictedBuildingDeletionAction extends ActionCartagen {
  /**
   */
  private double seuilTaux;

  public BlockMostConflictedBuildingDeletionAction(BlockAgent ag,
      Constraint cont, double seuilTaux, double poids) {
    super(ag, cont, poids);
    this.seuilTaux = seuilTaux;
  }

  @Override
  public ActionResult compute() {
    MostConflictedBuildingDeletion
        .compute(((BlockAgent) this.getAgent()).getFeature(), this.seuilTaux);
    return ActionResult.UNKNOWN;
  }
}
