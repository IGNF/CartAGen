package fr.ign.cogit.cartagen.agents.diogen.state;

import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentState;
import fr.ign.cogit.cartagen.agents.core.state.MicroAgentState;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartacomMicroAgent;

public interface CartacomMicroAgentState
    extends MicroAgentState, CartacomAgentState {

  public ICartacomMicroAgent getAgent();

}
