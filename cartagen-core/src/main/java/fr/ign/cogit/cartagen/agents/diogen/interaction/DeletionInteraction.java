package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.DeletionAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Size;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * AffinityInteraction class is a Constrained Degenerate Interaction used to
 * resolve an Elongation constraint.
 * @author AMaudet
 * 
 */
public class DeletionInteraction extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static DeletionInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private DeletionInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.SIZE_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized DeletionInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new DeletionInteraction();
    }
    return singletonObject;
  }

  /**
   * 
   * {@inheritDoc}
   */
  // @Override
  // public int preconditionByConstraint(Environment environment, Agent source,
  // GeographicConstraint constraint) {
  // String name = constraint.getClass().getName();
  // // if they are a size constraint, the goal area needs to be null.
  // if (name.equals(InteractionConfiguration.SIZE_CLASS_NAME)) {
  // Size cconstraint = (Size) constraint;
  // if (cconstraint.getGoalArea() != 0.0) {
  // return Integer.MIN_VALUE;
  // }
  // }
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
  public boolean isGoalAreaNoNull(Environment environment, IAgent source,
      GeographicConstraint constraint) {
    Size cconstraint = (Size) constraint;
    return cconstraint.getGoalArea() != 0.0;
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

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Size) {
        constraint = c;
        break;
      }
    }
    Action action = new DeletionAction(csource, constraint, 1.0);
    setAction(action);
    action.compute();
  }

}
