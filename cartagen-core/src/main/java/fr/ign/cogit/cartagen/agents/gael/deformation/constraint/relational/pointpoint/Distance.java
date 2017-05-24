package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointpoint;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;

/**
 * Constraint on the distance between 2 points. It is equivalent as the segment
 * length constraint.
 * 
 * @author JGaffuri
 * 
 */
public class Distance extends SubmicroRelationnalConstraint {

  private double distance;

  private GAELPointSingleton ps1;

  private GAELPointSingleton ps2;

  public Distance(GAELPointSingleton ps1, GAELPointSingleton ps2,
      double importance, double distance) {
    super(ps1, ps2, importance);
    this.ps1 = ps1;
    this.ps2 = ps2;
    this.distance = distance;
  }

  public Distance(GAELPointSingleton ps1, GAELPointSingleton ps2,
      double importance) {
    super(ps1, ps2, importance);
    this.ps1 = ps1;
    this.ps2 = ps2;
    this.distance = ps1.getPointAgent()
        .getDistanceInitiale(ps2.getPointAgent());
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    double dist = this.ps1.getPointAgent()
        .getDistance(this.ps2.getPointAgent());
    double dx, dy;
    // cas peu probable mais on ne sait jamais
    if (dist == 0) {
      dx = 0.5 * alpha * (this.ps1.getPointAgent().getXIni()
          - this.ps2.getPointAgent().getXIni());
      dy = 0.5 * alpha * (this.ps1.getPointAgent().getYIni()
          - this.ps2.getPointAgent().getYIni());
    } else {
      double a = 0.5 * alpha * (this.distance / dist - 1);
      dx = a
          * (this.ps1.getPointAgent().getX() - this.ps2.getPointAgent().getX());
      dy = a
          * (this.ps1.getPointAgent().getY() - this.ps2.getPointAgent().getY());
    }
    if (p == this.ps1.getPointAgent()) {
      new PointAgentDisplacementAction(p, this, dx, dy);
    } else if (p == this.ps2.getPointAgent()) {
      new PointAgentDisplacementAction(p, this, -dx, -dy);
    }
  }

  public int getViolation() {
    if (this.distance == 0) {
      return 0;
    }
    return (int) Math.abs(
        100 * (this.ps1.getPointAgent().getDistance(this.ps2.getPointAgent())
            - this.distance) / this.distance);
  }

}
