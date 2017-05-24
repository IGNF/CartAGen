package fr.ign.cogit.cartagen.agents.core.agent.network.road;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Density;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;

/**
 * le reseau routier
 * @author julien Gaffuri 10 févr. 2009
 * 
 */
public final class RoadNetworkAgent extends NetworkAgent {
  // private static Logger
  // logger=Logger.getLogger(ReseauRoutier.class.getName());

  public RoadNetworkAgent(INetwork net) {
    super(net);
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (AgentSpecifications.SATISFACTION_COMPOSANTS_RESEAU_ROUTIER) {
      this.ajouterContrainteSatisfactionComposants(
          AgentSpecifications.SATISFACTION_COMPOSANTS_RESEAU_ROUTIER_IMP);
    }
    if (AgentSpecifications.ROUTIER_DENSITE) {
      this.ajouterContrainteDensite(AgentSpecifications.ROUTIER_DENSITE_IMP);
    }
  }

  public void ajouterContrainteDensite(double importance) {
    new Density(this, importance);
  }

}
