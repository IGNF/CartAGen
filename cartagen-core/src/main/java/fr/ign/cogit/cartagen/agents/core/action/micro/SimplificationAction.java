/**
 * @author julien Gaffuri 30 sept. 2008
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;

/**
 * @author JGaffuri
 * 
 */
public class SimplificationAction extends ActionCartagen {
  static Logger logger = Logger.getLogger(SimplificationAction.class.getName());
  /**
   */
  @ActionField
  private double seuil;

  public SimplificationAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double seuil) {
    super(ag, cont, poids);
    this.seuil = seuil;
  }

  @Override
  public ActionResult compute() {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();
    IGeometry geom = SimplificationAlgorithm
        .simplification((IPolygon) ag.getGeom(), this.seuil);

    if (geom == null) {
      SimplificationAction.logger
          .warn("Echec de l'application d'algorithme de simplification pour "
              + ag + ". geometrie renvoyee nulle: " + geom);
    } else if (!geom.isValid()) {
      SimplificationAction.logger
          .warn("Echec de l'application d'algorithme de simplification pour "
              + ag + ". geometrie renvoyee non valide: " + geom);
    } else if (!(geom instanceof IPolygon)) {
      SimplificationAction.logger
          .warn("Echec de l'application d'algorithme de simplification pour "
              + ag + ". geometrie renvoyee n'est pas un polygone: " + geom);
    } else if (geom.area() < 0.0001) {
      SimplificationAction.logger
          .warn("Echec de l'application d'algorithme de simplification pour "
              + ag + ". geometrie renvoyee de surface presque nulle: " + geom);
    } else if (geom.isEmpty()) {
      SimplificationAction.logger
          .warn("Echec de l'application d'algorithme de simplification pour "
              + ag + ". geometrie vide: " + geom);
    } else {
      ag.setGeom(geom);
    }
    return ActionResult.UNKNOWN;
  }

}
