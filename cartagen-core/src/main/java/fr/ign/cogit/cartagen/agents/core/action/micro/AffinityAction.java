/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author julien Gaffuri 3 fevr. 08
 * 
 */
public class AffinityAction extends ActionCartagen {
  /**
   */
  @ActionField
  private double angle;
  /**
   */
  @ActionField
  private double coef;

  public AffinityAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double angle, double coef) {
    super(ag, cont, poids);
    this.angle = angle;
    this.coef = coef;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();
    IPolygon newGeom = CommonAlgorithms.affinite((IPolygon) ag.getGeom(),
        this.angle, this.coef);
    if (newGeom != null)
      ag.setGeom(newGeom);
    return ActionResult.UNKNOWN;
  }

}
