/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.building2.BuildingProximity;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingnetface.BuildingTopology;
import fr.ign.cogit.cartagen.agents.core.task.AggregableActionImpl;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * An action that displaces a small compact agent in order to satisfy a
 * constraint that is associated with a constrained zone.
 * 
 * @author CDuchene
 * 
 */
public class ConstrainedZoneDrivenDisplacement extends AggregableActionImpl {

  /**
   * Constructs an action of "displacement driven by a constrained zone" from a
   * cartacom small compact agent, a relational constraint having a constrained
   * zone and a weight.
   * 
   * @param agent the cartacom agent that should do the displacement
   * @param constraint the constraint to take into account during the
   *          displacement
   * @param weight the weight with which this action is proposed
   */
  public ConstrainedZoneDrivenDisplacement(ICartAComAgentGeneralisation agent,
      RelationalConstraint constraint, int limitZoneNumber, double weight) {
    super(agent, constraint, weight);
    this.setLimitZoneNumber(limitZoneNumber);
  }

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(ConstrainedZoneDrivenDisplacement.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * The number of the limit zone the agent should stay within during the
   * displacement.
   */
  private int limitZoneNumber;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Returns a set of the constaints classes to consider when aggregating this
   * action.
   * 
   * @return
   */
  public static Set<Class<? extends MicroMicroRelationalConstraintWithZone>> getConstraintsTypesToConsiderForAggregation() {
    Set<Class<? extends MicroMicroRelationalConstraintWithZone>> constraintsTypes = new HashSet<Class<? extends MicroMicroRelationalConstraintWithZone>>();
    constraintsTypes.add(BuildingProximity.class);
    constraintsTypes.add(
        fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.BuildingProximity.class);
    constraintsTypes.add(BuildingTopology.class);
    return constraintsTypes;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for limitZoneNumber.
   * 
   * @return the limitZoneNumber
   */
  public int getLimitZoneNumber() {
    return this.limitZoneNumber;
  }

  /**
   * Setter for limitZoneNumber.
   * 
   * @param limitZoneNumber the limitZoneNumber to set
   */
  public void setLimitZoneNumber(int limitZoneNumber) {
    this.limitZoneNumber = limitZoneNumber;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public ISmallCompactAgent getAgent() {
    return (ISmallCompactAgent) super.getAgent();
  }

  /**
   * {@inheritDoc}
   * <p>
   */
  @Override
  public boolean modifieEnvironmentRepresentation() {
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public MicroMicroRelationalConstraintWithZone getConstraint() {
    return (MicroMicroRelationalConstraintWithZone) super.getConstraint();
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  // Inherited from AggregableAction

  /**
   * {@inheritDoc}
   * <p>
   * Here the aggregated action is always an instance of
   * {@link ConstrainedZonesDrivenDisplacement}, parameterised with the same
   * limit zone number as {@code this}.
   */
  @Override
  public CartacomAction getAggregatedAction() {
    return new ConstrainedZonesDrivenDisplacement(this.getAgent(),
        this.getConstraint(), this.getLimitZoneNumber(), this.getWeight());
  }

  // Inherited from CartAComAction

  /**
   * {@inheritDoc}
   * <p>
   * Generates an FSMBased argument comprising the
   */
  @Override
  public Object computeDescribingArgument() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc} Here the validity is: until the agent's environment is
   * modified. TODO: could take into account a number of time this action has
   * already failed on this agent.
   */
  @Override
  public FailureValidity computeFailureValidity() {
    return FailureValidity.ENVIRONMENT_MODIFIED;
  }

  // Inherited from Action

  /**
   * Computes and executes a displacement outside (resp. inside) of the
   * constrained zone of the constraint to satisfy, depending of the nature of
   * the constrained zone associated to the constraint this action is trying to
   * satisfy.
   */
  @Override
  public ActionResult compute() {
    // TODO La version lull est plus compliquée, voir s'il faut la
    // reproduire.

    ConstrainedZoneDrivenDisplacement.logger
        .info("Starting execution of action " + this.getClass().getName()
            + " on agent " + this.getAgent().toString() + ".");
    // Computes the free space related to centroid, constrained by the
    // constraint proposing this action
    Set<MicroMicroRelationalConstraintWithZone> zonesSet = new HashSet<MicroMicroRelationalConstraintWithZone>();
    zonesSet.add(this.getConstraint());
    IGeometry centroidFreeSpace = this.getAgent().computeFreeSpace(zonesSet,
        this.getLimitZoneNumber(), true);
    // If no free space returns unchanged
    if (centroidFreeSpace.isEmpty()) {
      ConstrainedZoneDrivenDisplacement.logger
          .info("No free space found - agent left unchanged by action "
              + this.getClass().getName() + ".");
      return ActionResult.UNCHANGED;
    }
    // In this free space, finds the nearest point from the initial position
    // of the centroid
    IDirectPosition nearestFreePoint = CommonAlgorithmsFromCartAGen
        .getNearestVertexFromPoint(centroidFreeSpace,
            this.getAgent().getRootState().getCentroid());
    // Deduces the displacement vector
    Vector2D displVect = new Vector2D(this.getAgent().getCentroid(),
        nearestFreePoint);
    // If displacement vector null, returns unchanged
    if (displVect.isNull()) {
      ConstrainedZoneDrivenDisplacement.logger.info(
          "Computed displacement vector null - agent left unchanged by action "
              + this.getClass().getName() + ".");
      return ActionResult.UNCHANGED;
    }
    this.getAgent().displaceAndRegister(displVect.getX(), displVect.getY());
    ConstrainedZoneDrivenDisplacement.logger.info("Agent displaced by vector ("
        + displVect.getX() + " , " + displVect.getY() + ").");
    return ActionResult.MODIFIED;
  }

  // Inherited from Object, overrides definition in ActionImpl

  /**
   * Another action is equal to this one if it belongs to the same class,
   * concerns the same agent and the same constraint, and has the same
   * {@link #limitZoneNumber}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConstrainedZoneDrivenDisplacement other = (ConstrainedZoneDrivenDisplacement) obj;
    if (this.isActItselfAction() != other.isActItselfAction())
      return false;
    if (!this.getConstraint().equals(other.getConstraint()))
      return false;
    if (!this.getAgent().equals(other.getAgent()))
      return false;
    if (this.getLimitZoneNumber() != other.getLimitZoneNumber())
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return 10 * super.hashCode() + this.getLimitZoneNumber();
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
