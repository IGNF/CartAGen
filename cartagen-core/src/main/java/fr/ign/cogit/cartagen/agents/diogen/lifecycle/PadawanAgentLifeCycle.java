package fr.ign.cogit.cartagen.agents.diogen.lifecycle;

import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;

public interface PadawanAgentLifeCycle extends AgentLifeCycle {

  /**
   * Compute the life cycle of an agent using given environment.
   * @param agent
   * @param environment
   * @return
   * @throws InterruptedException
   */
  public AgentSatisfactionState compute(IAgent agent, Environment environment)
      throws InterruptedException;

}
