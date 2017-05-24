/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.decomposers.GAELMicroSquarring;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 3 févr. 07
 * 
 */
public class SquarringAction extends ActionCartagen {
  /**
   */
  private double toleranceAngle = 0.0;
  /**
   */
  private int nbLimiteActivationsParPoint = 0;

  public SquarringAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double toleranceAngle, int nbLimiteActivationsParPoint) {
    super(ag, cont, poids);
    this.toleranceAngle = toleranceAngle;
    this.nbLimiteActivationsParPoint = nbLimiteActivationsParPoint;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    new GAELMicroSquarring((IMicroAgentGeneralisation) this.getAgent(),
        this.toleranceAngle, this.nbLimiteActivationsParPoint).declencher();
    return ActionResult.UNKNOWN;
  }

}
