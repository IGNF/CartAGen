package fr.ign.cogit.cartagen.spatialanalysis.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefTriangle;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Static class compiling all methods used for enriching fields
 * @author JRenard
 * 
 */
public class FieldEnrichment {
  private static Logger logger = LogManager
      .getLogger(FieldEnrichment.class.getName());

  /**
   * Creates relief triangles in a relief field and update the GUI in
   * consequence
   * @param field
   */
  public static void createTrianglesInReliefField(IReliefField field) {
    IFeatureCollection<IReliefTriangle> triangles = new FT_FeatureCollection<IReliefTriangle>();
    Triangulation tri = FieldEnrichment.buildTriangulation(field);
    for (TriangulationTriangle triangle : tri.getTriangles()) {
      IReliefTriangle triFace = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getGeneObjImpl().getCreationFactory()
          .createReliefTriangle(triangle.getPoint1().getPosition(),
              triangle.getPoint2().getPosition(),
              triangle.getPoint3().getPosition());
      triangles.add(triFace);
    }
    field.setTriangles(triangles);
  }

  /**
   * effectue triangulation contrainte d'un champ relief, en prenant en compte
   * ses courbes de niveau, ses points cotés et ses pixels MNT
   */
  public static Triangulation buildTriangulation(IReliefField reliefField) {

    List<TriangulationPoint> triPoints = new ArrayList<TriangulationPoint>();
    Collection<TriangulationSegment> triSegments = new ArrayList<TriangulationSegment>();

    // Contour lines
    for (IContourLine line : reliefField.getContourLines()) {
      triSegments.addAll(FieldEnrichment.decomposeContourLine(line));
    }

    // Spot heights
    for (ISpotHeight spot : reliefField.getSpotHeights()) {
      TriangulationPoint pt = new TriangulationPointImpl(
          spot.getGeom().getPosition());
      pt.getPosition().setZ(spot.getZ());
      triPoints.add(pt);
    }

    // DEM pixels
    for (IDEMPixel pix : reliefField.getDEMPixels()) {
      TriangulationPoint pt = new TriangulationPointImpl(
          pix.getGeom().getPosition());
      pt.getPosition().setZ(pix.getZ());
      triPoints.add(pt);
    }

    // triangulation
    Triangulation tri = new Triangulation(triPoints, triSegments,
        new TriangulationSegmentFactoryImpl(),
        new TriangulationTriangleFactoryImpl());

    tri.compute(true);

    return tri;

  }

  /**
   * Decomposes a contour line by creating all triangulation segments on its
   * geometry
   * @param line
   * @return the triangulation segments ready to be used
   */
  private static List<TriangulationSegment> decomposeContourLine(
      IContourLine line) {

    List<TriangulationSegment> segs = new ArrayList<TriangulationSegment>();
    double z = line.getAltitude();
    IDirectPositionList coords = line.getGeom().coord();
    int nb = coords.size();

    // if there are less than 2 points, there is a problem
    if (nb < 2) {
      return segs;
    }

    // create the two first points and there segment
    TriangulationPoint ap0 = new TriangulationPointImpl(coords.get(0));
    ap0.getPosition().setZ(z);
    TriangulationPoint ap1 = new TriangulationPointImpl(coords.get(1));
    ap1.getPosition().setZ(z);
    segs.add(new TriangulationSegmentImpl(ap0, ap1));

    // store the two first points (usefull at the end, for angle construction)
    TriangulationPoint ap0_ = ap0;
    ap0_.getPosition().setZ(z);

    TriangulationPoint ap2 = null;
    for (int i = 2; i < nb - 1; i++) {

      // build point agent
      ap2 = new TriangulationPointImpl(coords.get(i));
      ap2.getPosition().setZ(z);

      // build segment
      segs.add(new TriangulationSegmentImpl(ap1, ap2));

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
      segs.add(new TriangulationSegmentImpl(ap1, ap0_));
    } else {
      // build the last point agent
      ap2 = new TriangulationPointImpl(coords.get(nb - 1));
      ap2.getPosition().setZ(z);
      // build the last segment
      segs.add(new TriangulationSegmentImpl(ap1, ap2));
    }

    return segs;

  }

  /**
   * picks up all contour lines of a field that are connected
   * @param relief field
   */
  public static void pickUpContourLines(IReliefField field) {

    if (FieldEnrichment.logger.isDebugEnabled()) {
      FieldEnrichment.logger
          .debug("creation de l'index spatial des cn de " + field);
    }
    field.getContourLines().initSpatialIndex(Tiling.class, false);

    // recupere cn a recoller
    Object[] cns = FieldEnrichment.findContourLinesToPickUp(field);

    while (cns != null) {

      // recupere les deux cn a recoller et de leur geometrie fusionnee
      IContourLine cn0 = (IContourLine) cns[0];
      IContourLine cn1 = (IContourLine) cns[1];
      IGeometry union = (IGeometry) cns[2];

      // fusion des deux courbes
      cn0.setGeom(union);
      field.removeContourLine(cn1);

      cns = FieldEnrichment.findContourLinesToPickUp(field);
    }

    field.getContourLines().removeSpatialIndex();

  }

  /**
   * returns contours lines of a field that need to be picked up
   * @param relief field
   */
  private static Object[] findContourLinesToPickUp(IReliefField field) {

    // parcours des courbes de niveau
    for (IContourLine cn0 : field.getContourLines()) {
      Collection<IContourLine> cns = field.getContourLines()
          .select(cn0.getGeom(), false);

      // System.out.println("nb="+cns.size());

      for (IContourLine cn1 : cns) {

        if (cn0 == cn1) {
          continue;
        }

        // System.out.println(cn0.getGeom());
        // System.out.println(cn1.getGeom());

        // IGeometry inter = cn0.getGeom().intersection(cn1.getGeom());
        IGeometry union = cn0.getGeom().union(cn1.getGeom());
        // System.out.println(inter);
        // System.out.println(union);

        if (union.isLineString()) {
          return new Object[] { cn0, cn1, union };
        }

        if (union.isMultiCurve()) {
          IMultiCurve<?> mc = (IMultiCurve<?>) union;
          // System.out.println(mc.size());
          if (mc.size() == 1) {
            return new Object[] { cn0, cn1, (ILineString) union };
          }
          if (mc.size() == 2) {
            IDirectPositionList dpl0 = cn0.getGeom().coord();
            IDirectPositionList dpl1 = cn1.getGeom().coord();

            IDirectPosition c00 = dpl0.get(0), c0n = dpl0.get(dpl0.size() - 1);
            IDirectPosition c10 = dpl1.get(0), c1n = dpl1.get(dpl1.size() - 1);

            //
            if (c00.equals(c10)) {
              dpl0.inverseOrdre();
              for (int i = 1; i < dpl1.size(); i++) {
                dpl0.add(dpl1.get(i));
              }
              return new Object[] { cn0, cn1, new GM_LineString(dpl0) };
            } else if (c0n.equals(c10)) {
              for (int i = 1; i < dpl1.size(); i++) {
                dpl0.add(dpl1.get(i));
              }
              return new Object[] { cn0, cn1, new GM_LineString(dpl0) };
            } else if (c00.equals(c1n)) {
              for (int i = 1; i < dpl0.size(); i++) {
                dpl1.add(dpl0.get(i));
              }
              return new Object[] { cn0, cn1, new GM_LineString(dpl1) };
            } else if (c0n.equals(c1n)) {
              dpl1.inverseOrdre();
              for (int i = 1; i < dpl1.size(); i++) {
                dpl0.add(dpl1.get(i));
              }
              return new Object[] { cn0, cn1, new GM_LineString(dpl0) };
            } else {
              FieldEnrichment.logger.warn("Warning: CN se touchent bizarrement "
                  + cn0.getGeom() + "   " + cn1.getGeom());
            }

          }
        }
      }
    }
    return null;
  }

}
