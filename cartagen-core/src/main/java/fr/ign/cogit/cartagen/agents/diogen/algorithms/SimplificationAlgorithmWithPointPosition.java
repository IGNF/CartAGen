package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.PadawanUtil;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.environment.LinearEnvironment;
import fr.ign.cogit.cartagen.agents.diogen.environment.PolylinearEnvironment;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELSegmentGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.generalisation.simplification.PolygonSegment;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;

public class SimplificationAlgorithmWithPointPosition
    extends SimplificationAlgorithm {

  private static Logger logger = LogManager
      .getLogger(SimplificationAlgorithmWithPointPosition.class.getName());

  private static double SEUIL_COTES_PARRALLELES = 20 * Math.PI / 180;
  private static double SEUIL_COTES_ORTHOGONAUX = 20 * Math.PI / 180;

  /**
   * simplifie un polygone en supprimant les cotes trop courts
   * 
   * @param poly le polygone
   * @param seuil la longueur seuil
   * @return le polygone simplifie
   */
  public static IGeometry simplification(IPolygon poly, double seuil,
      IDiogenAgent agent, Set<IDiogenAgent> agents,
      AdjacentBuildingsDecomposition decomposition) {

    // Map<GAELSegmentGeneObj, SegmentSubmicroAgent> segmentsMap = decomposition
    // .getSegmentsMap();
    // Map<IDirectPosition, IPointAgent> pointMap = decomposition.getPointMap();

    IPolygon poly_ = (IPolygon) poly.clone();

    ArrayList<PolygonSegment> cps = PolygonSegment.getSmallest(poly_, seuil);

    PolylinearEnvironment env = (PolylinearEnvironment) agent
        .getEncapsulatedEnv();

    // essayer de supprimer des cotes trop courts tant qu'il y en a
    while (cps.size() > 0) {

      // recupere le cote le plus court de la liste des cotes trop court
      PolygonSegment cpPlusCourt = cps.get(0);
      for (PolygonSegment cPoly : cps) {
        if (cPoly.segment.length < cpPlusCourt.segment.length) {
          cpPlusCourt = cPoly;
        }
      }

      // enleve le cote de la liste
      cps.remove(cpPlusCourt);

      // tente suppression du cote
      resultatSuppressionCotePolygone res = SimplificationAlgorithm
          .suppressionCote(poly_, cpPlusCourt);

      // si la suppression a echoué, essayer avec un autre coté
      if (!res.ok) {
        continue;
      }

      // Add specific gestion for modification
      // FIXME it is better to manage this kind of adaptation in the
      // construction of the new geometry
      // detect the ring index of modification
      IRing initialRing;
      IRing modifiedRing;
      if (cpPlusCourt.ringIndex == -1) {
        initialRing = poly_.getExterior();
        modifiedRing = res.poly.getExterior();
      } else {
        initialRing = poly_.getInterior(cpPlusCourt.ringIndex);
        modifiedRing = res.poly.getInterior(cpPlusCourt.ringIndex);
      }
      // System.out.println("poly " + poly_.coord());
      // System.out.println("size poly " + poly_.coord().size());
      // System.out.println("ring index " + cpPlusCourt.ringIndex);
      // System.out.println("ring size " + initialRing.coord().size());
      // System.out.println("ring size " + modifiedRing.coord().size());

      // get the point before
      int index = cpPlusCourt.segment.index;
      logger.debug("Index " + index);
      // IDirectPosition unchangedPos1 = initialRing.coord().get(index);
      int adjust = initialRing.coord().size() - modifiedRing.coord().size() == 1
          ? 5 : 4;
      index = index > 2 ? index - 3
          : initialRing.coord().size() + (index - adjust);
      logger.debug("Adjusted index " + index);
      IDirectPosition firstUnchangedPos = initialRing.coord().get(index);
      List<IDirectPosition> initialList = new ArrayList<>();
      initialList.add(firstUnchangedPos);
      for (int i = 1; i <= 7; i++) {
        index++;
        if (index == initialRing.coord().size()) {
          index = 1;
        }
        initialList.add(initialRing.coord().get(index));
      }
      IDirectPosition lastUnchangedPos = initialRing.coord().get(index);
      List<IDirectPosition> modifiedList = new ArrayList<>();
      // identify the first unchanged point
      boolean needSecondRun = true;
      for (IDirectPosition pos : modifiedRing.coord()) {
        if (!modifiedList.isEmpty()) {
          modifiedList.add(pos);
          if (pos.equals(lastUnchangedPos)) {
            needSecondRun = false;
            break;
          }
        }
        if (pos.equals(firstUnchangedPos)) {
          modifiedList.add(pos);
        }
      }

      if (needSecondRun) {
        boolean first = true;
        for (IDirectPosition pos : modifiedRing.coord()) {
          if (first) {
            first = false;
            continue;
          }
          if (!modifiedList.isEmpty()) {
            modifiedList.add(pos);
          }
          if (pos.equals(lastUnchangedPos)) {
            break;
          }
        }
      }

      // logger.debug("begin " + env.containedPointAgents);
      logger.debug("initialRing.coord() " + initialRing.coord());
      logger.debug("modifiedRing.coord() " + modifiedRing.coord());
      if (initialList.size() - modifiedList.size() == 1) {
        logger.debug("One segment is remplaced by one point");
        // The two point of the removed segments (B and C) become one point
        IDirectPosition pointAi = initialList.get(2);
        IDirectPosition pointBi = initialList.get(3);
        IDirectPosition pointCi = initialList.get(4);
        IDirectPosition pointDi = initialList.get(5);

        IDirectPosition pointAf = modifiedList.get(2);
        IDirectPosition pointMf = modifiedList.get(3);
        IDirectPosition pointDf = modifiedList.get(4);

        // get the agents
        IPointAgent aA = env.getEdgePointAgentWithCurrentPosition(pointAi);
        IPointAgent aB = env.getEdgePointAgentWithCurrentPosition(pointBi);
        IPointAgent aC = env.getEdgePointAgentWithCurrentPosition(pointCi);
        IPointAgent aD = env.getEdgePointAgentWithCurrentPosition(pointDi);

        logger.debug("get the agent of A : " + aA + ", position : " + pointAi);
        logger.debug("get the agent of B : " + aB + ", position : " + pointBi);
        logger.debug("get the agent of C : " + aC + ", position : " + pointCi);
        logger.debug("get the agent of D : " + aD + ", position : " + pointDi);

        // update the new position of A

        aA.updatePosition(pointAf);
        env.updateLocalPosition(aA);
        logger.debug("update of A : " + aA + ", position : " + pointAf);
        // updateAgentCoord(aA, pointAi, pointAf);
        // aA.updatePosition(pointAf);
        // pointMap.remove(pointAi);
        // pointMap.put(pointAf, aA);

        // update the position of the points on the segment before A
        updatePositions(
            env.getEdgePointAgentWithCurrentPosition(initialList.get(1)), aA,
            env);

        // update the new position of D
        aD.updatePosition(pointDf);
        env.updateLocalPosition(aD);
        logger.debug("update of D : " + aD + ", position : " + pointDf);

        // updateAgentCoord(aD, pointDi, pointDf);
        // aD.updatePosition(pointDf);
        // pointMap.remove(pointDi);
        // pointMap.put(pointDf, aD);

        // update the position of the points on the segment after D
        updatePositions(aD,
            env.getEdgePointAgentWithCurrentPosition(initialList.get(6)), env);

        // Create the new point
        // TODO add the agents in a scheduler
        IPointAgent aM = new GeographicPointAgent(pointMf);
        aM.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        env.addContainedAgents((IDiogenAgent) aM);

        logger.debug(
            "create new agent for M : " + aM + ", position : " + pointMf);

        // decomposition.getPointAgentFromDirectPosition(pointMf);

        // IPointAgent toReturn = env.getAgentWithPosition(pos);
        // if (toReturn == null) {
        // toReturn = pointMap.get(pos);
        // if (toReturn == null) {
        // toReturn = new GeographicPointAgent(pos);
        // toReturn.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        // // toReturn.addContainingEnvironments(env);
        // pointMap.put(pos, toReturn);
        // }
        // env.addContainedAgents(toReturn);
        // }
        // return toReturn;

        // Create the new segment before the new point
        // System.out.println(env.containedPointAgents);
        logger.debug("create segment AM");
        GAELSegmentGeneObj segmentAM = new GAELSegmentGeneObj(aA, aM);
        SegmentSubmicroAgent segmentAMAgent = new SegmentSubmicroAgent(
            segmentAM);
        LinearEnvironment envAM = AdjacentBuildingsDecomposition
            .createSegmentEnvironment();
        segmentAMAgent.setEncapsulatedEnv(envAM);
        segmentAMAgent.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        env.addContainedAgents(segmentAMAgent, true);

        envAM.addContainedAgentsWithCoordinate((IDiogenAgent) aA, 0);
        envAM.addContainedAgentsWithCoordinate((IDiogenAgent) aM, 1);

        // Add all AB agents in AM

        // System.out.println(env.containedPointAgents);
        logger.debug("Move AB to AM");

        // GAELSegmentGeneObj segmentAB = new GAELSegmentGeneObj(aA, aB);
        // System.out.println(env.containedPointAgents);
        SegmentSubmicroAgent segmentABAgent = env
            .getSegmentAgentWithExtremities(aA, aB);
        // System.out.println(env.containedPointAgents);
        LinearEnvironment envAB = (LinearEnvironment) segmentABAgent
            .getEncapsulatedEnv();
        // System.out.println(env.containedPointAgents);

        // for (IAgent a : envAB.getContainedAgents()) {
        // if (!(a instanceof IPointAgent)) {
        // continue;
        // }
        // envAM
        // .addContainedAgentsWithCoordinate(a, envAB.getAgentCoordinate(a));
        // }
        addContentFromAnEnvironmentToAnother(envAB, envAM, 0, 1, env);
        // System.out.println(env.containedPointAgents);
        env.removeContainedAgent(segmentABAgent);
        // System.out.println(env.containedPointAgents);
        agents.remove(segmentABAgent);

        // System.out.println(env.containedPointAgents);
        // create the new segment after the new point
        logger.debug("create segment MD");
        GAELSegmentGeneObj segmentMD = new GAELSegmentGeneObj(aM, aD);
        SegmentSubmicroAgent segmentMDAgent = new SegmentSubmicroAgent(
            segmentMD);
        LinearEnvironment envMD = AdjacentBuildingsDecomposition
            .createSegmentEnvironment();
        segmentMDAgent.setEncapsulatedEnv(envMD);
        segmentMDAgent.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        env.addContainedAgents(segmentMDAgent, true);
        //
        // SegmentSubmicroAgent segmentMDAgent = env
        // .getSegmentAgentWithExtremities(aM, aD);
        //
        // env.addContainedAgents(segmentMDAgent);
        // // segmentAMAgent.addContainingEnvironments(env);
        // LinearEnvironment envMD = (LinearEnvironment) segmentMDAgent
        // .getEncapsulatedEnv();
        envAM.addContainedAgentsWithCoordinate((IDiogenAgent) aA, 0);
        envAM.addContainedAgentsWithCoordinate((IDiogenAgent) aM, 1);

        // Add all CD agents in MD

        // logger.debug(env.containedPointAgents);
        logger.debug("Move CD to MD");
        for (SegmentSubmicroAgent ssa : env.getAllSegmentAgents()) {
          logger.debug("segment agent " + ssa);
        }
        logger.debug("ac " + aC);
        logger.debug("ad " + aD);
        SegmentSubmicroAgent segmentCDAgent = env
            .getSegmentAgentWithExtremities(aC, aD);
        LinearEnvironment envCD = (LinearEnvironment) segmentCDAgent
            .getEncapsulatedEnv();

        // addContentFromAnEnvironmentToAnother(envCD, envMD, 0, );
        // for (IAgent a : envCD.getContainedAgents()) {
        // if (!(a instanceof IPointAgent)) {
        // continue;
        // }
        // envMD
        // .addContainedAgentsWithCoordinate(a, envCD.getAgentCoordinate(a));
        // }

        addContentFromAnEnvironmentToAnother(envCD, envMD, 0, 1, env);
        env.removeContainedAgent(segmentCDAgent);
        agents.remove(segmentCDAgent);

        // move all point of the suppressed segment on the two new segment

        // System.out.println(env.containedPointAgents);
        logger.debug("Move BC to AM and MD");
        SegmentSubmicroAgent segmentBCAgent = env
            .getSegmentAgentWithExtremities(aB, aC);
        LinearEnvironment envBC = (LinearEnvironment) segmentBCAgent
            .getEncapsulatedEnv();
        // Add all BC agents in AM, with abscisse of 1 and in MD, with abscisse
        // of 0
        for (IAgent a : envBC.getContainedAgents()) {
          if (!(a instanceof IPointAgent)) {
            continue;
          }
          envAM.addContainedAgentsWithCoordinate((IDiogenAgent) a, 1);
          envMD.addContainedAgentsWithCoordinate((IDiogenAgent) a, 0);
          env.updateLocalPosition((IPointAgent) a);
        }
        env.removeContainedAgent(segmentBCAgent);
        agents.remove(segmentBCAgent);

        env.setIsEdge(aB, false);
        env.setIsEdge(aC, false);

      } else if (initialList.size() - modifiedList.size() == 2) {

        logger.debug("One segment is suppressed");
        // The two point of the removed segments (B and C) are added to the new
        // segment between the two points for the new segment (A and B)
        IDirectPosition pointAi = initialList.get(2);
        IDirectPosition pointBi = initialList.get(3);
        IDirectPosition pointCi = initialList.get(4);
        IDirectPosition pointDi = initialList.get(5);

        IDirectPosition pointAf = modifiedList.get(2);
        IDirectPosition pointDf = modifiedList.get(3);

        // get the agents
        IPointAgent aA = env.getEdgePointAgentWithCurrentPosition(pointAi);
        IPointAgent aB = env.getEdgePointAgentWithCurrentPosition(pointBi);
        IPointAgent aC = env.getEdgePointAgentWithCurrentPosition(pointCi);
        IPointAgent aD = env.getEdgePointAgentWithCurrentPosition(pointDi);
        logger.debug("get the agent of A : " + aA + ", position : " + pointAi);
        logger.debug("get the agent of B : " + aB + ", position : " + pointBi);
        logger.debug("get the agent of C : " + aC + ", position : " + pointCi);
        logger.debug("get the agent of D : " + aD + ", position : " + pointDi);
        // update the new position of A
        aA.updatePosition(pointAf);
        env.updateLocalPosition(aA);
        // updateAgentCoord(aA, pointAi, pointAf);
        // aA.updatePosition(pointAf);
        // pointMap.remove(pointAi);
        // pointMap.put(pointAf, aA);
        logger.debug("update of A : " + aA + ", position : " + pointAf);

        logger.debug("get segment between A and the point before A : "
            + env.getEdgePointAgentWithCurrentPosition(initialList.get(1))
            + ", for position : " + initialList.get(1));
        // update the position of the points on the segment before A
        updatePositions(
            env.getEdgePointAgentWithCurrentPosition(initialList.get(1)), aA,
            env);

        // update the new position of D
        aD.updatePosition(pointDf);
        env.updateLocalPosition(aD);
        // updateAgentCoord(aD, pointDi, pointDf);
        // aD.updatePosition(pointDf);
        // pointMap.remove(pointDi);
        // pointMap.put(pointDf, aD);
        logger.debug("update of D : " + aD + ", position : " + pointDf);

        // update the position of the points on the segment after D
        updatePositions(aD,
            env.getEdgePointAgentWithCurrentPosition(initialList.get(6)), env);

        // Create the new segment
        // TODO add the agents in a scheduler
        GAELSegmentGeneObj segment = new GAELSegmentGeneObj(aA, aD);
        SegmentSubmicroAgent segmentAgent = new SegmentSubmicroAgent(segment);
        LinearEnvironment envAD = AdjacentBuildingsDecomposition
            .createSegmentEnvironment();
        segmentAgent.setEncapsulatedEnv(envAD);
        segmentAgent.setLifeCycle(PadawanUtil.getLIFE_CYCLE());
        env.addContainedAgents(segmentAgent, true);

        segmentAgent.addContainingEnvironments(env);
        // agent.getEncapsulatedEnv().addContainedAgents(segmentAgent);
        // segmentAgent.addContainingEnvironments(agent.getEncapsulatedEnv());
        // LinearEnvironment envAD = (LinearEnvironment) segmentAgent
        // .getEncapsulatedEnv();
        envAD.addContainedAgentsWithCoordinate((IDiogenAgent) aA, 0);
        envAD.addContainedAgentsWithCoordinate((IDiogenAgent) aD, 1);

        // positioning points B and C on the new segment, projecting them on the
        // new segment
        IDirectPosition pointBf = Operateurs.projection(pointBi, pointAf,
            pointDf);
        IDirectPosition pointCf = Operateurs.projection(pointCi, pointAf,
            pointDf);

        aB.updatePosition(pointBf);
        env.updateLocalPosition(aB);
        // updateAgentCoord(aB, pointBi, pointBf);
        // aB.updatePosition(pointBf);
        // pointMap.remove(pointBi);
        // pointMap.put(pointBf, aB);
        logger.debug("update of B : " + aB + ", position : " + pointBf);
        double abscB = pointAf.distance(pointBf) / pointAf.distance(pointDf);
        envAD.addContainedAgentsWithCoordinate((IDiogenAgent) aB, abscB);

        aC.updatePosition(pointCf);
        env.updateLocalPosition(aC);
        // updateAgentCoord(aC, pointCi, pointCf);
        // aC.updatePosition(pointCf);
        // pointMap.remove(pointCi);
        // pointMap.put(pointCf, aC);
        logger.debug("update of C : " + aC + ", position : " + pointCf);
        double abscC = pointAf.distance(pointCf) / pointAf.distance(pointDf);
        envAD.addContainedAgentsWithCoordinate((IDiogenAgent) aC, abscC);

        // agents.add(segmentAgent);
        // decomposition.

        // suppress segments , and add the supported agent in the new one
        // GAELSegmentGeneObj segmentAB = new GAELSegmentGeneObj(aA, aB);
        SegmentSubmicroAgent segmentABAgent = env
            .getSegmentAgentWithExtremities(aA, aB);
        LinearEnvironment envAB = (LinearEnvironment) segmentABAgent
            .getEncapsulatedEnv();
        addContentFromAnEnvironmentToAnother(envAB, envAD, 0, abscB, env);
        env.removeContainedAgent(segmentABAgent);
        // segmentsMap.remove(segmentAB);
        agents.remove(segmentABAgent);
        // envAB.removeAllContainedAgents();

        // GAELSegmentGeneObj segmentBC = new GAELSegmentGeneObj(aB, aC);
        SegmentSubmicroAgent segmentBCAgent = env
            .getSegmentAgentWithExtremities(aB, aC);
        LinearEnvironment envBC = (LinearEnvironment) segmentBCAgent
            .getEncapsulatedEnv();
        addContentFromAnEnvironmentToAnother(envBC, envAD, abscB, abscC, env);
        env.removeContainedAgent(segmentBCAgent);
        // segmentsMap.remove(segmentBC);
        agents.remove(segmentBCAgent);
        // envBC.removeAllContainedAgents();

        // GAELSegmentGeneObj segmentCD = new GAELSegmentGeneObj(aC, aD);
        SegmentSubmicroAgent segmentCDAgent = env
            .getSegmentAgentWithExtremities(aC, aD);
        LinearEnvironment envCD = (LinearEnvironment) segmentCDAgent
            .getEncapsulatedEnv();
        addContentFromAnEnvironmentToAnother(envCD, envAD, abscC, 1, env);
        env.removeContainedAgent(segmentCDAgent);
        // segmentsMap.remove(segmentCD);
        agents.remove(segmentCDAgent);
        // envCD.removeAllContainedAgents();

        env.setIsEdge(aB, false);
        env.setIsEdge(aC, false);

      }

      // logger.debug("end " + env.containedPointAgents);

      // System.out.println(initialList);
      // System.out.println(modifiedList);
      //
      // System.out.println(initialList.get(0).equals(modifiedList.get(0)));
      // System.out.println(initialList.get(1).equals(modifiedList.get(1)));
      //
      // System.out.println(initialList.get(initialList.size() - 2).equals(
      // modifiedList.get(modifiedList.size() - 2)));
      // System.out.println(initialList.get(initialList.size() - 1).equals(
      // modifiedList.get(modifiedList.size() - 1)));
      //
      // System.out.println("size before " + initialList.size());
      // System.out.println("size after " + modifiedList.size());
      // suppression a reussi: continuer avec le resultat obtenu
      poly_ = res.poly;
      cps = PolygonSegment.getSmallest(poly_, seuil);
    }

    // System.out.println("poly_ " + poly_);
    // System.out.println("poly_ exterior " + poly_.getExterior());
    // System.out.println("poly_ interior " + poly_.getInterior());
    return poly_;
  }

  // private static void updateAgentCoord(IPointAgent agent,
  // IDirectPosition oldPos, IDirectPosition newPos) {
  // // System.out.println("");
  // agent.updatePosition(newPos);
  // // pointMap.remove(oldPos);
  // // pointMap.put(newPos, agent);
  // }

  private static void updatePositions(IPointAgent a, IPointAgent b,
      PolylinearEnvironment buildingEnv) {
    SegmentSubmicroAgent segmentAgent = buildingEnv
        .getSegmentAgentWithExtremities(a, b);
    LinearEnvironment env = (LinearEnvironment) segmentAgent
        .getEncapsulatedEnv();
    env.updateAllPositions();
    // update position of the agents in the building environment
    for (IAgent agent : env.getContainedAgents()) {
      if (agent instanceof IPointAgent) {
        buildingEnv.updateLocalPosition((IPointAgent) agent);
        logger.debug("update position of agent on a segment : " + agent
            + ", position : " + ((IPointAgent) agent).getPosition());
      }
    }
  }

  private static void addContentFromAnEnvironmentToAnother(
      LinearEnvironment originEnv, LinearEnvironment destEnv,
      double destEnvBegin, double destEnvEnd,
      PolylinearEnvironment buildingEnv) {
    for (IAgent a : originEnv.getContainedAgents()) {
      if (!(a instanceof IPointAgent)) {
        continue;
      }
      logger.debug("Move agent " + a + " from " + originEnv + " (coord="
          + originEnv.getAgentCoordinate(a) + ") to " + destEnv + " (coord="
          + destEnvBegin
          + originEnv.getAgentCoordinate(a) * (destEnvEnd - destEnvBegin)
          + ")");
      destEnv.addContainedAgentsWithCoordinate((IDiogenAgent) a, destEnvBegin
          + originEnv.getAgentCoordinate(a) * (destEnvEnd - destEnvBegin));
      buildingEnv.updateLocalPosition((IPointAgent) a);
    }
  }

  // private static void addContentFromAnEnvironmentToAnother(
  // LinearEnvironment originEnv, LinearEnvironment destEnv,
  // IDirectPosition pointAf, IDirectPosition pointDf) {
  //
  // for (IAgent a : originEnv.getContainedAgents()) {
  // if (!(a instanceof IPointAgent)) {
  // continue;
  // }
  // IDirectPosition proj = Operateurs.projection(
  // ((IPointAgent) a).getPosition(), pointAf, pointDf);
  // double absc = pointAf.distance(proj) / pointAf.distance(pointDf);
  // if (absc < 0) {
  // absc = 0;
  // }
  // if (absc > 1) {
  // absc = 1;
  // }
  // destEnv.addContainedAgentsWithCoordinate(a, absc);
  // }
  //
  // }

  /**
   * public static IGeometry simplification(IPolygon poly, double seuil, IAgent
   * agent, Map<IDirectPosition, IPointAgent> pointMap, Map<GAELSegmentGeneObj,
   * SegmentSubmicroAgent> segmentsMap, AdjacentBuildingsDecomposition
   * decomposition) {
   * 
   * IGeometry resPoly = SimplificationAlgorithm.simplification(poly, seuil);
   * 
   * CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
   * .addFeatureToGeometryPool(resPoly, Color.BLUE, 2);
   * 
   * IDirectPosition previousInitialPoint = null; int iModulo = 0; int k = 0;
   * for (int i = 0; i < poly.coord().size(); i++) {
   * 
   * System.out.println("iteration " + i);
   * 
   * iModulo = i % poly.coord().size(); IDirectPosition initialP =
   * poly.coord().get(iModulo); IDirectPosition finalP =
   * resPoly.coord().get(iModulo + k);
   * 
   * System.out.println("Point geom init " + initialP); System.out.println(
   * "Point geom init " + finalP);
   * 
   * // test if a point change // assume that the initials (and final) points of
   * the polygon are the // same if (finalP.equals(initialP)) {
   * previousInitialPoint = initialP; continue; }
   * 
   * // if the first point is not the same, maybe the simplification implies //
   * points from the end if (previousInitialPoint == null) {
   * previousInitialPoint = initialP; k--; continue; }
   * 
   * // List<IAgent> agents = new ArrayList<>(); List
   * <Double> distancesFromPrevious = new ArrayList<>();
   * 
   * double distance = 0.;
   * 
   * IPointAgent firstPointAgent = decomposition
   * .getPointAgentFromDirectPosition(previousInitialPoint); while
   * (!finalP.equals(initialP) && i <= 2 * poly.coord().size()) { // this point
   * needs to be put on another segment, with specific // coordinates on this
   * segment IPointAgent a1 = pointMap.get(initialP); IPointAgent a2 =
   * pointMap.get(previousInitialPoint); // agents.add(a1); distance +=
   * initialP.distance2D(previousInitialPoint);
   * distancesFromPrevious.add(distance); GAELSegmentGeneObj segmentToRemove =
   * new GAELSegmentGeneObj(a2, a1); SegmentSubmicroAgent segmentAgent =
   * decomposition .getSegmentAgent(segmentToRemove); if (segmentAgent != null)
   * { segmentAgent.getEncapsulatedEnv().removeContainedAgents(a1);
   * segmentAgent.getEncapsulatedEnv().removeContainedAgents(a2);
   * segmentAgent.setSuppressed(true);
   * agent.getEncapsulatedEnv().removeContainedAgents(segmentAgent); //
   * agents.remove(segmentAgent); } else { System.out.println(
   * "Error, segment not previously agentified."); } i++; iModulo = i %
   * poly.coord().size(); k--; previousInitialPoint = initialP; initialP =
   * poly.coord().get(iModulo); System.out.println("Point geom init " +
   * initialP); }
   * 
   * distance += initialP.distance2D(previousInitialPoint);
   * 
   * List<Double> coordinates = new ArrayList<>(); for (Double d :
   * distancesFromPrevious) { coordinates.add(d / distance); }
   * 
   * IPointAgent endPointAgent = decomposition
   * .getPointAgentFromDirectPosition(finalP);
   * 
   * // create the new segment GAELSegmentGeneObj segment = new
   * GAELSegmentGeneObj(firstPointAgent, endPointAgent); SegmentSubmicroAgent
   * segmentAgent = decomposition .getSegmentAgent(segment);
   * segmentAgent.addContainingEnvironments(agent.getEncapsulatedEnv());
   * segmentAgent.getEncapsulatedEnv().addContainedAgents(firstPointAgent);
   * segmentAgent.getEncapsulatedEnv().addContainedAgents(endPointAgent); //
   * agents.add(segmentAgent);
   * 
   * // remove the points from previous existing segment, and suppress these //
   * segments
   * 
   * // Now change the agent of environment. Remove them from their //
   * environment, and add them to the new segment environment, with //
   * coordinates
   * 
   * }
   * 
   * return resPoly; }
   **/
}
