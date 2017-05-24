/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;

/**
 * A task that can be associated to a processing state of a conversation, i.e. a
 * state where the conversational object is computing something before being
 * able to go on with the conversation.
 * <p>
 * Such a task should be able to return an argument for the next message the
 * conversational object will send (the performative should normally be
 * automatically determined depending on the result of the task, following the
 * pre-established dialog scenario the conversation follows). It should also
 * publish at least a method to initialise itself, since instances of such task
 * classes can be be created by the conversation engine with a {@code
 * newInstance()}.
 * @author CDuchene
 */
public interface ProcessingTaskWithinConv extends TaskWithinConversation {

  /**
   * Returns the argument for the message that will be sent by the
   * conversational object executing the task, once the task is completed. The
   * argument can be dependent on the result of the task.
   * @return the argument to send if any
   */
  public Object getArgumentToSend();

  /**
   * Setter for the argument for the message that will be sent by the
   * conversational object executing the task, once the task is completed. The
   * argument can be dependent on the result of the task.
   * @param argumentToSend the argument to send once the task is completed.
   */
  public void setArgumentToSend(Object argumentToSend);

  /**
   * For a task associated to a processing state that follows the reception of a
   * message.
   * <p>
   * Initialises this task depending on the conversation state to which it is
   * attached (in case a same task would be attached to several states of the
   * same or different conversations scenarios), and the argument of the
   * received message.
   * <p>
   * Should be called just after constructing this task with a call to {@code
   * newInstance()}.
   * @param convObj the conversational object owning this task
   * @param convState the conversation state associated to this task
   * @param receivedArgument the argument of the message received during the
   *          transition that led to the conversation state associated to this
   *          task.
   */
  public void initialiseBasedOnReceivedMessage(ConversationalObject convObj,
      ConversationState convState, Object receivedArgument);

  /**
   * For a task associated to a processing state that follows another processing
   * state (i.e. the object has sent a progression message between two tasks).
   * <p>
   * Initialises this task depending on the conversation state to which it is
   * attached (in case a same task would be attached to several states of the
   * same or different conversations scenarios), and the task executed at
   * previous state.
   * <p>
   * Should be called just after constructing this task with a call to {@code
   * newInstance()}.
   * @param convObj the conversational object owning this task
   * @param convState the conversation state associated to this task
   * @param previousTask the task executed at the previous state of the
   *          conversation
   */
  public void initialiseBasedOnPreviousTask(ConversationalObject convObj,
      ConversationState convState, ProcessingTaskWithinConv previousTask);

  /**
   * For a task associated to a processing state that follows the initial state
   * (i.e. the object has sent a warning before beginning a task).
   * <p>
   * Initialises this task depending on the conversation state to which it is
   * attached (in case a same task would be attached to several states of the
   * same or different conversations scenarios), and the task that generated the
   * conversation.
   * <p>
   * Should be called just after constructing this task with a call to {@code
   * newInstance()}.
   * @param convObj the conversational object owning this task
   * @param convState the conversation state associated to this task
   * @param dependentTask the task that generated the conversation
   */
  public void initialiseBasedOnDependentTask(ConversationalObject convObj,
      ConversationState convState, Task dependentTask);
}
