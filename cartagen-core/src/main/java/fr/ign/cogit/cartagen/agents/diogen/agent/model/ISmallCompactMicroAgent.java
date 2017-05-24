package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.diogen.state.SmallCompactMicroAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

public interface ISmallCompactMicroAgent
    extends ISmallCompactAgent, ICartacomMicroAgent, IDiogenAgent {

  @Override
  SmallCompactMicroAgentState buildCurrentState(AgentState previousState,
      Action action);

  public void goBackToState(SmallCompactMicroAgentState state);

}
