/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.BehavioralAgent;

/**
 * message interface
 * 
 * This class develops the messages created and sent by agents
 * 
 * @author GAltay
 * 
 */
public interface AMessage {

  /**
   * 
   * @return sender of this message
   */
  public BehavioralAgent getSender();

  /**
   * 
   * @return reciever of this message
   */
  public BehavioralAgent getReceiver();

  /**
   * 
   * @return the performative of the message
   */
  public Performative getPerformative();

  /**
   * 
   * @return the argument of the message
   */
  public AdHocArgument getArgument();

  /**
   * 
   * @return the conversation that this message is belonged
   */
  public AConversation getBelongedConversation();

  /**
   * marks that the message is read
   */
  public void markAsRead();

  /**
   * 
   * @return {@code true} if already read {@code false} else
   */
  public boolean isRead();

  /**
   * sends the message (writes it in the recievers inbox)
   */
  public void send();

  /**
   * creates a reply to this message
   * 
   * @param perf performative of new message
   * @param arg argument of new message
   * @return the reply message
   */
  public AMessage createReply(Performative perf, AdHocArgument arg);
}
