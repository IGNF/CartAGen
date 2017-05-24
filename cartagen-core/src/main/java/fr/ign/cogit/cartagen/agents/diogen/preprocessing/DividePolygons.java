package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class DividePolygons {

  public static Set<IPolygon> execute(IPolygon polygon, double minX,
      double maxX, double minY, double maxY, double step) {
    Set<IPolygon> toReturn = new HashSet<IPolygon>();
    for (double x = minX; x < maxX; x += step) {
      for (double y = minY; y < maxY; y += step) {
        IEnvelope rectangle = new GM_Envelope(x, x + step, y, y + step);
        JtsAlgorithms jtsAlgos = new JtsAlgorithms();
        IGeometry intersection = jtsAlgos.intersection(polygon,
            rectangle.getGeom());
        if (!intersection.isEmpty()) {
          if (intersection instanceof IPolygon) {
            toReturn.add((IPolygon) intersection);
            // newWaterAreas.add(new CdBWaterArea(,
            // ((CdBWaterArea) waterArea).isMaritime(),
            // ((CdBWaterArea) waterArea).getSymbo()));
          } else if (intersection instanceof IMultiSurface) {
            toReturn.addAll(((IMultiSurface<IPolygon>) intersection).getList());
            // for (IPolygon p : ((IMultiSurface<IPolygon>) intersection)
            // .getList()) {
            // toReturn.add(p);
            // newWaterAreas.add(new CdBWaterArea(polygon,
            // ((CdBWaterArea) waterArea).isMaritime(),
            // ((CdBWaterArea) waterArea).getSymbo()));

          }
        }
      }
    }
    return toReturn;
  }

  public static Set<IPolygon> execute(Set<IPolygon> polygons, double minX,
      double maxX, double minY, double maxY, double step) {
    Set<IPolygon> toReturn = new HashSet<IPolygon>();
    for (double x = minX; x < maxX; x += step) {
      for (double y = minY; y < maxY; y += step) {

        // System.out.println("y : " + y);
        IEnvelope rectangle = new GM_Envelope(x, x + step, y, y + step);
        for (IPolygon polygon : polygons) {
          JtsAlgorithms jtsAlgos = new JtsAlgorithms();
          IGeometry intersection = jtsAlgos.intersection(polygon,
              rectangle.getGeom());
          if (!intersection.isEmpty()) {
            if (intersection instanceof IPolygon) {
              toReturn.add((IPolygon) intersection);
              // newWaterAreas.add(new CdBWaterArea(,
              // ((CdBWaterArea) waterArea).isMaritime(),
              // ((CdBWaterArea) waterArea).getSymbo()));
            } else if (intersection instanceof IMultiSurface) {
              toReturn.addAll(((IMultiSurface<IPolygon>) intersection)
                  .getList());
              // for (IPolygon p : ((IMultiSurface<IPolygon>) intersection)
              // .getList()) {
              // toReturn.add(p);
              // newWaterAreas.add(new CdBWaterArea(polygon,
              // ((CdBWaterArea) waterArea).isMaritime(),
              // ((CdBWaterArea) waterArea).getSymbo()));
            }
          }
        }
      }
    }
    return toReturn;
  }

  public static Set<IPolygon> execute(Set<IPolygon> polygons,
      List<Double> xCoord, List<Double> yCoord) {
    Set<IPolygon> toReturn = new HashSet<IPolygon>();
    for (double x : xCoord) {
      if (xCoord.lastIndexOf(x) == xCoord.size() - 1) {
        break;
      }
      for (double y : yCoord) {
        if (yCoord.lastIndexOf(y) == yCoord.size() - 1) {
          break;
        }
        // System.out.println("y : " + y);
        IEnvelope rectangle = new GM_Envelope(x, xCoord.get(xCoord
            .lastIndexOf(x) + 1), y, yCoord.get(yCoord.lastIndexOf(y) + 1));
        for (IPolygon polygon : polygons) {
          JtsAlgorithms jtsAlgos = new JtsAlgorithms();
          IGeometry intersection = jtsAlgos.intersection(polygon,
              rectangle.getGeom());
          if (!intersection.isEmpty()) {
            if (intersection instanceof IPolygon) {
              toReturn.add((IPolygon) intersection);
              // newWaterAreas.add(new CdBWaterArea(,
              // ((CdBWaterArea) waterArea).isMaritime(),
              // ((CdBWaterArea) waterArea).getSymbo()));
            } else if (intersection instanceof IMultiSurface) {
              toReturn.addAll(((IMultiSurface<IPolygon>) intersection)
                  .getList());
              // for (IPolygon p : ((IMultiSurface<IPolygon>) intersection)
              // .getList()) {
              // toReturn.add(p);
              // newWaterAreas.add(new CdBWaterArea(polygon,
              // ((CdBWaterArea) waterArea).isMaritime(),
              // ((CdBWaterArea) waterArea).getSymbo()));
            }
          }
        }
      }
    }
    return toReturn;
  }
}
