package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.MicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class DisplacementAction extends ActionCartagen {
  /**
   */
  private double dx;
  /**
   */
  private double dy;

  public DisplacementAction(MicroAgent ag, Constraint cont, double poids,
      double dx, double dy) {
    super(ag, cont, poids);
    this.dx = dx;
    this.dy = dy;
  }

  @Override
  public ActionResult compute() {
    ((MicroAgent) this.getAgent()).displaceAndRegister(this.dx, this.dy);
    return ActionResult.UNKNOWN;
  }
}
