package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;

public class RoadsCarryingRoutesNetworkAgent
    extends CartAComAgentGeneralisation {

  private CartAGenDataSet dataset;

  public RoadsCarryingRoutesNetworkAgent(INetwork feature,
      CartAGenDataSet dataset) {
    super(feature);
    this.dataset = dataset;
  }

  public RoadsCarryingRoutesNetworkAgent(INetwork feature) {
    super(feature);
  }

  public CartAGenDataSet getDataset() {
    return dataset;
  }

}
