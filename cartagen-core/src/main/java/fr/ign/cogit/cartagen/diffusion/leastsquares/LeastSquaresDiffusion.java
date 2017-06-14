/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.diffusion.leastsquares;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.diffusion.AbstractDiffusionProcess;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.LSVectorDisplConstraint1;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSAngleConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCrossingConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCurvatureConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementDirConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSProximityConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSSegmentLengthConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSSideOrientConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSStiffnessConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * The least squares diffusion is a diffusion process based on the least squares
 * deformation engine used in generalisation or conflation. Here, the diffusion
 * is iterative: it is first computed on the neighbours "touching" the changed
 * features, than on the features that "touch" the features diffused in the
 * first step, until the diffusion is negligible. The default implementation of
 * "touching" is related to network diffusion so it returns the features
 * connected by the network.
 * @author GTouya
 * 
 */
public class LeastSquaresDiffusion extends AbstractDiffusionProcess {

  private MapspecsLS mapspecs;
  private int maxIterations = 4;

  public enum TouchingMode {
    NETWORK, DELAUNAY
  }

  private TouchingMode touchingMode = TouchingMode.NETWORK;

  public LeastSquaresDiffusion(MapspecsLS mapspecs) {
    super();
    this.mapspecs = mapspecs;
  }

  @Override
  public Map<IFeature, IGeometry> applyDiffusion(
      Map<IFeature, IGeometry> changedFeats,
      IFeatureCollection<? extends IFeature> neighbours) {
    // initialisation
    int i = 0;
    Map<IFeature, IGeometry> diffusedFeats = new HashMap<IFeature, IGeometry>();
    diffusedFeats.putAll(changedFeats);
    boolean diffuse = true;
    Set<IFeature> localChangedFeats = new HashSet<IFeature>();

    // loop for the iterative diffusion
    localChangedFeats.addAll(changedFeats.keySet());
    while (diffuse) {
      System.out.println("une itération");
      // first get the neighbours
      Map<IFeature, IDirectPosition> touchingNeighbours = null;
      if (touchingMode.equals(TouchingMode.NETWORK))
        touchingNeighbours = getTouchingNeighbours(localChangedFeats,
            neighbours, diffusedFeats);
      else if (touchingMode.equals(TouchingMode.DELAUNAY))
        touchingNeighbours = getDelaunayTouchingNeighbours(localChangedFeats,
            neighbours, diffusedFeats);
      System.out.println(touchingNeighbours.size() + " touching neighbours");
      Set<LocalFeatureDiffusion> localDiffusion = applyLocalDiffusion(
          localChangedFeats, touchingNeighbours, diffusedFeats);
      double maxDiffusion = 0.0;
      localChangedFeats.clear();
      for (LocalFeatureDiffusion f : localDiffusion) {
        double dist = f.getLastNode().distance2D(f.getNewLastNode());
        if (dist > maxDiffusion)
          maxDiffusion = dist;
        // update diffusedFeats
        diffusedFeats.put(f.getFeature(), f.getNewGeom());
        // diffuse network nodes
        if (f.getFeature() instanceof INetworkSection) {
          INetworkNode node = ((INetworkSection) f.getFeature()).getFinalNode();
          if (diffusedFeats.containsKey(node))
            node = ((INetworkSection) f.getFeature()).getInitialNode();
          diffusedFeats.put(node, new GM_Point(f.getNewLastNode()));
        }
        // update localChangedFeats
        if (dist >= getMinimumDisplacement())
          localChangedFeats.add(f.getFeature());
      }
      if (maxDiffusion < getMinimumDisplacement())
        diffuse = false;
      i++;
      if (i == maxIterations)
        diffuse = false;
    }
    return diffusedFeats;
  }

  private Set<LocalFeatureDiffusion> applyLocalDiffusion(
      Set<IFeature> changedFeats,
      Map<IFeature, IDirectPosition> touchingNeighbours,
      Map<IFeature, IGeometry> diffusedFeats) {
    mapspecs.setSelectedObjects(touchingNeighbours.keySet());
    Set<DefaultFeature> vectors = computeVectors(changedFeats, diffusedFeats);
    DiffusionLSScheduler sched = new DiffusionLSScheduler(mapspecs, vectors,
        LSVectorDisplConstraint1.class, getMinimumDisplacement());
    sched.setSolver(MatrixSolver.JAMA);
    // on lance la généralisation
    sched.triggerAdjustment();

    Set<LocalFeatureDiffusion> diffusions = new HashSet<LeastSquaresDiffusion.LocalFeatureDiffusion>();
    for (IFeature f : sched.getMapObjGeom().keySet()) {
      LSPoint pointIni = sched.getPointFromCoord(touchingNeighbours.get(f), f);
      IDirectPosition finalPt = f.getGeom().coord().get(0);
      if (finalPt.equals(touchingNeighbours.get(f)))
        finalPt = f.getGeom().coord().get(f.getGeom().coord().size() - 1);
      LSPoint pointFin = sched.getPointFromCoord(finalPt, f);
      diffusions.add(new LocalFeatureDiffusion(f, sched.getMapObjGeom().get(f),
          pointIni.getIniPt(), pointFin.getIniPt(), pointIni.getFinalPt(),
          pointFin.getFinalPt()));
    }
    return diffusions;
  }

  /**
   * Compute displacement vectors at the extremeties of the feature given as
   * changed.
   * @param changedFeats
   * @param diffusedFeats
   * @return
   */
  private Set<DefaultFeature> computeVectors(Set<IFeature> changedFeats,
      Map<IFeature, IGeometry> diffusedFeats) {
    Set<DefaultFeature> vectors = new HashSet<DefaultFeature>();
    for (IFeature f : changedFeats) {
      for (int i = 0; i < f.getGeom().coord().size(); i++) {
        if (i >= diffusedFeats.get(f).coord().size())
          break;
        IDirectPosition ini = f.getGeom().coord().get(i);
        IDirectPosition fin = diffusedFeats.get(f).coord().get(i);
        if (ini.equals2D(fin, 0.5))
          continue;
        vectors.add(new DefaultFeature(new GM_LineSegment(ini, fin)));
      }
    }
    return vectors;
  }

  /**
   * Compute displacement vectors at each vertex that has been moved in a
   * distorted geometry.
   * @param initialGeom
   * @param distortedGeom
   * @return
   */
  private Set<DefaultFeature> computeVectorsInsideGeom(IGeometry initialGeom,
      IGeometry distortedGeom) {
    Set<DefaultFeature> vectors = new HashSet<DefaultFeature>();
    for (int i = 0; i < initialGeom.coord().size(); i++) {
      IDirectPosition ini = initialGeom.coord().get(i);
      IDirectPosition fin = distortedGeom.coord().get(i);
      if (ini.equals2D(fin, 0.5))
        continue;
      vectors.add(new DefaultFeature(new GM_LineSegment(ini, fin)));
    }
    return vectors;
  }

  /**
   * Default implementation of the "touching" method, returns the features
   * connected to the changed features.
   * @param changedFeats
   * @param neighbours
   * @return
   */
  protected Map<IFeature, IDirectPosition> getTouchingNeighbours(
      Set<IFeature> changedFeats,
      IFeatureCollection<? extends IFeature> neighbours,
      Map<IFeature, IGeometry> diffusedFeats) {
    Map<IFeature, IDirectPosition> touchingNeighbours = new HashMap<IFeature, IDirectPosition>();
    for (IFeature f : changedFeats) {
      // get initial node neighbours
      Set<IFeature> followingFeats = getFollowingFeatures(f, neighbours, true);
      for (IFeature follow : followingFeats) {
        // continue if the feature has already been diffused
        if (diffusedFeats.containsKey(follow))
          continue;
        if (changedFeats.contains(follow))
          continue;
        if (!touchingNeighbours.containsKey(follow))
          touchingNeighbours.put(follow, f.getGeom().coord().get(0));
      }
      followingFeats.clear();
      // get final node neighbours
      followingFeats = getFollowingFeatures(f, neighbours, false);
      for (IFeature follow : followingFeats) {
        // continue if the feature has already been diffused
        if (diffusedFeats.containsKey(follow))
          continue;
        if (changedFeats.contains(follow))
          continue;
        if (!touchingNeighbours.containsKey(follow))
          touchingNeighbours.put(follow,
              f.getGeom().coord().get(f.getGeom().coord().size() - 1));
      }
    }
    return touchingNeighbours;
  }

  /**
   * Delaunay implementation of the "touching" method: a delaunay triangulation
   * is computed between the centroid of the neighbours+changedFeats, and the
   * method returns the features connected to the changed features by only one
   * triangulation edge.
   * @param changedFeats
   * @param neighbours
   * @return
   */
  protected Map<IFeature, IDirectPosition> getDelaunayTouchingNeighbours(
      Set<IFeature> changedFeats,
      IFeatureCollection<? extends IFeature> neighbours,
      Map<IFeature, IGeometry> diffusedFeats) {
    Map<IFeature, IDirectPosition> touchingNeighbours = new HashMap<IFeature, IDirectPosition>();

    List<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
    Map<IFeature, TriangulationPoint> mapChangedNode = new HashMap<>();
    for (IFeature obj : changedFeats) {
      TriangulationPoint point = new TriangulationPointImpl(
          obj.getGeom().centroid());
      points.add(point);
      mapChangedNode.put(obj, point);
    }
    Map<TriangulationPoint, IFeature> mapNodeNeighbour = new HashMap<>();
    for (IFeature obj : neighbours) {
      if (changedFeats.contains(obj))
        continue;
      TriangulationPoint point = new TriangulationPointImpl(
          obj.getGeom().centroid());
      points.add(point);
      mapNodeNeighbour.put(point, obj);
    }
    // trigger the triangulation
    Triangulation tri = new Triangulation(points,
        new TriangulationSegmentFactoryImpl(),
        new TriangulationTriangleFactoryImpl());
    tri.setOptions("czeBQ");
    try {
      tri.compute();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // get the neighbours connected to a changed feature
    for (IFeature obj : changedFeats) {
      TriangulationPoint node = mapChangedNode.get(obj);
      Set<INode> neighbourNodes = node.getNextNodes();
      for (INode neighbourNode : neighbourNodes) {
        IFeature neighbour = mapNodeNeighbour.get(neighbourNode);
        if (neighbour == null)
          continue;
        touchingNeighbours.put(neighbour, neighbour.getGeom().coord().get(0));
      }
    }

    return touchingNeighbours;
  }

  /**
   * @param neighbours
   */
  private Set<IFeature> getFollowingFeatures(IFeature changedFeat,
      IFeatureCollection<? extends IFeature> neighbours, boolean first) {
    Set<IFeature> followingFeats = new HashSet<IFeature>();
    if (changedFeat instanceof INetworkSection) {
      INetworkNode node = ((INetworkSection) changedFeat).getFinalNode();
      if (first)
        node = ((INetworkSection) changedFeat).getInitialNode();
      followingFeats.addAll(node.getInSections());
      followingFeats.addAll(node.getOutSections());
      followingFeats.remove(changedFeat);
    } else {
      // TODO here, only use geometry
    }
    return followingFeats;
  }

  public void setDefaultMapspecs() {
    // construction des mapspecs LSA
    Set<String> contraintesMalleables = new HashSet<String>();
    contraintesMalleables.add(LSMovementConstraint.class.getName());
    // contraintesMalleables.add(LSCurvatureConstraint.class.getName());
    contraintesMalleables.add(LSAngleConstraint.class.getName());
    contraintesMalleables.add(LSSegmentLengthConstraint.class.getName());
    contraintesMalleables.add(LSMovementDirConstraint.class.getName());
    contraintesMalleables.add(LSCrossingConstraint.class.getName());
    // contraintesMalleables.add(LSStiffnessConstraint.class.getName());
    Set<String> contraintesRigides = new HashSet<String>();
    contraintesRigides.add(LSStiffnessConstraint.class.getName());
    contraintesRigides.add(LSMovementConstraint.class.getName());

    Set<String> classesMalleables = new HashSet<String>();
    classesMalleables.add(IRoadLine.class.getName());
    classesMalleables.add(IPathLine.class.getName());
    classesMalleables.add(IWaterArea.class.getName());
    Set<String> classesRigides = new HashSet<String>();
    classesRigides.add(IBuilding.class.getName());
    Map<String[], Double> contraintesExternes = new HashMap<String[], Double>();
    contraintesExternes
        .put(new String[] { LSProximityConstraint.class.getName(),
            IBuilding.class.getName(), IBuilding.class.getName() }, 0.1);

    Map<String, Double> poidsContraintes = new HashMap<String, Double>();
    poidsContraintes.put(LSMovementConstraint.class.getName(), 5.0);
    poidsContraintes.put(LSCurvatureConstraint.class.getName(), 16.0);
    poidsContraintes.put(LSCrossingConstraint.class.getName(), 16.0);
    poidsContraintes.put(LSMovementDirConstraint.class.getName(), 15.0);
    poidsContraintes.put(LSSideOrientConstraint.class.getName(), 5.0);
    poidsContraintes.put(LSStiffnessConstraint.class.getName(), 15.0);
    poidsContraintes.put(LSVectorDisplConstraint1.class.getName(), 15.0);
    poidsContraintes.put(LSAngleConstraint.class.getName(), 16.0);
    poidsContraintes.put(LSSegmentLengthConstraint.class.getName(), 8.0);
    poidsContraintes.put(LSProximityConstraint.class.getName(), 20.0);

    mapspecs = new MapspecsLS(Legend.getSYMBOLISATI0N_SCALE(),
        new HashSet<IFeature>(), new HashSet<String>(), contraintesRigides,
        contraintesMalleables, contraintesExternes, new HashSet<String>(),
        classesRigides, classesMalleables, poidsContraintes);
    mapspecs.setDensStep(25.0);
    mapspecs.setFilter(true);
    mapspecs.setFilterThreshold(0.5);
  }

  class LocalFeatureDiffusion {
    private IFeature feature;
    private IGeometry newGeom;
    private IDirectPosition firstNode, lastNode, newFirstNode, newLastNode;

    public LocalFeatureDiffusion(IFeature feature, IGeometry newGeom,
        IDirectPosition firstNode, IDirectPosition lastNode,
        IDirectPosition newFirstNode, IDirectPosition newLastNode) {
      super();
      this.feature = feature;
      this.newGeom = newGeom;
      this.firstNode = firstNode;
      this.lastNode = lastNode;
      this.newFirstNode = newFirstNode;
      this.newLastNode = newLastNode;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((feature == null) ? 0 : feature.hashCode());
      result = prime * result
          + ((firstNode == null) ? 0 : firstNode.hashCode());
      result = prime * result + ((lastNode == null) ? 0 : lastNode.hashCode());
      result = prime * result
          + ((newFirstNode == null) ? 0 : newFirstNode.hashCode());
      result = prime * result + ((newGeom == null) ? 0 : newGeom.hashCode());
      result = prime * result
          + ((newLastNode == null) ? 0 : newLastNode.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      LocalFeatureDiffusion other = (LocalFeatureDiffusion) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (feature == null) {
        if (other.feature != null)
          return false;
      } else if (!feature.equals(other.feature))
        return false;
      if (firstNode == null) {
        if (other.firstNode != null)
          return false;
      } else if (!firstNode.equals(other.firstNode))
        return false;
      if (lastNode == null) {
        if (other.lastNode != null)
          return false;
      } else if (!lastNode.equals(other.lastNode))
        return false;
      if (newFirstNode == null) {
        if (other.newFirstNode != null)
          return false;
      } else if (!newFirstNode.equals(other.newFirstNode))
        return false;
      if (newGeom == null) {
        if (other.newGeom != null)
          return false;
      } else if (!newGeom.equals(other.newGeom))
        return false;
      if (newLastNode == null) {
        if (other.newLastNode != null)
          return false;
      } else if (!newLastNode.equals(other.newLastNode))
        return false;
      return true;
    }

    public IFeature getFeature() {
      return feature;
    }

    public void setFeature(IFeature feature) {
      this.feature = feature;
    }

    public IGeometry getNewGeom() {
      return newGeom;
    }

    public void setNewGeom(IGeometry newGeom) {
      this.newGeom = newGeom;
    }

    public IDirectPosition getFirstNode() {
      return firstNode;
    }

    public void setFirstNode(IDirectPosition firstNode) {
      this.firstNode = firstNode;
    }

    public IDirectPosition getLastNode() {
      return lastNode;
    }

    public void setLastNode(IDirectPosition lastNode) {
      this.lastNode = lastNode;
    }

    private LeastSquaresDiffusion getOuterType() {
      return LeastSquaresDiffusion.this;
    }

    public IDirectPosition getNewFirstNode() {
      return newFirstNode;
    }

    public void setNewFirstNode(IDirectPosition newFirstNode) {
      this.newFirstNode = newFirstNode;
    }

    public IDirectPosition getNewLastNode() {
      return newLastNode;
    }

    public void setNewLastNode(IDirectPosition newLastNode) {
      this.newLastNode = newLastNode;
    }

  }

  @Override
  public IGeometry applySingleDiffusion(IFeature feat,
      IGeometry distortedGeom) {
    // first, compute displacement vectors
    Set<DefaultFeature> vectors = computeVectorsInsideGeom(feat.getGeom(),
        distortedGeom);

    // then, compute the least squares adjustment
    Set<IFeature> selectedObjs = new HashSet<IFeature>();
    selectedObjs.add(feat);
    mapspecs.setSelectedObjects(selectedObjs);
    DiffusionLSScheduler sched = new DiffusionLSScheduler(mapspecs, vectors,
        LSVectorDisplConstraint1.class, getMinimumDisplacement());
    sched.setSolver(MatrixSolver.JAMA);
    sched.triggerAdjustment();

    // finally, return the diffused geometry
    return sched.getMapObjGeom().get(feat);
  }

  public MapspecsLS getMapspecs() {
    return mapspecs;
  }

  public void setMapspecs(MapspecsLS mapspecs) {
    this.mapspecs = mapspecs;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public TouchingMode getTouchingMode() {
    return touchingMode;
  }

  public void setTouchingMode(TouchingMode touchingMode) {
    this.touchingMode = touchingMode;
  }

}
