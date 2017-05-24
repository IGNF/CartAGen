package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.task.TaskResult;

/**
 * Transition between two states of a "half conversation scenario". In a
 * conversation, a transition occurs when the agent sends or receives a message
 * 
 * @author CDuchene
 * @see HalfConversationScenario
 * @see ConversationState
 * 
 */
public class ConversationTransition {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(ConversationTransition.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * HalfConversationScenario this transition belongs to
   */
  private HalfConversationScenario halfConversationScenario = null;
  /**
   * ConversationState immediately before this transition in the
   * HalfConversationScenario
   */
  private ConversationState previousState = null;
  /**
   * ConversationState immediately after this transition in the
   * HalfConversationScenario
   */
  private ConversationState followingState = null;
  /**
   * The {@linkplain Performative} that has been received by the CartacomAgent
   * during this transition. Only valid (non null) if the previous state
   * <ul>
   * <li>is of type {@linkplain ConversationStateType#WAITING WAITING}</li>
   * <li>is of type {@linkplain ConversationStateType#INITIAL INITIAL} and the
   * {@linkplain HalfConversationScenario#describedRole role} described by the
   * HalfConversationScenario is RESPONDENT.</li>
   * </ul>
   */
  private Performative receivedPerformative = null;
  /**
   * The {@linkplain Performative} that the CartacomAgent has to send during
   * this transition. Only valid (non null) if the previous state
   * <ul>
   * <li>is of type {@linkplain ConversationStateType.PROCESSING PROCESSING}
   * </li>
   * <li>is of type {@linkplain ConversationStateType.INITIAL INITIAL} and the
   * {@linkplain HalfConversationScenario#describedRole role} described by the
   * HalfConversationScenario is INITIATOR.</li>
   * </ul>
   */
  private Performative performativeToSend = null;
  /**
   * The result of the task that has been executed at the state previous to this
   * transition. It should be one of the possible {@code TaskResult} defined for
   * the {@code Task} that is associated to the state previous to this
   * transition. This field is valid (non null) if and only if the previous
   * state
   * <ul>
   * <li>is of type {@linkplain ConversationStateType.PROCESSING PROCESSING}
   * </li>
   * <li>is of type {@linkplain ConversationStateType.INITIAL INITIAL} and the
   * {@linkplain HalfConversationScenario#describedRole role} described by the
   * HalfConversationScenario is INITIATOR.</li>
   * </ul>
   */
  private TaskResult previousStateTaskResult = null;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * The default constructor is private, to force the use of the static methods
   * newXxxConversationTransition()
   */
  private ConversationTransition() {
    super();
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Method to build a Transition that corresponds to the reception of a
   * message.
   * @param halfConversationScenario The HalfConversationScenario this
   *          transition belongs to
   * @param previousState The ConversationState previous to this transition in
   *          the scenario
   * @param followingState The ConversationState following this transition in
   *          the scenario
   * @param receivedPerformative The received performative
   * @return the created transition
   */
  public static ConversationTransition newReceptionConversationTransition(
      HalfConversationScenario halfConversationScenario,
      ConversationState previousState, ConversationState followingState,
      Performative receivedPerformative) {
    // Variables
    ConversationTransition convTransition;
    // Consistency check: if the previous state is of type PROCESSING
    // or FINAL, there is a problem
    if ((previousState.getType() == ConversationStateType.PROCESSING)
        || (previousState.getType() == ConversationStateType.FINAL)) {
      ConversationTransition.logger
          .error("Attempt to create a reception transition in a conversation"
              + "scenario after a PROCESSING or FINAL state");
      return null;
    }
    // Create a new transition and initialise its fields
    convTransition = new ConversationTransition();
    convTransition.halfConversationScenario = halfConversationScenario;
    convTransition.followingState = followingState;
    convTransition.receivedPerformative = receivedPerformative;
    // Register this transition as a possible following transition of the
    // previous state
    // passed as parameter
    previousState.addFollowingTransition(convTransition);
    // And return the created transition
    return convTransition;
  }

  /**
   * Method to build a Transition that corresponds to the sending of a message.
   * @param halfConversationScenario The HalfConversationScenario this
   *          transition belongs to
   * @param previousState The ConversationState previous to this transition in
   *          the scenario
   * @param followingState The ConversationState following this transition in
   *          the scenario
   * @param previousStateTaskResult The result of the task executed at previous
   *          state, or null if the previous state is not a processing state.
   * @param performativeToSend The performative to send
   * @return the created transition
   */
  public static ConversationTransition newSendingConversationTransition(
      HalfConversationScenario halfConversationScenario,
      ConversationState previousState, ConversationState followingState,
      TaskResult previousStateTaskResult, Performative performativeToSend) {
    // Variables
    ConversationTransition convTransition;
    // Consistency check: if the previous state is of type WAITING
    // or FINAL, there is a problem
    if ((previousState.getType() == ConversationStateType.FINAL)
        || (previousState.getType() == ConversationStateType.WAITING)) {
      ConversationTransition.logger
          .error("Attempt to create a sending transition in a conversation"
              + "scenario after a FINAL or WAITING state");
      return null;
    }
    // Create a new transition and initialise its fields
    convTransition = new ConversationTransition();
    convTransition.setHalfConversationScenario(halfConversationScenario);
    convTransition.setPreviousState(previousState);
    convTransition.setFollowingState(followingState);
    convTransition.setPreviousStateTaskResult(previousStateTaskResult);
    convTransition.setPerformativeToSend(performativeToSend);
    // Registers this transition as a possible following transition of the
    // previous state
    // passed as parameter
    previousState.addFollowingTransition(convTransition);
    // And return the created transition
    return convTransition;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for halfConversationScenario.
   * @return the halfConversationScenario
   */
  public HalfConversationScenario getHalfConversationScenario() {
    return this.halfConversationScenario;
  }

  /**
   * Getter for previousState.
   * @return the previousState
   */
  public ConversationState getPreviousState() {
    return this.previousState;
  }

  /**
   * Getter for followingState.
   * @return the followingState
   */
  public ConversationState getFollowingState() {
    return this.followingState;
  }

  /**
   * Getter for receivedPerformative.
   * @return the receivedPerformative
   */
  public Performative getReceivedPerformative() {
    return this.receivedPerformative;
  }

  /**
   * Getter for performativeToSend.
   * @return the performativeToSend
   */
  public Performative getPerformativeToSend() {
    return this.performativeToSend;
  }

  /**
   * Getter for previousStateTaskResult.
   * @return the previousStateTaskResult
   */
  public TaskResult getPreviousStateTaskResult() {
    return this.previousStateTaskResult;
  }

  /**
   * Setter for halfConversationScenario.
   * @param halfConversationScenario the halfConversationScenario to set
   */
  protected void setHalfConversationScenario(
      HalfConversationScenario halfConversationScenario) {
    this.halfConversationScenario = halfConversationScenario;
  }

  /**
   * Setter for previousState. Also updates the reverse reference from
   * previousState to {@code this}. To break the reference use
   * {@code this.setPreviousState(null)}
   * @param previousState the previousState to set
   */
  protected void setPreviousState(ConversationState previousState) {
    ConversationState oldPreviousState = this.previousState;
    this.previousState = previousState;
    if (oldPreviousState != null) {
      oldPreviousState.getFollowingTransitionsSet().remove(this);
    }
    if (previousState != null) {
      if (!previousState.getFollowingTransitionsSet().contains(this)) {
        previousState.getFollowingTransitionsSet().add(this);
      }
    }
  }

  /**
   * Setter for followingState.
   * @param followingState the followingState to set
   */
  protected void setFollowingState(ConversationState followingState) {
    this.followingState = followingState;
  }

  /**
   * Setter for receivedPerformative.
   * @param receivedPerformative the receivedPerformative to set
   */
  protected void setReceivedPerformative(Performative receivedPerformative) {
    this.receivedPerformative = receivedPerformative;
  }

  /**
   * Setter for performativeToSend.
   * @param performativeToSend the performativeToSend to set
   */
  protected void setPerformativeToSend(Performative performativeToSend) {
    this.performativeToSend = performativeToSend;
  }

  /**
   * Setter for previousStateTaskResult.
   * @param previousStateTaskResult the previousStateTaskResult to set
   */
  protected void setPreviousStateTaskResult(
      TaskResult previousStateTaskResult) {
    this.previousStateTaskResult = previousStateTaskResult;
  }

  // /////////////////////////////////////////////
  // Public methods //
  // /////////////////////////////////////////////

  /**
   * {@inheritDoc}
   * <p>
   * Hashcode generated based on the received performative or the performative
   * to send (given how transitions are used, one and only one is not null, and
   * normally transitions grouped in a same HashSet should all have the same one
   * instanciated).
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.performativeToSend == null) ? 0
        : this.performativeToSend.hashCode());
    result = prime * result + ((this.receivedPerformative == null) ? 0
        : this.receivedPerformative.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    ConversationTransition other = (ConversationTransition) obj;
    if (this.performativeToSend == null) {
      if (other.performativeToSend != null) {
        return false;
      }
    } else if (!this.performativeToSend.equals(other.performativeToSend)) {
      return false;
    }
    if (this.receivedPerformative == null) {
      if (other.receivedPerformative != null) {
        return false;
      }
    } else if (!this.receivedPerformative.equals(other.receivedPerformative)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (this.performativeToSend != null) {
      return this.performativeToSend.toString();
    }
    return this.receivedPerformative.toString();
  }

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
