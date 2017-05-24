package fr.ign.cogit.cartagen.spatialanalysis.geospace;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class RuralAreas {

  /**
   * The first method to build rural areas uses urban and rurban areas as an
   * input. The rural areas are the complementary areas: where it's not urban or
   * rurban, it's rural. These areas are partitioned by network faces to avoid
   * too big areas.
   * 
   * 
   */
  public static Set<IPolygon> buildRuralAreas1() {
    // initialisation
    Set<IPolygon> areas = new HashSet<IPolygon>();

    // TODO

    return areas;
  }

  /**
   * Method 2 gives quite different rural areas but uses the method 1 as a first
   * step: in rural areas from method1, groups are built with buffers around
   * buildings and only these groups are considered as rural areas.
   * 
   * @param buffThres : the buffer size to build rural group in a rural network
   *          face.
   * 
   */
  public static Set<IPolygon> buildRuralAreas2(double buffThres) {
    // initialisation
    Set<IPolygon> areas = new HashSet<IPolygon>();

    // TODO

    return areas;
  }

}
