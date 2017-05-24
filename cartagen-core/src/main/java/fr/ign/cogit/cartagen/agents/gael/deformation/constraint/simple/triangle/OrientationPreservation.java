// 19 oct. 2005
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;

/**
 * Constraint that forces the normal vector orientation (in 3D) to the triangle
 * to be preserved. The triangle is supposed to be in 3D.
 * 
 * @author JGaffuri
 * 
 */
public class OrientationPreservation extends SubmicroSimpleConstraint {

  public OrientationPreservation(GAELTriangle tp, double importance) {
    super(tp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
  }

  // a revoir
  public void proposeDeplacement() {
    /*
     * TripletPoints t=(TripletPoints)gr; double[] oi,oc; double
     * ix,iy,iz,cx,cy,cz; double cos,sin,ux,uy,uz; double
     * a11,a12,a13,a21,a22,a23; double xg,yg,zg,a; //recupere les orientations
     * initiale et courante oi=t.getOrientationInitiale(); ix=oi[0]; iy=oi[1];
     * iz=oi[2]; oc=t.getOrientationCourante(); cx=oc[0]; cy=oc[1]; cz=oc[2];
     * //calcul du pv et des sin et cos ux=cy*iz-cz*iy; uy=cz*ix-cx*iz;
     * uz=cx*iy-cy*ix; cos=ix*cx+iy*cy+iz*cz; sin=Math.sqrt(ux*ux+uy*uy+uz*uz);
     * xg=(t.p1.x+t.p2.y+t.p3.z)/3.0; yg=(t.p1.y+t.p2.y+t.p3.y)/3.0;
     * zg=(t.p1.z+t.p2.z+t.p3.z)/3.0; //coef de la matrice de rotation composée
     * au projecteur sur le plan horizontal a11=cos+(1-cos)*ux*ux;
     * a12=(1-cos)*ux*uy-uz*sin; a13=(1-cos)*ux*uz+uy*sin;
     * a21=(1-cos)*ux*uy+uz*sin; a22=cos+(1-cos)*uy*uy;
     * a23=(1-cos)*uy*uz-ux*sin;
     * 
     * //affectation a=poids*(Math.atan2(sin,cos)); //t.p1.dx+=
     * a*(a11*(t.p1.x-xg)+a12*(t.p1.y-yg)+a13*(t.p1.z-zg)+xg-t.p1.x);
     * //t.p1.dy+=
     * a*(a21*(t.p1.x-xg)+a22*(t.p1.y-yg)+a23*(t.p1.z-zg)+yg-t.p1.y);
     * //t.p2.dx+=
     * a*(a11*(t.p2.x-xg)+a12*(t.p2.y-yg)+a13*(t.p2.z-zg)+xg-t.p2.x);
     * //t.p2.dy+=
     * a*(a21*(t.p2.x-xg)+a22*(t.p2.y-yg)+a23*(t.p2.z-zg)+yg-t.p2.y);
     * //t.p3.dx+=
     * a*(a11*(t.p3.x-xg)+a12*(t.p3.y-yg)+a13*(t.p3.z-zg)+xg-t.p3.x);
     * //t.p3.dy+=
     * a*(a21*(t.p3.x-xg)+a22*(t.p3.y-yg)+a23*(t.p3.z-zg)+yg-t.p3.y); new
     * Deplacement
     * (t.p1,this,a*(a11*(t.p1.x-xg)+a12*(t.p1.y-yg)+a13*(t.p1.z-zg)+xg
     * -t.p1.x),a*(a21*(t.p1.x-xg)+a22*(t.p1.y-yg)+a23*(t.p1.z-zg)+yg-t.p1.y));
     * new
     * Deplacement(t.p2,this,a*(a11*(t.p2.x-xg)+a12*(t.p2.y-yg)+a13*(t.p2.z-zg
     * )+xg
     * -t.p2.x),a*(a21*(t.p2.x-xg)+a22*(t.p2.y-yg)+a23*(t.p2.z-zg)+yg-t.p2.y));
     * new
     * Deplacement(t.p3,this,a*(a11*(t.p3.x-xg)+a12*(t.p3.y-yg)+a13*(t.p3.z-zg
     * )+xg
     * -t.p3.x),a*(a21*(t.p3.x-xg)+a22*(t.p3.y-yg)+a23*(t.p3.z-zg)+yg-t.p3.y));
     */
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
