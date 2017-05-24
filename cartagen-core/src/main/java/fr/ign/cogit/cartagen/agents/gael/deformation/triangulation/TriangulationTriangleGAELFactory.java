/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.triangulation;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangleFactory;

/**
 * The GAEL triangles factory
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationTriangleGAELFactory
    implements TriangulationTriangleFactory {

  /**
   * The GAEL deformable object the triangle belong to
   */
  private GAELDeformable def;

  /**
   * The constructor
   * 
   * @param def
   */
  public TriangulationTriangleGAELFactory(GAELDeformable def) {
    this.def = def;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationTriangleFactory
   * #create(fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint)
   */
  @Override
  public TriangulationTriangle create(TriangulationPoint point1,
      TriangulationPoint point2, TriangulationPoint point3) {
    return new GAELTriangle(this.def, (IPointAgent) point1,
        (IPointAgent) point2, (IPointAgent) point3);
  }

}
