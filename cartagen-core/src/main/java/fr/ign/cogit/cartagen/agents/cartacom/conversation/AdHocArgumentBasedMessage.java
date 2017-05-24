/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * A message that an agent can send to another agent. It is composed of a
 * performative and an argument, and includes the identifier of the conversation
 * it is part of. Its argument is an AdHocArgument i.e. an ad hoc structured
 * object (as opposed to a string for instance). The form of the argument
 * depends on the concrete subclass the message belongs to.
 * 
 * @author CDuchene
 * 
 */
public class AdHocArgumentBasedMessage implements Message {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // Private fields with public getters //

  /**
   * The ID of the conversation this message is part of
   */
  private long conversationId;

  /**
   * The performative of this message
   */
  private Performative performative = null;

  /**
     */
  private AdHocArgument argument = null;

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an AdHocArgumentBasedMessage based on the value of its fields.
   * @param conversationId the ID of the conversation the message belongs to
   * @param performative the performative of the message
   * @param argument the argument of the message (
   */
  public AdHocArgumentBasedMessage(long conversationId,
      Performative performative, AdHocArgument argument) {
    this.conversationId = conversationId;
    this.performative = performative;
    this.argument = argument;
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter of the property <tt>argument</tt>
   * @return Returns the argument.
   */
  @Override
public AdHocArgument getArgument() {
    return this.argument;
  }

  /**
   * Getter of the property <tt>conversationId</tt>
   * @return Returns the conversationId.
   */
  @Override
public long getConversationId() {
    return this.conversationId;
  }

  /**
   * Getter of the property <tt>performative</tt>
   * @return Returns the performative.
   */
  @Override
public Performative getPerformative() {
    return this.performative;
  }

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
