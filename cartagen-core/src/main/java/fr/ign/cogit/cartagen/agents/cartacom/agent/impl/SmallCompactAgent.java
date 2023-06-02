package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.action.ConstrainedZoneDrivenDisplacement;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.ConstrainedZoneType;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.state.CartacomAgentState;
import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentState;
import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentStateImpl;
import fr.ign.cogit.cartagen.agents.core.task.TaskStatus;
import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

public class SmallCompactAgent extends CartAComAgentGeneralisation
    implements ISmallCompactAgent {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(ISmallCompactAgent.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The centroid of the small compact agent. Should not necessarily be inside
   * the geometry of the agent. Used to express the constrianed and limit zones
   * in terms of where the centroid is allowed to be rather than in terms of
   * where the agent is allowed to be.
   */
  private IDirectPosition centroid = null;
  /**
   * The limit zones of the agent (see
   * {@link ICartAComAgentGeneralisation#getLimitZones}), translated into zones
   * where the centroid is allowed to be.
   */
  private List<IPolygon> centroidRelatedLimitZones = new ArrayList<IPolygon>();

  /**
   * The networkface containing this small compact agent
   */
  private NetworkFaceAgent containingFace;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a small compact agnet from a geographic object and the
   * associated AGENT agent.
   * 
   * @param feature
   */
  public SmallCompactAgent(IGeneObj feature) {
    super(feature);
    // Initialise the centroid of the small compact
    this.setCentroid(this.getGeom().centroid());
    // Initialise the limit zones
    for (int i = 1; i <= CartacomSpecifications.NB_LIMIT_ZONES; i++) {
      double bufferDist = (((double) i) / CartacomSpecifications.NB_LIMIT_ZONES)
          * GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      this.getLimitZones().add((IPolygon) this.getGeom().buffer(bufferDist));
    }
    // Initialise the centroid related limit zones
    for (int i = 1; i <= CartacomSpecifications.NB_LIMIT_ZONES; i++) {
      double bufferDist = (((double) i) / CartacomSpecifications.NB_LIMIT_ZONES)
          * GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      IPoint centroidPoint = new GM_Point(this.getCentroid());
      SmallCompactAgent.logger
          .debug("(IPolygon) centroidPoint.buffer(bufferDist) = "
              + centroidPoint.buffer(bufferDist));
      this.getCentroidRelatedLimitZones()
          .add((IPolygon) centroidPoint.buffer(bufferDist));
      SmallCompactAgent.logger.debug("this.getCentroidRelatedLimitZones() = "
          + this.getCentroidRelatedLimitZones());
    }

  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for centroid.
   * 
   * @return the centroid
   */
  public IDirectPosition getCentroid() {
    return this.centroid;
  }

  /**
   * Setter for centroid.
   * 
   * @param centroid the centroid to set
   */
  public void setCentroid(IDirectPosition centroid) {
    this.centroid = centroid;
  }

  /**
   * Getter for centroidRelatedLimitZones.
   * 
   * @return the centroidRelatedLimitZones
   */
  public List<IPolygon> getCentroidRelatedLimitZones() {
    return this.centroidRelatedLimitZones;
  }

  /**
   * Getter for the ith element of centroidRelatedLimitZones.
   * @param i the number of the centroidRelatedLimitZone we want to retrieve
   * @return the ith element of centroidRelatedLimitZones, or {@code null} if
   *         less than i centroidRelatedLimitZones
   */
  public IPolygon getCentroidRelatedLimitZone(int i) {
    if (this.centroidRelatedLimitZones.size() < i) {
      return null;
    }
    if (this.centroidRelatedLimitZones.size() == 0) {
      return null;
    }
    return this.centroidRelatedLimitZones.get(i);
  }

  /**
   * Setter for centroidRelatedLimitZones.
   * 
   * @param centroidRelatedLimitZones the centroidRelatedLimitZones to set
   */
  public void setCentroidRelatedLimitZones(
      List<IPolygon> centroidRelatedLimitZones) {
    this.centroidRelatedLimitZones = centroidRelatedLimitZones;
  }

  /**
   * Getter for containingFace.
   * 
   * @return the containingFace
   */
  public NetworkFaceAgent getContainingFace() {
    return this.containingFace;
  }

  /**
   * Setter for containingFace. Also updates the reverse reference from
   * containingFace to {@code this}. To break the reference use
   * {@code this.setContainingFace(null)}
   * 
   * @param containingFace the containingFace to set
   */
  public void setContainingFace(NetworkFaceAgent containingFace) {
    NetworkFaceAgent oldContainingFace = this.containingFace;
    this.containingFace = containingFace;
    if (oldContainingFace != null) {
      oldContainingFace.getContainedSmallCompacts().remove(this);
    }
    if (containingFace != null) {
      if (!containingFace.getContainedSmallCompacts().contains(this)) {
        containingFace.getContainedSmallCompacts().add(this);
      }
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public SmallCompactAgentState getRootState() {
    return (SmallCompactAgentState) super.getRootState();
  }

  /**
   * Retrieves all constraints of {@code this} that are relational constraints
   * having a constrained zone.
   * @return a set of all relational constriants having a constrained zone of
   *         {@code this}
   */
  public Set<MicroMicroRelationalConstraintWithZone> getRelationalConstraintsWithZone() {
    // Initialise returned set
    Set<MicroMicroRelationalConstraintWithZone> relConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();
    // Retrieve all constraints of the agent
    Set<Constraint> constraintsSet = this.getConstraints();
    // Go through the returned set and copy the ones that are relational
    // constriants with a zone to the returned set
    for (Constraint constraint : constraintsSet) {
      if (constraint instanceof MicroMicroRelationalConstraintWithZone) {
        relConstraints.add((MicroMicroRelationalConstraintWithZone) constraint);
      }
    }
    return relConstraints;
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  // Methods of this class

  /**
   * Computes the space allowed for this small compact in order to satisfy a
   * given set of constraints that have constrained zones.
   * 
   * @param constraints A set of the constraints to consider
   * @param limitZoneNumber the number of the limit zone to consider (that
   *          represents the maximum allowed displacement)
   * @param centroidRelated if {@code true}, the space allowed for the centroid
   *          of this small compact is computed, if {@code false} the space
   *          allowed for the entire small compact.
   * @return the space allowed for the small compact (resp. its centroid,
   *         depending on {@code centroidRelated}). If no free space, returns
   *         {@code null}.
   */
  public IGeometry computeFreeSpace(
      Set<MicroMicroRelationalConstraintWithZone> constraints,
      int limitZoneNumber, boolean centroidRelated) {
    // Constructs two sets to host the geometries inside which and outside
    // which this small compact should stay.
    Set<IPolygon> insideZones = new HashSet<IPolygon>();
    Set<IPolygon> outsideZones = new HashSet<IPolygon>();
    // Add the limit zone to consider in the inside set: limit zone or
    // centroid related limit zone depending of the boolean centroidRelated

    SmallCompactAgent.logger.debug("centroidRelated = " + centroidRelated);
    SmallCompactAgent.logger.debug("limitZoneNumber = " + limitZoneNumber);

    SmallCompactAgent.logger.debug("this = " + this);

    SmallCompactAgent.logger.debug("getCentroidRelatedLimitZones() = "
        + this.getCentroidRelatedLimitZones());

    if (centroidRelated) {
      insideZones.add(this.getCentroidRelatedLimitZone(limitZoneNumber));
      SmallCompactAgent.logger
          .debug("this.getCentroidRelatedLimitZone(limitZoneNumber) = "
              + this.getCentroidRelatedLimitZone(limitZoneNumber));
    } else {
      insideZones.add(this.getLimitZone(limitZoneNumber));
      SmallCompactAgent.logger.debug("this.getLimitZone(limitZoneNumber) = "
          + this.getLimitZone(limitZoneNumber));
    }

    SmallCompactAgent.logger.debug("insidesZone = " + insideZones);

    // Loop through the constraints to consider and fills in the inside and
    // outside sets depending on the type of the associated constrained zone
    for (MicroMicroRelationalConstraintWithZone constraint : constraints) {
      // Retrieve the geometry of the zone, related to centroid or not
      // depending on centroidRelated
      SmallCompactAgent.logger.debug("centroidRelated = " + centroidRelated);
      SmallCompactAgent.logger.debug("limitZoneNumber = " + limitZoneNumber);
      IPolygon zoneGeom;
      if (centroidRelated) {
        zoneGeom = constraint.getConstrainedZone().getCentroidRelatedZoneGeom();

        SmallCompactAgent.logger.debug(
            "constraint.getConstrainedZone().getCentroidRelatedZoneGeom() = "
                + constraint.getConstrainedZone().getCentroidRelatedZoneGeom());
      } else {
        zoneGeom = constraint.getConstrainedZone().getZoneGeom();
        SmallCompactAgent.logger
            .debug("constraint.getConstrainedZone().getZoneGeom() = "
                + constraint.getConstrainedZone().getZoneGeom());
      }
      // Retrieves the type of the zone
      ConstrainedZoneType zoneType = constraint.getConstrainedZone()
          .getConstrainedZoneType();
      // Adds the geometry to the right set
      if (zoneType == ConstrainedZoneType.INSIDE) {
        insideZones.add(zoneGeom);
      } else {
        outsideZones.add(zoneGeom);
      }

      SmallCompactAgent.logger.debug("insidesZone = " + insideZones);
    }
    // Combines the polygons of the inside set with an intersection operator
    Set<IGeometry> insideIGeometryZones = new HashSet<IGeometry>(insideZones);
    IGeometry insideZone = CommonAlgorithmsFromCartAGen
        .geomColnIntersection(insideIGeometryZones);
    // Combines the polygons of the outside set with an union operator
    Set<IGeometry> outsideIGeometryZones = new HashSet<IGeometry>(outsideZones);
    IGeometry outsideZone = CommonAlgorithmsFromCartAGen
        .geomColnUnion(outsideIGeometryZones);
    // Computes the difference between the resulting inside zone and outside
    // zone
    SmallCompactAgent.logger.debug("insideZone = " + insideZone);
    SmallCompactAgent.logger.debug("outsideZone = " + outsideZone);
    if (outsideZone != null) {
      return insideZone.difference(outsideZone);
    } else {
      return insideZone;
    }
  }

  /**
   * Computes the space allowed for the small compact in order to stay outside a
   * given area (the askedArea in parameter), while staying within a given limit
   * zone and satisfying a given set of constraints that have constrained zones.
   * Used to compute if the agent could find a position that respects its
   * constraint, if another agent would set its geometry to a new value (the
   * asked area in parameter is then the geometry of the proximity constrained
   * zone that would be computed from the new geometry of the other agent)
   * @author Gokhan Altay
   * @param askedArea the area the agent is expected to say outside
   * @param constraints the set of constraints to consider
   * @param limitZoneNumber the number of the limit zone to consider (that
   *          represents the maximum allowed displacement)
   * @param centroidRelated if {@code true}, the space allowed for the centroid
   *          of the small compact is computed, if {@code false} the space
   *          allowed for the entire small compact
   * @return the space allowed for the small compact (resp. its centroid,
   *         depending on {@code centroidRelated}). If no free space, returns
   *         {@code null}
   */
  public IGeometry computeFreeSpaceWithAdditionalGeom(IGeometry askedArea,
      Set<MicroMicroRelationalConstraintWithZone> constraints,
      int limitZoneNumber, boolean centroidRelated) {
    // Constructs two sets to host the geometries inside which and outside
    // which this small compact should stay.
    Set<IPolygon> insideZones = new HashSet<IPolygon>();
    Set<IPolygon> outsideZones = new HashSet<IPolygon>();
    // Add the limit zone to consider in the inside set: limit zone or
    // centroid related limit zone depending of the boolean centroidRelated
    if (centroidRelated) {
      insideZones.add(this.getCentroidRelatedLimitZone(limitZoneNumber));
    } else {
      insideZones.add(this.getLimitZone(limitZoneNumber));
    }
    // Loop through the constraints to consider and fills in the inside and
    // outside sets depending on the type of the associated constrained zone
    for (MicroMicroRelationalConstraintWithZone constraint : constraints) {
      // Retrieve the geometry of the zone, related to centroid or not
      // depending on centroidRelated
      IPolygon zoneGeom;
      if (centroidRelated) {
        zoneGeom = constraint.getConstrainedZone().getCentroidRelatedZoneGeom();
      } else {
        zoneGeom = constraint.getConstrainedZone().getZoneGeom();
      }
      // Retrieves the type of the zone
      ConstrainedZoneType zoneType = constraint.getConstrainedZone()
          .getConstrainedZoneType();
      // Adds the geometry to the right set
      if (zoneType == ConstrainedZoneType.INSIDE) {
        insideZones.add(zoneGeom);
      } else {
        outsideZones.add(zoneGeom);
      }
    }

    // Add the asked geom (or its translation at the centroid depending on
    // the value of centroidRelated) to the outside set
    IPolygon geomToAdd = null;
    if (centroidRelated) {
      IPolygon symbolGeom = (IPolygon) this.getSymbolGeom();
      // geomToAdd = (IPolygon) Minkowski
      // .extendPolygonByAuxiliaryPolygonAndCentroid((IPolygon) askedArea,
      // symbolGeom, this.getCentroid());

      geomToAdd = (IPolygon) (new MorphologyTransform()
          .minkowskiSumWithCustomPolyCentr((IPolygon) askedArea, symbolGeom,
              this.getCentroid()));

    } else {
      geomToAdd = (IPolygon) askedArea;
    }
    outsideZones.add(geomToAdd);

    // Combines the polygons of the inside set with an intersection operator
    Set<IGeometry> insideIGeometryZones = new HashSet<IGeometry>(insideZones);
    IGeometry insideZone = CommonAlgorithmsFromCartAGen
        .geomColnIntersection(insideIGeometryZones);
    // Combines the polygons of the outside set with an union operator
    Set<IGeometry> outsideIGeometryZones = new HashSet<IGeometry>(outsideZones);
    IGeometry outsideZone = CommonAlgorithmsFromCartAGen
        .geomColnUnion(outsideIGeometryZones);
    // Computes the difference between the resulting inside zone and outside
    // zone
    return insideZone.difference(outsideZone);
  }

  // Inherited from CartacomAgentGeneralisation

  /**
   * {@inheritDoc}
   */
  @Override
  public void goBackToInitialState() {
    super.goBackToInitialState();
    this.setCentroid(this.getRootState().getCentroid());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void goBackToState(SmallCompactAgentState state) {
    SmallCompactAgentState smallCompactState = (SmallCompactAgentState) state;
    this.setCentroid(smallCompactState.getCentroid());
    super.goBackToState(state);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CartacomAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new SmallCompactAgentStateImpl(this,
        (SmallCompactAgentState) previousState, action);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Also displaces the centroid of this small compact.
   */
  @Override
  public void displaceAndRegister(double dx, double dy) {
    super.displaceAndRegister(dx, dy);
    this.getCentroid().setX(this.getCentroid().getX() + dx);
    this.getCentroid().setY(this.getCentroid().getY() + dy);
  }

  /**
   * {@inheritDoc} Here if the action encapsulated in the task initiating the
   * aggregation is {@link ConstrainedZoneDrivenDisplacement}, retrieves the set
   * of constraints types that should be considered when aggregating this
   * action. For each constraint of the agent blonging to one of this classes,
   * creates a new task to aggregate unless a task encapsulating an action in
   * charge of this constraint is already present in the set of tasks already
   * about to be aggregated.
   * <p>
   * En francais: si l'action encapsulée dans la tache initiant l'aggrégation
   * est {@link ConstrainedZoneDrivenDisplacement} (déplacement pour respecter
   * une zone contrainte), récupère la liste des classes de contraintes qui
   * devraient être prises en compte dans cette action. Pour chaque contrainte
   * de l'agent qui appartient à l'une de ces classes, crée une nouvelle tâche à
   * agréger (encapsulant la même action), sauf si une tâche encapsulant une
   * action chargée de satisfaire cette contrainte est présente dans le set de
   * tâches déjà identifiées comme à agréger.
   */
  @Override
  public Set<TryActionTask> getAdditionalTasksToAggregate(
      TryActionTask taskInitiatingAggregation,
      Set<TryActionTask> tasksIdentifiedForAggregation) {
    // Creates the set to return (empty)
    Set<TryActionTask> additionalTasksToAggregate = new HashSet<TryActionTask>();
    // Case where the encapsulated action is of type
    // ConstrainedZoneDrivenDisplacement
    if (taskInitiatingAggregation
        .getActionToTry() instanceof ConstrainedZoneDrivenDisplacement) {
      ConstrainedZoneDrivenDisplacement actionToAggregate = (ConstrainedZoneDrivenDisplacement) taskInitiatingAggregation
          .getActionToTry();
      // Retrieves the types of constraints to consider for aggregation
      Set<Class<? extends MicroMicroRelationalConstraintWithZone>> constraintTypesToConsider = ConstrainedZoneDrivenDisplacement
          .getConstraintsTypesToConsiderForAggregation();
      // From the tasks already identified for aggregation, retrieves the
      // classes of the constraints they are handling (all together)
      Set<Constraint> alreadyHandledConstraint = new HashSet<Constraint>();
      alreadyHandledConstraint
          .add(taskInitiatingAggregation.getActionToTry().getConstraint());
      for (TryActionTask taskIdentifiedForAggregation : tasksIdentifiedForAggregation) {
        alreadyHandledConstraint
            .add(taskIdentifiedForAggregation.getActionToTry().getConstraint());
      }
      // Loop on the current constraints of the agent
      for (Constraint constraint : this.getConstraints()) {
        // We are only interested in the
        // MicroMicroRelationalConstraintWithZone
        // constraints
        if (!(constraint instanceof MicroMicroRelationalConstraintWithZone)) {
          continue;
        }
        MicroMicroRelationalConstraintWithZone microRelConstraint = (MicroMicroRelationalConstraintWithZone) constraint;
        // If the class of the constraint is to be considered but not
        // already
        // handled, create a new task to aggregate
        if (constraintTypesToConsider.contains(microRelConstraint.getClass())
            && !alreadyHandledConstraint
                .contains(microRelConstraint.getClass())) {
          TryActionTask additionalTask = new TryActionTask(this,
              new ConstrainedZoneDrivenDisplacement(this, microRelConstraint,
                  actionToAggregate.getLimitZoneNumber(), 1.0));
          additionalTask.setStatus(TaskStatus.WAITING);
          additionalTasksToAggregate.add(additionalTask);
        }
      }
    }
    return additionalTasksToAggregate;
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
