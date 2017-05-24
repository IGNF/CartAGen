/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field.action;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IGeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.field.FieldActivation;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class FieldActivationAction extends ActionCartagen {
  /**
   */
  private FieldAgent agentChamp;

  public FieldActivationAction(IGeographicObjectAgentGeneralisation agent,
      Constraint cont, double poids, FieldAgent fieldAgent) {
    super(agent, cont, poids);
    this.agentChamp = fieldAgent;
  }

  // activation de l'agent champ avec pour agent point actives ceux qui sont
  // sous l'agent
  @Override
  public ActionResult compute() throws InterruptedException {
    FieldActivation.compute(
        (GeographicObjectAgentGeneralisation) this.getAgent(), this.agentChamp);
    return ActionResult.UNKNOWN;
  }

}
