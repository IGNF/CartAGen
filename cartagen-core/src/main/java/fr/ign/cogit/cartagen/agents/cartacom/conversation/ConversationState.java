package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.task.ProcessingTaskWithinConv;
import fr.ign.cogit.cartagen.agents.core.task.TaskWithinConversation;

/**
 * State of a conversation scenario described by a
 * {@link HalfConversationScenario} . This class has no public constructor but
 * offers four static methods to construct instances, depending on the {@code
 * type} of the state to construct.
 * @author CDuchene
 */
public class ConversationState {

  // TODO Mettre des setters et les utiliser à la place des affectations dans
  // les constructeurs.
  // Et gérer le lien bidirectionnel vers le scenario comme tel.

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  /**
   * HalfConversationScenario this state belongs to
   */
  private HalfConversationScenario halfConversationScenario = null;
  /**
   * The {@linkplain ConversationStateType type} of this conversation state.
   */
  private ConversationStateType type = null;
  /**
   * The name of the state (that refers to it without ambiguity within this half
   * scenario). It is suggested that the name is "java class like" and begins
   * with InitialXxx for initial states, FinalXxx for final states, and with a
   * verb at progressive present for intermediate states. E.g.
   * InitialRequestForAction, TryingRequestedAction, FinalSuccess.
   */
  private String name = null;
  /**
   * The task to execute at this conversation state (a class inheriting from the
   * {@link fr.ign.cogit.task.TaskWithinConversation TaskWithinConversation}
   * class). Only valid (non null) if the {@code type} of this state is {@code
   * ConversationStateType.PROCESSING}.
   */
  private Class<? extends TaskWithinConversation> classOfTaskToExecute = null;
  /**
   * The set of transitions that can follow this state in this scenario,
   * depending on the next received message or task result. (bidirectional
   * reference, automatically managed).
   */
  private Set<ConversationTransition> followingTransitionsSet = new HashSet<ConversationTransition>();

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Builds a conversation state of type INITIAL for the
   * HalfConversationScenario passed as parameter, and initialises its {@code
   * followingTransitions} to an empty HashSet.
   * @param halfConversationScenario The half conversation scenario this state
   *          belongs to
   * @param name the name of this state
   * @return the constructed state
   */
  public static ConversationState InitialConversationStateFactory(
      HalfConversationScenario halfConversationScenario, String name) {
    // Builds a new ConversationState, registers its halfConversationScenario
    // and sets its type to INITIAL
    ConversationState state = new ConversationState();
    state.halfConversationScenario = halfConversationScenario;
    state.type = ConversationStateType.INITIAL;
    state.name = name;
    // Initialises its followingTransitions to an empty HashSet
    state.followingTransitionsSet = new HashSet<ConversationTransition>();
    // Initialises the reverse reference to it on its halfConversationScenario
    halfConversationScenario.getConversationStates().add(state);
    return state;
  }

  /**
   * Builds a conversation state of type PROCESSING for the
   * HalfConversationScenario passed as parameter, and initialises its {@code
   * followingTransitions} to an empty HashSet.
   * @param halfConversationScenario The half conversation scenario this state
   *          belongs to
   * @param name the name of this state
   * @param classOfTaskToExecute The class that encapsulates the task to execute
   *          at this state
   * @return the constructed state
   */
  public static ConversationState newProcessingConversationState(
      HalfConversationScenario halfConversationScenario, String name,
      Class<? extends ProcessingTaskWithinConv> classOfTaskToExecute) {
    // Builds a new ConversationState, registers its halfConversationScenario
    // and classOfTaskToExecute, and sets its type to PROCESSING
    ConversationState state = new ConversationState();
    state.halfConversationScenario = halfConversationScenario;
    state.type = ConversationStateType.PROCESSING;
    state.name = name;
    state.classOfTaskToExecute = classOfTaskToExecute;
    // Initialises its followingTransitions to an empty HashSet
    state.followingTransitionsSet = new HashSet<ConversationTransition>();
    // Initialises the reverse reference to it on its halfConversationScenario
    halfConversationScenario.getConversationStates().add(state);
    return state;
  }

  /**
   * Builds a conversation state of type WAITING for the
   * HalfConversationScenario passed as parameter, and initialises its {@code
   * followingTransitions} to an empty HashSet.
   * @param halfConversationScenario The half conversation scenario this state
   *          belongs to
   * @param name the name of this state
   * @return the constructed state
   */
  public static ConversationState newWaitingConversationState(
      HalfConversationScenario halfConversationScenario, String name) {
    // Builds a new ConversationState, registers its halfConversationScenario
    // and classOfTaskToExecute, and sets its type to PROCESSING
    ConversationState state = new ConversationState();
    state.halfConversationScenario = halfConversationScenario;
    state.type = ConversationStateType.WAITING;
    state.name = name;
    // Initialises its followingTransitions to an empty HashSet
    state.followingTransitionsSet = new HashSet<ConversationTransition>();
    // Initialises the reverse reference to it on its halfConversationScenario
    halfConversationScenario.getConversationStates().add(state);
    return state;
  }

  /**
   * Builds a conversation state of type FINAL for the HalfConversationScenario
   * passed as parameter. Its {@code followingTransitions} are left as null as a
   * final state is not supposed to have any following transition.
   * @param halfConversationScenario The half conversation scenario this state
   *          belongs to
   * @param name the name of this state
   * @param classOfTaskToExecute The class that encapsulates the task to execute
   *          at this state, or null if none
   * @return the constructed state
   */
  public static ConversationState newFinalConversationState(
      HalfConversationScenario halfConversationScenario, String name,
      Class<? extends TaskWithinConversation> classOfTaskToExecute) {
    // Builds a new ConversationState, registers its halfConversationScenario
    // and classOfTaskToExecute, and sets its type to FINAL
    ConversationState state = new ConversationState();
    state.halfConversationScenario = halfConversationScenario;
    state.type = ConversationStateType.FINAL;
    state.name = name;
    state.classOfTaskToExecute = classOfTaskToExecute;
    // Initialises the reverse reference to it on its halfConversationScenario
    halfConversationScenario.getConversationStates().add(state);
    return state;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors - none - use the static methods
  // newXxxConversationState() instead //

  // Getters and setters //

  /**
   * Getter for name.
   * @return the name of the conversation state
   */
  public String getName() {
    return this.name;
  }

  /**
   * Getter for type.
   * @return the type
   */
  public ConversationStateType getType() {
    return this.type;
  }

  /**
   * Setter for type.
   * @param type the type to set
   */
  public void setType(ConversationStateType type) {
    this.type = type;
  }

  /**
   * Getter for classOfTaskToExecute.
   * @return the classOfTaskToExecute
   */
  public Class<? extends TaskWithinConversation> getClassOfTaskToExecute() {
    return this.classOfTaskToExecute;
  }

  /**
   * Setter for classOfTaskToExecute.
   * @param classOfTaskToExecute the classOfTaskToExecute to set
   */
  public void setClassOfTaskToExecute(
      Class<? extends TaskWithinConversation> classOfTaskToExecute) {
    this.classOfTaskToExecute = classOfTaskToExecute;
  }

  /**
   * Getter for followingTransitionsSet.
   * @return the followingTransitionsSet
   */
  public Set<ConversationTransition> getFollowingTransitionsSet() {
    return this.followingTransitionsSet;
  }

  /**
   * Setter for followingTransitionsSet. Also updates the reverse reference from
   * each element of followingTransitionsSet to {@code this}. To break the
   * reference use {@code this.setFollowingTransitionsSet(new
   * HashSet<ConversationTransition>())}
   * @param followingTransitionsSet the set of followingTransitions to set
   */
  public void setFollowingTransitionsSet(
      Set<ConversationTransition> followingTransitionsSet) {
    Set<ConversationTransition> oldFollowingTransitionsSet = new HashSet<ConversationTransition>(
        this.followingTransitionsSet);
    for (ConversationTransition followingTransition : oldFollowingTransitionsSet) {
      followingTransition.setPreviousState(null);
    }
    for (ConversationTransition followingTransition : followingTransitionsSet) {
      followingTransition.setPreviousState(this);
    }
  }

  // Other public methods //

  /**
   * Adds a transition to the followingTransitions of this ConversationState,
   * and updates the reverse reference from the added ConversationTransition to
   * {@code this}.
   */
  public void addFollowingTransition(
      ConversationTransition followingTransition) {
    if (followingTransition == null) {
      return;
    }
    this.followingTransitionsSet.add(followingTransition);
    followingTransition.setPreviousState(this);
  }

  /**
   * Concatenates the name of this state with its type.
   * 
   */
  @Override
  public String toString() {
    return this.name + "(" + this.type.toString() + ") in scenario "
        + this.halfConversationScenario.toString();
  }

  /**
   * Among the following transitions of this conversation state, find the one
   * that has the performative in parameter as received performative.
   * @param performative
   * @return the matching transition - can be null
   */
  public ConversationTransition findFollowingTransition(
      Performative receivedPerformative) {
    ConversationTransition foundTransition = null;
    for (ConversationTransition transition : this
        .getFollowingTransitionsSet()) {
      if (transition.getReceivedPerformative() == receivedPerformative) {
        foundTransition = transition;
        break;
      }
    }
    return foundTransition;
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
  /**
   * The default constructor is private, to force the use of one of the factory
   * methods
   */
  private ConversationState() {
    super();
  }

}
