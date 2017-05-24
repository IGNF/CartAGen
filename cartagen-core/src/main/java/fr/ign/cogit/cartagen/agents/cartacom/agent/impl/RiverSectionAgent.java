/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IRiverSectionAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;

/**
 * @author GAltay
 * 
 */
public class RiverSectionAgent extends NetworkSectionAgent
    implements IRiverSectionAgent {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Set holding all the RiverSectionAgents instanciated in the system
   */
  private static Set<RiverSectionAgent> ALL_RIVER_SECTION_AGENTS = new HashSet<RiverSectionAgent>();

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a river section cartacom agent to handle the generalisation of a
   * waterline geographic object.
   * @param river The waterline object handled by this cartacom river section
   *          agent
   */
  public RiverSectionAgent(IWaterLine river) {
    super(river);
    this.setAgentAgent((GeographicObjectAgentGeneralisation) AgentUtil
        .getAgentFromGeneObj(river));
    RiverSectionAgent.getAllRivers().add(this);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_RIVER_SECTION_AGENTS.
   * @return of all river section agents defined
   */
  public static Set<RiverSectionAgent> getAllRivers() {
    return RiverSectionAgent.ALL_RIVER_SECTION_AGENTS;
  }

  /**
   * Gets a set of all RiverSectionAgents, as CartAComAgents.
   * @return of all river section agents defined
   */
  public static Set<ICartacomAgent> getAll() {
    return new HashSet<ICartacomAgent>(
        RiverSectionAgent.ALL_RIVER_SECTION_AGENTS);
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
  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

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

}
