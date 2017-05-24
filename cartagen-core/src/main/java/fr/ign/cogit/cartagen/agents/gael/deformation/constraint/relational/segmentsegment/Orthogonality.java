package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.segmentsegment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.SubmicroRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that forces 2 segments to rotate to be orthogonal. It can be
 * useful for exemple if we want to make the contour segments intersecting road
 * network segments to be orthogonal.
 * 
 * @author julien Gaffuri
 */
public class Orthogonality extends SubmicroRelationnalConstraint {

  private GAELSegment s1;

  private GAELSegment s2;

  public Orthogonality(GAELSegment s1, GAELSegment s2, double importance) {
    super(s1, s2, importance);
    this.s1 = s1;
    this.s2 = s2;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    System.out.print(this.s1.toString() + this.s2.toString());
    // PaireSegments ps=(PaireSegments)subMicro;

    // double poids=importance/p.sommeImportances;

    // recupere le segment auquel le point n'appartient pas
    /*
     * PairePoints pp=null; if ((p==ps.pp1.p1)||(p==ps.pp1.p2)) {pp=ps.pp2;}
     * else if ((p==ps.pp2.p1)||(p==ps.pp2.p2)) {pp=ps.pp1;} else {}
     */

  }

  public int getViolation() {
    return 0;
  }

}
