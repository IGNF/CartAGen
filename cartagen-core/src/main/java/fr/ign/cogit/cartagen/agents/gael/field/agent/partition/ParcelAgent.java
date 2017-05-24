/**
 * @author julien Gaffuri 1 sept. 2008
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.partition;

import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author julien Gaffuri 1 sept. 2008
 * 
 */
public abstract class ParcelAgent extends MicroAgentGeneralisation {

  @Override
  public IGeneObjSurf getFeature() {
    return (ISimpleAdminUnit) super.getFeature();
  }

  public ParcelAgent(IGeneObjSurf parcel) {
    super();
    this.setFeature(parcel);
    this.setInitialGeom((IPolygon) this.getGeom().clone());
  }

  public void decompose() {
    /*
     * GM_Polygon poly=getGeom(); decomposer( ); int
     * nb=poly.getInterior().size(); for(int i=0; i<nb; i++)
     * decomposer(poly.getInterior(i));
     * 
     * trianguleSurfacique();
     */
  }

}
