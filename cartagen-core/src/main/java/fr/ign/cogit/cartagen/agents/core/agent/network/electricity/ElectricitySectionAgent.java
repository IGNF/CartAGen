package fr.ign.cogit.cartagen.agents.core.agent.network.electricity;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author julien Gaffuri 1 sept. 2007
 * 
 */
public class ElectricitySectionAgent extends SectionAgent {
  // private static Logger
  // logger=LogManager.getLogger(TronconElectricite.class.getName());

  @Override
  public IElectricityLine getFeature() {
    return (IElectricityLine) super.getFeature();
  }

  public ElectricitySectionAgent(NetworkAgent res, IElectricityLine obj) {
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
    // Nothing to instantiate (yet)
  }

}
