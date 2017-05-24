package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class StrokeSymbol {

  public static IGeometry getSymbolExtentWithCarriedObjects(
      IHikingRouteStroke stroke, boolean left) {

    ILineString g = getOffsetWithCarriedObjects(stroke, left);

    if (g.coord().get(0).distance(stroke.getGeom().coord().get(0)) < g.coord()
        .get(0).distance(stroke.getGeom().coord()
            .get(stroke.getGeom().coord().size() - 1))) {
      g.coord().inverseOrdre();
    }

    IDirectPositionList set = (IDirectPositionList) stroke.getGeom().coord()
        .clone();

    for (IDirectPosition position : set) {
      g.coord().add(position);
    }
    g.coord().add(g.coord().get(0));
    return new GM_Polygon(g);
  }

  public static ILineString getOffsetWithCarriedObjects(
      IHikingRouteStroke stroke, boolean left) {

    // ICarrierNetworkSection section = (ICarrierNetworkSection)
    // stroke.getRoads()
    // .iterator().next();

    // Compute the size of the buffer
    double distance = Math.abs(stroke.distance(left));
    // System.out.println("distance " + distance);

    double offset = (stroke.getWidth() / 2 + distance)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000 + 6;
    offset *= left ? 1 : -1;

    IMultiCurve<ILineString> multiCurve = JtsAlgorithms
        .offsetCurve(stroke.getGeom(), offset);

    // System.out.println("multiCurve " + multiCurve);
    ILineString g = multiCurve.get(0);

    return g;
  }
}
