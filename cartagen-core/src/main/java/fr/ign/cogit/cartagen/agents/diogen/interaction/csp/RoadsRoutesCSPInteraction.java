package fr.ign.cogit.cartagen.agents.diogen.interaction.csp;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.RoadsCarryingRoutesNetworkAgent;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes.CSPSolver;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.HikingDataset;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class RoadsRoutesCSPInteraction
    extends ConstrainedMultipleTargetsInteraction {

  private static RoadsRoutesCSPInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private RoadsRoutesCSPInteraction() {
    super();
    loadSpecification();
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized RoadsRoutesCSPInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new RoadsRoutesCSPInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    HikingDataset dataset = (HikingDataset) ((RoadsCarryingRoutesNetworkAgent) source)
        .getDataset();
    CSPSolver.LaunchCSPSolver(dataset, 4);

  }

}
