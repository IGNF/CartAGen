package fr.ign.cogit.cartagen.agents.core.action.urbanalignment;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.EnsureAlignmentHomogeneousSpatialRepartition;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * 
 * @author JRenard
 * 
 */
public class HomogeneousSpatialRepartitionAction extends ActionCartagen {

  public HomogeneousSpatialRepartitionAction(UrbanAlignmentAgent ag,
      Constraint cont, double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    EnsureAlignmentHomogeneousSpatialRepartition algo = new EnsureAlignmentHomogeneousSpatialRepartition(
        ((UrbanAlignmentAgent) this.getAgent()).getFeature());
    algo.compute();
    return ActionResult.UNKNOWN;
  }

}
