/*
 * Créé le 14 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointsegment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * Constraint on a point and a segment: the distance between them is constrained
 * to be null.
 * 
 * @author JGaffuri
 */
public class Join extends SubmicroRelationnalConstraint {

  private GAELPointSingleton ps;

  private GAELSegment s;

  public Join(GAELPointSingleton ps, GAELSegment s, double importance) {
    super(ps, s, importance);
    this.ps = ps;
    this.s = s;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    double ps1, ps2;
    // calculs des produits scalaires pour connaitre la configuration
    ps1 = (this.s.getP2().getX() - this.s.getP1().getX())
        * (this.ps.getPointAgent().getX() - this.s.getP1().getX())
        + (this.s.getP2().getY() - this.s.getP1().getY())
            * (this.ps.getPointAgent().getY() - this.s.getP1().getY());
    ps2 = (this.s.getP1().getX() - this.s.getP2().getX())
        * (this.ps.getPointAgent().getX() - this.s.getP2().getX())
        + (this.s.getP1().getY() - this.s.getP2().getY())
            * (this.ps.getPointAgent().getY() - this.s.getP2().getY());

    if (p == this.ps.getPointAgent()) {
      if (ps1 > 0.0 && ps2 > 0.0) {
        // le point p est entre s.p1 et s.p2
        IDirectPosition proj = this.ps.getPointAgent().getProj(this.s);
        new PointAgentDisplacementAction(this.ps.getPointAgent(), this,
            alpha * 0.5 * (proj.getX() - p.getX()),
            alpha * 0.5 * (proj.getY() - p.getY()));
      }
      // le point p est du cote de s.p1
      else if (ps1 <= 0.0 && ps2 > 0.0) {
        new PointAgentDisplacementAction(this.ps.getPointAgent(), this,
            alpha * 0.5 * (this.s.getP1().getX() - p.getX()),
            alpha * 0.5 * (this.s.getP1().getY() - p.getY()));
      } else if (ps1 > 0.0 && ps2 <= 0.0) {
        new PointAgentDisplacementAction(this.ps.getPointAgent(), this,
            alpha * 0.5 * (this.s.getP2().getX() - p.getX()),
            alpha * 0.5 * (this.s.getP2().getY() - p.getY()));
      } else {
      }
    } else if (p == this.s.getP1() || p == this.s.getP2()) {
      if (ps1 > 0.0 && ps2 > 0.0) {
        // le minimum est atteint au niveau du projeté du point sur le segment.
        IDirectPosition proj = this.ps.getPointAgent().getProj(this.s);
        double d = p.getDistanceCourante(proj.getX(), proj.getY());
        double dd = this.s.getLength();
        new PointAgentDisplacementAction(p, this,
            (dd - d) / dd * alpha * 0.5
                * (this.ps.getPointAgent().getX() - proj.getX()),
            (dd - d) / dd * alpha * 0.5
                * (this.ps.getPointAgent().getY() - proj.getY()));
      } else {
        new PointAgentDisplacementAction(p, this,
            alpha * 0.5 * (this.ps.getPointAgent().getX() - p.getX()),
            alpha * 0.5 * (this.ps.getPointAgent().getY() - p.getY()));
      }
    } else {
    }
  }

  public int getViolation() {
    return 0;
  }

}
