package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.PadawanUtil;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingsBorderAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.IBuildingsBorderAgent;
import fr.ign.cogit.cartagen.agents.diogen.environment.LinearEnvironment;
import fr.ign.cogit.cartagen.agents.diogen.environment.PolylinearEnvironment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentType;
import fr.ign.cogit.cartagen.agents.diogen.schema.AggregateBuilding;
import fr.ign.cogit.cartagen.agents.diogen.schema.BuildingsBorder;
import fr.ign.cogit.cartagen.agents.diogen.schema.IAggregateBuilding;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELSegmentGeneObj;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class AdjacentBuildingsDecomposition {

  private static Logger logger = Logger
      .getLogger(AdjacentBuildingsDecomposition.class.getName());

  // a population for the building aggregation
  private IPopulation<IAggregateBuilding> aggregatedBuildings = new Population<>();

  // a population for the borders
  private IPopulation<BuildingsBorder> borders = new Population<>();

  /**
   * Create agents for the new feature created in the decomposition
   * @param dataset
   * @param global
   */
  public Set<IDiogenAgent> createAgents(CartAGenDataSet dataset,
      Environment global) {

    Set<IDiogenAgent> agents = new HashSet<>();
    // TODO create an environment type for the building as a container of
    // building, border, point and segments.

    Map<IDirectPosition, IPointAgent> pointMap = new HashMap<>();

    Map<CouplePoint, SegmentSubmicroAgent> segmentsMap = new HashMap<>();

    //
    for (IBuilding building : dataset.getBuildings()) {
      if (building instanceof IAggregateBuilding) {
        continue;
      }
      // get the agent for the building, and its encapsulated environment
      IDiogenAgent agent = PadawanUtil.getIODAAgentFromGeneObj(building);
      PolylinearEnvironment env;
      if (agent == null) {
        agent = PadawanUtil.createIODABuildingAgent(building, global);
        env = createAggregateBuildingStructureEnvironment();
        agent.setEncapsulatedEnv(env);
        // TODO manage the constraints
        // add a new environment to store other agents (original buildings +
        // border)
      } else {
        env = (PolylinearEnvironment) agent.getEncapsulatedEnv();
      }
      // add the agent to the return set
      agents.add(agent);

      // create agents for the points and segments of the building
      IPointAgent prevPointAgent = null;
      for (IDirectPosition directPosition : building.getGeom().coord()) {
        IPointAgent pointAgent = getPointAgentFromDirectPosition(directPosition,
            env, pointMap);
        ((IDiogenAgent) pointAgent).addContainingEnvironments(env);
        agents.add((IDiogenAgent) pointAgent);
        if (prevPointAgent != null) {
          GAELSegmentGeneObj segment = new GAELSegmentGeneObj(prevPointAgent,
              pointAgent);
          SegmentSubmicroAgent segmentAgent = getSegmentAgent(segment,
              segmentsMap);
          boolean direction = true;
          // logger.debug("prevPoint " + prevPointAgent + " currentPoint "
          // + pointAgent);
          // logger.debug("segment agent " + segmentAgent);

          if (segmentAgent.getP1() == pointAgent) {
            direction = false;
          }
          // logger.debug("direction " + direction);
          env.addContainedAgents(segmentAgent, direction);
          // segmentAgent.addContainingEnvironments(env);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) prevPointAgent,
                  0);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) pointAgent, 1);
          agents.add(segmentAgent);
        }
        prevPointAgent = pointAgent;
      }
      // for each point, create an agent
      // if the point is already agentified for another geometry, use the same
      // agent ?
    }

    // create agent for border
    for (BuildingsBorder border : borders) {
      IBuildingsBorderAgent agent = PadawanUtil
          .createBuildingsBorderAgent(border, global);
      PolylinearEnvironment env = createBorderEnvironment();
      agent.setEncapsulatedEnv(env);
      ((IDiogenAgent) PadawanUtil.getIODAAgentFromGeneObj(border.getB1()))
          .getEncapsulatedEnv().addContainedAgents(agent);
      ((IDiogenAgent) PadawanUtil.getIODAAgentFromGeneObj(border.getB2()))
          .getEncapsulatedEnv().addContainedAgents(agent);
      agents.add(agent);

      IPointAgent prevPointAgent = null;
      for (IDirectPosition directPosition : border.getGeom().coord()) {
        IPointAgent pointAgent = getPointAgentFromDirectPosition(directPosition,
            env, pointMap);
        ((IDiogenAgent) pointAgent).addContainingEnvironments(env);
        agents.add((IDiogenAgent) pointAgent);

        if (prevPointAgent != null) {
          GAELSegmentGeneObj segment = new GAELSegmentGeneObj(prevPointAgent,
              pointAgent);
          SegmentSubmicroAgent segmentAgent = getSegmentAgent(segment,
              segmentsMap);
          boolean direction = true;
          if (segmentAgent.getP1() == pointAgent) {
            direction = false;
          }
          env.addContainedAgents(segmentAgent, direction);
          // segmentAgent.addContainingEnvironments(env);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) prevPointAgent,
                  0);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) pointAgent, 1);
          agents.add(segmentAgent);
        }
        prevPointAgent = pointAgent;
      }
    }

    // create the agents for the new buildings of the buildings
    for (IAggregateBuilding building : aggregatedBuildings) {
      IDiogenAgent agent = PadawanUtil.getIODAAgentFromGeneObj(building);
      PolylinearEnvironment env;
      if (agent == null) {
        agent = PadawanUtil.createIODABuildingAgent(building, global);
        env = createAggregateBuildingStructureEnvironment();
        agent.setEncapsulatedEnv(env);

        for (IBuilding b : building.getAggregatedBuidlings()) {
          env.addContainedAgents(PadawanUtil.getIODAAgentFromGeneObj(b));
        }

        for (BuildingsBorder b : building.getBuildingBorders()) {
          env.addContainedAgents(PadawanUtil.getIODAAgentFromGeneObj(b));
        }
      } else {
        env = (PolylinearEnvironment) agent.getEncapsulatedEnv();
      }

      IPointAgent prevPointAgent = null;
      for (IDirectPosition directPosition : building.getGeom().coord()) {
        IPointAgent pointAgent = getPointAgentFromDirectPosition(directPosition,
            env, pointMap);
        // pointAgent.addContainingEnvironments(env);

        if (prevPointAgent != null) {
          GAELSegmentGeneObj segment = new GAELSegmentGeneObj(prevPointAgent,
              pointAgent);
          SegmentSubmicroAgent segmentAgent = getSegmentAgent(segment,
              segmentsMap);
          boolean direction = true;
          // logger.debug("prevPoint " + prevPointAgent + " currentPoint "
          // + pointAgent);
          // logger.debug("segment agent " + segmentAgent);
          if (segmentAgent.getP1() == pointAgent) {
            direction = false;
          }
          logger.debug("direction " + direction);
          env.addContainedAgents(segmentAgent, direction);
          // env.addContainedAgents(segmentAgent);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) prevPointAgent,
                  0);
          ((LinearEnvironment) segmentAgent.getEncapsulatedEnv())
              .addContainedAgentsWithCoordinate((IDiogenAgent) pointAgent, 1);
          // segmentAgent.getEncapsulatedEnv().addContainedAgents(pointAgent);
          agents.add(segmentAgent);
        }
        prevPointAgent = pointAgent;

      }
      agents.add(agent);
    }
    return agents;
  }

  /**
   * 
   * @param pos
   * @param env
   * @param pointMap
   * @return
   */
  public IPointAgent getPointAgentFromDirectPosition(IDirectPosition pos,
      PolylinearEnvironment env, Map<IDirectPosition, IPointAgent> pointMap) {
    IPointAgent toReturn = env.getPointAgentWithInitialPosition(pos);
    if (toReturn == null) {
      toReturn = pointMap.get(pos);
      if (toReturn == null) {
        toReturn = new GeographicPointAgent(pos);
        toReturn.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        // toReturn.addContainingEnvironments(env);
        pointMap.put(pos, toReturn);
      }
      env.addContainedAgents((IDiogenAgent) toReturn);
    }
    return toReturn;
  }

  /**
   * Get the segments agents constructed during the agent
   * @param seg
   * @param segmentsMap
   * @return
   */
  public SegmentSubmicroAgent getSegmentAgent(GAELSegmentGeneObj seg,
      Map<CouplePoint, SegmentSubmicroAgent> segmentsMap) {
    CouplePoint c = new CouplePoint(seg);
    SegmentSubmicroAgent toReturn = segmentsMap.get(c);
    // System.out.println("Get segment agent " + toReturn + " for " + seg);
    if (toReturn == null) {
      // System.out.println(segmentsMap);
      toReturn = new SegmentSubmicroAgent(seg);
      toReturn.setEncapsulatedEnv(createSegmentEnvironment());
      toReturn.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
      segmentsMap.put(c, toReturn);
    }
    return toReturn;
  }

  /**
   * Create the new features for the decomposition of buildings
   * @param dataset
   */
  public void detectAdjacentBuildings(CartAGenDataSet dataset) {

    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();

    // detects buildings to add
    Set<IBuilding> toAdds = new HashSet<>();
    toAdds.addAll(dataset.getBuildings());

    // associate building to a list of adjacent buildings
    Map<IBuilding, Set<IBuilding>> map = new HashMap<>();
    Map<IBuilding, Set<BuildingsBorder>> borderMap = new HashMap<>();

    // TODO use factory
    // PostGISFactory factory = new PostGISFactory();

    // associate each building to the aggregation in which it goes
    Map<IBuilding, IAggregateBuilding> buildingAggregateAssociation = new HashMap<>();

    for (IUrbanBlock block : dataset.getBlocks()) {
      for (IUrbanElement u1 : block.getUrbanElements()) {
        if (!(u1 instanceof IBuilding)) {
          continue;
        }
        IBuilding b1 = (IBuilding) u1;
        if (b1.getBlock() == null) {
          b1.setBlock(block);
        }
        for (IUrbanElement u2 : block.getUrbanElements()) {
          if (u1 == u2) {
            continue;
          }
          if (!(u2 instanceof IBuilding)) {
            continue;
          }
          IBuilding b2 = (IBuilding) u2;
          if (b2.getBlock() == null) {
            b2.setBlock(block);
          }
          // IGeometry
          if (u1.getGeom().intersects(u2.getGeom())) {
            IGeometry intersection = jtsAlgorithms.intersection(u1.getGeom(),
                u2.getGeom());
            // System.out.println(u1 + " intersects " + u2 + " interasection = "
            // + intersection);
            if (intersection instanceof IMultiCurve) {
              intersection = correctMultiLineString(
                  (IMultiCurve<ILineString>) intersection);
            }
            if (!(intersection instanceof ILineString)) {
              continue;
            }
            BuildingsBorder border = new BuildingsBorder(
                (ILineString) intersection, b1, b2);

            borders.add(border);
            Set<BuildingsBorder> sb = borderMap.get(b1);
            if (sb == null) {
              sb = borderMap.get(b2);
              if (sb == null) {
                sb = new HashSet<>();
                borderMap.put(b2, sb);
              }
              borderMap.put(b1, sb);
            } else {
              Set<BuildingsBorder> sb2 = borderMap.get(b1);
              if (sb2 != null) {
                sb.addAll(sb2);
              }
              borderMap.put(b2, sb);
            }
            sb.add(border);

            Set<IBuilding> s = map.get(b1);
            if (s == null) {
              s = map.get(b2);
              if (s == null) {
                s = new HashSet<>();
                map.put(b2, s);
              }
              map.put(b1, s);
            } else {
              Set<IBuilding> s2 = map.get(b1);
              if (s2 != null) {
                s.addAll(s2);
              }
              map.put(b2, s);
            }
            s.add(b1);
            s.add(b2);
          }
        }
      }
    }

    Set<IBuilding> keyset = map.keySet();
    // System.out.println("keyset " + keyset);

    while (!(keyset.isEmpty())) {
      IBuilding b = keyset.iterator().next();
      Set<IBuilding> buildings = map.get(b);

      // System.out.println("buildings " + buildings);
      List<IPolygon> geometries = new ArrayList<>();
      for (IBuilding bb : buildings) {
        geometries.add(bb.getGeom());
      }

      // logger.debug("Geometries " + geometries);

      IGeometry union = JtsAlgorithms.union(geometries);

      // System.out.println("union " + union);
      // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
      // .addFeatureToGeometryPool(union, Color.GREEN, 2);
      if (union instanceof IPolygon) {
        IPolygon poly = (IPolygon) union;
        if (!JtsAlgorithms.isCCW(poly.exteriorLineString())) {
          IDirectPositionList pos = poly.coord();
          pos = pos.reverse();
          poly = new GM_Polygon(new GM_LineString(pos));
        }

        IAggregateBuilding toAdd = new AggregateBuilding(poly);
        toAdd.addAggregatedBuildings(buildings);
        toAdd.addBorders(borderMap.get(b));

        aggregatedBuildings.add(toAdd);
        // factory.createBuilding(poly);
        // System.out.println("toAdd " + toAdd);

        buildings.iterator().next().getBlock().addUrbanElement(toAdd);

        // CartAGenDoc
        // .getInstance()
        // .getCurrentDataset()
        // .getGeometryPool()
        // .addFeatureToGeometryPool(
        // toAdd.getGeom().getEnvelope().getGeom(), Color.RED, 2);
        for (IBuilding bb : buildings) {
          buildingAggregateAssociation.put(bb, toAdd);
          bb.setDeleted(true);
        }
        // dataset.getBuildings().removeAll(buildings);
        toAdds.add(toAdd);
        keyset.removeAll(buildings);
      } else {
        logger.error("Problem, union is not a polygon : " + union);
      }
    }
    dataset.getBuildings().clear();
    dataset.getBuildings().addAll(toAdds);
  }

  private static IGeometry correctMultiLineString(
      IMultiCurve<ILineString> multiCurve) {
    CarteTopo carteTopo = new CarteTopo(
        "Fusionne MultiString into one LineString");
    IPopulation<Arc> edgePop = carteTopo.getPopArcs();
    for (ILineString line : multiCurve.getList()) {
      try {
        edgePop.nouvelElement(line);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    carteTopo.creeNoeudsManquants(0);
    carteTopo.fusionNoeuds(0);
    carteTopo.filtreArcsDoublons(0);
    carteTopo.rendPlanaire(0);
    carteTopo.fusionNoeuds(0);
    carteTopo.filtreArcsDoublons(0);
    carteTopo.filtreNoeudsSimples();

    IGeometry toReturn;
    if (edgePop.size() == 1) {
      toReturn = edgePop.get(0).getGeom();
    } else {
      List<ILineString> list = new ArrayList<>();
      for (Arc arc : edgePop) {
        list.add(arc.getGeometrie());
      }
      toReturn = new GM_MultiCurve<ILineString>(list);
    }
    return toReturn;

  }

  /**
   * Recompose all the buildings from the aggregated buildings
   * @param dataset
   */
  public void recompose(CartAGenDataSet dataset) {

    // Set<IBuilding> buildingsToAdd = new HashSet<>();
    // get all the aggregated buildings
    // for (IBuilding b : dataset.getBuildings()) {
    // if (!(b instanceof CdBAggregateBuilding)) {
    // // buildingsToAdd.add(b);
    // continue;
    // }
    //
    // for each aggregated building
    for (IAggregateBuilding aggregatedBuilding : aggregatedBuildings) {
      // get the aggregated building
      logger.debug(
          "Reconstruct the aggregated buildings from " + aggregatedBuilding);
      // get the agents
      IDiogenAgent aggregatedBuildingAgent = PadawanUtil
          .getIODAAgentFromGeneObj(aggregatedBuilding);
      // get the environment
      PolylinearEnvironment aggregatedBuildingEnv = (PolylinearEnvironment) aggregatedBuildingAgent
          .getEncapsulatedEnv();
      // take a segment
      // SegmentSubmicroAgent s1 = aggregatedBuildingEnv.getAllSegmentAgents()
      // .iterator().next();
      // SegmentSubmicroAgent sc = s1;
      // boolean bc = true;

      // for (IDirectPosition p : aggregatedBuilding.getInitialGeom().coord()) {
      // logger.debug(aggregatedBuildingEnv.getPointAgentWithInitialPosition(p));
      // }
      // System.out.println("by segment");

      // try to identify the first segment
      // while (true) {
      // LinearEnvironment e = (LinearEnvironment) sc.getEncapsulatedEnv();
      // logger.debug(sc);
      // logger.debug("P1 " + sc.getP1());
      // logger.debug("P2 " + sc.getP2());
      // List<IAgent> list = e.getOrderedPointAgents();
      // if (!bc)
      // Collections.reverse(list);
      // for (IAgent p : list) {
      // logger.debug(p + " position " + e.getAgentCoordinate(p));
      // }
      // SegmentAgentWithOrientation t = getNextSegment(aggregatedBuildingEnv,
      // sc, bc);
      // sc = t.agent;
      // bc = t.orientation;
      // logger.debug(bc);
      // if (sc == s1) {
      // break;
      // }
      // }

      // identify the aggregated buildings
      for (IDiogenAgent agent : aggregatedBuildingEnv.getContainedAgents()) {
        if (!(agent instanceof BuildingAgent)) {
          continue;
        }

        Color color = new Color((float) Math.random(), (float) Math.random(),
            (float) Math.random());
        // we get an original building
        IBuilding originalBuilding = ((BuildingAgent) agent).getFeature();
        logger.debug("Reconstruct building " + originalBuilding + " (agent = "
            + agent + " ) with geom " + originalBuilding.getGeom());

        // the environment encapsulated by this building
        PolylinearEnvironment originalBuildingEnv = (PolylinearEnvironment) agent
            .getEncapsulatedEnv();

        // for (IDirectPosition p : originalBuilding.getInitialGeom().coord()) {
        // logger.debug(originalBuildingEnv.getPointAgentWithInitialPosition(p));
        // }
        // FIXME Only for debug
        logger.debug("by segment");
        // while (true) {
        // // get the segment
        // SegmentSubmicroAgent sc = originalBuildingEnv.getAllSegmentAgents()
        // .iterator().next();
        // LinearEnvironment e = (LinearEnvironment) sc.getEncapsulatedEnv();
        //
        // boolean bc = originalBuildingEnv.getSegmentRelativeDirection(sc);
        // List<IAgent> list = e.getOrderedPointAgents();
        // if (!bc)
        // Collections.reverse(list);
        // for (IAgent p : list) {
        // logger.debug(p + " position " + e.getAgentCoordinate(p));
        // }
        // SegmentAgentWithOrientation t = getNextSegment(aggregatedBuildingEnv,
        // sc, bc);
        // sc = t.agent;
        // bc = t.orientation;
        //
        // }
        logger.debug("Geometry is clockwise oriented ? " + JtsAlgorithms
            .isCCW(originalBuilding.getGeom().exteriorLineString()));
        // FIXME
        // a list of positions for the new geometry of the building
        List<IDirectPosition> newBuildingPositions = new ArrayList<>();
        // identify if the border of this building is identified
        // boolean borderIncluded = false;
        // identify where the first position is

        // identify the current segment
        SegmentSubmicroAgent segmentAgent = null;

        int i = 0;
        IDirectPosition position = originalBuilding.getGeom().coord().get(i);
        IPointAgent pointAgent = originalBuildingEnv
            .getPointAgentWithInitialPosition(position);

        while (segmentAgent == null) {

          // try to identify where the two first points of the modified building
          // are

          //
          // BuildingsBorder concernedBorder = null;
          // test if in the border
          for (BuildingsBorder border : aggregatedBuilding
              .getBuildingBorders()) {
            // List<IDirectPosition> toAdd = new ArrayList<>();
            // if the first point is in the border, we need to identify the
            // first
            // point outside the border
            while (identifyPointInBorder(border, pointAgent)) {
              i++;
              position = originalBuilding.getGeom().coord().get(i);
              // try to identify where the first point of the new building is
              pointAgent = originalBuildingEnv
                  .getPointAgentWithInitialPosition(position);
              // add the points of the border
              // newBuildingPositions.addAll(toAdd);
              // the concerned border is identified
              // concernedBorder = border;
            }
          }

          // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
          // .addFeatureToGeometryPool(position.toGM_Point(), Color.GREEN, 2);
          //
          // CartAGenDoc
          // .getInstance()
          // .getCurrentDataset()
          // .getGeometryPool()
          // .addFeatureToGeometryPool(pointAgent.getPosition().toGM_Point(),
          // Color.PINK, 2);

          // now pointAgent is a point inside the aggregated geometry
          // try to identify the segment where this point is

          // identify the second point of geometry, at a different position of
          // the
          // first point
          int j = 1;
          IDirectPosition secondPosition = originalBuilding.getGeom().coord()
              .get(i + j);
          IPointAgent secondPointAgent = originalBuildingEnv
              .getPointAgentWithInitialPosition(secondPosition);

          while (aggregatedBuildingEnv.arePointAgentsAtSamePosition(pointAgent,
              secondPointAgent)) {
            // CartAGenDoc
            // .getInstance()
            // .getCurrentDataset()
            // .getGeometryPool()
            // .addFeatureToGeometryPool(secondPosition.toGM_Point(),
            // Color.BLUE, 2);
            //
            // CartAGenDoc
            // .getInstance()
            // .getCurrentDataset()
            // .getGeometryPool()
            // .addFeatureToGeometryPool(
            // secondPointAgent.getPosition().toGM_Point(), Color.PINK, 2);

            j++;
            secondPosition = originalBuilding.getGeom().coord().get(i + j);
            secondPointAgent = originalBuildingEnv
                .getPointAgentWithInitialPosition(secondPosition);
            // for (BuildingsBorder border :
            // aggregatedBuilding.getBuildingBorders()) {
            // if (identifyPointInBorder(border, secondPointAgent)) {
            // pointAgent = secondPointAgent;
            // position = secondPosition;
            // i = i + j;
            // while (identifyPointInBorder(border, secondPointAgent)) {
            // i++;
            // if (i == originalBuilding.getGeom().coord().size() - 1) {
            // i = 0;
            // }
            // position = originalBuilding.getGeom().coord().get(i);
            // // try to identify where the first point of the new building is
            // pointAgent = originalBuildingEnv
            // .getPointAgentWithInitialPosition(position);
            // }
            // j = 1;
            // secondPosition = originalBuilding.getGeom().coord().get(i + j);
            // secondPointAgent = originalBuildingEnv
            // .getPointAgentWithInitialPosition(secondPosition);
            //
            // }
            // }
          }

          // CartAGenDoc
          // .getInstance()
          // .getCurrentDataset()
          // .getGeometryPool()
          // .addFeatureToGeometryPool(secondPosition.toGM_Point(), Color.RED,
          // 2);
          //
          // CartAGenDoc
          // .getInstance()
          // .getCurrentDataset()
          // .getGeometryPool()
          // .addFeatureToGeometryPool(
          // secondPointAgent.getPosition().toGM_Point(), Color.PINK, 2);

          logger.debug("Take the first point of the building geometrie "
              + position + " and its agent " + pointAgent);

          logger.debug("Try to identify the segment with the points : "
              + pointAgent + " and " + secondPointAgent);
          for (SegmentSubmicroAgent sc : aggregatedBuildingEnv
              .getAllSegmentAgents()) {
            LinearEnvironment e = (LinearEnvironment) sc.getEncapsulatedEnv();
            if (e.contains((IDiogenAgent) pointAgent)) {
              logger.debug(pointAgent + " is inside  " + sc);
            }
            if (e.contains((IDiogenAgent) secondPointAgent)) {
              logger.debug(secondPointAgent + " is inside  " + sc);
            }
            if (e.contains((IDiogenAgent) pointAgent)
                && e.contains((IDiogenAgent) secondPointAgent)) {
              segmentAgent = sc;
              break;
            }
          }

          if (segmentAgent == null) {
            pointAgent = secondPointAgent;
            position = secondPosition;
            i = i + j;
          }

        }
        // if (segmentAgent == null) {
        // logger
        // .error("Error, point no identify as a point of the aggregated
        // building");
        // }

        // FIXME code for debug purpose
        SegmentSubmicroAgent sc1 = segmentAgent;
        do {
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(sc1.getFeature().getGeom(), Color.RED,
                  2);
          logger.debug("Segment " + sc1);
          LinearEnvironment sc1Env = (LinearEnvironment) sc1
              .getEncapsulatedEnv();
          logger.debug("contains " + sc1Env.getOrderedPointAgents());
          sc1 = aggregatedBuildingEnv.getNextSegment(sc1);
        } while (sc1 != segmentAgent);
        // FIXME code for debug purpose

        boolean orientation = aggregatedBuildingEnv
            .getSegmentRelativeDirection(segmentAgent);
        // the point as the be inside the aggregated building, if it is not he
        // case, it is a mistake
        // if (aggregatedBuildingEnv.contains(pointAgent)) {
        // // get the segment containing the point
        // boolean firstPointReached = false;
        // boolean secondPointReached = false;
        // for (SegmentSubmicroAgent s : aggregatedBuildingEnv
        // .getAllSegmentAgents()) {
        // for (IAgent p : ((LinearEnvironment) s.getEncapsulatedEnv())
        // .getOrderedPointAgents()) {
        // if (p == pointAgent) {
        // // test the position of the next point
        // if (secondPointReached) {
        // segmentAgent = s;
        // orientation = false;
        // }
        // firstPointReached = true;
        // } else if (p == secondPointAgent) {
        // // test the position of the next point
        // if (firstPointReached) {
        // segmentAgent = s;
        // orientation = true;
        // }
        // secondPointReached = true;
        // }
        //
        // if (firstPointReached && secondPointReached) {
        // // segment identified
        // // segmentAgent = s;
        // logger.debug("Segment of the first point identified "
        // + segmentAgent);
        // break;
        // }
        // }
        // if (firstPointReached && secondPointReached) {
        // // segment identified
        // // segmentAgent = s;
        // break;
        // }
        // firstPointReached = false;
        // secondPointReached = false;
        // }
        // } else {
        // // error
        // logger
        // .error("Error, point no identify as a point of the aggregated
        // building");
        // }
        // take segment by segment, until the point branching on the border is
        // identified
        while (true) {
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(segmentAgent.getFeature().getGeom(),
                  color, 2);

          // test if the current point is on a border
          boolean isInsideBorder = false;
          // the point of the border to add
          List<IDirectPosition> toAdd = new ArrayList<>();
          // the segment to explore
          LinearEnvironment segmentEnv = (LinearEnvironment) segmentAgent
              .getEncapsulatedEnv();
          // for each agent inside the segment, test if this agent is an
          // extremity of one of the border
          logger
              .debug("Test for each point of the segment if it is on a border");
          logger.debug("Segment : " + segmentAgent);

          // iterate on the point of the segment, and try to identify a point
          // extrtemities of a border
          List<IAgent> list = segmentEnv.getOrderedPointAgents();
          if (!orientation) {
            Collections.reverse(list);
          }
          // for (IAgent p : list) {
          // logger.debug("Point " + p + ", coordinates "
          // + segmentEnv.getAgentCoordinate(p));
          // }
          BuildingsBorder concernedBorder = null;
          double minCoordinates = 0;
          if (pointAgent != null) {
            minCoordinates = segmentEnv.getAgentCoordinate(pointAgent);
          }
          for (IAgent a : list) {
            if (segmentEnv.getAgentCoordinate(a) <= minCoordinates) {
              continue;
            }

            logger.debug("Test for the agent " + a);
            pointAgent = (IPointAgent) a;
            // if concerned border is null
            // if (concernedBorder == null) {
            // if the border is not identified, try to identify if it is the
            // good border
            for (BuildingsBorder border : aggregatedBuilding
                .getBuildingBorders()) {
              // test if the point is an extremity
              if (identifyPointInExtremitiesOfBorder(border, pointAgent,
                  toAdd)) {
                // the border is identified, we dont need to test here anymore
                concernedBorder = border;
                // the point is on a segment
                isInsideBorder = true;
                break;
              }
            }
            // } else {
            // isInsideBorder = identifyPointInExtremitiesOfBorder(
            // concernedBorder, pointAgent, toAdd);
            // logger.debug("Identify if inside the border " + isInsideBorder);
            // }
            if (isInsideBorder) {
              break;
            }
          }

          // if a point was detected as an extremity of the border
          if (isInsideBorder) { // && !borderIncluded) {
            logger.debug("Point " + pointAgent
                + " is an extremity of the border. Add content of the border.");
            // get the two current position of extremities

            // borderIncluded = true;
            // Identify the next segment, in the other side of the border
            IBuildingsBorderAgent borderAgent = (IBuildingsBorderAgent) PadawanUtil
                .getIODAAgentFromGeneObj(concernedBorder);
            PolylinearEnvironment borderEnv = (PolylinearEnvironment) borderAgent
                .getEncapsulatedEnv();

            IDirectPosition firstPosition = toAdd.get(0);
            IPointAgent firstPointAgent = (IPointAgent) borderEnv
                .getEdgePointAgentWithCurrentPosition(firstPosition);
            logger.debug("firstPointAgent " + firstPointAgent);
            IDirectPosition newFirstPosition = firstPointAgent.getPosition();
            logger.debug("newFirstPosition " + newFirstPosition);

            IDirectPosition lastPosition = toAdd.get(toAdd.size() - 1);
            IPointAgent lastPointAgent = (IPointAgent) borderEnv
                .getEdgePointAgentWithCurrentPosition(lastPosition);
            logger.debug("lastPointAgent " + lastPointAgent);
            IDirectPosition newLastPosition = lastPointAgent.getPosition();
            logger.debug("newLastPosition " + newLastPosition);

            boolean toBreak = false;
            for (IDirectPosition dp : toAdd) {

              logger.debug("dp " + dp);
              // get the distance from the first point
              double distanceFromFirst = dp.distance(firstPosition);
              logger.debug("distance from first " + distanceFromFirst);
              // get the distance from the last point
              double distanceFromLast = dp.distance(lastPosition);
              logger.debug("distance from last " + distanceFromLast);

              double relativeDistanceFromFirst = distanceFromLast
                  / (distanceFromFirst + distanceFromLast);
              logger.debug(
                  "relativeDistanceFromFirst " + relativeDistanceFromFirst);
              double relativeDistanceFromLast = distanceFromFirst
                  / (distanceFromFirst + distanceFromLast);
              logger.debug(
                  "relativeDistanceFromLast " + relativeDistanceFromLast);

              Vecteur vFirst = new Vecteur(dp, newFirstPosition);
              Vecteur vLast = new Vecteur(dp, newLastPosition);

              // get the projections vectors
              if (relativeDistanceFromFirst != 0) {
                logger.debug("vFirst x " + vFirst.getX());
                logger.debug("vFirst y " + vFirst.getY());
                vFirst = vFirst.multConstante(relativeDistanceFromFirst);
                logger.debug("vFirst " + vFirst);
                dp = Operateurs.translate(dp, vFirst);
                logger.debug("dp " + dp);
              }

              if (relativeDistanceFromLast != 0) {
                logger.debug("vLast x " + vLast.getX());
                logger.debug("vLast y " + vLast.getY());
                vLast = vLast.multConstante(relativeDistanceFromLast);
                logger.debug("vLast " + vLast);
                dp = Operateurs.translate(dp, vLast);
                logger.debug("dp " + dp);
              }

              newBuildingPositions.add(dp);
              if (newBuildingPositions.size() > 1) {
                if (newBuildingPositions.get(0).equals(newBuildingPositions
                    .get(newBuildingPositions.size() - 1))) {
                  logger.debug(
                      "Initial point reached, end of the reconstruction");
                  toBreak = true;
                  break;
                }
              }
            }
            if (toBreak) {
              break;
            }

            // identify the next segment
            logger.debug(
                "Try to identify the next segment, containing the point agent "
                    + lastPointAgent);
            // first, identify the position of the point in the original geom,
            // and identify the next point, with a different position on the
            // aggregated figure
            boolean currentDetected = false;
            IDirectPosition afterLastPosition = null;
            IPointAgent afterLastPointAgent = null;
            for (IDirectPosition p : originalBuilding.getGeom().coord()) {
              logger.debug("Point agent "
                  + originalBuildingEnv.getPointAgentWithInitialPosition(p));
              if (p.equals(lastPosition)) {
                logger.debug(
                    "Other extremitie detected inside the original geometry");
                currentDetected = true;
              } else if (currentDetected) {
                afterLastPointAgent = originalBuildingEnv
                    .getPointAgentWithInitialPosition(p);
                logger.debug(
                    "Test if the next point is at a different position : "
                        + afterLastPointAgent);
                if (!aggregatedBuildingEnv.arePointAgentsAtSamePosition(
                    lastPointAgent, afterLastPointAgent)) {
                  afterLastPosition = p;
                  break;
                }
              }

            }

            if (afterLastPosition == null && currentDetected) {
              for (IDirectPosition p : originalBuilding.getGeom().coord()) {
                afterLastPointAgent = originalBuildingEnv
                    .getPointAgentWithInitialPosition(p);
                if (!aggregatedBuildingEnv.arePointAgentsAtSamePosition(
                    lastPointAgent, afterLastPointAgent)) {
                  afterLastPosition = p;
                  break;
                }
              }
            }
            logger.debug("Old segment " + segmentAgent);
            logger.debug("Try to identify a segment with " + lastPointAgent
                + " and " + afterLastPointAgent);
            segmentAgent = null;
            for (SegmentSubmicroAgent sc : aggregatedBuildingEnv
                .getAllSegmentAgents()) {
              LinearEnvironment e = (LinearEnvironment) sc.getEncapsulatedEnv();
              if (e.contains((IDiogenAgent) lastPointAgent)) {
                logger.debug(lastPointAgent + " is inside  " + sc);
              }
              if (e.contains((IDiogenAgent) afterLastPointAgent)) {
                logger.debug(afterLastPointAgent + " is inside  " + sc);
              }
              if (e.contains((IDiogenAgent) lastPointAgent)
                  && e.contains((IDiogenAgent) afterLastPointAgent)) {
                segmentAgent = sc;
                logger.debug("New segment " + segmentAgent);
                break;
              }
            }
            if (segmentAgent == null) {
              logger.error(
                  "Error, point no identify as a point of the aggregated building");
              for (IPointAgent p : borderEnv.getAllPointAgents()) {
                logger.debug("Inside the border : " + p);
              }
            }
            pointAgent = lastPointAgent;
            orientation = aggregatedBuildingEnv
                .getSegmentRelativeDirection(segmentAgent);

            // boolean firstReached = false;
            // IPointAgent nextPointAgent = null;
            // for (IDirectPosition p : originalBuilding.getGeom().coord()) {
            // // try to identify the last point of the border in the original
            // // building
            // if (lastPointAgent == originalBuildingEnv
            // .getPointAgentWithInitialPosition(p)) {
            // logger
            // .debug("The last point of the border is identified in the
            // original building");
            // firstReached = true;
            // }
            //
            // if (firstReached) {
            // IPointAgent a = originalBuildingEnv
            // .getPointAgentWithInitialPosition(p);
            // if (!aggregatedBuildingEnv.arePointAgentsAtSamePosition(a,
            // lastPointAgent)) {
            // nextPointAgent = a;
            // logger
            // .debug("The point agent next to the last point is identified "
            // + nextPointAgent);
            // break;
            // }
            // }
            // }
            // if (!firstReached) {
            // logger
            // .error("Error, point no identify as a point of the aggregated
            // building");
            // }
            // // if the next point was not idetify, try a second loop
            // if (nextPointAgent == null) {
            // for (IDirectPosition p : originalBuilding.getGeom().coord()) {
            // IPointAgent a = originalBuildingEnv
            // .getPointAgentWithInitialPosition(p);
            // if (aggregatedBuildingEnv.arePointAgentsAtSamePosition(a,
            // lastPointAgent)) {
            // nextPointAgent = a;
            // logger
            // .debug("The point agent next to the last point is identified at
            // the second loop "
            // + nextPointAgent);
            // break;
            // }
            // }
            // }
            // if (nextPointAgent == null) {
            // logger
            // .error("Error, point no identify as a point of the aggregated
            // building");
            // }
            // logger.debug("Next point identified " + nextPointAgent);
            // // get the segment containing the point
            // boolean firstPointReached = false;
            // boolean secondPointReached = false;
            // // try to identify the point
            // for (SegmentSubmicroAgent s : aggregatedBuildingEnv
            // .getAllSegmentAgents()) {
            // for (IAgent p : ((LinearEnvironment) s.getEncapsulatedEnv())
            // .getOrderedPointAgents()) {
            // if (p == lastPointAgent) {
            // logger
            // .debug("Last point of the border identified on segment "
            // + s);
            // // test the position of the next point
            // if (secondPointReached) {
            // logger.debug("Two points reached.");
            // segmentAgent = s;
            // orientation = aggregatedBuildingEnv
            // .getSegmentRelativeDirection(segmentAgent);
            // }
            // firstPointReached = true;
            // } else if (p == nextPointAgent) {
            // logger
            // .debug("Next point of the border identified on segment "
            // + s);
            // // test the position of the next point
            // if (firstPointReached) {
            // logger.debug("Two points reached.");
            // segmentAgent = s;
            // orientation = aggregatedBuildingEnv
            // .getSegmentRelativeDirection(segmentAgent);
            // }
            // secondPointReached = true;
            // }
            // if (firstPointReached && secondPointReached) {
            // // segment identified
            // // segmentAgent = s;
            // logger.debug("Segment of the first points identified "
            // + segmentAgent);
            // break;
            // }
            // }
            // if (firstPointReached && secondPointReached) {
            // break;
            // }
            // firstPointReached = false;
            // secondPointReached = false;
            // }
            //
            // if (!firstPointReached && !secondPointReached) {
            // logger
            // .error("Error, point no identify as a point of the aggregated
            // building");
            // }

          } else {
            if (orientation) {
              newBuildingPositions.add(segmentAgent.getP2().getPosition());
              logger.debug("Add a new edge point " + segmentAgent.getP2());
            } else {
              newBuildingPositions.add(segmentAgent.getP1().getPosition());
              logger.debug("Add a new edge point " + segmentAgent.getP1());
            }
            pointAgent = null;
            logger.debug("Old segment " + segmentAgent);
            segmentAgent = aggregatedBuildingEnv.getNextSegment(segmentAgent);
            orientation = aggregatedBuildingEnv
                .getSegmentRelativeDirection(segmentAgent);
            logger.debug("New segment " + segmentAgent);
          }

          if (newBuildingPositions.size() > 1) {
            if (newBuildingPositions.get(0).equals(
                newBuildingPositions.get(newBuildingPositions.size() - 1))) {
              logger.debug("Initial point reached, end of the reconstruction");
              break;
            }
          }
        }

        // here the geometry is reconstructed,
        // the building may be reconstructed
        if (!newBuildingPositions.get(0).equals(
            newBuildingPositions.get(newBuildingPositions.size() - 1))) {
          newBuildingPositions.add(newBuildingPositions.get(0));
        }

        // recreate the new geometry
        IPolygon poly = new GM_Polygon(new GM_LineString(newBuildingPositions));
        originalBuilding.setGeom(poly);
        originalBuilding.setDeleted(false);
        // originalBuilding.set
        // buildingsToAdd.add(originalBuilding);
      }
      aggregatedBuilding.setDeleted(true);
    }
    // dataset.getBuildings().clear();
    // dataset.getBuildings().addAll(buildingsToAdd);
  }

  private SegmentAgentWithOrientation getNextSegment(PolylinearEnvironment env,
      SegmentSubmicroAgent segmentAgent, boolean goodOrientation) {
    IPointAgent pointAgent = goodOrientation ? segmentAgent.getP2()
        : segmentAgent.getP1();

    for (SegmentSubmicroAgent s : env.getAllSegmentAgents()) {
      if (segmentAgent != s) {
        if (pointAgent == s.getP1()) {
          return new SegmentAgentWithOrientation(s, true);
        } else if (pointAgent == s.getP2()) {
          return new SegmentAgentWithOrientation(s, false);
        }
      }
    }
    return null;
  }

  private class SegmentAgentWithOrientation {

    SegmentSubmicroAgent agent;

    boolean orientation;

    SegmentAgentWithOrientation(SegmentSubmicroAgent agent,
        boolean orientation) {
      this.agent = agent;
      this.orientation = orientation;
    }

  }

  /**
   * 
   * @param border
   * @param pointAgent
   * @param newBuildingPositions
   * @return
   */
  private static boolean identifyPointInExtremitiesOfBorder(
      BuildingsBorder border, IPointAgent pointAgent,
      List<IDirectPosition> newBuildingPositions) {

    BuildingsBorderAgent borderAgent = (BuildingsBorderAgent) PadawanUtil
        .getIODAAgentFromGeneObj(border);

    logger.debug("Try to identify point " + pointAgent
        + " as extremities of the border " + borderAgent);
    PolylinearEnvironment borderEnv = (PolylinearEnvironment) borderAgent
        .getEncapsulatedEnv();
    IDirectPosition beginPosition = border.getGeom().coord().get(0);
    IPointAgent beginAgentPoint = borderEnv
        .getPointAgentWithInitialPosition(beginPosition);
    // logger.debug("Begin point " + beginAgentPoint);
    if (beginAgentPoint == pointAgent) {
      // add the border list point to
      IDirectPositionList l = (IDirectPositionList) border.getGeom().coord()
          .clone();
      newBuildingPositions.addAll(l);
      logger.debug(pointAgent + " is first point of the border " + borderAgent);
      return true;
    }

    IDirectPosition endPosition = border.getGeom().coord()
        .get(border.getGeom().coord().size() - 1);
    IPointAgent endAgentPoint = borderEnv
        .getPointAgentWithInitialPosition(endPosition);

    // logger.debug("End point " + endAgentPoint);

    if (endAgentPoint == pointAgent) {
      // add the border list point to
      IDirectPositionList l = (IDirectPositionList) border.getGeom().coord()
          .clone();
      newBuildingPositions.addAll(l.reverse());
      logger.debug(pointAgent + " is last point of the border " + border);
      return true;
    }
    return false;

  }

  private static boolean identifyPointInBorder(BuildingsBorder border,
      IPointAgent pointAgent) {
    BuildingsBorderAgent borderAgent = (BuildingsBorderAgent) PadawanUtil
        .getIODAAgentFromGeneObj(border);

    PolylinearEnvironment borderEnv = (PolylinearEnvironment) borderAgent
        .getEncapsulatedEnv();
    // identify if a point is
    for (IPointAgent a : borderEnv.getAllPointAgents()) {
      if (a == pointAgent) {
        logger.debug(
            "Identify point " + pointAgent + " inside border " + borderAgent);
        return true;
      }
    }
    logger.debug(
        "Non identify point " + pointAgent + " inside border " + borderAgent);
    return false;
  }

  private static EnvironmentType buildingStructure = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getBuildingStructureEnvironmentType() {

    if (buildingStructure == null) {
      buildingStructure = new EnvironmentType();
      buildingStructure.setEnvironmentTypeName("BuildingStructure");
    }

    return buildingStructure;
  }

  public static Environment createBuildingStructureEnvironment() {
    Environment env = new PolylinearEnvironment();
    env.setEnvironmentType(getBuildingStructureEnvironmentType());
    return env;
  }

  private static EnvironmentType aggregateBuildingStructure = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getAggregateBuildingStructureEnvironmentType() {

    if (aggregateBuildingStructure == null) {
      aggregateBuildingStructure = new EnvironmentType();
      aggregateBuildingStructure
          .setEnvironmentTypeName("AggregateBuildingStructure");
    }

    /**
     * Class<? extends IAgent> sourceAgent = GeographicPointAgent.class; Class<?
     * extends IAgent> segmentAgent = SegmentSubmicroAgent.class; Class<?
     * extends IAgent> angleAgent = AngleSubmicroAgent.class;
     * 
     * buildingStructure.getInteractionMatrix().addDegenerateAssignation(
     * sourceAgent, new AssignationImpl<ConstrainedInteraction>(
     * PointAutoDisplacementInteraction.getInstance()));
     * 
     * buildingStructure.getInteractionMatrix().addSingleTargetAssignation(
     * sourceAgent, angleAgent, new AssignationImpl<ConstrainedInteraction>(
     * PointDisplacementAggregableInteraction.getInstance()));
     * 
     * buildingStructure.getInteractionMatrix().addSingleTargetAssignation(
     * sourceAgent, segmentAgent, new AssignationImpl<ConstrainedInteraction>(
     * PointDisplacementAggregableInteraction.getInstance()));
     * 
     * 
     **/

    return aggregateBuildingStructure;
  }

  public static PolylinearEnvironment createAggregateBuildingStructureEnvironment() {
    PolylinearEnvironment env = new PolylinearEnvironment();
    env.setEnvironmentType(getAggregateBuildingStructureEnvironmentType());
    return env;
  }

  private static EnvironmentType segmentEnvironment = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getSegmentEnvironmentType() {

    if (segmentEnvironment == null) {
      segmentEnvironment = new EnvironmentType();
      segmentEnvironment.setEnvironmentTypeName("Segment");
    }

    return segmentEnvironment;
  }

  public static LinearEnvironment createSegmentEnvironment() {
    LinearEnvironment env = new LinearEnvironment();
    env.setEnvironmentType(getSegmentEnvironmentType());
    return env;
  }

  private static EnvironmentType borderEnvironment = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getBorderEnvironmentType() {

    if (borderEnvironment == null) {
      borderEnvironment = new EnvironmentType();
      borderEnvironment.setEnvironmentTypeName("Border");
    }

    return borderEnvironment;
  }

  public static PolylinearEnvironment createBorderEnvironment() {
    PolylinearEnvironment env = new PolylinearEnvironment();
    env.setEnvironmentType(getBorderEnvironmentType());
    return env;
  }

  private class CouplePoint {

    private IPointAgent begin;
    private IPointAgent end;

    public IPointAgent getBegin() {
      return begin;
    }

    public IPointAgent getEnd() {
      return end;
    }

    // public CouplePoint(IPointAgent begin, IPointAgent end) {
    // this.begin = begin;
    // this.end = end;
    // }

    public CouplePoint(GAELSegmentGeneObj seg) {
      this.begin = seg.getSubMicro().getP1();
      this.end = seg.getSubMicro().getP2();
    }

    public boolean equals(Object o) {
      if (o == null) {
        return false;
      } else if (!(o instanceof CouplePoint)) {
        return false;
      } else {
        CouplePoint c = (CouplePoint) o;
        boolean res = this.getBegin().equals(c.getBegin())
            && this.getEnd().equals(c.getEnd());
        res = res || this.getBegin().equals(c.getEnd())
            && this.getEnd().equals(c.getBegin());
        return res;
      }
    }
  }

}
