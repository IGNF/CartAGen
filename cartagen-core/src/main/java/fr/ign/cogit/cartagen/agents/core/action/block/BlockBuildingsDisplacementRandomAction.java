package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRandom;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class BlockBuildingsDisplacementRandomAction extends ActionCartagen {

  public BlockBuildingsDisplacementRandomAction(BlockAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() {
    BuildingDisplacementRandom
        .compute(((BlockAgent) this.getAgent()).getFeature());
    return ActionResult.UNKNOWN;
  }
}
