package fr.ign.cogit.cartagen.spatialanalysis.clustering;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * Make clusters by buffering the features and looking for the overlaps between
 * buffers.
 * 
 * @author GTouya
 * 
 */
public class BufferClustering {

  private IFeatureCollection<IFeature> features;
  private double bufferSize;
  private boolean isDebug = false;
  /**
   * Add the eliminated features in the clustering if true;
   */
  private boolean eliminated = false;
  private Random red, green, blue;

  public BufferClustering(Collection<? extends IFeature> features,
      double bufferSize) {
    super();
    this.features = new FT_FeatureCollection<IFeature>();
    this.features.addAll(features);
    this.bufferSize = bufferSize;
    this.red = new Random();
    this.green = new Random();
    this.blue = new Random();
  }

  public void setFeatures(IFeatureCollection<IFeature> features) {
    this.features = features;
  }

  public IFeatureCollection<IFeature> getFeatures() {
    return this.features;
  }

  public void setDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  public boolean isDebug() {
    return this.isDebug;
  }

  public void setEliminated(boolean eliminated) {
    this.eliminated = eliminated;
  }

  public boolean isEliminated() {
    return this.eliminated;
  }

  /**
   * Compute a simple buffer clustering: buffers are merged, then separated into
   * simple geometries. The features contained in a simple geometry form a
   * cluster. Be careful, this is a memory greedy method.
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public Map<Set<IFeature>, IPolygon> getClusters() {
    Map<Set<IFeature>, IPolygon> clusters = new HashMap<Set<IFeature>, IPolygon>();

    // first, build the merged geometry
    IGeometry mergedGeom = null;
    for (IFeature obj : this.features) {
      if (!this.eliminated & obj.isDeleted()) {
        continue;
      }
      IGeometry buffer = obj.getGeom().buffer(this.bufferSize);

      // merge buffer to the global geometry
      if (mergedGeom == null) {
        mergedGeom = buffer;
      } else {
        mergedGeom = mergedGeom.union(buffer);
      }
    }

    if (mergedGeom == null) {
      return null;
    }

    if (mergedGeom instanceof IPolygon) {
      Set<IFeature> cluster = new HashSet<IFeature>();
      cluster.addAll(this.features.select(mergedGeom));
      clusters.put(cluster, (IPolygon) mergedGeom);
      return clusters;
    }

    // now make any simple part of mergedGeom a cluster
    for (IGeometry simple : ((IMultiSurface<IOrientableSurface>) mergedGeom)
        .getList()) {
      Set<IFeature> cluster = new HashSet<IFeature>();
      cluster.addAll(this.features.select(simple));
      clusters.put(cluster, (IPolygon) simple);
    }

    return clusters;
  }

  /**
   * Same as getClusters() but network intersections are removed from the merged
   * geometry to avoid that two features are clustered simply because they are
   * connected. Be careful, this is a memory greedy method.
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public Set<Set<IFeature>> getNetworkClusters() {
    Set<Set<IFeature>> clusters = new HashSet<Set<IFeature>>();
    double epsilon = 5.0;
    // first, build the merged geometry
    IGeometry mergedGeom = null;
    for (IFeature obj : this.features) {
      if (!(obj instanceof INetworkSection)) {
        continue;
      }
      INetworkSection section = (INetworkSection) obj;
      IGeometry buffer = section.getGeom().buffer(this.bufferSize);
      // check the initial node
      INetworkNode initial = section.getInitialNode();
      buffer = buffer
          .difference(initial.getGeom().buffer(this.bufferSize + epsilon));
      // check the final node
      INetworkNode finalNode = section.getFinalNode();
      buffer = buffer
          .difference(finalNode.getGeom().buffer(this.bufferSize + epsilon));
      // merge buffer to the global geometry
      if (mergedGeom == null) {
        mergedGeom = buffer;
      } else {
        mergedGeom = mergedGeom.union(buffer);
      }
    }

    if (mergedGeom instanceof IPolygon) {
      Set<IFeature> cluster = new HashSet<IFeature>();
      cluster.addAll(this.features.select(mergedGeom));
      clusters.add(cluster);
      if (this.isDebug) {
        Color color = new Color(this.red.nextInt(254), this.green.nextInt(254),
            this.blue.nextInt(254));
        // FIXME no use of application here anymore
        // CartagenApplication.getInstance().getFrame().getLayerManager()
        // .addToGeometriesPool(mergedGeom, color, 3);
      }
      return clusters;
    }

    if (mergedGeom == null) {
      return null;
    }

    // now make any simple part of mergedGeom a cluster
    for (IGeometry simple : ((IMultiSurface<IOrientableSurface>) mergedGeom)
        .getList()) {
      Set<IFeature> cluster = new HashSet<IFeature>();
      cluster.addAll(this.features.select(simple));
      clusters.add(cluster);
      if (this.isDebug) {
        Color color = new Color(this.red.nextInt(254), this.green.nextInt(254),
            this.blue.nextInt(254));
        // FIXME no use of application here
        // anymoreCartagenApplication.getInstance().getFrame().getLayerManager()
        // .addToGeometriesPool(simple, color, 3);
      }
    }

    return clusters;
  }

  /**
   * Computes the convex hull of the cluster.
   * 
   * @param cluster
   * @return
   */
  public IPolygon computeGeometry(Set<IGeneObj> cluster) {
    Iterator<IGeneObj> i = cluster.iterator();
    IGeometry merged = i.next().getGeom();
    while (i.hasNext()) {
      merged = merged.union(i.next().getGeom());
    }
    return (IPolygon) merged.convexHull();
  }
}
