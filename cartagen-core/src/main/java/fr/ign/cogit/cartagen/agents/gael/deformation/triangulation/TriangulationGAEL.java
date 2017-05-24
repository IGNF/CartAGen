package fr.ign.cogit.cartagen.agents.gael.deformation.triangulation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * The GAEL triangulation class
 * 
 * @author Gaffuri
 */
public class TriangulationGAEL {
  private static Logger logger = Logger
      .getLogger(TriangulationGAEL.class.getName());

  /**
   * Compute a triangulation of a GAEL deformable object
   * 
   * @param def
   * @param createTriangles True is the triangles are needed
   * @param geom An input geometry to consider if we want the triangulation to
   *          belong to it. Null in other case.
   */
  public static void compute(GAELDeformable def, boolean createTriangles,
      IGeometry geom) {
    if (TriangulationGAEL.logger.isDebugEnabled()) {
      TriangulationGAEL.logger.debug("Triangulation of " + def);
    }

    // convert the input points list
    List<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
    points.addAll(def.getPointAgents());

    // convert the input segments list
    List<TriangulationSegment> segments = new ArrayList<TriangulationSegment>();
    for (GAELSegment seg : def.getSegments())
      segments.add(seg);

    // computes the triangulation
    Triangulation tri = new Triangulation(points, segments,
        new TriangulationSegmentGAELFactory(def),
        new TriangulationTriangleGAELFactory(def));
    tri.compute(createTriangles, geom);
  }

}
