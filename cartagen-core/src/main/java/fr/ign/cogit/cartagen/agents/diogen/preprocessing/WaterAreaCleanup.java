package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class WaterAreaCleanup {

  private static double step = 500.;

  public static IFeatureCollection<IWaterArea> execute(
      IFeatureCollection<IWaterArea> waterAreas, IEnvelope envelope,
      double minX, double minY, double maxX, double maxY,
      AbstractCreationFactory factory) {

    IFeatureCollection<IWaterArea> toReturn = new FT_FeatureCollection<IWaterArea>();
    toReturn.addAll(waterAreas);

    Map<IWaterArea, Set<IPolygon>> map = new Hashtable<IWaterArea, Set<IPolygon>>();
    // List<Double> xCoord = new ArrayList<Double>();
    // xCoord.add(envelope.getLowerCorner().getX());
    // xCoord.add(minX);
    // xCoord.add(maxX);
    // xCoord.add(envelope.getUpperCorner().getX());
    //
    // List<Double> yCoord = new ArrayList<Double>();
    // yCoord.add(envelope.getLowerCorner().getY());
    // yCoord.add(minY);
    // yCoord.add(maxY);
    // yCoord.add(envelope.getUpperCorner().getY());

    for (IWaterArea waterArea : waterAreas) {
      Set<IPolygon> geoms = new HashSet<IPolygon>();
      geoms.add(waterArea.getGeom());
      // map.put(waterArea, DividePolygons.execute(geoms, xCoord, yCoord));
      map.put(waterArea,
          DividePolygons.execute(geoms, envelope.getLowerCorner().getX(),
              envelope.getUpperCorner().getX(),
              envelope.getLowerCorner().getY(),
              envelope.getUpperCorner().getY(), step));
    }

    // Suppress waterareas with geometries outside the given range
    Set<IWaterArea> waterAreaToRemove = new HashSet<IWaterArea>();
    Set<IWaterArea> waterAreaToAdd = new HashSet<IWaterArea>();
    for (IWaterArea waterArea : waterAreas) {
      Set<IPolygon> toRemove = new HashSet<IPolygon>();
      for (IPolygon poly : map.get(waterArea)) {
        IEnvelope waterAreaEnvelope = poly.envelope();
        if (waterAreaEnvelope.getLowerCorner().getX() > maxX
            || waterAreaEnvelope.getLowerCorner().getY() > maxY
            || waterAreaEnvelope.getUpperCorner().getX() < minX
            || waterAreaEnvelope.getUpperCorner().getY() < minY) {
          toRemove.add(poly);
        }
      }
      map.get(waterArea).removeAll(toRemove);
      if (map.get(waterArea).isEmpty()) {
        waterAreaToRemove.add(waterArea);
      } else {
        IGeometry united = JtsAlgorithms
            .union(new ArrayList<IPolygon>(map.get(waterArea)));
        if (united.isMultiSurface()) {
          waterAreaToRemove.add(waterArea);
          for (IPolygon p : ((IMultiSurface<IPolygon>) united).getList()) {
            waterAreaToAdd.add(factory.createWaterArea(p));
          }
        } else {
          waterArea.setGeom(united);
        }
      }
    }
    toReturn.removeAll(waterAreaToRemove);
    toReturn.addAll(waterAreaToAdd);
    return toReturn;
  }
}
