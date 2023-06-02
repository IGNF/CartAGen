package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingnetface;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

public class BuildingTopology extends MicroMicroRelationalConstraintWithZone {

  /**
   * Logger for this class
   */
  @SuppressWarnings("unused")
  private static Logger logger = LogManager
      .getLogger(BuildingTopology.class.getName());

  /**
   * @param ag
   * @param rel
   * @param importance
   * @param constrainedZoneType
   */
  public BuildingTopology(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance,
      ConstrainedZoneType constrainedZoneType) {
    super(ag, rel, importance, constrainedZoneType);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void computePriority() {
    this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_5);
  }

  @Override
  public void computeConstrainedGeom() {
    // Retrieves the network face agent sharing this constraint
    NetworkFaceAgent netFaceAgent = (NetworkFaceAgent) this
        .getAgentSharingConstraint();
    // logger.debug("Constrained zone geom computation Building = " +
    // this.getAgent().toString() +
    // " NetFace = " + netFaceAgent.toString());
    // The constrained geom is its geometry
    this.getConstrainedZone().setZoneGeom((IPolygon) netFaceAgent.getGeom());
  }

  /**
   * {@inheritDoc}
   * <p>
   * Here it is enough if the centroid stays inside the network face so centroid
   * related geom = zone geom (= network face geom).
   */
  @Override
  public void computeCentroidRelatedGeom() {
    this.getConstrainedZone()
        .setCentroidRelatedZoneGeom(this.getConstrainedZone().getZoneGeom());
  }

  @Override
  public Set<ActionProposal> getActions() {
    // TODO Auto-generated method stub
    return null;
  }

}
