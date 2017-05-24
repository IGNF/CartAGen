/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.relief;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * @author julien Gaffuri
 * 
 */
public class EmbankmentAgent extends MicroAgentGeneralisation {

  public EmbankmentAgent(ReliefFieldAgent champ, IReliefElementLine line) {
    super();
    this.setFeature(line);
    champ.getEmbankments().add(this);
    this.setInitialGeom((IPoint) this.getGeom().clone());
  }

  @Override
  public IReliefElementLine getFeature() {
    return (IReliefElementLine) super.getFeature();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }
}
