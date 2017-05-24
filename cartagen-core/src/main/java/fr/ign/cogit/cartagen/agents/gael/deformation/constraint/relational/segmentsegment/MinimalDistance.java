// 17 nov. 2005
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.segmentsegment;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * Constraint on the minimal distance between two segments. Depending on the
 * cases, the segment points are constrained to displace in a orthogonal
 * direction of the other segment, or not.
 * 
 * @author JGaffuri
 * 
 */
public class MinimalDistance extends SubmicroRelationnalConstraint {
  private static Logger logger = Logger
      .getLogger(MinimalDistance.class.getName());

  public double distance;

  private GAELSegment s1;

  private GAELSegment s2;

  public MinimalDistance(GAELSegment s1, GAELSegment s2, double importance,
      double distance) {
    super(s1, s2, importance);
    this.s1 = s1;
    this.s2 = s2;
    this.distance = distance;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = null;
    if (p == this.s1.getP1() || p == this.s1.getP2()) {
      s = this.s2;
    } else if (p == this.s2.getP1() || p == this.s2.getP2()) {
      s = this.s1;
    } else {
      MinimalDistance.logger.error("Erreur dans la contrainte " + this
          + ". le point " + p + " n'est pas un point des segments " + this.s1
          + " et " + this.s2);
      return;
    }

    double ps1 = (p.getX() - s.getP1().getX())
        * (s.getP2().getX() - s.getP1().getX())
        + (p.getY() - s.getP1().getY()) * (s.getP2().getY() - s.getP1().getY());
    double ps2 = (p.getX() - s.getP2().getX())
        * (s.getP1().getX() - s.getP2().getX())
        + (p.getY() - s.getP2().getY()) * (s.getP1().getY() - s.getP2().getY());

    // traiter le cas ou les deux se coupent

    double dx = 0.0, dy = 0.0;
    if (ps1 > 0.0 && ps2 > 0.0) {
      // le minimum est atteint au niveau du projete de p sur s
      IDirectPosition proj = p.getProj(s);
      double d = p.getDistanceCourante(proj.getX(), proj.getY());
      if (d > this.distance) {
        return;
      }
      if (d == 0.0) {
        double a = this.distance * alpha * 0.5 / s.getLength();
        new PointAgentDisplacementAction(p, this,
            a * (s.getP2().getY() - s.getP1().getY()),
            a * (s.getP1().getX() - s.getP2().getX()));
      } else {
        double a = alpha * 0.5 * (this.distance / d - 1);
        dx = a * (p.getX() - proj.getX());
        dy = a * (p.getY() - proj.getY());
      }
    } else if (ps1 <= 0.0 && ps2 > 0.0) {
      // le minimum est atteint au niveau de p1.
      double d = p.getDistanceCourante(s.getP1());
      if (d > this.distance) {
        return;
      }
      if (d == 0.0) {
        double a = this.distance * alpha * 0.5 / s.getLength();
        new PointAgentDisplacementAction(p, this,
            a * (s.getP1().getX() - s.getP2().getX()),
            a * (s.getP1().getY() - s.getP2().getY()));
      } else {
        double a = alpha * 0.5 * (this.distance / d - 1);
        dx = a * (p.getX() - s.getP1().getX());
        dy = a * (p.getY() - s.getP1().getY());
      }
    } else if (ps1 > 0.0 && ps2 <= 0.0) {
      // le minimum est atteint au niveau de p2.
      double d = p.getDistanceCourante(s.getP2());
      if (d > this.distance) {
        return;
      }
      if (d == 0.0) {
        double a = this.distance * alpha * 0.5 / s.getLength();
        new PointAgentDisplacementAction(p, this,
            a * (s.getP2().getX() - s.getP1().getX()),
            a * (s.getP2().getY() - s.getP1().getY()));
      } else {
        double a = alpha * 0.5 * (this.distance / d - 1);
        dx = a * (p.getX() - s.getP2().getX());
        dy = a * (p.getY() - s.getP2().getY());
      }
    } else {
    }
    new PointAgentDisplacementAction(p, this, dx, dy);
  }

  public int getViolation() {
    return 0;
  }

}
