/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.CartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;

/**
 * A record of a message and its sender. The message box of a conversational
 * agent typically contains SignedMessages.
 * @author CDuchene
 * 
 */
public class SignedMessage {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The sender of the message
   */
  private ConversationalObject sender;
  /**
   * The message
   */
  private Message message;

  // Very private fields (no public getter) //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  /**
   * @param sender
   * @param message
   */
  public SignedMessage(ConversationalObject sender, Message message) {
    if (sender instanceof CartacomAgent) {
      this.sender = ((CartacomAgent) sender).getConversationManager();
    } else {
      this.sender = sender;
    }
    this.message = message;
  }

  // Public getters and setters //
  /**
   * Gets the sender of the message.
   * @return the sender
   */
  public ConversationalObject getSender() {
    return this.sender;
  }

  /**
   * Gets the message
   * @return the message
   */
  public Message getMessage() {
    return this.message;
  }

}
