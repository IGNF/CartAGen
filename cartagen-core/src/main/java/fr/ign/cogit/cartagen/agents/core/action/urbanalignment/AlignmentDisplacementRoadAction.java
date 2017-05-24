package fr.ign.cogit.cartagen.agents.core.action.urbanalignment;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * 
 * @author JRenard
 * 
 */
public class AlignmentDisplacementRoadAction extends ActionCartagen {

  public AlignmentDisplacementRoadAction(UrbanAlignmentAgent ag,
      Constraint cont, double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    return ActionResult.UNCHANGED;
  }

}
