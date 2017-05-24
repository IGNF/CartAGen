package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.action.RotationAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.RoadOrientation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedSingleTargetInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class RotationInteraction extends ConstrainedSingleTargetInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static RotationInteraction singletonObject;

  /**
   * A private Constructor prevents any other class from instantiating.
   * @return
   */
  private RotationInteraction() {
    super();
    loadSpecification();
    this.setWeight(2);
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized RotationInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new RotationInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      IDiogenAgent target, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof RoadOrientation) {
        constraint = c;
        break;
      }
    }

    RotationAction action = new RotationAction((ISmallCompactAgent) source,
        constraint, 1);

    this.setAction(action);

    action.compute();

  }

}
