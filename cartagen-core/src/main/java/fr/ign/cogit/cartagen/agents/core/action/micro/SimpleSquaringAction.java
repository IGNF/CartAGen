/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.algorithms.polygon.PolygonSquaring;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * An action that uses the simple squaring algorithm from
 * {@link PolygonSquaring}
 * @author gtouya
 * 
 */
public class SimpleSquaringAction extends ActionCartagen {
  /**
   */
  private double toleranceAngle = 0.0;
  /**
   */
  private double correctionAngle = 0.0;

  public SimpleSquaringAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double toleranceAngle, double correctionAngle) {
    super(ag, cont, poids);
    this.toleranceAngle = toleranceAngle;
    this.correctionAngle = correctionAngle;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    IMicroAgentGeneralisation ag = (IMicroAgentGeneralisation) this.getAgent();
    PolygonSquaring squaring = new PolygonSquaring((IPolygon) ag.getGeom(),
        toleranceAngle, correctionAngle);
    IPolygon squared = squaring.simpleSquaring();
    ag.setGeom(squared);
    return ActionResult.UNKNOWN;
  }

}
