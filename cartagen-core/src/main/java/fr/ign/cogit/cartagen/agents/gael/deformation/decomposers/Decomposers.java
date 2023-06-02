package fr.ign.cogit.cartagen.agents.gael.deformation.decomposers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Some procedure to decompose deformable objects into submicro.
 * 
 * @author JGaffuri
 * 
 */
public class Decomposers {
  private static Logger logger = LogManager.getLogger(Decomposers.class.getName());

  /**
   * Decompose the geometry of a polygonal deformable object. This deformable
   * object is supposed to implement the Feature interface, and have a polygonal
   * geometry. The build submicro objects are: - the point agent composing the
   * geometry, - the segments linking these points (the internal holes are
   * considered) - if specified, the angles are built too
   * 
   * @param def
   * @param buildAngles
   */
  public static void decomposeLimitPolygon(GAELDeformable def,
      boolean buildAngles) {
    if (Decomposers.logger.isDebugEnabled()) {
      Decomposers.logger.debug("decomposition of " + def);
    }

    // check it is a feature
    IFeature feat = null;
    if (!(def instanceof IFeature)) {
      feat = ((GeographicObjectAgentGeneralisation) def).getFeature();
    } else {
      feat = (IFeature) def;
    }

    // get the geometry
    IGeometry geom = feat.getGeom();

    // check it is a polygon
    if (!(geom instanceof IPolygon)) {
      Decomposers.logger.error("Impossible to decompose " + def
          + ". Its geometry should be a polygon");
      return;
    }

    // get the polygon
    IPolygon poly = (IPolygon) geom;

    // decompose external ring
    Decomposers.decomposerRing(def, poly.getExterior(), buildAngles);

    // decompose internal rings
    for (int i = 0; i < poly.getInterior().size(); i++) {
      Decomposers.decomposerRing(def, poly.getInterior(i), buildAngles);
    }
  }

  /**
   * Decompose the geometry of a linear deformable object. This deformable
   * object is supposed to implement the Feature interface, and have a linear
   * geometry. The build submicro objects are: - the point agent composing the
   * geometry, - the segments linking these points - if specified, the angles
   * are built too
   * 
   * @param def
   * @param buildAngles
   */
  public static void decomposerLinear(GAELDeformable def, boolean buildAngles) {
    if (Decomposers.logger.isDebugEnabled()) {
      Decomposers.logger.debug("decomposition of " + def);
    }

    // check it is a feature
    if (!(def instanceof GeographicObjectAgentGeneralisation)) {
      Decomposers.logger.error("Impossible to decompose " + def
          + ". It should be from " + IFeature.class.getSimpleName());
      return;
    }

    // get the geometry
    IGeometry geom = ((GeographicObjectAgentGeneralisation) def).getFeature()
        .getGeom();

    // check it is a linestring
    if (!(geom instanceof ILineString)) {
      Decomposers.logger.error("Impossible to decompose " + def
          + ". Its geometry should be a linestring");
      return;
    }

    // decompose the geometry
    Decomposers.decomposerDPL(def, ((ILineString) geom).coord(), buildAngles);
  }

  /**
   * Decompose the geometry of a linear deformable object. This deformable
   * object is supposed to implement the Feature interface, and have a linear
   * geometry. The build submicro objects are: - the point agent composing the
   * geometry, - the segments linking these points - if specified, the angles
   * are built too
   * 
   * @param def
   * @param buildAngles
   */
  public static void decomposerLinear(GAELDeformable def, ILineString geom,
      boolean buildAngles) {
    if (Decomposers.logger.isDebugEnabled()) {
      Decomposers.logger.debug("decomposition of " + def);
    }

    // decompose the geometry
    Decomposers.decomposerDPL(def, geom.coord(), buildAngles);
  }

  /**
   * Decompose a ring into a specified deformable object.
   * 
   * @param def
   * @param ring
   * @param buildAngles
   */
  private static void decomposerRing(GAELDeformable def, IRing ring,
      boolean buildAngles) {
    if (Decomposers.logger.isDebugEnabled()) {
      Decomposers.logger.debug("decomposition of " + def + ": " + ring);
    }
    Decomposers.decomposerDPL(def, ring.coord(), buildAngles);
  }

  /**
   * Decompose a directposition list into a specified deformable object.
   * 
   * @param def
   * @param coords
   * @param buildAngles
   */
  private static void decomposerDPL(GAELDeformable def,
      IDirectPositionList coords, boolean buildAngles) {
    if (Decomposers.logger.isDebugEnabled()) {
      Decomposers.logger.debug("decomposition of " + def + ": " + coords);
    }

    int nb = coords.size();
    if (Decomposers.logger.isTraceEnabled()) {
      Decomposers.logger.trace("points nb=" + nb);
    }

    // if there are less than 2 points, there is a problem
    if (nb < 2) {
      Decomposers.logger.error("Error when decomposing in " + def
          + ". Coordinates list must have more than 2 points.");
      Decomposers.logger.error(coords);
      return;
    }

    // create the two first points and there segment
    IPointAgent ap0 = new PointAgentImpl(def, coords.get(0));
    IPointAgent ap1 = new PointAgentImpl(def, coords.get(1));
    new GAELSegment(def, ap0, ap1);

    // store the two first points (usefull at the end, for angle construction)
    IPointAgent ap0_ = ap0;
    IPointAgent ap1_ = ap1;

    IPointAgent ap2 = null;
    for (int i = 2; i < nb - 1; i++) {

      // build point agent
      if (Decomposers.logger.isTraceEnabled()) {
        Decomposers.logger.trace(
            "(" + coords.get(i).getX() + ", " + coords.get(i).getY() + ")");
      }
      ap2 = new PointAgentImpl(def, coords.get(i));

      // build segment
      new GAELSegment(def, ap1, ap2);

      // build angle (is needed)
      if (buildAngles) {
        new GAELAngle(def, ap0, ap1, ap2);
      }

      // next
      ap0 = ap1;
      ap1 = ap2;
    }

    // test closure
    boolean closed;
    if (coords.get(0).distance(coords.get(nb - 1)) <= 0.001) {
      closed = true;
    } else {
      closed = false;
    }

    // line is closed
    if (closed) {
      // build the last segment to close the ring
      new GAELSegment(def, ap1, ap0_);
      // build the two last angles (if needed)
      if (buildAngles) {
        new GAELAngle(def, ap0, ap1, ap0_);
        new GAELAngle(def, ap1, ap0_, ap1_);
      }
      // possible link between last coordinate and first point agent
      if (coords.get(0) != coords.get(nb - 1)) {
        ap0_.getPositions().add(coords.get(nb - 1));
      }
    } else {
      // build the last point agent
      if (Decomposers.logger.isTraceEnabled()) {
        Decomposers.logger.trace("(" + coords.get(nb - 1).getX() + ", "
            + coords.get(nb - 1).getY() + ")");
      }
      ap2 = new PointAgentImpl(def, coords.get(nb - 1));

      // build the last segment
      new GAELSegment(def, ap1, ap2);

      // build the last angle (if needed)
      if (buildAngles) {
        new GAELAngle(def, ap0, ap1, ap2);
      }
    }
  }
}
