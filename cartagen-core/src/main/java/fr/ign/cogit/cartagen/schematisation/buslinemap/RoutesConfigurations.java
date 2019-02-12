package fr.ign.cogit.cartagen.schematisation.buslinemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class builds all routes configurations we can have for all kind of
 * strokes
 * @author JTeulade-Denantes
 * 
 */
public class RoutesConfigurations {

  /**
   * routesConfigurations.get(3).get(6) returns the 6th possible configuration
   * for a stroke carrying 3 routes
   */
  private final static List<List<List<Integer>>> routesConfigurations = init();

  private static List<List<List<Integer>>> init() {
    ArrayList<List<List<Integer>>> routesConfigurations = new ArrayList<List<List<Integer>>>();
    // we add null to have routesConfigurations.get(i) related to a stroke with
    // i routes
    routesConfigurations.add(null);
    for (int i = 1; i < 6; i++) {
      List<List<Integer>> result = new ArrayList<List<Integer>>();
      for (List<Integer> frameSet : generateSets(i)) {
        result.addAll(shiftedLists(frameSet));
      }
      Collections.shuffle(result);
      routesConfigurations.add(result);
    }
    return routesConfigurations;

  }

  private static List<List<Integer>> shiftedLists(List<Integer> list) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    List<Integer> listAux = new ArrayList<Integer>();
    List<Integer> listAux2;
    listAux.add(list.get(0));
    result.add(listAux);
    int currentNumber;
    int currentSize;
    for (int i = 1; i < list.size(); i++) {
      currentNumber = list.get(i);
      currentSize = result.size();
      for (int j = 0; j < currentSize; j++) {
        listAux = new ArrayList<Integer>(result.get(j));
        for (int k = 0; k < i + 1; k++) {
          if (k == 0) {
            result.get(j).add(0, currentNumber);
          } else {
            listAux2 = new ArrayList<Integer>(listAux);
            listAux2.add(k, currentNumber);
            result.add(listAux2);
          }
        }
      }
    }
    return result;
  }

  private static List<List<Integer>> generateSets(int a) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    List<Integer> aux = new ArrayList<Integer>();
    for (int i = -1; i >= -a; i--) {
      aux.add(0, i);
    }
    result.add(new ArrayList<Integer>(aux));
    for (int i = 1; i <= a; i++) {
      aux.remove(0);
      aux.add(i);
      result.add(new ArrayList<Integer>(aux));
    }
    return result;
  }

  /**
   * This function returns the configurations number for a stroke with
   * routesNumber routes It's equal to "factorial(routesNumber + 1)"
   * @param routesNumber
   * @return the configurations number
   */
  public static int getConfigurationsNumber(int routesNumber) {
    try {
      return routesConfigurations.get(routesNumber).size();
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * This function returns the routes positions related to a stroke
   * @param routesNumber
   * @param configurationIndex
   * @return the configuration.
   */
  public static List<Integer> getConfiguration(int routesNumber,
      int configurationIndex) {
    try {
      return routesConfigurations.get(routesNumber).get(configurationIndex);
    } catch (Exception e) {
      // allows to know when we exceed the bounds
      return null;
    }
  }

  public static int findConfigurationIndex(int routesNumber,
      List<Integer> configuration) {
    return routesConfigurations.get(routesNumber).indexOf(configuration);
  }

}
