/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * A message that an agent can send to another agent. It is composed of a
 * performative and an argument following the speech acts based theory, and
 * includes the identifier of the conversation it is part of. The form of the
 * argument is left to the choice of the class implementing this interface:
 * string, ad hoc structured object, etc. At this level it is just considered an
 * Object.
 * @author CDuchene
 */
public interface Message {

  /**
   * Returns the identifier of the conversation the message is part of
   * @return the conversation Id.
   */
  public long getConversationId();

  /**
   * Returns the performative of the message.
   * @return the performative of the message.
   */
  public Performative getPerformative();

  /**
   * Returns the argument of the message.
   * @return the argument of the message.
   */
  public Object getArgument();
}
