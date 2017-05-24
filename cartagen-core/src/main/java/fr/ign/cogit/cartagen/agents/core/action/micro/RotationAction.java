package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 * 
 */
public class RotationAction extends ActionCartagen {
  /**
   */
  private double angle;

  public RotationAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double angle) {
    super(ag, cont, poids);
    this.angle = angle;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();
    ag.setGeom(CommonAlgorithms.rotation((IPolygon) ag.getGeom(), this.angle));
    return ActionResult.UNKNOWN;
  }
}
