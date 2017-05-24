package fr.ign.cogit.cartagen.agents.diogen.state;

import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentStateImpl;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ISmallCompactMicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;

public class SmallCompactMicroAgentStateImpl extends SmallCompactAgentStateImpl
    implements SmallCompactMicroAgentState {

  /**
   * @param ag
   * @param previousState
   * @param action
   */
  public SmallCompactMicroAgentStateImpl(ISmallCompactMicroAgent ag,
      SmallCompactMicroAgentState previousState, Action action) {
    // Constructs a state like in CartacomAgentState
    super(ag, previousState, action);
  }

  @Override
  public ISmallCompactMicroAgent getAgent() {
    return (ISmallCompactMicroAgent) super.getAgent();
  }
}
