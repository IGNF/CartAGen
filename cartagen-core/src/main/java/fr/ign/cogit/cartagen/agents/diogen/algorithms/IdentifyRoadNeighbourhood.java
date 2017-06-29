package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.road.RoadNeighbourhoodAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.GraphLinkableFeature;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class IdentifyRoadNeighbourhood {

  // .addFeatureToGeometryPool(point.toGM_Point(), Color.BLUE, 2);

  private static double MAX_DISTANCE_FROM_ROAD = 1.;

  private static double MAX_DISTANCE_BETWEEN_BUILDING = 2.;

  private static double MAX_DISTANCE_PROJECTION = 1.;

  /**
   * Main method, uses buffer in order to detect buildings at proximity.
   * @param roads
   * @param buildings
   */
  public static void compute(IPopulation<IRoadLine> roads,
      IPopulation<IBuilding> buildings) {

    for (IRoadLine road : roads) {
      RoadNeighbourhoodAgent leftAgent = RoadNeighbourhoodAgent
          .getNeighbourhoodAgent(road, true);
      for (IBuilding building : select(road, buildings, true)) {
        leftAgent.addAgentsInNeighbourhood(getBuildingAgent(building));
      }

      RoadNeighbourhoodAgent rightAgent = RoadNeighbourhoodAgent
          .getNeighbourhoodAgent(road, false);
      for (IBuilding building : select(road, buildings, false)) {
        rightAgent.addAgentsInNeighbourhood(getBuildingAgent(building));
      }
    }
  }

  /**
   * Main method using triangulation in order to create the neighbourhood.
   * @param roads
   * @param buildings
   */
  public static void computeUsingTriangulation(IPopulation<IRoadLine> roads,
      IPopulation<IBuilding> buildings) {

    GeometryPool geomPool = CartAGenDoc.getInstance().getCurrentDataset()
        .getGeometryPool();

    // create triangulation
    Triangulation tri = buildTriangulation(buildings, roads);

    // Set<TriangulationTriangle> triangles = new HashSet<>();
    // triangles.addAll(tri.getTriangles());
    Set<IBuilding> addedBuidlings = new HashSet<>();

    // for each triangle, test if the triangle is linking two point of road to a
    // building
    for (TriangulationTriangle t : tri.getTriangles()) {

      // identify point from road section from building centroids.
      List<INode> buildingsInTriangle = new ArrayList<>();
      List<INode> roadsInTriangle = new ArrayList<>();

      if (t.getNode1().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding) {
        buildingsInTriangle.add(t.getNode1());
      } else if (t.getNode1().getGraphLinkableFeature()
          .getFeature() instanceof PointOfRoadForTriangulation) {
        roadsInTriangle.add(t.getNode1());
      }

      if (t.getNode2().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding) {
        buildingsInTriangle.add(t.getNode2());
      } else if (t.getNode2().getGraphLinkableFeature()
          .getFeature() instanceof PointOfRoadForTriangulation) {
        roadsInTriangle.add(t.getNode2());
      }

      if (t.getNode3().getGraphLinkableFeature()
          .getFeature() instanceof IBuilding) {
        buildingsInTriangle.add(t.getNode3());
      } else if (t.getNode3().getGraphLinkableFeature()
          .getFeature() instanceof PointOfRoadForTriangulation) {
        roadsInTriangle.add(t.getNode3());
      }

      if (roadsInTriangle.size() == 2) {
        IRoadLine road = (IRoadLine) ((PointOfRoadForTriangulation) roadsInTriangle
            .get(0).getGraphLinkableFeature().getFeature()).getRoad();
        IRoadLine road2 = (IRoadLine) ((PointOfRoadForTriangulation) roadsInTriangle
            .get(1).getGraphLinkableFeature().getFeature()).getRoad();

        if (!road.equals(road2)) {
          continue;
        }

        IBuilding building = (IBuilding) buildingsInTriangle.get(0)
            .getGraphLinkableFeature().getFeature();

        // calculate the angle, and put it in the appropriate environment
        IDirectPosition point1, point2, point3;
        point1 = roadsInTriangle.get(0).getPosition();
        double position1 = ((PointOfRoadForTriangulation) roadsInTriangle.get(0)
            .getGraphLinkableFeature().getFeature()).getPosition();
        point3 = roadsInTriangle.get(1).getPosition();
        double position3 = ((PointOfRoadForTriangulation) roadsInTriangle.get(1)
            .getGraphLinkableFeature().getFeature()).getPosition();

        if (position1 > position3) {
          IDirectPosition point = point3;
          point3 = point1;
          point1 = point;
        }

        // System.out.println("point1 " + point1);
        // System.out.println("position1 " + position1);
        // System.out.println("point3 " + point3);
        // System.out.println("position3 " + position3);

        point2 = buildingsInTriangle.get(0).getPosition();

        double distanceMax = (MAX_DISTANCE_FROM_ROAD + (road.getWidth() / 2))
            * Legend.getSYMBOLISATI0N_SCALE() / 1000;

        if (road instanceof ICarrierNetworkSection) {
          distanceMax += ((ICarrierNetworkSection) road).maxWidth()
              * Legend.getSYMBOLISATI0N_SCALE() / 1000;
        }

        // System.out.println("Distance: " + distanceMax);

        if (point1.toGM_Point().distance(building.getGeom()) > distanceMax
            || point3.toGM_Point().distance(building.getGeom()) > distanceMax) {
          continue;
        }

        Color fillColor = new Color((float) Math.random(),
            (float) Math.random(), (float) Math.random());
        geomPool.addFeatureToGeometryPool(
            new GM_Polygon(new GM_LineString(t.getPoint1().getPosition(),
                t.getPoint2().getPosition(), t.getPoint3().getPosition(),
                t.getPoint1().getPosition())),
            fillColor, 2);

        // compute the vectorial product
        double pv = (point1.getX() - point2.getX())
            * (point3.getY() - point2.getY())
            - (point1.getY() - point2.getY()) * (point3.getX() - point2.getX());

        // compute the scalar product
        double ps = (point1.getX() - point2.getX())
            * (point3.getX() - point2.getX())
            + (point1.getY() - point2.getY()) * (point3.getY() - point2.getY());

        double angle = Math.atan2(pv, ps);

        // System.out.println("angle " + angle);

        if (angle > 0) {
          // the building is on the left side
          RoadNeighbourhoodAgent leftAgent = RoadNeighbourhoodAgent
              .getNeighbourhoodAgent(road, true);
          // System.out.println(building + " added to left " + leftAgent);
          leftAgent.addAgentsInNeighbourhood(getBuildingAgent(building));
          // geomPool.addFeatureToGeometryPool(point2.toGM_Point(), Color.RED,
          // 2);
        } else {
          // the building is on the right side
          RoadNeighbourhoodAgent rightAgent = RoadNeighbourhoodAgent
              .getNeighbourhoodAgent(road, false);
          // System.out.println(building + " added to right " + rightAgent);
          rightAgent.addAgentsInNeighbourhood(getBuildingAgent(building));
          // geomPool.addFeatureToGeometryPool(point2.toGM_Point(), Color.BLUE,
          // 2);
        }
        addedBuidlings.add(building);
      }
    }

    while (!addedBuidlings.isEmpty()) {
      IBuilding building = addedBuidlings.iterator().next();
      addedBuidlings.remove(building);
      // System.out.println("building: " + building);
      INode node = getFeatureFromNode(tri, building);
      // get the other nodes at proximity
      Set<INode> nodes = node.getNextNodes();
      for (INode otherNode : nodes) {
        IGeneObj f = (IGeneObj) otherNode.getGraphLinkableFeature()
            .getFeature();
        if (!(f instanceof IBuilding)) {
          continue;
        }

        // if there are building add them to the environments where the current
        // building is, and test if the neighbour buildings are already in

        IBuilding otherBuilding = (IBuilding) f;
        for (Environment env : getBuildingAgent(building)
            .getContainingEnvironments()) {

          if (env.getContainedAgents()
              .contains(getBuildingAgent(otherBuilding))) {
            continue;
          }

          IAgent host = env.getHostAgent();
          if (!(host instanceof RoadNeighbourhoodAgent)) {
            continue;
          }

          System.out
              .println(otherBuilding.getGeom().distance(building.getGeom()));

          if (otherBuilding.getGeom()
              .distance(building.getGeom()) > MAX_DISTANCE_BETWEEN_BUILDING
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000) {
            continue;
          }

          DirectPositionList positions = (DirectPositionList) ((ILineString) (((RoadNeighbourhoodAgent) host)
              .getFeature().getGeom())).coord().clone();

          int position = Operateurs.projectAndInsertWithPosition(
              otherBuilding.getGeom().centroid(), positions.getList());

          System.out.println("Buiding " + otherBuilding);
          System.out
              .println("Road " + ((RoadNeighbourhoodAgent) host).getFeature());

          System.out
              .println("position " + position + " on " + positions.size());

          System.out.println("Distance 1 " + positions.get(0).toGM_Point()
              .distance(otherBuilding.getGeom()));

          System.out.println("Distance 2 " + positions.get(positions.size() - 1)
              .toGM_Point().distance(otherBuilding.getGeom()));

          if (position == 1 && positions.get(0).toGM_Point()
              .distance(otherBuilding.getGeom()) > MAX_DISTANCE_PROJECTION
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000) {
            continue;
          }

          if (position == positions.size() - 2
              && positions.get(positions.size() - 1).toGM_Point()
                  .distance(otherBuilding.getGeom()) > MAX_DISTANCE_PROJECTION
                      * Legend.getSYMBOLISATI0N_SCALE() / 1000) {
            continue;
          }

          ((RoadNeighbourhoodAgent) host)
              .addAgentsInNeighbourhood(getBuildingAgent(otherBuilding));

          System.out.println(otherBuilding + " added to ? " + host);
          addedBuidlings.add(otherBuilding);

          geomPool.addFeatureToGeometryPool(
              new GM_LineString(node.getPosition(),
                  Operateurs.milieu(node.getPosition(),
                      otherNode.getPosition()),
                  otherNode.getPosition()),
              Color.RED, 2);

          // env.addContainedAgents(getBuildingAgent(otherBuilding));
        }
      }
    }
  }

  private static TriangulationPoint getFeatureFromNode(Triangulation tri,
      IGeneObj f) {

    for (TriangulationPoint n : tri.getPoints()) {
      if (n.getGraphLinkableFeature().getFeature() == f) {
        return n;
      }
    }
    return null;
  }

  /**
   * Get the agent associated to a building
   * @param building
   * @return
   */
  private static BuildingAgent getBuildingAgent(IBuilding building) {

    if (building.getGeneArtifacts() == null) {
      return null;
    }
    BuildingAgent agent = null;
    for (Object obj : building.getGeneArtifacts()) {
      if (obj instanceof BuildingAgent) {
        agent = (BuildingAgent) obj;
        break;
      }
    }
    return agent;
  }

  /**
   * Select buidling in neighbourhood
   * @param road
   * @param buildings
   * @param left
   * @return
   */
  private static Collection<IBuilding> select(IRoadLine road,
      IPopulation<IBuilding> buildings, boolean left) {

    IMultiCurve<ILineString> multiCurve = JtsAlgorithms
        .offsetCurve(road.getGeom(), MAX_DISTANCE_FROM_ROAD * (left ? 1 : -1)
            * Legend.getSYMBOLISATI0N_SCALE() / 1000);
    ILineString g = multiCurve.get(0);

    if (g.coord().get(0).distance(road.getGeom().coord().get(0)) < g.coord()
        .get(0).distance(
            road.getGeom().coord().get(road.getGeom().coord().size() - 1))) {
      g.coord().inverseOrdre();
    }

    IDirectPositionList set = (IDirectPositionList) road.getGeom().coord()
        .clone();

    for (IDirectPosition position : set) {
      g.coord().add(position);
    }
    g.coord().add(g.coord().get(0));

    if (!left) {
      g.coord().inverseOrdre();
    }

    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(g, Color.BLUE, 2);

    return buildings.select(new GM_Polygon(g));
  }

  /**
   * Create a triangulation using centroid of buidlings and projection of
   * building on roads.
   * @param buildings
   * @param roadLines
   * @param distanceMax
   * @return
   */
  public static Triangulation buildTriangulation(
      IPopulation<IBuilding> buildings, IPopulation<IRoadLine> roadLines) {

    // GeometryPool geomPool = CartAGenDoc.getInstance().getCurrentDataset()
    // .getGeometryPool();

    List<TriangulationPoint> triPoints = new ArrayList<>();

    // identify buildings centroid, and projections on roadLines
    TriangulationPoint apBatiment, apTroncon;
    IPoint ptBatiment, ptTroncon;
    for (IBuilding building : buildings) {
      if (building.isDeleted()) {
        continue;
      }

      // get building centroid
      ptBatiment = new GM_Point(building.getGeom().centroid());

      // add point to triangulation
      apBatiment = new TriangulationPointImpl(ptBatiment.getPosition());
      IGraphLinkableFeature linkedBuilding = new GraphLinkableFeature(building);
      apBatiment.setGraphLinkableFeature(linkedBuilding);
      linkedBuilding.setReferentGraphNode(apBatiment);
      // System.out.println("Add point: centroid.");
      // geomPool.addFeatureToGeometryPool(apBatiment.getGeom(), Color.GREEN,
      // 2);
      triPoints.add(apBatiment);

      // parcours des troncons
      for (INetworkSection section : roadLines) {
        if (section.isDeleted()) {
          continue;
        }

        double distanceMax = (MAX_DISTANCE_FROM_ROAD + (section.getWidth() / 2))
            * Legend.getSYMBOLISATI0N_SCALE() / 1000;

        if (section instanceof ICarrierNetworkSection) {
          distanceMax += ((ICarrierNetworkSection) section).maxWidth()
              * Legend.getSYMBOLISATI0N_SCALE() / 1000;
        }
        // System.out.println("Distance: " + distanceMax);

        // if the distance between the road and the building is too high,
        // continue.
        if (building.getGeom().distance(section.getGeom()) > distanceMax) {
          continue;
        }

        // get the projection of the building on the road and the nearest point.
        DirectPositionList positions = (DirectPositionList) ((ILineString) (section
            .getGeom())).coord().clone();
        // System.out.println("Geom size " + section.getGeom().coord().size());
        // System.out.println("Section " + section);
        int position = Projections.projectAndInsertWithPositionOutside(
            ptBatiment.getPosition(), positions.getList());

        ptTroncon = new GM_Point(positions.get(position));

        PointOfRoadForTriangulation feature;

        // Color c;

        if (position == 0) {
          feature = new PointOfRoadForTriangulation(ptTroncon, section,
              -positions.get(0).distance(positions.get(1)));
          // c = Color.MAGENTA;
        } else if (position == positions.size() - 1) {

          feature = new PointOfRoadForTriangulation(ptTroncon, section,
              positions.size() + positions.get(positions.size() - 1)
                  .distance(positions.get(positions.size() - 2)));
          // c = Color.ORANGE;
        } else {
          IPoint ptTronconBefore = new GM_Point(positions.get(position - 1));
          IPoint ptTronconAfter = new GM_Point(positions.get(position + 1));

          double d = (ptTronconBefore.distance(ptTroncon))
              / (ptTronconBefore.distance(ptTronconAfter));

          feature = new PointOfRoadForTriangulation(ptTroncon, section,
              position - 1 + d);

          // c = Color.BLUE;
        }

        // creer agent point au niveau du projete
        apTroncon = new TriangulationPointImpl(ptTroncon.getPosition());
        apTroncon.setGraphLinkableFeature(new GraphLinkableFeature(feature));

        // System.out.println("Add point: projection.");
        // geomPool.addFeatureToGeometryPool(apTroncon.getGeom(), c, 2);
        triPoints.add(apTroncon);
        // Add an other point next to the projection
        IPoint ptTroncon2;
        PointOfRoadForTriangulation feature2;
        if (position < positions.size() - 1) {
          ptTroncon2 = new GM_Point(positions.get(position + 1));
          feature2 = new PointOfRoadForTriangulation(ptTroncon2, section,
              position);
        } else {
          ptTroncon2 = new GM_Point(positions.get(position - 1));
          feature2 = new PointOfRoadForTriangulation(ptTroncon2, section,
              position - 1);
        }
        TriangulationPointImpl apTroncon2 = new TriangulationPointImpl(
            ptTroncon2.getPosition());
        apTroncon2.setGraphLinkableFeature(new GraphLinkableFeature(feature2));

        // System.out.println("Add point: neighbour of projection.");
        // geomPool.addFeatureToGeometryPool(apTroncon2.getGeom(), Color.RED,
        // 2);
        triPoints.add(apTroncon2);
      }
    }

    // triangulation
    Triangulation tri = new Triangulation(triPoints,
        new TriangulationSegmentFactoryImpl(),
        new TriangulationTriangleFactoryImpl());
    tri.compute(true);
    // System.out.println("Triangulation computed");
    // for (TriangulationTriangle t : tri.getTriangles()) {
    // // System.out.println("Triangle " + t);
    // Color fillColor = new Color((float) Math.random(), (float) Math.random(),
    // (float) Math.random());
    // geomPool.addFeatureToGeometryPool(new GM_Polygon(new GM_LineString(t
    // .getPoint1().getPosition(), t.getPoint2().getPosition(), t
    // .getPoint3().getPosition(), t.getPoint1().getPosition())), fillColor,
    // 2);
    // }

    return tri;
  }
}
