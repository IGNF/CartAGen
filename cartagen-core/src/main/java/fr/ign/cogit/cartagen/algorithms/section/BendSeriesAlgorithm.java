package fr.ign.cogit.cartagen.algorithms.section;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.Bend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.BendSeries;
import fr.ign.cogit.cartagen.util.comparators.DoubleMapComparator;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * This class allows to trigger algorithms dedicated to bend series like
 * schematisation (Lecordix et al 97), or accordion (Plazanet 96).
 * @author GTouya
 * 
 */
public class BendSeriesAlgorithm {

  private BendSeries bendSeries;
  private Set<Bend> bendsToKeep;
  @SuppressWarnings("unused")
  private double innerWidth, casingWidth;
  private boolean debugMode = false;
  private GeometryPool pool;

  public BendSeriesAlgorithm(BendSeries bendSeries, double innerWidth,
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
  public ILineString schematisation() {
    bendsToKeep = new HashSet<Bend>();
    // first, identifies the bends to keep, i.e. the first, the last and the
    // ones much bigger than the others.
    if (bendSeries.getBends().size() < 3)
      return bendSeries.getGeom();
    if (bendSeries.getBends().size() > 3) {
      bendsToKeep.add(bendSeries.getBends().get(0));
      bendsToKeep
          .add(bendSeries.getBends().get(bendSeries.getBends().size() - 1));
    }
    Map<Bend, Double> bendIndices = new HashMap<Bend, Double>();
    Map<Bend, Double> bendSizes = new HashMap<Bend, Double>();
    // compute the total of bend measures to normalize them afterwards.
    double lengthTotal = 0.0;
    double shapeTotal = 0.0;
    double sizeTotal = 0.0;
    for (Bend bend : bendSeries.getBends()) {
      lengthTotal += bend.getLength();
      shapeTotal += bend.getShapeMeasure();
      sizeTotal += bend.getSizeMeasure();
    }

    List<Bend> bendsToRemoveIndex = new ArrayList<Bend>();
    List<Bend> bendsToRemoveSize = new ArrayList<Bend>();
    for (Bend bend : bendSeries.getBends()) {
      if (bendsToKeep.contains(bend))
        continue;
      bendsToRemoveIndex.add(bend);
      bendsToRemoveSize.add(bend);
      bendSizes.put(bend, bend.getSizeMeasure() / sizeTotal);
      if (bendSeries.getBends().size() < 6)
        bendIndices.put(bend,
            (2.0 * bend.getShapeMeasure() / shapeTotal
                + bend.getSizeMeasure() / sizeTotal
                + bend.getLength() / lengthTotal) / 4.0);
      else {
        if (bendSeries.getBends().size() < 11)
          bendIndices.put(bend,
              (bend.getShapeMeasure() / shapeTotal
                  + bend.getSizeMeasure() / sizeTotal
                  + bend.getLength() / lengthTotal + bend.getSymmetry()) / 4.0);
        else
          bendIndices.put(bend,
              (bend.getShapeMeasure() / shapeTotal
                  + 2.0 * bend.getSizeMeasure() / sizeTotal
                  + 0.5 * bend.getLength() / lengthTotal
                  + 0.5 * bend.getSymmetry()) / 4.0);
      }
    }
    // sort bends according to the index
    Collections.sort(bendsToRemoveIndex,
        new DoubleMapComparator<Bend>(bendIndices));
    Collections.sort(bendsToRemoveSize,
        new DoubleMapComparator<Bend>(bendSizes));
    // update the bends to keep in specific cases
    if (bendSeries.getBends().size() > 4) {
      if (bendSeries.getBends().size() == 5)
        bendsToKeep.clear();
      // forbid the removal of the biggest bend
      bendsToKeep.add(bendsToRemoveSize.get(bendsToRemoveSize.size() - 1));
      bendsToRemoveIndex
          .remove(bendsToRemoveSize.get(bendsToRemoveSize.size() - 1));
    }

    // ***********************************************************************
    // now, choose the bends to remove (two consecutive bends) according to
    // height, width, length and symmetry of bends (summarized in the index).
    Bend bend1 = null, bend2 = null;
    int bend1Index = 0, bend2Index = 0;
    // first, if there are not enough bends, do nothing (schematisation is not
    // the good algorithm)
    if (bendsToRemoveIndex.size() < 2)
      return bendSeries.getGeom();
    if (bendsToRemoveIndex.size() == 2) {
      bend1 = bendsToRemoveIndex.get(0);
      bend2 = bendsToRemoveIndex.get(1);
    } else {

      Bend b1 = bendsToRemoveIndex.get(0);
      for (int j = 1; j < bendsToRemoveIndex.size(); j++) {
        int currentBendNb = bendSeries.getBends()
            .indexOf(bendsToRemoveIndex.get(j));
        if (currentBendNb == bendSeries.getBends().indexOf(b1) + 1
            || currentBendNb == bendSeries.getBends().indexOf(b1) - 1) {
          bend1 = b1;
          bend2 = bendsToRemoveIndex.get(j);
          bend1Index = bendSeries.getBends().indexOf(bend1);
          bend2Index = bendSeries.getBends().indexOf(bend2);
          break;
        }
      }
    }

    if (debugMode) {
      System.out.println("bend1 index: " + bend1Index);
      System.out.println(bend1.getGeom());
      System.out.println("bend2 index: " + bend2Index);
      System.out.println(bend2.getGeom());
    }
    // *************************************************
    // then, join the remaining bends

    // first, cut the bend series into two parts, by removing the chosen bends
    IDirectPositionList part1 = new DirectPositionList();
    IDirectPositionList part2 = new DirectPositionList();
    // also store all summits in each part for further use
    Set<IDirectPosition> part1Summits = new HashSet<IDirectPosition>();
    Set<IDirectPosition> part2Summits = new HashSet<IDirectPosition>();
    for (int i = 0; i < bendSeries.getBends().size(); i++) {
      Bend bend = bendSeries.getBends().get(i);
      if (i < bend1Index) {
        part1Summits.add(bend.getBendSummit());
        if (part1.size() == 0)
          part1.addAll(bend.getGeom().coord());
        else {
          for (int j = 1; j < bend.getGeom().coord().size(); j++) {
            part1.add(bend.getGeom().coord().get(j));
          }
        }
        continue;
      }
      if (i > bend2Index) {
        part2Summits.add(bend.getBendSummit());
        if (part2.size() == 0)
          part2.addAll(bend.getGeom().coord());
        else {
          for (int j = 1; j < bend.getGeom().coord().size(); j++) {
            part2.add(bend.getGeom().coord().get(j));
          }
        }
      }
    }

    if (debugMode) {
      for (IDirectPosition pt : part1)
        pool.addFeatureToGeometryPool(pt.toGM_Point(), Color.YELLOW, 1);
      for (IDirectPosition pt : part2)
        pool.addFeatureToGeometryPool(pt.toGM_Point(), Color.BLUE, 1);
    }

    // compute the joining point. It's the middle of part1 end point and part2
    // start point, weighted by part lengths.
    double part1Length = new GM_LineString(part1).length();
    double part2Length = new GM_LineString(part2).length();
    double lengthRatio = part1Length / part2Length;
    Segment joiningSeg = new Segment(part1.get(part1.size() - 1), part2.get(0));
    IDirectPosition joiningPt = joiningSeg.getWeightedMiddlePoint(lengthRatio);
    Vector2D vect1 = new Vector2D(joiningSeg.getStartPoint(), joiningPt);
    Vector2D vect2 = new Vector2D(part2.get(0), joiningPt);

    if (debugMode) {
      System.out.println("joiningPt: " + joiningPt);
    }

    // now the deformation to join bend series parts has to be cushioned.
    // cushion is made in two directions.
    // cushion part1 first
    double mainDirection1 = new Segment(part1.get(part1.size() - 2),
        part1.get(part1.size() - 1)).orientation();
    double perpDirection1 = mainDirection1 + Math.PI / 2;
    if (perpDirection1 > 2 * Math.PI)
      perpDirection1 -= 2 * Math.PI;
    // project vect1 in both directions
    Vector2D vect1Main = vect1.project(mainDirection1);
    Vector2D vect1Perp = vect1.project(perpDirection1);
    if (debugMode) {
      pool.addVectorToGeometryPool(vect1Main, joiningPt, Color.BLUE, 2);
      pool.addVectorToGeometryPool(vect1Perp, joiningPt, Color.CYAN, 2);
      System.out.println("mainDirection1: " + mainDirection1);
      System.out.println("perpDirection1: " + perpDirection1);
    }

    // identify the last half bend of part1
    Set<IDirectPosition> lastHalfBend = new HashSet<IDirectPosition>();
    double halfBendLength = 0.0;
    for (int i = part1.size() - 2; i >= 0; i--) {
      IDirectPosition pt = part1.get(i);
      halfBendLength += pt.distance2D(part1.get(i + 1));
      if (part1Summits.contains(pt))
        break;
      lastHalfBend.add(pt);
    }
    // compute the part1 cushioned version
    IDirectPositionList generalisedBends = new DirectPositionList();
    double totalLength = 0.0;
    for (int i = part1.size() - 2; i >= 0; i--) {
      IDirectPosition pt = part1.get(i);
      totalLength += pt.distance2D(part1.get(i + 1));
      IDirectPosition proj = pt;
      // cushion last half bend only in the main direction
      if (lastHalfBend.contains(pt)) {
        Vector2D vect = vect1Main
            .changeNorm(vect1Main.norme() * (1 - totalLength / halfBendLength));
        proj = vect.translate(proj);
      }

      // cushion in the perpendicular direction
      Vector2D vect = vect1Perp
          .changeNorm(vect1Perp.norme() * (1 - totalLength / part1Length));
      proj = vect.translate(proj);
      generalisedBends.add(0, proj);
    }

    // add the joining vertex
    generalisedBends.add(joiningPt);

    // then, do the same thing on part2
    double mainDirection2 = new Segment(part2.get(0), part2.get(1))
        .orientation();
    double perpDirection2 = mainDirection2 + Math.PI / 2;
    if (perpDirection2 > 2 * Math.PI)
      perpDirection2 -= 2 * Math.PI;
    // project vect2 in both directions
    Vector2D vect2Main = vect2.project(mainDirection2);
    Vector2D vect2Perp = vect2.project(perpDirection2);
    if (debugMode) {
      pool.addVectorToGeometryPool(vect2Main, joiningPt, Color.PINK, 2);
      pool.addVectorToGeometryPool(vect2Perp, joiningPt, Color.ORANGE, 2);
      System.out.println("mainDirection2: " + mainDirection2);
      System.out.println("perpDirection2: " + perpDirection2);
    }

    // identify the first half bend of part2
    Set<IDirectPosition> firstHalfBend = new HashSet<IDirectPosition>();
    halfBendLength = 0.0;
    for (int i = 1; i < part2.size() - 1; i++) {
      IDirectPosition pt = part2.get(i);
      halfBendLength += pt.distance2D(part2.get(i - 1));
      if (part1Summits.contains(pt))
        break;
      firstHalfBend.add(pt);
    }
    // compute the part2 cushioned version
    totalLength = 0.0;
    for (int i = 1; i < part2.size() - 1; i++) {
      IDirectPosition pt = part2.get(i);
      totalLength += pt.distance2D(part2.get(i - 1));
      IDirectPosition proj = pt;
      // cushion last half bend only in the main direction
      if (firstHalfBend.contains(pt)) {
        Vector2D vect = vect2Main
            .changeNorm(vect2Main.norme() * (1 - totalLength / halfBendLength));
        proj = vect.translate(proj);
      }

      // cushion in the perpendicular direction
      Vector2D vect = vect2Perp
          .changeNorm(vect2Perp.norme() * (1 - totalLength / part2Length));
      proj = vect.translate(proj);
      generalisedBends.add(proj);
    }
    generalisedBends.add(part2.get(part2.size() - 1));

    return new GM_LineString(generalisedBends);

  }

  /**
   * Implementation of the bend series schematisation algorithm (Lecordix et al
   * 1997).
   * @return
   */
  public ILineString accordion() {
    // initialisation
    IDirectPositionList generalisedBends = new DirectPositionList();
    Map<Bend, IDirectPositionList> genBendsMap = new HashMap<Bend, IDirectPositionList>();

    // first, get the middle of the bend series (the generalised line will be
    // translated back to this point.
    IDirectPosition lineMiddle = Operateurs.milieu(bendSeries.getGeom());

    // ************************************************************************************
    // first, enlarge each bend individually
    if (debugMode)
      System.out.println(bendSeries.getBends().size() + " bends in the series");
    for (Bend bend : bendSeries.getBends()) {
      // first find point P (Plazanet 96) to compute the distortion vector
      Vector2D vector = findVectorForAccordion(bend);
      if (vector == null) {
        // if P is not find, do not enlarge the bend
        genBendsMap.put(bend, bend.getGeom().coord());
        continue;
      }
      double scalarProd = vector.prodScalaire(
          new Vector2D(bend.getGeom().startPoint(), bend.getGeom().endPoint()));
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

      if (debugMode)
        pool.addVectorToGeometryPool(vector, bend.getGeom().endPoint(),
            Color.DARK_GRAY, 2);
      IDirectPositionList generalisedBend = new DirectPositionList();
      double totalLength = 0.0;
      double lineLength = bend.getGeom().length();
      int nbPt = bend.getGeom().coord().size();
      for (int i = nbPt - 1; i >= 0; i--) {
        IDirectPosition pt = bend.getGeom().coord().get(i);
        if (i == nbPt - 1) {
          generalisedBend.add(vector.translate(pt));
          continue;
        }
        totalLength += pt.distance2D(bend.getGeom().coord().get(i + 1));
        Vector2D distortionVector = vector.copy();
        distortionVector
            .scalarMultiplication((lineLength - totalLength) / lineLength);
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

    return new GM_LineString(
        CommonAlgorithms.translation(generalisedBends, dx, dy));
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
        .extendAtExtremities(width * 2);
    IGeometry intersection = segment.intersection(new GM_LineString(coordList));
    if (intersection instanceof IPoint) {
      IDirectPosition p = ((IPoint) intersection).getPosition();
      double dist = p.distance2D(startPt);
      return vector.changeNorm(casingWidth - dist);
    }

    coordList.clear();
    coordList.addAll(bend.getGeom().coord());
    coordList.remove(coordList.size() - 1);
    segment = new Segment(bend.getGeom().endPoint(),
        vector.translate(bend.getGeom().endPoint()))
            .extendAtExtremities(width * 2);
    intersection = segment.intersection(new GM_LineString(coordList));
    if (intersection instanceof IPoint) {
      IDirectPosition p = ((IPoint) intersection).getPosition();
      double dist = p.distance2D(bend.getGeom().endPoint());
      return vector.changeNorm(casingWidth - dist);
    }

    return null;
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
