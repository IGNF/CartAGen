package fr.ign.cogit.cartagen.agents.diogen.padawan;

import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * This AgentState class is a class specific for the Padawan based life cycle.
 * This class is an adapater for another AgentState. The specifity of a Padawan
 * Agent is to not use Action.
 * @author AMaudet
 * 
 */
public interface PadawanAgentState extends AgentState {

  public AgentState getState();

}
