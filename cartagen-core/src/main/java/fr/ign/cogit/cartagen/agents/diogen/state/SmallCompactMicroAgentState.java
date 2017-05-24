package fr.ign.cogit.cartagen.agents.diogen.state;

import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentState;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ISmallCompactMicroAgent;

public interface SmallCompactMicroAgentState
    extends CartacomMicroAgentState, SmallCompactAgentState {

  public ISmallCompactMicroAgent getAgent();

}
