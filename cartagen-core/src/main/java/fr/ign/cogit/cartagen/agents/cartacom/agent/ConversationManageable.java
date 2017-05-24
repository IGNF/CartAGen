/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.Message;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;

/**
 * An interface that objects that are made conversational by using a delegation
 * to a {@link ConversationManager} should implement.
 * @author CDuchene
 * 
 */
public interface ConversationManageable extends ConversationalObject {

  /**
   * From a collection of received messages, makes a list that is ordered in
   * such a way that the messages that should be handled first appear first. The
   * content of this method depends on the possible contents of the expected
   * messages and on the way they are to be handled.
   * @param receivedMessages a collection containing the received (signed)
   *          messages to order
   * @return the received messages, ordered by emergency in a list
   */
  public List<SignedMessage> orderReceivedMessagesByEmergency(
      Collection<SignedMessage> receivedMessages);

  /**
   * A factory method that composes a message of the relevant class depending on
   * the language spoken by this object.
   * @param ConversationId The identifier of the conversation this message
   *          belongs to
   * @param performative The {@link Performative performative} of the message
   * @param argument The argument of the message. Its actual type depends on the
   *          language this object speaks with its conversation partners.
   * @return a message of a concrete class implementing <code>Message</code>.
   *         The class of this returned message is chosen depending on the
   *         actual type of the argument, which this object should be aware of.
   */
  public Message composeMessage(long ConversationId, Performative performative,
      Object argument);

}
