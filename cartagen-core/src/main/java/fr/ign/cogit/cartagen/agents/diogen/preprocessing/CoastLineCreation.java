package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.algorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class CoastLineCreation {

  public static IFeatureCollection<ICoastLine> execute(
      IFeatureCollection<IWaterArea> waterAreas,
      AbstractCreationFactory factory) {
    return execute(waterAreas, 0., factory);
  }

  public static IFeatureCollection<ICoastLine> execute(
      IFeatureCollection<IWaterArea> waterAreas, double simplification,
      AbstractCreationFactory factory) {

    IFeatureCollection<ICoastLine> toReturn = new FT_FeatureCollection<ICoastLine>();

    List<IPolygon> list = new ArrayList<IPolygon>();
    for (IWaterArea waterArea : waterAreas) {
      list.add(waterArea.getGeom());
    }

    List<ILineString> listWaterLine = GetLinesFromPolygons.execute(list);
    VisvalingamWhyatt visvalingamWhyatt = new VisvalingamWhyatt(simplification);
    for (ILineString lineString : listWaterLine) {
      if (simplification > 0.0) {
        ILineString simplifyLine = visvalingamWhyatt.simplify(lineString);
        if (simplifyLine.coord().size() <= 2) {
          toReturn.add(factory.createCoastLine(lineString));
        } else {
          toReturn.add(factory.createCoastLine(simplifyLine));

        }
      } else {
        toReturn.add(factory.createCoastLine(lineString));
      }
    }

    return toReturn;
  }
}
