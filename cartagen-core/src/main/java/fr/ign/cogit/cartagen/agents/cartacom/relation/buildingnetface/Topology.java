package fr.ign.cogit.cartagen.agents.cartacom.relation.buildingnetface;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingnetface.BuildingTopology;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingnetface.NetFaceTopology;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class Topology extends MicroMicroRelation {

  private double currentValue;
  private double goalValue;
  private double initialValue;

  /**
   * Constructs a topology relation between a small compact agent and a network
   * face agent
   * 
   * @param ag1 Small compact agent of relation
   * @param ag2 Network face agent of relation
   */
  public Topology(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
    if (ag1 instanceof ISmallCompactAgent && ag2 instanceof NetworkFaceAgent) {
      new BuildingTopology(ag1, this, this.getImportance(),
          ConstrainedZoneType.INSIDE);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< gkhn
      ag1.addNeighbor(ag2);
      ag2.addNeighbor(ag1);
      // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      new NetFaceTopology(ag2, this, this.getImportance());
    } else {
      // error
    }
  }

  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {
    boolean result = false;
    ISmallCompactAgent comAg = (ISmallCompactAgent) ag1;
    if (comAg.getContainingFace() == null
        || comAg.getContainingFace().equals(ag2)) {
      result = true;
    }
    return result;

  }

  @Override
  public void computeCurrentValue() {
    ICartAComAgentGeneralisation agCom = this.getAgentGeo1();
    ICartAComAgentGeneralisation agNet = this.getAgentGeo2();
    ICartAComAgentGeneralisation agTemp;
    if (agCom instanceof NetworkFaceAgent) {
      agTemp = agCom;
      agCom = agNet;
      agNet = agTemp;
    }

    // Get geometry of first agent's symbole
    IPolygon geomCompact = (IPolygon) agCom.getFeature().getGeom();

    // Get geometry of second agent's symbole
    IPolygon geomNet = (IPolygon) agNet.getFeature().getGeom();

    if (geomNet.contains(geomCompact)) {
      this.currentValue = 1.0;
    } else {
      this.currentValue = 0.0;
    }

  }

  @Override
  public void computeGoalValue() {
    this.goalValue = CartacomSpecifications.TOPOLOGY_RELATION_GOAL_VALUE;

  }

  @Override
  public void computeInitialValue() {
    ICartAComAgentGeneralisation agCom = this.getAgentGeo1();
    ICartAComAgentGeneralisation agNet = this.getAgentGeo2();
    ICartAComAgentGeneralisation agTemp;
    if (agCom instanceof NetworkFaceAgent) {
      agTemp = agCom;
      agCom = agNet;
      agNet = agTemp;
    }

    // Get geometry of first agent's symbole
    IPolygon geomCompact = (IPolygon) agCom.getFeature().getGeom();

    // Get geometry of second agent's symbole
    IPolygon geomNet = (IPolygon) agNet.getFeature().getGeom();

    if (geomNet.contains(geomCompact)) {
      this.currentValue = 1.0;
    } else {
      this.currentValue = 0.0;
    }

  }

  public double getInitialValue() {
    return this.initialValue;
  }

  @Override
  public void computeSatisfaction() {
    /*
     * Satisfaction max si egalite, min sinon if (valeur_courante == valeur_but)
     * then satisfaction := 5.0; else satisfaction := 1.0;
     */

    this.computeGoalValue();
    this.computeCurrentValue();

    double result;
    if (this.currentValue == this.goalValue) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else {
      result = CartacomSpecifications.SATISFACTION_1;
    }

    this.setSatisfaction(result);
  }

}
