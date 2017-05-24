package fr.ign.cogit.cartagen.agents.core.state;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;

/**
 * 
 * A micro agent state
 * 
 * @author JGaffuri
 * 
 */
public interface MicroAgentState extends GeographicObjectAgentState {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#getAgent()
   */
  @Override
  public MicroAgent getAgent();

}
