package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.MicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

public interface ICartacomMicroAgent
    extends ICartAComAgentGeneralisation, MicroAgent {
  @Override
  CartacomMicroAgentState buildCurrentState(AgentState previousState,
      Action action);

  public void goBackToState(CartacomMicroAgentState state);

}
