/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent.network.hydro;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.section.hydro.RoadNetworkProximity;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointsegment.MinimalDistance;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.hydrorelief.HydroSectionOutflow;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.hydrorelief.ReliefHydroSectionOutflow;
import fr.ign.cogit.cartagen.agents.gael.field.relation.hydrofield.HydroSectionOutflowRelation;
import fr.ign.cogit.cartagen.core.GeneralisationLegend;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SuperpositionRateHydroRoad;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author JGaffuri
 * 
 */
public class HydroSectionAgent extends SectionAgent
    implements IHydroSectionAgent {
  // private static Logger
  // logger=Logger.getLogger(TronconCoursEau.class.getName());

  @Override
  public IWaterLine getFeature() {
    return (IWaterLine) super.getFeature();
  }

  public HydroSectionAgent(NetworkAgent res, IWaterLine obj) {
    super();
    this.setFeature(obj);
    this.setNetwork(res);
    this.getNetwork().getTroncons().add(this.getFeature());
    this.getNetwork().getComponents().add(this);
    this.setInitialGeom((ILineString) this.getGeom().clone());
  }

  /**
   * @return part du troncon superposee avec le reseau routier
   */
  public double getTauxSuperpositionRoutier() {
    SuperpositionRateHydroRoad algo = new SuperpositionRateHydroRoad(
        this.getFeature(),
        CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork());
    return algo.compute();

  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    // empatement
    if (AgentSpecifications.HYDRO_EMPATEMENT) {
      this.ajouterContrainteEmpatement(
          AgentSpecifications.HYDRO_EMPATEMENT_IMP);
    }
    // proximite routier
    if (AgentSpecifications.PROXIMITE_HYDRO_ROUTIER) {
      this.ajouterContrainteProximiteRoutier(
          AgentSpecifications.PROXIMITE_HYDRO_ROUTIER_IMP);
    }
    // ecoulement
    if (AgentSpecifications.ECOULEMENT_HYDRO) {
      this.ajouterContrainteEcoulement(
          AgentSpecifications.ECOULEMENT_HYDRO_IMP);
    }
  }

  public void ajouterContrainteEcoulement(double imp) {
    // construit la relation
    HydroSectionOutflowRelation rel = new HydroSectionOutflowRelation(this);
    // contruit la contrainte du troncon
    new HydroSectionOutflow(this, rel, imp);
    // contruit la contrainte du champ relief
    new ReliefHydroSectionOutflow(
        AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField()),
        rel, imp);
  }

  public void ajouterContrainteProximiteRoutier(double imp) {
    new RoadNetworkProximity(this, imp);
  }

  public void ajouterContraintesSubmicrosProximiteRoutier(double imp) {
    // ajoute une contrainte de distance minimale entre chaque point du reseau
    // hydro et les segments proches du reseau routier

    // parcours les troncons routier
    for (INetworkSection obj : CartAGenDoc.getInstance().getCurrentDataset()
        .getRoadNetwork().getSections()) {

      double distanceSeparation = ((GeneralisationLegend.RES_EAU_LARGEUR
          + obj.getWidth()) * 0.5
          + GeneralisationSpecifications.DISTANCE_SEPARATION_HYDRO_ROUTIER)
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      if (this.getGeom().distance(obj.getGeom()) > 3 * distanceSeparation) {
        continue;
      }

      // this et tr sont suffisamment proches
      // parcours les points de this et les segments de tr
      SectionAgent tr = (SectionAgent) AgentUtil.getAgentFromGeneObj(obj);
      for (IPointAgent ap : this.getPointAgents()) {
        for (GAELSegment s : tr.getSegments()) {
          // si le point et le segment sont trop eloignes, ne rien faire
          if (ap.getDistance(s) > 3 * distanceSeparation) {
            continue;
          }
          // cree eventuellement point singleton
          if (ap.getPointSingleton() == null) {
            new GAELPointSingleton(this, ap);
          }
          // instancier contrainte de distance minimale entre le point et le
          // segment
          new MinimalDistance(ap.getPointSingleton(), s, imp,
              distanceSeparation);
        }
      }
    }
  }

}
