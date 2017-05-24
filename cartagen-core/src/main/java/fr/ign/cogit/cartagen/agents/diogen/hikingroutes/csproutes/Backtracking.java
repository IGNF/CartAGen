package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;

/**
 * This class provides four different backtracking algorithms
 * @author JTeulade-Denantes
 * 
 */
public class Backtracking {

  private static Logger LOGGER = Logger.getLogger(Backtracking.class.getName());

  /**
   * This function uses a dichotomic approach to solve the CSP problem
   * @param strokes, the variables of our CSP problem
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   * @param minScore, an upper bound of the optimal score
   * @param minConfigSolution, the configuration of the best solution
   * @return the optimal score
   */
  public static double dichotomicBacktracking(RoadStrokeForRoutes[] strokes,
      Map<StrokeNode, Integer> nodeOccurrences, List<Integer> minConfigSolution) {
    double score;
    int count = 0;
    int i = 0;
    double dichotomicMax = 1000;
    double dichotomicMin = 0;
    Map<StrokeNode, Integer> minNodeOccurrences = null;

    while (count < 1260000000 && dichotomicMin + 1 < dichotomicMax) {
      count++;
      if (count % 500000 == 0) {
        LOGGER.info(" dichotomicMin = " + dichotomicMin
            + " et dichotomicMax = " + dichotomicMax);
        LOGGER.info(getStrokesConfigurations(strokes));
      }
      if (strokes[0].getRoutesPositions() == null) {
        dichotomicMin = (dichotomicMax + dichotomicMin) / 2;
        LOGGER.info("on n'a pas trouvé de solutions, on augmente le min à "
            + dichotomicMin);
        // we didn' find any solutions
        for (StrokeNode node : nodeOccurrences.keySet()) {
          nodeOccurrences.put(node, minNodeOccurrences.get(node));
        }
        for (int j = 0; j < strokes.length; j++) {
          strokes[j].setRouteConfiguration(minConfigSolution.get(i));
        }
        i = strokes.length - 1;
        strokes[i].backtrack(nodeOccurrences);

      } else if (i >= strokes.length) {
        score = strokes[strokes.length - 1].getStrokesScore();
        LOGGER.debug("+++++++++on est tt en bas avec i = " + i
            + " et position = "
            + strokes[strokes.length - 1].getRouteConfiguration()
            + " et score = " + score);
        if (score <= (dichotomicMin + dichotomicMax) / 2) {
          dichotomicMax = score;
          LOGGER.info("on a trouvé une meilleure solution, on réduit le max à "
              + dichotomicMax);
          if (minNodeOccurrences == null) {
            minNodeOccurrences = new HashMap<>(nodeOccurrences);
          }
          // we found a solution
          minConfigSolution.clear();
          ;
          for (RoadStrokeForRoutes stroke : strokes) {
            minConfigSolution.add(stroke.getRouteConfiguration());
          }
        }
        i = strokes.length - 1;
        strokes[i].backtrack(nodeOccurrences);
      } else if (strokes[i].getRoutesPositions() == null) {
        LOGGER.debug("-> -> -> bloqué, il faut remonter pour i = " + i);
        strokes[i].resetRouteConfiguration();
        i--;
        strokes[i].backtrack(nodeOccurrences);
      } else {
        LOGGER.debug("stroke i = "
            + i
            + " et position = "
            + strokes[i].getRouteConfiguration()
            + " ("
            + RoutesConfigurations.getConfiguration(
                strokes[i].getCarriedObjectsNumber(),
                strokes[i].getRouteConfiguration()) + ")");
        score = (int) strokes[i].updateScores((i == 0) ? null : strokes[i - 1],
            nodeOccurrences);
        LOGGER.debug("updateScores = " + score);
        if (score <= (dichotomicMin + dichotomicMax) / 2) {
          i++;
        } else {
          strokes[i].backtrack(nodeOccurrences);
          LOGGER.debug("\tscore trop haut (cf dichotomicMin = " + dichotomicMin
              + ")");
        }
      }
    }
    return dichotomicMax;
  }

  /**
   * This function uses a incremental approach to solve the CSP problem
   * @param strokes, the variables of our CSP problem
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   * @param minConfigSolution, the configuration of the solution
   * @return the optimal score
   */
  public static int incrementalBacktracking(RoadStrokeForRoutes[] strokes,
      Map<StrokeNode, Integer> nodeOccurrences, List<Integer> minConfigSolution) {
    int score;
    int count = 0;
    int i = 0;
    int incrementalMin = 0;
    Map<StrokeNode, Integer> minNodeOccurrences = null;

    while (count < 1260000000) {
      count++;
      if (count % 500000 == 0) {
        LOGGER.info(" incrementalMin = " + incrementalMin);
        LOGGER.info(getStrokesConfigurations(strokes));
      }
      if (strokes[0].getRoutesPositions() == null) {
        incrementalMin++;
        LOGGER
            .info("on n'a pas trouvé de solutions, on augmente incrementalMin à "
                + incrementalMin);
        // we didn' find any solutions
        for (StrokeNode node : nodeOccurrences.keySet()) {
          nodeOccurrences.put(node, node.getInStrokes().size()
              + node.getOutStrokes().size());
        }
        for (int j = 0; j < strokes.length; j++) {
          strokes[j].resetRouteConfiguration();
        }
        i = 0;

      } else if (i >= strokes.length) {
        score = (int) strokes[strokes.length - 1].getStrokesScore();
        LOGGER.debug("+++++++++on est tt en bas avec i = " + i
            + " et position = "
            + strokes[strokes.length - 1].getRouteConfiguration()
            + " et score = " + score);
        if (score <= incrementalMin) {
          LOGGER.info("on a trouvé la meilleure solution, incrementalMin = "
              + incrementalMin);
          // we found the solution
          minConfigSolution.clear();
          ;
          for (RoadStrokeForRoutes stroke : strokes) {
            minConfigSolution.add(stroke.getRouteConfiguration());
          }
          return score;
        }
        i = strokes.length - 1;
        strokes[i].backtrack(nodeOccurrences);
      } else if (strokes[i].getRoutesPositions() == null) {
        LOGGER.debug("-> -> -> bloqué, il faut remonter pour i = " + i);
        strokes[i].resetRouteConfiguration();
        i--;
        strokes[i].backtrack(nodeOccurrences);
      } else {
        LOGGER.debug("stroke i = "
            + i
            + " et position = "
            + strokes[i].getRouteConfiguration()
            + " ("
            + RoutesConfigurations.getConfiguration(
                strokes[i].getCarriedObjectsNumber(),
                strokes[i].getRouteConfiguration()) + ")");
        score = (int) strokes[i].updateScores((i == 0) ? null : strokes[i - 1],
            nodeOccurrences);
        LOGGER.debug("updateScores = " + score);
        if (score <= incrementalMin) {
          i++;
        } else {
          strokes[i].backtrack(nodeOccurrences);
          LOGGER.debug("\tscore trop haut (cf incrementalMin = "
              + incrementalMin + ")");
        }
      }
    }
    return incrementalMin;
  }

  /**
   * This function uses a threshold approach to solve the CSP problem
   * @param strokes, the variables of our CSP problem
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   * @param minScore, an upper bound of the optimal score
   * @param minConfigSolution, the configuration of the solution
   * @param minScore, an upper bound of the optimal score
   * @return the optimal score
   */
  public static double thresholdBacktracking(RoadStrokeForRoutes[] strokes,
      Map<StrokeNode, Integer> nodeOccurrences,
      List<Integer> minConfigSolution, double minScore) {

    //this array gives the depth in the search tree where the algorithm backtrack
    int[] treeDepth = new int[strokes.length];
    double score;
    int count = 0;
    int i = 0;

    for (int j = 0; j < strokes.length; j++) {
      strokes[j].resetRouteConfiguration();
    }

    //count has been chosen arbitrarily || we finished the algorithm
    while (count < 1260000000 && strokes[0].getRoutesPositions() != null) {
      count++;
      // each 500000 iterations we print the system state
      if (count % 500000 == 0) {
        LOGGER.info(" minScore = " + minScore);
        LOGGER.info(getStrokesConfigurations(strokes));
      }
      
      if (i >= strokes.length) {
        score = strokes[strokes.length - 1].getStrokesScore();
        LOGGER.debug("+++++++++on est tt en bas avec i = " + i
            + " et position = "
            + strokes[strokes.length - 1].getRouteConfiguration()
            + " et score = " + score);
        if (score < minScore) {
          LOGGER.debug("on trouve une nouvelle solution !");
          minScore = score;
          minConfigSolution.clear();
          for (RoadStrokeForRoutes stroke : strokes) {
            minConfigSolution.add(stroke.getRouteConfiguration());
          }
        }
        i = strokes.length - 1;
        treeDepth[i]++;
        strokes[i].backtrack(nodeOccurrences);
      } else if (strokes[i].getRoutesPositions() == null) {
        LOGGER.debug("-> -> -> bloqué, il faut remonter pour i = " + i);
        strokes[i].resetRouteConfiguration();
        i--;
        strokes[i].backtrack(nodeOccurrences);
      } else {
        LOGGER.debug("stroke i = "
            + i
            + " et position = "
            + strokes[i].getRouteConfiguration()
            + " ("
            + RoutesConfigurations.getConfiguration(
                strokes[i].getCarriedObjectsNumber(),
                strokes[i].getRouteConfiguration()) + ")");
        score = strokes[i].updateScores((i == 0) ? null : strokes[i - 1],
            nodeOccurrences);
        LOGGER.debug("updateScores = " + score);
        if (score < minScore) {
          i++;
        } else {
          treeDepth[i]++;
          strokes[i].backtrack(nodeOccurrences);
          LOGGER.debug("\tscore trop haut (cf minScore = " + minScore + ")");
        }
      }
    }
    //if we want to launch again the algorithm
    strokes[0].resetRouteConfiguration();
    LOGGER.debug("iteration number  = " + count);
    LOGGER.info("treeDepth:");
    for (int j = 0; j < treeDepth.length; j++) {
      if (treeDepth[j] != 0)
        LOGGER.info(j + " -> " + treeDepth[j]);
    }
    return minScore;
  }

  private static String getStrokesConfigurations(RoadStrokeForRoutes[] strokes) {
    String s = "strokes configurations : ";
    for (int j = 0; j < strokes.length; j++) {
      s += " + " + strokes[j].getRouteConfiguration() + " ("
          + strokes[j].getRoutesPositions() + ")";
    }
    return s;
  }


}