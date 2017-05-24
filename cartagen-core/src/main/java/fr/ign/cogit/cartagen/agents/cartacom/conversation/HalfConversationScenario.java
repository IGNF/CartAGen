/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Describes a conversation scenario seen from the point of view of one of the
 * two roles played by the agents taking part to the conversation (the
 * {@linkplain RoleInConversation.INITIATOR initiator} or the
 * {@linkplain RoleInConversation.RESPONDENT respondent} ), under the form of a
 * finite state machine (FSM) or transition graph. The FSM is composed of
 * transitions (see {@link ConversationTransition} ) describing the messages
 * that can be sent and received by the agent, and states (see
 * {@link ConversationState} ) describing what the agent should do once it has
 * received or sent a given message. This follows and refines the proposal of
 * Barbuceanu and Fox (1995) (the COOL language), and Ferber (1995, p. 348 et
 * sq.).
 * @author CDuchene
 */
public class HalfConversationScenario {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(HalfConversationScenario.class.getName());
  /**
   * All declared HalfConversationScenario.
   */
  private static List<HalfConversationScenario> ALL_DEFINED_SCENARIOS = new ArrayList<HalfConversationScenario>();

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * {@linkplain RoleInConversation Role} from the point of view of which the
   * conversation scenario is described (INITIATOR or RESPONDENT)
   */
  private RoleInConversation describedRole;
  /**
   * The states that compose this scenario
   */
  private HashSet<ConversationState> conversationStates = new HashSet<ConversationState>();
  /**
   * The transitions that compose this scenario
   */
  private HashSet<ConversationTransition> conversationTransitions = new HashSet<ConversationTransition>();
  /**
   * The initial state of this scenario
   */
  private ConversationState initialState;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Retrieves a HalfConversation scenario giving its described role and first
   * performative.
   * @param roleInConversation the role the half scenario to retrieve describes
   * @param firstPerformative the first performative of the half scenario to
   *          retrieve
   * @return the HalfConversationScenario looked for, if it exists
   * @throws ScenarioNotFoundException No scenario with these role and first
   *           performative has been found
   */
  public static HalfConversationScenario retrieveHalfConversationScenario(
      RoleInConversation roleInConversation, Performative firstPerformative)
      throws ScenarioNotFoundException {
    // local variables
    HalfConversationScenario foundScenario = null;
    ConversationState scenarioFirstState;
    Performative scenarioFirstPerformative;
    // Loop on all registered scenarios
    for (HalfConversationScenario scenario : HalfConversationScenario.ALL_DEFINED_SCENARIOS) {
      // First, check if the role matches
      if (roleInConversation != scenario.getDescribedRole()) {
        continue;
      }
      // Get the first performative of the currently checked scenario depending
      // on
      // the described role
      scenarioFirstState = scenario.getInitialState();
      if (roleInConversation == RoleInConversation.INITIATOR) {
        scenarioFirstPerformative = scenarioFirstState
            .getFollowingTransitionsSet().iterator().next()
            .getPerformativeToSend();
      } else {
        scenarioFirstPerformative = scenarioFirstState
            .getFollowingTransitionsSet().iterator().next()
            .getReceivedPerformative();
      }
      // If it matches with the firstPerformative we are looking for: found!
      if (firstPerformative == scenarioFirstPerformative) {
        foundScenario = scenario;
        break;
      } // if (firstPerformative == scenarioFirstPerformative)
    } // for (HalfConversationScenario scenario : ALL_DEFINED_SCENARIOS)
    // Case where no matching scenario was found : problem
    if (foundScenario == null) {
      HalfConversationScenario.logger.error("Attempt to retrieve "
          + HalfConversationScenario.class.getSimpleName() + " with role = "
          + roleInConversation + " and first performative = "
          + firstPerformative + " - FAILED");
      throw new ScenarioNotFoundException(roleInConversation, firstPerformative);
    }
    return foundScenario;
  }

  /**
   * Retrieves a the first state of a HalfConversation scenario giving its
   * described role and first performative.
   * @param roleInConversation the role the half scenario to retrieve describes
   * @param firstPerformative the first performative of the half scenario to
   *          retrieve
   * @return the first state of the HalfConversationScenario looked for, if this
   *         scenario exists
   * @throws ScenarioNotFoundException No scenario with these role and first
   *           performative has been found
   */
  public static ConversationState retrieveFirstStateOfScenario(
      RoleInConversation roleInConversation, Performative firstPerformative)
      throws ScenarioNotFoundException {
    // local variables
    ConversationState foundFirstState = null;
    ConversationState scenarioFirstState;
    Performative scenarioFirstPerformative;
    // Loop on all registered scenarios
    for (HalfConversationScenario scenario : HalfConversationScenario.ALL_DEFINED_SCENARIOS) {
      // First, check if the role matches
      if (roleInConversation != scenario.getDescribedRole()) {
        continue;
      }
      // Get the first performative of the currently checked scenario depending
      // on
      // the described role
      scenarioFirstState = scenario.getInitialState();
      if (roleInConversation == RoleInConversation.INITIATOR) {
        scenarioFirstPerformative = scenarioFirstState
            .getFollowingTransitionsSet().iterator().next()
            .getPerformativeToSend();
      } else {
        scenarioFirstPerformative = scenarioFirstState
            .getFollowingTransitionsSet().iterator().next()
            .getReceivedPerformative();
      }
      // If it matches with the firstPerformative we are looking for: found!
      if (firstPerformative == scenarioFirstPerformative) {
        foundFirstState = scenarioFirstState;
        break;
      } // if (firstPerformative == scenarioFirstPerformative)
    } // for (HalfConversationScenario scenario : ALL_DEFINED_SCENARIOS)
    // Case where no matching scenario was found : problem
    if (foundFirstState == null) {
      HalfConversationScenario.logger.error("Attempt to retrieve "
          + HalfConversationScenario.class.getSimpleName() + " with role = "
          + roleInConversation + " and first performative = "
          + firstPerformative + " - FAILED");
      throw new ScenarioNotFoundException(roleInConversation, firstPerformative);
    }
    return foundFirstState;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  /**
   * Constructs a half conversation scenario for the specified role and
   * registers it with all declared half conversation scenarios. The initial
   * state of the scenario is also constructed.
   * @param describedRole the role from the point of view of which the scenario
   *          is described
   * @param nameOfInitialState name to assign to the initial state of this
   *          scenario
   */
  public HalfConversationScenario(RoleInConversation describedRole,
      String nameOfInitialState) {
    // Variables
    ConversationState tempInitialState;
    // Initialises the fields of this half scenario
    this.describedRole = describedRole;
    tempInitialState = ConversationState.InitialConversationStateFactory(this,
        nameOfInitialState);
    this.initialState = tempInitialState;
    // Registers this half scenario in the list of all declared half scenarios
    HalfConversationScenario.ALL_DEFINED_SCENARIOS.add(this);
  }

  // Getters and setters //

  /**
   * Getter for aLL_SCENARIOS_ALIST.
   * @return the aLL_SCENARIOS_ALIST
   */
  public static List<HalfConversationScenario> getALL_SCENARIOS_ALIST() {
    return HalfConversationScenario.ALL_DEFINED_SCENARIOS;
  }

  /**
   * Getter for describedRole.
   * @return the describedRole
   */
  public RoleInConversation getDescribedRole() {
    return this.describedRole;
  }

  /**
   * Setter for describedRole.
   * @param describedRole the describedRole to set
   */
  public void setDescribedRole(RoleInConversation describedRole) {
    this.describedRole = describedRole;
  }

  /**
   * Getter for conversationStates.
   * @return the conversationStates
   */
  public HashSet<ConversationState> getConversationStates() {
    return this.conversationStates;
  }

  /**
   * Setter for conversationStates.
   * @param conversationStates the conversationStates to set
   */
  public void setConversationStates(
      HashSet<ConversationState> conversationStates) {
    this.conversationStates = conversationStates;
  }

  /**
   * Getter for conversationTransitions.
   * @return the conversationTransitions
   */
  public HashSet<ConversationTransition> getConversationTransitions() {
    return this.conversationTransitions;
  }

  /**
   * Setter for conversationTransitions.
   * @param conversationTransitions the conversationTransitions to set
   */
  public void setConversationTransitions(
      HashSet<ConversationTransition> conversationTransitions) {
    this.conversationTransitions = conversationTransitions;
  }

  /**
   * Getter for initialState.
   * @return the initialState
   */
  public ConversationState getInitialState() {
    return this.initialState;
  }

  /**
   * Setter for initialState.
   * @param initialState the initialState to set
   */
  public void setInitialState(ConversationState initialState) {
    this.initialState = initialState;
  }

  // Other public methods //

  /**
   * Concatenates the performative of the first transition with the role from
   * the point of view of which the conversation scenario is described.
   */
  @Override
  public String toString() {
    Set<ConversationTransition> initialTransitions;
    // Get the transitions following the initial state of this half scenario.
    initialTransitions = this.initialState.getFollowingTransitionsSet();
    // Case where no transition yet has been registered
    if (initialTransitions.isEmpty()) {
      return "Scenario under construction";
    }
    // Normally here one and only one transition has been registered: get its
    // string
    // representation and concatenates it with the string representation of the
    // role
    // described by this scenario
    return initialTransitions.iterator().next().toString() + "("
        + this.describedRole.toString() + ")";
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
