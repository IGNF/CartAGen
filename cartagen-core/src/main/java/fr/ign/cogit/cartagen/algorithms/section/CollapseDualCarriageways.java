package fr.ign.cogit.cartagen.algorithms.section;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class CollapseDualCarriageways {
  @SuppressWarnings("unused")
  private static Logger logger = Logger
      .getLogger(CollapseDualCarriageways.class.getName());

  // Importance of roads that will be collapsed
  private final int importance;

  // Default constructor with tested parameters
  public CollapseDualCarriageways(int importance) {
    this.importance = importance;
  }

  // ////////////////////////////
  // SIMPLIFICATION OF THE MOTORWAYS
  // ////////////////////////////

  /**
   * Main simplification method
   */

  public void simplifyDualCarriageways(List<Face> separators) {
    HashMap<Face, IRoadLine> generalisedSeparators = this
        .collapseDualCarriageways(separators);
    this.reconnectSlipRoads(generalisedSeparators);
    NetworkEnrichment.enrichNetwork(
        CartAGenDoc.getInstance().getCurrentDataset(),
        CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork(), false,
        CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
            .getGeneObjImpl().getCreationFactory());
    separators.get(0).getCarteTopo().nettoyer();
    separators.clear();
    generalisedSeparators.clear();
  }

  /**
   * Main method that collapses motorway based on a triangulation of the
   * separators
   */

  public HashMap<Face, IRoadLine> collapseDualCarriageways(
      List<Face> separators) {

    ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
    HashSet<ILineSegment> hashSeg = new HashSet<ILineSegment>();
    HashSet<ILineSegment> hashSegFork = new HashSet<ILineSegment>();
    ArrayList<ILineSegment> segFace = new ArrayList<ILineSegment>();
    HashSet<IDirectPosition> pointsSeuls = new HashSet<IDirectPosition>();

    // loop on each separator
    for (Face face : separators) {
      ILineString contour = new GM_LineString(face.getGeom().coord());

      // the list contains all the points arround the face
      for (int i = 0; i < contour.numPoints(); i++) {
        TriangulationPointImpl point = new TriangulationPointImpl(
            contour.getControlPoint(i));
        points.add(point);
      }

      // the triangulation is generated
      Triangulation tri = new Triangulation(points,
          new TriangulationSegmentFactoryImpl(),
          new TriangulationTriangleFactoryImpl());
      tri.compute(true);
      Collection<TriangulationTriangle> triangles = tri.getTriangles();

      // Cas particulier des faces au bord du jeu de données(ce sont les seuls
      // "end triangles" à être analysés maintenant
      IDirectPosition pointBord = new DirectPosition();

      IFeatureCollection<IMask> masqueColl = CartAGenDoc.getInstance()
          .getCurrentDataset().getMasks();
      for (IMask masque : masqueColl) {
        if (contour.buffer(0.1).intersects(masque.getGeom())) {
          for (TriangulationTriangle leTriangle : triangles) {
            TriangulationPointImpl pt1 = new TriangulationPointImpl(
                leTriangle.getPoint1().getPosition());
            TriangulationPointImpl pt2 = new TriangulationPointImpl(
                leTriangle.getPoint2().getPosition());
            TriangulationPointImpl pt3 = new TriangulationPointImpl(
                leTriangle.getPoint3().getPosition());
            TriangulationSegmentImpl seg1 = new TriangulationSegmentImpl(pt1,
                pt2);
            TriangulationSegmentImpl seg2 = new TriangulationSegmentImpl(pt2,
                pt3);
            TriangulationSegmentImpl seg3 = new TriangulationSegmentImpl(pt3,
                pt1);

            if (masque.getGeom().buffer(0.1).intersection(seg1.getGeom())
                .equals(seg1.getGeom())) {
              pointBord = seg1.getGeom().centroid();
              ILineSegment segment = new GM_LineSegment(pointBord,
                  seg3.getGeom().centroid());
              if (contour.buffer(0.1).contains(seg2.getGeom())) {
                segFace.add(segment);
              } else {
                segment = new GM_LineSegment(pointBord,
                    seg2.getGeom().centroid());
                segFace.add(segment);
              }
            }
            if (masque.getGeom().buffer(0.1).intersection(seg2.getGeom())
                .equals(seg2.getGeom())) {
              pointBord = seg2.getGeom().centroid();
              ILineSegment segment = new GM_LineSegment(pointBord,
                  seg3.getGeom().centroid());
              if (contour.buffer(0.1).contains(seg1.getGeom())) {
                segFace.add(segment);
              } else {
                segment = new GM_LineSegment(pointBord,
                    seg1.getGeom().centroid());
                segFace.add(segment);
              }
            }
            if (masque.getGeom().buffer(0.1).intersection(seg3.getGeom())
                .equals(seg3.getGeom())) {
              pointBord = seg3.getGeom().centroid();
              ILineSegment segment = new GM_LineSegment(pointBord,
                  seg1.getGeom().centroid());
              if (contour.buffer(0.1).contains(seg2.getGeom())) {
                segFace.add(segment);
              } else {
                segment = new GM_LineSegment(pointBord,
                    seg2.getGeom().centroid());
                segFace.add(segment);
              }
            }
          }
        }
      }

      // Cas particulier où la triangulation de la face contient deux triangles
      if (triangles.size() == 2) {
        for (TriangulationSegment seg : tri.getSegments()) {
          if ((face.getGeom().contains(seg.getGeom())) && (contour.buffer(0.05)
              .intersection(seg.getGeom()).equals(seg.getGeom()) == false)) {
            pointsSeuls.add(seg.getGeom().centroid());
          }
        }
      }

      else {
        // There are three case of triangles
        for (TriangulationTriangle triangle : triangles) {

          if (face.getGeom().contains(triangle.getGeom())) {

            TriangulationPointImpl point1 = new TriangulationPointImpl(
                triangle.getPoint1().getPosition());
            TriangulationPointImpl point2 = new TriangulationPointImpl(
                triangle.getPoint2().getPosition());
            TriangulationPointImpl point3 = new TriangulationPointImpl(
                triangle.getPoint3().getPosition());
            TriangulationSegmentImpl seg1 = new TriangulationSegmentImpl(point1,
                point2);
            TriangulationSegmentImpl seg2 = new TriangulationSegmentImpl(point2,
                point3);
            TriangulationSegmentImpl seg3 = new TriangulationSegmentImpl(point3,
                point1);
            int nbSeg = 0, contourSeg1 = 0, contourSeg2 = 0, contourSeg3 = 0;

            if (contour.buffer(0.1).contains(seg1.getGeom()) == true) {
              contourSeg1 = 1;
            }
            if (contour.buffer(0.1).contains(seg2.getGeom()) == true) {
              contourSeg2 = 1;
            }
            if (contour.buffer(0.1).contains(seg3.getGeom()) == true) {
              contourSeg3 = 1;
            }

            nbSeg = contourSeg1 + contourSeg2 + contourSeg3;

            // The end triangles (the end line starts and finishes where these
            // triangles are => We don't make anything now

            // The most frequent triangles (1 side in common with the separator
            // outline)
            if (nbSeg == 1) {
              IDirectPosition pos1 = new DirectPosition();
              IDirectPosition pos2 = new DirectPosition();

              if (contourSeg1 == 1) {
                pos1 = seg2.getGeom().centroid();
                pos2 = seg3.getGeom().centroid();
                ILineSegment segment = new GM_LineSegment(pos1, pos2);
                segFace.add(segment);
              } else if (contourSeg2 == 1) {
                pos1 = seg1.getGeom().centroid();
                pos2 = seg3.getGeom().centroid();
                ILineSegment segment = new GM_LineSegment(pos1, pos2);
                segFace.add(segment);
              } else {
                pos1 = seg1.getGeom().centroid();
                pos2 = seg2.getGeom().centroid();
                ILineSegment segment = new GM_LineSegment(pos1, pos2);
                segFace.add(segment);
              }
            }

            // Third case : a fork is created (no side in common with the
            // separator outline)
            if (nbSeg == 0) {
              IDirectPosition pos1 = new DirectPosition();
              IDirectPosition pos2 = new DirectPosition();
              IDirectPosition pos3 = new DirectPosition();
              IDirectPosition barycentre = new DirectPosition();
              pos1 = seg1.getGeom().centroid();
              pos2 = seg2.getGeom().centroid();
              pos3 = seg3.getGeom().centroid();
              barycentre = triangle.getGeom().centroid();

              ILineSegment segSuivant1 = new GM_LineSegment(barycentre, pos1);
              ILineSegment segSuivant2 = new GM_LineSegment(barycentre, pos2);
              ILineSegment segSuivant3 = new GM_LineSegment(barycentre, pos3);
              hashSegFork.add(segSuivant1);
              hashSegFork.add(segSuivant2);
              hashSegFork.add(segSuivant3);
            }

          }
        }
      }

      // Removal of the useless segments created with the forks by filetering
      for (ILineSegment segmentFork : hashSegFork) {
        IDirectPosition debutFork = segmentFork.getStartPoint();
        IDirectPosition finFork = segmentFork.getEndPoint();
        boolean boolFork = false;

        for (ILineSegment segment : segFace) {
          IDirectPosition debut = segment.getStartPoint();
          IDirectPosition fin = segment.getEndPoint();
          if (debutFork.equals(debut) || debutFork.equals(fin)
              || finFork.equals(debut) || finFork.equals(fin)) {
            boolFork = true;
          }
        }

        if (boolFork == true) {
          hashSeg.add(segmentFork);
        }
      }

      hashSeg.addAll(segFace);
      segFace.clear();
      hashSegFork.clear();

      // On vide la hashSet pour la remplir avec les points de la prochaine face
      points.clear();

    }

    // All separators have now been treated
    // hashSeg contient tous les segments (sauf ceux en bord de face) et
    // pointsSeuls contient tous les points pour les faces composées de 2
    // triangles!!

    // On stocke dans une hashTable les séparateurs et la future route
    // généralisée associée
    HashMap<Face, IRoadLine> generalisedSeparators = new HashMap<Face, IRoadLine>();

    // creation of the segments at the edges of each face
    IDirectPosition centroid = new DirectPosition();
    IDirectPosition ptDep = new DirectPosition();
    IDirectPosition ptFin = new DirectPosition();
    IDirectPosition ptFinal = new DirectPosition();
    double distance = 1000.0;
    int comp;
    // pour chaque face, on récupère les segments inclus dans cette face
    // on utilise segFace pour stocker tous les segments

    for (Face face : separators) {
      comp = 0;

      for (ILineSegment segment : hashSeg) {
        if (face.getGeom().contains(segment) == true) {
          segFace.add(segment);
        }
      }

      // on vérifie l'intersection entre chaque face
      for (Face face2 : separators) {

        // si il y a une intersection, on récupère le centroide
        if (face.getGeom().buffer(0.1)
            .intersects(face2.getGeom().buffer(0.1)) == true
            && face.equals(face2) == false) {
          centroid = face.getGeom().buffer(0.1)
              .intersection(face2.getGeom().buffer(0.1)).centroid();

          // si la face ne contient aucun segment (triangulation composée de 2
          // triangles)
          if ((segFace.size() == 0 && comp == 0)
              || (segFace.size() == 1 && comp == 1)) {
            for (IDirectPosition pointSeul : pointsSeuls) {
              ILineSegment segFinal = new GM_LineSegment(pointSeul, centroid);
              if (face.getGeom().buffer(0.1).contains(segFinal) == true) {
                segFace.add(segFinal);
                comp++;
              }
            }
          } else {

            // on cherche le point d'extrémité d'un des segments de la face, le
            // plus proche
            for (ILineSegment seg : segFace) {
              ptDep = seg.getStartPoint();
              ptFin = seg.getEndPoint();
              if (centroid.distance(ptDep) < distance) {
                ptFinal = ptDep;
                distance = centroid.distance(ptDep);
              }
              if (centroid.distance(ptFin) < distance) {
                ptFinal = ptFin;
                distance = centroid.distance(ptFin);
              }
            }
            ILineSegment segFinal = new GM_LineSegment(ptFinal, centroid);
            segFace.add(segFinal);
            distance = 1000.0;
          }
        }

      }

      // union des segments pour créer un tronçon par face(segFace contient tous
      // les segments d'une face)
      if (segFace.isEmpty()) {
        continue;
      }
      ILineString ligneGene = new GM_LineString();
      DirectPositionList ordre = new DirectPositionList();

      // on initialise la liste avec les coordonnées du premier segment.
      ordre.addAll(segFace.get(0).coord());
      segFace.remove(segFace.get(0));

      // on crée la géométrie complète du tronçon de proche en proche
      int compteur = 0;
      while (segFace.isEmpty() == false) {
        for (ILineSegment seg : segFace) {
          if (ordre.get(0).equals(seg.startPoint())) {
            ordre.inverseOrdre();
            ordre.add(seg.endPoint());
            segFace.remove(seg);
            break;
          }
          if (ordre.get(0).equals(seg.endPoint())) {
            ordre.inverseOrdre();
            ordre.add(seg.startPoint());
            segFace.remove(seg);
            break;
          }
          if (ordre.get(ordre.size() - 1).equals(seg.startPoint())) {
            ordre.add(seg.endPoint());
            segFace.remove(seg);
            break;
          }
          if (ordre.get(ordre.size() - 1).equals(seg.endPoint())) {
            ordre.add(seg.startPoint());
            segFace.remove(seg);
            break;
          }
        }
        compteur++;
        if (compteur > 1000) {
          break;
        }
      }

      // on construit l'extrémité du tronçon
      for (int i = 0; i < ordre.size(); i++) {
        ligneGene.addControlPoint(ordre.get(i));
      }

      segFace.clear();

      // on marque les tronçons initiaux et les noeuds initiaux de chaque face
      // comme "supprimés"
      int localImportance = this.importance;
      IGeneObj toCopy = null;
      List<Arc> arcs = new ArrayList<Arc>();
      arcs.addAll(face.getArcsDirects());
      arcs.addAll(face.getArcsIndirects());
      for (Arc arc : arcs) {
        IFeature obj = arc.getCorrespondant(0);
        if (obj instanceof IRoadLine) {
          IRoadLine sect = (IRoadLine) obj;
          if (toCopy == null)
            toCopy = sect;
          if (sect.getGeom().intersects(ligneGene.buffer(0.1)) == false) {
            localImportance = sect.getImportance();
            sect.eliminate();
            sect.getInitialNode().eliminate();
            sect.getFinalNode().eliminate();
          }
        }
      }

      // On construit le tronçon central
      IRoadLine tr = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getGeneObjImpl().getCreationFactory()
          .createRoadLine(new TronconDeRouteImpl((Reseau) CartAGenDoc
              .getInstance().getCurrentDataset().getRoadNetwork().getGeoxObj(),
              false, ligneGene), localImportance);
      tr.setBeenCreated(true);
      tr.copyAttributes(toCopy);
      CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()
          .addSection(tr);
      CartAGenDoc.getInstance().getCurrentDataset().getRoads().add(tr);
      generalisedSeparators.put(face, tr);

    }

    return generalisedSeparators;

  }

  /**
   * Method that ensures the reconnection of slip roads on the new simplified
   * motorway
   */

  public void reconnectSlipRoads(
      HashMap<Face, IRoadLine> generalisedSeparators) {

    // 1) DETECTION OF SLIP ROADS

    HashMap<INetworkNode, IRoadLine> slipRoads = new HashMap<INetworkNode, IRoadLine>();

    for (IRoadNode node : CartAGenDoc.getInstance().getCurrentDataset()
        .getRoadNodes()) {

      // If the node is on the limit of the dataset => excluded
      if (!CartAGenDoc.getInstance().getCurrentDataset().getMasks().isEmpty()) {
        IMask mask = CartAGenDoc.getInstance().getCurrentDataset().getMasks()
            .iterator().next();
        if (node.getGeom().buffer(1.0).intersects(mask.getGeom())) {
          continue;
        }
      }

      // If the degreee of the node is different than 1, it is not a dead end =>
      // excluded
      int nodeDegree = 0;
      IRoadLine road = null;
      for (INetworkSection section : node.getInSections()) {
        if (!(section.isDeleted())) {
          nodeDegree++;
          road = (IRoadLine) section;
        }
      }
      for (INetworkSection section : node.getOutSections()) {
        if (!(section.isDeleted())) {
          nodeDegree++;
          road = (IRoadLine) section;
        }
      }
      if (nodeDegree != 1) {
        continue;
      }

      // If the related road doesn't exist or is a pathway => excluded
      if (road == null || road.getImportance() == 0) {
        continue;
      }

      // If the node doesn't intersect a separator, it is not the end of a slip
      // road => excluded
      boolean isSlipRoad = false;
      for (Face face : generalisedSeparators.keySet()) {
        if (node.getGeom().buffer(1.0).intersects(face.getGeom())) {
          isSlipRoad = true;
        }
      }
      if (!isSlipRoad) {
        continue;
      }

      // Otherwise the node and its related road section are the components of a
      // slip road
      slipRoads.put(node, road);

    }

    // 2) RECONNECTION OF SLIP ROADS

    HashMap<INetworkNode, IRoadLine> nonReconnectedNodes = new HashMap<INetworkNode, IRoadLine>();

    // the separator that intersects the slip roads
    for (INetworkNode node : slipRoads.keySet()) {

      boolean order = false;
      boolean boolLinkPoint1 = false;

      ILineString section = new GM_LineString();
      IRoadLine road = null;
      boolean isNodeInsideFace = false;
      for (Face sep : generalisedSeparators.keySet()) {
        // the central section created in place of the separator (sep)
        if (node.getGeom().buffer(1.0).intersects(sep.getGeom())) {
          section = generalisedSeparators.get(sep).getGeom();
          road = slipRoads.get(node);
          // the node is in the central part of the face (not a slip road but a
          // prolongation)
          if (node.getGeom().intersects(sep.getGeom().buffer((-2.0)))) {
            isNodeInsideFace = true;
          }
        }
      }

      if (road == null) {
        continue;
      }

      // node in the central part of a separator => not a slip road
      if (isNodeInsideFace) {
        nonReconnectedNodes.put(node, road);
        break;
      }

      // Projection of the node on the section => if too far away, no
      // reconnection
      IDirectPosition project = CommonAlgorithms.getNearestPoint(section,
          node.getGeom());
      if (node.getGeom().distance(project.toGM_Point()) > 200.0) {
        nonReconnectedNodes.put(node, road);
        break;
      }

      // a circle is created around the point project, two points are created
      ILineString buffer = new GM_LineString(
          project.toGM_Point().buffer(20.0).coord());
      IGeometry intersections = buffer.intersection(section);

      // No intersection => nothing to do
      if (intersections.coord().size() == 0) {
        nonReconnectedNodes.put(node, road);
      }

      // 1 intersection => end of the road, prolongation needed
      else if (intersections.coord().size() == 1) {
        if (node.getInSections().contains(road)) {
          road.getGeom().addControlPoint(project);
        } else if (node.getOutSections().contains(road)) {
          road.getGeom().addControlPoint(0, project);
        }
      }

      // 2 intersections => slip road, reconnection needed
      else {

        IDirectPosition linkPoint1 = intersections.coord().get(0);
        IDirectPosition linkPoint2 = intersections.coord().get(1);

        // the second point of the road
        IDirectPosition theNode = new DirectPosition();
        IDirectPosition sectionPoint = new DirectPosition();
        if (road.getGeom().getControlPoint(0).getX() == node.getGeom().coord()
            .get(0).getX()
            && road.getGeom().getControlPoint(0).getY() == node.getGeom()
                .coord().get(0).getY()) {
          theNode = road.getGeom().getControlPoint(0);
          sectionPoint = road.getGeom().getControlPoint(1);
        } else {
          theNode = road.getGeom()
              .getControlPoint(road.getGeom().sizeControlPoint() - 1);
          sectionPoint = road.getGeom()
              .getControlPoint(road.getGeom().sizeControlPoint() - 2);
          order = true;
        }

        Angle alpha1 = Angle.angleTroisPoints(sectionPoint, theNode,
            linkPoint1);
        Angle alpha2 = new Angle(0.0);
        if (linkPoint2 != null) {
          alpha2 = Angle.angleTroisPoints(sectionPoint, theNode, linkPoint2);
        }

        if (linkPoint2 == null) {
          boolLinkPoint1 = true;
        } else {
          if (Math.abs(alpha1.getValeur()) < Math.abs(alpha2.getValeur())) {
            if (Math.abs(alpha1.getValeur()) > Math.PI
                && Math.abs(alpha2.getValeur()) > Math.PI) {
              boolLinkPoint1 = true;
            }
          } else {
            if (Math.abs(alpha1.getValeur()) < Math.PI
                || Math.abs(alpha2.getValeur()) < Math.PI) {
              boolLinkPoint1 = true;
            }
          }
        }

        if (order == true) {
          if (boolLinkPoint1 == true) {
            road.getGeom().addControlPoint(linkPoint1);
          } else {
            road.getGeom().addControlPoint(linkPoint2);
          }
        } else {
          if (boolLinkPoint1 == true) {
            road.getGeom().addControlPoint(0, linkPoint1);
          } else {
            road.getGeom().addControlPoint(0, linkPoint2);
          }
        }

      }

    }

    // 3) RECONNECTION OF REMAINING LONELY NODES

    while (nonReconnectedNodes.size() > 1) {

      Iterator<INetworkNode> iterator = nonReconnectedNodes.keySet().iterator();
      INetworkNode node1 = iterator.next();
      IRoadLine road = nonReconnectedNodes.get(node1);

      // Searching for the nearest remaining lonely node
      IDirectPosition node2Position = null;
      double distanceMin = Double.MAX_VALUE;
      while (iterator.hasNext()) {
        INetworkNode node = iterator.next();
        double distance = node.getGeom().distance(node1.getGeom());
        if (distance < distanceMin) {
          distanceMin = distance;
          node2Position = node.getPosition();
        }
      }

      // No lonely node in proximity => remains lonely
      if (node2Position == null || distanceMin > 50.0) {
        nonReconnectedNodes.remove(node1);
        continue;
      }

      // Reconnection of the two nodes
      if (road.getGeom().coord().get(0).equals(node1.getGeom())) {
        road.getGeom().addControlPoint(0, node2Position);
      } else {
        road.getGeom().addControlPoint(node2Position);
      }

      // Removal of the two nodes from the hashmap - they are now reconnected
      nonReconnectedNodes.remove(node1);
      // if (node2 != null) {
      // nonReconnectedNodes.remove(node2);
      // }

    }

  }
}
