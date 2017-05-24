/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author CDuchene
 * 
 */
public class RoadSectionAgent extends NetworkSectionAgent
    implements IRoadSectionAgent {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Set holding all the RoadSectionAgents instanciated in the system
   */
  private static Set<RoadSectionAgent> ALL_ROAD_SECTION_AGENTS = new HashSet<RoadSectionAgent>();

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////
  /**
   * Constructs a road section cartacom agent to handle the generalisation of a
   * road geographic object.
   * @param road The road section object handled by this cartacom road section
   *          agent
   */
  public RoadSectionAgent(IRoadLine road) {
    super(road);
    this.setAgentAgent((GeographicObjectAgentGeneralisation) AgentUtil
        .getAgentFromGeneObj(road));
    RoadSectionAgent.getAllRoads().add(this);
  }

  /**
   * Constructor from the network agent the road belongs to and the road gene
   * obj it will encapsulate. Typically to create a road section agent from a
   * road gene obj.
   * @param netwAg
   * @param obj
   */
  public RoadSectionAgent(NetworkAgent netwAg, IRoadLine obj) {
    super(obj);
    this.setInitialGeom((ILineString) this.getGeom().clone());
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_ROAD_SECTION_AGENTS.
   * @return of all road section agents defined
   */
  public static Set<RoadSectionAgent> getAllRoads() {
    return RoadSectionAgent.ALL_ROAD_SECTION_AGENTS;
  }

  /**
   * Gets a set of all RoadSectionAgents, as CartAComAgents.
   * @return of all road section agents defined
   */
  public static Set<ICartacomAgent> getAll() {
    return new HashSet<ICartacomAgent>(
        RoadSectionAgent.ALL_ROAD_SECTION_AGENTS);
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setId(int id) {
    super.setId(id);
    this.getFeature().setId(id);
  }

  public fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent getAgentAgent() {
    return (fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent) super.getAgentAgent();
  }
  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

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
