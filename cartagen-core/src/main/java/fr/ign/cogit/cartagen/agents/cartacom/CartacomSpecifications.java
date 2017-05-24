package fr.ign.cogit.cartagen.agents.cartacom;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationTransition;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.HalfConversationScenario;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.RoleInConversation;
import fr.ign.cogit.cartagen.agents.core.task.AcknowledgeRequestForActionResultTask;
import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;
import fr.ign.cogit.cartagen.agents.diogen.tasks.TryInteractionTask;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;

/**
 * Stereotype singleton
 * 
 * @author GAltay
 * @author CDuchene
 * 
 */
public class CartacomSpecifications {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /** Holds the unique instance of the class */
  private static CartacomSpecifications CARTACOM_SPECIFICATIONS;
  private static CartacomSpecifications CARTACOM_SPECIFICATIONS_FOR_PADAWAN;

  // Parameters
  public static final int CONSTRAINT_PRIORITY_5 = 5;
  public static final int CONSTRAINT_PRIORITY_4 = 4;
  public static final int CONSTRAINT_PRIORITY_3 = 3;
  public static final int CONSTRAINT_PRIORITY_2 = 2;
  public static final int CONSTRAINT_PRIORITY_1 = 1;

  public static final double TOPOLOGY_RELATION_GOAL_VALUE = 1.0;
  public static final double PARALLELISM_RELATION_GOAL_VALUE = 0.0;
  public static final double PARALLELISM_RELATION_FLEXIBILITY = 2.0;
  public static final double PARALLELISM_RELATION_GOAL0 = 15.0;

  public static final double SATISFACTION_1 = 1.0;
  public static final double SATISFACTION_2 = 2.0;
  public static final double SATISFACTION_3 = 3.0;
  public static final double SATISFACTION_4 = 4.0;
  public static final double SATISFACTION_5 = 5.0;

  // XML specificaitons files

  /**
   * the file containing all constraints descriptors
   */
  public static final String ALL_CAC_CONSTRAINTS_DESCRIPTORS_XML_FILE = "/xml/cartacom/CartAComSpecifications.xml";
  /**
   * the file defining the constraints to consider
   */
  public static final String CAC_CONSTRAINTS_TO_CONSIDER_XML_FILE = "/xml/cartacom/AllCartAComRelationalConstraintsDescriptors.xml";

  /**
   * Offset of the buffer applied to the geometry of a CartACom agent in order
   * to build its environment zone -
   * {@link ICartAComAgentGeneralisation#getEnvironementZone}
   */
  public static final double ENVIRONMENT_ZONE_OFFSET = 2
      * GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
      + GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT;

  /**
   * Number of limit zones
   */
  public static final int NB_LIMIT_ZONES = 3;
  /**
   * Distance under which a small compact is considered 'close' enough to a
   * network section so that there is probably no other small compact between it
   * and the network section. This distance is in terrain units. It is
   * considered from the small compact geometry to the geometry of the network
   * section (=> its axis, if it is linear).
   */
  public final static double DIST_SMALLCOMPACT_CLOSE_TO_NETWORK_SECTION = 15.0;

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * A set decribing the relational constraints to consider
   */
  private Set<RelationalConstraintDescriptor> constraintsToConsider = new HashSet<RelationalConstraintDescriptor>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Default constructor set private to force the singleton
   * 
   * 
   */
  private CartacomSpecifications() {

  }

  private void defineSpecificationForCartACom() {
    // Builds the conversaions scenario to consider
    // TODO move this somewhere else in the future...
    this.defineConversationScenarios();

    // List of the constraints to consider
    // TODO enable xml declaration in the future (or connect to Guillaume
    // Touya's constraints declaration environement
    this.defineConstraintsToConsider();
  }

  private void defineSpecificationForPadawan() {
    // Builds the conversaions scenario to consider
    // TODO move this somewhere else in the future...
    this.defineConversationScenariosForPadawan();

    // List of the constraints to consider
    // TODO enable xml declaration in the future (or connect to Guillaume
    // Touya's constraints declaration environement
    this.defineConstraintsToConsider();
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for the unique instance of the class
   * @return the unique instance of this class
   */
  public static CartacomSpecifications getInstance() {

    CARTACOM_SPECIFICATIONS = new CartacomSpecifications();
    CARTACOM_SPECIFICATIONS.defineSpecificationForCartACom();
    return CartacomSpecifications.CARTACOM_SPECIFICATIONS;
  }

  public static CartacomSpecifications getInstanceForPadawan() {

    CARTACOM_SPECIFICATIONS_FOR_PADAWAN = new CartacomSpecifications();
    CARTACOM_SPECIFICATIONS_FOR_PADAWAN.defineSpecificationForPadawan();

    return CartacomSpecifications.CARTACOM_SPECIFICATIONS_FOR_PADAWAN;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for constraintsToConsider.
   * 
   * @return the constraintsToConsider
   */
  public Set<RelationalConstraintDescriptor> getConstraintsToConsider() {
    return this.constraintsToConsider;
  }

  // /////////////////////////////////////////////
  // Other public methods //
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

  /**
   * Instantiates the field {@code #constraintsToConsider} TODO Replace with xml
   * declaration + generic instantiation from xml parsing
   */
  private void defineConstraintsToConsider() {

    CartAComInitialisations.loadConstraintsDescrFromXMLs(this);

    // Proximity constraint btw two small compacts
    // this.getConstraintsToConsider()
    // .add(
    // new RelationalConstraintDescriptor(
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.SmallCompactAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.SmallCompactAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.relation.buildingBuilding.Proximity.class,
    // 8.0));
    // // Topology (inclusion) constraint btw small compact and network face
    // this.getConstraintsToConsider()
    // .add(
    // new RelationalConstraintDescriptor(
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.SmallCompactAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.NetworkFaceAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.relation.buildingnetface.Topology.class,
    // 10.0));
    // // Proximity constraint btw small compact and network section
    // this.getConstraintsToConsider()
    // .add(
    // new RelationalConstraintDescriptor(
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.SmallCompactAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.agent.NetworkSectionAgent.class,
    // fr.ign.cogit.cartagen.agentGeneralisation.cartacom.relation.buildingRoad.Proximity.class,
    // 9.0));
  }

  /**
   * Instantiates all needed conversation scenarios in the system Here:
   * RequestForAction and Information
   */
  private void defineConversationScenarios() {
    this.defineRequestForActionScenarioInitiator();
    this.defineRequestForActionScenarioRespondent();
    this.defineInformationScenarioInitiator();
    this.defineInformationScenarioRespondent();
  }

  /**
   * Defines the half conversation scenarios describing a request for action
   * conversation from the point of view of the initiator
   */
  private void defineRequestForActionScenarioInitiator() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.INITIATOR, "InitialRequestForActionInitiator");
    ConversationState waitingActionState = ConversationState
        .newWaitingConversationState(scenario, "WaitingForAction");
    ConversationTransition.newSendingConversationTransition(scenario,
        scenario.getInitialState(), waitingActionState, null,
        Performative.ASK_TO_DO);
    ConversationState finalSuccessState = ConversationState
        .newFinalConversationState(scenario, "FinalActionSucceededInitiator",
            AcknowledgeRequestForActionResultTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        waitingActionState, finalSuccessState, Performative.FINISHED_TO_DO);
    ConversationState finalFailureState = ConversationState
        .newFinalConversationState(scenario, "FinalActionFailedInitiator",
            AcknowledgeRequestForActionResultTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        waitingActionState, finalFailureState, Performative.REFUSE_TO_DO);
  }

  /**
   * Defines the half conversation scenarios describing a request for action
   * conversation from the point of view of the respondent
   */
  private void defineRequestForActionScenarioRespondent() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.RESPONDENT, "InitialRequestForActionRespondent");
    ConversationState tryingActionState = ConversationState
        .newProcessingConversationState(scenario, "TryingAction",
            TryActionTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        scenario.getInitialState(), tryingActionState, Performative.ASK_TO_DO);
    ConversationState finalSuccessState = ConversationState
        .newFinalConversationState(scenario, "FinalActionSucceededRespondent",
            null);
    ConversationTransition.newSendingConversationTransition(scenario,
        tryingActionState, finalSuccessState, TryActionTask.SUCCEEDED,
        Performative.FINISHED_TO_DO);
    ConversationState finalFailureState = ConversationState
        .newFinalConversationState(scenario, "FinalActionFailedRespondent",
            null);
    ConversationTransition.newSendingConversationTransition(scenario,
        tryingActionState, finalFailureState, TryActionTask.FAILED,
        Performative.REFUSE_TO_DO);
  }

  /**
   * Defines the half conversation scenarios describing an information
   * conversation from the point of view of the initiator
   */
  private void defineInformationScenarioInitiator() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.INITIATOR, "InitialInfoInitiator");
    ConversationState finalState = ConversationState.newFinalConversationState(
        scenario, "FinalInfoInitiator",
        AcknowledgeRequestForActionResultTask.class);
    ConversationTransition.newSendingConversationTransition(scenario,
        scenario.getInitialState(), finalState, null, Performative.INFORM);
  }

  /**
   * Defines the half conversation scenarios describing an information
   * conversation from the point of view of the respondent
   */
  private void defineInformationScenarioRespondent() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.RESPONDENT, "InitialInfoRespondent");
    ConversationState finalState = ConversationState
        .newFinalConversationState(scenario, "FinalInfoRespondent", null);
    ConversationTransition.newReceptionConversationTransition(scenario,
        scenario.getInitialState(), finalState, Performative.INFORM);
  }

  // Scenario for Padawan

  /**
   * Instantiates all needed conversation scenarios in the system Here:
   * RequestForAction and Information
   */
  private void defineConversationScenariosForPadawan() {
    this.defineRequestForInteractionScenarioInitiator();
    this.defineRequestForInteractionScenarioRespondent();
    this.defineInformationScenarioInitiator();
    this.defineInformationScenarioRespondent();
  }

  /**
   * Defines the half conversation scenarios describing a request for padawan
   * interaction conversation from the point of view of the initiator
   */
  private void defineRequestForInteractionScenarioInitiator() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.INITIATOR, "InitialRequestForActionInitiator");
    ConversationState waitingActionState = ConversationState
        .newWaitingConversationState(scenario, "WaitingForAction");
    ConversationTransition.newSendingConversationTransition(scenario,
        scenario.getInitialState(), waitingActionState, null,
        Performative.ASK_TO_DO);
    ConversationState finalSuccessState = ConversationState
        .newFinalConversationState(scenario, "FinalActionSucceededInitiator",
            AcknowledgeRequestForActionResultTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        waitingActionState, finalSuccessState, Performative.FINISHED_TO_DO);
    ConversationState finalFailureState = ConversationState
        .newFinalConversationState(scenario, "FinalActionFailedInitiator",
            AcknowledgeRequestForActionResultTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        waitingActionState, finalFailureState, Performative.REFUSE_TO_DO);
  }

  /**
   * Defines the half conversation scenarios describing a request for padawan
   * interaction conversation from the point of view of the respondent
   */
  private void defineRequestForInteractionScenarioRespondent() {
    HalfConversationScenario scenario = new HalfConversationScenario(
        RoleInConversation.RESPONDENT, "InitialRequestForActionRespondent");
    ConversationState tryingActionState = ConversationState
        .newProcessingConversationState(scenario, "TryingInteraction",
            TryInteractionTask.class);
    ConversationTransition.newReceptionConversationTransition(scenario,
        scenario.getInitialState(), tryingActionState, Performative.ASK_TO_DO);
    ConversationState finalSuccessState = ConversationState
        .newFinalConversationState(scenario, "FinalActionSucceededRespondent",
            null);
    ConversationTransition.newSendingConversationTransition(scenario,
        tryingActionState, finalSuccessState, TryActionTask.SUCCEEDED,
        Performative.FINISHED_TO_DO);
    ConversationState finalFailureState = ConversationState
        .newFinalConversationState(scenario, "FinalActionFailedRespondent",
            null);
    ConversationTransition.newSendingConversationTransition(scenario,
        tryingActionState, finalFailureState, TryActionTask.FAILED,
        Performative.REFUSE_TO_DO);
  }

}
