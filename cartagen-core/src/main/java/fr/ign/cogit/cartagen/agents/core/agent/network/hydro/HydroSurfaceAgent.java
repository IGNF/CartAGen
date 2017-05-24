/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent.network.hydro;

import fr.ign.cogit.cartagen.agents.core.agent.SmallCompactAgent;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * @author JGaffuri
 * 
 */
public class HydroSurfaceAgent extends SmallCompactAgent {
  // private static Logger logger=Logger.getLogger(SurfaceEau.class.getName());

  @Override
  public IWaterArea getFeature() {
    return (IWaterArea) super.getFeature();
  }

  public HydroSurfaceAgent(HydroNetworkAgent res, IWaterArea surf) {
    super();
    this.setFeature(surf);
    res.getSurfacesEau().add(this.getFeature());
    this.setInitialGeom((GM_Polygon) this.getGeom().clone());
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    // ajouter contrainte platitude de lac, un jour
  }

}
