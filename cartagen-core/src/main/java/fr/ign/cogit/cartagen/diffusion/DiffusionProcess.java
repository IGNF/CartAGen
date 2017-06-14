/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.diffusion;

import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Interface for diffusion process (bird-eye, least squares, snakes...) i.e.
 * processes that propagate the deformations of one (or more) feature(s) on
 * other neighbour objects. Some processes may only process diffusion on
 * (previously) connected objects (e.g. in the road network).
 * @author GTouya
 * 
 */
public interface DiffusionProcess {

  /**
   * Apply the diffusion on neighbours of the deformation of changedFeats.
   * @param changedFeats
   * @param neighbours
   * @return a map of the features from neighbours that have to be diffused with
   *         the new geometry they should have.
   */
  public Map<IFeature, IGeometry> applyDiffusion(
      Map<IFeature, IGeometry> changedFeats,
      IFeatureCollection<? extends IFeature> neighbours);

  /**
   * Apply the diffusion on a single feature that has been distorted : some of
   * its vertices are moved, and the rest of the vertices have to move in order
   * to preserve the initial shape.
   * @param feat
   * @param distortedGeom
   * @return the diffused geometry.
   */
  public IGeometry applySingleDiffusion(IFeature feat, IGeometry distortedGeom);

  /**
   * Get the minimum displacement value under which a diffusion is considered
   * insignificant. It's a parameter of all diffusion processes.
   * @return
   */
  public double getMinimumDisplacement();

  public void setMinimumDisplacement(double minDisplacement);

  /**
   * The ratio describes how quickly the diffusion is absorbed by the
   * neighbours. 0 means no absorption (i.e. illimited diffusion) and 1 means no
   * diffusion.
   * @return
   */
  public double getAbsorptionRatio();

  public void setAbsorptionRatio(double absorptionRatio);
}
