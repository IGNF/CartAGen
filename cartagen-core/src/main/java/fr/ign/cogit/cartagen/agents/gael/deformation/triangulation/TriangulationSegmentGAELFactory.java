/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.triangulation;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegmentFactory;

/**
 * The GAEL segments factory
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationSegmentGAELFactory
    implements TriangulationSegmentFactory {

  /**
   * The GAEL deformable object the triangle belong to
   */
  private GAELDeformable def;

  /**
   * The constructor
   * 
   * @param def
   */
  public TriangulationSegmentGAELFactory(GAELDeformable def) {
    this.def = def;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationSegmentFactory
   * #create(fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint)
   */
  @Override
  public TriangulationSegment create(TriangulationPoint point1,
      TriangulationPoint point2) {
    return new GAELSegment(this.def, (IPointAgent) point1,
        (IPointAgent) point2);
  }
}
