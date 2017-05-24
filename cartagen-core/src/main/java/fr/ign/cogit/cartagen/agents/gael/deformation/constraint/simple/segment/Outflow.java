package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that forces a segment to flow down on the relief
 * 
 * @author julien Gaffuri
 * 
 */
public class Outflow extends SubmicroSimpleConstraint {
  public static double LIMITE = 0.1;

  public Outflow(GAELSegment pp, double importance) {
    super(pp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = (GAELSegment) this.getSubmicro();

    if (p != s.getP1() && p != s.getP2()) {
      return;
    }

    double anglePente = s.getSlopeZenitalOrientation(); // entre 0 et Pi/2
    if (anglePente == 0.0) {
      return; // segment est plat
    }
    if (anglePente == 999.9) {
      anglePente = 0.0; // la pente n'est pas definie sous le mnt
    }
    double ecartOrientationPente = s.getSlopeAzimutalOrientationDifference(); // ecart
                                                                              // entre
                                                                              // l'orientation
                                                                              // azimutale
                                                                              // de
                                                                              // la
                                                                              // pente
                                                                              // et
                                                                              // celle
                                                                              // du
                                                                              // segment.
                                                                              // entre
                                                                              // -Pi
                                                                              // et
                                                                              // Pi
                                                                              // en
                                                                              // radians
    if (ecartOrientationPente == 0.0) {
      return; // segment déjà orienté vers la pente
    }

    // rotation: c'est une rotation autour du centre du segment pour couler vers
    // la pente
    double cos = Math
        .cos(-alpha * 2 * anglePente / Math.PI * ecartOrientationPente),
        sin = Math
            .sin(-alpha * 2 * anglePente / Math.PI * ecartOrientationPente);
    double dxRotation = 0.0, dyRotation = 0.0;
    if (p == s.getP1()) {
      dxRotation = 0.5 * (s.getP2().getX() - s.getP1().getX()
          + cos * (s.getP1().getX() - s.getP2().getX())
          - sin * (s.getP1().getY() - s.getP2().getY()));
      dyRotation = 0.5 * (s.getP2().getY() - s.getP1().getY()
          + sin * (s.getP1().getX() - s.getP2().getX())
          + cos * (s.getP1().getY() - s.getP2().getY()));
    } else {
      dxRotation = 0.5 * (s.getP1().getX() - s.getP2().getX()
          + cos * (s.getP2().getX() - s.getP1().getX())
          - sin * (s.getP2().getY() - s.getP1().getY()));
      dyRotation = 0.5 * (s.getP1().getY() - s.getP2().getY()
          + sin * (s.getP2().getX() - s.getP1().getX())
          + cos * (s.getP2().getY() - s.getP1().getY()));
    }

    // translation: translation dans une direction orthogonale à celle du
    // segment pour tomber vers la pente
    double anglePentePourCouler = s.getSlopeOrientationToFlowDown(); // angle
                                                                     // pour
                                                                     // couler
    double dxTranslation = 0.0, dyTranslation = 0.0;
    if (anglePentePourCouler != 999.9) {
      // longueur du deplacement: la meme que celle de la rotation
      double d = Math.sqrt(dxRotation * dxRotation + dyRotation * dyRotation);
      // calcul du vecteur de deplacement vers la pente
      double dxp = d * Math.cos(anglePentePourCouler); // *Math.abs(ecartOrientationPente)/Math.PI;
      double dyp = d * Math.sin(anglePentePourCouler); // *Math.abs(ecartOrientationPente)/Math.PI;
      // calcul du vecteur orthogonal au segment
      double dxn = -s.getP2().getY() + s.getP1().getY();
      double dyn = s.getP2().getX() - s.getP1().getX();
      double dn = Math.sqrt(dxn * dxn + dyn * dyn);
      dxn /= dn;
      dyn /= dn;
      // projection du vecteur de deplacement vers la pente selon le vecteur
      // orthogonal au segment
      double ps = dxn * dxp + dyn * dyp; // produit scalaire
      dxTranslation = ps * dxn;
      dyTranslation = ps * dyn;
    }

    // new Deplacement(p,this,dxRotation,dyRotation);
    // new Deplacement(p,this,dxTranslation,dyTranslation);
    new PointAgentDisplacementAction(p, this,
        (dxRotation + dxTranslation) * 0.5, (dyRotation + dyTranslation) * 0.5);
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
