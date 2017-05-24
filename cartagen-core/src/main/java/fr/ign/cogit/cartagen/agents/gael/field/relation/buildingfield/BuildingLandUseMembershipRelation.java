/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseParcelAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * @author julien Gaffuri 27 janv. 08
 * 
 */
public class BuildingLandUseMembershipRelation extends ObjectFieldRelation {
  /**
   */
  private LandUseParcelAgent domaineInitial = null;
  /**
   */
  private LandUseParcelAgent domaineCourant = null;
  /**
   */
  private double distanceInitiale = 0.0;
  /**
   */
  private double dist = 0.0;

  public BuildingLandUseMembershipRelation(GeographicAgentGeneralisation ag) {
    super(
        (LandUseFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getLandUseField()),
        (GeographicObjectAgent) ag);
    this.computeInitialValue();
    this.domaineCourant = this.domaineInitial;
    this.dist = this.distanceInitiale;
  }

  @Override
  public void computeSatisfaction() {
    if (this.domaineInitial == null) {
      this.setSatisfaction(100);
    } else if (this.domaineCourant == this.domaineInitial) {
      // le batiment est bien dans sa zone d'occupation du sol
      // setSatisfaction(100(1-dist/SpecCarto.D_DISTANCE_LIMITE_ZONE_OS_1));
    } else {
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computeCurrentValue() {
    BuildingAgent ab = (BuildingAgent) this.getAgentGeo();
    this.domaineCourant = ab.getLandUseParcel();
    if (this.domaineCourant == this.domaineInitial) {
      this.dist = ab.getDistanceLimiteDomaineOccSol(this.domaineCourant)
          - this.distanceInitiale;
    } else {
      this.dist = ab.getGeom().distance(this.domaineInitial.getGeom());
    }
  }

  @Override
  public void computeInitialValue() {
    this.computeCurrentValue();
    this.domaineInitial = this.domaineCourant;
    this.distanceInitiale = this.dist;
  }

}
