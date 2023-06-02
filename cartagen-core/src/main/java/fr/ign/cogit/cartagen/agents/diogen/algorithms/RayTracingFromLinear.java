package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.LineString;

import fr.ign.cogit.cartagen.core.defaultschema.misc.MiscLine;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class RayTracingFromLinear {

  // A set of sections from with the ray will be launched
  private Set<INetworkSection> sections;
  // = deadEnd.getFeatures()

  // the side to launch the rays
  private boolean onLeft;

  // the level of densification of the road
  private double step;

  // the max distance for launching the ray
  private double distance;

  // the object to consider for colision.
  private IFeatureCollection<?> limits;

  // show the line on the dataset or no.
  private boolean debug = false;

  // the maximum distance get by one of the rays before meeting an obstacle
  private double maxDistance;

  // All the feature from the limts collection collided by the ray associated to
  // their minimal distance from the lauching point
  private Map<IFeature, Double> overlappingFeatureMap = new Hashtable<IFeature, Double>();

  // A point of collision with its limit
  private Map<IDirectPosition, Double> conflictPointsMap = new Hashtable<IDirectPosition, Double>();

  private boolean parallelConflicts = false;

  private boolean orthogonalConflicts = false;

  private Vector2D root;

  public RayTracingFromLinear(Set<INetworkSection> sections, boolean onLeft,
      double step, double distance, IFeatureCollection<?> limits, Vector2D root,
      boolean debug) {
    this.sections = sections;
    this.onLeft = onLeft;
    this.step = step;
    this.distance = distance;
    this.limits = limits;
    this.debug = debug;
    this.root = root;
  }

  public RayTracingFromLinear(Set<INetworkSection> sections, boolean onLeft,
      double step, double distance, IFeatureCollection<?> limits,
      boolean debug) {
    this(sections, onLeft, step, distance, limits, null, debug);
  }

  public RayTracingFromLinear(Set<INetworkSection> sections, boolean onLeft,
      double step, double distance, IFeatureCollection<?> limits) {
    this(sections, onLeft, step, distance, limits, null, false);
  }

  /**
   * Instantiate the algorithm with debug unactivated
   * @param sections
   * @param onLeft
   * @param step
   * @param distance
   * @param limits
   */
  public RayTracingFromLinear(Set<INetworkSection> sections, boolean onLeft,
      double step, double distance, IFeatureCollection<?> limits,
      Vector2D root) {
    this(sections, onLeft, step, distance, limits, root, false);
  }

  /**
   * Compute method for the algorithm
   */
  public void compute() {

    if (sections == null) {
      maxDistance = distance;
      return;
    }

    if (sections.isEmpty()) {
      maxDistance = distance;
      return;
    }

    IFeatureCollection<IGeneObjLin> toShow;
    toShow = new FT_FeatureCollection<IGeneObjLin>();

    double result = distance;

    double threshold = Math.PI / 6;

    // boolean reverse = false;
    //
    // if (!deadEnd.getRoot().getGeom().coord().get(0)
    // .equals2D(deadEnd.getRootNode().getPosition())) {
    // reverse = true;
    // }

    for (INetworkSection feature : this.sections) {
      // Densify the line

      ILineString densifiedLine = null;
      try {
        densifiedLine = (ILineString) JtsGeOxygene
            .makeGeOxygeneGeom(LineDensification.densification(
                (LineString) JtsGeOxygene.makeJtsGeom(feature.getGeom(), true),
                step));
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }

      IDirectPositionList points = densifiedLine.coord();
      for (int i = 1; i < points.size() - 1; i++) {
        IDirectPosition pointA = points.get(i - 1);
        IDirectPosition pointO = points.get(i);
        IDirectPosition pointB = points.get(i + 1);

        // take 3 adjacent points and get the bisector of the angle
        Vector2D vectAO = new Vector2D(pointA, pointO);
        Vector2D vectOB = new Vector2D(pointO, pointB);

        vectAO = vectAO.changeNorm(distance);
        vectOB = vectOB.changeNorm(distance);

        double angle = vectAO.vectorAngle(vectOB);

        Vector2D bisector = null;
        if (angle == 0) {
          // choose orthogonal
          bisector = new Vector2D(-vectAO.getY(), vectAO.getX());
        } else {
          bisector = vectAO.add(vectOB);
          bisector = new Vector2D(-bisector.getY(), bisector.getX());
        }
        if (onLeft) {
          bisector = new Vector2D(-bisector.getX(), -bisector.getY());
        }
        bisector = bisector.changeNorm(distance + feature.getWidth() / 2);

        ILineString bisectorLine = lineFromVectorAndPoint(bisector, pointO);

        if (debug) {
          toShow.add(new MiscLine(bisectorLine));
        }

        for (IFeature f : limits) {
          if (f.getGeom().intersects(bisectorLine)) {
            IGeometry intersection = f.getGeom().intersection(bisectorLine);
            if (intersection instanceof GM_Point) {
              GM_Point intersectionPoint = (GM_Point) intersection;
              double measure = pointO.distance(intersectionPoint.getPosition());
              result = Math.min(result, measure);

              conflictPointsMap.put(intersectionPoint.getPosition(), result);

              // test if the feature is already crossed.
              if (overlappingFeatureMap.get(f) == null) {
                overlappingFeatureMap.put(f, new Double(measure));
              } else if (overlappingFeatureMap.get(f) > measure) {
                overlappingFeatureMap.put(f, new Double(measure));
              }

              // test the orientation of the lauched rayon with the root of the
              // section
              if (this.root != null) {
                double angleBetweenRootAndRay = this.root.angleVecteur(bisector)
                    .getValeur();
                if ((Math.abs(angleBetweenRootAndRay) < threshold)
                    || (Math.abs(angleBetweenRootAndRay - Math.PI) < threshold))
                  this.orthogonalConflicts = true;
                else
                  this.parallelConflicts = true;
              }

            }
          }
        }
      }
    }

    maxDistance = result;
    return;
  }

  /**
   * Get an object LineString resulting of the application of vector on point.
   * @param vector
   * @param point
   * @return
   */
  private ILineString lineFromVectorAndPoint(Vector2D vector,
      IDirectPosition point) {
    ILineString line = new GM_LineString();
    line.addControlPoint(point);
    line.addControlPoint(new DirectPosition(point.getX() + vector.getX(),
        point.getY() + vector.getY()));
    return line;
  }

  public Map<IFeature, Double> getOverlappingFeatureMap() {
    return overlappingFeatureMap;
  }

  public double getMaxDistance() {
    return maxDistance;
  }

  public Map<IDirectPosition, Double> getConflictPointsMap() {
    return conflictPointsMap;
  }

  public boolean isParallelConflicts() {
    return parallelConflicts;
  }

  public boolean isOrthogonalConflicts() {
    return orthogonalConflicts;
  }
}
