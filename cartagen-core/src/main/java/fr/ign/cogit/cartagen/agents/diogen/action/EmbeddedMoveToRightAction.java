package fr.ign.cogit.cartagen.agents.diogen.action;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.EmbeddedEnvironmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.constraint.EnoughSpaceAtRightRayTracing;
import fr.ign.cogit.cartagen.agents.diogen.schema.IEmbeddedDeadEndArea;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

public class EmbeddedMoveToRightAction extends ActionCartagen {

  public EmbeddedMoveToRightAction(EmbeddedEnvironmentAgent agent,
      Constraint constraint, double weight) {
    super(agent, constraint, weight);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    ((IEmbeddedDeadEndArea) ((EmbeddedEnvironmentAgent) this.getAgent())
        .getFeature()).goToRight(
            ((EnoughSpaceAtRightRayTracing) this.getConstraint()).getSpace());
    return ActionResult.UNKNOWN;
  }
}
