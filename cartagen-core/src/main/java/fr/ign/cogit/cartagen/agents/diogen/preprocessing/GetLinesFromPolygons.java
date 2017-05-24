package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class GetLinesFromPolygons {
  public static List<ILineString> execute(List<IPolygon> list) {

    IGeometry united = JtsAlgorithms.union(list);
    List<ILineString> toReturn = new ArrayList<ILineString>();
    if (united instanceof IMultiSurface) {
      IMultiSurface<IPolygon> multiSurface = ((IMultiSurface<IPolygon>) united);
      for (int i = 0; i < multiSurface.size(); i++) {
        // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        // .addFeatureToGeometryPool(multiSurface.get(i), Color.YELLOW, 1);
        toReturn.addAll(createLines(multiSurface.get(i)));
      }
    } else {
      IPolygon p = (IPolygon) united;
      toReturn.addAll(createLines(p));
    }
    return toReturn;
  }

  private static Set<ILineString> createLines(IPolygon polygon) {
    Set<ILineString> toReturn = new HashSet<ILineString>();
    toReturn.addAll(splitCoords(polygon.getExterior().coord()));
    //
    // CartAGenDoc
    // .getInstance()
    // .getCurrentDataset()
    // .getGeometryPool()
    // .addFeatureToGeometryPool(
    // new GM_LineString(polygon.getExterior().coord()), Color.RED, 1);
    for (IRing ring : polygon.getInterior()) {
      toReturn.addAll(splitCoords(ring.coord()));
      // CartAGenDoc
      // .getInstance()
      // .getCurrentDataset()
      // .getGeometryPool()
      // .addFeatureToGeometryPool(new GM_LineString(ring.coord()),
      // Color.RED, 1);
    }
    return toReturn;
  }

  private static double threshold = 80;

  private static Set<ILineString> splitCoords(IDirectPositionList coords) {
    Set<ILineString> toReturn = new HashSet<ILineString>();
    ILineString firstLineString = null;
    ILineString lastLineString = null;
    IDirectPositionList currentPositions = new DirectPositionList();
    IDirectPosition lastCoord = null;
    for (IDirectPosition directPosition : coords) {
      if (lastCoord != null) {
        if (directPosition.distance(lastCoord) > threshold
            && (directPosition.getX() == lastCoord.getX() || directPosition
                .getY() == lastCoord.getY())) {
          if (currentPositions.size() > 1) {
            lastLineString = new GM_LineString(currentPositions);
            toReturn.add(lastLineString);
            if (firstLineString == null) {
              firstLineString = lastLineString;
            }
            currentPositions = new DirectPositionList();
          } else {
            currentPositions.clear();

          }
        }
      }
      currentPositions.add(directPosition);
      lastCoord = directPosition;
    }

    lastLineString = new GM_LineString(currentPositions);
    toReturn.add(lastLineString);

    if (firstLineString != null) {
      if (lastCoord.equals(firstLineString.coord().get(0))) {
        currentPositions = new DirectPositionList();
        currentPositions.addAll(lastLineString.coord());
        currentPositions.addAll(firstLineString.coord());
        toReturn.remove(firstLineString);
        toReturn.remove(lastLineString);
        toReturn.add(new GM_LineString(currentPositions));
      }
    }
    return toReturn;
  }
}
