// 19 oct. 2005
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;

/**
 * Constraint on the triangle azimutal slope orientation. Of course, the
 * triangle is supposed to be in 3D.
 * 
 * @author JGaffuri
 * 
 */
public class AzimutalSlopeOrientationPreservation
    extends SubmicroSimpleConstraint {

  public AzimutalSlopeOrientationPreservation(GAELTriangle t,
      double importance) {
    super(t, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent ap, double alpha) {
    GAELTriangle t = (GAELTriangle) this.getSubmicro();
    if (ap != t.getP1() && ap != t.getP2() && ap != t.getP3()) {
      return;
    }
    double angle = -alpha * t.getSlopeAzimutalOrientationDifference();
    // if (tp.estRetourne()) return; //angle=-angle;

    // propose la rotation autour du centre
    double cos = Math.cos(angle), sin = Math.sin(angle);
    double xg = t.getX(), yg = t.getY();
    double dx = xg - ap.getX() + cos * (ap.getX() - xg)
        - sin * (ap.getY() - yg);
    double dy = yg - ap.getY() + sin * (ap.getX() - xg)
        + cos * (ap.getY() - yg);

    new PointAgentDisplacementAction(ap, this, dx, dy);
  }

  @Override
  public void computeCurrentValue() {
    // TODO
  }

  @Override
  public void computeGoalValue() {
    // TODO
  }

  @Override
  public void computeSatisfaction() {
    // TODO
  }

  @Override
  public void computePriority() {
  }

}
