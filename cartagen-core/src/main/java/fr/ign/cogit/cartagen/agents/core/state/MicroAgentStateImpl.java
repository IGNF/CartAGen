package fr.ign.cogit.cartagen.agents.core.state;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentStateImpl;

/**
 * 
 * A micro agent state
 * 
 * @author JGaffuri
 * 
 */
public class MicroAgentStateImpl extends GeographicObjectAgentStateImpl
    implements MicroAgentState {

  /**
   * @param agent
   * @param previousState
   * @param action
   */
  public MicroAgentStateImpl(MicroAgent agent, MicroAgentState previousState,
      Action action) {
    super(agent, previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#getAgent()
   */
  @Override
  public MicroAgent getAgent() {
    return (MicroAgent) super.getAgent();
  }

}
