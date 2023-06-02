/*
 * Créé le 14 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.segmentsegment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * Constraint on two segments: the distance between them is constrained to be
 * null.
 * 
 * @author JGaffuri
 */
public class Join extends SubmicroRelationnalConstraint {
  private static Logger logger = LogManager.getLogger(Join.class.getName());

  private GAELSegment s1;

  private GAELSegment s2;

  public Join(GAELSegment s1, GAELSegment s2, double importance) {
    super(s1, s2, importance);
    this.s1 = s1;
    this.s2 = s2;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {

    // recupere le segment auquel le point n'appartient pas
    GAELSegment s = null;
    if (p == this.s1.getP1() || p == this.s1.getP2()) {
      s = this.s2;
    } else if (p == this.s2.getP1() || p == this.s2.getP2()) {
      s = this.s1;
    } else {
      Join.logger.error("Erreur dans la contrainte " + this + ". le point " + p
          + " n'est pas un point des segments " + this.s1 + " et " + this.s2);
      return;
    }

    double ps1 = (p.getX() - s.getP1().getX())
        * (s.getP2().getX() - s.getP1().getX())
        + (p.getY() - s.getP1().getY()) * (s.getP2().getY() - s.getP1().getY());
    double ps2 = (p.getX() - s.getP2().getX())
        * (s.getP1().getX() - s.getP2().getX())
        + (p.getY() - s.getP2().getY()) * (s.getP1().getY() - s.getP2().getY());

    double dx = 0.0, dy = 0.0;
    if (ps1 > 0.0 && ps2 > 0.0) {
      // le minimum est atteint au niveau du projete de p sur s
      IDirectPosition proj = p.getProj(s);
      dx = alpha * 0.5 * (proj.getX() - p.getX());
      dy = alpha * 0.5 * (proj.getY() - p.getY());
    }
    // le minimum est atteint au niveau de p1.
    else if (ps1 <= 0.0 && ps2 > 0.0) {
      dx = alpha * 0.5 * (s.getP1().getX() - p.getX());
      dy = alpha * 0.5 * (s.getP1().getY() - p.getY());
    }
    // le minimum est atteint au niveau de p2.
    else if (ps1 > 0.0 && ps2 <= 0.0) {
      dx = alpha * 0.5 * (s.getP2().getX() - p.getX());
      dy = alpha * 0.5 * (s.getP2().getY() - p.getY());
    } else {
    }
    new PointAgentDisplacementAction(p, this, dx, dy);
  }

  public int getViolation() {
    return 0;
  }

}
