package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.core.state.MicroAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * 
 * A micro agent
 * 
 * @author JGaffuri
 * 
 */
public interface MicroAgent
    extends GeographicObjectAgent, IGeographicObjectAgentGeneralisation {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#buildCurrentState
   * (fr.ign.cogit.agentgeoxygene.state.GeographicAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  MicroAgentState buildCurrentState(AgentState previousState, Action action);

  public double getSidesOrientation();

}
