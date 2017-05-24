package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public interface IRoute extends INetworkSection, IStyleDependingGeomFeature {

  void add(IRouteSection routeSection);

  boolean contains(IRouteSection next);

  void add(int i, IRouteSection previous);

  List<IRouteSection> getRouteSections();

  List<IRouteNode> getNodes();

}
