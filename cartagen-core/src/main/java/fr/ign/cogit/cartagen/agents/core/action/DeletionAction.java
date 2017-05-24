/**
 * @author julien Gaffuri 9 sept. 2008
 */
package fr.ign.cogit.cartagen.agents.core.action;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 9 sept. 2007
 * 
 */
public class DeletionAction extends ActionCartagen {

  public DeletionAction(IAgent ag, Constraint cont, double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() {
    ((GeographicObjectAgentGeneralisation) this.getAgent()).deleteAndRegister();
    return ActionResult.UNKNOWN;
  }

}
