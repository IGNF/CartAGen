package fr.ign.cogit.cartagen.agents.diogen.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * Tree Exploration Life Cycle class for agent using IODA/Padawan model. This
 * life cycle explores all the possible interactions, ordered by their trigger
 * and preconditions value. If the computation of the interaction return a worst
 * satisfaction value, the interaction is canceled.
 * http://www.lifl.fr/SMAC/projects/ioda/
 * @author AMaudet
 * 
 */

public class PadawanTreeExplorationCycle implements AgentLifeCycle {

  private static Logger logger = Logger
      .getLogger(PadawanTreeExplorationCycle.class.getName());

  /**
   * Private constructor.
   */
  private PadawanTreeExplorationCycle() {
    super();
  }

  private static PadawanTreeExplorationCycle singletonObject;

  /**
   * Return the unique instance of this class.
   * @return
   */
  public static AgentLifeCycle getInstance() {
    if (PadawanTreeExplorationCycle.singletonObject == null) {
      PadawanTreeExplorationCycle.singletonObject = new PadawanTreeExplorationCycle();
    }
    return PadawanTreeExplorationCycle.singletonObject;
  }

  /**
   * The validity satisfaction threshold: a satisfaction is said better than
   * another if its value, plus this threshold value, is greater.
   */
  private static double VALIDITY_SATISFACTION_THRESHOLD = 0.5;

  /**
   * Return a set with realizable interaction for the agent executing this life
   * cycle.
   * @param realizableInteractions
   * @return
   */
  private RealizableConstrainedInteraction chooseInteraction(
      List<RealizableConstrainedInteraction> realizableInteractions) {
    return Collections.max(realizableInteractions);
  }

  /**
   * Return an ordered list of all realizable interactions.
   * 
   * @param env
   * @param agent
   * @param previousInteraction
   * @return
   */
  private List<RealizableConstrainedInteraction> getAllRealizableInteraction(
      Environment env, IDiogenAgent agent,
      Set<RealizableConstrainedInteraction> alreadyDoneInteractionsList) {

    if (PadawanTreeExplorationCycle.logger.isDebugEnabled()) {
      for (Constraint c : agent.getConstraints()) {
        GeographicConstraint cc = (GeographicConstraint) c;
        PadawanTreeExplorationCycle.logger.debug("    Constraint " + cc
            + " satisfaction level : " + cc.getSatisfaction());
      }
    }

    return env.getRealizableInteractions(agent, alreadyDoneInteractionsList);
  }

  protected boolean storeStates = true;

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public boolean isStoreStates() {
    return true;
  }

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public void setStoreStates(boolean storeStates) {
    this.storeStates = storeStates;
  }

  /**
   * Compute an IODA life cycle. {@inheritDoc}
   */
  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {

    PadawanTreeExplorationCycle.logger.debug("******* Activation of " + agent
        + " (" + agent.getConstraints().size() + " constraint(s) )");

    // TODO : For now, we consider that an agent is contained in only one
    // environment.
    Environment env = null;
    if (((IDiogenAgent) agent).getContainingEnvironments() == null
        || ((IDiogenAgent) agent).getContainingEnvironments().isEmpty()) {
      env = ((IDiogenAgent) agent).getContainingEnvironments().iterator()
          .next();
    } else {
      env = ((IDiogenAgent) agent).getEncapsulatedEnv();
    }

    // clean the possibly previously created and stored states
    agent.cleanStates();

    // satisfaction computation
    agent.computeSatisfaction();

    // store the current state, and the best encountered state
    AgentState currentState = agent.buildCurrentState(null, null);
    AgentState bestEncounteredState = currentState;

    // mark the current state as the root state
    agent.setRootState(currentState);

    // test if the satisfaction is perfect
    if (agent.getSatisfaction() >= 100.0
        - PadawanTreeExplorationCycle.VALIDITY_SATISFACTION_THRESHOLD) {

      // perfect satisfaction
      PadawanTreeExplorationCycle.logger
          .debug(" Perfect initial state (S=" + agent.getSatisfaction() + ")");

      // clean stored states
      if (!this.isStoreStates()) {
        agent.cleanStates();
      }

      // clean actions list
      agent.cleanActionsToTry();

      // end
      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    // a map to store the number of times an interaction has been applied to
    // agent
    Map<AgentState, List<RealizableConstrainedInteraction>> interactionsForState = new HashMap<AgentState, List<RealizableConstrainedInteraction>>();

    // the loop
    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;
    List<RealizableConstrainedInteraction> interactionsList;

    interactionsList = this.getAllRealizableInteraction(env,
        (IDiogenAgent) agent, null);

    PadawanTreeExplorationCycle.logger
        .debug("Interactions #  " + interactionsList.size());

    interactionsForState.put(currentState, interactionsList);
    int i = 0;

    Interaction previousInteraction = null;

    Set<RealizableConstrainedInteraction> alreadyDoneInteractionsList = new HashSet<RealizableConstrainedInteraction>();

    while (true) {

      // test if no more actions to try
      if (interactionsList.size() == 0) {

        // test if there is a previous state
        if (currentState.getPreviousState() != null) {

          // there is a previous state: go back to this state, and try to
          // compute new actions
          PadawanTreeExplorationCycle.logger.debug(
              "   No more action to try in current state: back to the previous state");

          currentState = currentState.getPreviousState();
          agent.goBackToState(currentState);
          interactionsList = interactionsForState.get(currentState);
          continue;
        }

        // there is no previous state: the current state is the initial one. The
        // whole tree has been explored.
        // all possible interactions have been tried. The best possible state
        // has
        // been reached.

        // back to the best encountered state
        agent.goBackToState(bestEncounteredState);
        PadawanTreeExplorationCycle.logger
            .debug("******* End of activation of " + agent
                + "; no more action to try. Back to the best encountered state (S="
                + agent.getSatisfaction() + ")");

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        return out;
      }

      // for(RealizableConstrainedInteraction ii : interactionsList){
      // logger.debug("Interaction " + ii + " preconditions= " +
      // ii.getPreconditionsValue() + " trigger " + ii.getTriggerValue());
      // }

      // if too many states have been encountered: it is enough! We stop here.
      if (i > 50) {
        // too many states have been encountered: it is enough! We stop here.

        // back to the best encountered state
        agent.goBackToState(bestEncounteredState);
        PadawanTreeExplorationCycle.logger.debug("******* End of activation of "
            + agent + "; too many encountered states (S="
            + agent.getSatisfaction() + ")");

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        return out;
      }
      i++;

      if (PadawanTreeExplorationCycle.logger.isDebugEnabled()) {
        for (RealizableConstrainedInteraction interaction : interactionsList) {
          PadawanTreeExplorationCycle.logger.debug("    Realizable interaction "
              + interaction.getInteraction().getName() + " trigger level : "
              + interaction.getTriggerValue());

        }

      }
      // choose one of the interaction
      RealizableConstrainedInteraction performedInteraction = this
          .chooseInteraction(interactionsList);

      interactionsForState.get(currentState).remove(performedInteraction);

      try {
        performedInteraction.perform();

        alreadyDoneInteractionsList.add(performedInteraction);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new InterruptedException();
      }

      // compute the new satisfaction
      agent.computeSatisfaction();

      // get the new actions from constraints
      // agent.updateActionProposals();

      // build the new state
      currentState = agent.buildCurrentState(currentState,
          performedInteraction.getInteraction().getAction());

      // perfect satisfaction
      if (agent.getSatisfaction() >= 100.0
          - PadawanTreeExplorationCycle.VALIDITY_SATISFACTION_THRESHOLD) {
        PadawanTreeExplorationCycle.logger
            .debug("   SUCCESS -> perfect state reached (S="
                + agent.getSatisfaction() + ")");

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
      }
      // valid state
      if (currentState.isValid(
          PadawanTreeExplorationCycle.VALIDITY_SATISFACTION_THRESHOLD)) {

        previousInteraction = performedInteraction.getInteraction();

        interactionsList = this.getAllRealizableInteraction(env,
            (IDiogenAgent) agent, alreadyDoneInteractionsList);

        PadawanTreeExplorationCycle.logger.debug(
            "   SUCCESS -> valid state (S=" + agent.getSatisfaction() + ")");
        PadawanTreeExplorationCycle.logger.debug(
            "   " + interactionsList.size() + " new interaction(s) to try");
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;

        interactionsForState.put(currentState, interactionsList);

        // if the current state satisfaction is better than the best one, mark
        // the current state as the best encountered one
        if (currentState.getSatisfaction() > bestEncounteredState
            .getSatisfaction()) {
          PadawanTreeExplorationCycle.logger
              .debug("   best encountered state !");
          bestEncounteredState = currentState;
        }
      }
      // non valid state
      else {
        PadawanTreeExplorationCycle.logger.debug(
            "   FAILURE -> non valid state (satisfaction worst or unchanged, S="
                + agent.getSatisfaction() + ")");
        // go back to the previous state
        currentState = currentState.getPreviousState();
        agent.goBackToState(currentState);
      }
    }
  }
}
