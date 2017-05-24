/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.building2;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.ConstrainedZoneDrivenDisplacement;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

/**
 * @author JGaffuri
 * @author CDuchene
 * 
 */
public class BuildingProximity extends MicroMicroRelationalConstraintWithZone {

  private static Logger logger = Logger
      .getLogger(BuildingProximity.class.getName());

  /**
   * Constructs a "building proximity" constraint on an agent (expected to be a
   * building itself), with the constrained relation passed as argument.
   * @param ag The agent on which the constraint is to be created
   * @param rel The relation this constraint will manage on the agent
   * @param importance The importance of the relation
   * @param constrainedZoneType The type of the constrained zone associated to
   *          this constraint: ConstrainedZoneType.OUTSIDE if the agent is
   *          expected to stay outside the zone, ConstrainedZoneType.INSIDE if
   *          it is expected to stay inside the zone
   */
  public BuildingProximity(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance,
      ConstrainedZoneType constrainedZoneType) {
    super(ag, rel, importance, constrainedZoneType);
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.lib.constraints.geographicConstraints.
   * GeographicConstraint#computePriority()
   */
  @Override
  public void computePriority() {
    /*
     * 
     * On laisse la priorité 5 à la contrainte de topologie Priorité 4 si
     * satisfaction < 3 (i.e. distance inférieure à la moitié du seuil de
     * séparabilité) Sinon, on relache la priorite a 2
     */

    double sfact = this.getRelation().getSatisfaction();

    if (sfact < 3.0) {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_4);
    } else {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_2);
    }
  }

  /**
   * 
   */
  @Override
  public Set<ActionProposal> getActions() {

    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    if (this.getSatisfaction() >= 5) {
      return actionsSet;
    }

    for (int i = 1; i <= 3; i++) {
      CartacomAction action1 = new ConstrainedZoneDrivenDisplacement(
          (ISmallCompactAgent) this.getAgent(), this, i - 1, 4 * (4 - i));
      actionsSet.add(new ActionProposal(this, true, action1, 4 * (4 - i)));

      CartacomAction action2 = new ConstrainedZoneDrivenDisplacement(
          (ISmallCompactAgent) this.getAgent(), this, i - 1, 4 - i);
      action2.setActItselfAction(false);
      actionsSet.add(new ActionProposal(this, false, action2, 4 - i));

      // CartacomAction action2 = new AskToMove(
      // (SmallCompactAgent) this.getAgent(), this, i - 1,
      // (SmallCompactAgent) this.getAgentSharingConstraint(), i);
      // actionsSet.add(new ActionProposal(this, true, action2, (4 - i)));
    }
    return actionsSet;
  }

  /**
     * 
     */
  @Override
  public void computeConstrainedGeom() {
    // Get compact agents
    ICartAComAgentGeneralisation ag = this.getRelation().getAgentGeo1();
    ICartAComAgentGeneralisation agOther = this.getRelation().getAgentGeo2();
    ICartAComAgentGeneralisation agTemp;
    double bufferLength = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    // Make sure which one is the other agent
    // FIXME hope it works
    if (agOther.getId() == this.getAgent().getId()) {
      // agOther is actually ag, so we change
      agTemp = agOther;
      agOther = ag;
      ag = agTemp;
    }

    // Return buffer (using goal value of relation)
    this.getConstrainedZone()
        .setZoneGeom((IPolygon) agOther.getGeom().buffer(bufferLength));
  }

  /**
     * 
     */
  @Override
  public void computeCentroidRelatedGeom() {
    // get geometry of the constrained zone (not related to centroid)
    IPolygon zoneGeom = this.getConstrainedZone().getZoneGeom();
    // get agent centroid and symbol geom
    ISmallCompactAgent smallCompAgent = (ISmallCompactAgent) this.getAgent();
    IDirectPosition centroid = smallCompAgent.getCentroid();
    IPolygon symbolGeom = (IPolygon) smallCompAgent.getSymbolGeom();

    // DEBUG
    if (BuildingProximity.logger.isDebugEnabled()) {
      // Colore en magenta la zone geom, en cyan la symbol geom et en bleu le
      // centroide
      IGeometry[] debugGeoms = { zoneGeom, symbolGeom, centroid.toGM_Point() };
      Color[] debugColors = { Color.MAGENTA, Color.CYAN, Color.BLUE };
      int[] debugWidths = { 1, 1, 1 };
      String debugMsg = "Calcul zone réduite pour contrainte" + this.toString()
          + " magenta:zone geom, cyan:symbol geom, bleu: centroid";
      // CecileGUIComponent.DisplayGeomsAndPause(debugGeoms, debugColors,
      // debugWidths, debugMsg);
    }
    // FIN DEBUG

    // Compute zone geom related to centroid
    IPolygon result = null;
    // result = (IPolygon) Minkowski.extendPolygonByAuxiliaryPolygonAndCentroid(
    // zoneGeom, symbolGeom, centroid);

    result = (IPolygon) (new MorphologyTransform()
        .minkowskiSumWithCustomPolyCentr(zoneGeom, symbolGeom, centroid));

    this.getConstrainedZone().setCentroidRelatedZoneGeom(result);
  }
}
