/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.relation.buildingroad;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.BuildingProximity;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.RoadProximity;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.spatialanalysis.measures.ProximityBtwPossiblyOverlappingPolygon;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author julien Gaffuri
 * 
 */
public class Proximity extends MicroMicroRelation {

  protected double currentValue;
  protected double goalValue;
  protected double initialValue;
  protected double flexibility;

  /**
   * @param ag1 the small compact agent
   * @param ag2 the road section agent
   */
  public Proximity(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
    if (ag1 instanceof ISmallCompactAgent
        && ag2 instanceof INetworkSectionAgent) {
      MicroMicroRelationalConstraint constr1 = new BuildingProximity(ag1, this,
          this.getImportance(), ConstrainedZoneType.OUTSIDE);
      this.setConstraint1(constr1);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< gkhn
      ag1.addNeighbor(ag2);
      ag2.addNeighbor(ag1);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      MicroMicroRelationalConstraint constr2 = new RoadProximity(ag2, this,
          this.getImportance());
      this.setConstraint2(constr2);
      // System.out.println("in the building road proximity, constraints
      // succesfully initialised");
    } else if (ag2 instanceof ISmallCompactAgent
        && ag1 instanceof INetworkSectionAgent) {
      MicroMicroRelationalConstraint constr1 = new BuildingProximity(ag2, this,
          this.getImportance(), ConstrainedZoneType.OUTSIDE);
      this.setConstraint1(constr1);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< gkhn
      ag1.addNeighbor(ag2);
      ag2.addNeighbor(ag1);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      MicroMicroRelationalConstraint constr2 = new RoadProximity(ag1, this,
          this.getImportance());
      this.setConstraint2(constr2);
    } else {
      System.out.println(
          "in the building road proximity, constraints !!!!!!!!!!!doesnt initialised");
    }
  }

  /**
   * We are sure that in the initialisation of network faces, small compacts are
   * informed about, in which network face it belongs, and network faces knows
   * which small compacts it contains
   * 
   * @param ag1 Small compact agent
   * @param ag2 Road section agent
   * @return
   */
  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {
    boolean result = false;

    // Cast agents
    ISmallCompactAgent comAg = (ISmallCompactAgent) ag1;
    INetworkSectionAgent netAg = (INetworkSectionAgent) ag2;

    // Check if the network face in which the
    // small compact agent exists is a border to road section agent

    Set<NetworkFaceAgent> leftBordereds = netAg.getLeftBorderedFaces();
    for (NetworkFaceAgent networkFaceAgent : leftBordereds) {
      if (networkFaceAgent.getContainedSmallCompacts().contains(comAg)) {
        result = true;
        break;
      }
    }

    if (result == false) {
      Set<NetworkFaceAgent> rightBordereds = netAg.getRightBorderingFaces();
      for (NetworkFaceAgent networkFaceAgent : rightBordereds) {
        if (networkFaceAgent.getContainedSmallCompacts().contains(comAg)) {
          result = true;
          break;
        }
      }
    }
    return result;

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
    this.goalValue = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE;
  }

  @Override
  public void computeCurrentValue() {

    // System.out.println("enter computeCurrentValue");
    // Get geometry of agents' symboles
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    IPolygon sym2 = (IPolygon) SectionSymbol.getSymbolExtent(
        ((INetworkSectionAgent) this.getAgentGeo2()).getFeature());

    // this.currentValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    double proximityCartagen = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // double gothicCartagen = GothicGeomAlgorithms
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // System.out.println("Proximity between " + this.getAgentGeo1() + " and "
    // + this.getAgentGeo2() + " = " + proximityCartagen);

    // System.out.println("gothicCartagen " + gothicCartagen);

    this.currentValue = proximityCartagen;
  }

  @Override
  public void computeInitialValue() {
    // Get geometrys of agents' symboles
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    IPolygon sym2 = (IPolygon) SectionSymbol.getSymbolExtent(
        ((INetworkSectionAgent) this.getAgentGeo2()).getFeature());

    // this.initialValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);
    double proximityCartagen = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // double gothicCartagen = GothicGeomAlgorithms
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    System.out.println("Proximity between " + this.getAgentGeo1() + " and "
        + this.getAgentGeo2() + " = " + proximityCartagen);

    this.initialValue = proximityCartagen;

  }

  public double getInitialValue() {
    return this.initialValue;
  }

}
