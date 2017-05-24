package fr.ign.cogit.cartagen.agents.gael.field.agent.partition.administrative;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.ParcelAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class AdministrativeParcelAgent extends ParcelAgent {

  public AdministrativeParcelAgent(AdministrativeFieldAgent champ,
      ISimpleAdminUnit unit) {
    super(unit);
    champ.getParcelles().add(this.getFeature());
    this.setGeom(unit.getGeom());
    this.setInitialGeom((IPolygon) unit.getGeom().clone());
  }

  public AdministrativeParcelAgent(AdministrativeFieldAgent champ,
      IPolygon poly) {
    super(CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
        .getGeneObjImpl().getCreationFactory().createSimpleAdminUnit(poly));
    champ.getParcelles().add(this.getFeature());
    this.setGeom(poly);
    this.setInitialGeom((IPolygon) poly.clone());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
