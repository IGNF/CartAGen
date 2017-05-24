/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.core.agent.network;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * @author julien Gaffuri
 */
public class NetworkAgent extends MesoAgentGeneralisation<ISectionAgent> {

  public NetworkAgent(INetwork net) {
    super();
    this.setFeature(net);
  }

  @Override
  public INetwork getFeature() {
    return (INetwork) super.getFeature();
  }

  public CarteTopo getCarteTopo() {
    return this.getFeature().getCarteTopo();
  }

  /**
   * les tronçons
   */
  private Population<INetworkSection> troncons = new Population<INetworkSection>();

  /**
   * @return
   */
  public IPopulation<INetworkSection> getTroncons() {
    return this.troncons;
  }

  /**
   * les noeuds
   */
  private IFeatureCollection<INetworkNode> noeuds = new FT_FeatureCollection<INetworkNode>();

  /**
   * @return
   */
  public IFeatureCollection<INetworkNode> getNoeuds() {
    return this.noeuds;
  }

  // ajout Kusay
  public void setNoeuds(IFeatureCollection<INetworkNode> noeuds) {
    this.noeuds = noeuds;
  }

  public void decompose() {
    for (INetworkSection tr : this.getTroncons()) {
      SectionAgent agent = (SectionAgent) AgentUtil.getAgentFromGeneObj(tr);
      agent.decompose();
      this.getGAELDeformable().getSegments().addAll(agent.getSegments());
    }
    this.creerPointsSingletonsAPartirDesPoints();
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
    for (INetworkSection tr : this.getTroncons()) {
      ((SectionAgent) AgentUtil.getAgentFromGeneObj(tr)).cleanDecomposition();
    }
  }

  /**
   * recupere le noeud du reseau existant eventuellement en un point
   * @param c les coordonnées du point
   * @return le noeud s'il existe, le pointeur null sinon
   */
  public INetworkNode getNoeud(IDirectPosition c) {
    IDirectPosition c_;
    for (INetworkNode n : this.getNoeuds()) {
      c_ = n.getGeom().coord().get(0);
      if (c_.getX() == c.getX() && c_.getY() == c.getY()) {
        return n;
      }
    }
    return null;
  }

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj) {
    // Do nothing as default
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
