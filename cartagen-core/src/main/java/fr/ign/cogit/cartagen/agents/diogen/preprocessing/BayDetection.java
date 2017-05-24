package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.awt.Color;

import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.Bend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.BendSeries;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Create lines of the bays from a collection of ICoastLine
 * @author AMaudet
 * 
 */
public class BayDetection {

  private static GeometryPool geometryPool = null;

  public static GeometryPool getGeometryPool() {
    return geometryPool;
  }

  public static void setGeometryPool(GeometryPool geometryPool) {
    BayDetection.geometryPool = geometryPool;
  }

  public static IFeatureCollection<ICoastLine> execute(
      IFeatureCollection<ICoastLine> coastLines,
      AbstractCreationFactory factory) {

    IFeatureCollection<ICoastLine> toReturn = new FT_FeatureCollection<ICoastLine>();

    for (ICoastLine coastLine : coastLines) {
      ILineString lineString = coastLine.getGeom();
      BendSeries bendSeries = new BendSeries(lineString, 375);

      if (bendSeries.getBends() == null || bendSeries.getBends().isEmpty()) {
        continue;
      }
      IDirectPosition pointA = bendSeries.getBends().get(0).getGeom().coord()
          .get(0);
      IDirectPosition pointB = bendSeries.getBends().get(0).getBendSummit();
      IDirectPosition pointC = bendSeries.getBends().get(0).getGeom().coord()
          .get(bendSeries.getBends().get(0).getGeom().coord().size() - 1);

      // Calculation of the coordinate of c1-c0 vector
      double x10 = pointA.getX() - pointB.getX();
      double y10 = pointA.getY() - pointB.getY();

      // Calculation of the coordinate of c1-c0 vector
      double x12 = pointC.getX() - pointB.getX();
      double y12 = pointC.getY() - pointB.getY();

      double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);

      boolean bay = false;

      if (angle > 0) {
        bay = true;
      }

      IDirectPositionList positions = new DirectPositionList();
      for (Bend bend : bendSeries.getBends()) {
        for (IDirectPosition p : bend.getGeom().coord()) {
          if (positions.isEmpty()
              || !p.equals(positions.get(positions.size() - 1))) {
            positions.add(p);
          }
          if (bend.getBendSummit().equals2D(p, 0.01)) {
            if (bay) {
              bay = false;
            } else {
              ILineString newLineString = new GM_LineString(positions);
              toReturn.add(factory.createBayLine(newLineString));
              positions = new DirectPositionList();
              positions.add(p);
              bay = true;
              if (geometryPool != null) {
                Color randomColor = new Color((float) Math.random(),
                    (float) Math.random(), (float) Math.random());
                geometryPool.addFeatureToGeometryPool(newLineString,
                    randomColor, 6);
              }
            }
          }
        }
      }
      ILineString newLineString = new GM_LineString(positions);
      toReturn.add(factory.createBayLine(newLineString));
      if (geometryPool != null) {
        Color randomColor = new Color((float) Math.random(),
            (float) Math.random(), (float) Math.random());
        geometryPool.addFeatureToGeometryPool(newLineString, randomColor, 6);

        for (IDirectPosition inflectionPoint : bendSeries.getInflectionPts()) {
          geometryPool.addFeatureToGeometryPool(inflectionPoint.toGM_Point(),
              Color.RED, 1);
        }

        for (IDirectPosition summit : bendSeries.getAllSummits()) {
          geometryPool.addFeatureToGeometryPool(summit.toGM_Point(),
              Color.GREEN, 1);
        }
      }
    }

    return toReturn;
  }

}
