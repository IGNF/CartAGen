package fr.ign.cogit.cartagen.schematisation.buslinemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseauFlagPair;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/**
 * This class implements methods where the arcs of the stroke are from
 * TronconDeRouteItineraire type. They are the variables of a CSP problem
 * @author JTeulade-Denantes
 * 
 */
@Entity
public class RoadStrokeForRoutes extends Stroke {

  private static Logger LOGGER = Logger
      .getLogger(RoadStrokeForRoutes.class.getName());

  private static AtomicInteger COUNTER = new AtomicInteger();

  /**
   * This constructor creates the stroke, checks arcReseau type is
   * TronconDeRouteItineraire and initiates the routes configuration
   * @param network
   * @param root
   */
  public RoadStrokeForRoutes(StrokesNetwork network, ArcReseau root) {
    super(network, root);
    for (ArcReseau arcReseau : getFeatures())
      if (!arcReseau.getClass().getSimpleName()
          .equals("TronconDeRouteItineraireImpl"))
        LOGGER.info(
            "Problem in RoadStrokeForRoutes construction, this arc has the type "
                + arcReseau.getClass().getSimpleName()
                + " instead of TronconDeRouteItineraireImpl");
    this.initiateRoutes();

  }

  /**
   * This constructor creates the stroke, checks arcReseau type is
   * TronconDeRouteItineraire and initiates the routes configuration
   * @param network
   * @param root
   */
  public RoadStrokeForRoutes(RoadStrokesNetwork network,
      List<ArcReseauFlagPair> features, ILineString geomStroke) {
    super(network, (ArrayList<ArcReseauFlagPair>) features, geomStroke);
    for (ArcReseau arcReseau : getFeatures())
      if (!arcReseau.getClass().getSimpleName()
          .equals("TronconDeRouteItineraireImpl"))
        LOGGER.info(
            "Problem in RoadStrokeForRoutes construction, this arc has the type "
                + arcReseau.getClass().getSimpleName()
                + " instead of TronconDeRouteItineraireImpl");
    this.initiateRoutes();

  }

  /**
   * This attribute is a constant list with the routes names created . This list
   * is never modified
   */
  @Transient
  private List<String> routesNames = new ArrayList<String>();

  public List<String> getRoutesName() {
    return this.routesNames;
  }

  /**
   * This index allows to find the current stroke configuration with all routes
   * positions. It is modified according to the stroke configuration
   */
  private int routeConfiguration = 0;

  public int getRouteConfiguration() {
    return routeConfiguration;
  }

  public void setRouteConfiguration(int routeConfiguration) {
    this.routeConfiguration = routeConfiguration;
  }

  /**
   * This score is the sum of previous internal strokes scores in a backtracking
   * algorithm.
   */
  private double strokesScore = 0;

  public double getStrokesScore() {
    return strokesScore;
  }

  public void setStrokesScore(double strokesScore) {
    this.strokesScore = strokesScore;
  }

  public void nextRouteConfiguration() {
    this.routeConfiguration++;
  }

  public void resetRouteConfiguration() {
    this.setRouteConfiguration(0);
  }

  public void randomRouteConfiguration() {
    this.setRouteConfiguration((int) (Math.random() * RoutesConfigurations
        .getConfigurationsNumber(this.getCarriedObjectsNumber())));
  }

  /**
   * This function swaps two different routes from the same stroke
   */
  public void swapTwoRoutes() {
    // we use a copy of the current routes positions
    List<Integer> currentConfiguration = new ArrayList<Integer>(
        this.getRoutesPositions());
    // we pick randomly the first route
    int firstRoute = (int) (Math.random() * currentConfiguration.size());
    // idem with the second route
    int secondRoute = (int) (Math.random() * (currentConfiguration.size() - 1));
    if (firstRoute == secondRoute) {
      secondRoute = currentConfiguration.size() - 1;
    }
    // we swap the two random routes
    Collections.swap(currentConfiguration, firstRoute, secondRoute);
    // we find the index related to this configuration
    this.setRouteConfiguration(RoutesConfigurations.findConfigurationIndex(
        this.getCarriedObjectsNumber(), currentConfiguration));
  }

  /**
   * This function moves all the routes from the same stroke in a random
   * direction
   */
  public void moveAllRoutes() {
    // we use a copy of the current routes positions
    List<Integer> currentConfiguration = new ArrayList<Integer>(
        this.getRoutesPositions());
    int moveDirection;
    if (currentConfiguration.contains(currentConfiguration.size())) {
      // we can only move in this direction
      moveDirection = -1;
    } else if (currentConfiguration.contains(-currentConfiguration.size())) {
      // idem with the other direction
      moveDirection = 1;
    } else {
      // otherwise, we pick randomly a direction
      moveDirection = (Math.random() < 0.5) ? 1 : -1;
    }

    // we move each route in the direction we chose
    for (int i = 0; i < currentConfiguration.size(); i++) {
      // the position 0 is for the road, we have to move over it
      if (currentConfiguration.get(i) + moveDirection == 0) {
        currentConfiguration.set(i, -currentConfiguration.get(i));
      } else {
        currentConfiguration.set(i,
            currentConfiguration.get(i) + moveDirection);
      }
    }
    // we find the index related to this configuration
    this.setRouteConfiguration(RoutesConfigurations.findConfigurationIndex(
        this.getCarriedObjectsNumber(), currentConfiguration));
  }

  /**
   * This function finds the configuration of each route from routeConfiguration
   * attribute
   * @return the list of routes positions related to the stroke
   */
  @Transient
  public List<Integer> getRoutesPositions() {
    return RoutesConfigurations.getConfiguration(this.getCarriedObjectsNumber(),
        this.getRouteConfiguration());
  }

  /**
   * This function initializes routesNames and routesPositions attributes
   */
  private void initiateRoutes() {
    for (ICarriedObject route : getCarriedObjects(0)) {
      this.routesNames.add(route.getName());
    }
  }

  /**
   * This function allows to use easily a road section from an ArcReseauFlagPair
   * @param arc related to the road section
   * @return the roadSection wanted
   */
  @Transient
  public ICarrierNetworkSection getRoadSection(ArcReseauFlagPair arc) {
    return ((TronconDeRouteItineraire) arc.getArcReseau()).getRoadSection();
  }

  /**
   * This function allows to use easily a road section from an index
   * @param index in this.getFeatures
   * @return the roadSection wanted
   */
  @Transient
  public ICarrierNetworkSection getRoadSection(int index) {
    return ((TronconDeRouteItineraire) this.getOrientedFeatures().get(index)
        .getArcReseau()).getRoadSection();
  }

  /**
   * This function allows to have the feature routes in a stroke
   * @param arc related to the road section
   * @return the routes collection carried by the feature
   */
  public Collection<ICarriedObject> getCarriedObjects(ArcReseauFlagPair arc) {
    return this.getRoadSection(arc).getCarriedObjects();
  }

  /**
   * This function allows to have the feature routes in a stroke
   * @param index in the features
   * @return the routes collection carried by the feature
   */
  public Collection<ICarriedObject> getCarriedObjects(int index) {
    return this.getRoadSection(index).getCarriedObjects();
  }

  /**
   * This function puts the same route at a position wanted on a stroke. You
   * have to deal with the geometries of the arcs
   * @param routeName
   * @param position
   */
  public void setRoadRelativePosition(String routeName, int position) {
    // you run all the stroke elements
    for (ArcReseauFlagPair arc : this.getOrientedFeatures()) {
      int newPosition = position;
      // you choose the good position according to the flag (cf geometry)
      if (!arc.getFlag())
        newPosition = -position;

      // you have to find the route section associated to this routeName
      for (ICarriedObject route : this.getCarriedObjects(arc))
        if (route.getName().equals(routeName)) {
          // when you find it, you can move the route section at the good new
          // position
          this.getRoadSection(arc).switchCarriedObject(route, newPosition);
          route.setRoadRelativePosition(newPosition);
          break;
        }
    }
  }

  /**
   * This function counts the number of internal roads along the stroke right or
   * left
   * @param right, the side of the stroke
   * @return the number of roads
   */
  public int crossedRoadsNumber(boolean right) {
    int crossedRoadsNumber = 0;
    NoeudReseau node = null;
    ArcReseauFlagPair previousArc = null;
    // we look for all the intern nodes in the current stroke
    for (ArcReseauFlagPair arc : this.getOrientedFeatures()) {
      // we need two arcs to find crossed roads between them
      if (previousArc != null) {
        // if the previous arc has been instantiated, we can increment the
        // crossed roads number
        crossedRoadsNumber += node.clockwiseSelectedArcs(
            previousArc.getArcReseau(), arc.getArcReseau(), right).size();
      }

      // we find the node which will be analyzed in the next loop
      if (arc.getFlag())
        node = arc.getArcReseau().getNoeudFinal();
      else
        node = arc.getArcReseau().getNoeudInitial();
      previousArc = arc;
    }
    return crossedRoadsNumber;
  }

  /**
   * This functions counts the routes number
   * @return the routes number
   */
  @Transient
  public int getCarriedObjectsNumber() {
    return this.getRoutesName().size();
  }

  /**
   * this function checks whether the road related to this stroke is fictive
   * @return true is the road is fictive, otherwise it returns false
   */
  @Transient
  public boolean isFictive() {
    // on this kind of stroke, all the roads have the same symbology, that's why
    // we chose
    // the first one
    return (((TronconDeRouteItineraire) this.getFeatures().get(0))
        .getSymbo() == "fictive road");
  }

  /**
   * This function counts the routes which haven't already been seen. In the
   * case where the route have already been seen, we don't even count the
   * previous one. We also add the road itself if it's not fictive.
   * @param routesAlreadySeen, the routes set
   * @return the number of unique routes
   */
  public int countNewRoutesAndRoad(Set<String> routesAlreadySeen) {
    int routesAndRoadNumber = this.getCarriedObjectsNumber();
    for (String routeName : this.getRoutesName()) {
      // have we already seen this route?
      if (routesAlreadySeen.contains(routeName))
        // we could remove the route from the set but there will be a problem
        // for the multiple routes
        routesAndRoadNumber -= 2;
      else
        routesAlreadySeen.add(routeName);
    }
    // we count the road only if it is a non fictive road
    if (!this.isFictive())
      routesAndRoadNumber++;
    return routesAndRoadNumber;
  }

  /**
   * This function updates strokeScore attribute which is the current score and
   * the previous strokes score
   * @param roadStrokeForRoutes, the previous stroke
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   * @return the new score
   */
  public double updateScores(RoadStrokeForRoutes roadStrokeForRoutes,
      Map<StrokeNode, Integer> nodeOccurrences) {
    // roadStrokeForRoutes == null => it's the first stroke
    double score = (roadStrokeForRoutes == null) ? 0
        : roadStrokeForRoutes.getStrokesScore();
    LOGGER.debug("stroke : " + this.getCarriedObjectsNumber()
        + " avant score = " + score);

    int initialOccurrences = nodeOccurrences.get(this.getStrokeInitialNode());
    int finalOccurrences = nodeOccurrences.get(this.getStrokeFinalNode());
    double aux;
    // just in case to keep having positive occurrences
    if (initialOccurrences > 0)
      nodeOccurrences.put(this.getStrokeInitialNode(), initialOccurrences - 1);
    if (finalOccurrences > 0)
      nodeOccurrences.put(this.getStrokeFinalNode(), finalOccurrences - 1);

    // have we seen all the strokes related to the initial node?
    if (initialOccurrences < 2) {
      // we calculate the score for the initial node
      aux = ProblemConstraints
          .getNodeCrossingsScore(this.getStrokeInitialNode())
          + ProblemConstraints
              .getNodeRelativePositionsScore(this.getStrokeInitialNode());
      // if (aux >= 13) {
      // return 10000;
      // }
      score += aux;
    }

    if (finalOccurrences < 2) {
      aux = ProblemConstraints.getNodeCrossingsScore(this.getStrokeFinalNode())
          + ProblemConstraints
              .getNodeRelativePositionsScore(this.getStrokeFinalNode());
      // if (aux >= 13) {
      // return 10000;
      // }
      score += aux;
    }

    // we count 0.5 for each internal crossings
    score += ProblemConstraints.getInternalCrossedScore(this) / 2;
    this.setStrokesScore(score);
    // we return the new score, sum of the previous score and the current stroke
    // score
    return score;
  }

  /**
   * This function backtracks this stroke, so we modify node occurrences and
   * take the next configuration
   * @param nodeOccurrences, a map between a node and the number of its related
   *          strokes
   */
  public void backtrack(Map<StrokeNode, Integer> nodeOccurrences) {
    nodeOccurrences.put(this.getStrokeInitialNode(),
        nodeOccurrences.get(this.getStrokeInitialNode()) + 1);
    nodeOccurrences.put(this.getStrokeFinalNode(),
        nodeOccurrences.get(this.getStrokeFinalNode()) + 1);
    this.nextRouteConfiguration();
  }

  /**
   * This function returns all the stroke informations
   * @return a String with the stroke informations
   */
  @Transient
  public String getStrokeInformations() {
    String informations = "";
    informations += "\nbasic information : " + this;
    informations += "\nsymbo = " + ((TronconDeRouteItineraire) this
        .getOrientedFeatures().get(0).getArcReseau()).getSymbo();
    informations += "\nimportance = " + ((TronconDeRouteItineraire) this
        .getOrientedFeatures().get(0).getArcReseau()).getImportance();
    informations += "\n " + this.getCarriedObjectsNumber() + " routes : "
        + this.getRoutesName();
    informations += "\nroutes configuration : " + this.getRoutesPositions();

    informations += "\ninitial and final nodes coordinates : ("
        + this.getStrokeInitialNode().getNoeudReseau().getGeom() + " ; "
        + this.getStrokeFinalNode().getNoeudReseau().getGeom() + ")";
    informations += "\nSections : ";
    for (ArcReseauFlagPair arc : this.getOrientedFeatures()) {
      informations += "\n\t" + arc.getArcReseau().getNoeudInitial().getGeom()
          + " -> " + arc.getArcReseau().getNoeudFinal().getGeom() + " ("
          + arc.getFlag() + ")";
    }
    return informations;

  }

}
