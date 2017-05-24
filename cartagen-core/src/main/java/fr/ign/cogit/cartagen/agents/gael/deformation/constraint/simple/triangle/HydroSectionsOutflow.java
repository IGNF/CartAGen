// 19 oct. 2005
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

/**
 * Constraint that forces the triangle to change to make the hydrographic
 * sections on it flow down.
 * 
 * @author JGaffuri
 * 
 */
public class HydroSectionsOutflow extends SubmicroSimpleConstraint {

  public HydroSectionsOutflow(GAELTriangle tp, double importance) {
    super(tp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent ap, double alpha) {
    GAELTriangle tp = (GAELTriangle) this.getSubmicro();
    if (ap != tp.getP1() && ap != tp.getP2() && ap != tp.getP3()) {
      return;
    }

    double ecartOrientationPourFaireCouler = tp
        .getSlopeAzimutalOrientationDifferenceToFlowDown(
            ((HydroNetworkAgent) AgentUtil.getAgentFromGeneObj(CartAGenDoc
                .getInstance().getCurrentDataset().getHydroNetwork()))); // entre
                                                                         // -Pi
                                                                         // et
                                                                         // Pi

    System.out.println(ecartOrientationPourFaireCouler);
    if (ecartOrientationPourFaireCouler == 0.0) {
      return; // cas où il n'y a pas de reseau hydro dessus ou où le réseau
      // coule déja parfaitement bien
    }

    // le role de ce coefficient est de faire en sorte que ca coule plus la ou
    // c'est plus pentu et que ça coule pas là où c'est plat
    double coefficientPente = tp.getSlopeAngle(); // entre 0 et Pi
    if (coefficientPente == 0.0) {
      return; // le triangle est plat
    }
    if (coefficientPente >= Math.PI / 2) {
      coefficientPente = 1.0; // le triangle est retourne
    } else {
      coefficientPente /= Math.PI / 2.0;
    }

    // un coefficient qui permet de faire en sorte que
    // plus le rapport taille du réseau sur le triangle SUR taille du triangle
    // est fort, plus la valeur doit être forte.
    double coefficientQuantite = tp.getHydroNetworkLengthUp(
        ((HydroNetworkAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork())))
        / Math.sqrt(Math.abs(tp.getArea()));

    // rotation autour du centre pour faire en sorte que ça coule
    double cos = Math.cos(-alpha * ecartOrientationPourFaireCouler
        * coefficientPente * coefficientQuantite);
    double sin = Math.sin(-alpha * ecartOrientationPourFaireCouler
        * coefficientPente * coefficientQuantite);
    double xg = tp.getX(), yg = tp.getY();
    double dxRotation = xg - ap.getX() + cos * (ap.getX() - xg)
        - sin * (ap.getY() - yg);
    double dyRotation = yg - ap.getY() + sin * (ap.getX() - xg)
        + cos * (ap.getY() - yg);

    // translation dans la direction opposée à celle de la pente du triangle
    // pour faire en sorte que ça coule
    double orientationAzimutalePente = tp.getSlopeAzimutalOrientation();
    if (orientationAzimutalePente == -999.9) {
      return; // triangle plat
    }
    double dxTranslation = 0.0, dyTranslation = 0.0;
    // longueur du deplacement: la meme que celle de la rotation
    double d = Math.sqrt(dxRotation * dxRotation + dyRotation * dyRotation);
    System.out.println("longueur du deplacement: " + d);
    // calcul du vecteur de deplacement dans le sens opposé à la pente
    double dxp = -d * Math.cos(orientationAzimutalePente); // *Math.abs(ecartOrientationPourFaireCouler)/Math.PI;
    double dyp = -d * Math.sin(orientationAzimutalePente); // *Math.abs(ecartOrientationPourFaireCouler)/Math.PI;
    // le vecteur orthogonal au reseau hydro a pour direction
    // orientationAzimutalePente+ecartOrientationPourFaireCouler
    double dxn = -Math
        .sin(orientationAzimutalePente - ecartOrientationPourFaireCouler);
    double dyn = Math
        .cos(orientationAzimutalePente - ecartOrientationPourFaireCouler);
    // projection du vecteur de deplacement vers la pente selon le vecteur
    // orthogonal au reseau
    double ps = dxn * dxp + dyn * dyp; // produit scalaire
    dxTranslation = ps * dxn;
    dyTranslation = ps * dyn;

    /*
     * dxTranslation=dxp; dyTranslation=dyp;
     */

    // new Deplacement(ap,this,dxRotation,dyRotation);
    new PointAgentDisplacementAction(ap, this, dxTranslation, dyTranslation);
    // new
    // Deplacement(ap,this,(dxRotation+dxTranslation)*0.5,(dyRotation+dyTranslation)*0.5);
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
