/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;

/**
 * @author JGaffuri
 * 
 */
public class HydroSectionDeformation {
  private static Logger logger = LogManager
      .getLogger(HydroSectionDeformation.class.getName());

  public static void compute(IHydroSectionAgent tr,
      int nbLimiteActivationsParPoint) throws InterruptedException {

    // decompose
    tr.decompose();

    // fixe les points extremes
    tr.getPointAgents().get(0).setFixe(true);
    tr.getPointAgents().get(tr.getPointAgents().size() - 1).setFixe(true);

    // instancie les contraintes submicros

    // preservation
    for (GAELSegment s : tr.getSegments()) {
      s.addLengthConstraint(2);
      s.addOrientationConstraint(5);
    }
    tr.creerPointsSingletonsAPartirDesPoints();
    for (GAELPointSingleton ps : tr.getPointSingletons()) {
      ps.addPositionConstraint(1);
    }

    // changement
    if (AgentSpecifications.RIVER_COALESCENCE) {
    }
    if (AgentSpecifications.RIVER_ROAD_PROXIMITY) {
    }
    if (AgentSpecifications.RIVER_FLOW_PRESERVATION) {
      for (GAELSegment s : tr.getSegments()) {
        s.addOutflowConstraint(10);
      }
    }

    // initialise la pile
    tr.chargerPointsNonEquilibres();

    // activation des agents point avec une faible valeur de resolution
    double res = GeneralisationSpecifications.getRESOLUTION();
    // SpecGene.setRESOLUTION(0.001);

    if (HydroSectionDeformation.logger.isTraceEnabled()) {
      HydroSectionDeformation.logger.trace("activation des agents point");
    }
    tr.activatePointAgents(
        nbLimiteActivationsParPoint * tr.getPointAgents().size());

    GeneralisationSpecifications.setRESOLUTION(res);

    // nettoyage
    tr.cleanDecomposition();
    tr.registerDisplacement();

  }

}
