package fr.ign.cogit.cartagen.schematisation.buslinemap;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;

/**
 * This class extends ArcReseau. It's used to create RoadStrokeForRoutes stroke.
 * @author JTeulade-Denantes
 * 
 */
public interface TronconDeRouteItineraire extends TronconDeRoute {

  public String getSymbo();

  public int getImportance();

  public Set<String> getRoutesName();

  public ICarrierNetworkSection getRoadSection();

  public void setRoadSection(ICarrierNetworkSection roadSection);

  public void setRoadGeom(ILineString geom);

}
