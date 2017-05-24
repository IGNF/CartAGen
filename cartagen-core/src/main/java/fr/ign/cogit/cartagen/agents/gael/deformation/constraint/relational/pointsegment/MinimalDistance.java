/*
 * Créé le 12 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointsegment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * Constraint on the minimal distance between a point and a segment. Depending
 * on the cases, the point is constraint to displace in a orthogonal direction
 * of the segment, or not.
 * 
 * @author JGaffuri
 * 
 */
public class MinimalDistance extends SubmicroRelationnalConstraint {

  public double distance;

  private GAELPointSingleton ps;

  private GAELSegment s;

  public MinimalDistance(GAELPointSingleton ps, GAELSegment s,
      double importance, double distance) {
    super(ps, s, importance);
    this.ps = ps;
    this.s = s;
    this.distance = distance;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    // calculs des produits scalaires pour connaitre la configuration
    double ps1 = (this.s.getP2().getX() - this.s.getP1().getX())
        * (this.ps.getPointAgent().getX() - this.s.getP1().getX())
        + (this.s.getP2().getY() - this.s.getP1().getY())
            * (this.ps.getPointAgent().getY() - this.s.getP1().getY());
    double ps2 = (this.s.getP1().getX() - this.s.getP2().getX())
        * (this.ps.getPointAgent().getX() - this.s.getP2().getX())
        + (this.s.getP1().getY() - this.s.getP2().getY())
            * (this.ps.getPointAgent().getY() - this.s.getP2().getY());

    if (p == this.ps.getPointAgent()) {
      if (ps1 > 0.0 && ps2 > 0.0) {
        // le point p est entre s.p1 et s.p2
        IDirectPosition proj = p.getProj(this.s);
        double d = p.getDistanceCourante(proj.getX(), proj.getY());
        if (d > this.distance) {
          return;
        }
        if (d == 0) {
          double a = this.distance * alpha * 0.5 / this.s.getLength();
          new PointAgentDisplacementAction(p, this,
              a * (this.s.getP2().getY() - this.s.getP1().getY()),
              a * (this.s.getP1().getX() - this.s.getP2().getX()));
        } else {
          double a = alpha * 0.5 * (this.distance / d - 1);
          new PointAgentDisplacementAction(p, this,
              a * (p.getX() - proj.getX()), a * (p.getY() - proj.getY()));
        }
      } else if (ps1 <= 0.0 && ps2 > 0.0) {
        // le point p est du cote de s.p1
        double d = p.getDistanceCourante(this.s.getP1());
        if (d > this.distance) {
          return;
        }
        if (d == 0) {
          double a = this.distance * alpha * 0.5 / this.s.getLength();
          new PointAgentDisplacementAction(p, this,
              a * (this.s.getP1().getX() - this.s.getP2().getX()),
              a * (this.s.getP1().getY() - this.s.getP2().getY()));
        } else {
          double a = alpha * 0.5 * (this.distance / d - 1);
          new PointAgentDisplacementAction(p, this,
              a * (p.getX() - this.s.getP1().getX()),
              a * (p.getY() - this.s.getP1().getY()));
        }
      } else if (ps2 <= 0.0 && ps1 > 0.0) {
        // le point p est du cote de s.p2
        double d = p.getDistanceCourante(this.s.getP2());
        if (d > this.distance) {
          return;
        }
        if (d == 0) {
          double a = this.distance * alpha * 0.5 / this.s.getLength();
          new PointAgentDisplacementAction(p, this,
              a * (this.s.getP2().getX() - this.s.getP1().getX()),
              a * (this.s.getP2().getY() - this.s.getP1().getY()));
        } else {
          double a = alpha * 0.5 * (this.distance / d - 1);
          new PointAgentDisplacementAction(p, this,
              a * (p.getX() - this.s.getP2().getX()),
              a * (p.getY() - this.s.getP2().getY()));
        }
      }
    } else if (p == this.s.getP1() || p == this.s.getP2()) {
      if (ps1 > 0.0 && ps2 > 0.0) {
        // le point p est entre s.p1 et s.p2
        IDirectPosition proj = this.ps.getPointAgent().getProj(this.s);
        double d = this.ps.getPointAgent().getDistanceCourante(proj.getX(),
            proj.getY());
        if (d > this.distance) {
          return;
        }
        if (d == 0) {
          double d_ = p.getDistanceCourante(this.ps.getPointAgent());
          double dd = this.s.getLength();
          double a = (1 - d_ / dd) * alpha * 0.5 * this.distance;
          new PointAgentDisplacementAction(p, this,
              a * (this.s.getP1().getY() - this.s.getP2().getY()),
              a * (this.s.getP2().getX() - this.s.getP1().getX()));
        } else {
          double d_ = p.getDistanceCourante(proj.getX(), proj.getY());
          double dd = this.s.getLength();
          double a = (1 - d_ / dd) * alpha * 0.5 * (this.distance / d - 1);
          new PointAgentDisplacementAction(p, this,
              a * (proj.getX() - this.ps.getPointAgent().getX()),
              a * (proj.getY() - this.ps.getPointAgent().getY()));
        }
      } else {
        // le point p n'est pas entre s.p1 et s.p2
        double d = this.ps.getPointAgent().getDistanceCourante(p.getX(),
            p.getY());
        if (d > this.distance) {
          return;
        }
        if (d == 0) {
          IPointAgent p_ = null;
          if (p == this.s.getP1()) {
            p_ = this.s.getP2();
          } else {
            p_ = this.s.getP1();
          }
          double a = alpha * 0.5 * this.distance;
          new PointAgentDisplacementAction(p, this, a * (p_.getX() - p.getX()),
              a * (p_.getY() - p.getY()));
        } else {
          double a = alpha * 0.5 * (this.distance / d - 1);
          new PointAgentDisplacementAction(p, this,
              a * (p.getX() - this.ps.getPointAgent().getX()),
              a * (p.getY() - this.ps.getPointAgent().getY()));
        }
      }
    } else {
    }
  }

  public int getViolation() {
    double d = this.ps.getPointAgent().getDistanceCourante(this.s);
    if (d > this.distance) {
      return 0;
    }
    return (int) ((this.distance - d) / this.distance);
  }

}
