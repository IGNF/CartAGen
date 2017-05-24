package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartacomMicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;

public interface ICarryingRoadStrokeAgent
    extends ICartacomMicroAgent, IDiogenAgent {

  public IHikingRouteStroke getFeature();

}
