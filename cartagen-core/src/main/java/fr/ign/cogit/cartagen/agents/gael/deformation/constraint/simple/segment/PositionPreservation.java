package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that force a segment to have its position preserved
 * 
 * @author JGaffuri
 * 
 */
public class PositionPreservation extends SubmicroSimpleConstraint {

  public PositionPreservation(GAELSegment pp, double importance) {
    super(pp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    // deux ressort au niveau de chaque point ves sa position initiale
    GAELSegment s = (GAELSegment) this.getSubmicro();
    double a = alpha;
    if (p == s.getP1()) {
      double d1 = Math.sqrt((s.getP1().getXIni() - s.getP1().getX())
          * (s.getP1().getXIni() - s.getP1().getX())
          + (s.getP1().getYIni() - s.getP1().getY())
              * (s.getP1().getYIni() - s.getP1().getY()));
      if (d1 != 0.0) {
        new PointAgentDisplacementAction(s.getP1(), this,
            a * (s.getP1().getXIni() - s.getP1().getX()),
            a * (s.getP1().getYIni() - s.getP1().getY()));
      }
    }
    if (p == s.getP2()) {
      double d2 = Math.sqrt((s.getP2().getXIni() - s.getP2().getX())
          * (s.getP2().getXIni() - s.getP2().getX())
          + (s.getP2().getYIni() - s.getP2().getY())
              * (s.getP2().getYIni() - s.getP2().getY()));
      if (d2 != 0.0) {
        new PointAgentDisplacementAction(s.getP2(), this,
            a * (s.getP2().getXIni() - s.getP2().getX()),
            a * (s.getP2().getYIni() - s.getP2().getY()));
      }
    }

    // vers le centre de gravite si ca ne coupe pas, vers le centre de gravite
    // des parties si ca coupe
    // pb: discontinuité de la force quand ca commence a couper; d'ou
    // l'oscillation
    /*
     * PairePoints s=(PairePoints)subMicro; double
     * pv=(s.p2.x-s.p1.x)*(s.p2.getYIni
     * ()-s.p1.getYIni())-(s.p2.y-s.p1.y)*(s.p2.getXIni()-s.p1.getXIni());
     * double t,xm=0.0,ym=0.0; boolean coupe=false; if (pv!=0) { //calculer
     * l'intersection
     * t=((s.p1.getXIni()-s.p1.x)*(s.p2.getYIni()-s.p1.getYIni())-
     * (s.p1.getYIni()-s.p1.y)*(s.p2.getXIni()-s.p1.getXIni()))/pv;
     * xm=s.p1.x+t*(s.p2.x-s.p1.x); ym=s.p1.y+t*(s.p2.y-s.p1.y); //verifier si
     * l'intersection appartient aux deux segments double
     * ps1=(xm-s.p1.x)*(s.p2.x-s.p1.x)+(ym-s.p1.y)*(s.p2.y-s.p1.y); double
     * ps2=(xm-s.p2.x)*(s.p1.x-s.p2.x)+(ym-s.p2.y)*(s.p1.y-s.p2.y); double
     * ps3=(xm
     * -s.p1.getXIni())*(s.p2.getXIni()-s.p1.getXIni())+(ym-s.p1.getYIni()
     * )*(s.p2.getYIni()-s.p1.getYIni()); double
     * ps4=(xm-s.p2.getXIni())*(s.p1.getXIni
     * ()-s.p2.getXIni())+(ym-s.p2.getYIni())*(s.p1.getYIni()-s.p2.getYIni());
     * if ((ps1>0)&&(ps2>0)&&(ps3>0)&&(ps4>0)) coupe=true; } if (coupe) { double
     * poids=importance/p.sommeImportances; double xG=(xm+p.x+p.getXIni())/3;
     * double yG=(ym+p.y+p.getYIni())/3; new
     * Deplacement(p,this,poids*(xG-p.x),poids*(yG-p.y)); } else { double
     * poids=importance/p.sommeImportances; double
     * xG=(s.p1.getXIni()+s.p2.getXIni()+s.p1.x+s.p2.x)*0.25; double
     * yG=(s.p1.getYIni()+s.p2.getYIni()+s.p1.y+s.p2.y)*0.25; new
     * Deplacement(p,this,poids*(xG-p.x),poids*(yG-p.y)); }
     */

    // tire vers le centre de gravite
    /*
     * PairePoints s=(PairePoints)subMicro; double
     * poids=importance/p.sommeImportances; double
     * xG=(s.p1.getXIni()+s.p2.getXIni()+s.p1.x+s.p2.x)*0.25; double
     * yG=(s.p1.getYIni()+s.p2.getYIni()+s.p1.y+s.p2.y)*0.25; new
     * Deplacement(p,this,poids*(xG-p.x),poids*(yG-p.y));
     */

    // truc autre qui ne marche pas mais qui prend en compte la surface
    /*
     * PairePoints s=(PairePoints)subMicro; double
     * poids=importance/p.sommeImportances; double
     * a=poids*Math.sqrt(s.getAire()); if (p==s.p1){ double
     * d1=Math.sqrt((s.p1.getXIni
     * ()-s.p1.x)*(s.p1.getXIni()-s.p1.x)+(s.p1.getYIni
     * ()-s.p1.y)*(s.p1.getYIni()-s.p1.y)); if (d1!=0.0) new
     * Deplacement(s.p1,this
     * ,a*(s.p1.getXIni()-s.p1.x)/d1,a*(s.p1.getYIni()-s.p1.y)/d1); } if
     * (p==s.p2){ double
     * d2=Math.sqrt((s.p2.getXIni()-s.p2.x)*(s.p2.getXIni()-s.p2
     * .x)+(s.p2.getYIni()-s.p2.y)*(s.p2.getYIni()-s.p2.y)); if (d2!=0.0) new
     * Deplacement
     * (s.p2,this,a*(s.p2.getXIni()-s.p2.x)/d2,a*(s.p2.getYIni()-s.p2.y)/d2); }
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
