package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;

/**
 * Thrown when there is an attempt to retrieve a HalfConversationScenario by its
 * described role and the performative of its first transition, and no scenario
 * matching these role+first performative has been declared. In other words, a
 * conversational object tries to begin a conversation with a message that the
 * system cannot match to a predefined scenario.
 * @author CDuchene
 * 
 */
public class ConversationRetrievalException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2923899352056030603L;

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  /**
   * Default constructor made private to force the use of the parameterised
   * constructor. Constructs an IllegalStateException with no detail message. A
   * detail message is a String that describes this particular exception.
   * <p>
   * Constructeur par défaut rendu privé pour forcer l'utilisation du
   * constructeur spécifique.
   */
  @SuppressWarnings("unused")
  private ConversationRetrievalException() {
    super();
  }

  /**
   * Constructor parameterised the Id and the expected partner of the
   * conversation to retrieve.
   * <p>
   * Constructeur paramétré par l'identifiant et l'interlocuteur de la
   * conversation en cours qu'on n'a pas réussi à retrouver.
   * @param convObj the ConversationalObject on which an attempt to retrieve a
   *          conversation has failed
   * @param conversationId the Id of the conversation to retrieve
   * @param partner the partner in the conversation to retrieve
   */
  public ConversationRetrievalException(ConversationalObject convObj,
      long conversationId, ConversationalObject partner) {
    // Calls the super constructor, parameterised by the message to print
    super("Problem while retrieving conversation with Id " + conversationId
        + " on " + convObj.toString() + "with expected partner "
        + partner.toString() + ".");
  }

}
