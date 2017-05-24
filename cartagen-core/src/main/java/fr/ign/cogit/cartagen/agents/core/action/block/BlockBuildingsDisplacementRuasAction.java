package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRuas;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class BlockBuildingsDisplacementRuasAction extends ActionCartagen {

  private double maxDisp;
  private int maxIter;

  public BlockBuildingsDisplacementRuasAction(IBlockAgent ag, Constraint cont,
      double poids, double maxDisp, int maxIter) {
    super(ag, cont, poids);
    this.maxDisp = maxDisp;
    this.maxIter = maxIter;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    BuildingDisplacementRuas.compute(
        ((IBlockAgent) this.getAgent()).getFeature(), this.maxDisp,
        this.maxIter);
    return ActionResult.UNKNOWN;
  }

}
