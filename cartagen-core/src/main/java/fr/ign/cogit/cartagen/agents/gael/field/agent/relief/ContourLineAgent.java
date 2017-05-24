/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.relief;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.decomposers.Decomposers;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;

/**
 * @author julien Gaffuri
 */
public class ContourLineAgent extends MicroAgentGeneralisation {
  // private static Logger logger = Logger.getLogger(CourbeDeNiveau.class);

  @Override
  public IContourLine getFeature() {
    return (IContourLine) super.getFeature();
  }

  @Override
  public ILineString getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  public ILineString getInitialGeom() {
    return (ILineString) super.getInitialGeom();
  }

  public ChampContinu getChampContinu() {
    return ((CourbeDeNiveau) this.getFeature().getGeoxObj()).getChampContinu();
  }

  public void setChampContinu(ChampContinu champContinu) {
    ((CourbeDeNiveau) this.getFeature().getGeoxObj())
        .setChampContinu(champContinu);
  }

  private ReliefFieldAgent field;

  public ContourLineAgent(ReliefFieldAgent field, IContourLine cn) {
    super();
    this.setFeature(cn);
    this.field = field;
    if (!field.getContourLines().contains(this)) {
      field.getContourLines().add(this);
    }

    // affecte altitude a tous les points
    for (IDirectPosition dp : this.getGeom().coord()) {
      dp.setZ(this.getFeature().getAltitude());
    }

    this.setInitialGeom((ILineString) this.getGeom().clone());

  }

  public void decompose() {
    // applique decomposition generique d'objet lineaire
    Decomposers.decomposerLinear(this.field, this.getGeom(), false);

    // renseigne valeur d'altitude des coordonnees
    for (IDirectPosition dp : this.getGeom().coord()) {
      dp.setZ(this.getFeature().getAltitude());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
