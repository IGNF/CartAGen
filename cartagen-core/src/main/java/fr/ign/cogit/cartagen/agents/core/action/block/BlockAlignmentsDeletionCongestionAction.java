package fr.ign.cogit.cartagen.agents.core.action.block;

import java.util.List;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.AlignmentsRecenterAndMerge;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.MostOverlappedAlignmentsInBlock;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 4 sept. 2007
 * 
 */
public class BlockAlignmentsDeletionCongestionAction extends ActionCartagen {

  public BlockAlignmentsDeletionCongestionAction(IBlockAgent ag,
      Constraint cont, double poids) {
    super(ag, cont, poids);

  }

  @Override
  public ActionResult compute() throws InterruptedException {
    MostOverlappedAlignmentsInBlock measure = new MostOverlappedAlignmentsInBlock(
        ((IBlockAgent) this.getAgent()).getFeature());
    List<IUrbanAlignment> alignsToMerge = measure.compute();
    AlignmentsRecenterAndMerge.compute(alignsToMerge);
    return ActionResult.UNKNOWN;
  }

}
