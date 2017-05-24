package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public abstract class HikingFactory extends AbstractCreationFactory {

  public abstract IHikingRouteSection createTouristRoute(ILineString line,
      String name);

  public abstract IHikingRouteSection createTouristRoute(String id,
      ILineString line, String name);

  public abstract IHikingRouteSection createTouristRoute(ILineString line,
      String name, String symbo);

  public abstract IHikingRouteSection createTouristRoute(String id,
      ILineString line, String name, String symbo);

}
