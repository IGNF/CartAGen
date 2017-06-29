package fr.ign.cogit.cartagen.algorithms.section;

import java.awt.Color;

import fr.ign.cogit.cartagen.algorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.graph.TreeGraph;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.Bend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.contrib.graphe.GraphPath;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

/**
 * Class that triggers generalisation algorithms on line bends (e.g. "max break"
 * algorithm).
 * @author GTouya
 * 
 */
public class BendAlgorithm {

  private Bend bend;
  private double innerWidth, casingWidth;
  private ILineString bendSkeleton;

  public BendAlgorithm(Bend bend, double innerWidth, double casingWidth) {
    super();
    this.bend = bend;
    this.innerWidth = innerWidth;
    this.casingWidth = casingWidth;
  }

  public Bend getBend() {
    return bend;
  }

  public void setBend(Bend bend) {
    this.bend = bend;
  }

  public ILineString getBendSkeleton() {
    return bendSkeleton;
  }

  public void setBendSkeleton(ILineString bendSkeleton) {
    this.bendSkeleton = bendSkeleton;
  }

  /**
   * Compute the max break algorithm from S. Mustière (see its PhD, 98). It
   * enlarges the bend by computing an offset line by half the external symbol
   * width.
   * @return
   */
  public ILineString maxBreak() {
    // first, find the side of the bend
    Side side = bend.getBendSide();
    double offset = casingWidth / 2;
    if (side.equals(Side.LEFT))
      offset = -offset;
    ILineString offsetLine = null;
    IMultiCurve<ILineString> multiLine = JtsAlgorithms
        .offsetCurve(bend.getGeom(), offset);
    if (multiLine == null || multiLine.size() == 0) {
      offsetLine = BufferComputing.buildHalfOffsetLine(side.inverse(),
          bend.getGeom(), Math.abs(offset));
    } else {
      offsetLine = multiLine.get(0);
    }

    return offsetLine;
  }

  /**
   * Compute the min break algorithm S. Mustière (see its PhD, 98).
   * @param dpThreshold the threshold for a Douglas&Peucker filtering applied on
   *          the skeleton of the bend.
   * @return
   */
  public ILineString minBreak(double dpThreshold) {
    // first, make a polygon of the bend
    IPolygon bendPolygon = bend.closeBend();

    // then, compute the TIN skeleton of the bend
    TreeGraph graph = Skeletonize.skeletonizeTINGraph(bendPolygon, -1);
    pool.addGraphToGeometryPool(graph, Color.DARK_GRAY, Color.ORANGE);
    GraphPath longestPath = graph.getLongestPathBetweenLeaves();

    // make sure the longest path is in the correct order, i.e. from to base to
    // bend summit.
    INode initialNode = longestPath.getInitialNode();
    double distInitial = initialNode.getPosition()
        .distance2D(bend.getGeom().startPoint())
        + initialNode.getPosition().distance2D(bend.getGeom().endPoint());
    INode finalNode = longestPath.getFinalNode();
    double distFinal = finalNode.getPosition()
        .distance2D(bend.getGeom().startPoint())
        + finalNode.getPosition().distance2D(bend.getGeom().endPoint());
    if (distFinal < distInitial)
      longestPath = longestPath.reverse();

    // compute the skeleton geometry and extend it at the bend base
    ILineString skeleton = Filtering
        .DouglasPeuckerLineString(longestPath.getPathGeometry(), dpThreshold);
    IDirectPosition middle = new Segment(bend.getGeom().startPoint(),
        bend.getGeom().endPoint()).getMiddlePoint();
    IDirectPositionList forOffset = new DirectPositionList();
    forOffset.add(0, middle);
    for (IDirectPosition pt : skeleton.coord())
      forOffset.add(pt);
    for (int i = skeleton.coord().size() - 2; i > 0; i--)
      forOffset.add(skeleton.coord().get(i));
    forOffset.add(middle);

    bendSkeleton = new GM_LineString(forOffset);

    // the offset is the innerWidth plus half the casing width
    double offset = innerWidth + casingWidth / 2;

    // compute the offset line
    ILineString offsetLine = null;
    IMultiCurve<ILineString> multiLine = JtsAlgorithms.offsetCurve(bendSkeleton,
        offset);
    if (multiLine == null || multiLine.size() == 0) {
      offsetLine = BufferComputing.buildHalfOffsetLine(Side.LEFT, bendSkeleton,
          offset);
    } else {
      offsetLine = multiLine.get(0);
    }

    return offsetLine;
  }

  private GeometryPool pool;

  public void setPool(GeometryPool pool) {
    this.pool = pool;
  }
}
