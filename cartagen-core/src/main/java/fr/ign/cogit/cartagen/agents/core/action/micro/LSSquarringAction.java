/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.core.action.micro;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.algorithms.polygon.SquarePolygonLS;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * The action to trigger the Least Squares based squaring algorithm.
 * @author gtouya
 * 
 */
public class LSSquarringAction extends ActionCartagen {
  /**
   */
  private double toleranceAngle = 0.0;
  private double midTol = 0.0;

  public LSSquarringAction(IMicroAgentGeneralisation ag, Constraint cont,
      double poids, double toleranceAngle, double toleranceMidAngle) {
    super(ag, cont, poids);
    this.toleranceAngle = toleranceAngle;
    this.midTol = toleranceMidAngle;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    IMicroAgentGeneralisation ab = (IMicroAgentGeneralisation) this.getAgent();
    SquarePolygonLS squaring = new SquarePolygonLS(toleranceAngle, 0.1, midTol);
    squaring.setPolygon((IPolygon) ab.getGeom());
    ab.setGeom(squaring.square());
    return ActionResult.UNKNOWN;
  }

}
