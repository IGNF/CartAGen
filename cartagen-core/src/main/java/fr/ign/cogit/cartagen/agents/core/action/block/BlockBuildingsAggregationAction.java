package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.BuildingsAggregation;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * 
 * @author JGaffuri
 * 
 */
public class BlockBuildingsAggregationAction extends ActionCartagen {

  public BlockBuildingsAggregationAction(BlockAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    @SuppressWarnings("unused")
    IBuilding ab = BuildingsAggregation
        .compute(((IBlockAgent) this.getAgent()).getFeature());
    // AgentUtil.getAgentAgentFromGeneObj(ab).activate();
    return ActionResult.UNKNOWN;
  }
}
