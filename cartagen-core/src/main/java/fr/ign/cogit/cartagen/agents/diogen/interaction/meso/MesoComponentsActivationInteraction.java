package fr.ign.cogit.cartagen.agents.diogen.interaction.meso;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.MesoComponentsActivation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.MesoComponentsSatisfaction;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockBuildingsDeletionCongestionInteraction class is a ConstrainedInteraction
 * used to resolve a density constraint.
 * @author AMaudet
 * 
 */
public class MesoComponentsActivationInteraction
    extends ConstrainedDegenerateInteraction {

  private static MesoComponentsActivationInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private MesoComponentsActivationInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.MESO_COMPONENTS_SATISFACTION_CLASS_NAME);
  }

  public static synchronized MesoComponentsActivationInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new MesoComponentsActivationInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof MesoComponentsSatisfaction) {
        constraint = c;
        break;
      }
    }

    // TODO
    // Actually limited to building container. This needs to be changed.
    MesoComponentsActivation<BuildingAgent> action = new MesoComponentsActivation<BuildingAgent>(
        source, constraint, 1.0);
    setAction(action);
    action.compute();

  }

}
