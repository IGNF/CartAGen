/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.BehavioralAgent;

/**
 * message box interface
 * 
 * this class is develops an inbox for an agent and menages the messages
 * received
 * 
 * @author GAltay
 * 
 */
public interface AMessageBox {

  /**
   * adds the message in the box
   * 
   * @param message the message to add
   */
  public void addNewMessage(AMessage message);

  /**
   * adds the conversation to message box
   * 
   * @param conversation the conversation to be added
   */
  public void addConversation(AConversation conversation);

  /**
   * gets the unread messages by filter passed by parameter and marks them
   * "read"
   * 
   * @param subject the filtering subject
   * @return the filtered messages
   */
  public Set<AMessage> getUnreadMessagesBySelection(
      AConversationSubject subject);

  /**
   * checks if there is any unread message matching to the filter passed by
   * parameter
   * 
   * @param subject the filter
   * @return {@code true} if any unread message
   */
  public boolean hasUnreadMessagesBySelection(AConversationSubject subject);

  /**
   * gets the unread messages by filter passed by parameter without marking them
   * as "read"
   * 
   * @param agent the filter
   * @return the set of filtered messages
   */
  public Set<AMessage> getMessagesBySelection(BehavioralAgent agent);

  /**
   * 
   * @return the owner of this box
   */
  public BehavioralAgent getOwner();

  /**
   * gets the unread messages filtered by parameters and marks them "read"
   * 
   * @param subject the filter subject
   * @param perf the filter parameter
   * @return the set of filtered unread messages
   */
  public Set<AMessage> getUnreadMessagesBySelection(
      AConversationSubject subject, Performative perf);

  /**
   * gets the unread messages filtered by parameters without marking them as
   * "read"
   * 
   * @param subject the filter subject
   * @param perf the filter parameter
   * @return the set of filtered unread messages
   */
  public Set<AMessage> checkUnreadMessagesBySelection(
      AConversationSubject subject, Performative perf);

  /**
   * marks messages as read according to the filter passed by parameter
   * 
   * @param sender the filter
   */
  public void markMessagesAsRead(BehavioralAgent sender);
}
