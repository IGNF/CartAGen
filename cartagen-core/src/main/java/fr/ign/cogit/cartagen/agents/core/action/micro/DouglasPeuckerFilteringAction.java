/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author julien Gaffuri
 * 
 */
public class DouglasPeuckerFilteringAction extends ActionCartagen {
  /**
   */
  private double seuil;

  public DouglasPeuckerFilteringAction(IMicroAgentGeneralisation ag,
      Constraint cont, double poids, double seuil) {
    super(ag, cont, poids);
    this.seuil = seuil;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();

    // tentative d'application de filtre de DP
    IGeometry geom = CommonAlgorithms.filtreDouglasPeucker(ag.getGeom(),
        this.seuil);

    ag.setGeom(geom);
    return ActionResult.UNKNOWN;
  }

}
