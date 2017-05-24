package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedNetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public interface IRouteSection extends INetworkSection, ICarriedNetworkSection,
    IStyleDependingGeomFeature {

  public void setName(String name);

  public String getName();

  public String getSymbo();

  public IRouteSection getNext();

  public IRouteSection getPrevious();

  public void setRoute(IRoute route);

  public IRoute getRoute();

  public boolean fromSameRoute(IRouteSection otherRoute);

  public ILineString getOffsetGeom();

  public ILineString getOffsetGeomForPolygon();

  public boolean isAlternateGeomNull();

  @Override
  public ICarryingRoadLine getCarrierObject();

  public void setCarrierObject(ICarryingRoadLine carrierObject);

  public IRouteSection getNextForGeom();

  public IRouteSection getPreviousForGeom();

}
