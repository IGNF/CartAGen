/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.relation.building2;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.building2.BuildingProximity;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.spatialanalysis.measures.ProximityBtwPossiblyOverlappingPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri
 * @author Cecile Duchene
 * @author Gokhan Altay
 * 
 */
public class Proximity extends MicroMicroRelation {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager.getLogger(Proximity.class.getName());

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Construct a Proximity relation between the two small compact agents passed
   * as arguments. The corresponding constraints on each agent are also created.
   * @param ag1 The first small compact agent involved in the proximity relation
   * @param ag2 The second small compact agent involved in the proximity
   *          relation
   */
  public Proximity(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
    // Check the types of agents
    if (!(ag1 instanceof ISmallCompactAgent
        && ag2 instanceof ISmallCompactAgent)) {
      Proximity.logger
          .error("Attempt to create a building to building proximity relation"
              + "between two agents that are not small compact agents. The system"
              + "will reach an instable state because an instable constraint has"
              + "been built");
      return;
    }

    // FIXME on ne peut pas passer geomConstrainedZone par parametre, il
    // est calculé dans constraint et il utilise lautre agent pour
    // calcul
    new BuildingProximity(ag1, this, this.getImportance(),
        ConstrainedZoneType.OUTSIDE);

    // Specific to activation by the strategy developed in Gokhan Altay's
    // internship - therefore ignored for a standard CartACom activation
    // TODO Tidy up: see how the neighbours retrieval could be done in the same
    // way for both types of activations
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< gkhn
    // ag1.addNeighbor(ag2);
    // ag2.addNeighbor(ag1);
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    new BuildingProximity(ag2, this, this.getImportance(),
        ConstrainedZoneType.OUTSIDE);
  }

  private double currentValue;
  private double goalValue;
  private double initialValue;
  private double flexibility = 0;

  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {
    ISmallCompactAgent agentCompact1 = (ISmallCompactAgent) ag1;
    ISmallCompactAgent agentCompact2 = (ISmallCompactAgent) ag2;

    // Check if both are small compacts of the same network face
    boolean relevant = false;
    if (!ag1.equals(ag2)) {
      relevant = true;
    }

    if (relevant) {
      relevant = agentCompact1.getContainingFace() == null
          || agentCompact1.getContainingFace().getContainedSmallCompacts()
              .contains(agentCompact2);
      // If the agents of same face, check if constraint is already
      // instantiated
      if (relevant) {
        Set<Constraint> cons = ag1.getConstraints();
        ICartAComAgentGeneralisation compareAg1;
        ICartAComAgentGeneralisation compareAg2;
        for (Constraint constraint : cons) {
          MicroMicroRelationalConstraint mmrc = (MicroMicroRelationalConstraint) constraint;
          if (mmrc.getRelation() instanceof Proximity) {
            compareAg1 = mmrc.getRelation().getAgentGeo1();
            compareAg2 = mmrc.getRelation().getAgentGeo2();
            if ((ag1.equals(compareAg1) && ag2.equals(compareAg2))
                || ag1.equals(compareAg2) && ag2.equals(compareAg1)) {
              relevant = false;
              break;
            }

          }
        }
      }
    }
    return relevant;

  }

  @Override
  public void computeSatisfaction() {

    this.computeGoalValue();
    this.computeCurrentValue();

    /*
     * Calcule la satisfaction de la relation (les =, <, > sont a flexibilite
     * pres: Satisfaction = 5 si valeur_courante >= valeur_but Sinon: 1 si
     * valeur_courante < 0.0 2 si 0.0 < valeur_courante < 0.5*valeur_but 3 si
     * 0.5*valeur_but < valeur_courante < valeur_but
     */

    double result;
    logger.debug("Current Value: " + this.currentValue + ", Goal Value: "
        + this.goalValue);
    if (this.currentValue - this.goalValue >= -this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else if (this.currentValue < -this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_1;
    } else if (this.currentValue < 0.5 * this.goalValue - this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_2;
    } else {
      result = CartacomSpecifications.SATISFACTION_3;
    }

    this.setSatisfaction(result);
  }

  @Override
  public void computeGoalValue() {
    this.goalValue = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT;
  }

  @Override
  public void computeCurrentValue() {
    // Get geometry of first agent's symbole
    // TODO Remplacer par symbol extent quand disponible sur les bâtiments
    // (a ajouter dans pivot gene
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    // Get geometry of second agent's symbole
    IPolygon sym2 = (IPolygon) this.getAgentGeo2().getFeature().getGeom();

    double proximityCartagen = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // double gothicCartagen = GothicGeomAlgorithms
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // System.out.println("proximityCartagen " + proximityCartagen);

    // System.out.println("gothicCartagen " + gothicCartagen);

    this.currentValue = proximityCartagen;

  }

  @Override
  public void computeInitialValue() {
    // Get geometry of first agent's symbole
    // TODO Remplacer par symbol extent quand disponible sur les bâtiments
    // (a ajouter dans pivot gene
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    // Get geometry of second agent's symbole
    IPolygon sym2 = (IPolygon) this.getAgentGeo2().getFeature().getGeom();

    // this.initialValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    double proximityCartagen = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // double gothicCartagen = GothicGeomAlgorithms
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // System.out.println("proximityCartagen " + proximityCartagen);

    // System.out.println("gothicCartagen " + gothicCartagen);

    this.initialValue = proximityCartagen;
  }

  public double getInitialValue() {
    return this.initialValue;
  }
}
