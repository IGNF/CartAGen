package fr.ign.cogit.cartagen.agents.diogen.interaction.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;
import fr.ign.cogit.cartagen.agents.diogen.lifecycle.PadawanAdvancedLifeCycle;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;

/**
 * SuspendActivityInteraction class is a Constrained Degenerate Interaction used
 * to specify to the agent life cycle to stop.
 * @author AMaudet
 * 
 */
public class ActivateComponentsInteraction
    extends ConstrainedMultipleTargetsInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static ActivateComponentsInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private ActivateComponentsInteraction() {
    super();
    loadSpecification();
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized ActivateComponentsInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ActivateComponentsInteraction();
    }
    return singletonObject;
  }

  /**
   * Perform a SuspendActivity interaction for the agent source. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    // TODO get the type of the components instead to use BuidlingAgent
    @SuppressWarnings("unchecked")
    MesoAgent<BuildingAgent> csource = (MesoAgent<BuildingAgent>) source;
    Set<BuildingAgent> ctargets = new HashSet<>();
    for (IAgent a : targets) {
      ctargets.add((BuildingAgent) a);
    }

    AgentLifeCycle lifeCycle = source.getLifeCycle();
    if (lifeCycle.getClass().isAssignableFrom(PadawanAdvancedLifeCycle.class)) {
      // PadawanAdvancedLifeCycle padawanAdvancedLifeCycle =
      // (PadawanAdvancedLifeCycle) lifeCycle;

      // ArrayList<GeographicObjectAgent> components = new
      // ArrayList<GeographicObjectAgent>();
      // for (Agent target : targets) {
      // GeographicObjectAgent aGeo = (GeographicObjectAgent) target;
      // if (!aGeo.getFeature().isDeleted()) {
      // components.add(aGeo);
      // }
      // }

      // int nb = components.size();
      // int i = 1;

      ArrayList<BuildingAgent> components = new ArrayList<>(ctargets);
      AgentGeneralisationScheduler.getInstance().addToTheTop(csource);

      while (targets.size() > 0) {
        GeographicObjectAgent a = csource
            .getBestComponentToActivate(components);

        if (a.getStructureAgents().size() == 0) {
          AgentGeneralisationScheduler.getInstance().addToTheTop(a);
          // a.activate();
          // csource.manageInternalSideEffects(a);
          components.remove(a);
        } else {
          // TODO adapt to alignment issues
          // StructureActivationAction action = new StructureActivationAction(
          // a,
          // constraints
          // .get(InteractionConfiguration.MESO_COMPONENTS_SATISFACTION_CLASS_NAME),
          // this.getWeight());
          // action.compute();
          // for (InternStructureAgent structure : a.getStructureAgents()) {
          // for (GeographicObjectAgent agent : structure.getComponents()) {
          // components.remove(agent);
          // }
          // }
        }

      }
    } else {
      // TODO immplements an error message
    }
  }
}
