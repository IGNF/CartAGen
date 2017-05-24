package fr.ign.cogit.cartagen.agents.diogen.interaction.control;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.lifecycle.PadawanAdvancedLifeCycle;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;

/**
 * SuspendActivityInteraction class is a Constrained Degenerate Interaction used
 * to specify to the agent life cycle to stop.
 * @author AMaudet
 * 
 */
public class SuspendActivityInteraction
    extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static SuspendActivityInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private SuspendActivityInteraction() {
    super();
    loadSpecification();
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized SuspendActivityInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new SuspendActivityInteraction();
    }
    return singletonObject;
  }

  /**
   * Perform a SuspendActivity interaction for the agent source. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    AgentLifeCycle lifeCycle = source.getLifeCycle();
    if (lifeCycle.getClass().isAssignableFrom(PadawanAdvancedLifeCycle.class)) {
      PadawanAdvancedLifeCycle padawanAdvancedLifeCycle = (PadawanAdvancedLifeCycle) lifeCycle;
      padawanAdvancedLifeCycle.suspendLifeCycle();
    } else {
      // TODO immplements an error message
    }
  }
}
