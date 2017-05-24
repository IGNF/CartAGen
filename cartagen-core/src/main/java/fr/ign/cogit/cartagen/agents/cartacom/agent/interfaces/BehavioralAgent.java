/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.AConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessageBox;

/**
 * behavioral agent interface, common agent type for the plateforme that
 * developped by GAltay
 * 
 * @author GAltay
 * 
 */
public interface BehavioralAgent {

  /**
   * should be used only by a behaviour class
   * 
   * @return the behavior of the agent
   */
  public Behavior getBehavior();

  /**
   * should be used only by the behavior instance of this agent
   * 
   * @return
   */
  public Set<BehavioralAgent> getNeighbors();

  /**
   * should be used only by the behavior instance of this agent
   * 
   * @param neighbor the neighbour
   */
  public void addNeighbor(BehavioralAgent neighbor);

  /**
   * reaches agents message box, should be used only by the behavior instance of
   * this agent
   * 
   * @return agents message box
   */
  public AMessageBox getMessageBox();

  /**
   * should be used only by communication system
   * 
   * @param conversation
   */
  public void addConversation(AConversation conversation);

  /**
   * should be used only by communication system
   * 
   * @param message
   */
  public void addMessageToAgentsBox(AMessage message);

  /**
   * should be used only by a strategy class
   * 
   * @param behavior
   */
  public void setBehavior(Behavior behavior);

  /**
   * 
   * @return agents id (the same with feature id)
   */
  public int getID();

}
