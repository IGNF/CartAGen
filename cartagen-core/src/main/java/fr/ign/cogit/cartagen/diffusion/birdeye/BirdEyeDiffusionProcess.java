/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.diffusion.birdeye;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.diffusion.AbstractDiffusionProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Implementation of a diffusion process that follows (Legrand 2005) bird-eye
 * diffusion process. This diffusion process is not bound to network features,
 * but is not so good to preserve straight line features. It should not be used
 * as a standard diffusion process for road network features.
 * @author GTouya
 * 
 */
public class BirdEyeDiffusionProcess extends AbstractDiffusionProcess {

  /**
   * parameter of birdeye diffusion: the interval between two vectors of the
   * vector field. Value in map mm. Advised value: 0.5 map mm. paramètre de la
   * diffusion à vol d'oiseau. Intervalle entre les vecteurs du champ de
   * vecteurs. Exprimé en mm carte. valeur conseillée : 0.5 mm carte.
   */
  private double vectInterval;
  /**
   * la limite max que peut avoir une zone d'influence d'un déplacement d'objet.
   */
  private double vdoLimitZoneInflu = 3000.0;
  /**
   * le set des objets fixes dans la diffusion à vol d'oiseau. Ils ne sont pas
   * diffusés et bloquent la diffusion sur les objets cachés derrière eux.
   */
  private Set<IFeature> fixedObjects;

  @Override
  public Map<IFeature, IGeometry> applyDiffusion(
      Map<IFeature, IGeometry> changedFeats,
      IFeatureCollection<? extends IFeature> neighbours) {
    // TODO Auto-generated method stub
    return null;
  }

  public double getVectInterval() {
    return vectInterval;
  }

  public void setVectInterval(double vectInterval) {
    this.vectInterval = vectInterval;
  }

  public double getVdoLimitZoneInflu() {
    return vdoLimitZoneInflu;
  }

  public void setVdoLimitZoneInflu(double vdoLimitZoneInflu) {
    this.vdoLimitZoneInflu = vdoLimitZoneInflu;
  }

  public Set<IFeature> getFixedObjects() {
    return fixedObjects;
  }

  public void setFixedObjects(Set<IFeature> fixedObjects) {
    this.fixedObjects = fixedObjects;
  }

  @Override
  public IGeometry applySingleDiffusion(IFeature feat,
      IGeometry distortedGeom) {
    // TODO Auto-generated method stub
    return null;
  }

}
