/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IMaskAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;

/**
 * @author CDuchene
 * 
 */
public class MaskAgent extends NetworkSectionAgent implements IMaskAgent {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Set holding all the RailwaySectionAgents instanciated in the system
   */
  private static Set<MaskAgent> ALL_MASK_AGENTS = new HashSet<MaskAgent>();

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
  public MaskAgent(IMask mask) {
    super(mask);
    this.setAgentAgent((GeographicObjectAgentGeneralisation) AgentUtil
        .getAgentFromGeneObj(mask));
    MaskAgent.getAllMasks().add(this);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Getter for ALL_RAILWAY_SECTION_AGENTS.
   * @return of all railway section agents defined
   */
  public static Set<MaskAgent> getAllMasks() {
    return MaskAgent.ALL_MASK_AGENTS;
  }

  /**
   * Gets a set of all RailwaySectionAgents, as CartAComAgents.
   * @return of all railway section agents defined
   */
  public static Set<ICartacomAgent> getAll() {
    return new HashSet<ICartacomAgent>(MaskAgent.ALL_MASK_AGENTS);
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
