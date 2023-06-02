/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.relief;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * @author julien Gaffuri
 * 
 */
public class SpotHeightAgent extends MicroAgentGeneralisation {
  static Logger logger = LogManager.getLogger(SpotHeightAgent.class.getName());

  @Override
  public ISpotHeight getFeature() {
    return (ISpotHeight) super.getFeature();
  }

  public SpotHeightAgent(ReliefFieldAgent champ, ISpotHeight pt) {
    super();
    this.setFeature(pt);
    champ.getSpotHeights().add(this);
    this.setInitialGeom((IPoint) this.getGeom().clone());
  }

  public void decompose() {
    if (SpotHeightAgent.logger.isDebugEnabled()) {
      SpotHeightAgent.logger.debug("decomposition de " + this);
    }

    IDirectPosition c = this.getGeom().coord().get(0);

    if (SpotHeightAgent.logger.isDebugEnabled()) {
      SpotHeightAgent.logger.debug("(" + c.getX() + ", " + c.getY() + ")");
    }

    new PointAgentImpl(this, c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }
}
