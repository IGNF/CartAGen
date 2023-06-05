package fr.ign.cogit.cartagen.schematisation.buslinemap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Pair;

import com.google.common.collect.Iterables;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;

/**
 * This class provides a simulated annealing algorithm
 * @author JTeulade-Denantes
 * 
 */
public class SimulatedAnnealing {
  private static Logger LOGGER = LogManager.getLogger(SimulatedAnnealing.class
      .getName());

  /**
   * This function launches the simulated annealing
   * @param nodeMinEnergies
   * @param strokes, the variables
   * @param minConfigSolution, the best solution configuration
   * @return the local best energy
   * @throws Exception 
   */
  public static double launchSimulatedAnnealing(RoadStrokeForRoutes[] strokes,
      List<Integer> minConfigSolution,
      Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies,
      Map<RoadStrokeForRoutes, Integer> strokesChoices, double temperature) {

    for (StrokeNode a : nodeMinAndCurrentEnergies.keySet()) {
      if (nodeMinAndCurrentEnergies.get(a).first() == 10000)
        LOGGER.error("problem with the node = " + a);
      if (nodeMinAndCurrentEnergies.get(a).second() != -1)
        LOGGER.error("problem with the node = " + a);
    }

    // we instantiate a solution of the system
    double initialEnergy = instantiateSolution(strokes,
        nodeMinAndCurrentEnergies);

    double energy = initialEnergy;
    int count = 0, stableCount = 0;
    double localEnergy = 0;
    Map<StrokeNode, Pair<Double, Double>> localnodeEnergies = new HashMap<StrokeNode, Pair<Double, Double>>();
    // parameters we have to adjust
    double minEnergy = 100000;
//    double temperature = 1000000;
    double alpha = 0.99999;
    Integer[] precStrokesConfig = new Integer[strokes.length];

    while (true) {
      // we can print the system state, -1 means we don't print anything
      if (count % 400000 == -1) {
        LOGGER.info("\nenergy = " + energy + " et temperature = "
            + temperature + " et minEnergy = " + minEnergy
            + " et stableCount = " + stableCount);
        if (!minConfigSolution.isEmpty()) {
          String s ="strokes : ";
          for (int j = 0; j < strokes.length; j++) {
            s += " + "
                + minConfigSolution.get(j)
                + " ("
                + RoutesConfigurations.getConfiguration(
                    strokes[j].getCarriedObjectsNumber(),
                    minConfigSolution.get(j)) + ")";
          }
          LOGGER.info(s);
        }
      }

      // we don't improve the energy since 150000 iterations, we check if we are
      // in a local minimum
      if (stableCount > 150000) {
        LOGGER.debug("are we in a local minima?");
        localEnergy = isLocalMinimum(strokes, energy, nodeMinAndCurrentEnergies);
        if (localEnergy > 0) {
          // it means we found a better energy
          energy = localEnergy;
          stableCount = 0;
        } else {
          // it's a local minimum, we finished the algorithm
          break;
        }
      }
      // System.out.println(" energy = " + energy + " et temperature = " +
      // temperature + " et minEnergy = " + minEnergy );

      // we save the current routes configuration if we want to backtrack
      for (int i = 0; i < strokes.length; i++) {
        precStrokesConfig[i] = strokes[i].getRouteConfiguration();
      }
      for (StrokeNode strokeNode : nodeMinAndCurrentEnergies.keySet()) {
        localnodeEnergies.put(strokeNode, new Pair<Double, Double>(
            nodeMinAndCurrentEnergies.get(strokeNode).first(),
            nodeMinAndCurrentEnergies.get(strokeNode).second()));
      }


      // we disturb the system
      localEnergy = disturb(energy, localnodeEnergies, strokesChoices);

      // if we found a better energy or if we choose randomly to keep this
      // configuration
      if (localEnergy < energy
          || Math.random() < Math.exp((energy - localEnergy) / temperature)) {
        // we reset the stable count
        if (Math.abs(energy - localEnergy) > 0.0001) {
          stableCount = 0;
        }
        // we modify energy and nodeEnergies
        energy = localEnergy;
        // nodeMinAndCurrentEnergies = localnodeEnergies;
        for (StrokeNode strokeNode : localnodeEnergies.keySet()) {
          nodeMinAndCurrentEnergies.put(strokeNode,
              new Pair<Double, Double>(localnodeEnergies.get(strokeNode)
                  .first(), localnodeEnergies.get(strokeNode).second()));
        }
        // nodeMinAndCurrentEnergies = localnodeEnergies;
        if (energy < minEnergy) {
          minEnergy = energy;
          minConfigSolution.clear();
          for (RoadStrokeForRoutes stroke : strokes) {
            minConfigSolution.add(stroke.getRouteConfiguration());
          }
        }

      } else {
        // otherwise, we restart from the previous configuration
        for (int i = 0; i < strokes.length; i++) {
          strokes[i].setRouteConfiguration(precStrokesConfig[i]);
        }
      }
      // just to check nodeMinAndCurrentEnergies components
      // test = 0;
      // for (StrokeNode node : nodeMinAndCurrentEnergies.keySet()) {
      // if (node == null) {
      // continue;
      // }
      // test += nodeMinAndCurrentEnergies.get(node).second();
      // }
      // if (Math.abs(test - nodeMinAndCurrentEnergies.get(null).second()) >
      // 0.0001) {
      // System.out.println("sniff car test = " + test + " et total = " +
      // nodeMinAndCurrentEnergies.get(null).second());
      // }
      
      // we modify the temperature
      temperature *= alpha;
      count++;
      stableCount++;
    }
    LOGGER.debug("count = " + count);
    return minEnergy;
  }

  /**
   * This function instantiates a initial solution
   * @param nodeMinEnergies
   * @param strokes, the initial solution
   * @param nodeEnergies, the map between the nodes and their score
   * @return the energy related to the instantiation
   */
  private static double instantiateSolution(RoadStrokeForRoutes[] strokes,
      Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies) {
    double energy = 0;
    double nodeEnergy;
    StrokeNode strokeNode;

    // we randomly pick a configuration for each stroke
    for (int i = 0; i < strokes.length; i++) {
      strokes[i].randomRouteConfiguration();
    }

    // we instantiate nodeEnergies map
    for (int i = 0; i < strokes.length; i++) {
      strokeNode = strokes[i].getStrokeInitialNode();
      if (nodeMinAndCurrentEnergies.get(strokeNode).second() < 0) {
        nodeEnergy = ProblemConstraints.getNodeCrossingsScore(strokeNode)
            + ProblemConstraints.getNodeRelativePositionsScore(strokeNode)
            - nodeMinAndCurrentEnergies.get(strokeNode).first();
        nodeMinAndCurrentEnergies.get(strokeNode).set2(nodeEnergy);
        energy += nodeEnergy;
      }
      strokeNode = strokes[i].getStrokeFinalNode();
      if (nodeMinAndCurrentEnergies.get(strokeNode).second() < 0) {
        nodeEnergy = ProblemConstraints.getNodeCrossingsScore(strokeNode)
            + ProblemConstraints.getNodeRelativePositionsScore(strokeNode)
            - nodeMinAndCurrentEnergies.get(strokeNode).first();
        nodeMinAndCurrentEnergies.get(strokeNode).set2(nodeEnergy);
        energy += nodeEnergy;
      }
    }

    // we put the total energy of all the nodes in the map with the null key
    nodeMinAndCurrentEnergies.put(null,
        new Pair<Double, Double>(energy, energy));

    // don't forget the internal energies!
    for (int i = 0; i < strokes.length; i++) {
      energy += ProblemConstraints.getInternalCrossedScore(strokes[i]);
    }

    return energy;
  }

  /**
   * This function checks if the current routes configuration is a local minimum
   * by checking all the other route configuration
   * @param strokes, with the configuration we want to check
   * @param energyMin, the current energy which can be the optimal energy
   * @param nodeEnergies, the map between the nodes and their score
   * @return a better energy if the current routes configuration is not a local
   *         minimum, -1 otherwise
   */
  private static double isLocalMinimum(RoadStrokeForRoutes[] strokes,
      double energyMin,
      Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies) {
    int configurationIndex;
    double initialEnergy, finalEnergy, nodeDifference, energy;

    // we check all the routes
    for (RoadStrokeForRoutes stroke : strokes) {
      // we save the configuration index
      configurationIndex = stroke.getRouteConfiguration();
      // we start from an energy without the current stroke
      energy = energyMin - ProblemConstraints.getInternalCrossedScore(stroke);
      // we reset the route configuration
      stroke.resetRouteConfiguration();

      // we check all the configurations
      while (stroke.getRoutesPositions() != null) {
        initialEnergy = ProblemConstraints.getNodeCrossingsScore(stroke
            .getStrokeInitialNode())
            + ProblemConstraints.getNodeRelativePositionsScore(stroke
                .getStrokeInitialNode())
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode())
                .first();
        finalEnergy = ProblemConstraints.getNodeCrossingsScore(stroke
            .getStrokeFinalNode())
            + ProblemConstraints.getNodeRelativePositionsScore(stroke
                .getStrokeFinalNode())
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode())
                .first();
        // nodeDifference is the the difference between the current node
        // energies and the previous ones
        nodeDifference = initialEnergy
            + finalEnergy
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode())
                .second()
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode())
                .second();
        // we calculate the new energy
        energy += nodeDifference
            + ProblemConstraints.getInternalCrossedScore(stroke);

        // is the new energy better?
        if (energy + 0.0001 < energyMin) {
          // if so, we add the changes in nodeEnergies map, and we return the
          // new energy
          nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode()).set2(
              finalEnergy);
          nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode()).set2(
              initialEnergy);
          nodeMinAndCurrentEnergies.get(null).set2(
              nodeMinAndCurrentEnergies.get(null).second() + nodeDifference);
          LOGGER.debug("we found a better energy (" + energy
              + ") thanks to the stroke " + stroke);
          return energy;
        }

        // otherwise, we try the next configuration
        stroke.nextRouteConfiguration();
      }

      // we didn't find a better configuration for this stroke
      // so, we put it back as before and continue to the next stroke
      stroke.setRouteConfiguration(configurationIndex);
    }
    LOGGER.debug("we didn't find a better solution -> it's a local minimum");
    return -1;
  }

  /**
   * This function chooses a stroke according to its node complexity and
   * randomly picks a new configuration
   * @param energy, the current energy of the system
   * @param nodeEnergies, the map between the nodes and their score
   * @return the new energy after the disturbance
   * @throws Exception 
   */
  private static double disturb(double energyParam,
      Map<StrokeNode, Pair<Double, Double>> nodeMinAndCurrentEnergies,
      Map<RoadStrokeForRoutes, Integer> strokesChoices) {

    // we randomly pick a node energy (ie null key is related to total energy of
    // all the nodes
    double randomEnergy = Math.random()
        * (nodeMinAndCurrentEnergies.get(null).second() + (nodeMinAndCurrentEnergies
            .size() - 1) * 0.5);
    StrokeNode strokeNode = null;

    // we find the node related to randomEnergy
    for (Entry<StrokeNode, Pair<Double, Double>> nodeEnergy : nodeMinAndCurrentEnergies
        .entrySet()) {
      // it's not a node
      if (nodeEnergy.getKey() == null) {
        continue;
      }
      // we withdraw the node value until randomEnergy become negative
      randomEnergy -= nodeEnergy.getValue().second() + 0.5;
      if (randomEnergy < 0) {
        // we found the node, we can stop the loop
        strokeNode = nodeEnergy.getKey();
        break;
      }
    }

    if (strokeNode == null) {
      LOGGER
          .error("problem to find a stroke node for nodeMinAndCurrentEnergies = "
              + nodeMinAndCurrentEnergies);
      return 0;
    }
    LOGGER.debug("\nwe pick randomEnergy = " + randomEnergy
        + " and strokeNode = " + strokeNode);

    double initialEnergy, finalEnergy, nodeDifference;

    // we count the number of routes related to this node, to pick more likely a
    // stroke with many routes
    int routesNodeNumber = 0;
    for (Stroke stroke : Iterables.concat(strokeNode.getInStrokes(),
        strokeNode.getOutStrokes())) {
      routesNodeNumber += RoutesConfigurations
          .getConfigurationsNumber(((RoadStrokeForRoutes) stroke)
              .getCarriedObjectsNumber());
    }

    // we randomly pick a stroke among strokes related to the node
    double randomStroke = Math.random() * routesNodeNumber;
    double energy = energyParam;
    // we use the same process as above
    for (Stroke s : Iterables.concat(strokeNode.getInStrokes(),
        strokeNode.getOutStrokes())) {
      RoadStrokeForRoutes stroke = (RoadStrokeForRoutes) s;
      randomStroke -= RoutesConfigurations.getConfigurationsNumber(stroke
          .getCarriedObjectsNumber());
      if (randomStroke <= 0) {
        strokesChoices.put(stroke, strokesChoices.get(stroke) + 1);
        
        // once we found it, we calculate the new energy
        energy -= ProblemConstraints.getInternalCrossedScore(stroke);


        // we can choose a random configuration
         stroke.randomRouteConfiguration();
         
         // or specific transformations
//        double randomTransformation = Math.random()
//            * nodeMinAndCurrentEnergies.get(strokeNode).second();
        /*if (nodeMinAndCurrentEnergies.get(strokeNode).second() > 2) {
          stroke.randomRouteConfiguration();
        } else */
//        if (stroke.getCarriedObjectsNumber() == 1 || Math.random() < 0.5) {
//          stroke.moveAllRoutes();
//        } else {
//          stroke.swapTwoRoutes();
//        }
         
        initialEnergy = ProblemConstraints.getNodeCrossingsScore(stroke
            .getStrokeInitialNode())
            + ProblemConstraints.getNodeRelativePositionsScore(stroke
                .getStrokeInitialNode())
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode())
                .first();
        finalEnergy = ProblemConstraints.getNodeCrossingsScore(stroke
            .getStrokeFinalNode())
            + ProblemConstraints.getNodeRelativePositionsScore(stroke
                .getStrokeFinalNode())
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode())
                .first();

        // nodeDifference is the the difference between the current node
        // energies and the previous ones
        nodeDifference = initialEnergy
            + finalEnergy
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode())
                .second()
            - nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode())
                .second();

        // we add the changes in nodeEnergies map, and we return the new energy
        nodeMinAndCurrentEnergies.get(stroke.getStrokeFinalNode()).set2(
            finalEnergy);
        nodeMinAndCurrentEnergies.get(stroke.getStrokeInitialNode()).set2(
            initialEnergy);
        nodeMinAndCurrentEnergies.get(null).set2(
            nodeMinAndCurrentEnergies.get(null).second() + nodeDifference);

        // we calculate the new energy
        energy += nodeDifference
            + ProblemConstraints.getInternalCrossedScore(stroke);

        break;
      }
    }

    return energy;
  }

}
