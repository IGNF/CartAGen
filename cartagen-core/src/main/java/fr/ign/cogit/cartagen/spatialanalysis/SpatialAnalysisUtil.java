package fr.ign.cogit.cartagen.spatialanalysis;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

public class SpatialAnalysisUtil {

  /**
   * Partitions a polygon using area features that form a partition of space: if
   * the polygon intersects two elements of the partition, it is cut in two. The
   * case where the outside face of the partition is not built is dealt with.
   * @param polygon
   * @param partition
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> partitionPolygon(IPolygon polygon,
      IFeatureCollection<? extends IFeature> partition) {
    Set<IPolygon> polygons = new HashSet<IPolygon>();
    IMultiSurface<IPolygon> globalInter = new GM_MultiSurface<IPolygon>();
    for (IFeature face : partition) {
      // intersect both geometries
      IGeometry inter = polygon.intersection(face.getGeom());
      // test the complexity of the intersection
      if (inter instanceof IPolygon) {
        polygons.add((IPolygon) inter);
        globalInter.add((IPolygon) inter);
      }
      if (inter instanceof IMultiSurface<?>) {
        for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) inter)
            .getList()) {
          polygons.add((IPolygon) simple);
          globalInter.add((IPolygon) simple);
        }
      }
    }
    // now check if some part of the polygon is outside the partition
    IGeometry outside = polygon.difference(globalInter);
    if (outside == null)
      return polygons;
    if (outside.isEmpty())
      return polygons;
    if (outside instanceof IPolygon)
      polygons.add((IPolygon) outside);
    if (outside instanceof IMultiSurface<?>) {
      for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) outside)
          .getList()) {
        polygons.add((IPolygon) simple);
      }
    }

    return polygons;
  }
}
