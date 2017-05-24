package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;

/**
 * @author
 */
public interface IBuildingAgent extends IUrbanElementAgent {

  @Override
  public IBuilding getFeature();

}
