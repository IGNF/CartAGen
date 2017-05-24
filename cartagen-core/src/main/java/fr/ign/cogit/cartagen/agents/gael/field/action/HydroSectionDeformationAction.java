/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field.action;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.gael.field.HydroSectionDeformation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri
 * 
 */
public class HydroSectionDeformationAction extends ActionCartagen {
  /**
   */
  private int nbLimiteActivationsParPoint = 10;

  public HydroSectionDeformationAction(IHydroSectionAgent agent,
      Constraint cont, double poids, int nbLimiteActivationsParPoint) {
    super(agent, cont, poids);
    this.nbLimiteActivationsParPoint = nbLimiteActivationsParPoint;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    IHydroSectionAgent tr = (IHydroSectionAgent) this.getAgent();
    HydroSectionDeformation.compute(tr, this.nbLimiteActivationsParPoint);
    return ActionResult.UNKNOWN;
  }

}
