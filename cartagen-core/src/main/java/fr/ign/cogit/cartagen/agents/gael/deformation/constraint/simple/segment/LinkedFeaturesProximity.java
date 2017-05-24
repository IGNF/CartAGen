/**
 * @author julien Gaffuri 10 sept. 2008
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Constraint that forces the linked fetatures of points of a segment to be
 * further than a goal minimum distance.
 * 
 * @author julien Gaffuri 10 sept. 2008
 * 
 */
public class LinkedFeaturesProximity extends SubmicroSimpleConstraint {

  // la distance minimum en m entre les objets

  private double distanceMinimum;

  public LinkedFeaturesProximity(ISubMicro sm, double importance,
      double distanceMinimummm) {
    super(sm, importance);
    this.distanceMinimum = distanceMinimummm * Legend.getSYMBOLISATI0N_SCALE()
        / 1000.0;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = (GAELSegment) this.getSubmicro();

    // recupere les deux geometries des agents referents des deux points du
    // segment
    IGeometry geom1 = s.getP1().getLinkedFeature()
        .getFeature() instanceof INetworkSection
            ? SectionSymbol.getSymbolExtent(
                (INetworkSection) s.getP1().getLinkedFeature().getFeature())
            : s.getP1().getLinkedFeature().getGeom();
    IGeometry geom2 = s.getP2().getLinkedFeature()
        .getFeature() instanceof INetworkSection
            ? SectionSymbol.getSymbolExtent(
                (INetworkSection) s.getP2().getLinkedFeature().getFeature())
            : s.getP2().getLinkedFeature().getGeom();

    // si les deux objets sont suffisamment loin, ne rien faire
    if (geom1.distance(geom2) >= this.distanceMinimum
        && this.distanceMinimum > 0.0) {
      return;
    }

    double distBut;
    // les deux geometries sont trop proches
    // calcul de la distance caracteristique: racine carree de l'aire de
    // l'intersection
    if (this.distanceMinimum == 0.0) {
      IGeometry inter = geom1.intersection(geom2);
      if (inter != null && inter.isValid()) {
        distBut = inter.area();
      } else {
        distBut = 0;
      }
    } else {
      distBut = geom1.buffer(this.distanceMinimum, 5)
          .intersection(geom2.buffer(this.distanceMinimum, 5)).area();
    }
    distBut = s.getLength() + Math.sqrt(distBut);

    // incite le segment a mesure distBut
    double a = 0.5 * alpha * (distBut / s.getLength() - 1);
    double dx = a * (s.getP1().getX() - s.getP2().getX());
    double dy = a * (s.getP1().getY() - s.getP2().getY());

    if (p == s.getP1()) {
      new PointAgentDisplacementAction(p, this, dx, dy);
    } else if (p == s.getP2()) {
      new PointAgentDisplacementAction(p, this, -dx, -dy);
    }
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
