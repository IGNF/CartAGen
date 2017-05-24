/**
 * @author julien Gaffuri 17 juil. 2008
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.beamsgael;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;

/**
 * Beams GAEL The network is modelled has a set of connected beams
 * @author julien Gaffuri 17 juil. 2008
 * 
 */
public class BeamsGAEL {
  private static Logger logger = Logger.getLogger(BeamsGAEL.class.getName());

  /**
   * the network on which to apply the algorithm
   */
  private NetworkAgent network = null;

  /**
   * the maximum number of activation for per point
   */
  private int activationPerPointLimitNumber = 0;

  public BeamsGAEL(NetworkAgent network, int activationPerPointLimitNumber) {
    this.network = network;
    this.activationPerPointLimitNumber = activationPerPointLimitNumber;
  }

  public void compute() throws InterruptedException {

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("decomposition of the network");
    }
    this.network.decompose();

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("instanciation of the contraints");
    }

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger
          .trace("length and orientation constraints of the segments");
    }
    this.network.ajouterContSegmentLongueur(1.0);
    this.network.ajouterContSegmentOrientation(2.0);

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("points position constraint");
    }
    this.network.ajouterContPointPosition(0.4);

    // THE constraint on the segments

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("create spatial index on the segments list");
    }

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("go through the list of segments");
      /*
       * for(Segment s : network.getSegments()){ //double orientation =
       * s.getOrientation();
       * 
       * //retrieve the close segments
       * 
       * //go through the list of close segments
       * 
       * //compute the orientation of each of them
       * 
       * //compute the orientation difference between two segments
       * 
       * //instanciate constraint: a minimum constraint on the segments
       * 
       * //the strengh should depend on the orientation difference between them
       * //certainly: a minimum distance constraint between points and segments
       * should be enough? }
       */
    }

    // constraints on the junctions: to do.
    // many things could be done: offset segments caricature, round-about
    // enlargement, interchange enlargement

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("point agents list initialisation");
    }
    for (IPointAgent ap : this.network.getPointAgents()) {
      if (BeamsGAEL.logger.isTraceEnabled()) {
        BeamsGAEL.logger.trace("  ajout de " + ap);
      }
      ap.setDansListe(true);
      this.network.getListeAgentsPoints().add(ap);
    }

    // point agents activation with a low resolution value
    double res = GeneralisationSpecifications.getRESOLUTION();
    GeneralisationSpecifications.setRESOLUTION(0.001);

    if (BeamsGAEL.logger.isTraceEnabled()) {
      BeamsGAEL.logger.trace("points activation");
    }
    this.network.activatePointAgents(this.activationPerPointLimitNumber
        * this.network.getPointAgents().size());

    GeneralisationSpecifications.setRESOLUTION(res);

    // cleaning of the decomposition
    this.network.cleanDecomposition();
  }

}
