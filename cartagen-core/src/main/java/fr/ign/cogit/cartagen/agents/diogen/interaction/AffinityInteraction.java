package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.micro.AffinityAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Elongation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * AffinityInteraction class is a Constrained Degenerate Interaction used to
 * resolve an Elongation constraint.
 * @author AMaudet
 * 
 */
public class AffinityInteraction extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static AffinityInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private AffinityInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized AffinityInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new AffinityInteraction();
    }
    return singletonObject;
  }

  /**
   * Perform an AffinityInteraction for the agent source. Use the original
   * AffinityAction. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Elongation) {
        constraint = c;
        break;
      }
    }
    IMicroAgentGeneralisation csource = ((IMicroAgentGeneralisation) source);

    double angle = csource.getGeneralOrientation() + Math.PI / 2;
    double coef = csource.getInitialElongation()
        / ((Elongation) constraint).getElongationCourante();
    Action action = new AffinityAction(csource, constraint, 1.0, angle, coef);
    setAction(action);
    action.compute();
  }

}
