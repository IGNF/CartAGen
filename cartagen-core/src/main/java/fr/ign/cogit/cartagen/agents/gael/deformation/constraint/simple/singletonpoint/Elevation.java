package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.singletonpoint;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

/**
 * A constraint on the point elevation value. The point is forced to move to
 * have its elevation value changed toward a goal one.
 * 
 * @author JGaffuri
 * 
 */
public class Elevation extends SubmicroSimpleConstraint {

  /**
   * The goal elevation value
   */
  private double zGoal;

  /**
   * The constructor
   * 
   * @param p
   * @param importance
   * @param zGoal
   */
  public Elevation(GAELPointSingleton p, double importance, double zGoal) {
    super(p, importance);
    this.zGoal = zGoal;
  }

  /**
   * The constructor for a preservation of the elevation value.
   * 
   * @param p
   * @param importance
   */
  public Elevation(GAELPointSingleton p, double importance) {
    this(p, importance, p.getPointAgent().getZIni());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.constraint.SubmicroConstraint#
   * proposeDisplacement (fr.ign.cogit.gaeldeformation.PointAgent, double)
   */
  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    // get the relief triangle under the point
    GAELTriangle tri = ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
        CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
            .getTriangle(p.getPosition());

    // if the DTM is not present under the point, return
    if (tri == null) {
      return;
    }

    // get the slope vector
    double[] pv = tri.getSlopeVector();

    // if the triangle is horizontal, return
    if (pv[2] == 1) {
      return;
    }

    // the slope vector has a (dx, dy) direction in the plan
    double dx = pv[0] / Math.sqrt(pv[0] * pv[0] + pv[1] * pv[1]);
    double dy = pv[1] / Math.sqrt(pv[0] * pv[0] + pv[1] * pv[1]);

    // if the triangle is horizontal, return
    double angle = tri.getSlopeAngle();
    if (angle == 0.0) {
      return;
    }

    // get the elevation under the point
    double z = tri.getZ(p.getPosition());

    // compute the elevation difference
    double dz = this.zGoal - z;
    if (dz == 0.0) {
      return;
    }

    // propose a displacement toward the slope
    double dist = alpha * dz / Math.tan(angle);
    new PointAgentDisplacementAction(p, this, -dist * dx, -dist * dy);
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
