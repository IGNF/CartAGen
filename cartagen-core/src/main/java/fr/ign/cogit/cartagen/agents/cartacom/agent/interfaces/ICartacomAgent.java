/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionFailure;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * Agent that shares relational constraints with other agents and is able to
 * communicate with other agents
 * 
 * @author CDuchene
 * 
 */
public interface ICartacomAgent
    extends ConversationalObject, GeographicObjectAgent {

  /**
   * Builds the agent's internal representation of its spatial environment. Only
   * builds what cannot be built in the agent's constructor - e.g. because it
   * requires all other CartACom agents to be already built. This method is
   * called once, before any agent is activated.
   */
  void initialiseEnvironmentRepresentation();

  /**
   * Updates the agent's internal representation of its spatial environment. To
   * be triggered just after a modification of the agent.
   */
  void updateEnvironmentRepresentation();

  /**
   * Identifies and instantiates the relational constraints that are relevant
   * for this agent.
   */
  void initialiseRelationalConstraints();

  /**
   * Retrieves, among the constraints of the agent, the ones that are relational
   * constraints
   * @return a set of the relational constraints of the agent
   */
  Set<RelationalConstraint> getRelationalConstraints();

  /**
   * Retrieves, agent sharing constraints
   * @return a set of agents
   */
  Set<IAgent> getAgentsSharingRelation();

  /**
   * Retrieves a list of the actions failures encountered by this Cartacom
   * agent.
   * @return a list of the failures.
   */
  List<ActionFailure> getFailures();

  /**
   * Searches if an action is likely to generate a failure on an agent, by
   * looking for a failure related to a similar action in the failures list of
   * the agent.
   * @param action the action being searched for
   * @return {@code true} if a similar action is found in the failures list,
   *         {@code false} otherwise
   */
  boolean isActionInFailuresList(Action action);

  /**
   * Retrieves the current state of this cartacom agent
   * @return the agent's current state
   */
  // TODO Remonter sur classes d'agents plus generiques??
  CartacomAgentState getCurrentState();

  @Override
  CartacomAgentState buildCurrentState(AgentState previousState, Action action);

  /**
   * Indicates whether this agent has been modified during the current
   * activation.
   * @return {@code true} if the agent has been modified, {@code false}
   *         otherwise.
   */
  boolean hasModifiedItselfDuringThisActivation();

  /**
   * Acknowledges on this agent the fact that it has just been modified by a
   * task. Triggered at the end of the task or just after it, once it is sure
   * that the modification will not be backtracked.
   */
  void manageHavingJustBeenModifiedByATask(
      boolean changeCentroidRelatedConstrainedZone);

  /**
   * Update tasks status
   */
  void updateTasksStatus();

  /**
   * 
   */
  void executeCascadingTasks();

  /**
   * 
   */
  Set<ActionProposal> synthesizePossibleActions();

  /**
   * 
   * @return
   */
  boolean isWaiting();

  /**
   * 
   * @param isWaiting
   */
  void setWaiting(boolean isWaiting);

  /**
   * 
   */
  void eraseEndedConversations();

  void createTaskFromActionProposal(CartacomAction action);
}
