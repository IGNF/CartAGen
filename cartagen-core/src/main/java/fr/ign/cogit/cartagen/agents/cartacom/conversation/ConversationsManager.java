/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.HashMap;
import java.util.Map;

/**
 * Stereotype singleton. An entity in charge of managing the conversations in
 * the system, incrementally allocating them unique identifiers. It also holds a
 * summary of the
 * @author CDuchene
 * 
 */
public class ConversationsManager {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /** Holds the unique instance of the class */
  private static ConversationsManager CONVERSATIONS_MNG = new ConversationsManager();

  // Private fields with public getter //

  /** The last allocated conversation ID */
  private long lastAllocatedConversationId = 0;

  /**
   * A map holding a summary of the on-going and finished conversations in the
   * system. The key of the map is the Id of the conversations.
   */
  private Map<Long, ConversationSummary> conversationsSummary = new HashMap<Long, ConversationSummary>();

  // //////////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////////

  /**
   * Default constructor set private to force the singleton
   */
  private ConversationsManager() {
    super();
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /** Getter for the unique instance of the class */
  public static ConversationsManager getInstance() {
    return ConversationsManager.CONVERSATIONS_MNG;
  }

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Gets the last allocated conversation ID
   * @return the lastAllocatedConversationId
   */
  public long getLastAllocatedConversationId() {
    return this.lastAllocatedConversationId;
  }

  /**
   * Getter for conversationsSummary.
   * @return the conversationsSummary Map
   */
  public Map<Long, ConversationSummary> getConversationsSummary() {
    return this.conversationsSummary;
  }

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  /**
   * Created a new conversation ID by incrementing lastAllocatedConversationId
   * and returns it.
   * @return the new created conversation ID
   */
  public long getNewConversationId() {
    this.lastAllocatedConversationId = this.lastAllocatedConversationId + 1;
    return this.lastAllocatedConversationId;
  }

  /**
   * Reset the manager by clearing the conversations summary and re-initialising
   * the id to 0.
   */
  public void reset() {
    this.lastAllocatedConversationId = 0;
    this.conversationsSummary.clear();
  }
}
