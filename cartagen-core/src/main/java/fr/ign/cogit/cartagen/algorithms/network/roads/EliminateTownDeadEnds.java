package fr.ign.cogit.cartagen.algorithms.network.roads;

import java.util.HashSet;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class EliminateTownDeadEnds {

  private static Logger logger = Logger.getLogger(EliminateTownDeadEnds.class);

  /**
   * The street network
   */
  private StreetNetwork network;

  public StreetNetwork getNetwork() {
    return this.network;
  }

  /**
   * The collection of dead ends of the network
   */
  private IFeatureCollection<DeadEndGroup> deadEnds;

  public IFeatureCollection<DeadEndGroup> getDeadEnds() {
    return this.deadEnds;
  }

  /**
   * The minimal dead end length
   */
  private double deadEndLength;

  public double getDeadEndLength() {
    return this.deadEndLength;
  }

  /**
   * Constructor
   * @param deadEnd
   * @param sectionToSlideOn
   * @param curvAbsc
   */
  public EliminateTownDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds,
      StreetNetwork network, double deadEndLength) {
    this.deadEnds = deadEnds;
    this.network = network;
    this.deadEndLength = deadEndLength;
  }

  /**
   * Méthode qui marque les impasses qui sont éliminées par le processus. Cette
   * méthode 2 est issue de la thèse d'Anne Ruas : prend en compte la densité en
   * bâtiments de sa situation et un seuil de longueur (utilise le champ
   * deadEndLength)
   */
  public void execute() {

    // loop on the deadEndGroups
    for (DeadEndGroup deg : this.deadEnds) {
      if (logger.isDebugEnabled())
        logger.debug(deg.toString() + " is analyzed");
      IDirectPosition centre = deg.getGeom().centroid();
      // get the network city block containing the dead end group
      IUrbanBlock block = null;
      for (IUrbanBlock b : this.network.getCityBlocks()) {
        if (b.getGeom().contains(centre.toGM_Point())) {
          block = b;
          break;
        }
      }

      // particular case, a part of the dead end is outside the city
      if (block == null) {
        double maxInter = 0.0;
        for (IUrbanBlock b : this.network.getCityBlocks()) {
          double area = deg.getGeom().intersection(b.getGeom()).area();
          if (area > maxInter) {
            maxInter = area;
            block = b;
          }
        }
      }
      if (block == null) {
        if (logger.isDebugEnabled())
          logger.debug("null block");
        continue;
      }

      // first test if the group is attached to already eliminated streets
      HashSet<INetworkSection> connected = deg.getFeaturesConnectedToRoot();
      boolean disconnected = true;
      for (INetworkSection feat : connected) {
        disconnected = feat.isEliminated();
        if (!disconnected) {
          break;
        }
      }

      // if the group is disconnected, it is totally eliminated
      if (disconnected) {
        if (logger.isDebugEnabled())
          logger.debug("the group is connected to eliminated roads");
        for (INetworkSection feat : deg.getFeatures()) {
          if (logger.isTraceEnabled())
            logger.trace(feat.toString() + " is eliminated");
          feat.eliminate();
        }
        continue;
      }

      // there, treat the group as a whole
      // first, test if the group is too small
      boolean case1 = deg.getLength() < 2 * this.deadEndLength;
      boolean case2 = block.getSimulatedDensity() > 1;
      boolean case3 = (block.getSimulatedDensity() > 0.8 && deg.getLength() < 3.5 * this.deadEndLength);
      if (case1 || case2 || case3) {
        // the whole group is eliminated
        if (logger.isDebugEnabled())
          logger.debug("the whole group is too short (" + deg.getLength()
              + ", " + case1 + ", " + case2 + ", " + case3 + ")");
        for (INetworkSection feat : deg.getFeatures()) {
          if (logger.isTraceEnabled())
            logger.trace(feat.toString() + " is eliminated");
          feat.eliminate();
        }
        continue;
      }// if(...)

      // there, test each leaf length
      if (logger.isDebugEnabled())
        logger.debug("leaf by leaf analysis");
      boolean allElim = true;
      for (INetworkSection leaf : deg.getLeafs()) {
        if (logger.isDebugEnabled())
          logger.debug("analysis of leaf " + leaf);
        double leafLength = leaf.getGeom().length();
        boolean leafCase1 = leafLength < this.deadEndLength;
        boolean leafCase3 = (block.getSimulatedDensity() > 0.8 && leafLength < 1.6 * this.deadEndLength);
        if (leafCase1 || case2 || leafCase3) {
          if (logger.isDebugEnabled())
            logger.debug("the leaf is too short (" + leafLength + ", "
                + leafCase1 + ", " + case2 + ", " + leafCase3 + ")");
          // eliminate this leaf
          if (logger.isTraceEnabled())
            logger.trace(leaf.toString() + " is eliminated");
          leaf.eliminate();
        } else {
          allElim = false;
        }
      }

      // finally, if all leaves are eliminated, eliminate the rest
      if (allElim) {
        for (INetworkSection feat : deg.getFeatures()) {
          if (logger.isTraceEnabled())
            logger.trace("elimination of remaining root " + feat.toString());
          feat.eliminate();
        }
      }
    }// loop on the dead end groups

  }
}
