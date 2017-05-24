/**
 * 
 */
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.GraphLinkableFeature;
import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * @author JGaffuri
 * 
 */
public class BlockTriangulation {
  private static Logger logger = Logger
      .getLogger(BlockTriangulation.class.getName());

  /**
   * effectue triangulation d'ilot (centres des batiments et projetes sur les
   * troncons de l'ilot)
   * 
   * @param distanceMax distance seuil en m (à mettre en mm plutôt non?) au dela
   *          de laquelle aucune relation de proximite n'est definie
   */
  public static Triangulation buildTriangulation(IUrbanBlock block,
      double distanceMax) {

    List<TriangulationPoint> triPoints = new ArrayList<TriangulationPoint>();

    // parcours des batiments de l'ilot
    TriangulationPoint apBatiment, apTroncon;
    IPoint ptBatiment, ptTroncon;
    for (IUrbanElement building : block.getUrbanElements()) {
      if (building.isDeleted()) {
        continue;
      }

      // recupere le centre du batiment
      ptBatiment = new GM_Point(building.getGeom().centroid());

      // cree agent point au centre
      apBatiment = new TriangulationPointImpl(ptBatiment.getPosition());
      IGraphLinkableFeature linkedBuilding = new GraphLinkableFeature(building);
      apBatiment.setGraphLinkableFeature(linkedBuilding);
      linkedBuilding.setReferentGraphNode(apBatiment);
      triPoints.add(apBatiment);

      // parcours des troncons
      for (INetworkSection section : block.getSurroundingNetwork()) {
        if (section.isDeleted()) {
          continue;
        }

        // recupere le point du troncon le plus proche du centre du batiment
        ptTroncon = new GM_Point(CommonAlgorithms
            .getPointsLesPlusProches(section.getGeom(), ptBatiment).get(0));
        if (BlockTriangulation.logger.isTraceEnabled()) {
          BlockTriangulation.logger.trace("proj = " + ptTroncon);
        }

        // si distance entre le batiment et le projete est trop grande, sortir
        if (ptTroncon.distance(building.getGeom()) > distanceMax) {
          continue;
        }

        // creer agent point au niveau du projete
        apTroncon = new TriangulationPointImpl(ptTroncon.getPosition());
        apTroncon.setGraphLinkableFeature(new GraphLinkableFeature(section));
        triPoints.add(apTroncon);
      }

    }

    // triangulation
    Triangulation tri = new Triangulation(triPoints,
        new TriangulationSegmentFactoryImpl(),
        new TriangulationTriangleFactoryImpl());

    tri.compute();

    // supprime les segments troncon-troncon (aucun impact sur les composants
    // reels du meso)
    // supprime les segments bati-troncon dont l'angle avec le troncon est trop
    // elevé (le point troncon est issu de la projection d'un batiment voisin)

    double tolerance = Math.PI / 12.0; // tolerance d'angle bati-troncon: 15° de
    // part et d'autre de l'angle droit
    ArrayList<TriangulationSegment> segmentsToRemove = new ArrayList<TriangulationSegment>();
    for (TriangulationSegment s : tri.getSegments()) {

      // segment bati-bati: on garde
      if (s.getInitialNode().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding
          && s.getFinalNode().getGraphLinkableFeature()
              .getFeature() instanceof IBuilding) {
        continue;
        // segment troncon-troncon: on supprime
      } else if (s.getInitialNode().getGraphLinkableFeature()
          .getFeature() instanceof INetworkSection
          && s.getFinalNode().getGraphLinkableFeature()
              .getFeature() instanceof INetworkSection) {
        segmentsToRemove.add(s);
        // segment bati-troncon: on traite0
      } else if (s.getInitialNode().getGraphLinkableFeature()
          .getFeature() instanceof INetworkSection
          || s.getFinalNode().getGraphLinkableFeature()
              .getFeature() instanceof INetworkSection) {
        IGeometry sectionGeom = null;
        ILineSegment segGeom = null;
        // la section est liée au point 1
        if (s.getInitialNode().getGraphLinkableFeature()
            .getFeature() instanceof INetworkSection) {
          sectionGeom = s.getInitialNode().getGraphLinkableFeature()
              .getFeature().getGeom();
          segGeom = new GM_LineSegment(
              s.getFinalNode().getGeom().coord().get(0),
              s.getInitialNode().getGeom().coord().get(0));
        }
        // la section est liée au point 2
        else {
          sectionGeom = s.getFinalNode().getGraphLinkableFeature().getFeature()
              .getGeom();
          segGeom = new GM_LineSegment(
              s.getInitialNode().getGeom().coord().get(0),
              s.getFinalNode().getGeom().coord().get(0));
        }
        IDirectPosition nearestOnSection = CommonAlgorithmsFromCartAGen
            .getNearestOtherVertexFromPoint(sectionGeom,
                segGeom.coord().get(1));
        if (nearestOnSection == null) {
          // le projeté est un vertex
          int vertexPosition = CommonAlgorithmsFromCartAGen
              .getNearestVertexPositionFromPoint(sectionGeom,
                  segGeom.coord().get(1));
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

    for (TriangulationSegment s : segmentsToRemove) {
      tri.getSegments().remove(s);
    }

    // calcule valeur des segments
    for (TriangulationSegment s : tri.getSegments()) {
      s.setWeight(
          s.getInitialNode().getGraphLinkableFeature().getSymbolGeom().distance(
              s.getFinalNode().getGraphLinkableFeature().getSymbolGeom()));
    }

    // supprime les segments trop longs
    segmentsToRemove = new ArrayList<TriangulationSegment>();
    for (TriangulationSegment s : tri.getSegments()) {
      if (s.getWeight() > distanceMax) {
        segmentsToRemove.add(s);
      }
    }
    for (TriangulationSegment s : segmentsToRemove) {
      tri.getSegments().remove(s);
    }

    // lien entre batiment et ses segments
    for (TriangulationSegment s : tri.getSegments()) {
      if (s.getInitialNode().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding) {
        s.getInitialNode().getGraphLinkableFeature().getProximitySegments()
            .add(s);
      }
      if (s.getFinalNode().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding) {
        s.getFinalNode().getGraphLinkableFeature().getProximitySegments()
            .add(s);
      }
    }

    return tri;

  }

}
