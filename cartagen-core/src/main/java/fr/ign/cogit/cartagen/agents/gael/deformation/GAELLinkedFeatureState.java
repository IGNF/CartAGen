/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * A feature state object. A point state linked to a feature has its state
 * linked to such object.
 * 
 * @author JGaffuri
 * 
 */
public class GAELLinkedFeatureState {

  /**
   * The feature the state is linked to
   */
  private GAELLinkableFeature feature;

  /**
   * @return
   */
  public GAELLinkableFeature getFeature() {
    return this.feature;
  }

  /**
   * The geometry of the feature
   */
  private IGeometry geometry;

  /**
   * @return
   */
  public IGeometry getGeometry() {
    return this.geometry;
  }

  /**
   * The constructor
   * 
   * @param feature
   */
  public GAELLinkedFeatureState(GAELLinkableFeature feature) {
    this.feature = feature;
    if (feature.getGeom() == null) {
      this.geometry = null;
    } else {
      this.geometry = (IGeometry) feature.getGeom().clone();
    }
  }

  /**
   * Clean the object
   */
  public void clean() {
    this.feature = null;
    this.geometry = null;
  }

}
