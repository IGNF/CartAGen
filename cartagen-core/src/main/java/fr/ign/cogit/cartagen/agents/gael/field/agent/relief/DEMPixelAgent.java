/**
 * @author julien Gaffuri 16 déc. 2008
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.relief;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * @author julien Gaffuri 16 déc. 2008
 */
public class DEMPixelAgent extends MicroAgentGeneralisation {
  static Logger logger = Logger.getLogger(DEMPixelAgent.class);

  @Override
  public IDEMPixel getFeature() {
    return (IDEMPixel) super.getFeature();
  }

  @Override
  public IPoint getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  public IPoint getInitialGeom() {
    return (IPoint) super.getInitialGeom();
  }

  public DEMPixelAgent(ReliefFieldAgent champ, IDEMPixel pix) {
    super();
    this.setFeature(pix);
    champ.getPixelsMNT().add(this);
  }

  /**
   * @return
   */
  public double getX() {
    return this.getFeature().getX();
  }

  /**
   * @return
   */
  public double getY() {
    return this.getFeature().getY();
  }

  /**
   * @return
   */
  public double getZ() {
    return this.getFeature().getZ();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
