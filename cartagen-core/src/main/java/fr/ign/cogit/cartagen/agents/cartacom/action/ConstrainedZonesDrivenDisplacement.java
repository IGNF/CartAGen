/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.core.task.AggregatedActionImpl;
import fr.ign.cogit.cartagen.agents.core.task.Task;
import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;
import fr.ign.cogit.cartagen.algorithms.network.ConstrainedZonesDrivenDisplacementAlgo;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

/**
 * An action that displaces a small compact agent in order to satisfy a set of
 * constraints that are associated with constrained zones.
 * 
 * @author CDuchene
 * 
 */
public class ConstrainedZonesDrivenDisplacement extends AggregatedActionImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(ConstrainedZonesDrivenDisplacement.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The number of the limit zone the agent should stay within during the
   * displacement.
   */
  private int limitZoneNumber;

  /**
   * Set of the constraints (with constrained zone) this action is expected to
   * satisfy if possible.
   */
  // private Set<MicroMicroRelationalConstraintWithZone> constraintsToSatisfy
  // =
  // new HashSet<MicroMicroRelationalConstraintWithZone>();

  /**
   * Among the constraints to satisfy, the ones that had proposed to trigger
   * this action (the other ones have been added during the aggregation).
   */
  // private Set<MicroMicroRelationalConstraintWithZone> triggeringConstraints
  // =
  // new HashSet<MicroMicroRelationalConstraintWithZone>();

  /**
   * Among the triggering constraints, the ones for which a request for this
   * action has been sent by the other agent sharing the constraint.
   */
  // private Set<MicroMicroRelationalConstraintWithZone>
  // requestLinkedConstraints =
  // new HashSet<MicroMicroRelationalConstraintWithZone>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an aggregated displacement action for a cartacom generalisation
   * agent, with the limit zone number passed as parameter
   * 
   * @param agent the agent that will execute the aggregated displacement
   * @param limitZoneNumber the number of the limit zone the agent should stay
   *          within during its aggregated displacement.
   */
  public ConstrainedZonesDrivenDisplacement(ISmallCompactAgent agent,
      RelationalConstraint constraint, int limitZoneNumber, double weight) {
    // Constructs an action with the task owner as agent, and unassigned
    // values of constraint and weight
    super(agent, constraint, weight);
    // Sets its limit zone number
    this.setLimitZoneNumber(limitZoneNumber);
  }

  /**
   * Constructor inherited from super class set private to force the use of the
   * specific constructor.
   * 
   * @param agent the agent
   */
  private ConstrainedZonesDrivenDisplacement(ICartacomAgent agent) {
    super(agent);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

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
   */
  @Override
  public MicroMicroRelationalConstraintWithZone getConstraint() {
    return (MicroMicroRelationalConstraintWithZone) super.getConstraint();
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  // Inherited from CartAComAction

  /**
   * {@inheritDoc}
   * <p>
   * Returns null because this action is not supposed to be directly
   * encapsulated in a task that is dependent on a conversation. Should be
   * written is this should change.
   */
  @Override
  public Object computeDescribingArgument() {
    return null;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Returns null because this action is not supposed to be directly
   * encapsulated in a task that is dependent on a conversation. Should be
   * written is this should change.
   */
  @Override
  public FailureValidity computeFailureValidity() {
    return null;
  }

  // Inherited from Action

  /**
   * Tries to find a position that respects the constraints associated to the
   * dependant tasks linked to {@code this} (i.e. the tasks that have been
   * aggregated to generate {@code this}), using the constrained zones
   * associated to every task. First the agent seeks for free space taking into
   * account all contraints, then if no free space can be found the constraints
   * are relaxed by decreasing order of importance. For two constraints of the
   * same importance, a constraint for which the other agent sent a request for
   * action is relaxed after.
   */
  @Override
  public ActionResult compute() {
    ConstrainedZonesDrivenDisplacement.logger
        .info("Starting execution of action " + this.getClass().getName()
            + " on agent " + this.getAgent().toString() + ".");

    // Constructs sets of classified constraints to satisfy
    // Set of the constraints (with constrained zone) this action is
    // expected to
    // satisfy if possible.
    Set<MicroMicroRelationalConstraintWithZone> constraintsToSatisfy = new HashSet<MicroMicroRelationalConstraintWithZone>();
    // Among the constraints to satisfy, the ones that had proposed to
    // trigger this
    // action (the other ones have been added during the aggregation).
    Set<MicroMicroRelationalConstraintWithZone> triggeringConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();
    // Among the triggering constraints, the ones for which a request for
    // this action
    // has been sent by the other agent sharing the constraint.
    Set<MicroMicroRelationalConstraintWithZone> requestLinkedConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();

    // Initialises these sets of constraints
    // Retrieve encapsulating task (if any) and its dependent tasks (if any,
    // case where the encapsulating task is an aggregated task)
    Set<Task> dependentTasks;
    Task encapsTask = this.getEncapsulatingTask();
    if ((encapsTask != null) && (encapsTask.getDependentTasks() != null)) {
      dependentTasks = new HashSet<Task>(encapsTask.getDependentTasks());
      logger.debug(
          "encapsTask.getDependentTasks() " + encapsTask.getDependentTasks());
    } else {
      dependentTasks = new HashSet<Task>();
    }
    for (Task dependentTask : dependentTasks) {
      if (!(dependentTask instanceof TryActionTask)) {
        continue;
      }
      logger.debug("Dependent task " + dependentTask);
      TryActionTask task = (TryActionTask) dependentTask;

      if (task.getActionToTry() == null) {
        continue;
      }

      // Check the handled constraint is of type
      // MicroMicroRelationalConstraintWithZone
      if (!(task.getActionToTry()
          .getConstraint() instanceof MicroMicroRelationalConstraintWithZone)) {
        ConstrainedZonesDrivenDisplacement.logger.error(
            "Found a dependent task with constraint that has no associated constrained zone on "
                + this.toString());
        continue;
      }
      // Now it is of type MicroMicroRelationalConstraintWithZone
      MicroMicroRelationalConstraintWithZone constraint = (MicroMicroRelationalConstraintWithZone) task
          .getActionToTry().getConstraint();

      logger.debug("Constraint " + constraint);
      // Adds it to the set of constriants to satisfy
      constraintsToSatisfy.add(constraint);
      // Checks if the current task is triggering and if yes, adds it to
      // the set
      // of triggering constraints
      if (!task.isTriggeringPartOfAggregatedTask()) {
        continue;
      }

      triggeringConstraints.add(constraint);
      // Checks if the current task is dependent on a conversation and if
      // yes, adds
      // the constriant to the set of 'request linked constraints'
      if (task.getDependentConversation() == null) {
        continue;
      }
      requestLinkedConstraints.add(constraint);
    } // loop on the dependent tasks to initialise the sets of constraints

    // Debug message
    ConstrainedZonesDrivenDisplacement.logger
        .debug("Beginning of constrained zones aggregated displacement");
    ConstrainedZonesDrivenDisplacement.logger
        .debug("Handled constraints: " + constraintsToSatisfy.toString());
    ConstrainedZonesDrivenDisplacement.logger
        .debug("Triggering constraints: " + triggeringConstraints.toString());
    ConstrainedZonesDrivenDisplacement.logger
        .debug("Triggering constraints linked to a conversation: "
            + requestLinkedConstraints.toString());

    // Launch the actual search for a better position
    ConstrainedZonesDrivenDisplacementAlgo displAlgo = new ConstrainedZonesDrivenDisplacementAlgo(
        this.getAgent(), this.getLimitZoneNumber(), constraintsToSatisfy,
        triggeringConstraints, requestLinkedConstraints);
    return displAlgo.compute();

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
