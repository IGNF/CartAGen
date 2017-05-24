package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * Thrown when there is an attempt to retrieve a HalfConversationScenario by its
 * described role and the performative of its first transition, and no scenario
 * matching these role+first performative has been declared. In other words, a
 * conversational object tries to begin a conversation with a message that the
 * system cannot match to a predefined scenario.
 * @author CDuchene
 * 
 */
public class ScenarioNotFoundException extends Exception {

  /**
	 * 
	 */
  private static final long serialVersionUID = -2923899352056030603L;

  /**
   * Default constructor made private to force the use of the parameterised
   * constructor.
   */
  public ScenarioNotFoundException() {
    super();
  }

  /**
   * Constructs a ScenarioNotFoundException with a message indicating what
   * (role, performative) pair has led to a failure in retrieving a
   * HalfConversationScenario.
   * @param role role the scenario to retrieve was supposed to represent
   * @param perf first performative of the scenario to retrieve
   */
  public ScenarioNotFoundException(RoleInConversation role, Performative perf) {
    // Calls the super constructor parametrised by a message string.
    super("Scenario not found role = " + role + " first performative = " + perf);
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

}
