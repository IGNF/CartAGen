package fr.ign.cogit.cartagen.agents.diogen.state;

import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentStateImpl;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartacomMicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ISmallCompactMicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;

public class CartacomMicroAgentStateImpl extends CartacomAgentStateImpl
    implements CartacomMicroAgentState {

  /**
   * @param ag
   * @param previousState
   * @param action
   */
  public CartacomMicroAgentStateImpl(ICartacomMicroAgent ag,
      CartacomMicroAgentState previousState, Action action) {
    // Constructs a state like in CartacomAgentState
    super(ag, previousState, action);
  }

  @Override
  public ICartacomMicroAgent getAgent() {
    return (ISmallCompactMicroAgent) super.getAgent();
  }

}
