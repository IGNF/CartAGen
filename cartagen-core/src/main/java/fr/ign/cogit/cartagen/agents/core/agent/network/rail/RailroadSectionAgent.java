package fr.ign.cogit.cartagen.agents.core.agent.network.rail;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author julien Gaffuri 1 sept. 2007
 * 
 */
public class RailroadSectionAgent extends SectionAgent {
  // private static Logger
  // logger=Logger.getLogger(TronconVoieFerree.class.getName());

  @Override
  public IRailwayLine getFeature() {
    return (IRailwayLine) super.getFeature();
  }

  public RailroadSectionAgent(NetworkAgent res, IRailwayLine obj) {
    super();
    this.setFeature(obj);
    this.setNetwork(res);
    this.getNetwork().getTroncons().add(this.getFeature());
    this.getNetwork().getComponents().add(this);
    this.setInitialGeom((ILineString) this.getGeom().clone());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate at this moment
  }

}
