package fr.ign.cogit.cartagen.algorithms.section;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.Bend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.BendSeries;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * This class allows to trigger continuous versions of algorithms dedicated to
 * bend series like accordion (Plazanet 96).
 * @author GTouya
 * 
 */
public class BendSeriesContinuousAlgorithm {

  private BendSeries bendSeries;
  private Set<Bend> bendsToKeep;
  @SuppressWarnings("unused")
  private double innerWidth, casingWidth;
  private boolean debugMode = false;
  private GeometryPool pool;

  public BendSeriesContinuousAlgorithm(BendSeries bendSeries, double innerWidth,
      double casingWidth) {
    super();
    this.bendSeries = bendSeries;
    this.innerWidth = innerWidth;
    this.casingWidth = casingWidth;
  }

  public BendSeries getBendSeries() {
    return bendSeries;
  }

  public void setBendSeries(BendSeries bendSeries) {
    this.bendSeries = bendSeries;
  }

  public Set<Bend> getBendsToKeep() {
    return bendsToKeep;
  }

  public void setBendsToKeep(Set<Bend> bendsToKeep) {
    this.bendsToKeep = bendsToKeep;
  }

  /**
   * Implementation of the bend series schematisation algorithm (Lecordix et al
   * 1997).
   * @return
   */
  public List<ILineString> accordion(int nbSteps) {
    // initialisation
    List<ILineString> continuousList = new ArrayList<>();

    // first, get the middle of the bend series (the generalised line will be
    // translated back to this point.
    IDirectPosition lineMiddle = Operateurs.milieu(bendSeries.getGeom());
    Map<Bend, Vector2D> mapOrientations = new HashMap<>();
    Map<Bend, IDirectPositionList> genBendsMap = new HashMap<Bend, IDirectPositionList>();

    if (debugMode)
      System.out.println(bendSeries.getBends().size() + " bends in the series");
    for (Bend bend : bendSeries.getBends()) {
      // first find point P (Plazanet 96) to compute the distortion vector
      Vector2D vector = findVectorForAccordion(bend);
      mapOrientations.put(bend, vector);
      if (vector == null) {
        // if P is not find, do not enlarge the bend
        genBendsMap.put(bend, bend.getGeom().coord());
        continue;
      }
      if (debugMode)
        pool.addVectorToGeometryPool(vector, bend.getGeom().endPoint(),
            Color.DARK_GRAY, 2);
    }

    for (int i = 0; i < nbSteps; i++) {
      IDirectPositionList generalisedBends = new DirectPositionList();
      // ************************************************************************************
      // first, enlarge each bend individually
      for (Bend bend : bendSeries.getBends()) {
        Vector2D vector = mapOrientations.get(bend);
        if (vector == null) {
          genBendsMap.put(bend, bend.getGeom().coord());
          continue;
        }
        double scalarProd = vector.prodScalaire(new Vector2D(
            bend.getGeom().startPoint(), bend.getGeom().endPoint()));
        if (scalarProd < 0.0)
          vector = vector.opposite();

        IPolygon bendPolygon = bend.closeBend();
        boolean clockWise = JTSAlgorithms.isClockwise(bendPolygon);
        Vector2D as = new Vector2D(bend.getGeom().startPoint(),
            bend.getBendSummit());
        Vector2D ab = new Vector2D(bend.getGeom().startPoint(),
            bend.getGeom().endPoint());
        double prodVect = as.getX() * ab.getY() - as.getY() * ab.getX();
        if (clockWise && prodVect > 0) {
          vector = vector.opposite();
        } else if (!clockWise && prodVect < 0) {
          vector = vector.opposite();
        }

        IDirectPositionList generalisedBend = new DirectPositionList();
        double totalLength = 0.0;
        double lineLength = bend.getGeom().length();
        int nbPt = bend.getGeom().coord().size();
        for (int j = nbPt - 1; j >= 0; j--) {
          IDirectPosition pt = bend.getGeom().coord().get(j);
          if (j == nbPt - 1) {
            Vector2D smallVect = new Vector2D(vector.getX(), vector.getY());
            smallVect.scalarMultiplication((double) (i + 1) / (nbSteps + 1));
            generalisedBend.add(smallVect.translate(pt));
            if (debugMode)
              pool.addVectorToGeometryPool(smallVect, pt, Color.PINK, 2);
            continue;
          }
          totalLength += pt.distance2D(bend.getGeom().coord().get(j + 1));
          Vector2D distortionVector = vector.copy();
          distortionVector.scalarMultiplication((lineLength - totalLength)
              * (i + 1) / (lineLength * (nbSteps + 1)));
          generalisedBend.add(0, distortionVector.translate(pt));
        }
        genBendsMap.put(bend, generalisedBend);
        if (debugMode)
          pool.addFeatureToGeometryPool(new GM_LineString(generalisedBend),
              Color.GREEN, 2);
      }

      // ************************************************************************************
      // then, join back the enlarged bends
      // first, make sure the bends are properly sorted
      IDirectPosition lastEnd = null;
      for (Bend bend : bendSeries.getBends()) {
        if (generalisedBends.size() == 0) {
          generalisedBends.addAll(genBendsMap.get(bend));
          lastEnd = generalisedBends.get(generalisedBends.size() - 1);
          continue;
        }
        // first translate the currentBend start to last end point
        IDirectPosition firstPt = bend.getGeom().startPoint();
        double dx = lastEnd.getX() - firstPt.getX();
        double dy = lastEnd.getY() - firstPt.getY();
        IDirectPositionList translatedPts = CommonAlgorithms
            .translation(genBendsMap.get(bend), dx, dy);
        generalisedBends.addAll(translatedPts);
        // update the last end point
        lastEnd = translatedPts.get(translatedPts.size() - 1);
      }
      ILineString genLine = new GM_LineString(generalisedBends);

      // ************************************************************************************
      // finally, translate the generalised line to the initial middle point of
      // the line
      IDirectPosition finalMiddle = Operateurs.milieu(genLine);
      double dx = lineMiddle.getX() - finalMiddle.getX();
      double dy = lineMiddle.getY() - finalMiddle.getY();
      if (debugMode)
        pool.addVectorToGeometryPool(new Vector2D(dx, dy), lineMiddle,
            Color.BLACK, 2);

      ILineString line = new GM_LineString(
          CommonAlgorithms.translation(generalisedBends, dx, dy));
      continuousList.add(line);
    }
    return continuousList;
  }

  /**
   * Compute the distortion vector of the accordion algorithm. It involves
   * finding the orthogonal projection to bend orientation of the first point of
   * the bend. If no such point can be found, the search is tried with the
   * inverse line (Plazanet 96).
   * @param bend
   * @return an {@link Object} array with the point P at position 0 and the
   *         length of segment AP at position 1
   */
  private Vector2D findVectorForAccordion(Bend bend) {
    double orientation = bend.getOrientation();
    if (debugMode)
      System.out.println("bend orientation: " + orientation * 180 / Math.PI);
    double width = bend.getWidth();
    IDirectPosition startPt = bend.getGeom().startPoint();
    IDirectPositionList coordList = new DirectPositionList();
    coordList.addAll(bend.getGeom().coord());
    coordList.remove(0);
    Angle angle = new Angle(orientation - Math.PI / 2);
    if (orientation < Math.PI)
      angle = new Angle(orientation + Math.PI / 2);
    if (debugMode)
      System.out
          .println("vector orientation: " + angle.getValeur() * 180 / Math.PI);

    Vector2D vector = new Vector2D(angle, width * 2);
    ILineSegment segment = new Segment(startPt, vector.translate(startPt))
        .extendAtExtremities(width * 5);
    IGeometry intersection = segment.intersection(new GM_LineString(coordList));
    if (intersection instanceof IPoint) {
      IDirectPosition p = ((IPoint) intersection).getPosition();
      double dist = p.distance2D(startPt);
      Vector2D normedVector = vector
          .changeNorm(Math.max(casingWidth - dist, 0.1));
      // if (normedVector.prodScalaire(new Vector2D(bend.getGeom().startPoint(),
      // bend.getGeom().endPoint())) > 0.0)
      // normedVector = normedVector.opposite();
      return normedVector;
    }

    coordList.clear();
    coordList.addAll(bend.getGeom().coord());
    coordList.remove(coordList.size() - 1);
    segment = new Segment(bend.getGeom().endPoint(),
        vector.translate(bend.getGeom().endPoint()))
            .extendAtExtremities(width * 5);
    intersection = segment.intersection(new GM_LineString(coordList));
    if (intersection instanceof IPoint) {
      IDirectPosition p = ((IPoint) intersection).getPosition();
      double dist = p.distance2D(bend.getGeom().endPoint());
      Vector2D normedVector = vector
          .changeNorm(Math.max(casingWidth - dist, 0.1));
      // if (normedVector.prodScalaire(new Vector2D(bend.getGeom().startPoint(),
      // bend.getGeom().endPoint())) < 0.0)
      // normedVector = normedVector.opposite();
      return normedVector;
    }

    return null;
  }

  /**
   * Compute of continuous version of the max break algorithm from S. Mustière
   * (see its PhD, 98). It enlarges the bend by computing an offset line by half
   * the external symbol width.
   * @return
   */
  public List<ILineString> continuousMaxBreak(int nbSteps, Bend bend) {
    List<ILineString> lines = new ArrayList<>();
    // first, find the side of the bend
    Side side = bend.getBendSide();
    double offset = casingWidth / 2;

    if (side.equals(Side.LEFT))
      offset = -offset;

    for (int i = 0; i < nbSteps; i++) {
      double offsetStep = offset * (i + 1) / nbSteps;
      ILineString offsetLine = null;
      System.out.println(offsetStep);
      IMultiCurve<ILineString> multiLine = JtsAlgorithms
          .offsetCurve(bend.getGeom(), offsetStep);
      if (multiLine == null || multiLine.size() == 0) {
        offsetLine = BufferComputing.buildHalfOffsetLine(side.inverse(),
            bend.getGeom(), Math.abs(offsetStep));
      } else {
        offsetLine = multiLine.get(0);
      }
      lines.add(offsetLine);
    }
    return lines;
  }

  /**
   * Put the algorithm in debug mode, it displays intermediate geometries in the
   * geometry pool.
   */
  public void setDebugMode(GeometryPool pool) {
    this.debugMode = true;
    this.pool = pool;
  }
}
