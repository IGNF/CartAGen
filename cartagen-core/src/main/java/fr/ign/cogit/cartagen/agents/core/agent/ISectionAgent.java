package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkNodeAgent;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public interface ISectionAgent extends IMicroAgentGeneralisation {

  @Override
  public INetworkSection getFeature();

  @Override
  public ILineString getGeom();

  public NetworkAgent getNetwork();

  public NetworkNodeAgent getInitialNode();

  public void setInitialNode(NetworkNodeAgent node);

  public NetworkNodeAgent getFinalNode();

  public void setFinalNode(NetworkNodeAgent node);

  public void decompose();

}
