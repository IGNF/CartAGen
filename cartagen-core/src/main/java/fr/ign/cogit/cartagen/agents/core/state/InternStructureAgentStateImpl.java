package fr.ign.cogit.cartagen.agents.core.state;

import java.util.HashSet;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentStateImpl;
import fr.ign.cogit.geoxygene.contrib.agents.state.InternStructureAgentState;

/**
 * A meso agent state. This state does not concern only the state of the meso
 * agent, but a list of states of its components too, in a recursive way.
 * @author JGaffuri
 */
public class InternStructureAgentStateImpl extends
    GeographicObjectAgentStateImpl implements InternStructureAgentState {

  /**
   * the states of the components
   */
  private HashSet<GeographicObjectAgentState> componentStates = new HashSet<GeographicObjectAgentState>();

  /**
   * @return
   */
  public HashSet<GeographicObjectAgentState> getComponentStates() {
    return this.componentStates;
  }

  /**
   * @param agent
   * @param previousState
   * @param action
   */
  public InternStructureAgentStateImpl(InternStructureAgent agent,
      InternStructureAgentState previousState, Action action) {
    super(agent, previousState, action);

    // store the components states too
    for (GeographicObjectAgent ag : agent.getComponents()) {
      if (ag instanceof MicroAgent) {
        this.getComponentStates()
            .add(new MicroAgentStateImpl((MicroAgent) ag, null, action));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#clean()
   */
  @Override
  public void clean() {
    super.clean();
    for (GeographicObjectAgentState eago : this.getComponentStates()) {
      eago.clean();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#getAgent()
   */
  @Override
  public InternStructureAgent getAgent() {
    return (InternStructureAgent) super.getAgent();
  }

}
