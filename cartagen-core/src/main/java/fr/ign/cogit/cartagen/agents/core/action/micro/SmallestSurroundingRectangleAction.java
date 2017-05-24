package fr.ign.cogit.cartagen.agents.core.action.micro;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

/**
 * @author JGaffuri
 * 
 */
public class SmallestSurroundingRectangleAction extends ActionCartagen {
  private static Logger logger = Logger
      .getLogger(SmallestSurroundingRectangleAction.class.getName());
  /**
   */
  private double aireBut;

  public SmallestSurroundingRectangleAction(IMicroAgentGeneralisation ag,
      Constraint cont, double poids, double aireBut) {
    super(ag, cont, poids);
    this.aireBut = aireBut;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();
    GM_Object geom = (GM_Object) SmallestSurroundingRectangleComputation
        .getSSRGoalArea(ag.getGeom(), this.aireBut);

    if (geom == null) {
      SmallestSurroundingRectangleAction.logger
          .warn("Echec de l'application d'algorithme de PPRE pour " + ag
              + ". geometrie renvoyee nulle: " + geom);
    } else if (!geom.isValid()) {
      SmallestSurroundingRectangleAction.logger
          .warn("Echec de l'application d'algorithme PPRE pour " + ag
              + ". geometrie renvoyee non valide: " + geom);
    } else if (!(geom instanceof IPolygon)) {
      SmallestSurroundingRectangleAction.logger
          .warn("Echec de l'application d'algorithme PPRE pour " + ag
              + ". geometrie renvoyee n'est pas un polygone: " + geom);
    } else if (geom.area() < 0.0001) {
      SmallestSurroundingRectangleAction.logger
          .warn("Echec de l'application d'algorithme PPRE pour " + ag
              + ". geometrie renvoyee de surface presque nulle: " + geom);
    } else if (geom.isEmpty()) {
      SmallestSurroundingRectangleAction.logger
          .warn("Echec de l'application d'algorithme PPRE pour " + ag
              + ". geometrie vide: " + geom);
    } else {
      ag.setGeom(geom);
    }
    return ActionResult.UNKNOWN;
  }

}
