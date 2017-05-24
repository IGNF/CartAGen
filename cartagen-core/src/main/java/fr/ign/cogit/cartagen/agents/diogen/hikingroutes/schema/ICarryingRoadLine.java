package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;

public interface ICarryingRoadLine extends IRoadLine, ICarrierNetworkSection {

  void switchCarriedObject(ICarriedObject carriedLineObject, int position);

  HikingRouteStroke getRoadStrokeForRoute();

  void setRoadStrokeForRoute(HikingRouteStroke roadStrokeForRoutes);

  boolean switchSide(boolean leftToRight);

  String getSymbo();

}
