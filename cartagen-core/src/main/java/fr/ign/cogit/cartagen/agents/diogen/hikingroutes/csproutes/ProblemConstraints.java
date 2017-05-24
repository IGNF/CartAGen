package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;

/**
 * 
 * This class implements all the different constraints we need to create a CSP.
 * @author JTeulade-Denantes
 * 
 */
public class ProblemConstraints {

  private static Logger LOGGER = Logger
      .getLogger(ProblemConstraints.class.getName());

  /**
   * This function returns a score on the relative positions for a node. The
   * goal is to further a route at the same relative position
   * @param node
   * @return the score
   */
  public static double getNodeRelativePositionsScore(StrokeNode node) {
    if (node == null) {
      LOGGER.info(
          "Problem to evaluate a null node in getNodeRelativePositionsScore function");
      return 0;
    }

    // What weight do we want to give for a distance of relative position equals
    // to 1?
    // Warning it's used in logarithm function
    double relativePositionsWeight = 2;

    // this map will contain for a route all the relative positions according to
    // this node
    Map<String, List<Integer>> nodeRoutesPositions = new HashMap<String, List<Integer>>();

    RoadStrokeForRoutes roadStrokeForRoutes;

    int inStroke;
    for (Stroke stroke : Iterables.concat(node.getInStrokes(),
        node.getOutStrokes())) {
      roadStrokeForRoutes = ((RoadStrokeForRoutes) stroke);
      // if the stroke is going out the node, we have to take the inverse of its
      // route position
      inStroke = (node.getInStrokes().contains(stroke)) ? 1 : -1;
      for (int i = 0; i < roadStrokeForRoutes.getRoutesName().size(); i++) {
        String routeName = roadStrokeForRoutes.getRoutesName().get(i);
        if (nodeRoutesPositions.get(routeName) == null) {
          nodeRoutesPositions.put(routeName, new ArrayList<Integer>());
        }
        nodeRoutesPositions.get(routeName)
            .add(roadStrokeForRoutes.getRoutesPositions().get(i) * inStroke);
      }
    }

    double score = 0;
    for (String routeName : nodeRoutesPositions.keySet()) {
      List<Integer> routePositions = nodeRoutesPositions.get(routeName);
      LOGGER.debug(routeName + " -> " + routePositions);
      if (routePositions.size() < 2) {
        continue;
      }

      int routeScore = 0;
      // we choose to count only the minimum of each position
      for (int i = 0; i < routePositions.size(); i++) {
        int minScore = -1;
        for (int j = i + 1; j < routePositions.size(); j++) {
          int localScore = Math
              .abs(routePositions.get(i) + routePositions.get(j));
          if (minScore == -1 || localScore < minScore) {
            minScore = localScore;
          }
        }
        LOGGER.debug("minScore = " + minScore);
        if (minScore != -1) {
          routeScore += minScore;
        }
      }
      score += Math.log(routeScore + 1) * relativePositionsWeight;
      LOGGER.debug("score = " + score);
    }

    LOGGER.debug("routesRelativePositionsForNode returns the score " + score);
    return score;

  }

  /**
   * This function counts the number of internal crossed roads along the stroke
   * @param stroke whose you want to have a score
   * @return the score
   */
  public static int getInternalCrossedScore(RoadStrokeForRoutes stroke) {
    if (stroke == null) {
      LOGGER.info(
          "Problem to evaluate a null stroke in getInternalCrossedScore function");
      return 0;
    }

    int crossedRoadsRight = stroke.crossedRoadsNumber(true);
    int crossedRoadsLeft = stroke.crossedRoadsNumber(false);
    int rightRoutes = 0;
    int leftRoutes = 0;

    for (int position : stroke.getRoutesPositions()) {
      if (position > 0) {
        // we have to check the orientation of the arc
        rightRoutes = crossedRoadsRight;
      } else if (position < 0) {
        leftRoutes = crossedRoadsLeft;
      }
    }

    int score = Math.max(rightRoutes, leftRoutes)
        - Math.min(crossedRoadsRight, crossedRoadsLeft);
    LOGGER.debug("getInternalCrossedRoadsScore returns the score " + score);
    return score;
  }

  /**
   * this function returns a node score according the number of crossed roads
   * and routes
   * @param node whose you want to have the score
   * @return the final score
   */
  public static double getNodeCrossingsScore(StrokeNode node) {
    if (node == null) {
      LOGGER.error("Problem to evaluate a null node");
      return 0;
    }
    // What weight do we want to give for a node crossing?
    double nodeCrossingsWeight = 0.6;
    // this map saves the routes we already found the score
    List<String> routesAlreadySeen = new ArrayList<String>();
    // we get the ordered list
    List<String> orderedRoutesAndRoads = orderedRoutesAndRoads(node);
    // This map gives all the crossings for a route. So for a crossing between
    // two routes, we add two objects in this map
    Map<String, List<String>> crossingsRouteMap = new HashMap<String, List<String>>();

    double score = 0;
    for (Stroke stroke : Iterables.concat(node.getInStrokes(),
        node.getOutStrokes())) {
      for (String route : ((RoadStrokeForRoutes) stroke).getRoutesName()) {
        if (!routesAlreadySeen.contains(route)) {
          routesAlreadySeen.add(route);
          // we add the score related to this route
          score += nodeCrossingsForARoute(orderedRoutesAndRoads, route,
              crossingsRouteMap);
        }
      }
    }
    // we multiply the final score by the weight for the node crossings
    score *= nodeCrossingsWeight;

    // how quickly clusters score will grow?
    double clusterWeight = 2;
    LOGGER.debug("score without crossingsRouteMap = " + score);
    LOGGER.debug("crossingsRouteMap = " + crossingsRouteMap);
    int clusterOccurrences = 0;
    String mostCrossingsRoute = null;
    // we iterate while we didn't see all the crossings
    while (!crossingsRouteMap.isEmpty()) {
      int sizeMax = 0;
      // we look for the route with the most crossings number, it s
      // mostCrossingsRoute
      for (String route : crossingsRouteMap.keySet()) {
        if (crossingsRouteMap.get(route).size() > sizeMax) {
          sizeMax = crossingsRouteMap.get(route).size();
          mostCrossingsRoute = route;
        }
      }
      // we found a crossings cluster
      score += Math.exp(clusterOccurrences - 1);
      clusterOccurrences += clusterWeight;
      // once we found it, we remove all its occurrence from crossingsRouteMap
      for (String route : crossingsRouteMap.get(mostCrossingsRoute)) {
        crossingsRouteMap.get(route).remove(mostCrossingsRoute);
        if (crossingsRouteMap.get(route).isEmpty()) {
          crossingsRouteMap.remove(route);
        }
      }
      crossingsRouteMap.remove(mostCrossingsRoute);
    }
    return score;
  }

  /**
   * This function returns ordered routes and roads related to this nodes
   * @param node
   * @return the ordered list of strokes
   */
  private static List<String> orderedRoutesAndRoads(StrokeNode node) {

    RoadStrokeForRoutes routeStroke;
    int routePosition, carriedObjectsNumber, inStroke;
    List<String> globalClockwise = new ArrayList<String>();

    // For all the ordered strokes
    for (Stroke stroke : node.orderedStrokes()) {
      if (stroke == null) {
        globalClockwise.add("non fictive road");
        continue;
      }
      routeStroke = (RoadStrokeForRoutes) stroke;
      carriedObjectsNumber = routeStroke.getCarriedObjectsNumber();

      // we create an array with the good size
      String[] strokeClockwise = new String[2 * carriedObjectsNumber + 1];
      // if the stroke is going out the node, we have to take the inverse of its
      // route position
      inStroke = (node.getInStrokes().contains(stroke)) ? 1 : -1;
      // for all the routes
      for (int i = 0; i < routeStroke.getCarriedObjectsNumber(); i++) {
        routePosition = routeStroke.getRoutesPositions().get(i) * inStroke;
        // to have positive index, we add the routes number.
        strokeClockwise[carriedObjectsNumber + routePosition] = routeStroke
            .getRoutesName().get(i);
      }
      for (int i = 0; i < strokeClockwise.length; i++) {
        if (strokeClockwise[i] != null) {
          globalClockwise.add(strokeClockwise[i]);
        } else if (i == carriedObjectsNumber && !routeStroke.isFictive()) {
          // we had a key allowing to save non fictive road
          globalClockwise.add("non fictive road");
        }
      }
    }

    return globalClockwise;

  }

  /**
   * this function returns the score for a route on a node
   * @param orderedRoutesAndRoads, the ordered list related to the node
   * @param route whose we want to have the score
   * @param crossingsRouteMap, with all the crossings for a route
   * @return
   */
  private static int nodeCrossingsForARoute(List<String> orderedRoutesAndRoads,
      String route, Map<String, List<String>> crossingsRouteMap) {
    LOGGER.debug("orderedRoutesAndRoads = " + orderedRoutesAndRoads);
    LOGGER.debug("on calcule le score pour l'itinéraire " + route);
    // This set saves all the routes
    Set<String> alreadySeen = new HashSet<String>();
    // This list splits orderedRoutesAndRoads list in different sections
    // delimited by route argument
    List<Set<String>> sections = new ArrayList<Set<String>>();
    int firstOccurrenceIndex = orderedRoutesAndRoads.indexOf(route);
    if (firstOccurrenceIndex == -1) {
      LOGGER.error("the route " + route + " can't be found in the list "
          + orderedRoutesAndRoads);
      return 0;
    }
    int routeOccurrence = 0;
    sections.add(new HashSet<String>());
    for (int i = (firstOccurrenceIndex + 1)
        % orderedRoutesAndRoads.size(); i != firstOccurrenceIndex; i = (i + 1)
            % orderedRoutesAndRoads.size()) {
      if (orderedRoutesAndRoads.get(i).equals(route)) {
        // it's a new section
        routeOccurrence++;
        sections.add(new HashSet<String>());
      } else {
        // we add the route in the current section
        sections.get(routeOccurrence).add(orderedRoutesAndRoads.get(i));
        alreadySeen.add(orderedRoutesAndRoads.get(i));
      }
    }
    LOGGER.debug("sections = " + sections);
    int score = 0;
    int localScore;
    for (String differentRoute : alreadySeen) {
      localScore = routeCrossings(sections, differentRoute);
      score += localScore;
      // we add the crossings we found between route and differentRoute
      for (int i = 0; i < localScore; i++) {
        if (!crossingsRouteMap.containsKey(route)) {
          crossingsRouteMap.put(route, new ArrayList<String>());
        }
        if (!crossingsRouteMap.containsKey(differentRoute)) {
          crossingsRouteMap.put(differentRoute, new ArrayList<String>());
        }
        // we add it in twice for each route
        crossingsRouteMap.get(route).add(differentRoute);
        crossingsRouteMap.get(differentRoute).add(route);
      }
    }

    // we remove the route, we have just processed
    while (orderedRoutesAndRoads.remove(route))
      ;
    LOGGER.debug("on trouve un score = " + score);
    return score;
  }

  /**
   * This functions counts the crossings for a route in a list
   * @param sections related to a route
   * @param route
   * @return the number of crossings between the route argument and the other
   *         one related to the sections
   */
  private static int routeCrossings(List<Set<String>> sections, String route) {
    int sectionsSize = sections.size();
    int incrIndex = sectionsSize;
    // we find the last occurrence of the route in sections
    while (!sections.get(--incrIndex).contains(route))
      ;
    int decrIndex = incrIndex;
    int index = 1;
    // we stop when we checked all the sets
    while (decrIndex >= index
        && (incrIndex + index) % sectionsSize != decrIndex) {
      if (sections.get((incrIndex + index) % sectionsSize).contains(route)) {
        // we found a a set containing the route
        incrIndex += index;
        index = 1;
      } else if (sections.get(decrIndex - index).contains(route)) {
        // we found a a set containing the route
        decrIndex -= index;
        index = 1;
      } else {
        // we keep searching
        index++;
      }
    }
    return incrIndex - decrIndex;
  }

}
