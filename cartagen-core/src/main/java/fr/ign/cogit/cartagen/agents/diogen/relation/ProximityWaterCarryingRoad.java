package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;

public class ProximityWaterCarryingRoad extends ProximityCarryingRoad {

  public ProximityWaterCarryingRoad(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
  }

}
