// 19 oct. 2005
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;

/**
 * Constraint that forces a triangle to move to make the elevation value of the
 * buildings on it preserved.
 * 
 * @author JGaffuri
 * 
 */
public class BuildingsElevationPreservation extends SubmicroSimpleConstraint {

  public BuildingsElevationPreservation(GAELTriangle tp, double importance) {
    super(tp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent ap, double alpha) {
    GAELTriangle tp = (GAELTriangle) this.getSubmicro();
    if (ap != tp.getP1() && ap != tp.getP2() && ap != tp.getP3()) {
      return;
    }

    // recupere la denivellee du triplet
    double dz = tp
        .getEcartAltitudeBatiments((ReliefFieldAgent) ap.getFieldAgent());
    if (dz == 0.0) {
      return;
    }

    // propose une translation du triangle pour retablir le z

    // recupere pv norme
    double[] pv = tp.getSlopeVector();
    // triangle plat: on sort
    if (pv[2] == 1) {
      return;
    }
    // le vecteur pente a pour direction px, py dans le plan
    double px = pv[0] / Math.sqrt(pv[0] * pv[0] + pv[1] * pv[1]);
    double py = pv[1] / Math.sqrt(pv[0] * pv[0] + pv[1] * pv[1]);

    double angle = tp.getSlopeAngle();
    // triangle plat: on sort...
    if (angle == 0.0) {
      return;
    }

    double dist = alpha * dz / Math.tan(angle);
    new PointAgentDisplacementAction(ap, this, -dist * px, -dist * py);
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
