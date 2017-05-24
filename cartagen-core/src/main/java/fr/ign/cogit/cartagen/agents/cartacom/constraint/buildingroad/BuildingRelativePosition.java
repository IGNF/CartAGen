/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.buildingroad.RelativePosition;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

/**
 * The building side part of the constraint on relative position between dead
 * end roads and buildings. See p. 157-158 in C. Duchêne's PhD.
 * @author GTouya
 * 
 */
public class BuildingRelativePosition
    extends MicroMicroRelationalConstraintWithZone {

  /**
   * @param ag
   * @param rel
   * @param importance
   * @param constrainedZoneType
   */
  public BuildingRelativePosition(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance,
      ConstrainedZoneType constrainedZoneType) {
    super(ag, rel, importance, constrainedZoneType);
    // TODO Auto-generated constructor stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RelativePosition getRelation() {
    return (RelativePosition) super.getRelation();
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.lib.constraints.geographicConstraints.
   * GeographicConstraint#computePriority()
   */
  @Override
  public void computePriority() {
    // TODO
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    // TODO
    return actionsSet;
  }

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * 
   */
  @Override
  public void computeConstrainedGeom() {
    // Get lineer agent and compact agent
    ICartAComAgentGeneralisation agCom = this.getRelation().getAgentGeo1();
    ICartAComAgentGeneralisation agLin = this.getRelation().getAgentGeo2();
    // Make sure which one is lineer agent
    if (agLin.getId() == this.getAgent().getId()) {
      // agLin is actually building agent, we change
      ICartAComAgentGeneralisation agTemp = agLin;
      agLin = agCom;
      agCom = agTemp;
    }
    // Return buffer (using goal value of relation)
    double bufferLength = (GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE)
        * (Legend.getSYMBOLISATI0N_SCALE() / 1000);

    IPolygon result = null;
    if (agLin.getFeature() instanceof ICarryingRoadLine
        && ((ICarryingRoadLine) agLin.getFeature()).getCarriedObjects()
            .size() > 0) {
      IGeometry res = SectionSymbol.getSymbolExtentWithCarriedObjects(
          ((ICarryingRoadLine) agLin.getFeature())).buffer(bufferLength);
      result = (IPolygon) res;
    } else {
      result = (IPolygon) SectionSymbol
          .getSymbolExtent((INetworkSection) agLin.getFeature())
          .buffer(bufferLength);
    }

    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(result, Color.RED, 2);
    this.getConstrainedZone().setZoneGeom(result);
  }

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * 
   */
  @Override
  public void computeCentroidRelatedGeom() {
    IPolygon zoneGeom = this.getConstrainedZone().getZoneGeom();
    // Get linear agent and compact agent
    ICartAComAgentGeneralisation agCom = this.getRelation().getAgentGeo1();
    ICartAComAgentGeneralisation agLin = this.getRelation().getAgentGeo2();

    // Make sure which one is linear agent
    if (agLin.getId() == this.getAgent().getId()) {
      // agLin is actually building agent, we change
      ICartAComAgentGeneralisation agTemp = agLin;
      agLin = agCom;
      agCom = agTemp;
    }

    IPolygon symbolGeom = (IPolygon) agCom.getSymbolGeom();
    IPolygon result = null;
    // result = (IPolygon) Minkowski.extendPolygonByAuxiliaryPolygonAndCentroid(
    // zoneGeom, symbolGeom, ((SmallCompactAgent) agCom).getCentroid());

    result = (IPolygon) (new MorphologyTransform()
        .minkowskiSumWithCustomPolyCentr(zoneGeom, symbolGeom,
            ((ISmallCompactAgent) agCom).getCentroid()));

    this.getConstrainedZone().setCentroidRelatedZoneGeom(result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  // TODO A corriger. Apparemement cette contrainte est utilisee aussi entre
  // RiverSectionAgent et Batiments. Il faut creer un package pour les
  // contraintes
  // (et relations) entre NetworkSectionAgent et SmallCompacts (contenant cette
  // contrainte),
  // different du package pour les contraintes NetwSection/Bati(contenant la
  // contrainte
  // d'orientation par exemple). Le package Routes/Bati doit pouvoir être
  // supprimé
  // l'instant.
  public INetworkSectionAgent getAgentSharingConstraint() {
    return (INetworkSectionAgent) super.getAgentSharingConstraint();
  }

}
