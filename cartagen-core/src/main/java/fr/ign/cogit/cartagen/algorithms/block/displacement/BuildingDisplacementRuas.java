package fr.ign.cogit.cartagen.algorithms.block.displacement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockTriangulation;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.ExhaustDirections;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class BuildingDisplacementRuas {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger
      .getLogger(BuildingDisplacementRuas.class.getName());

  private static final double distanceMax = 2
      * GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE;
  private static final int nbSegments = 12;
  private static final double translationVectorOffset = 1.5;
  private static final double epsilonRate = 0.002;
  private static final double epsilonOrtho = 0.5;
  private static final double circleRadius = 30.0;// meters
  private static final double maxAngleFreeSpace = Math.PI / 2.0;
  private static final double maxPropagation = 0.25;// map mm

  // Public fields //
  public static final boolean DEBUG = false;
  public static final int DEBUG_ID = -1;
  static HashSet<Vector2D> contrsAff;
  public static HashMap<INode, Vector2D> vecteursAff;
  static ArrayList<Segment> triangAff;
  public static IDirectPosition centre;
  public static Vector2D deplfinal, deplFirst;

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  public static void compute(IUrbanBlock block, double maxDisp, int maxIter) {

    if (BuildingDisplacementRuas.DEBUG) {
      BuildingDisplacementRuas.contrsAff = new HashSet<Vector2D>();
      BuildingDisplacementRuas.vecteursAff = new HashMap<INode, Vector2D>();
      BuildingDisplacementRuas.triangAff = new ArrayList<Segment>();
    }

    // ***************************************
    // STEP 1 : PRELIMINARY MICRO DISPLACEMENT
    // ***************************************
    BuildingDisplacementRuas.logger
        .fine("step 1 : PRELIMINARY MICRO DISPLACEMENT");
    // a preliminary operation is carried to improve displacement:
    // the buildings that overlap the meso limits are slightly displaced
    // inside the meso using the reduced geometry method from CartACom
    for (IUrbanElement build : block.getUrbanElements()) {

      // test if the building overlaps a road
      if (!BlockBuildingsMeasures.isBuildingOverlappingBlock(build, block)) {
        continue;
      }

      // here, the building is overlapping at least one road
      // As in CartACom, a reduced constraint area is computed to move the
      // building inside the meso, minimising the movement.
      // Compute first the convex hull of the building
      IGeometry hull = build.getSymbolGeom().convexHull();

      // computes the centroid of the building hull
      IDirectPosition centroid = hull.centroid();
      // build a circle centred on the centroid
      IPolygon circle = GeometryFactory.buildCircle(centroid,
          BuildingDisplacementRuas.circleRadius,
          BuildingDisplacementRuas.nbSegments);

      // the displacement area is the intersection of the circle and the meso
      IGeometry displArea = circle.intersection(block.getGeom());
      if (displArea instanceof IMultiSurface<?>) {
        double max = 0.0;
        for (IOrientableSurface surf : ((IMultiSurface<?>) displArea)
            .getList()) {
          if (surf.area() > max) {
            max = surf.area();
            displArea = surf;
          }
        }
      }

      // get all the roads inside the displArea
      List<INetworkSection> roads = new ArrayList<INetworkSection>();
      IFeatureCollection<INetworkSection> troncons = new FT_FeatureCollection<INetworkSection>();
      for (INetworkSection as : block.getSurroundingNetwork()) {
        troncons.add(as);
      }
      Collection<INetworkSection> tronconsArea = troncons.select(displArea);
      for (INetworkSection obj : tronconsArea) {
        roads.add(obj);
      }

      // initialises the reduced constraint area geometry
      IGeometry reducedConstrArea = null;
      // loop on the roads to build the reduced constrained area
      for (INetworkSection road : roads) {
        // get the segments from the road geometry
        List<Segment> segments = Segment.getSegmentList(road.getGeom());
        for (Segment segt : segments) {
          // determines the nearest hull point of segt
          IDirectPosition nearestPt = CommonAlgorithms.getNearestPoint(hull,
              segt);

          // now builds the translation vector
          Vector2D transVector = new Vector2D(nearestPt, centroid);

          // adds translationVectorOffset m to vector norm
          double norm = transVector.norme();
          transVector.scalarMultiplication(
              (norm + BuildingDisplacementRuas.translationVectorOffset) / norm);

          // Now build the parallelogram from the translated segment
          IPolygon parall = GeometryFactory.buildParallelogram(segt,
              transVector);

          // on l'agrège à la géométrie de la zone réduite
          if (reducedConstrArea == null) {
            reducedConstrArea = parall;
          } else {
            reducedConstrArea = reducedConstrArea.union(parall);
          }

        } // for(Segment segt:segments)
      } // for(ISectionAgent road:roads)
        // now intersects the displArea and the reducedConstrArea
      IGeometry inter = displArea.difference(reducedConstrArea);

      // if the intersection is null or empty, deletes the building
      if (inter == null) {
        build.eliminate();
        continue;
      }
      if (inter.isEmpty()) {
        build.eliminate();
        continue;
      }

      // now gets the inter point the nearest to centre
      IDirectPosition newCentre = inter.centroid();
      // now translates the building geometry to the new centre
      double dx = newCentre.getX() - centroid.getX();
      double dy = newCentre.getY() - centroid.getY();
      BuildingDisplacementRuas.logger
          .finer(" a building is displaced to (" + dx + ", " + dy + ")");
      build.displaceAndRegister(dx, dy);
    }

    // ***************************************
    // STEP 2 : PROXIMITY COMPUTING
    // ***************************************
    BuildingDisplacementRuas.logger.fine("step 2 : PROXIMITY COMPUTING");
    Triangulation tri = BlockTriangulation.buildTriangulation(block,
        BuildingDisplacementRuas.distanceMax);

    // ***************************************
    // STEP 3 : ROAD VECTORS COMPUTING
    // ***************************************
    BuildingDisplacementRuas.logger.fine("step 3 : ROAD VECTORS COMPUTING");
    HashMap<INode, Vector2D> displacementVectors = new HashMap<INode, Vector2D>();

    // Loop on the triangulation segments
    for (TriangulationSegment s : tri.getSegments()) {
      // first filter the long segments
      if (s.getWeight() > BuildingDisplacementRuas.distanceMax) {
        continue;
      }
      if (!displacementVectors.containsKey(s.getInitialNode())
          && s.getInitialNode().getGraphLinkableFeature()
              .getFeature() instanceof IRoadLine) {
        IRoadLine road = (IRoadLine) s.getInitialNode()
            .getGraphLinkableFeature().getFeature();
        IDirectPosition point = s.getInitialNode().getPosition();
        displacementVectors.put(s.getInitialNode(),
            BuildingDisplacementRuas.buildRoadDisplVector(block, road, point));
      }
      if (!displacementVectors.containsKey(s.getFinalNode()) && s.getFinalNode()
          .getGraphLinkableFeature().getFeature() instanceof IRoadLine) {
        IRoadLine road = (IRoadLine) s.getFinalNode().getGraphLinkableFeature()
            .getFeature();
        IDirectPosition point = s.getFinalNode().getPosition();
        displacementVectors.put(s.getFinalNode(),
            BuildingDisplacementRuas.buildRoadDisplVector(block, road, point));
      }
    }

    for (IUrbanElement build : block.getUrbanElements()) {
      if (build.getProximitySegments() == null) {
        continue;
      }

    }

    // ***************************************
    // STEP 4 : BUILDING DISPLACEMENT
    // ***************************************
    BuildingDisplacementRuas.logger.fine("step 4 : BUILDING DISPLACEMENT");
    int i = 0;
    double overlapRate = BlockBuildingsMeasures
        .getBuildingsOverlappingRateMean(block);
    int nbBuild = BlockBuildingsMeasures
        .getBlockNonDeletedBuildingsNumber(block);
    HashSet<IUrbanElement> buildToDispl = new HashSet<IUrbanElement>();
    for (IUrbanElement build : block.getUrbanElements()) {
      // if the building has been deleted, continue
      if (build.isDeleted()) {
        continue;
      }
      buildToDispl.add(build);
    }
    // Loop while the number of iteration is under the maximum and the mean
    // of overlappingRates in the meso is not negligible
    while (i < maxIter * nbBuild
        && overlapRate > BuildingDisplacementRuas.epsilonRate) {
      if (buildToDispl.size() == 0) {
        for (IUrbanElement build : block.getUrbanElements()) {
          // if the building has been deleted, continue
          if (build.isDeleted()) {
            continue;
          }
          buildToDispl.add(build);
        }
      }
      // ***************************************
      // STEP 4.1 : BEST CANDIDATE FOR DISPLACEMENT
      // ***************************************
      BuildingDisplacementRuas.logger
          .fine("step 4.1 : BEST CANDIDATE FOR DISPLACEMENT");
      IUrbanElement bestCandidate = BuildingDisplacementRuas
          .getBestCandidate(displacementVectors, buildToDispl, block);
      buildToDispl.remove(bestCandidate);
      BuildingDisplacementRuas.logger
          .finer("step 4.1 : BEST CANDIDATE : " + bestCandidate);
      if (BuildingDisplacementRuas.DEBUG
          && (bestCandidate.getId() == BuildingDisplacementRuas.DEBUG_ID)
          && (i < nbBuild)) {
        BuildingDisplacementRuas.centre = bestCandidate.getSymbolGeom()
            .centroid();
      }
      // ***************************************
      // STEP 4.2 : COMPUTE DISPLACEMENT VECTORS
      // ***************************************
      BuildingDisplacementRuas.logger
          .fine("step 4.2 : COMPUTE DISPLACEMENT VECTORS");
      ArrayList<Vector2D> contribs = BuildingDisplacementRuas
          .computeDisplacementContributions(displacementVectors, bestCandidate,
              block);
      if (contribs.size() == 0) {
        i++;
        continue;
      }
      // ***************************************
      // STEP 4.2bis : COMPUTE FREE SPACE VECTORS
      // ***************************************
      BuildingDisplacementRuas.logger
          .fine("step 4.2bis : COMPUTE FREE SPACE VECTORS");
      BuildingDisplacementRuas.addFreeSpaceAttraction(bestCandidate, block,
          contribs);
      if (BuildingDisplacementRuas.DEBUG
          && (bestCandidate.getId() == BuildingDisplacementRuas.DEBUG_ID)) {
        for (Vector2D vect : contribs) {
          BuildingDisplacementRuas.contrsAff.add(vect);
        }
      }
      // ***************************************
      // STEP 4.3 : AGGREGATE VECTORS
      // ***************************************
      BuildingDisplacementRuas.logger.fine("step 4.3 : AGGREGATE VECTORS");
      Vector2D displVector = BuildingDisplacementRuas
          .aggregateDisplContributions(contribs, bestCandidate);
      BuildingDisplacementRuas.logger
          .finest("step 4.3 : AGGREGATED VECTOR : " + displVector);
      // ***************************************
      // STEP 4.4 : MICRO DISPLACEMENT TO ADJUST POSITION
      // ***************************************
      BuildingDisplacementRuas.logger
          .fine("step 4.4 : MICRO DISPLACEMENT TO ADJUST POSITION");
      // compute the exhaust directions of the building
      HashSet<Vector2D> displVectors = BuildingDisplacementRuas
          .getDisplVectorsOnBuilding(bestCandidate, block, displacementVectors);

      ExhaustDirections ed = new ExhaustDirections(displVectors, displVector);
      BuildingDisplacementRuas.logger
          .fine("initial displacement vector : " + displVector);
      Vector2D displVectorFinal = ed.getMicroDispl();
      if (BuildingDisplacementRuas.DEBUG
          && (bestCandidate.getId() == BuildingDisplacementRuas.DEBUG_ID)) {
        BuildingDisplacementRuas.deplFirst = displVector;
      }
      BuildingDisplacementRuas.logger
          .fine("final displacement vector : " + displVectorFinal);
      BuildingDisplacementRuas.logger
          .fine("final displacement vector norm: " + displVectorFinal.norme());
      // ***************************************
      // STEP 4.5 : PERFORM DISPLACEMENT
      // ***************************************
      BuildingDisplacementRuas.logger.fine("step 4.5 : PERFORM DISPLACEMENT");
      // test if displacement is less than maximum displacement
      if (displVectorFinal.norme() > maxDisp) {
        displVectorFinal
            .scalarMultiplication(maxDisp / displVectorFinal.norme());
      }
      if (BuildingDisplacementRuas.DEBUG
          && (bestCandidate.getId() == BuildingDisplacementRuas.DEBUG_ID)) {
        BuildingDisplacementRuas.deplfinal = displVectorFinal;
      }
      // apply displacement
      bestCandidate.displaceAndRegister(displVectorFinal.getX(),
          displVectorFinal.getY());
      // update the displacement map
      INode ap = null;
      for (IEdge s : tri.getSegments()) {
        // if the segment does not concern build, continue
        if (!bestCandidate.getFeature()
            .equals(s.getInitialNode().getGraphLinkableFeature().getFeature())
            && !bestCandidate.getFeature().equals(
                s.getFinalNode().getGraphLinkableFeature().getFeature())) {
          continue;
        }
        ap = s.getInitialNode();
        if (!ap.getGraphLinkableFeature().getFeature()
            .equals(bestCandidate.getFeature())) {
          ap = s.getFinalNode();
        }
      }
      displacementVectors.put(ap, displVectorFinal);
      i++;
      overlapRate = BlockBuildingsMeasures
          .getBuildingsOverlappingRateMean(block);
    }

    // ***************************************
    // STEP 5 : DELETION/RE-CENTER
    // ***************************************
    BuildingDisplacementRuas.logger.fine("step 5 : DELETION/RE-CENTER");
    // Loop on the buildings
    for (IUrbanElement build : block.getUrbanElements()) {
      BuildingDisplacementRuas.deleteRecenterBuilding(build, block);
    }
  }

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  /**
   * Step 3 of the displacement algorithm. Builds the displacement vector of a
   * road triangulation node, perpendicular to the road and with the symbol
   * width as norm.
   * 
   * @param ai the block agent the displacement is computed in
   * @param road the road agent that contribute to this displacement vector
   * @param point the vertex of the road included in triangulation that will
   *          carry the displacement vector
   * @return the displacement vector of the road at this point
   */
  private static Vector2D buildRoadDisplVector(IUrbanBlock ai,
      INetworkSection road, IDirectPosition point) {
    IDirectPosition inside = JTSAlgorithms.getInteriorPoint(ai.getGeom());
    // first builds the vector parallel to road
    IDirectPosition nearV = CommonAlgorithmsFromCartAGen
        .getNearestOtherVertexFromPoint(road.getGeom(), point);
    Vector2D roadVect = new Vector2D(point, nearV);
    // builds a new translation vector
    // first get the road symbol width
    double sW = road.getWidth() / 2.0;
    Vector2D displVect = new Vector2D(point, inside);
    // normalise the vector with symbol width
    displVect.normalise();
    displVect.scalarMultiplication(sW);
    // computes the angle between the two vectors
    double angle = roadVect.vectorAngle(displVect);

    // rotate the vector to make it a 90° angle with roadVector
    DirectPositionList points = new DirectPositionList();
    points.add(point);
    points.add(displVect.translate(point));
    ILineString lsG = new GM_LineString(points);
    LineString ls = null;
    try {
      ls = (LineString) JtsGeOxygene.makeJtsGeom(lsG, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    double angleRot = Math.PI / 2.0 - Math.abs(angle);
    if (angle < 0.0) {
      angleRot = -angleRot;
    } else if (angleRot < 0.0 && displVect.direction().getValeur() > Math.PI) {
      angleRot = Math.PI / 2 + angleRot;
    }
    ls = CommonAlgorithms.rotation(ls,
        new Coordinate(point.getX(), point.getY()), angleRot);
    // on teste si le point d'arriver est bien dans le meso
    // sinon, on refait une rotation de Pi
    DirectPosition test = new DirectPosition(ls.getEndPoint().getX(),
        ls.getEndPoint().getY());
    if (!ai.getGeom().contains(new GM_Point(test))) {
      ls = CommonAlgorithms.rotation(ls,
          new Coordinate(point.getX(), point.getY()), Math.PI);
    }
    // on reconstruit notre nouveau vecteur de déplacement
    displVect = new Vector2D(point,
        new DirectPosition(ls.getEndPoint().getX(), ls.getEndPoint().getY()));

    return displVect;
  }

  /**
   * Step 4.1 of the displacement algorithm. Among the buildings of a block, the
   * method chooses the best candidate for a displacement, i.e. the one that has
   * not been displaced yet and that has the most severe proximity conflict.
   * 
   * @param displVectors the vectors of previously computed displacements
   * @param buildToDispl the buildings that have not been displaced yet
   * @param ai the block in which the displacement is carried out
   * @return the best building agent candidate for a displacement
   * @author GTouya
   */
  private static IUrbanElement getBestCandidate(
      HashMap<INode, Vector2D> displVectors,
      HashSet<IUrbanElement> buildToDispl, IUrbanBlock ai) {
    IUrbanElement best = null;
    double conflictMinCost = Double.MAX_VALUE;
    // loop on the buildings candidate to displacement
    for (IUrbanElement build : buildToDispl) {
      // look for the conflict with the fixed objects (i.e. the roads and
      // the already displaced buildings) connected in triangulation
      for (IEdge s : build.getProximitySegments()) {
        INode ap = s.getInitialNode();
        if (ap.getGraphLinkableFeature().getFeature().equals(build)) {
          ap = s.getFinalNode();
        }
        // if ap linked feature doesn't have a vector, continue
        if (displVectors.get(ap) == null) {
          continue;
        }
        // computes the conflict cost for this proximity
        double cost = BuildingDisplacementRuas.computeConflictCost(displVectors,
            build, ap);
        if (cost < conflictMinCost) {
          best = build;
          conflictMinCost = cost;
        }
      } // for(GAELSegment s : ai.getSegments())
    } // for(IBuildingAgent build:buildToDispl)
    BuildingDisplacementRuas.logger
        .fine("Min cost for Best Candidate: " + conflictMinCost);
    // test if a best building was chosen (if there is no conflict with roads
    // within the block, no best candidate might be chosen!)
    if (best == null) {
      return buildToDispl.iterator().next();
    }
    return best;
  }

  /**
   * Compute the conflict cost of the building with the point agent of the
   * triangulation. The cost decreases when the building is nearer to the
   * triangulation point and also decreases when the displacement vector carried
   * by the point has the same direction as the smallest distance between the
   * building and the point (cosinus used).
   * 
   * @param displVectors the map of displacement vectors carried by points
   * @param build the building on which conflict cost is computed
   * @param ap the point agent related to this conflict cost
   * @return a cost that can be negative when conflict is high
   * @author GTouya
   */
  private static double computeConflictCost(
      HashMap<INode, Vector2D> displVectors, IUrbanElement build, INode ap) {
    double min = Double.MAX_VALUE;
    double norm = displVectors.get(ap).norme();
    for (IDirectPosition a : ap.getGraphLinkableFeature().getSymbolGeom()
        .coord()) {
      for (IDirectPosition b : build.getGeom().coord()) {
        double dist = a.distance2D(b);
        Vector2D ab = new Vector2D(a, b);
        double cos = Math
            .cos(displVectors.get(ap).angleVecteur(ab).getValeur());
        double cost = dist - norm * cos;
        if (cost < min) {
          min = cost;
        }
      }
    }
    return min;
  }

  /**
   * Step 4.2 of the displacement algorithm. The method computes the
   * contributions of each displaced neighbour on the displacement of a
   * building. The method (and the notation) is the extracted from (Ruas, 1999,
   * pp.181-182).
   * 
   * @param displVectors the map of displacement vectors carried by points
   * @param build the building on which contributions are computed
   * @param ai the block agent in which displacement is computed
   * @return a list of vector contributions.
   * @author GTouya
   */
  private static ArrayList<Vector2D> computeDisplacementContributions(
      HashMap<INode, Vector2D> displVectors, IUrbanElement build,
      IUrbanBlock ai) {
    ArrayList<Vector2D> contributions = new ArrayList<Vector2D>();
    HashMap<INode, Vector2D> displMap = BuildingDisplacementRuas
        .getDisplVectorsAndPtOnBuilding(build, ai, displVectors);
    BuildingDisplacementRuas.logger.finest(displMap.size() + " noeuds");
    for (INode ap : displMap.keySet()) {
      Vector2D v = displMap.get(ap);

      // compute the contribution of the object linked to this point
      // first get the points o1 & o2 of each geometry that are the closest
      IGeometry geom2 = build.getSymbolGeom();
      IGeometry geom1 = ap.getGraphLinkableFeature().getSymbolGeom();
      IDirectPositionList ppp = CommonAlgorithms.getPointsLesPlusProches(geom1,
          geom2);
      IDirectPosition o1 = ppp.get(0);
      IDirectPosition o2 = ppp.get(1);

      // compute the separation threshold
      double lambda0 = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      if (ap.getGraphLinkableFeature().getFeature() instanceof IRoadLine) {
        lambda0 = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE
            * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
        lambda0 += ((IRoadLine) ap.getGraphLinkableFeature().getFeature())
            .getWidth() / 2.0;
      }
      double lambdaM = BuildingDisplacementRuas.maxPropagation
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0 + lambda0;
      // project the displacement on the straight line (o1o2)
      Vector2D vo1o2 = new Vector2D(o1, o2);
      double proj = v.norme()
          * Math.cos(v.direction().getValeur() - vo1o2.direction().getValeur());

      // case 4: not foreseen in (Ruas, 1999), overlapping buildings
      if ((geom1 instanceof IPolygon) && (geom2 instanceof IPolygon)
          && vo1o2.equals(new Vector2D(0.0, 0.0))) {
        BuildingDisplacementRuas.logger.fine("case 4");
        double length = CommonAlgorithmsFromCartAGen
            .getOverlappingLength((IPolygon) geom1, (IPolygon) geom2);
        // compute the vector between centroids
        Vector2D vect = new Vector2D(geom1.centroid(), geom2.centroid());
        double dispNorm = vect.norme();
        double dx = (lambda0 + length) * vect.getX() / dispNorm;
        double dy = (lambda0 + length) * vect.getY() / dispNorm;
        Vector2D contrib = new Vector2D(dx, dy);
        contributions.add(contrib);
        continue;
      }
      // case 5: not foreseen in (Ruas, 1999), road overlapping a building
      else if (vo1o2.equals(new Vector2D(0.0, 0.0))) {
        BuildingDisplacementRuas.logger.fine("case 5");
        Vector2D vect = new Vector2D(o1, geom2.centroid());
        double dispNorm = vect.norme();
        double dx = (lambda0 + dispNorm) * vect.getX() / dispNorm;
        double dy = (lambda0 + dispNorm) * vect.getY() / dispNorm;
        Vector2D contrib = new Vector2D(dx, dy);
        contributions.add(contrib);
        continue;
      }
      // case 2 of (Ruas, 1999, pp.182), no displacement
      if (vo1o2.norme() > lambdaM - proj) {
        BuildingDisplacementRuas.logger.fine("case 2");
        continue;
      }

      // case 1 of (Ruas, 1999, pp.182), simple displacement
      if (vo1o2.norme() < lambda0) {
        BuildingDisplacementRuas.logger.fine("case 1");
        Vector2D v1 = vo1o2.copy();
        v1.scalarMultiplication(lambda0 / vo1o2.norme() - 1.0);
        contributions.add(v1);
        continue;
      }

      // case 3 of (Ruas, 1999, pp.182), complex case
      // here the displacement is colinear to v but depends on
      // the situation : it can be a repulsion, an attraction or a following
      // following case :
      if (Math.abs(proj) < BuildingDisplacementRuas.epsilonOrtho) {
        BuildingDisplacementRuas.logger.fine("case 3 following");
        Vector2D contrib = vo1o2.copy();
        double constant = (lambdaM - vo1o2.norme()) / (lambdaM - lambda0);
        contrib.scalarMultiplication(constant);
        contributions.add(contrib);
        continue;
      }

      Vector2D contrib = vo1o2.copy();
      double radius = Math.max(lambda0, lambda0 + (vo1o2.norme() - lambda0)
          * (lambdaM - lambda0) / (lambdaM - lambda0 - proj));

      // repulsion case pr attraction case, depending on proj sign
      if (proj > 0.0) {
        BuildingDisplacementRuas.logger.fine("case 3 repulsion");
      } else {
        BuildingDisplacementRuas.logger.fine("case 3 attraction");
      }
      // solve the polynomial equation of radius circle intersection
      double ps = -proj * vo1o2.norme();
      double decal = v.norme();
      // solve the equation
      double delta = ps * ps
          - decal * decal * (vo1o2.norme() * vo1o2.norme() - radius * radius);
      double constant = 0.0;
      if (delta < 0) {
        // Can't find a intersection with the circle
        constant = (lambdaM - (vo1o2.norme() + proj)) / (lambdaM - lambda0);
      } else {
        double sol1 = (-ps - Math.sqrt(delta)) / (decal * decal + 1.0);
        double sol2 = (-ps + Math.sqrt(delta)) / (decal * decal + 1.0);
        constant = Math.max(sol1, sol2);
        if (constant > 1.0) {
          constant = Math.min(sol1, sol2);
        }
        if (constant < 0.0 || constant > 1.0) {
          constant = (lambdaM - (vo1o2.norme() + proj)) / (lambdaM - lambda0);
        }
      }
      contrib.scalarMultiplication(constant);
      contributions.add(contrib);
    }

    return contributions;
  }

  /**
   * Get the displacement vectors from the map that concern a building. They are
   * the ones that are connected through triangulation with short segments.
   * 
   * @param build the building concerned by the displacement vectors
   * @param ai the block agent in which displacement is computed
   * @param displMap the map containing the point agent and their related
   *          displacement vector
   * @return a set of displacement vectors
   * @author GTouya
   */
  private static HashSet<Vector2D> getDisplVectorsOnBuilding(
      IUrbanElement build, IUrbanBlock ai, HashMap<INode, Vector2D> displMap) {
    HashSet<Vector2D> displVectors = new HashSet<Vector2D>();
    for (IEdge s : build.getProximitySegments()) {
      if (s.getWeight() > BuildingDisplacementRuas.distanceMax) {
        continue;
      }
      // if the segment does not concern build, continue
      if (!build
          .equals(s.getInitialNode().getGraphLinkableFeature().getFeature())
          && !build.equals(
              s.getFinalNode().getGraphLinkableFeature().getFeature())) {
        continue;
      }
      INode ap = s.getInitialNode();
      if (ap.getGraphLinkableFeature().getFeature().equals(build)) {
        ap = s.getFinalNode();
      }
      if (displMap.get(ap) == null) {
        continue;
      }
      displVectors.add(displMap.get(ap));
    } // for(GAELSegment s : ai.getSegments())
    return displVectors;
  }

  /**
   * Same as getDisplVectorsOnBuilding but also returns the point agent related
   * to the displacement vector.
   * 
   * @param build the building concerned by the displacement vectors
   * @param ai the block agent in which displacement is computed
   * @param displMap the map containing the point agent and their related
   *          displacement vector
   * @return a map with the point agents and their related vector
   * @author GTouya
   */
  private static HashMap<INode, Vector2D> getDisplVectorsAndPtOnBuilding(
      IUrbanElement build, IUrbanBlock ai, HashMap<INode, Vector2D> displMap) {
    HashMap<INode, Vector2D> displVectors = new HashMap<INode, Vector2D>();
    for (IEdge s : build.getProximitySegments()) {
      if (s.getWeight() > BuildingDisplacementRuas.distanceMax) {
        continue;
      }
      // if the segment does not concern build, continue
      if (!build
          .equals(s.getInitialNode().getGraphLinkableFeature().getFeature())
          && !build.equals(
              s.getFinalNode().getGraphLinkableFeature().getFeature())) {
        continue;
      }
      INode ap = s.getInitialNode();
      if (ap.getGraphLinkableFeature().getFeature().equals(build)) {
        ap = s.getFinalNode();
      }
      if (displMap.get(ap) == null) {
        continue;
      }

      if (BuildingDisplacementRuas.DEBUG
          && build.getFeature().getId() == BuildingDisplacementRuas.DEBUG_ID) {
        BuildingDisplacementRuas.triangAff.add(new Segment(
            s.getInitialNode().getPosition(), s.getFinalNode().getPosition()));
      }
      displVectors.put(ap, displMap.get(ap));
    } // for(GAELSegment s : ai.getSegments())
    return displVectors;
  }

  /**
   * Step 4.3 of the displacement algorithm that aggregates the displacement
   * contributions of the building neighbours to a single displacement vector.
   * It follows (Ruas, 1999, p.183) instructions: first the main direction is
   * computed based on the longest contribution. Then, all contributions are
   * projected on the main direction and its orthogonal direction. Finally, the
   * main direction vector is added to its orthogonal.
   * 
   * @param contributions the list of displacement vectors proposed by build
   *          neighbours during step 4.2
   * @param build the building on which contributions are aggregated
   * @return a displacement vector aggregating all contributions
   * @author GTouya
   */
  private static Vector2D aggregateDisplContributions(
      ArrayList<Vector2D> contributions, IUrbanElement build) {
    // first compute the two principal displacement directions
    // they are the direction with the biggest displacement vector and
    // its orthogonal direction
    Vector2D principalDir1 = new Vector2D();
    double maxNorm = 0.0;
    for (Vector2D v : contributions) {
      if (v.norme() > maxNorm) {
        maxNorm = v.norme();
        principalDir1 = v.copy();
      }
    }

    principalDir1.normalise();
    Vector2D principalDir2 = CommonAlgorithmsFromCartAGen
        .rotateVector(principalDir1, Math.PI / 2.0);

    // then projects the contributions on these two directions
    double displSum1 = 0.0, displSum2 = 0.0, sumCos = 0.0, sumSin = 0.0;
    // loop on the contributions
    for (Vector2D v : contributions) {
      double angleDiff = principalDir1.vectorAngle(v);

      displSum1 += Math.abs(Math.cos(angleDiff)) * Math.cos(angleDiff)
          * v.norme();
      sumCos += Math.abs(Math.cos(angleDiff));
      displSum2 += Math.abs(Math.sin(angleDiff)) * Math.sin(angleDiff)
          * v.norme();
      sumSin += Math.abs(Math.sin(angleDiff));
    }

    // finally adds the two principal vectors
    principalDir1.scalarMultiplication(displSum1 / sumCos);
    double facteur2 = 0.0;
    if (sumSin != 0.0) {
      facteur2 = displSum2 / sumSin;
    }
    principalDir2.scalarMultiplication(facteur2);

    return principalDir1.add(principalDir2);
  }

  /**
   * Step 5 of the displacement algorithm (final step). It concerns the
   * remaining building overlaps after step 4 that displaced buildings. When two
   * buildings still overlap, the smallest one is deleted and the kept one is
   * recentered. Thus, the method searches for building overlaps with build. If
   * one is found, the delete/recenter operation is carried out.
   * 
   * @param build the building on which overlap are searched
   * @param ai the block agent in which displacement is computed
   * @author GTouya
   */
  private static void deleteRecenterBuilding(IUrbanElement build,
      IUrbanBlock ai) {
    // if the building has been deleted, continue
    if (build.isDeleted()) {
      return;
    }

    // measures overlapping with buildings
    if (BlockBuildingsMeasures
        .getBuildingDirectOverlapRateWithOtherBuildings(build, ai) == 0.0) {
      return;
    }
    // loop on the other buildings to look for an overlapping one
    IUrbanElement neighbour = null;
    for (IUrbanElement ag : ai.getUrbanElements()) {
      if (ag.equals(build)) {
        continue;
      }
      if (ag.isDeleted()) {
        continue;
      }
      IGeometry intersection = ag.getSymbolGeom()
          .intersection(build.getSymbolGeom());
      if (intersection != null) {
        if (!intersection.isEmpty()) {
          neighbour = ag;
          break;
        }
      }
    } // for (IBuildingAgent ag : ai.getComponents())

    if (neighbour == null) {
      return;
    }

    // now computes the DELETION/RE-CENTER operation
    // first, chooses the one to delete, the smallest one
    IUrbanElement kept, deleted;
    if (build.getSymbolArea() > neighbour.getSymbolArea()) {
      kept = build;
      deleted = neighbour;
    } else {
      kept = neighbour;
      deleted = build;
    }

    IDirectPosition buildingCentre = Operateurs.milieu(
        build.getSymbolGeom().centroid(), neighbour.getSymbolGeom().centroid());

    // deletes the one chosen to be deleted
    deleted.eliminate();

    // now computes the translation to re-center the kept building
    IDirectPosition centroid = kept.getSymbolGeom().centroid();
    double dx = buildingCentre.getX() - centroid.getX();
    double dy = buildingCentre.getY() - centroid.getY();
    // computes the translation
    kept.displaceAndRegister(dx, dy);
  }

  /**
   * Step 4.2bis of the displacement algorithm. Tests showed that a weakness of
   * (Ruas, 1999) algorithm was that a building might not be pushed into block
   * free space if roads or other buildings do not push it into. Force the
   * building to move to free space make the displacement of all block buildings
   * easier. So this step is added to attract a building into a free space if
   * one exists. The method searches for triangulation segments longer than
   * proximity threshold. Then, it checks if the free space directions are
   * focused on a small angle (if there is free space everywhere around the
   * building, there is no need for attraction). Then it adds contribution
   * vectors to influence the displacement towards the free space.
   * 
   * @param build the building on which we search free space attraction
   * @param ai the block agent in which displacement is computed
   * @param contribs
   * @author GTouya
   */
  private static void addFreeSpaceAttraction(IUrbanElement build,
      IUrbanBlock ai, ArrayList<Vector2D> contribs) {

    // get the triangulation segments that are long enough
    HashSet<Vector2D> longSegments = new HashSet<Vector2D>();
    double maxLength = 0.0;
    for (IEdge s : build.getProximitySegments()) {
      if (s.getWeight() > BuildingDisplacementRuas.distanceMax) {
        IDirectPosition pt1 = s.getInitialNode().getPosition();
        IDirectPosition pt2 = s.getFinalNode().getPosition();
        if (!s.getInitialNode().getGraphLinkableFeature().getFeature()
            .equals(build)) {
          pt1 = s.getFinalNode().getPosition();
          pt2 = s.getInitialNode().getPosition();
        }
        if (s.getWeight() > maxLength) {
          maxLength = s.getWeight();
        }
        longSegments.add(new Vector2D(pt1, pt2));
      }
    }
    // filter the long segments to keep only the longest ones
    HashSet<Vector2D> copy = new HashSet<Vector2D>();
    copy.addAll(longSegments);
    for (Vector2D s : copy) {
      if (s.norme() < 0.9 * maxLength) {
        longSegments.remove(s);
      }
    }

    // compute the maximum angle between the segments
    double maxAngle = 0.0;
    for (Vector2D v : longSegments) {
      for (Vector2D w : longSegments) {
        if (w.equals(v)) {
          continue;
        }
        double angle = w.vectorAngle0ToPi(v);
        if (angle > maxAngle) {
          maxAngle = angle;
        }
      }
    }

    // if the maximum angle is superior than threshold, return without adding
    // free space attraction
    if (maxAngle > BuildingDisplacementRuas.maxAngleFreeSpace) {
      return;
    }

    // loop on the segments
    for (Vector2D v : longSegments) {
      // create an attraction vector related to this free space vector
      v.normalise();
      // put the separation threshold as norm for this vector
      double norm = Legend.getSYMBOLISATI0N_SCALE() / 1000.0
          * GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT;
      v.scalarMultiplication(norm);
      // add the attraction vector to the vector map
      contribs.add(v);
    }

  }
}
