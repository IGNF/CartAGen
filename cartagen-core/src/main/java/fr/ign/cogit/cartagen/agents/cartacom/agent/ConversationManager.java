/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent;

/**
 * An object that manages the conversations of another object known as <code>
 * managedObject</code> , to give this object the abilities of a conversational
 * object. The intended use is that class of the managed object implements
 * {@link ConversationalObject} and has a delegation to
 * {@link ConversationManager}. Thus it implements the methods of
 * ConversationalObject by encapsulating those of its conversation manager. The
 * class of the managed object must implement ConversationManageable.
 * 
 * Requirements for implementation: <li>
 * <ul>
 * The classes implementing ConversationManager should have only one
 * constructor, parameterised by the managed object. The default
 * (unparametrised) constructor should be made private.
 * </ul>
 * <ul>
 * Methods sendMessage and initiateConversation, inherited from <code>
 * ConversationalObject</code> , should send a message (resp. initiate a
 * conversation) "in the name of" the managed object, i.e. the managed object
 * should appear as the sender of the message (resp. initiator of the
 * conversation).
 * </ul>
 * </li>
 * 
 * @author CDuchene
 */
public interface ConversationManager extends ConversationalObject {

  /**
   * Getter for managedObject.
   * @return the ConversationManageable of which <code>this</code> manages the
   *         conversations
   */
  ConversationManageable getManagedObject();

  /**
   * Sets the managedObject associated to <code>this</code>.
   * @param agent the managedObject to set
   */

  void setManagedObject(ConversationManageable managedObject);

}
