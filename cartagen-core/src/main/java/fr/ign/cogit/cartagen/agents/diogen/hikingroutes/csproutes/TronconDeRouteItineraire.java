package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
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

  public ICarryingRoadLine getRoadSection();

  public void setRoadSection(ICarryingRoadLine roadSection);

  public void setRoadGeom(ILineString geom);

}
