package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.RelationalConstraintDescriptor;
import fr.ign.cogit.cartagen.agents.cartacom.agent.CartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.Behavior;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.BehavioralAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessageBox;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgumentBasedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.InformAdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.InformationContent;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentState;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentStateImpl;
import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentStateImpl;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public abstract class CartAComAgentGeneralisation extends CartacomAgent
    implements ICartAComAgentGeneralisation {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(ICartAComAgentGeneralisation.class.getName());

  /**
   * Population of the geographic objects corresponding to all the Cartatacom
   * agents defined in the system (that can be retrieved by
   * {@link CartacomAgent#getAll()}
   */
  private static IPopulation<IGeneObj> ALL_CARTACOM_GENE_OBJS = new Population<IGeneObj>();

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The AGENT agent managing the same geographic object.
   */
  private GeographicObjectAgentGeneralisation agentAgent = null;
  /**
   * The environment zone of the agent: agents with which to instanciate
   * constraints will be searched for in this zone.
   */
  private IGeometry environementZone = null;
  /**
   * List of nested polygons which are buffers of increasing offsets around the
   * initial geometry of the agent. The last one represents the zone in which
   * the agent is allowed to move during its generalisation. The other ones are
   * smaller. (offset of ith zone = (i/nb_zones)*(offset of last zone))
   */
  private List<IPolygon> limitZones = new ArrayList<IPolygon>();

  private boolean relationalConstraintsInitialisationDone = false;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a cartacom agent to handle the generalisation of a feature
   * 
   * @param feature the geographic object handled by this agent
   */
  public CartAComAgentGeneralisation(IGeneObj feature) {
    // Apply constructore of a generic CartACom agent
    super(feature);
    // Add this to the population of managed CartACom agents for generalisation
    CartAComAgentGeneralisation.getAllCartAComGeneObjs().add(this.getFeature());

    // Specific to activation by the strategy developed in Gokhan Altay's
    // internship - therefore ignored for a standard CartACom activation
    // TODO Tidy up, split class into one for standard CartACom activation
    // and one for Gokhan activation
    // <<<<<added by gkhn<<<<<<<<<<<<<<<<<<
    // SchedulerModel.getAllBehavioralAgents().add(this);
    // this.messageBox = new AMessageBoxImpl(this);
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // Initialise Environment zone
    double environmentDist = CartacomSpecifications.ENVIRONMENT_ZONE_OFFSET
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    IGeometry envZone = this.getGeom().buffer(environmentDist);
    if (envZone instanceof IPolygon)
      this.setEnvironementZone((IPolygon) envZone);
    else
      this.setEnvironementZone(
          (IPolygon) this.getGeom().buffer(environmentDist - 1.0));
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_CARTACOM_GENE_OBJS.
   * 
   * @return the ALL_CARTACOM_GENE_OBJS
   */
  public static IPopulation<IGeneObj> getAllCartAComGeneObjs() {
    return CartAComAgentGeneralisation.ALL_CARTACOM_GENE_OBJS;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public IGeneObj getFeature() {
    return (IGeneObj) super.getFeature();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFeature(IFeature feature) {
    if (feature instanceof IGeneObj) {
      super.setFeature(feature);
      if (this.getFeature() != null) {
        this.getFeature().addToGeneArtifacts(this);
      }
    } else {
      CartAComAgentGeneralisation.logger.error(
          "Attempt to set a feature that is not a IGeneObj to a CartacomAgentGeneralisation");
    }
  }

  /**
   * Getter for agentAgent.
   * 
   * @return the agentAgent
   */
  public IGeographicAgent getAgentAgent() {
    return this.agentAgent;
  }

  /**
   * Setter for agentAgent.
   * 
   * @param agentAgent the agentAgent to set
   */
  protected void setAgentAgent(GeographicObjectAgentGeneralisation agentAgent) {
    this.agentAgent = agentAgent;
  }

  /**
   * Getter for environementZone.
   * 
   * @return the environementZone
   */
  public IGeometry getEnvironementZone() {
    return this.environementZone;
  }

  /**
   * Setter for environementZone.
   * 
   * @param environementZone the environementZone to set
   */
  public void setEnvironementZone(IGeometry environementZone) {
    this.environementZone = environementZone;
  }

  /**
   * Getter for limitZones.
   * 
   * @return the limitZones
   */
  public List<IPolygon> getLimitZones() {
    return this.limitZones;
  }

  /**
   * Getter for the ith element of limitZones.
   * @param i the number of the limitZone we want to retrieve
   * @return the ith element of limitZones, or {@code null} if less than i
   *         limitZones
   */
  public IPolygon getLimitZone(int i) {
    if (this.limitZones.size() < i) {
      return null;
    }
    return this.limitZones.get(i);
  }

  /**
   * Setter for limitZones.
   * 
   * @param limitZones the limitZones to set
   */
  public void setLimitZones(List<IPolygon> limitZones) {
    this.limitZones = limitZones;
  }

  /**
   * Getter for relationalConstraintsInitialisationDone.
   * @return TRUE if relational constraints initialised, FALSE otherwise
   */
  public boolean isRelationalConstraintsInitialisationDone() {
    return this.relationalConstraintsInitialisationDone;
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  // Inherited from ConversatioManageable

  /**
   * Composes a message of class AdHocArgumentBasedMessage, because the class
   * AdHocArgument corresponds to the content language spoken by
   * CartAComAgentGeneralisation agents.
   * <p>
   * Compose un message de la classe AdHocArgumentBasedMessage, puisque la
   * classe AdHocArgument correspond au langage parlé par les agents CartACom
   * pour la generalisation.
   * 
   * @param conversationId The identifier of the conversation this message
   *          belongs to
   * @param performative The {@link Performative performative} of the message
   * @param argument The argument of the message. Its actual type should be a
   *          subtype of <code>AdHocArgument</code>, otherwise the method fails.
   * @return a message of the class <code>AdHocArgumentBasedMessage</code>.
   * @throws ClassCastException , if the argument is not of type AdHocArgument
   */
  @Override
  public AdHocArgumentBasedMessage composeMessage(long conversationId,
      Performative performative, Object argument) {
    try {
      AdHocArgument adHocArg = (AdHocArgument) argument;
      return new AdHocArgumentBasedMessage(conversationId, performative,
          adHocArg);
    } catch (ClassCastException e) {
      CartAComAgentGeneralisation.logger
          .error("Attempt to compose a message with wrong argument type in "
              + this.getClass().getName());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Here places the messages informing from another agent having eliminated
   * itself at the beginning of the list. The other messages are left in an
   * unchanged order. TODO Faut-il laisser ça ici ou le mettre sur
   * CartAComAgentImpl?
   */
  @Override
  public List<SignedMessage> orderReceivedMessagesByEmergency(
      Collection<SignedMessage> receivedMessages) {
    // Create list to return
    List<SignedMessage> orderedMessages = new ArrayList<SignedMessage>();
    // Loop on the received messages
    for (SignedMessage signedMessage : receivedMessages) {
      // If the performative is not INFORM, just add the message at the
      // end of the list
      if (signedMessage.getMessage().getPerformative() != Performative.INFORM) {
        orderedMessages.add(signedMessage);
      } else {
        InformAdHocArgument argument = (InformAdHocArgument) signedMessage
            .getMessage().getArgument();
        // If yes adds the message to the beginning of the list
        if (argument.getInformation() == InformationContent.ELIMINATED) {
          orderedMessages.add(0, signedMessage);
          // Otherwise adds it at the end
        } else {
          orderedMessages.add(signedMessage);
        }
      }
    }
    return orderedMessages;
  }

  // Inherited from CartacomAgent

  /**
   * {@inheritDoc}
   * <p>
   * Intentionnally left empty at CartAComAgenGeneralisation level (stub).
   * Should be defined on subtypes that need it.
   */
  @Override
  public void initialiseEnvironmentRepresentation() {
    // Intentionnally left empty (stub)
  }

  /**
   * {@inheritDoc}
   * <p>
   * Intentionnally left empty at CartAComAgenGeneralisation level (stub).
   * Should be defined on subtypes that need it.
   */
  @Override
  public void updateEnvironmentRepresentation() {
    // Intentionnally left empty (stub)
  }

  /**
   * {@inheritDoc}
   * <p>
   * First retrieves the CartACom agents included in the environment zone, then
   * identifies the relational constraints to instanciate with each of them.
   */
  @Override
  public void initialiseRelationalConstraints() {

    // Do nothing if the relational constraints intialisation has already been
    // done
    if (this.relationalConstraintsInitialisationDone) {
      return;
    }

    // The initialisation has not been done yet - do it

    CartAComAgentGeneralisation.logger.info(
        "Initialising relational constraints of agent " + this.toString());

    // Declaration of two arrays containing respectively the types and values of
    // the two arguments of the "checkRelationRelevance" static method that
    // should be defined on each class extending MicroMicroRelation.
    // These arrays are respectively used to retrieve and invoke this
    // method by reflexivity on the right relation classes for the agent and
    // each of its neighbours
    // Array containing the argument types
    Class<?>[] checkRelevanceMethodArgTypes = new Class[] {
        ICartAComAgentGeneralisation.class,
        ICartAComAgentGeneralisation.class };
    // Array containing the actual arguments
    Object[] checkRelevanceMethodArgs = null;

    // Declaration of two arrays containing respectively the types and values of
    // the two arguments of the constructor that should be defined on each class
    // extending MicroMicroRelation.
    // These arrays are respectively used to retrieve and invoke the constructor
    // by reflexivity on the right relation classes for the agent and
    // each of its neighbours
    // Array containing the argument types
    Class<?>[] constructorMethodArgTypes = new Class[] {
        ICartAComAgentGeneralisation.class, ICartAComAgentGeneralisation.class,
        double.class };
    // Array containing the actual arguments
    Object[] constructorMethodArgs = null;

    // Retrieve classes of contraints to consider from the specifications
    Set<RelationalConstraintDescriptor> constraintsToConsider = CartacomSpecifications
        .getInstance().getConstraintsToConsider();
    // Retrieve all the neighbors as IGeneObjs (Get the IGeneObjs in the
    // EnvironementZone area), then retrieve the corresponding CartACom agents
    Collection<IGeneObj> geneNeighbours;
    geneNeighbours = CartAComAgentGeneralisation.getAllCartAComGeneObjs()
        .select(this.getEnvironementZone());
    Set<ICartAComAgentGeneralisation> cacNeighbours;
    cacNeighbours = AgentUtil.getCartAComAgentSetFromGeneObjs(geneNeighbours);
    // Loop on the CartACom neighbours to try and instantiate the relevant
    // relational constraints with each of them
    try {
      for (ICartAComAgentGeneralisation cacNeighbour : cacNeighbours) {
        // For current neighbour, loop on possible relational constraints
        // (described in constraint descriptors)
        for (RelationalConstraintDescriptor constrDescriptor : constraintsToConsider) {
          // Does the constraint descriptor match the types of the agent and
          // its neighbour?
          if (constrDescriptor.holds(this.getClass(),
              cacNeighbour.getClass())) {
            // logger.debug("Relation describes "
            // + constrDescriptor.getRelationClass());
            // Instantiate the argument values for checkRelationRelevance method
            checkRelevanceMethodArgs = new Object[] { this, cacNeighbour };
            // Invoke the method to check the relevance of the constrained
            // relation between the two agents
            Object isRelevant = constrDescriptor.getRelationClass()
                .getMethod("checkRelationRelevance",
                    checkRelevanceMethodArgTypes)
                .invoke(null, checkRelevanceMethodArgs);
            // If the result is TRUE, invoke the construction of the relation
            // with the two agents as arguments
            if (isRelevant.equals(Boolean.TRUE)) {
              // Instantiate the argument values for the constructor
              constructorMethodArgs = new Object[] { this, cacNeighbour,
                  new Double(constrDescriptor.getImportance()) };
              // Retrieve and invoke the constructor
              CartAComAgentGeneralisation.logger
                  .info("Found relevant constrained relation of class "
                      + constrDescriptor.getRelationClass().getName()
                      + " with agent " + cacNeighbour.toString()
                      + " - instantiating it...");
              constrDescriptor.getRelationClass()
                  .getConstructor(constructorMethodArgTypes)
                  .newInstance(constructorMethodArgs);
              CartAComAgentGeneralisation.logger.info("...done");
            }
          }
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    // While initialising, once all the constraints of an agent have been
    // initialised, update all constraints to fill their zone variables
    this.updateConstraints(true, true);
    // Create a root state
    // TODO N'a rien à faire là!!! Mais il est nécessaire que l'intialisation
    // des contraintes soit faite AVANT la création du current state/root
    // state
    if (this instanceof ISmallCompactAgent) {
      ((ISmallCompactAgent) this).buildCurrentState(null, null);
      this.setRootState(new SmallCompactAgentStateImpl(
          (ISmallCompactAgent) this, null, null));
    } else {
      this.buildCurrentState(null, null);
      this.setRootState(new CartacomAgentStateImpl(this, null, null));
    }

    // this.setRootState(new CartacomAgentState(this, null, null));

    // Ensure the relational constraints initialisation will not be done twice
    this.relationalConstraintsInitialisationDone = true;

  }

  // Inherited from AgentImpl

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public boolean isDeleted() {
    return this.getFeature().isDeleted();
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setDeleted(boolean deleted) {
    this.getFeature().setDeleted(deleted);
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setMesoAgent(MesoAgent<? extends GeographicObjectAgent> meso) {
    // Nothing to do

  }

  @Override
  public void setStructureAgents(List<InternStructureAgent> structureAgents) {
    // Nothing to do

  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void clean() {
    super.clean();
    this.setEnvironementZone(null);
    if (this.getLimitZones() != null) {
      this.getLimitZones().clear();
    }
  }

  /**
   * Updates the constraints of this agent due to a modification of the shape of
   * this agent or a modification of its environment
   * 
   * @param myShapeChanged {@code true} if the shape of this agent has been
   *          modified (including rotations but not translations), {@code false}
   *          otherwise
   * @param environmentChanged {@code true} if the environment of this agent has
   *          been modified, {@code false} otherwise
   */
  public void updateConstraints(boolean myShapeChanged,
      boolean environmentChanged) {
    Set<Constraint> allCons = this.getConstraints();
    for (Constraint constraint : allCons) {
      if (!(constraint instanceof MicroMicroRelationalConstraint)) {
        continue;
      }
      MicroMicroRelationalConstraint mmc = (MicroMicroRelationalConstraint) constraint;
      mmc.update(myShapeChanged, environmentChanged);
    }
  }

  /**
   * Updates the constraints shared by this agent with the specified agent due
   * to a modification of the shape of this agent or a modification of the other
   * agent
   * 
   * @param otherAgent the other agent with which we want to update the shared
   *          constraints
   * @param myShapeChanged {@code true} if the shape of this agent has been
   *          modified (including rotations but not translations), {@code false}
   *          otherwise
   * @param otherChanged {@code true} if the other agent has been modified,
   *          {@code false} otherwise
   */
  public void updateConstraintsWithAgent(
      ICartAComAgentGeneralisation otherAgent, boolean myShapeChanged,
      boolean otherChanged) {
    Set<Constraint> allCons = this.getConstraints();
    for (Constraint constraint : allCons) {
      if (!(constraint instanceof MicroMicroRelationalConstraint)) {
        continue;
      }
      MicroMicroRelationalConstraint mmc = (MicroMicroRelationalConstraint) constraint;
      if (!mmc.getAgentSharingConstraint().equals(otherAgent)) {
        continue;
      }
      mmc.update(myShapeChanged, otherChanged);
    }
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public void goBackToState(AgentState state) {
    super.goBackToState(state);
    CartacomAgentState cartacomState = (CartacomAgentState) state;
    this.getFeature().setGeom(cartacomState.getGeometry());
    this.getFeature().setDeleted(cartacomState.isDeleted());

    this.updateConstraints(true, true);
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public void goBackToInitialState() {
    this.getFeature().setDeleted(false);
    this.getFeature().setGeom(this.getInitialGeom());
    if (this instanceof MesoAgent<?>) {
      MesoAgent<?> meso = (MesoAgent<?>) this;
      for (GeographicObjectAgent ag : meso.getComponents()) {
        ag.goBackToInitialState();
      }
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * WARNING does not warn the agents sharing a constraint of this elimination.
   * A ajouter ici ou seulement dans l'action qui applique cette méthode??
   */
  @Override
  public void deleteAndRegister() {
    // delete the associated feature
    this.getFeature().eliminate();
    // clean this agent
    this.clean();
    // TODO Retirer du scheduler
  }

  /**
   * {@inheritDoc}
   * <p>
   */
  @Override
  public void displaceAndRegister(double dx, double dy) {
    // displace the geometry of the related feature
    this.getFeature().setGeom(
        CommonAlgorithms.translation(this.getFeature().getGeom(), dx, dy));
  }

  // Inherited from CartacomAgentImpl

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return super.hashCode();
  }

  @Override
  public double getSatisfaction() {
    double totalSatisfactions = 0;
    double totalImportances = 0;

    if (logger.isDebugEnabled())
      logger.debug("agent " + this.toString() + " satisfaction: ");
    for (Constraint c : this.getConstraints()) {
      totalImportances += c.getImportance();
      totalSatisfactions += ((GeographicConstraint) c).getSatisfaction()
          * c.getImportance();
      if (logger.isDebugEnabled()) {
        logger.debug("Constraint " + c);
        logger.debug("importance: " + c.getImportance());
        logger.debug(
            "satisfaction: " + ((GeographicConstraint) c).getSatisfaction());
      }
    }

    if (totalImportances == 0)
      return 5.0;

    double satisfaction = totalSatisfactions / totalImportances;
    if (logger.isDebugEnabled())
      logger.debug("agent satisfaction: " + satisfaction);
    return satisfaction;
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

  // /////////////////////////////////////////
  // /// *********gkhn*********** ////////////
  // /////////////////////////////////////////

  // Methods and fields for agent's neighbors
  private Set<BehavioralAgent> neighbors = new HashSet<BehavioralAgent>();

  @Override
  public int getID() {
    return this.getFeature().getId();
  }

  @Override
  public Set<BehavioralAgent> getNeighbors() {
    return this.neighbors;
  }

  @Override
  public void addNeighbor(BehavioralAgent neighbor) {
    if (!this.getNeighbors().contains(neighbor)) {
      this.getNeighbors().add(neighbor);
    }
  }

  // Methods and fields for agent's behaviors
  private Behavior behavior = null;

  @Override
  public Behavior getBehavior() {
    return this.behavior;
  }

  @Override
  public void setBehavior(Behavior behavior) {
    Behavior oldBehavior = this.behavior;
    this.behavior = behavior;
    if (oldBehavior != null) {
      oldBehavior.setBelongingAgent(null);
    }
    if (behavior != null) {
      if (behavior.getBelongingAgent() != this) {
        behavior.setBelongingAgent(this);
      }
    }
  }

  // Methods and fields for agent's communication
  private AMessageBox messageBox;

  @Override
  public void addMessageToAgentsBox(AMessage message) {
    this.messageBox.addNewMessage(message);

  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void addConversation(AConversation conversation) {
    this.messageBox.addConversation(conversation);

  }

  @Override
  public AMessageBox getMessageBox() {
    return this.messageBox;
  }

  /**
   * {@inheritDoc}
   * <p>
   */
  @Override
  public void manageHavingJustBeenModifiedByATask(
      boolean changeCentroidRelatedConstrainedZone) {
    super.manageHavingJustBeenModifiedByATask(
        changeCentroidRelatedConstrainedZone);
    this.updateConstraints(changeCentroidRelatedConstrainedZone, false);
  }

  @Override
  public void clearConversations() {
    this.getConversationManager().clearConversations();
  }

}
