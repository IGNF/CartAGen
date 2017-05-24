/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * 
 * @author JGaffuri
 * 
 */
public class PointAgentDisplacement {
  private static Logger logger = Logger.getLogger(PointAgentDisplacement.class
      .getName());

  public static void displace(IPointAgent pa, double dx, double dy) {

    if (PointAgentDisplacement.logger.isDebugEnabled()) {
      PointAgentDisplacement.logger.debug("displacement of " + pa + "   dx="
          + dx + ", dy=" + dy);
    }
    pa.setX(pa.getX() + dx);
    pa.setY(pa.getY() + dy);

    // get the feature possiblilly linked to the point
    GAELLinkableFeature lf = pa.getLinkedFeature();
    if (lf != null) {
      if (PointAgentDisplacement.logger.isDebugEnabled()) {
        PointAgentDisplacement.logger
            .debug("displacement of the linked feature " + lf);
      }
      lf.getFeature().setGeom(
          CommonAlgorithms.translation(lf.getGeom(), dx, dy));
    }

  }

}
