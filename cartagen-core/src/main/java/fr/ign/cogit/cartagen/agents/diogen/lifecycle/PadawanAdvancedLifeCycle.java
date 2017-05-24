package fr.ign.cogit.cartagen.agents.diogen.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.task.Task;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealisableConstrainedMultipleTargetsAggregatedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.tasks.TryInteractionTask;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
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

public class PadawanAdvancedLifeCycle implements AgentLifeCycle {

  private static Logger logger = Logger
      .getLogger(PadawanTreeExplorationCycle.class.getName());

  /**
   * Private constructor.
   */
  private PadawanAdvancedLifeCycle() {
    super();
  }

  private static PadawanAdvancedLifeCycle singletonObject;

  /**
   * Return the unique instance of this class.
   * @return
   */
  public static AgentLifeCycle getInstance() {
    if (PadawanAdvancedLifeCycle.singletonObject == null) {
      PadawanAdvancedLifeCycle.singletonObject = new PadawanAdvancedLifeCycle();
    }
    return PadawanAdvancedLifeCycle.singletonObject;
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
      List<RealizableConstrainedInteraction> realizableInteractions,
      List<RealizableConstrainedInteraction> otherInteractions) {

    logger.debug("Begin : Choose of Interaction in " + realizableInteractions);
    Map<ConstrainedMultipleTargetsInteraction, Set<RealizableConstrainedInteraction>> realisableInteractionMap = new HashMap<ConstrainedMultipleTargetsInteraction, Set<RealizableConstrainedInteraction>>();
    Map<ConstrainedMultipleTargetsInteraction, Set<RealizableConstrainedInteraction>> otherRealisableInteractionMap = new HashMap<ConstrainedMultipleTargetsInteraction, Set<RealizableConstrainedInteraction>>();

    // for each interaction identified as realisable
    for (RealizableConstrainedInteraction realisableInteraction : realizableInteractions) {
      if (realisableInteraction == null) {
        continue;
      }
      Interaction interaction = realisableInteraction.getInteraction();
      // test if the interaction is aggregable
      if (!(interaction instanceof AggregableInteraction)) {
        continue;
      }
      AggregableInteraction aggregableInteraction = (AggregableInteraction) interaction;
      logger.debug("Try to aggregate interaction " + aggregableInteraction);

      // compare with other realisable interactions
      for (RealizableConstrainedInteraction realisableInteraction2 : realizableInteractions) {

        if (realisableInteraction2 == null) {
          continue;
        }

        if (realisableInteraction == realisableInteraction2) {
          continue;
        }

        Interaction interaction2 = realisableInteraction2.getInteraction();
        // test if the interaction is aggregable
        if (!(interaction2 instanceof AggregableInteraction)) {
          continue;
        }
        AggregableInteraction aggregableInteraction2 = (AggregableInteraction) interaction2;

        logger.debug("Try to aggregate interaction " + aggregableInteraction
            + " with " + aggregableInteraction2);

        // Test if its action is aggregable with the action of this task
        if (!aggregableInteraction
            .testAggregableWithInteraction(aggregableInteraction2)) {
          logger.debug(aggregableInteraction2 + "is not aggregable with "
              + aggregableInteraction);
          continue;
        }

        Set<RealizableConstrainedInteraction> interactionsToAggregate = realisableInteractionMap
            .get(aggregableInteraction.getAggregatedInteraction());
        if (interactionsToAggregate == null) {
          interactionsToAggregate = new HashSet<RealizableConstrainedInteraction>();
          realisableInteractionMap.put(
              aggregableInteraction.getAggregatedInteraction(),
              interactionsToAggregate);
          interactionsToAggregate.add(realisableInteraction);
        }
        logger.debug(
            "List of interactions to aggregate " + interactionsToAggregate);
        interactionsToAggregate.add(realisableInteraction2);
        logger.debug("Add " + realisableInteraction2
            + " to list of interaction to aggregate");
      }

      logger.debug("Other interactions " + otherInteractions);

      for (RealizableConstrainedInteraction realisableInteraction2 : otherInteractions) {
        if (realisableInteraction2 == null) {
          continue;
        }

        if (realisableInteraction == realisableInteraction2) {
          continue;
        }

        Interaction interaction2 = realisableInteraction2.getInteraction();
        // Test if the task is a TryActionTask
        if (!(interaction2 instanceof AggregableInteraction)) {
          continue;
        }
        AggregableInteraction aggregableInteraction2 = (AggregableInteraction) interaction2;
        logger.debug("Try to aggregate interaction " + aggregableInteraction
            + " with the non triggering interaction " + aggregableInteraction2);

        // if (otherRealisableInteractionMap.get(aggregableInteraction2
        // .getAggregatedInteraction()) != null) {
        // continue;
        // }

        // Test if its action is aggregable with the action of this task
        if (!aggregableInteraction
            .testAggregableWithInteraction(aggregableInteraction2)) {
          logger.debug(aggregableInteraction2 + "is not aggregable with "
              + aggregableInteraction);
          continue;
        }

        Set<RealizableConstrainedInteraction> interactionsToAggregate = otherRealisableInteractionMap
            .get(aggregableInteraction.getAggregatedInteraction());
        if (interactionsToAggregate == null) {
          interactionsToAggregate = new HashSet<RealizableConstrainedInteraction>();
          otherRealisableInteractionMap.put(
              aggregableInteraction.getAggregatedInteraction(),
              interactionsToAggregate);
          // interactionsToAggregate.add(e)
        }
        logger.debug(
            "List of interactions to aggregate " + interactionsToAggregate);
        interactionsToAggregate.add(realisableInteraction2);
        logger.debug("Add " + realisableInteraction2
            + " to list of interaction to aggregate");
      }

    }

    for (ConstrainedMultipleTargetsInteraction cmti : realisableInteractionMap
        .keySet()) {

      Set<RealizableConstrainedInteraction> set = realisableInteractionMap
          .get(cmti);
      IDiogenAgent source = null;
      Environment environment = null;
      Set<IDiogenAgent> targets = new HashSet<IDiogenAgent>();
      Set<GeographicConstraint> constraintsToSatisfy = new HashSet<GeographicConstraint>();
      Set<GeographicConstraint> triggeringConstraints = new HashSet<GeographicConstraint>();
      Set<GeographicConstraint> requestLinkedConstraints = new HashSet<GeographicConstraint>();
      for (RealizableConstrainedInteraction rci : set) {
        source = rci.getSource();
        environment = rci.getEnvironment();
        targets.add(rci.getTarget());
        constraintsToSatisfy.addAll(rci.getConstraints());
        triggeringConstraints.addAll(rci.getConstraints());
      }

      Set<RealizableConstrainedInteraction> otherSet = otherRealisableInteractionMap
          .get(cmti);
      for (RealizableConstrainedInteraction rci : otherSet) {
        targets.add(rci.getTarget());
        constraintsToSatisfy.addAll(rci.getConstraints());
      }

      RealisableConstrainedMultipleTargetsAggregatedInteraction toAdd = new RealisableConstrainedMultipleTargetsAggregatedInteraction(
          cmti, source, targets, environment, constraintsToSatisfy,
          triggeringConstraints, requestLinkedConstraints);

      logger.debug("constraintsToSatisfy " + constraintsToSatisfy);
      logger.debug("triggeringConstraints " + triggeringConstraints);
      logger.debug("requestLinkedConstraints " + requestLinkedConstraints);

      realizableInteractions.removeAll(set);
      realizableInteractions.add(toAdd);

    }
    // RealisableConstrainedMultipleTargetsAggregatedInteraction

    return Collections.max(realizableInteractions);
  }

  /**
   * Return an ordered list of all realizable interactions.
   * 
   * @param env
   * @param agent
   * @param alreadyDoneInteractionsList
   * @return
   */
  private List<RealizableConstrainedInteraction> getAllRealizableInteraction(
      Environment env, IDiogenAgent agent,
      Set<RealizableConstrainedInteraction> alreadyDoneInteractionsList) {
    return env.getRealizableInteractions(agent, alreadyDoneInteractionsList);
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

  private boolean suspended = false;

  public void suspendLifeCycle() {
    this.suspended = true;
  }

  public void unsuspendLifeCycle() {
    this.suspended = false;
  }

  /**
   * Compute an IODA life cycle. {@inheritDoc}
   */
  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {

    PadawanAdvancedLifeCycle.logger.debug("******* Activation of " + agent
        + " (" + agent.getConstraints().size() + " constraint(s) )");

    // TODO : For now, we consider that an agent is contained in only one
    // environment.
    Set<Environment> envs = ((IDiogenAgent) agent).getContainingEnvironments();
    PadawanAdvancedLifeCycle.logger.debug("Get the environments " + envs);

    Integer nbEx = numberOfExecutions.get(agent);
    if (nbEx == null) {
      nbEx = 0;
    }
    numberOfExecutions.put(agent, nbEx + 1);

    ICartacomAgent cAgent = (ICartacomAgent) agent;

    // cAgent.computeSatisfaction();

    // Handle received messages, and create associated tasks

    PadawanAdvancedLifeCycle.logger.debug("Manage messages");
    cAgent.handleReceivedMessages();
    // PadawanAdvancedLifeCycle.logger.debug("On going conversation "
    // + cAgent.getOnGoingConversations());
    // PadawanAdvancedLifeCycle.logger
    // .debug("Tasks " + cAgent.getGeneratedTasks());
    Map<RealizableConstrainedInteraction, TryInteractionTask> interactionsFromConversation = new HashMap<RealizableConstrainedInteraction, TryInteractionTask>();
    for (Task task : cAgent.getGeneratedTasks()) {
      if (task instanceof TryInteractionTask) {
        interactionsFromConversation
            .put((RealizableConstrainedInteraction) ((TryInteractionTask) task)
                .getRealisableInteraction(), (TryInteractionTask) task);
      }
    }

    cAgent.eraseEndedConversations();
    // PadawanAdvancedLifeCycle.logger
    // .debug("Tasks " + cAgent.getGeneratedTasks());
    cAgent.updateTasksStatus();
    // PadawanAdvancedLifeCycle.logger
    // .debug("Tasks " + cAgent.getGeneratedTasks());
    // cAgent.executeCascadingTasks();

    // clean the possibly previously created and stored states
    // agent.cleanStates();

    // satisfaction computation
    agent.computeSatisfaction();

    // store the current state, and the best encountered state
    AgentState currentState = agent.buildCurrentState(null, null);
    AgentState bestEncounteredState = currentState;

    // mark the current state as the root state
    agent.setRootState(currentState);

    // test if the satisfaction is perfect
    if (agent.getSatisfaction() >= 100.0
        - PadawanAdvancedLifeCycle.VALIDITY_SATISFACTION_THRESHOLD) {
      // perfect satisfaction
      PadawanAdvancedLifeCycle.logger
          .debug(" perfect initial state (S=" + agent.getSatisfaction() + ")");
      // clean stored states
      if (!this.isStoreStates()) {
        agent.cleanStates();
      }
      // clean actions list
      agent.cleanActionsToTry();
      // end
      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    PadawanAdvancedLifeCycle.logger
        .debug("   Initial satisfaction S=" + agent.getSatisfaction());

    // a map to store the number of times an interaction has been applied to
    // agent
    Map<AgentState, List<RealizableConstrainedInteraction>> interactionsForState = new HashMap<AgentState, List<RealizableConstrainedInteraction>>();

    // the loop
    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;

    Set<RealizableConstrainedInteraction> interactionsToExclude = new HashSet<RealizableConstrainedInteraction>();
    interactionsToExclude.addAll(interactionsFromConversation.keySet());

    List<RealizableConstrainedInteraction> interactionsList = new ArrayList<>();
    for (Environment env : envs) {
      interactionsList.addAll(this.getAllRealizableInteraction(env,
          (IDiogenAgent) agent, interactionsToExclude));
    }
    interactionsList.addAll(interactionsFromConversation.keySet());

    PadawanAdvancedLifeCycle.logger
        .debug("# Interactions " + interactionsList.size());

    interactionsForState.put(currentState, interactionsList);
    // Interaction previousInteraction = null;

    this.unsuspendLifeCycle();

    while (true) {

      if (this.suspended) {
        return out;
      }

      // test if no more actions to try
      if (interactionsList.size() == 0) {

        // test if there is a previous state
        // if (currentState.getPreviousState() != null) {
        //
        // // there is a previous state: go back to this state, and try to
        // // compute new actions
        // PadawanAdvancedLifeCycle.logger
        // .debug(" No more action to try in current state: back to the previous
        // state");
        // currentState = currentState.getPreviousState();
        // agent.goBackToState(currentState);
        // interactionsList = interactionsForState.get(currentState);
        // continue;
        // }

        // there is no previous state: the current state is the initial one. The
        // whole tree has been explored.
        // all possible interactions have been tried. The best possible state
        // has
        // been reached.

        // back to the best encountered state
        // agent.goBackToState(bestEncounteredState);
        PadawanAdvancedLifeCycle.logger
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

      // choose one of the interaction
      List<RealizableConstrainedInteraction> otherInteractionsList = new ArrayList<>();
      for (Environment env : envs) {
        otherInteractionsList.addAll(env.getRealizableInteractions(
            (IDiogenAgent) agent, interactionsToExclude, true));
      }
      otherInteractionsList.removeAll(interactionsList);

      // System.out.println("interactionList " + interactionsList);
      RealizableConstrainedInteraction performedInteraction = this
          .chooseInteraction(interactionsList, otherInteractionsList);

      PadawanAdvancedLifeCycle.logger.debug(
          "   Chosen interaction " + performedInteraction.getInteraction()
              + " on " + performedInteraction.getTargets());
      interactionsForState.get(currentState).remove(performedInteraction);

      IGeometry previousGeom = (IGeometry) cAgent.getFeature().getGeom()
          .clone();
      // System.out.println("Geom before: " + cAgent.getFeature().getGeom());
      try {
        performedInteraction.perform();
        // System.out.println("Geom after: " + cAgent.getFeature().getGeom());
        TryInteractionTask task = interactionsFromConversation
            .get(performedInteraction);
        if (task != null) {
          task.execute();
          interactionsFromConversation.remove(performedInteraction);
        }
        interactionsToExclude.add(performedInteraction);
        /*
         * if (AgentStepVisualisationPlugin.isActivated()) {
         * JAgentStepSlider.getInstance().addAgentStep(performedInteraction,
         * previousGeom); }
         */
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
          - PadawanAdvancedLifeCycle.VALIDITY_SATISFACTION_THRESHOLD) {
        PadawanAdvancedLifeCycle.logger
            .debug("   SUCCESS -> perfect state reached (S="
                + agent.getSatisfaction() + ")");

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
      }
      // valid state
      if (currentState
          .isValid(PadawanAdvancedLifeCycle.VALIDITY_SATISFACTION_THRESHOLD)) {

        // previousInteraction = performedInteraction.getInteraction();

        interactionsList = new ArrayList<>();
        for (Environment env : envs) {
          interactionsList.addAll(this.getAllRealizableInteraction(env,
              (IDiogenAgent) agent, interactionsToExclude));
        }
        interactionsList.addAll(interactionsFromConversation.keySet());

        PadawanAdvancedLifeCycle.logger.debug(
            "   SUCCESS -> valid state (S=" + agent.getSatisfaction() + ")");
        PadawanAdvancedLifeCycle.logger.debug(
            "   " + interactionsList.size() + " new interaction(s) to try");
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;

        interactionsForState.put(currentState, interactionsList);

        // if the current state satisfaction is better than the best one, mark
        // the current state as the best encountered one
        if (currentState.getSatisfaction() > bestEncounteredState
            .getSatisfaction()) {
          PadawanAdvancedLifeCycle.logger.debug("   best encountered state !");
          bestEncounteredState = currentState;
        }
      } else if (currentState.isValid(0)) {
        PadawanAdvancedLifeCycle.logger
            .debug("   UNKNOWN -> state unchanged (satisfaction unchanged, S="
                + agent.getSatisfaction() + ")");
        // previousInteraction = performedInteraction.getInteraction();
        // interactionsList = this.getAllRealizableInteraction(env, agent,
        // alreadyDoneInteractionsList);
        interactionsList.remove(performedInteraction);
        interactionsForState.put(currentState, interactionsList);
      } else {
        PadawanAdvancedLifeCycle.logger.debug(
            "   FAILURE -> non valid state (satisfaction worst or unchanged, S="
                + agent.getSatisfaction() + ")");
        // go back to the previous state
        interactionsList.remove(performedInteraction);
        currentState = currentState.getPreviousState();
        agent.goBackToState(currentState);
      }

      // TODO do a better postprocess
      if (agent instanceof GeographicPointAgent) {
        ArrayList<IPointAgent> list = ((GeographicPointAgent) agent)
            .getAgentPointAccointants();
        for (IPointAgent ag : list) {
          logger.debug("Add agent to sceduler: " + ag);
          if (ag instanceof GeographicPointAgent) {
            Integer nbEx2 = numberOfExecutions.get(ag);
            if (nbEx2 == null) {
              nbEx2 = 0;
            }
            if (nbEx2 < 20) {
              AgentGeneralisationScheduler.getInstance().add(ag);
            }
          }
        }
      }
    }
  }

  private Map<IAgent, Integer> numberOfExecutions = new HashMap<>();

}
