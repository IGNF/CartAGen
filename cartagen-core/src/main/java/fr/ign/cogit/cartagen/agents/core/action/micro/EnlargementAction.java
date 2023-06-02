package fr.ign.cogit.cartagen.agents.core.action.micro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 * 
 */
public class EnlargementAction extends ActionCartagen {
  private static Logger logger = LogManager
      .getLogger(EnlargementAction.class.getName());
  /**
   */
  private double aireBut;

  public EnlargementAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double aireBut) {
    super(ag, cont, poids);
    this.aireBut = aireBut;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();

    GM_Object geom = (GM_Object) CommonAlgorithms.homothetie(
        (IPolygon) ag.getGeom(), Math.sqrt(this.aireBut / ag.getGeom().area()));

    if (geom == null) {
      EnlargementAction.logger
          .warn("Echec de l'application d'algorithme de Dilatation pour " + ag
              + ". geometrie renvoyee nulle: +geom)");
      return ActionResult.UNCHANGED;
    } else if (!geom.isValid()) {
      EnlargementAction.logger
          .warn("Echec de l'application d'algorithme Dilatation pour " + ag);
      // + ". geometrie renvoyee non valide: " + geom);
      return ActionResult.UNCHANGED;
    } else if (geom.isEmpty()) {
      EnlargementAction.logger
          .warn("Echec de l'application d'algorithme Dilatation pour " + ag
              + ". geometrie vide: " + geom);
      return ActionResult.UNCHANGED;
    } else {
      ag.setGeom(geom);
    }
    return ActionResult.UNKNOWN;
  }

}
