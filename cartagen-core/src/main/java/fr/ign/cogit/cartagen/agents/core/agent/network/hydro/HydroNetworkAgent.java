package fr.ign.cogit.cartagen.agents.core.agent.network.hydro;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * le reseau hydrographique
 * @author julien Gaffuri 10 févr. 2009
 */
public final class HydroNetworkAgent extends NetworkAgent {

  public HydroNetworkAgent(INetwork net) {
    super(net);
  }

  /**
   * les surfaces d'eau du réseau
   */
  private IFeatureCollection<IWaterArea> surfacesEau = new FT_FeatureCollection<IWaterArea>();

  /**
   * @return
   */
  public IFeatureCollection<IWaterArea> getSurfacesEau() {
    return this.surfacesEau;
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
    for (IWaterArea se : this.surfacesEau) {
      AgentUtil.getAgentFromGeneObj(se).cleanDecomposition();
    }
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (AgentSpecifications.SATISFACTION_COMPOSANTS_RESEAU_HYDRO) {
      this.ajouterContrainteSatisfactionComposants(
          AgentSpecifications.SATISFACTION_COMPOSANTS_RESEAU_HYDRO_IMP);
    }
  }

  @Override
  public IFeatureCollection<GAELSegment> getSegments() {
    return this.getGAELDeformable().getSegments();
  }

}
