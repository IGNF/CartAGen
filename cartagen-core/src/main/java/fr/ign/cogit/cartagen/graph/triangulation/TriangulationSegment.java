/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.graph.triangulation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;

/**
 * The segment interface
 * @author jgaffuri
 */
public interface TriangulationSegment extends IEdge {

  /**
   */
  public TriangulationPoint getPoint1();

  /**
   */
  public TriangulationPoint getPoint2();

  /**
   * @return The geometry of the segment
   */
  @Override
  public ILineSegment getGeom();

}
