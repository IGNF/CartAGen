package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionSize;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class BlockBuildingsDeletionSizeAction extends ActionCartagen {
  /**
   */
  private int nbLimite;

  public BlockBuildingsDeletionSizeAction(BlockAgent ag, Constraint cont,
      int nbLimite, double poids) {
    super(ag, cont, poids);
    this.nbLimite = nbLimite;
  }

  @Override
  public ActionResult compute() {
    BuildingsDeletionSize.compute(((BlockAgent) this.getAgent()).getFeature(),
        this.nbLimite);
    return ActionResult.UNKNOWN;
  }

}
