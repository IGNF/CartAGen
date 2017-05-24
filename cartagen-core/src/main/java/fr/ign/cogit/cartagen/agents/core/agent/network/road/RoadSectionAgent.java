/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent.network.road;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.section.road.NonOverlapping;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author JGaffuri
 * 
 */
public class RoadSectionAgent extends SectionAgent
    implements IRoadSectionAgent {
  // private static Logger
  // logger=Logger.getLogger(TronconRoute.class.getName());

  @Override
  public IRoadLine getFeature() {
    return (IRoadLine) super.getFeature();
  }

  /**
   * Constructor from the network agent the road belongs to and the road gene
   * obj it will encapsulate. Typically to create a road section agent from a
   * road gene obj.
   * @param netwAg
   * @param obj
   */
  public RoadSectionAgent(NetworkAgent netwAg, IRoadLine obj) {
    super();
    this.setFeature(obj);
    this.setNetwork(netwAg);
    this.getNetwork().getTroncons().add(this.getFeature());
    this.getNetwork().getComponents().add(this);
    this.setInitialGeom((ILineString) this.getGeom().clone());
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (AgentSpecifications.ROUTIER_EMPATEMENT) {
      this.ajouterContrainteEmpatement(
          AgentSpecifications.ROUTIER_EMPATEMENT_IMP);
    }
    if (AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION) {
      this.ajouterContrainteControleDeformation(
          AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION_IMP);
    }
    this.ajouterContrainteNonSuperposition(10.0);
  }

  public void ajouterContrainteNonSuperposition(double imp) {
    new NonOverlapping(this, imp);
  }

}
