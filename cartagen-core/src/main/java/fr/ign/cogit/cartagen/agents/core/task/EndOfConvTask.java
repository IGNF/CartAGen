/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;

/**
 * A task that can be associated to a final state of a conversation, when this
 * state follows the reception of a message. It has a specific {@code execute()}
 * method parameterised by the sender of the message, the argument of the
 * message and the (final) state to which it is attached - in case a same task
 * would be used for the final state of several different conversation
 * scenarios. This execute() method is only supposed to modify the 'mental
 * state' of the conversational object to ackowledge the facts meant by the
 * received message.
 * @author CDuchene
 * 
 */
public interface EndOfConvTask extends TaskWithinConversation {

  /**
   * What to do to acknowledge the message that received during the transition
   * that generated this task. WARNING this method should switch the status of
   * this task to {@code TaskStatus.FINISHED} at the end.
   * @param partner the partner in the conversation that generated this task
   *          (which is the sender of the message)
   * @param convState the {@code ConversationState} to which this task is
   *          associated
   * @param receivedArgument the argument of the received message.
   */
  public void execute(ConversationalObject partner, ConversationState convState,
      Object receivedArgument);

}
