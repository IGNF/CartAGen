/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.BehavioralAgent;

/**
 * conversation interface
 * 
 * This class holds the whole conversation, it allows the recover of a
 * conversation past
 * 
 * @author GAltay
 */
public interface AConversation {

  /**
   * 
   * @return the conversation id
   */
  public int getId();

  /**
   * 
   * @return the initiator of the conversation
   */
  public BehavioralAgent getInitiator();

  /**
   * 
   * @return the partner of the conversation
   */
  public BehavioralAgent getPartner();

  /**
   * 
   * @return the subject of the conversation
   */
  public AConversationSubject getConversationSubject();

  /**
   * 
   * @return the set of messages that belongs to this conversation
   */
  public Set<AMessage> getMessages();

  /**
   * adds the message to this conversation
   * 
   * @param message the message to add
   */
  public void addMessage(AMessage message);

}
