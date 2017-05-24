package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.micro.EnlargementAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Convexity;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Size;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * EnlargmentInteraction class is a Constrained Degenerate Interaction used to
 * resolve an Elongation constraint.
 * @author AMaudet
 * 
 */
public class EnlargementInteraction extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static EnlargementInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private EnlargementInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.SIZE_CLASS_NAME);
    // this.addConstraintTypeName(InteractionConfiguration.CONVEXITY_CLASS_NAME);
    this.setWeight(1);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized EnlargementInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new EnlargementInteraction();
    }
    return singletonObject;
  }

  /**
   * 
   * {@inheritDoc}
   */
  // public int preconditionByConstraint(Environment environment, Agent source,
  // GeographicConstraint constraint) {
  // String name = constraint.getClass().getName();
  //
  // // if they are a Convexity Constraint, we need to ensure that the object is
  // // convex
  // // Elsewhere, the Interaction is not recommended.
  // if (name.equals(InteractionConfiguration.CONVEXITY_CLASS_NAME)) {
  // Convexity cconstraint = (Convexity) constraint;
  // cconstraint.computeSatisfaction();
  // // System.out.println(CommonAlgorithms.convexity( ((GeographicObjectAgent)
  // // source).getGeom()));
  // if (CommonAlgorithms
  // .convexity(((GeographicObjectAgent) source).getGeom()) < 1) {
  // return -1;
  // }
  // return 0;
  // }
  //
  // // if they are a size constraint, the goal area needs to be not null.
  // if (name.equals(InteractionConfiguration.SIZE_CLASS_NAME)) {
  // Size cconstraint = (Size) constraint;
  // cconstraint.computeSatisfaction();
  // if (cconstraint.getGoalArea() <= 0.0) {
  // return Integer.MIN_VALUE;
  // }
  // }
  //
  // return super.preconditionByConstraint(environment, source, constraint);
  // }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isConcave(Environment environment, IAgent source,
      GeographicConstraint constraint) {
    Convexity cconstraint = (Convexity) constraint;
    cconstraint.computeSatisfaction();
    return CommonAlgorithms
        .convexity(((GeographicObjectAgent) source).getGeom()) < 1;
  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isGoalAreaNull(Environment environment, IAgent source,
      GeographicConstraint constraint) {
    Size cconstraint = (Size) constraint;
    cconstraint.computeSatisfaction();
    return cconstraint.getGoalArea() <= 0.0;
  }

  /**
   * Perform an AffinityInteraction for the agent source. For now, use the
   * original AffinityAction. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    IMicroAgentGeneralisation csource = ((IMicroAgentGeneralisation) source);

    Size constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Size) {
        constraint = (Size) c;
        break;
      }
    }
    Action action = new EnlargementAction(csource, constraint, 2.0,
        constraint.getGoalArea());
    setAction(action);
    action.compute();
  }

}
