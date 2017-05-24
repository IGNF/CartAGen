/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.decomposers;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkableFeatureImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * 
 * Build a triangulation of a meso
 * 
 * @author JGaffuri
 * 
 */
public class MesoDecomposer {

  public static void buildTriangulation(
      MesoAgentGeneralisation<? extends GeographicObjectAgent> meso,
      double distanceMax) {

    // suppression de decomposition precedente
    meso.cleanDecomposition();

    // parcours des composants du meso
    IPointAgent ap, apTroncon;
    IPoint pt, ptTroncon;
    for (GeographicObjectAgent ag : meso.getComponents()) {
      if (ag.isDeleted()) {
        continue;
      }

      // recupere le centre du composant
      pt = new GM_Point(ag.getGeom().centroid());

      // cree agent point au centre
      ap = new PointAgentImpl(meso, pt.getPosition());

      // liens point/feature
      GAELLinkableFeatureImpl linked = new GAELLinkableFeatureImpl(
          ag.getFeature());
      ap.setLinkedFeature(linked);
      linked.setAgentPointReferant(ap);

      // parcours des troncons
      for (ISectionAgent at : meso.getSectionAgents()) {
        if (at == null)
          continue;
        if (at.isDeleted()) {
          continue;
        }

        // recupere le point du troncon le plus proche du centre du batiment
        ptTroncon = new GM_Point(
            CommonAlgorithms.getPointsLesPlusProches(at.getGeom(), pt).get(0));

        // si distance entre le batiment et le projete est trop grande, sortir
        if (ptTroncon.distance(ag.getGeom()) > distanceMax) {
          continue;
        }

        // cree agent point au niveau du projete
        apTroncon = new PointAgentImpl(meso, ptTroncon.getPosition());

        // liens point/feature
        GAELLinkableFeatureImpl linkedTroncon = new GAELLinkableFeatureImpl(
            at.getFeature());
        apTroncon.setLinkedFeature(linkedTroncon);
        linkedTroncon.setAgentPointReferant(apTroncon);

      }

    }

    // triangulation
    meso.triangule(false, null);

    // supprime les segments troncon-troncon (aucun impact sur les composants
    // reels du meso)
    // supprime les segments bati-troncon dont l'angle avec le troncon est trop
    // elevé (le point troncon est issu de la projection d'un batiment voisin)

    double tolerance = Math.PI / 12.0; // tolerance d'angle bati-troncon: 15° de
    // part et d'autre de l'angle droit
    ArrayList<GAELSegment> segmentsToRemove = new ArrayList<GAELSegment>();
    for (GAELSegment s : meso.getSegments()) {

      // segment bati-bati: on garde
      if (s.getP1().getLinkedFeature().getFeature() instanceof IBuilding
          && s.getP2().getLinkedFeature().getFeature() instanceof IBuilding) {
        continue;
        // segment troncon-troncon: on supprime
      } else if (s.getP1().getLinkedFeature()
          .getFeature() instanceof INetworkSection
          && s.getP2().getLinkedFeature()
              .getFeature() instanceof INetworkSection) {
        segmentsToRemove.add(s);
        // segment bati-troncon: on traite0
      } else if (s.getP1().getLinkedFeature()
          .getFeature() instanceof INetworkSection
          || s.getP2().getLinkedFeature()
              .getFeature() instanceof INetworkSection) {
        IGeometry sectionGeom = null;
        ILineSegment segGeom = null;
        // la section est liée au point 1
        if (s.getP1().getLinkedFeature()
            .getFeature() instanceof INetworkSection) {
          sectionGeom = s.getP1().getLinkedFeature().getFeature().getGeom();
          segGeom = new GM_LineSegment(s.getP2().getPosition(),
              s.getP1().getPosition());
        }
        // la section est liée au point 2
        else {
          sectionGeom = s.getP2().getLinkedFeature().getFeature().getGeom();
          segGeom = new GM_LineSegment(s.getP1().getPosition(),
              s.getP2().getPosition());
        }
        IDirectPosition nearestOnSection = CommonAlgorithmsFromCartAGen
            .getNearestOtherVertexFromPoint(sectionGeom,
                segGeom.coord().get(1));
        if (nearestOnSection == null) {
          // le projeté est un vertex
          int vertexPosition = CommonAlgorithmsFromCartAGen
              .getNearestVertexPositionFromPoint(sectionGeom,
                  segGeom.coord().get(1));
          // le projeté est une etxrémité de troncon
          if (vertexPosition == 0
              || vertexPosition == (segGeom.coord().size() - 1)) {
            break;
          }
          // le projeté n'est pas une extrémité de troncon
          Angle angle1 = Angle.angleTroisPoints(segGeom.coord().get(0),
              segGeom.coord().get(1),
              sectionGeom.coord().get(vertexPosition - 1));
          Angle angle2 = Angle.angleTroisPoints(segGeom.coord().get(0),
              segGeom.coord().get(1),
              sectionGeom.coord().get(vertexPosition + 1));
          if (Angle.ecart(angle1, Angle.angleDroit).getValeur() > tolerance
              && Angle
                  .ecart(angle1,
                      Angle.ajoute(Angle.anglePlat, Angle.angleDroit))
                  .getValeur() > tolerance
              && Angle.ecart(angle2, Angle.angleDroit).getValeur() > tolerance
              && Angle
                  .ecart(angle2,
                      Angle.ajoute(Angle.anglePlat, Angle.angleDroit))
                  .getValeur() > tolerance) {
            segmentsToRemove.add(s);
          }
        } else {
          // le projeté n'est pas un vertex
          Angle angle = Angle.angleTroisPoints(segGeom.coord().get(0),
              segGeom.coord().get(1), nearestOnSection);
          if (Angle.ecart(angle, Angle.angleDroit).getValeur() > tolerance
              && Angle
                  .ecart(angle, Angle.ajoute(Angle.anglePlat, Angle.angleDroit))
                  .getValeur() > tolerance) {
            segmentsToRemove.add(s);
          }
        }
      }

    }

    for (GAELSegment s : segmentsToRemove) {
      meso.getSegments().remove(s);
    }

    // calcule valeur des segments
    for (GAELSegment s : meso.getSegments()) {
      s.setValue(s.getP1().getLinkedFeature().getGeom()
          .distance(s.getP2().getLinkedFeature().getGeom()));
    }

    // supprime les segments trop longs
    segmentsToRemove = new ArrayList<GAELSegment>();
    for (GAELSegment s : meso.getSegments()) {
      if (s.getValue() > distanceMax) {
        segmentsToRemove.add(s);
      }
    }
    for (GAELSegment s : segmentsToRemove) {
      meso.getSegments().remove(s);
    }

    // lien entre chaque composant et ses segments
    for (GAELSegment s : meso.getSegments()) {
      s.getP1().getLinkedFeature().getSegmentsProximite().add(s);
      s.getP2().getLinkedFeature().getSegmentsProximite().add(s);
    }

  }
}
