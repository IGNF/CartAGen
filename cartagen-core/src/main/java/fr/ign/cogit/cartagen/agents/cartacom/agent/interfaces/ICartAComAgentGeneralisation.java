/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationManageable;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AMessageBox;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgumentBasedMessage;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.SignedMessage;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * @author CDuchene
 * 
 */
public interface ICartAComAgentGeneralisation
    extends ICartacomAgent, ConversationManageable, BehavioralAgent {

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public IGeneObj getFeature();

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFeature(IFeature feature);

  /**
   * Getter for agentAgent.
   * 
   * @return the agentAgent
   */
  public IGeographicAgent getAgentAgent();

  /**
   * Getter for environementZone.
   * 
   * @return the environementZone
   */
  public IGeometry getEnvironementZone();

  /**
   * Setter for environementZone.
   * 
   * @param environementZone the environementZone to set
   */
  public void setEnvironementZone(IGeometry environementZone);

  /**
   * Getter for limitZones.
   * 
   * @return the limitZones
   */
  public List<IPolygon> getLimitZones();

  /**
   * Getter for the ith element of limitZones.
   * @param i the number of the limitZone we want to retrieve
   * @return the ith element of limitZones, or {@code null} if less than i
   *         limitZones
   */
  public IPolygon getLimitZone(int i);

  /**
   * Setter for limitZones.
   * 
   * @param limitZones the limitZones to set
   */
  public void setLimitZones(List<IPolygon> limitZones);

  /**
   * Getter for relationalConstraintsInitialisationDone.
   * @return TRUE if relational constraints initialised, FALSE otherwise
   */
  public boolean isRelationalConstraintsInitialisationDone();

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
      Performative performative, Object argument);

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
      Collection<SignedMessage> receivedMessages);

  // Inherited from CartacomAgent

  /**
   * {@inheritDoc}
   * <p>
   * Intentionnally left empty at CartAComAgenGeneralisation level (stub).
   * Should be defined on subtypes that need it.
   */
  @Override
  public void initialiseEnvironmentRepresentation();

  /**
   * {@inheritDoc}
   * <p>
   * Intentionnally left empty at CartAComAgenGeneralisation level (stub).
   * Should be defined on subtypes that need it.
   */
  @Override
  public void updateEnvironmentRepresentation();

  /**
   * {@inheritDoc}
   * <p>
   * First retrieves the CartACom agents included in the environment zone, then
   * identifies the relational constraints to instanciate with each of them.
   */
  @Override
  public void initialiseRelationalConstraints();

  // Inherited from AgentImpl

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public boolean isDeleted();

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setDeleted(boolean deleted);

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setMesoAgent(MesoAgent<? extends GeographicObjectAgent> meso);

  @Override
  public void setStructureAgents(List<InternStructureAgent> structureAgents);

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void clean();

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
      boolean environmentChanged);

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
      boolean otherChanged);

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public void goBackToState(AgentState state);

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public void goBackToInitialState();

  /**
   * {@inheritDoc}
   * <p>
   * WARNING does not warn the agents sharing a constraint of this elimination.
   * A ajouter ici ou seulement dans l'action qui applique cette méthode??
   */
  @Override
  public void deleteAndRegister();

  /**
   * {@inheritDoc}
   * <p>
   */
  @Override
  public void displaceAndRegister(double dx, double dy);

  @Override
  public int getID();

  @Override
  public Set<BehavioralAgent> getNeighbors();

  @Override
  public void addNeighbor(BehavioralAgent neighbor);

  @Override
  public Behavior getBehavior();

  @Override
  public void setBehavior(Behavior behavior);

  @Override
  public void addMessageToAgentsBox(AMessage message);

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void addConversation(AConversation conversation);

  @Override
  public AMessageBox getMessageBox();

  /**
   * {@inheritDoc}
   * <p>
   */
  @Override
  public void manageHavingJustBeenModifiedByATask(
      boolean changeCentroidRelatedConstrainedZone);
}
