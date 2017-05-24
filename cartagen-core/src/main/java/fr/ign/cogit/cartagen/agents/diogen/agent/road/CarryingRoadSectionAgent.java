package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartAComAgentDeformableGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;

public class CarryingRoadSectionAgent extends RoadSectionAgent implements
    ICarryingRoadSectionAgent, ICartAComAgentDeformableGeneralisation {

  public CarryingRoadSectionAgent(NetworkAgent netwAg, ICarryingRoadLine obj) {
    super(netwAg, obj);
  }

  @Override
  public ICarryingRoadLine getFeature() {
    return (ICarryingRoadLine) super.getFeature();
  }

}
