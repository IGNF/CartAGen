package fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.ParcelAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class LandUseParcelAgent extends ParcelAgent {

  @Override
  public ISimpleLandUseArea getFeature() {
    return (ISimpleLandUseArea) super.getFeature();
  }

  /**
   * @return
   */
  public int getType() {
    return this.getFeature().getType();
  }

  /**
   * @param type
   */
  public void setType(int type) {
    this.getFeature().setType(type);
  }

  public LandUseParcelAgent(LandUseFieldAgent champ, ISimpleLandUseArea area,
      int type, double doug) {
    super(area);
    champ.getParcelles().add(this.getFeature());
    this.setType(type);

    this.setGeom(CommonAlgorithms.filtreDouglasPeucker(area.getGeom(), doug));
    this.setInitialGeom((IGeometry) area.getGeom().clone());
  }

  public LandUseParcelAgent(LandUseFieldAgent champ, IPolygon poly, int type,
      double doug) {
    super(CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
        .getGeneObjImpl().getCreationFactory()
        .createSimpleLandUseArea(poly, type));
    champ.getParcelles().add(this.getFeature());
    this.setType(type);

    this.setGeom(CommonAlgorithms.filtreDouglasPeucker(poly, doug));
    this.setInitialGeom((IGeometry) this.getGeom().clone());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
