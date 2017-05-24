/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationRetrievalException;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationStateType;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationSummary;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationTransition;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationsManager;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.FSMBasedOnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.HalfConversationScenario;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Message;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.OnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.RoleInConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ScenarioNotFoundException;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.TransitionExecutionException;
import fr.ign.cogit.cartagen.agents.core.task.EndOfConvTask;
import fr.ign.cogit.cartagen.agents.core.task.ProcessingTaskWithinConv;
import fr.ign.cogit.cartagen.agents.core.task.Task;
import fr.ign.cogit.cartagen.agents.core.task.TaskWithinConversation;

/**
 * An object capable of having conversations (dialogs) following pre-established
 * dialog scenarios stored as transition graphs by means of
 * {@link HalfConversationScenario}). An {@code FSMBasedConversationalObject}
 * bases on these {@code HalfConversationScenario} to fulfil the contract of the
 * interface {@link ConversationalObject}, i.e. to be able to initiate a
 * conversation, analyse a received message, act according to it and answer to
 * it. The actual conversations it takes part to are represented by
 * {@link OnGoingConversation} objects.
 * 
 * This class is abstract in order to work with any implementation of the
 * interface {@link Message}. Concrete subclasses should implement the factory
 * method {@link #composeMessage} with the right type of returned Message.
 * 
 * @author CDuchene
 * 
 */
public abstract class FSMBasedConversationalObject
    implements ConversationalObject {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(FSMBasedConversationalObject.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * The tasks set (bidirectional reference, automatically managed).
   */
  private Set<Task> tasks = new HashSet<Task>();

  /**
   * List of messages received by {@code this}
   */
  private List<SignedMessage> receivedMessages = new ArrayList<SignedMessage>();

  /**
   * Set of on going conversations in which {@code this} is involved
   */
  private Set<OnGoingConversation> onGoingConversations = new HashSet<OnGoingConversation>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for tasks.
   * @return the tasks
   */
  @Override
  public Set<Task> getTasks() {
    return this.tasks;
  }

  /**
   * Setter for tasks. Also updates the reverse reference from each element of
   * tasks to {@code this}. To break the reference use {@code this.setTasks(new
   * HashSet<Task>())}
   * @param tasks the set of tasks to set
   */
  @Override
  public void setTasks(Set<Task> tasks) {
    Set<Task> oldTasks = new HashSet<Task>(this.tasks);
    for (Task task : oldTasks) {
      task.setTaskOwner(null);
    }
    for (Task task : tasks) {
      task.setTaskOwner(this);
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
    this.tasks.add(task);
    task.setTaskOwner(this);
  }

  /**
   * Removes a Task from tasks, and updates the reverse reference from the
   * removed Task to {@code null}.
   * @param task the task to remove
   */
  public void removeTask(Task task) {
    if (task == null) {
      return;
    }
    this.tasks.remove(task);
    task.setTaskOwner(null);
  }

  /**
   * Getter of the property <tt>messagesStack</tt>
   * @return Returns the messagesStack.
   */
  public List<SignedMessage> getReceivedMessages() {
    return this.receivedMessages;
  }

  /**
   * Setter of the property <tt>receivedMessages</tt>
   * @param receivedMessages The receivedMessages to set.
   */
  public void setReceivedMessages(List<SignedMessage> receivedMessages) {
    this.receivedMessages = receivedMessages;
  }

  /**
   * Getter for onGoingConversations.
   * @return the onGoingConversations
   */
  @Override
  public Set<OnGoingConversation> getOnGoingConversations() {
    return this.onGoingConversations;
  }

  /**
   * Setter for onGoingConversations.
   * @param onGoingConversations the onGoingConversations to set
   */
  public void setOnGoingConversations(
      Set<OnGoingConversation> onGoingConversations) {
    this.onGoingConversations = onGoingConversations;
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  /**
   * Factory method to compose a message from a conversation Id, a performative
   * and an argument. The concrete class of the message will be dependent of the
   * actual conversational object that invokes the method.
   * @param convId the Id of the conversation the message is part of
   * @param performative the performative of the message
   * @param argument the argument of the message
   * @return the composed message
   */
  public abstract Message composeMessage(long convId, Performative performative,
      Object argument);

  /**
   * Handles the received messages: handles each message in turn, after having
   * reordered them by emergency.
   */
  @Override
  public void handleReceivedMessages() {
    List<SignedMessage> orderedMessages = this
        .orderReceivedMessagesByEmergency();
    FSMBasedConversationalObject.logger
        .debug("Received messages : " + this.getReceivedMessages());
    Iterator<SignedMessage> iterator = orderedMessages.iterator();
    while (iterator.hasNext()) {
      SignedMessage signedMessage = iterator.next();
      this.handleReceivedMessage(signedMessage);
      iterator.remove();
    }
  }

  /**
   * Begins a conversation with another conversational object, with a first
   * message composed of a given performative and argument. For this, generates
   * a new OnGoingConversation based on a scenario corresponding to the first
   * performative to send, and executes its first transition. WARNING if the
   * conversation is initiated in the context of a task, use
   * {@link #initiateConversation(ConversationalObject, Performative, Object, Task)}
   * instead.
   * @param partner The conversational object with which the conversation takes
   *          place
   * @param performative The performative of the first message
   * @param argument The argument of the first message. At this level it is
   *          defined as an <code>Object</code>, but its actual type should
   *          depend on the implementation of the interface {@link Message}
   *          actually returned by the method composeMessage.
   */
  @Override
  public void initiateConversation(ConversationalObject partner,
      Performative performative, Object argument) {
    // Just initiates a conversation depending of no task
    this.initiateConversation(partner, performative, argument, null);
  }

  /**
   * Begins a conversation with another conversational object in the context of
   * a task (i.e. the conversation is generated by the task), with a first
   * message composed of a given performative and argument. For this, generates
   * a new OnGoingConversation based on a scenario corresponding to the first
   * performative to send, and executes its first transition.
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
      ConversationsManager.getInstance().getConversationsSummary().put(
          new Long(onGoingConv.getConversationId()),
          new ConversationSummary(performative, this, partner));
      // Executes the first transition of the new conversation
      this.executeTransitionInConversation(onGoingConv, scenario
          .getInitialState().getFollowingTransitionsSet().iterator().next(),
          argument);
      // Print info
      FSMBasedConversationalObject.logger
          .info("Conversation with first message '" + performative + "("
              + argument.toString() + ")' and task " + task.toString()
              + " initiated with partner " + partner.toString());
    } catch (ScenarioNotFoundException e) {
      FSMBasedConversationalObject.logger
          .error("Impossible to initiate conversation with partner "
              + partner.toString() + " with a first message '" + performative
              + "(" + argument.toString() + ")' and task " + task.toString());
      e.printStackTrace();
    } catch (TransitionExecutionException e) {
      FSMBasedConversationalObject.logger
          .error("Impossible to initiate conversation with partner "
              + partner.toString() + " with a first message '" + performative
              + "(" + argument.toString() + ")' and task " + task.toString());
      e.printStackTrace();
    }
  }

  /**
   * Receives a message sent by another conversational object, by transforming
   * it into a signed message and adding it to its list of received messages.
   * 
   */
  @Override
  public void receiveMessage(ConversationalObject partner, Message message) {
    this.getReceivedMessages().add(new SignedMessage(partner, message));
  }

  /**
   * Sends a message to a partner by letting it receive the message with
   * <code>this</code> as a sender.
   * <p>
   * Envoie un message à un partenaire, en lui faisant recevoir le message avec
   * <code>this</code> comme expediteur.
   * @param partner the ConversationalObject to which the message should be sent
   * @param message the message to send
   */
  @Override
  public void sendMessage(ConversationalObject partner, Message message) {
    // Lets the partner receive the message, with this as a sender
    partner.receiveMessage(this, message);
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  /**
   * Generates a list containing the received (signed) messages, and ordered in
   * such a way that the messages that should be handled first appear first. The
   * content of this method depends on the possible contents of the expected
   * messages and on the way they are to be handled.
   * @return the received messages, ordered by emergency in a list
   */
  protected abstract List<SignedMessage> orderReceivedMessagesByEmergency();

  /**
   * Handles the received message passed as parameter.
   * @param message the message to handle
   */
  protected void handleReceivedMessage(SignedMessage message) {
    try {
      // Info message
      FSMBasedConversationalObject.logger
          .info("Handling received message " + message.toString()
              + " on conversational object " + this.toString() + ".");
      // Variable for the next transition to execute in the conversation
      // associated to the received message.
      ConversationTransition transitionToExecute = null;
      // Checks if the message belongs to a new conversation or an existing
      // conversation, by trying to retrieve an on-going conversation of same Id
      OnGoingConversation onGoingConv = this.retrieveConversation(
          message.getMessage().getConversationId(),
          (FSMBasedConversationalObject) message.getSender());
      if (logger.isTraceEnabled()) {
        logger.trace("OnGoingConversation: " + onGoingConv);
        logger.trace(
            "ConversationId: " + message.getMessage().getConversationId());
        if (onGoingConv != null)
          logger.trace(
              "OnGoingConversation partner: " + onGoingConv.getPartner());
      }
      // If a matching conversation has been found, identifies the next
      // transition
      // to execute
      if (onGoingConv != null) {
        FSMBasedConversationalObject.logger.info(
            "This message corresponds to an already on going conversation.");
        transitionToExecute = ((FSMBasedOnGoingConversation) onGoingConv)
            .getReferenceConversationState()
            .findFollowingTransition(message.getMessage().getPerformative());
        if (logger.isTraceEnabled()) {
          logger
              .trace("Performative: " + message.getMessage().getPerformative());
          logger.trace("Current conversation state: "
              + ((FSMBasedOnGoingConversation) onGoingConv)
                  .getReferenceConversationState().getName());
        }
      }
      // Else, no matching conversation retrieved: create a new conversation
      // The transition to execute will be the first transition (the only
      // one following the initial state).
      else {
        FSMBasedConversationalObject.logger.info(
            "This message corresponds to a new conversation. I create it.");
        ConversationState firstConvState = (HalfConversationScenario
            .retrieveHalfConversationScenario(RoleInConversation.RESPONDENT,
                message.getMessage().getPerformative())).getInitialState();
        onGoingConv = new FSMBasedOnGoingConversation(
            message.getMessage().getConversationId(), message.getSender(),
            firstConvState);
        transitionToExecute = firstConvState.getFollowingTransitionsSet()
            .iterator().next();
      }

      this.getOnGoingConversations().add(onGoingConv);
      // Now a the on-going conversation matching this message exists: execute
      // the transition corresponding to the reception of this message
      FSMBasedConversationalObject.logger
          .info("Executing the next transition of the conversation...");
      this.executeTransitionInConversation(
          (FSMBasedOnGoingConversation) onGoingConv, transitionToExecute,
          message.getMessage().getArgument());
    } catch (ConversationRetrievalException e) {
      FSMBasedConversationalObject.logger
          .error("Problem while handling message " + message.toString()
              + " on conversational object " + this.toString()
              + ". The conversation"
              + " to which the message belongs is known with another partner.");
      e.printStackTrace();
    } catch (ScenarioNotFoundException e) {
      FSMBasedConversationalObject.logger
          .error("Problem while handling message " + message.toString()
              + " on conversational object " + this.toString() + ". Unable to"
              + " identify the conversation scenario matching the received message");
      e.printStackTrace();
    } catch (TransitionExecutionException e) {
      FSMBasedConversationalObject.logger
          .error("Problem while handling message " + message.toString()
              + " on conversational object " + this.toString() + ". Unable to"
              + " execute the next transition in the corresponding conversation.");
      e.printStackTrace();
    }
  }

  /**
   * Executes a given transition within an on-going conversation of this
   * conversational object. The reference state of the on-going conversation is
   * updated depending on the executed transition. If the transition requires a
   * message to be sent, the message is sent with the argument passed as
   * parameter. If the new reference state is of type <code>
   * ConversationStateType.PROCESSING</code> , the associated task is created
   * with the status <code>TaskStatus.NOT_STARTED</code> and the argument passed
   * as parameter. If the new reference state of is of type <code>
   * ConversationStateType.FINAL</code> , the associated task is created and
   * directly executed.
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
   * @throws TransitionExecutionException if the transition fails - more info is
   *           given by the logger
   */
  @Override
  @SuppressWarnings("unchecked")
  public void executeTransitionInConversation(
      FSMBasedOnGoingConversation onGoingConv,
      ConversationTransition transition, Object argument)
      throws TransitionExecutionException {
    // Check that the previous state of the transition to execute is the current
    // state of the conversation
    if (transition.getPreviousState() != onGoingConv
        .getReferenceConversationState()) {
      FSMBasedConversationalObject.logger
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
    FSMBasedConversationalObject.logger.info(
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
      // Creates an instance of this task class and initialises it depending
      // on the characteristics of the new conversation state reached.
      // Then registers it as the task generated by the conversation
      try {
        ProcessingTaskWithinConv newTask = taskClass.newInstance();
        // If a message has been received during the transition: initialises
        // the task depending on the new state and the received argument
        if (transition.getReceivedPerformative() != null) {
          newTask.initialiseBasedOnReceivedMessage(this, newState, argument);
          onGoingConv.setGeneratedTask(newTask);
        }
        // If the previous state of the conversation was already a processing
        // state:
        // initialises the task depending on the new state and the task executed
        // at previous state (which at this stage is still the task generated
        // by the conversation)
        else if (transition.getPreviousState()
            .getType() == ConversationStateType.PROCESSING) {
          newTask.initialiseBasedOnPreviousTask(this, newState,
              (ProcessingTaskWithinConv) onGoingConv.getGeneratedTask());
          onGoingConv.setGeneratedTask(newTask);
        }
        // If the previous state of the conversation is the initial state:
        // initialises the task depending on the task that generated the
        // conversation,
        // if it exists (otherwise: error)
        else if (transition.getPreviousState()
            .getType() == ConversationStateType.INITIAL) {
          if (onGoingConv.getDependentTask() != null) {
            newTask.initialiseBasedOnDependentTask(this, newState,
                onGoingConv.getDependentTask());
            onGoingConv.setGeneratedTask(newTask);
          } // if (onGoingConv.getDependentTask() != null)
          // Previous state = initial state and no dependent task: not managed
          // case.
          // I.e. a conversational object should always initiate a conversation
          // containing processing states within a task.
          else {
            FSMBasedConversationalObject.logger
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
          FSMBasedConversationalObject.logger
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
          newTask.setTaskOwner(this);
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

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

  /**
   * Retrieves an on-going conversation on <code>this</code> from its Id and
   * partner (the partner is used for double checking).
   * <p>
   * Récupère une conversation en cours sur <code>this</code> à partir de son Id
   * et de l'objet partenaire (utilisé pour vérification).
   * @param conversationId the Id of the conversation to retrieve
   * @param partner the partner in the conversation
   * @return the OnGoingConversation corresponding to these Id and partner. It
   *         can be {@code null} if the conversation has not been created yet
   *         (e.g. {@code this} has just revceived the first message of this
   *         conversation).
   * @throws ConversationRetrievalException if a conversation of this Id but
   *           with another partner is found
   */
  private OnGoingConversation retrieveConversation(long conversationId,
      FSMBasedConversationalObject partner)
      throws ConversationRetrievalException {
    // Searches through the set of on going conversations for an instance
    // having the right Id
    OnGoingConversation foundConv = null;
    for (OnGoingConversation onGoingConv : this.getOnGoingConversations()) {
      // Checks if the current conversation has the right Id
      if (onGoingConv.getConversationId() == conversationId) {
        foundConv = onGoingConv;
        break;
      }
    } // for (OnGoingConversation onGoingConv : this.getOnGoingConversations())
    // If a conversation with the right Id has been found, error if
    // the partner is not the expected one
    if ((foundConv != null) && (!foundConv.getPartner().equals(partner))) {
      FSMBasedConversationalObject.logger.error(
          "Conversation with Id " + conversationId + " on " + this.toString()
              + "is with partner " + foundConv.getPartner().toString()
              + ", not with partner " + partner + ".");
      throw new ConversationRetrievalException(this, conversationId, partner);
    }
    // return the found conversation (possibly null)
    return foundConv;
  }

  public Set<Task> getGeneratedTasks() {
    Set<Task> toReturn = new HashSet<Task>();
    for (OnGoingConversation conv : this.getOnGoingConversations()) {
      toReturn.add(conv.getGeneratedTask());
    }
    return toReturn;
  }

  /**
   * Clears all the information related to previous conversations. Useful when
   * CartACom needs to be triggered more than once with the same agents.
   */
  @Override
  public void clearConversations() {
    this.onGoingConversations.clear();
    this.receivedMessages.clear();
    this.tasks.clear();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((onGoingConversations == null) ? 0
        : onGoingConversations.hashCode());
    result = prime * result
        + ((receivedMessages == null) ? 0 : receivedMessages.hashCode());
    result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FSMBasedConversationalObject other = (FSMBasedConversationalObject) obj;
    if (onGoingConversations == null) {
      if (other.onGoingConversations != null)
        return false;
    } else if (!onGoingConversations.equals(other.onGoingConversations))
      return false;
    if (receivedMessages == null) {
      if (other.receivedMessages != null)
        return false;
    } else if (!receivedMessages.equals(other.receivedMessages))
      return false;
    if (tasks == null) {
      if (other.tasks != null)
        return false;
    } else if (!tasks.equals(other.tasks))
      return false;
    return true;
  }

}
