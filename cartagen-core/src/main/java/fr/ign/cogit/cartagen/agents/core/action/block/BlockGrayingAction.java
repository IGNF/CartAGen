/**
 * @author julien Gaffuri 23 juil. 2008
 */
package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.BlockGraying;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 23 juil. 2008
 * 
 */
public class BlockGrayingAction extends ActionCartagen {

  public BlockGrayingAction(BlockAgent ag, Constraint cont, double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() {
    new BlockGraying(((BlockAgent) this.getAgent()).getFeature()).compute();
    return ActionResult.UNKNOWN;
  }

}
