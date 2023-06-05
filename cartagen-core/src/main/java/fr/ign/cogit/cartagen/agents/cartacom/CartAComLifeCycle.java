package fr.ign.cogit.cartagen.agents.cartacom;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.InternalGeneralisationAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;

public class CartAComLifeCycle implements AgentLifeCycle {

  private static Logger logger = LogManager
      .getLogger(CartAComLifeCycle.class.getName());

  /**
   * Private constructor.
   */
  private CartAComLifeCycle() {
    super();
  }

  private static CartAComLifeCycle singletonObject;

  /**
   * Return the unique instance of this class.
   * @return
   */
  public static AgentLifeCycle getInstance() {
    if (CartAComLifeCycle.singletonObject == null) {
      CartAComLifeCycle.singletonObject = new CartAComLifeCycle();
    }
    return CartAComLifeCycle.singletonObject;
  }

  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {

    // Begining of the life cycle.
    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Begin Cartacom lifecycle for " + agent);
    }
    ICartacomAgent cAgent = (ICartacomAgent) agent;

    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Is agent waiting ? " + cAgent.isWaiting());
    }
    // TODO Suppress failures with validity = nextAction

    // Handle received messages, and create associated tasks
    if (CartAComLifeCycle.logger.isDebugEnabled()) {
      CartAComLifeCycle.logger
          .debug("Messages : " + cAgent.getOnGoingConversations());
    }
    cAgent.handleReceivedMessages();

    // Destroy ended conversation
    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Erase ended conversation.");
    }
    cAgent.eraseEndedConversations();

    // Update tasks status
    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Update task status.");
    }
    cAgent.updateTasksStatus();

    // Create an internal generalisation task.
    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Internal generalisation task.");
    }
    cAgent
        .createTaskFromActionProposal(new InternalGeneralisationAction(cAgent));
    if (CartAComLifeCycle.logger.isDebugEnabled()) {
      CartAComLifeCycle.logger.debug("Task : " + cAgent.getTasks());
    }

    // Execute cascading tasks.
    cAgent.executeCascadingTasks();
    if (CartAComLifeCycle.logger.isInfoEnabled()) {
      CartAComLifeCycle.logger.info("Internal generalisation task done.");
    }

    // TODO Update status. Manage if the agent is destroyed.

    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;
    while (true) {

      if (cAgent.isWaiting()) {
        if (CartAComLifeCycle.logger.isDebugEnabled()) {
          logger.debug("Status waiting.");
          logger.debug("Waiting for " + cAgent.getOnGoingConversations());
        }
        return out;
      }

      // Synthesize possible actions.
      Set<ActionProposal> actionsProposals = cAgent.synthesizePossibleActions();
      if (CartAComLifeCycle.logger.isInfoEnabled()) {
        CartAComLifeCycle.logger
            .info("Synthetise possible actions : " + actionsProposals);
      }

      // test if there are actions to try
      if (actionsProposals.size() == 0) {
        // all possible action have been tried: the best possible state as been
        // reached.
        // clean actions list
        if (CartAComLifeCycle.logger.isDebugEnabled()) {
          logger.debug("No action to try.");
        }
        agent.cleanActionsToTry();
        return out;
      }

      // Create specific Tasks for each action available.
      if (CartAComLifeCycle.logger.isInfoEnabled()) {
        CartAComLifeCycle.logger.info("Create tasks.");
      }
      for (ActionProposal proposal : actionsProposals) {
        CartacomAction cAction = (CartacomAction) proposal.getAction();
        cAgent.createTaskFromActionProposal(cAction);
      }

      // Update tasks status.
      if (CartAComLifeCycle.logger.isInfoEnabled()) {
        CartAComLifeCycle.logger.info("Update tasks status.");
      }
      cAgent.updateTasksStatus();

      // Execute cascading tasks.
      if (CartAComLifeCycle.logger.isDebugEnabled()) {
        CartAComLifeCycle.logger
            .debug("Satisfaction before : " + agent.getSatisfaction());
        CartAComLifeCycle.logger
            .debug("Execute Cascading Tasks : " + cAgent.getTasks());
      }
      cAgent.executeCascadingTasks();

      if (CartAComLifeCycle.logger.isDebugEnabled()) {
        CartAComLifeCycle.logger
            .debug("Satisfaction after : " + agent.getSatisfaction());
        CartAComLifeCycle.logger
            .debug("Failures list : " + cAgent.getFailures());
      }

      // build the new state
      // currentState = agent.buildCurrentState(currentState, actionToTry);
      // perfect satisfaction
      if (agent.getSatisfaction() >= 5.0
          - CartAComLifeCycle.getValiditySatisfactionTreshold()) {

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }
        // clean actions list
        agent.cleanActionsToTry();
        if (logger.isInfoEnabled())
          logger.info("Agent " + agent.toString()
              + " has a perfect satisfaction after cycle");
        return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
      } else {
        if (logger.isInfoEnabled())
          logger.info("Agent " + agent.toString()
              + " has an improved satisfaction after cycle");
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;
      }
    }
  }

  private static double getValiditySatisfactionTreshold() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isStoreStates() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setStoreStates(boolean storeStates) {
    // TODO Auto-generated method stub

  }

}
