package fr.ign.cogit.cartagen.agents.core.action.section;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.algorithms.section.LineCurvatureSmoothing;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 9 juil. 2007
 * 
 */
public class CurvatureSmoothingAction extends ActionCartagen {

  public CurvatureSmoothingAction(ISectionAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() {
    ISectionAgent at = (ISectionAgent) this.getAgent();
    LineCurvatureSmoothing algoCurvatureSmoothing = new LineCurvatureSmoothing(
        at.getFeature());
    algoCurvatureSmoothing.compute(true);
    return ActionResult.UNKNOWN;
  }

}
