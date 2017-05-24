package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartacomMicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;

public interface ICarryingRoadSectionAgent extends ICartacomMicroAgent {

  @Override
  public ICarryingRoadLine getFeature();

}
