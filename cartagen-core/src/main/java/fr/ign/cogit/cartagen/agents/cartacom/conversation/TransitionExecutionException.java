package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.FSMBasedConversationalObject;

/**
 * Thrown when a conversational object tries to execute a transition in a
 * conversation that is not consistent with the current reference state of the
 * conversation.
 * <p>
 * Exception envoyée lorsque'un objet conversationel n'arrive pas à executer une
 * transition dans une conversation parce que la transition à exécuter n'est pas
 * cohérente avec l'état de référence courant de la conversation.
 * @author CDuchene
 * 
 */
public class TransitionExecutionException extends Exception {

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
  private TransitionExecutionException() {
    super();
  }

  /**
   * Constructor parameterised by the conversational object that fails making
   * the transition, the conversation and the failing transition.
   * <p>
   * Constructeur paramétré par l'agent n'ayant pas réussi à executer la
   * transition, la conversation et la transition ayant échoué.
   * 
   * @param convObj the ConversationalObject on which an attempt to retrieve a
   *          conversation has failed
   * @param conversation the conversation
   * @param transition the attempted transition
   */
  public TransitionExecutionException(FSMBasedConversationalObject convObj,
      FSMBasedOnGoingConversation conversation,
      ConversationTransition transition) {
    // Gets the conversation Id, partner, current state

    // Calls the super constructor, parameterised by the message to print
    super("Conversation Id " + conversation.getConversationId() + " between "
        + convObj.toString() + " and " + conversation.getPartner().toString()
        + " : failed to execute transition " + transition.toString()
        + " because it is not consistent with current conversation state ("
        + conversation.getReferenceConversationState().toString() + ").");
  }

}
