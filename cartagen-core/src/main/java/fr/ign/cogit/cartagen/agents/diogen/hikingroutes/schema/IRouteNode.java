package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import fr.ign.cogit.cartagen.agents.diogen.schema.IBidirectionalNetworkNode;

public interface IRouteNode extends IBidirectionalNetworkNode {

  public int getPositionForRouteSection(IRouteSection routeSection);
}
