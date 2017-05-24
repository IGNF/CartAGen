package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.preprocessing.ComputeRouteSectionGeom;
import fr.ign.cogit.cartagen.agents.diogen.preprocessing.ConcatLineStrings;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

public abstract class Route implements IRoute {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(Route.class.getName());

  private List<IRouteSection> routeSections = new ArrayList<IRouteSection>();

  // private Map<IRouteSection, Boolean> routeSectionsDirectionMap = new
  // Hashtable<IRouteSection, Boolean>();

  public List<IRouteSection> getRouteSections() {
    return routeSections;
  }

  @Override
  public void add(IRouteSection routeSection) {
    routeSection.setRoute(this);
    this.routeSections.add(routeSection);
    if (nodes.isEmpty()) {
      nodes.add((IRouteNode) routeSection.getInitialGeom());
      nodes.add((IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes.get(0) == routeSection.getInitialGeom()) {
      Collections.reverse(nodes);
      nodes.add((IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes
        .get(this.nodes.size() - 1) == (routeSection.getInitialGeom())) {
      nodes.add((IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes.get(0) == (routeSection.getFinalNode())) {
      Collections.reverse(nodes);
      nodes.add((IRouteNode) routeSection.getInitialGeom());
    } else if (this.nodes
        .get(this.nodes.size() - 1) == (routeSection.getFinalNode())) {
      nodes.add((IRouteNode) routeSection.getInitialGeom());
    }
  }

  @Override
  public void add(int i, IRouteSection routeSection) {
    routeSection.setRoute(this);
    this.routeSections.add(i, routeSection);
    if (nodes.isEmpty()) {
      nodes.add((IRouteNode) routeSection.getInitialGeom());
      nodes.add((IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes.get(0) == (routeSection.getInitialGeom())) {
      nodes.add(0, (IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes
        .get(this.nodes.size() - 1) == (routeSection.getInitialGeom())) {
      Collections.reverse(nodes);
      nodes.add(0, (IRouteNode) routeSection.getFinalNode());
    } else if (this.nodes.get(0) == (routeSection.getFinalNode())) {
      nodes.add(0, (IRouteNode) routeSection.getInitialGeom());
    } else if (this.nodes
        .get(this.nodes.size() - 1) == (routeSection.getFinalNode())) {
      Collections.reverse(nodes);
      nodes.add(0, (IRouteNode) routeSection.getInitialGeom());
    }
  }

  @Override
  public boolean contains(IRouteSection routeSection) {
    return this.routeSections.contains(routeSection);

  }

  private ArrayList<IRouteNode> nodes = new ArrayList<IRouteNode>();

  public List<IRouteNode> getNodes() {
    return nodes;
  }

  private IMultiSurface<IPolygon> polygonGeom = null;

  /**
   * Get alternate geometry for this route.
   * @return
   */
  public IMultiSurface<IPolygon> getPolygonGeom() {
    return this.polygonGeom;
  }

  public boolean isPolygonGeomNull() {
    return (this.polygonGeom == null);
  }

  @Override
  public void computeGeom() {
    List<ILineString> positions = new ArrayList<ILineString>();
    List<IPolygon> polygons = new ArrayList<IPolygon>();
    ComputeRouteSectionGeom computeRouteSectionGeom = new ComputeRouteSectionGeom();
    for (IRouteSection section : routeSections) {
      if (!section.isAlternateGeomNull()) {
        positions.add(section.getGeom());
      }
      // polygons.add(computeRouteSectionGeom.computeRouteSectionPolygon(section,
      // section.getPreviousForGeom(), section.getNextForGeom()));
    }
    // LOGGER.debug("Route " + this);
    // for (IRouteSection section : routeSections) {
    // LOGGER.debug("section " + section);
    // }

    this.polygonGeom = new GM_MultiSurface<IPolygon>(polygons);
    this.setGeom((new ConcatLineStrings()).concatLineStrings(positions));
  }
}
