/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;

/**
 * @author CDuchene
 * 
 */
public class BuildingAgent extends SmallCompactAgent implements IBuildingAgent {

  private static Logger logger = Logger
      .getLogger(BuildingAgent.class.getName());

  /**
   * Constructs a building cartacom agent to handle the generalisation of a
   * building geographic object.
   * @param bat The building object handled by this cartacom building agent
   */
  public BuildingAgent(IBuilding bat) {
    super(bat);
    this.setAgentAgent((GeographicObjectAgentGeneralisation) AgentUtil
        .getAgentFromGeneObj(bat));
  }

  @Override
  public IBuildingAgent getAgentAgent() {
    return (IBuildingAgent) super.getAgentAgent();
  }

}
