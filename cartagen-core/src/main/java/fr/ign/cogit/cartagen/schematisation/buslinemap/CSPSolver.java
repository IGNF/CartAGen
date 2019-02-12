package fr.ign.cogit.cartagen.schematisation.buslinemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;
import utils.Pair;

/**
 * This class allows to launch a CSP algorithm (backtracking or simulated
 * annealing)
 * @author JTeulade-Denantes
 * 
 */
public class CSPSolver {

  private static Logger LOGGER = Logger.getLogger(CSPSolver.class.getName());

  /**
   * This function solves a CSP problem with 4 possible algorithms
   * @param data
   * @param algorithmChoice
   */
  public static void LaunchCSPSolver(
      Set<RoadStrokeForRoutes> selectedRouteStrokes,
      Collection<StrokeNode> routeStrokeNodes, int algorithmChoice) {

    // algorithmChoice == 2 means it s a backtracking with side constraints
    RoadStrokeForRoutes[] strokes = sortStrokes(selectedRouteStrokes,
        algorithmChoice == 2);
    // for the simulated annealing, you don't need to sort the variables
    // RoadStrokeForRoutes[] strokes = new RoadStrokeForRoutes[data
    // .getSelectedRouteStrokes().size()];
    // int j = 0;
    // for (RoadStrokeForRoutes stroke : data.getSelectedRouteStrokes()) {
    // strokes[j++] = stroke;
    // }

    // allow to compare a good and a bad sorting of the variables (1)
    // RoadStrokeForRoutes[] reverseStrokes = new
    // RoadStrokeForRoutes[strokes.length];
    // for (int i = 0 ; i < strokes.length ; i++) {
    // reverseStrokes[i] = strokes[strokes.length - i - 1];
    // }
    // this map links each node to its number of related strokes
    Map<StrokeNode, Integer> nodeOccurrences = new HashMap<StrokeNode, Integer>();
    for (StrokeNode node : routeStrokeNodes) {
      nodeOccurrences.put(node,
          node.getInStrokes().size() + node.getOutStrokes().size());
    }

    List<Integer> minConfigSolution = new ArrayList<>();
    LOGGER.debug("Let's go !");
    long timer = System.currentTimeMillis();
    double score = 0;
    double temperature = 20000;
    List<Integer> localMinConfigSolution = new ArrayList<>();
    double minScore = 1000;
    try {
      // we call the algorithm
      if (algorithmChoice == 0) {
        minScore = Backtracking.incrementalBacktracking(strokes,
            nodeOccurrences, minConfigSolution);
      } else if (algorithmChoice == 1) {
        minScore = minInitialization(strokes, new HashMap<>(nodeOccurrences),
            minConfigSolution);
        //
        // allow to compare a good and a bad sorting of the variables (2)
        // LOGGER.info("worst solution");
        // timer = System.currentTimeMillis();
        // minScore = Backtracking.thresholdBacktracking(reverseStrokes,
        // nodeOccurrences,
        // minConfigSolution, 10000);
        // LOGGER.info("time = " + (System.currentTimeMillis() - timer) / 1000);
        // LOGGER.info("Here below is the best strokes configuration with
        // minScore = "+
        // minScore);
        // for (int i = 0; i < minConfigSolution.size(); i++) {
        // LOGGER.info(reverseStrokes[i] + " -> " + minConfigSolution.get(i)
        // + " (" +
        // RoutesConfigurations.getConfiguration(reverseStrokes[i].getCarriedObjectsNumber(),
        // minConfigSolution.get(i)) + ")");
        // }
        //
        // minConfigSolution.clear();
        // nodeOccurrences.clear();
        // for (StrokeNode node : data.getRouteStrokeNodes()) {
        // nodeOccurrences.put(node, node.getInStrokes().size()
        // + node.getOutStrokes().size());
        // }
        // LOGGER.info("best solution");

        timer = System.currentTimeMillis();
        minScore = Backtracking.thresholdBacktracking(strokes, nodeOccurrences,
            minConfigSolution, 10000);
        LOGGER.info("time = " + (System.currentTimeMillis() - timer) / 1000);

      } else if (algorithmChoice == 2) {
        // allow to consider side constraints on the intersections.
        nodeOccurrences.clear();
        for (int j = 0; j < strokes.length; j++) {
          int nb = 1;
          if (nodeOccurrences.containsKey(strokes[j].getStrokeFinalNode())) {
            nb += nodeOccurrences.get(strokes[j].getStrokeFinalNode());
          }
          nodeOccurrences.put(strokes[j].getStrokeFinalNode(), nb);

          nb = 1;
          if (nodeOccurrences.containsKey(strokes[j].getStrokeInitialNode())) {
            nb += nodeOccurrences.get(strokes[j].getStrokeInitialNode());
          }
          nodeOccurrences.put(strokes[j].getStrokeInitialNode(), nb);

        }
        LOGGER.debug("nodeOccurences = " + nodeOccurrences);
        minScore = minInitialization(strokes, new HashMap<>(nodeOccurrences),
            minConfigSolution);
        minScore = Backtracking.thresholdBacktracking(strokes, nodeOccurrences,
            minConfigSolution, minScore);

      } else if (algorithmChoice == 3) {
        minScore = Backtracking.dichotomicBacktracking(strokes, nodeOccurrences,
            minConfigSolution);
      } else if (algorithmChoice == 4) {
        // this map allow to know how many times each stroke has been modified
        // by a transformation
        Map<RoadStrokeForRoutes, Integer> strokesChoices = new HashMap<RoadStrokeForRoutes, Integer>();
        for (int i = 0; i < strokes.length; i++) {
          strokesChoices.put(strokes[i], 0);
        }
        LOGGER.info("we find for each node, the minimum score related");
        // For a node, there are the min and the current energies
        Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies = instanciateMinNodeEnergy(
            routeStrokeNodes);

        // how many time do you want to launch the simulated annealing
        // algorithm?
        int iterations = 3;
        LOGGER.info("now, we will launch " + iterations
            + " times a simulated annealing .");
        for (int i = 0; i < iterations; i++) {
          for (Pair<Double, Double> pair : nodeMinAndCurrentEnergies.values()) {
            pair.set2((double) -1);
          }
          score = SimulatedAnnealing.launchSimulatedAnnealing(strokes,
              localMinConfigSolution, nodeMinAndCurrentEnergies, strokesChoices,
              temperature);
          LOGGER.info(
              "for the iteration number " + i + " we found a score = " + score);
          // we keep the best global result
          if (score < minScore) {
            minScore = score;
            minConfigSolution.clear();
            for (Integer position : localMinConfigSolution) {
              minConfigSolution.add(position);
            }
          }

          LOGGER.info("time = " + (System.currentTimeMillis() - timer) / 1000);
          timer = System.currentTimeMillis();
          temperature = 0;
        }
        // we want to see how many times each variable (stroke) has been
        // modified
        int totalChoices = 0;
        for (int nb : strokesChoices.values()) {
          totalChoices += nb;
        }
        LOGGER.info("Let's see strokes occurence average");
        for (RoadStrokeForRoutes stroke : strokesChoices.keySet()) {
          LOGGER.info(stroke + "(" + stroke.getCarriedObjectsNumber() + ")->\t"
              + Math.round(
                  ((double) 1000 * strokesChoices.get(stroke)) / totalChoices)
              + " ‰");
        }
      }
      // we print the solution
      LOGGER.info(" we finished the algorithm in "
          + (System.currentTimeMillis() - timer) / 1000
          + " seconds and with a score = " + minScore);
      for (int i = 0; i < strokes.length; i++) {
        strokes[i].setRouteConfiguration(minConfigSolution.get(i));
      }
      LOGGER.info("Here below is the best strokes configuration");
      for (int i = 0; i < minConfigSolution.size(); i++) {
        LOGGER.info(strokes[i] + " -> " + strokes[i].getRouteConfiguration()
            + " (" + strokes[i].getRoutesPositions() + ")");
      }

      // we add the solution in our map
      for (RoadStrokeForRoutes routeStroke : selectedRouteStrokes) {
        for (int i = 0; i < routeStroke.getCarriedObjectsNumber(); i++) {
          routeStroke.setRoadRelativePosition(
              routeStroke.getRoutesName().get(i),
              routeStroke.getRoutesPositions().get(i));
        }
      }

      LOGGER.info("SUCCESS");

    } catch (Exception e) {
      LOGGER.info(
          "FAILURE / timer = " + (System.currentTimeMillis() - timer) / 1000);
      LOGGER.info("minConfigSolution");
      for (int i = 0; i < minConfigSolution.size(); i++) {
        LOGGER.info(strokes[i] + " -> " + minConfigSolution.get(i) + " ("
            + RoutesConfigurations.getConfiguration(
                strokes[i].getCarriedObjectsNumber(), minConfigSolution.get(i))
            + ")");
      }
      throw (e);
    }
  }

  /**
   * This function orders the strokes according to the node complexity
   * @param selectedRouteStrokes
   * @param sideConstraints, boolean to know if we consider the side constraints
   *          on a node
   * @return the ordered array
   */
  private static RoadStrokeForRoutes[] sortStrokes(
      Set<RoadStrokeForRoutes> selectedRouteStrokes, boolean sideConstraints) {
    RoadStrokeForRoutes[] strokes = new RoadStrokeForRoutes[selectedRouteStrokes
        .size()];

    // This map gives for a routes number, all the nodes with this routes number
    Map<Integer, Set<StrokeNode>> routesNumberNodes = new HashMap<Integer, Set<StrokeNode>>();
    // for the selected route strokes
    for (RoadStrokeForRoutes stroke : selectedRouteStrokes) {
      // we identify the initial node
      StrokeNode node = stroke.getStrokeInitialNode();
      int routesNumberNode = 1;
      // for all the strokes related to this node, we evaluate the node
      // complexity
      for (Stroke nodeStroke : Iterables.concat(node.getInStrokes(),
          node.getOutStrokes())) {
        if (sideConstraints || selectedRouteStrokes.contains(nodeStroke)) {
          // we choose to multiply the strokes size configuration, good
          // approximation to the node complexity
          routesNumberNode *= RoutesConfigurations.getConfigurationsNumber(
              ((RoadStrokeForRoutes) nodeStroke).getCarriedObjectsNumber());
        } else {
          // otherwise, we don't care about this node because we can't access to
          // all its strokes
          routesNumberNode = 0;
          break;
        }
      }
      // we add the node to the map
      if (!routesNumberNodes.containsKey(routesNumberNode)) {
        routesNumberNodes.put(routesNumberNode, new HashSet<StrokeNode>());
      }
      routesNumberNodes.get(routesNumberNode).add(node);

      // idem for the final node
      node = stroke.getStrokeFinalNode();
      routesNumberNode = 1;
      for (Stroke nodeStroke : Iterables.concat(node.getInStrokes(),
          node.getOutStrokes())) {
        if (sideConstraints || selectedRouteStrokes.contains(nodeStroke)) {
          routesNumberNode *= RoutesConfigurations.getConfigurationsNumber(
              ((RoadStrokeForRoutes) nodeStroke).getCarriedObjectsNumber());
        } else {
          routesNumberNode = 0;
          break;
        }
      }
      if (!routesNumberNodes.containsKey(routesNumberNode)) {
        routesNumberNodes.put(routesNumberNode, new HashSet<StrokeNode>());
      }
      routesNumberNodes.get(routesNumberNode).add(node);
    }

    // this list reverses the key set to get the nodes from the most complex to
    // the least
    List<Integer> routesNumberNodesKeys = new ArrayList<Integer>(
        routesNumberNodes.keySet());
    Collections.sort(routesNumberNodesKeys, Collections.reverseOrder());

    // this list returns the ordered strokes
    List<RoadStrokeForRoutes> strokesAlreadySeen = new ArrayList<RoadStrokeForRoutes>();
    for (Integer key : routesNumberNodesKeys) {
      for (StrokeNode node : routesNumberNodes.get(key)) {
        for (Stroke stroke : Iterables.concat(node.getInStrokes(),
            node.getOutStrokes())) {
          if (selectedRouteStrokes.contains(stroke)
              && !strokesAlreadySeen.contains(stroke)) {
            strokesAlreadySeen.add((RoadStrokeForRoutes) stroke);
          }
        }
      }
    }

    for (int i = 0; i < strokesAlreadySeen.size(); i++) {
      strokes[i] = strokesAlreadySeen.get(i);
      LOGGER.info(i + " -> " + strokes[i] + " ("
          + strokes[i].getCarriedObjectsNumber() + ")");
    }

    return strokes;
  }

  /**
   * this function tries to find the best initialization of the minimum score
   * thanks to a stochastic algorithm
   * @param strokes, the variables of our CSP problem
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   * @param minConfigSolution related to the bbest score
   * @return the best score we found
   */
  private static double minInitialization(RoadStrokeForRoutes[] strokes,
      Map<StrokeNode, Integer> nodeOccurrences,
      List<Integer> minConfigSolution) {
    int count = 0;
    double score;
    double minScore = 10000;
    int i = 0;
    Map<StrokeNode, Integer> localNodeOccurrences = new HashMap<>(
        nodeOccurrences);
    // first allows to try the current configuration for the beginning
    boolean first = true;
    // we choose arbitrarily a threshold
    // 500000 is better
    while (count < 50000) {
      count++;
      if (i >= strokes.length) {
        score = strokes[strokes.length - 1].getStrokesScore();

        if (score < minScore) {
          minScore = score;
          minConfigSolution.clear();
          for (RoadStrokeForRoutes stroke : strokes) {
            minConfigSolution.add(stroke.getRouteConfiguration());
          }
        }
        if (first) {
          first = false;
          LOGGER.info("minScore = " + minScore + " et minNodeSolution = "
              + minConfigSolution);
        }
        // we restart everything
        i = 0;
        for (StrokeNode node : nodeOccurrences.keySet()) {
          localNodeOccurrences.put(node, nodeOccurrences.get(node));
        }
      } else if (strokes[i].getRoutesPositions() == null) {
        LOGGER.error("problem with i = " + i + " and getCarriedObjectsNumber = "
            + strokes[i].getCarriedObjectsNumber()
            + " and getRouteConfiguration =  "
            + strokes[i].getRouteConfiguration());
        break;
      } else {
        score = strokes[i].updateScores((i == 0) ? null : strokes[i - 1],
            localNodeOccurrences);
        if (score >= minScore) {
          // we restart everything
          i = -1;
          for (StrokeNode node : nodeOccurrences.keySet()) {
            localNodeOccurrences.put(node, nodeOccurrences.get(node));
          }
        }
        i++;
      }
      if (!first && i < strokes.length) {
        // we choose a random configuration every time except for the first time
        strokes[i].randomRouteConfiguration();
      }
    }
    LOGGER.info("minInitialization = " + minScore);
    return minScore;
  }

  /**
   * This function finds the minimum energy for each node
   * @param routeStrokeNodes, all the nodes
   * @return a map with the minimum energy for each node
   */
  public static Map<StrokeNode, Pair<Double, Double>> instanciateMinNodeEnergy(
      Collection<StrokeNode> routeStrokeNodes) {
    Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies = new HashMap<StrokeNode, Pair<Double, Double>>();
    RoadStrokeForRoutes[] strokes;
    double minScore, totalMinScore = 0;

    // for all the nodes
    for (StrokeNode strokeNode : routeStrokeNodes) {
      // we create the array related to the node
      strokes = new RoadStrokeForRoutes[strokeNode.getInStrokes().size()
          + strokeNode.getOutStrokes().size()];
      int i = 0;
      int routesNumber = 1;
      for (Stroke s : Iterables.concat(strokeNode.getInStrokes(),
          strokeNode.getOutStrokes())) {
        ((RoadStrokeForRoutes) s).resetRouteConfiguration();
        strokes[i++] = (RoadStrokeForRoutes) s;
        routesNumber *= RoutesConfigurations.getConfigurationsNumber(
            ((RoadStrokeForRoutes) s).getCarriedObjectsNumber());
      }

      i = 0;
      minScore = 10000;

      // FIXME Patch node too long to handle
      if (routesNumber == 12441600) {
        nodeMinAndCurrentEnergies.put(strokeNode,
            new Pair<Double, Double>((double) 2, (double) -1));
        totalMinScore += 2;
        LOGGER.info(strokeNode.getGeom() + ": " + routesNumber
            + " different solutions -> 2");
        continue;
      }
      // we use a backtracking algorithm on all the strokes
      while (minScore > 0 && strokes[0].getRoutesPositions() != null) {
        if (i >= strokes.length) {
          minScore = Math.min(minScore,
              ProblemConstraints.getNodeCrossingsScore(strokeNode)
                  + ProblemConstraints
                      .getNodeRelativePositionsScore(strokeNode));
          i = strokes.length - 1;
          strokes[i].nextRouteConfiguration();
        } else if (strokes[i].getRoutesPositions() == null) {
          strokes[i].resetRouteConfiguration();
          i--;
          strokes[i].nextRouteConfiguration();
        } else {
          i++;
        }
      }
      // we add the minScore we found
      nodeMinAndCurrentEnergies.put(strokeNode,
          new Pair<Double, Double>(minScore, (double) -1));
      totalMinScore += minScore;
      LOGGER.debug(strokeNode.getGeom() + ": " + routesNumber
          + " different solutions -> " + minScore);
    }
    LOGGER.info("totalMinScore = " + totalMinScore);
    return nodeMinAndCurrentEnergies;
  }

}
