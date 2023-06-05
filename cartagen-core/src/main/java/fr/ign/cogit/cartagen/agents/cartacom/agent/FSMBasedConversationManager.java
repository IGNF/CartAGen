/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationStateType;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationSummary;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationTransition;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationsManager;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.FSMBasedOnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.HalfConversationScenario;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Message;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.RoleInConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ScenarioNotFoundException;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.TransitionExecutionException;
import fr.ign.cogit.cartagen.agents.core.task.EndOfConvTask;
import fr.ign.cogit.cartagen.agents.core.task.ProcessingTaskWithinConv;
import fr.ign.cogit.cartagen.agents.core.task.Task;
import fr.ign.cogit.cartagen.agents.core.task.TaskWithinConversation;
import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;

/**
 * An FSM based conversational object that acts as a conversation manager i.e.
 * that handles the conversations of another object (its <code>managedObject
 * </code>).
 * @author CDuchene
 */
public class FSMBasedConversationManager extends FSMBasedConversationalObject
    implements ConversationManager {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(FSMBasedConversationManager.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The object of which this FSMBasedConversationManager manages the
   * conversations
   */
  ConversationManageable managedObject;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a conversation manager for a managed object.
   * <p>
   * Construit un conversation manager chargé de générer les conversations du
   * ConversationManageable en paramètre.
   * @param managedObject the ConversationManageable this conversation manager
   *          will be managing the conversations
   */
  public FSMBasedConversationManager(ConversationManageable managedObject) {
    this.managedObject = managedObject;
  }

  /**
   * The default constructor is made private to force the use of the constructor
   * parametrised by the managed object.
   * <p>
   * Le constructeur par défault est déclaré privé pour forcer l'utilisation du
   * constructeur paramétré par le ConversationManageable géré.
   */
  @SuppressWarnings("unused")
  private FSMBasedConversationManager() {
    super();
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public ConversationManageable getManagedObject() {
    return this.managedObject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setManagedObject(ConversationManageable managedObject) {
    this.managedObject = managedObject;
  }

  /**
   * Setter for tasks. Also updates the reverse reference from each element of
   * tasks to {@code this}. To break the reference use {@code this.setTasks(new
   * HashSet<Task>())}
   * @param tasks the set of tasks to set
   */
  @Override
  public void setTasks(Set<Task> tasks) {
    Set<Task> oldTasks = new HashSet<Task>(this.getTasks());
    for (Task task : oldTasks) {
      task.setTaskOwner(null);
    }
    for (Task task : tasks) {
      task.setTaskOwner(this.getManagedObject());
    }
  }

  /**
   * Adds a Task to tasks, and updates the reverse reference from the added Task
   * to {@code this}.
   * @param task the task to add
   */
  @Override
  public void addTask(Task task) {
    if (task == null) {
      return;
    }
    this.getTasks().add(task);
    task.setTaskOwner(this.getManagedObject());
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  /**
   * Factory method to compose a message from a conversation Id, a performative
   * and an argument, inherited from <code>FSMBasedConversationalObject</code>.
   * Here it delegates the composition of the message to its managed object. The
   * concrete class of the returned message, as well as the actual class of the
   * argument depend on the language spoken by the managed object.
   * @param convId the Id of the conversation the message is part of
   * @param performative the performative of the message
   * @param argument the argument of the message
   * @return the composed message
   */
  @Override
  public Message composeMessage(long convId, Performative performative,
      Object argument) {
    return this.getManagedObject().composeMessage(convId, performative,
        argument);
  }

  /**
   * Sends a message to a partner on behalf of the managed object (<code>
   * managedObject</code> , by triggering the
   * <code>receiveMessage()<code> method
   * of the partner with the managed object as sender. <p>
   * Envoie un message à un partenaire au nom de l'objet géré (<code>
   * managedObject</code>, en déclenchant la méthode <code>receiveMessage()
   * <code> du partenaire avec l'objet géré comme expediteur.
   * @param partner the ConversationalObject to which the message should be sent
   * @param message the message to send
   */
  @Override
  public void sendMessage(ConversationalObject partner, Message message) {
    // Triggers the receiveMessage() method of the partner
    partner.receiveMessage(this.getManagedObject(), message);
  }

  /**
   * Begins a conversation on behalf of the managed object with another
   * conversational object in the context of a task (i.e. the conversation is
   * generated by the task), with a first message composed of a given
   * performative and argument. For this, generates a new OnGoingConversation
   * based on a scenario corresponding to the first performative to send, and
   * executes its first transition.
   * @param partner The conversational object with which the conversation takes
   *          place
   * @param performative The performative of the first message
   * @param argument The argument of the first message. At this level it is
   *          defined as an <code>Object</code>, but its actual type depends on
   *          the implementation of the interface {@link Message} actually
   *          returned by the method composeMessage.
   * @param task the task in the context of which the conversation is initiated.
   *          This task will be registered as dependent on the created
   *          conversation.
   */
  @Override
  public void initiateConversation(ConversationalObject partner,
      Performative performative, Object argument, Task task) {

    String taskString = "";
    if (task != null) {
      taskString = " and task " + task.toString();
    }
    try {
      // From the performative retrieves the reference half conversation
      // scenario
      HalfConversationScenario scenario = HalfConversationScenario
          .retrieveHalfConversationScenario(RoleInConversation.INITIATOR,
              performative);
      // Constructs a new OnGoingConversation based on the first state of this
      // scenario
      FSMBasedOnGoingConversation onGoingConv = new FSMBasedOnGoingConversation(
          partner, task, scenario.getInitialState());
      // Adds the new conversation to the on-going conversations of -this-
      this.getOnGoingConversations().add(onGoingConv);
      // Adds an entry for this conversation in the ConversationsSummary Map
      // (stores the managed object, not this conversation manager)
      ConversationsManager.getInstance().getConversationsSummary().put(
          new Long(onGoingConv.getConversationId()), new ConversationSummary(
              performative, this.getManagedObject(), partner));
      // Executes the first transition of the new conversation
      this.executeTransitionInConversation(onGoingConv, scenario
          .getInitialState().getFollowingTransitionsSet().iterator().next(),
          argument);
      // Print info
      FSMBasedConversationManager.logger
          .info("Conversation with first message '" + performative + "("
              + argument.toString() + ")' " + taskString
              + " initiated with partner " + partner.toString());
    } catch (ScenarioNotFoundException e) {
      FSMBasedConversationManager.logger
          .error("Impossible to initiate conversation with partner "
              + partner.toString() + " with a first message '" + performative
              + "(" + argument.toString() + ")' " + taskString);
      e.printStackTrace();
    } catch (TransitionExecutionException e) {
      FSMBasedConversationManager.logger
          .error("Impossible to initiate conversation with partner "
              + partner.toString() + " with a first message '" + performative
              + "(" + argument.toString() + ")' " + taskString);
      e.printStackTrace();
    }
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * Here, delegates the ordering of the messages to its managed object (that
   * knows how to interpret the messages, and therefore how urgent they are).
   */
  @Override
  protected List<SignedMessage> orderReceivedMessagesByEmergency() {
    return this.getManagedObject()
        .orderReceivedMessagesByEmergency(this.getReceivedMessages());
  }

  /**
   * Executes a given transition within an on-going conversation of this
   * conversation manager. The reference state of the on-going conversation is
   * updated depending on the executed transition. If the transition requires a
   * message to be sent, the message is sent with the argument passed as
   * parameter. If the new reference state is of type <code>
   * ConversationStateType.PROCESSING</code> , the associated task is created on
   * the with the status <code>TaskStatus.NOT_STARTED</code> and the argument
   * passed as parameter. If the new reference state of is of type <code>
   * ConversationStateType.FINAL</code> , the associated task is created and
   * directly executed. In any case the owner of the created task is set to the
   * managed object, not to this conversation manager.
   * <p>
   * Execute une transition donnée dans une conversation en cours de l'objet
   * conversationnel. Met a jour l'etat de reference de la conversation en
   * fonction de la transition exécutée. Si la transition appelle un envoi de
   * message, envoie le message correspondant avec comme argument l'argument
   * passé en paramètre. Si le nouvel etat est de type <code>
   * ConversationStateType.PROCESSING</code> , crée la tâche associée avec le
   * statut <code>TaskStatus.NOT_STARTED</code> et l'argument passé en
   * parametre. Si le type du nouvel etat est "fin", crée la tâche associée et
   * l'exécute directement.
   * @param onGoingConv the on-going conversation concerned in which the
   *          transition is to be executed
   * @param transition the transition of the reference scenario to execute
   * @param argument the argument of the received message that provokes the
   *          transition, or the argument to send (either computed by the task
   *          executed at the previous state of the conversation, or generated
   *          within the task dependent of the conversation if the conversation
   *          has just been initiated).
   */
  @SuppressWarnings("unchecked")
  @Override
  public void executeTransitionInConversation(
      FSMBasedOnGoingConversation onGoingConv,
      ConversationTransition transition, Object argument)
      throws TransitionExecutionException {
    // Check that the previous state of the transition to execute is the current
    // state of the conversation
    if (transition.getPreviousState() != onGoingConv
        .getReferenceConversationState()) {
      FSMBasedConversationManager.logger
          .error("Failed to execute a transition in conversation Id "
              + onGoingConv.getConversationId() + ". Inconsistency between "
              + "current conversation state ("
              + onGoingConv.getReferenceConversationState().toString()
              + ") and transition to execute.");
      throw new TransitionExecutionException(this, onGoingConv, transition);
    }
    // Updates the reference state of the on-going conversation
    ConversationState newState = transition.getFollowingState();
    onGoingConv.setReferenceConversationState(newState);
    FSMBasedConversationManager.logger.info(
        "Transition in conversation: the state of the conversation changed from "
            + transition.getPreviousState().getType() + " to "
            + newState.getType() + ".");
    // Case where a message has to be sent: sends it to the partner
    Performative perf = transition.getPerformativeToSend();
    if (perf != null) {
      this.sendMessage(onGoingConv.getPartner(),
          this.composeMessage(onGoingConv.getConversationId(), perf, argument));
    }
    // Case where the new reference state is of type PROCESSING
    if (newState.getType() == ConversationStateType.PROCESSING) {
      // Gets the class of the task to create
      Class<? extends ProcessingTaskWithinConv> taskClass = (Class<? extends ProcessingTaskWithinConv>) newState
          .getClassOfTaskToExecute();
      FSMBasedConversationManager.logger
          .info("Class of the task to execute: " + taskClass + ".");
      // Creates an instance of this task class and initialises it depending
      // on the characteristics of the new conversation state reached.
      // Then registers it as the task generated by the conversation
      try {
        ProcessingTaskWithinConv newTask = taskClass.newInstance();
        // If a message has been received during the transition: initialises
        // the task depending on the new state and the received argument
        if (transition.getReceivedPerformative() != null) {
          newTask.initialiseBasedOnReceivedMessage(this.getManagedObject(),
              newState, argument);
          onGoingConv.setGeneratedTask(newTask);
          onGoingConv.setDependentTask(newTask);
        }
        // If the previous state of the conversation was already a processing
        // state:
        // initialises the task depending on the new state and the task executed
        // at previous state (which at this stage is still the task generated
        // by the conversation)
        else if (transition.getPreviousState()
            .getType() == ConversationStateType.PROCESSING) {
          newTask.initialiseBasedOnPreviousTask(this.getManagedObject(),
              newState,
              (ProcessingTaskWithinConv) onGoingConv.getGeneratedTask());
          onGoingConv.setGeneratedTask(newTask);
          onGoingConv.setDependentTask(newTask);
        }
        // If the previous state of the conversation is the initial state:
        // initialises the task depending on the task that generated the
        // conversation,
        // if it exists (otherwise: error)
        else if (transition.getPreviousState()
            .getType() == ConversationStateType.INITIAL) {
          if (onGoingConv.getDependentTask() != null) {
            newTask.initialiseBasedOnDependentTask(this.getManagedObject(),
                newState, onGoingConv.getDependentTask());
            onGoingConv.setGeneratedTask(newTask);
            onGoingConv.setDependentTask(newTask);
          } // if (onGoingConv.getDependentTask() != null)
          // Previous state = initial state and no dependent task: not managed
          // case.
          // I.e. a conversational object should always initiate a conversation
          // containing processing states within a task.
          else {
            FSMBasedConversationManager.logger
                .error("Failed to execute a transition in conversation Id "
                    + onGoingConv.getConversationId()
                    + ". The conversation contains "
                    + "a processing state and has not been generated by a task.");
            throw new TransitionExecutionException(this, onGoingConv,
                transition);
          } // else
        } // else if (transition.getPreviousState().getType() ==
          // ConversationStateType.INITIAL)
        // Other configurations: not managed (inconsistency in the conversation
        // scenario)
        else {
          FSMBasedConversationManager.logger
              .error("Failed to execute a transition in conversation Id "
                  + onGoingConv.getConversationId() + ". The reference scenario"
                  + "is probably inconsistent.");
          throw new TransitionExecutionException(this, onGoingConv, transition);
        }
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    } // (newState.getType() == ConversationStateType.PROCESSING)
    // Case where the new reference state is of type FINAL: if it has an
    // associated
    // task, this task is created and immediately executed, as it is supposed
    // to only acknowledge some facts.
    else if (newState.getType() == ConversationStateType.FINAL) {
      // Gets the class of the task to create
      Class<? extends TaskWithinConversation> taskClass = newState
          .getClassOfTaskToExecute();
      if (taskClass != null) {
        try {
          // Create the task
          TaskWithinConversation newTask = taskClass.newInstance();
          newTask.setTaskOwner(this.getManagedObject());
          // Associate it to the conversation
          onGoingConv.setGeneratedTask(newTask);
          // Execute it
          ((EndOfConvTask) newTask).execute(onGoingConv.getPartner(), newState,
              argument);
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      } // if (taskClass != null)
    } // else if (newState.getType() == ConversationStateType.FINAL)
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<TryActionTask> getAdditionalTasksToAggregate(
      TryActionTask taskInitiatingAggregation,
      Set<TryActionTask> tasksIdentifiedForAggregation) {
    // TODO Auto-generated method stub
    return null;
  }

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
