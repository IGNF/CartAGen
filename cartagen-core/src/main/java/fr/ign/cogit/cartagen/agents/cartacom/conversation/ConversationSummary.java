/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;

/**
 * A (very brief!) summary of an on-going or finished conversation: holds the
 * first performative of the conversation, the two conversational objects taking
 * part to the conversation (initiator and respondent), a boolean indicating if
 * the conversation is finished or not and the name of the final state if the
 * conversation is finished. Objects of this class are only used as values of
 * the Map field {@link conversationsSummary} of the singleton class
 * {@linkConversationsManager}.
 * @author CDuchene
 * 
 */
public class ConversationSummary {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // Private fields with public getter //
  /**
   * The first performative of the conversation
   */
  private Performative firstPerformative;
  /**
   * The intiator of the conversation
   */
  private ConversationalObject initiator;
  /**
   * The respondent of the conversation
   */
  private ConversationalObject respondent;
  /**
   * True if the conversation is currently finished, false if it is on-going
   */
  private boolean finished;
  /**
   * The name of the final state if finished, null otherwise
   */
  private String finalStateName;

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Makes the default constructor private to force the use of the parameterised
   * constructor.
   */
  @SuppressWarnings("unused")
  private ConversationSummary() {
    super();
  }

  /**
   * Constructs a conversation summary for a given first performative, initiator
   * and respondent. Finished is set to false and the final state name, to null.
   * @param firstPerformative
   * @param initiator
   * @param respondent
   */
  public ConversationSummary(Performative firstPerformative,
      ConversationalObject initiator, ConversationalObject respondent) {
    this.firstPerformative = firstPerformative;
    this.initiator = initiator;
    this.respondent = respondent;
    this.finished = false;
    this.finalStateName = null;
  }

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for firstPerformative.
   * @return the firstPerformative
   */
  public Performative getFirstPerformative() {
    return this.firstPerformative;
  }

  /**
   * Getter for initiator.
   * @return the initiator
   */
  public ConversationalObject getInitiator() {
    return this.initiator;
  }

  /**
   * Getter for respondent.
   * @return the respondent
   */
  public ConversationalObject getRespondent() {
    return this.respondent;
  }

  /**
   * Getter for finished.
   * @return the finished
   */
  public boolean isFinished() {
    return this.finished;
  }

  /**
   * Setter for finished.
   * @param finished the finished to set
   */
  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  /**
   * Getter for finalStateName.
   * @return the finalStateName
   */
  public String getFinalStateName() {
    return this.finalStateName;
  }

  /**
   * Setter for finalStateName.
   * @param finalStateName the finalStateName to set
   */
  public void setFinalStateName(String finalStateName) {
    this.finalStateName = finalStateName;
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

}
