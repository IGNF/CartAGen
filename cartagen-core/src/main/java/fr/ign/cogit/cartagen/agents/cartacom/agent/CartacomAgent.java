/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgumentBasedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationTransition;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.FSMBasedOnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Message;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.OnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.RemovedFailureInformAdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.TransitionExecutionException;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentState;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentStateImpl;
import fr.ign.cogit.cartagen.agents.core.task.InternalGeneralisationTask;
import fr.ign.cogit.cartagen.agents.core.task.LetToDoActionTask;
import fr.ign.cogit.cartagen.agents.core.task.Task;
import fr.ign.cogit.cartagen.agents.core.task.TaskStatus;
import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionFailure;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * Agent that shares relational constraints with other agents and solves them by
 * communicating with them
 * 
 * @author CDuchene
 * 
 */
public abstract class CartacomAgent extends GeographicAgent
    implements ICartacomAgent, ConversationManageable {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(TryActionTask.class.getName());

  /**
   * Set holding all the CartACom agents instanciated in the system
   */
  private static Set<ICartacomAgent> ALL_CARTACOM_AGENTS = new HashSet<ICartacomAgent>();

  /**
   * The last allocated agent ID.
   */
  private static int lastAllocatedCartacomId = 0;

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * List of action failures encountered by this agent
   */
  private List<ActionFailure> failures = new ArrayList<ActionFailure>();

  /**
   * The geographic object (feature) this cartacom agent is handling.
   */
  private IFeature feature = null;

  /**
   * The initial geometry
   */
  private IGeometry initialGeom;

  /**
   * The conversation manager in charge of managing the conversations of
   * {@code this}.
   */
  private ConversationManager conversationManager = null;

  /**
   * The current state of this agent
   */
  private CartacomAgentState currentState;

  /**
   * {@code true} as soon as this agent has modified itself during the current
   * activation, {@code false} before. (Reset to {@code false} at each beginning
   * of activation)
   */
  private boolean modifiedItselfDuringThisActivation = false;

  /**
   * True when the agent is waiting another agent to act.
   */
  private boolean isWaiting = false;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a Cartacom agent to handle a feature (geographic object)
   * @param feature the feature to handle
   */
  public CartacomAgent(IFeature feature) {
    this.setFeature(feature);
    this.setInitialGeom((IGeometry) feature.getGeom().clone());
    this.setId(CartacomAgent.getNewCartacomId());
    this.setConversationManager(new FSMBasedConversationManager(this));
    CartacomAgent.getAll().add(this);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_CARTACOM_AGENTS
   * @return the set of all CartACom agents instancited in the system
   */
  public static Set<ICartacomAgent> getAll() {
    return CartacomAgent.ALL_CARTACOM_AGENTS;
  }

  /**
   * Created a new agent ID by incrementing lastAllocatedAgentId and returns it.
   * 
   * @return the new created agent ID
   */
  protected static int getNewCartacomId() {
    CartacomAgent.lastAllocatedCartacomId = CartacomAgent.lastAllocatedCartacomId
        + 1;
    return CartacomAgent.lastAllocatedCartacomId - 1;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public IFeature getFeature() {
    return this.feature;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFeature(IFeature feature) {
    this.feature = feature;
    if (feature instanceof IGeneObj) {
      ((IGeneObj) feature).addToGeneArtifacts(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGeometry getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  // TODO A corriger! Ca devrait dépendre de la symbolisation... et renvoyer un
  // IPolygon
  public IGeometry getSymbolGeom() {
    return this.getGeom();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setGeom(IGeometry g) {
    this.getFeature().setGeom(g);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGeometry getInitialGeom() {
    return this.initialGeom;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInitialGeom(IGeometry initialGeom) {
    this.initialGeom = initialGeom;
  }

  /**
   * Getter of the property {@code conversationManager}
   * @return the conversation managerin charge of the conversations (and tasks)
   *         of this agent
   */
  public ConversationManager getConversationManager() {
    return this.conversationManager;
  }

  /**
   * Setter of the property {@code conversationManager}
   * @param conversationManager The conversationManager to set.
   */
  public void setConversationManager(ConversationManager conversationManager) {
    this.conversationManager = conversationManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ActionFailure> getFailures() {
    return this.failures;
  }

  /**
   * Getter for rootState.
   * @return the rootState
   */
  @Override
  public CartacomAgentState getRootState() {
    return (CartacomAgentState) super.getRootState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Task> getTasks() {
    return this.getConversationManager().getTasks();
  }

  /**
   * Adds a task
   */
  @Override
  public void addTask(Task task) {
    this.getConversationManager().addTask(task);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTasks(Set<Task> tasks) {
    this.getConversationManager().setTasks(tasks);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<OnGoingConversation> getOnGoingConversations() {
    return this.getConversationManager().getOnGoingConversations();
  }

  /**
   * Getter for currentState.
   * @return the currentState
   */
  @Override
  public CartacomAgentState getCurrentState() {
    return this.currentState;
  }

  /**
   * Setter for currentState.
   * @param currentState the currentState to set
   */
  public void setCurrentState(CartacomAgentState currentState) {
    this.currentState = currentState;
  }

  /**
   * Getter for modifiedItselfDuringThisActivation.
   * @return the modifiedItselfDuringThisActivation
   */
  @Override
  public boolean hasModifiedItselfDuringThisActivation() {
    return this.modifiedItselfDuringThisActivation;
  }

  /**
   * Setter for modifiedItselfDuringThisActivation.
   * @param modifiedItselfDuringThisActivation the
   *          modifiedItselfDuringThisActivation to set
   */
  public void setModifiedItselfDuringThisActivation(
      boolean modifiedItselfDuringThisActivation) {
    this.modifiedItselfDuringThisActivation = modifiedItselfDuringThisActivation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<RelationalConstraint> getRelationalConstraints() {
    // Initialise returned set
    Set<RelationalConstraint> relConstraints = new HashSet<RelationalConstraint>();
    // Retrieve all constraints of the agent
    Set<Constraint> constraintsSet = this.getConstraints();
    // Go through the returned set and copy the ones that are relational to the
    // returned set
    for (Constraint constraint : constraintsSet) {
      if (constraint instanceof RelationalConstraint) {
        relConstraints.add((RelationalConstraint) constraint);
      }
    }
    return relConstraints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<IAgent> getAgentsSharingRelation() {
    Set<IAgent> agents = new HashSet<IAgent>();
    for (RelationalConstraint c : this.getRelationalConstraints()) {
      agents.add(c.getAgentSharingConstraint());
    }
    return agents;
  }

  @Override
  public boolean isWaiting() {
    return this.isWaiting;
  }

  @Override
  public void setWaiting(boolean isWaiting) {
    this.isWaiting = isWaiting;
  }

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  // Methods of this class

  /**
   * Removes from the failures of this agnet, the ones having the specified
   * validity. If the other agent is to be informed, informs it.
   * @param validity the validity of the failures to remove
   */
  public void removeFailuresOfValidity(FailureValidity validity) {
    Iterator<ActionFailure> iterator = this.getFailures().iterator();
    while (iterator.hasNext()) {
      ActionFailure failure = iterator.next();
      if (failure.getValidity() == validity) {
        // Removes the failure
        iterator.remove();
        // If the failure removal should be notified to the other agent, does it
        if (failure.getInformOtherAgent()) {
          ICartacomAgent otherAgent = (ICartacomAgent) ((RelationalConstraint) failure
              .getAction().getConstraint()).getAgentSharingConstraint();
          this.initiateConversation(otherAgent, Performative.INFORM,
              new RemovedFailureInformAdHocArgument(failure));
        }
      } // if (failure.getValidity() == validity)
    } // while (iterator.hasNext())

  }

  // Inherited from ConversationManageable //

  // Inherited from ConversationalObject //

  /**
   * {@inheritDoc}
   */
  @Override
  public Message composeMessage(long ConversationId, Performative performative,
      Object argument) {
    return new AdHocArgumentBasedMessage(ConversationId, performative,
        (AdHocArgument) argument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SignedMessage> orderReceivedMessagesByEmergency(
      Collection<SignedMessage> receivedMessages) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method is delegated to the conversation manager.
   */
  @Override
  public void handleReceivedMessages() {
    this.getConversationManager().handleReceivedMessages();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method is delegated to the conversation manager.
   */
  @Override
  public void initiateConversation(ConversationalObject partner,
      Performative performative, Object argument, Task task) {
    if (partner instanceof CartacomAgent) {
      this.getConversationManager().initiateConversation(
          ((CartacomAgent) partner).getConversationManager(), performative,
          argument, task);
    } else {
      this.getConversationManager().initiateConversation(partner, performative,
          argument, task);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method is delegated to the conversation manager.
   */
  @Override
  public void initiateConversation(ConversationalObject partner,
      Performative performative, Object argument) {
    if (partner instanceof CartacomAgent) {
      this.getConversationManager().initiateConversation(
          ((CartacomAgent) partner).getConversationManager(), performative,
          argument);
    } else {
      this.getConversationManager().initiateConversation(partner, performative,
          argument);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method is delegated to the conversation manager.
   */
  @Override
  public void receiveMessage(ConversationalObject partner, Message message) {
    if (partner instanceof CartacomAgent) {
      this.getConversationManager().receiveMessage(
          ((CartacomAgent) partner).getConversationManager(), message);
    } else {
      this.getConversationManager().receiveMessage(partner, message);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method is delegated to the conversation manager.
   */
  @Override
  public void sendMessage(ConversationalObject partner, Message message) {
    if (partner instanceof CartacomAgent) {
      this.getConversationManager().sendMessage(
          ((CartacomAgent) partner).getConversationManager(), message);
    } else {
      this.getConversationManager().sendMessage(partner, message);
    }
  }

  /**
   * {@inheritDoc} At this level, returns an empty set of tasks
   */
  @Override
  public Set<TryActionTask> getAdditionalTasksToAggregate(
      TryActionTask taskInitiatingAggregation,
      Set<TryActionTask> tasksIdentifiedForAggregation) {
    return new HashSet<TryActionTask>();
  }

  // Inherited from GeographicObjectAgent //

  /**
   * {@inheritDoc}
   */
  @Override
  public CartacomAgentState buildCurrentState(AgentState previousState,
      Action action) {
    CartacomAgentState state = new CartacomAgentStateImpl(this,
        (CartacomAgentState) previousState, action);
    this.setCurrentState(state);
    return state;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MesoAgent<? extends GeographicObjectAgent> getMesoAgent() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<InternStructureAgent> getStructureAgents() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPopulation<IFeature> getPopulation() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void goBackToInitialState() {
    this.goBackToState(this.getRootState());
  }

  // Inherited from AgentImpl

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CartACom" + this.getClass().getSimpleName() + " " + this.getId()
        + "(" + this.getFeature().toString() + ")";
  }

  // Inherited from CartacomAgent

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isActionInFailuresList(Action action) {
    for (ActionFailure failure : this.getFailures()) {
      if (action.equals(failure.getAction())) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Set the boolean {@code #modifiedItselfDuringThisActivation} to {@code true}
   * and removes from the failures list all failures having
   * {@code FailureValidity#AGENT_MODIFIED} or
   * {@code FailureValidity#AGENT_OR_ENVIRONMENT_MODIFIED} as validity.
   */
  @Override
  public void manageHavingJustBeenModifiedByATask(
      boolean changeCentroidRelatedConstrainedZone) {
    // Removes the failures having the validity
    // AGENT_MODIFIED or AGENT_OR_ENVIRONMENT_MODIFIED
    this.removeFailuresOfValidity(FailureValidity.AGENT_MODIFIED);
    this.removeFailuresOfValidity(
        FailureValidity.AGENT_OR_ENVIRONMENT_MODIFIED);
    // Acknowledges that this agent has been modified during the current
    // activation
    this.setModifiedItselfDuringThisActivation(true);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clean() {
    super.clean();
    this.setTasks(new HashSet<Task>());
    if (this.getFailures() != null) {
      this.getFailures().clear();
    }
  }

  // Inherited from Object

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    if (this.getId() != ((CartacomAgent) obj).getId()) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.getId();
  }

  @Override
  public void executeCascadingTasks() {

    // Suppress ended task (method suppressEndedTasks)
    // Suppress ended conversation (method suppressEndedConversations)
    // updateSatusTask

    // Loop on the tasks
    if (CartacomAgent.logger.isDebugEnabled()) {
      CartacomAgent.logger.debug("Available tasks : " + this.getTasks());
    }

    while (true) {
      Task task = this.chooseNextTask();
      if (task == null) {
        return;
      }
      // this.getTasks().remove(task);

      if (CartacomAgent.logger.isDebugEnabled()) {
        CartacomAgent.logger.debug("Execute task " + task);
        CartacomAgent.logger.debug("Stage " + task.getStage());
        if (task instanceof TryActionTask) {
          CartacomAgent.logger
              .debug("Action " + ((TryActionTask) task).getActionToTry());
        }
      }

      task.execute();

      if (CartacomAgent.logger.isDebugEnabled()) {
        CartacomAgent.logger.debug("Execute task " + task + " : done.");
        CartacomAgent.logger.debug("Stage " + task.getStage());
      }
      // Suppress ended task (method suppressEndedTasks)
      // Suppress ended conversation (method suppressEndedConversations)
      // updateSatusTask
    }
  }

  private Task chooseNextTask() {
    Task chosenTask = null;
    boolean noInternal = true;
    boolean noResumable = true;

    for (Task task : this.getTasks()) {
      TaskStatus status = task.getStatus();
      CartacomAgent.logger.debug("Task " + task + " Status " + status);
      if (status == TaskStatus.PROCESSING) {
        return null;
      } else if (status == TaskStatus.RESUMABLE) {
        noResumable = false;
        chosenTask = task;
      } else if (status == TaskStatus.NOT_STARTED
          && task instanceof InternalGeneralisationTask && noResumable) {
        noInternal = false;
        chosenTask = task;
      } else if (status == TaskStatus.NOT_STARTED && noInternal
          && noResumable) {
        chosenTask = task;
      }
    }
    return chosenTask;
  }

  @Override
  public void updateTasksStatus() {
    Set<Task> tasksToRemove = new HashSet<Task>();
    for (Task task : this.getTasks()) {
      if (task.getStatus() == TaskStatus.WAITING) {
        // Loop on the depending task
        if (task.getDependentTasks().isEmpty()
            && task.getGeneratedConversations().isEmpty()) {
          task.setStatus(TaskStatus.RESUMABLE);
        }
      } else if (task.getStatus() == TaskStatus.FINISHED) {
        tasksToRemove.add(task);
      }
    }
    this.getTasks().removeAll(tasksToRemove);
  }

  @Override
  public Set<ActionProposal> synthesizePossibleActions() {
    Map<Constraint, ActionProposal> table = new Hashtable<Constraint, ActionProposal>();
    this.updateActionProposals();
    for (ActionProposal actionProposal : this.getActionProposals()) {
      boolean failedAction = false;
      for (ActionFailure failure : this.getFailures()) {
        if (failure.getAction().equals(actionProposal.getAction())) {
          failedAction = true;
          break;
        }
      }
      if (!failedAction) {
        if ((table.get(actionProposal.getAction().getConstraint()) != null)) {
          if (actionProposal.getWeight() <= table
              .get(actionProposal.getAction().getConstraint()).getWeight()) {
            continue;
          }
        }
        table.put(actionProposal.getAction().getConstraint(), actionProposal);
      }
    }
    return new HashSet<ActionProposal>(table.values());
  }

  /**
   * Get conversations with status FINISHED and remove them.
   */
  @Override
  public void eraseEndedConversations() {
    for (OnGoingConversation conversation : this.getConversationManager()
        .getOnGoingConversations()) {
      if (conversation.isFinished()) {
        this.getOnGoingConversations().remove(conversation);
      }
    }
  }

  @Override
  public void createTaskFromActionProposal(CartacomAction action) {
    if (action.isActItselfAction()) {
      // Action to be execute by current agent
      new TryActionTask(this, action);
      // if (task.tryToAggregate()) {
      // logger.debug("Try to aggregate ok.");
      // task = (TryActionTask) task.getAggregatedTask();
      // }

      // ((CartacomAgent) this).addTask(task);
    } else {
      // Action to be executed by the other agent
      // CartacomAgent partner = (CartacomAgent) ((RelationnalConstraint) action
      // .getConstraint()).getAgentSharingConstraint();
      // action.setAgent(partner);
      new LetToDoActionTask(this, action);
      // this.addTask(task);
    }
  }

  @Override
  public void executeTransitionInConversation(
      FSMBasedOnGoingConversation onGoingConv,
      ConversationTransition transition, Object argument)
      throws TransitionExecutionException {
    this.getConversationManager().executeTransitionInConversation(onGoingConv,
        transition, argument);
  }

  public Set<Task> getGeneratedTasks() {
    return conversationManager.getGeneratedTasks();
  }

}
