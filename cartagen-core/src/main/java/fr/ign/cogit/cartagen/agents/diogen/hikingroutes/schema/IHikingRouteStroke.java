package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierObject;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public interface IHikingRouteStroke extends ICarrierObject, INetworkSection {
  public Set<ICarryingRoadLine> getRoads();

}
