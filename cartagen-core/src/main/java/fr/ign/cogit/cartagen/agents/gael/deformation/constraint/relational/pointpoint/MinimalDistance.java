/*
 * Créé le 12 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointpoint;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;

/**
 * Constraint on the minimal distance between 2 points. Of course, if the two
 * points ar far enougth, this constraint has no effect.
 * 
 * @author JGaffuri
 * 
 */
public class MinimalDistance extends SubmicroRelationnalConstraint {

  private double distance;

  private GAELPointSingleton ps1;

  private GAELPointSingleton ps2;

  public MinimalDistance(GAELPointSingleton ps1, GAELPointSingleton ps2,
      double importance, double distance) {
    super(ps1, ps2, importance);
    this.ps1 = ps1;
    this.ps2 = ps2;
    this.distance = distance;
  }

  public MinimalDistance(GAELPointSingleton ps1, GAELPointSingleton ps2,
      double importance) {
    super(ps1, ps2, importance);
    this.ps1 = ps1;
    this.ps2 = ps2;
    this.distance = ps1.getPointAgent()
        .getDistanceInitiale(ps2.getPointAgent());
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    double d, a, dx, dy;
    d = this.ps1.getPointAgent().getDistanceCourante(this.ps2.getPointAgent());
    if (d > this.distance) {
      return;
    }
    if (d == 0.0) {
      // cas peu probable mais on ne sait jamais
      a = alpha * this.distance * 0.5 / this.ps1.getPointAgent()
          .getDistanceInitiale(this.ps2.getPointAgent());
      dx = a * (this.ps2.getPointAgent().getXIni()
          - this.ps1.getPointAgent().getXIni());
      dy = a * (this.ps2.getPointAgent().getYIni()
          - this.ps1.getPointAgent().getYIni());
      if (p == this.ps1.getPointAgent()) {
        new PointAgentDisplacementAction(p, this, -dx, -dy);
      } else if (p == this.ps2.getPointAgent()) {
        new PointAgentDisplacementAction(p, this, dx, dy);
      } else {
      }
    } else {
      a = alpha * (this.distance - d) / d * 0.5;
      dx = a
          * (this.ps2.getPointAgent().getX() - this.ps1.getPointAgent().getX());
      dy = a
          * (this.ps2.getPointAgent().getY() - this.ps1.getPointAgent().getY());
      if (p == this.ps1.getPointAgent()) {
        new PointAgentDisplacementAction(p, this, -dx, -dy);
      } else if (p == this.ps2.getPointAgent()) {
        new PointAgentDisplacementAction(p, this, dx, dy);
      } else {
      }
    }
  }

  public int getViolation() {
    /*
     * Segment pp=(Segment)subMicro; double
     * d=pp.ap1.getDistanceCourante(pp.ap2); if (d>distance) return 0; else
     * return (int)((distance-d)/distance);
     */
    return 0;
  }

}
