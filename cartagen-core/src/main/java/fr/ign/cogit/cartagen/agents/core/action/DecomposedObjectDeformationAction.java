/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.action;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * Computes a deformation of an object using GAEL This object has to be already
 * decomposed, its submicro constraints instanciated, and some points presents
 * in the GAEL object
 * 
 * @author JGaffuri
 * 
 */
public class DecomposedObjectDeformationAction extends ActionCartagen {

  public DecomposedObjectDeformationAction(IAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {

    // activates the point agents of the stack
    ((GAELDeformable) this.getAgent()).activatePointAgents();
    return ActionResult.UNKNOWN;

  }

}
