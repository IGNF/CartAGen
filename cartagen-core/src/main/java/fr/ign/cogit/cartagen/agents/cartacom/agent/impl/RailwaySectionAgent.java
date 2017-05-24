/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IRailwaySectionAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;

/**
 * @author CDuchene
 * 
 */
public class RailwaySectionAgent extends NetworkSectionAgent
    implements IRailwaySectionAgent {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Set holding all the RailwaySectionAgents instanciated in the system
   */
  private static Set<RailwaySectionAgent> ALL_RAILWAY_SECTION_AGENTS = new HashSet<RailwaySectionAgent>();

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////
  /**
   * Constructs a rrailway section cartacom agent to handle the generalisation
   * of a railway line geographic object.
   * @param railway The railway section object handled by this cartacom railway
   *          section agent
   */
  public RailwaySectionAgent(IRailwayLine rail) {
    super(rail);
    this.setAgentAgent((GeographicObjectAgentGeneralisation) AgentUtil
        .getAgentFromGeneObj(rail));
    RailwaySectionAgent.getAllRailwayLines().add(this);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_RAILWAY_SECTION_AGENTS.
   * @return of all railway section agents defined
   */
  public static Set<RailwaySectionAgent> getAllRailwayLines() {
    return RailwaySectionAgent.ALL_RAILWAY_SECTION_AGENTS;
  }

  /**
   * Gets a set of all RailwaySectionAgents, as CartAComAgents.
   * @return of all railway section agents defined
   */
  public static Set<ICartacomAgent> getAll() {
    return new HashSet<ICartacomAgent>(
        RailwaySectionAgent.ALL_RAILWAY_SECTION_AGENTS);
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
