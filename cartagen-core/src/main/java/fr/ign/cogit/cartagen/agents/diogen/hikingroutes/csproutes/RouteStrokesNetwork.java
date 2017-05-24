package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokeNode;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseauFlagPair;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/**
 * This class has been created from RoadStrokesNetwork class 
 * to create RoadStrokeForRoutes strokes 
 * instead of RoadStroke strokes
 * @author JTeulade-Denantes
 * 
 */
public class RouteStrokesNetwork extends RoadStrokesNetwork {

  private static Logger LOGGER = Logger.getLogger(RouteStrokesNetwork.class
      .getName());

  private Collection<StrokeNode> routeStrokeNodes;

  public Collection<StrokeNode> getRouteStrokeNodes() {
    return routeStrokeNodes;
  }

  public RouteStrokesNetwork() {
    super(new HashSet<ArcReseau>());
  }

  public RouteStrokesNetwork(Set<ArcReseau> features) {
    super(features);
  }

  public RouteStrokesNetwork(IFeatureCollection<TronconDeRoute> features) {
    super(features);
  }

  public void buildStrokes(Set<String> attributeNames,
      Set<String> attributeNamesNodeOfDegreeTwo, double deviatAngle,
      double deviatSum, boolean noStop) {

    // loop on the network features
    for (ArcReseau obj : this.getFeatures()) {
      // test if the feature has already been treated
      if (this.getGroupedFeatures().contains(obj)) {
        continue;
      }

      // build a new stroke object
      RoadStrokeForRoutes stroke = new RoadStrokeForRoutes(this, obj);

      // build the stroke on the initial side
      stroke.buildOneSide(true, attributeNames, attributeNamesNodeOfDegreeTwo,
          deviatAngle, deviatSum, noStop);

      // build the stroke on the final side
      stroke.buildOneSide(false, attributeNames, attributeNamesNodeOfDegreeTwo,
          deviatAngle, deviatSum, noStop);

      // build the stroke geometry
      stroke.buildGeomStroke();

      // add the stroke to the strokes set
      this.getStrokes().add(stroke);
    }

    Set<Stroke> toRemove = new HashSet<Stroke>();
    for (Stroke stroke : this.getStrokes()) {
      // if the stroke doesn't carry any routes, we can remove it
      if (((RoadStrokeForRoutes) stroke).getCarriedObjectsNumber() < 1) {
        toRemove.add(stroke);
      }
    }
    this.getStrokes().removeAll(toRemove);

    // once the route strokes network has been created, we have to instantiate
    // the stroke nodes
    Map<NoeudReseau, StrokeNode> strokeNodesMap = new HashMap<NoeudReseau, StrokeNode>();
    for (Stroke stroke : this.getStrokes()) {
      stroke.instantiateStrokeNodes(strokeNodesMap);
    }
    routeStrokeNodes = strokeNodesMap.values();

    // we identify problematic nodes which have been forgotten by the
    // buildStroke algorithm
    // A problematic node is a node where two strokes have the same symbology
    // and the same routes but can't be gathered because an other stroke with a
    // route is on the same node
    for (StrokeNode node : routeStrokeNodes) {
      boolean out = false;
      for (Stroke stroke : this.getStrokes()) {
        if (stroke.getFeatures().size() > 1
            && !node.equals(stroke.getStrokeFinalNode())
            && !node.equals(stroke.getStrokeInitialNode())) {
          int arcIndex = 0;
          for (ArcReseau arc : stroke.getFeatures()) {
            if ((node.getNoeudReseau().getArcsSortants().contains(arc) || node
                .getNoeudReseau().getArcsEntrants().contains(arc))
                && this.isRoutesBelongsNode(
                    ((RoadStrokeForRoutes) stroke).getRoutesName(), node)) {
              LOGGER.debug("we found a problematic arc related to the node "
                  + node + "\n\tand for the stroke " + stroke);
              // when we find the stroke, we remove it and create two new ones
              this.getStrokes().remove(stroke);
              if (!node.getOutStrokes().remove(stroke)) {
                node.getInStrokes().remove(stroke);
              }
              this.splitStroke(stroke, node, arcIndex);
              out = true;
              break;
            }
            arcIndex++;
          }
          if (out) {
            break;
          }
        }
      }
    }
  }

  /**
   * This function checks whether there is a route from routes parameter which
   * crosses the node
   * @param routes name
   * @param node
   * @return true if there is at least one route on the node
   */
  private boolean isRoutesBelongsNode(List<String> routes, StrokeNode node) {
    for (Stroke stroke : Iterables.concat(node.getInStrokes(),
        node.getOutStrokes())) {
      for (String route : ((RoadStrokeForRoutes) stroke).getRoutesName()) {
        if (routes.contains(route)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This function splits a stroke in two different strokes
   * @param stroke to split
   * @param node to insert in the stroke
   * @param splittingIndex, give the index where we have to split
   */
  private void splitStroke(Stroke stroke, StrokeNode node, int splittingIndex) {
    // this condition allows to know in which strokes, the splitting index arc
    // belongs
    if ((splittingIndex != 0
        && !stroke.getFeatures().get(splittingIndex - 1).getNoeudFinal()
            .equals(node.getNoeudReseau()) && !stroke.getFeatures()
        .get(splittingIndex - 1).getNoeudInitial()
        .equals(node.getNoeudReseau()))
        || (splittingIndex != stroke.getFeatures().size() - 1 && (stroke
            .getFeatures().get(splittingIndex + 1).getNoeudFinal()
            .equals(node.getNoeudReseau()) || stroke.getFeatures()
            .get(splittingIndex + 1).getNoeudInitial()
            .equals(node.getNoeudReseau())))) {
      // in this case, the splitting index arc belongs to the first stroke
      splittingIndex++;
    }

    // we split the arcs in two lists
    List<ArcReseauFlagPair> firstHalfArcs = new ArrayList<ArcReseauFlagPair>();
    List<ArcReseauFlagPair> secondHalfArcs = new ArrayList<ArcReseauFlagPair>();
    for (int i = 0; i < stroke.getFeatures().size(); i++) {
      if (i < splittingIndex) {
        firstHalfArcs.add(stroke.getOrientedFeatures().get(i));
      } else {
        secondHalfArcs.add(stroke.getOrientedFeatures().get(i));
      }
    }

    // we create the first stroke
    RoadStrokeForRoutes roadStrokeForRoutes = new RoadStrokeForRoutes(this,
        firstHalfArcs, null);
    roadStrokeForRoutes.buildGeomStrokeWithoutFlags();
    // we add it to the global strokes list
    this.getStrokes().add(roadStrokeForRoutes);
    // we initialize its final and initial node
    roadStrokeForRoutes.setStrokeInitialNode(stroke.getStrokeInitialNode());
    roadStrokeForRoutes.setStrokeFinalNode(node);
    // we add it in the stroke list of the node
    node.getInStrokes().add(roadStrokeForRoutes);
    if (!stroke.getStrokeInitialNode().getOutStrokes().remove(stroke)) {
      LOGGER.info("Problem to remove the stroke " + stroke
          + " and to split it in two strokes related to the node "
          + node.getNoeudReseau().getGeom());
    }
    stroke.getStrokeInitialNode().getOutStrokes().add(roadStrokeForRoutes);

    // idem for the second stroke
    roadStrokeForRoutes = new RoadStrokeForRoutes(this, secondHalfArcs, null);
    roadStrokeForRoutes.buildGeomStrokeWithoutFlags();
    this.getStrokes().add(roadStrokeForRoutes);
    roadStrokeForRoutes.setStrokeInitialNode(node);
    roadStrokeForRoutes.setStrokeFinalNode(stroke.getStrokeFinalNode());
    node.getOutStrokes().add(roadStrokeForRoutes);
    if (!stroke.getStrokeFinalNode().getInStrokes().remove(stroke)) {
      LOGGER.info("Problem to remove the stroke " + stroke
          + " and to split it in two strokes related to the node "
          + node.getNoeudReseau().getGeom());
    }
    stroke.getStrokeFinalNode().getInStrokes().add(roadStrokeForRoutes);

  }

  /**
   * This function casts Strokes list in RoadStrokeForRoutes list
   * @return the RoadStrokeForRoutes list
   */
  public Set<RoadStrokeForRoutes> getRoadStrokesForRoutes() {
    Set<RoadStrokeForRoutes> roadStrokesForRoutes = new HashSet<RoadStrokeForRoutes>();
    for (Stroke stroke : this.getStrokes()) {
      roadStrokesForRoutes.add((RoadStrokeForRoutes) stroke);
    }
    return roadStrokesForRoutes;
  }

}
