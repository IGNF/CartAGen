package fr.ign.cogit.cartagen.agents.diogen.lifecycle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * Basic Life Cycle class for agent using IODA model. In this life cycle, we
 * apply the interaction in the order giver by the computation of the trigger
 * and precondition values of the interactions given by the matrix. If the
 * computation of the interaction return a worst satisfaction value, the
 * interaction is canceled. This life cycle dont'n allow the consecutive use of
 * the same interaction. http://www.lifl.fr/SMAC/projects/ioda/
 * @author AMaudet
 * 
 */
public class PadawanBasicLifeCycle implements AgentLifeCycle {

  private static Logger logger = LogManager
      .getLogger(PadawanBasicLifeCycle.class.getName());

  /**
   * Private constructor.
   */
  private PadawanBasicLifeCycle() {
    super();
  }

  private static PadawanBasicLifeCycle singletonObject;

  /**
   * Return the unique instance of this class.
   * @return
   */
  public static AgentLifeCycle getInstance() {
    if (PadawanBasicLifeCycle.singletonObject == null) {
      PadawanBasicLifeCycle.singletonObject = new PadawanBasicLifeCycle();
    }
    return PadawanBasicLifeCycle.singletonObject;
  }

  /**
   * The validity satisfaction threshold: a satisfaction is said better than
   * another if its value, plus this threshold value, is greater.
   */
  private static double VALIDITY_SATISFACTION_THRESHOLD = 0.5;

  /**
   * Return a set with realizable agent for the agent executing this life cycle.
   * @param realizableInteractions
   * @return
   */
  private RealizableConstrainedInteraction chooseInteraction(
      List<RealizableConstrainedInteraction> realizableInteractions) {
    return realizableInteractions.iterator().next();
  }

  protected boolean storeStates;

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public boolean isStoreStates() {
    return this.storeStates;
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

    // TODO
    // For now, we consider that an agent is contained in only one environment.
    Environment env = ((IDiogenAgent) agent).getContainingEnvironments()
        .iterator().next();
    // TODO

    // logger.debug("Environment : " + env);
    // logger.debug("Type : " +
    // env.getEnvironmentType().getEnvironmentTypeName());
    // logger.debug("Matrix : " + env.getInteractionMatrix());
    // Set<Environment> containingEnvironments =
    // agent.getContainingEnvironments();

    // clean the possibly previously created and stored states
    // agent.cleanStates();
    // satisfaction computation
    agent.computeSatisfaction();
    // store current state
    AgentState currentState = agent.buildCurrentState(null, null);

    // test if the satisfaction is perfect
    if (agent.getSatisfaction() >= 100.0
        - PadawanBasicLifeCycle.VALIDITY_SATISFACTION_THRESHOLD) {
      PadawanBasicLifeCycle.logger
          .debug("Agent : " + agent + " is already satisfied.");
      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    // the agent is not satisfied: try to improve its satisfaction by
    // triggering some actions
    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;
    int i = 0;
    PadawanBasicLifeCycle.logger.debug("Agent : " + agent + " (type "
        + agent.getClass() + " ) begin its life cycle with the satisfaction "
        + agent.getSatisfaction());

    // Initialize the list of failed try.
    Set<Interaction> failList = new HashSet<Interaction>();

    while (true) {

      // if too many states have been encountered: it is enough! We stop here.
      if (i > 10) {
        return out;
      }
      i++;

      // Get all the most triggered realizable interactions for this agent in
      // Environment env.
      // Set<RealizableConstrainedInteraction> realizableInteractions;
      List<RealizableConstrainedInteraction> interactionsList;

      interactionsList = this.getAllRealizableInteraction(env,
          (IDiogenAgent) agent);
      // realizableInteractions =
      // this.getMostTriggeredRealizableInteraction(env, agent, failList);

      // if they are no RealizableInteraction, do nothing.
      if (interactionsList == null) {
        return out;
      } else if (interactionsList.isEmpty()) {
        return out;
      }

      // choose one of the interaction
      RealizableConstrainedInteraction performedInteraction = this
          .chooseInteraction(interactionsList);

      try {
        performedInteraction.perform();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new InterruptedException();
      }

      // compute the new satisfaction
      agent.computeSatisfaction();

      // build the new state
      currentState = agent.buildCurrentState(currentState,
          performedInteraction.getInteraction().getAction());

      if (agent.getSatisfaction() >= 100.0
          - PadawanBasicLifeCycle.VALIDITY_SATISFACTION_THRESHOLD) {
        PadawanBasicLifeCycle.logger
            .debug("Agent " + agent.getId() + " choose to perform intertaction "
                + performedInteraction.getInteraction().getClass()
                + " and its new satisfaction is " + agent.getSatisfaction()
                + " : it is satisfied");
        // logger.debug("Agent : " + agent + " is satisfied.");
        return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
      } else if (currentState
          .isValid(PadawanBasicLifeCycle.VALIDITY_SATISFACTION_THRESHOLD)) {
        PadawanBasicLifeCycle.logger
            .debug("Agent " + agent.getId() + " choose to perform intertaction "
                + performedInteraction.getInteraction().getClass()
                + " and its new satisfaction is " + agent.getSatisfaction()
                + " : it is improved but not perfect.");

        // logger.debug("Agent : " + agent + " is improved but not perfect.");

        failList.clear();
        failList.add(performedInteraction.getInteraction());
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;
      } else {
        // logger.debug("The satisfaction of " + agent + " is worst.");
        // go back to the previous state
        failList.add(performedInteraction.getInteraction());
        PadawanBasicLifeCycle.logger
            .debug("Agent " + agent.getId() + " choose to perform intertaction "
                + performedInteraction.getInteraction().getClass()
                + " and its new satisfaction is " + agent.getSatisfaction()
                + " : it is worst.");
        // logger.debug("state " + currentState.getClass());
        // logger.debug("state " +(currentState ==
        // currentState.getPreviousState()));
        currentState = currentState.getPreviousState();
        // logger.debug("state " + currentState.getClass());
        agent.goBackToState(currentState);
      }
    }
  }

  private List<RealizableConstrainedInteraction> getAllRealizableInteraction(
      Environment env, IDiogenAgent agent) {
    return env.getRealizableInteractions(agent, null);
  }

}
