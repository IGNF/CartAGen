package fr.ign.cogit.cartagen.spatialanalysis.measures;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class ProximityBtwPossiblyOverlappingPolygon {
  final static Logger logger = Logger
      .getLogger(ProximityBtwPossiblyOverlappingPolygon.class.getName());

  /**
   * For two possibly overlapping polygons A and B , returns an indicator of
   * proximity or overlapping btw them computed as follows: -if they do not
   * overlap, the minimum (euclidian) distance -if they overlap, a negative
   * distance which is the opposite of the Hausdorff distance between
   * (outerline(A)-B) and (outeline(B)-A).
   * @param poly1 first polygon
   * @param poly2 second polygon (the order does not impact the result)
   * @return the computed distance
   */
  @SuppressWarnings("unchecked")
  public static double ProximityBtwPossiblyOverlappingPolygons(IPolygon poly1,
      IPolygon poly2) {

    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();

    // test if geometries are disjoint.
    if (jtsAlgorithms.disjoint(poly1, poly2)) {
      // return the smallest eulician distance between geometries.
      return jtsAlgorithms.distance(poly1, poly2);
    } else if (isInside(poly1, poly2)) {
      // if one of the surface is included in the other, return -infinite.
      return Double.MIN_VALUE;
    } else {
      // if geometries are intersecting, return a negative value
      try {
        // compute the difference between the outerline of poly1 and poly2
        IGeometry geo1 = jtsAlgorithms.difference(poly1.getExterior()
            .getPrimitive(), poly2);
        ILineString line1 = null;
        if (geo1.isMultiCurve()) {
          // TODO dont realy manage polylines
          line1 = (ILineString) ((IMultiCurve<ILineString>) geo1).get(0);
          // Set<IDirectPosition> line1Point = new HashSet<IDirectPosition>();
          // for (ILineString l : ((IMultiCurve<ILineString>) geo1).getList()) {
          // if (!line1.getControlPoint(line1.sizeControlPoint() - 1).equals(
          // point)) {
          // line1.addControlPoint(point);
          // }
          // }
        } else {
          line1 = (ILineString) geo1;
        }

        // compute the difference between the outerline of poly2 and poly1
        IGeometry geo2 = jtsAlgorithms.difference(poly2.getExterior()
            .getPrimitive(), poly1);
        ILineString line2 = null;
        if (geo2.isMultiCurve()) {
          // TODO dont realy manage polylines
          line2 = (ILineString) ((IMultiCurve<ILineString>) geo2).get(0);
          // for (IDirectPosition point : ((IMultiCurve<ILineString>)
          // geo2).get(0)
          // .coord()) {
          // if (!line2.getControlPoint(line2.sizeControlPoint() - 1).equals(
          // point)) {
          // line2.addControlPoint(point);
          // }
          // }
        } else {
          line2 = (ILineString) geo2;
        }

        // System.out.println("poly1 " + poly1 + ", size " +
        // poly1.coord().size());
        // System.out.println("poly2 " + poly2 + ", size " +
        // poly2.coord().size());
        // System.out.println("poly2.getExterior() " + poly2.getExterior()
        // + ", size " + poly2.getExterior().coord().size());
        //
        // System.out.println("line1 " + line1 + ", size " +
        // line1.coord().size());
        // System.out.println("line2 " + line2 + ", size " +
        // line2.coord().size());
        //
        // if (line2.isEmpty()) {
        // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        // .addFeatureToGeometryPool(poly2, Color.BLUE, 2);
        // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        // .addFeatureToGeometryPool(poly1, Color.RED, 2);
        // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        // .addFeatureToGeometryPool(poly1, Color.YELLOW, 4);
        // }

        return -Distances.hausdorff(line1, line2);
      } catch (ClassCastException e) {
        e.printStackTrace();
      }
      return 0;
    }
  }

  private static boolean isInside(IPolygon poly1, IPolygon poly2) {
    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();
    IGeometry inter = jtsAlgorithms.intersection(poly1, poly2);
    double area = 0.0;
    if (inter != null)
      area = inter.area();

    double threshold = 0.000001;

    double diff1 = Math.abs(poly1.area() - area);
    double diff2 = Math.abs(poly2.area() - area);

    return diff1 > threshold || diff2 > threshold;
  }
}
