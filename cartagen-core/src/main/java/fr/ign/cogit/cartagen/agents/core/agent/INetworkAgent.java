package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;

public interface INetworkAgent
    extends MesoAgent<INetworkAgent>, GAELDeformable {

  @Override
  INetwork getFeature();

  CarteTopo getCarteTopo();

  IPopulation<INetworkSection> getTroncons();

  IFeatureCollection<INetworkNode> getNoeuds();

  void setNoeuds(IFeatureCollection<INetworkNode> noeuds);

  void decompose();

  /**
   * recupere le noeud du reseau existant eventuellement en un point
   * @param c les coordonnées du point
   * @return le noeud s'il existe, le pointeur null sinon
   */
  INetworkNode getNoeud(IDirectPosition c);

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj);

  /**
   * {@inheritDoc}
   */
  // @Override
  // void instantiateConstraints();
}
