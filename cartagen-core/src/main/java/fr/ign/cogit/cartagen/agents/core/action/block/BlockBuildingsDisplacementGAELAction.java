package fr.ign.cogit.cartagen.agents.core.action.block;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.decomposers.MesoComponentsDisplacementGAEL;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class BlockBuildingsDisplacementGAELAction extends ActionCartagen {

  public BlockBuildingsDisplacementGAELAction(BlockAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    new MesoComponentsDisplacementGAEL((BlockAgent) this.getAgent()).compute();
    for (InternStructureAgent structure : ((BlockAgent) this.getAgent())
        .getInternStructures()) {
      if (structure instanceof UrbanAlignmentAgent) {
        ((UrbanAlignmentAgent) structure).computeShapeLine();
      }
      structure.activate();
    }
    return ActionResult.UNKNOWN;
  }

}
