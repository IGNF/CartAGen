package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.HikingFactory;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteSection;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IRouteSection;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierObject;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;

public class RouteRoadAssociation {

  private static final Logger LOGGER = Logger
      .getLogger(RouteRoadAssociation.class.getName());

  /**
   * Assign the element of a network to another one
   * @param routes
   * @param roads
   * @param factory
   */
  public void roadsToRoutesAssociation(IFeatureCollection<IRouteSection> routes,
      IFeatureCollection<ICarryingRoadLine> roads,
      AbstractCreationFactory factory) {
    CarteTopo carteTopo = new CarteTopo("Roads to Routes Association");
    IPopulation<Arc> edgePop = carteTopo.getPopArcs();
    // IPopulation<Noeud> nodePop = carteTopo.getPopNoeuds();
    LOGGER.debug("Add routes");
    carteTopo.importClasseGeo(roads, true);
    // addElementsFromFeatureCollection(edgePop, nodePop, routes);
    LOGGER.debug("Add roads");
    carteTopo.importClasseGeo(routes, true);
    // addElementsFromFeatureCollection(edgePop, nodePop, roads);

    LOGGER.debug("creeNoeudsManquants");
    carteTopo.creeNoeudsManquants(1);
    LOGGER.debug("fusionNoeuds");
    carteTopo.fusionNoeuds(1);
    LOGGER.debug("filtreArcsDoublons");
    carteTopo.filtreArcsDoublons(1);
    LOGGER.debug("rendPlanaire");
    carteTopo.rendPlanaire(1);
    LOGGER.debug("fusionNoeuds");
    carteTopo.fusionNoeuds(1);
    LOGGER.debug("filtreArcsDoublons");
    carteTopo.filtreArcsDoublons(1);
    // LOGGER.debug("filtreNoeudsSimples");
    carteTopo.filtreNoeudsSimples(false, null, true);
    // carteTopo.fil
    Set<IRoadLine> roadsToRemove = new HashSet<IRoadLine>();
    Set<IRouteSection> routesToRemove = new HashSet<IRouteSection>();

    LOGGER.debug("Edges number: " + edgePop.size());
    int i = 1;
    for (Arc edge : edgePop) {
      LOGGER.info(i++ + "/" + edgePop.size());
      Set<IFeature> roadsCorrespondant = new HashSet<IFeature>(
          edge.getCorrespondants(roads));
      Set<IFeature> routesCorrespondant = new HashSet<IFeature>(
          edge.getCorrespondants(routes));

      LOGGER.debug("Arc " + edge + ", roads " + roadsCorrespondant + ", routes "
          + routesCorrespondant);
      IRoadLine supportingRoadSection = null;
      if (roadsCorrespondant.size() == 0) {
        // A Route without supporting road section
        // We create here a new road section
        ILineString newGeom = (ILineString) edge.getGeometrie().clone();
        newGeom
            .setCRS(routesCorrespondant.iterator().next().getGeom().getCRS());
        ICarryingRoadLine roadSection = (ICarryingRoadLine) factory
            .createRoadLine(newGeom, 5);
        LOGGER.debug("Created new road: " + roadSection);
        roads.add(roadSection);
        supportingRoadSection = roadSection;
      } else if (roadsCorrespondant.size() >= 1) {
        // One road for the edge.
        IRoadLine roadSection = (IRoadLine) roadsCorrespondant.toArray()[0];
        if (roadSection.getCorrespondants(edgePop).size() > 1) {
          // Test if the road is on several edge: this mean that there are
          // several route section (of the same route) on different parts of the
          // road.
          roadsToRemove.add(roadSection);
          LOGGER.debug("Remove road " + roadSection);
          ILineString newGeom = (ILineString) edge.getGeometrie().clone();
          newGeom.setCRS(roadSection.getGeom().getCRS());
          ICarryingRoadLine newRoadSection = (ICarryingRoadLine) factory
              .createRoadLine(newGeom, roadSection.getImportance());
          newRoadSection.copyAttributes(roadSection);
          LOGGER.debug("Created road (portion): " + newRoadSection);
          roads.add(newRoadSection);
          supportingRoadSection = newRoadSection;
        } else {
          supportingRoadSection = roadSection;
        }

        if (roadsCorrespondant.size() > 1) {
          LOGGER.debug("Several roads in the arc: " + edge + ", nb= "
              + roadsCorrespondant.size() + ", list= " + roadsCorrespondant);
          // This produce in the border of two different "dalles"
          roadsCorrespondant.remove(roadSection);
          for (IFeature c : roadsCorrespondant) {
            roadsToRemove.add((IRoadLine) c);
          }
        }
      }

      for (IFeature route : routesCorrespondant) {
        if (route.getCorrespondants(edgePop).size() == 1) {
          ((ICarrierObject) supportingRoadSection)
              .addCarriedObject((ICarriedObject) route);
        } else {
          routesToRemove.add((IRouteSection) route);
          // FIXME
          ILineString newGeom = (ILineString) edge.getGeometrie().clone();
          newGeom.setCRS(route.getGeom().getCRS());
          IHikingRouteSection newRoute = ((HikingFactory) factory)
              .createTouristRoute(newGeom, ((IRouteSection) route).getName(),
                  ((IRouteSection) route).getSymbo());
          ((ICarrierObject) supportingRoadSection)
              .addCarriedObject((ICarriedObject) newRoute);
          routes.add(newRoute);
        }
      }
    }
    roads.removeAll(roadsToRemove);
    // roads.addAll(roadsToAdd);
    routes.removeAll(routesToRemove);
    // routes.addAll(routesToAdd);

    LOGGER.debug("road population " + roads);

    LOGGER.debug("routes population " + routes);

    carteTopo.nettoyer();

  }
}
