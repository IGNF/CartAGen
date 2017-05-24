package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDeletionProximityMultiCriterionAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Proximity;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class BlockBuildingsDeletionProximityMultiCriterionInteraction
    extends ConstrainedDegenerateInteraction {

  private static BlockBuildingsDeletionProximityMultiCriterionInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockBuildingsDeletionProximityMultiCriterionInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.PROXIMITY_CLASS_NAME);
  }

  public static synchronized BlockBuildingsDeletionProximityMultiCriterionInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockBuildingsDeletionProximityMultiCriterionInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Proximity) {
        constraint = c;
        break;
      }
    }

    BlockBuildingsDeletionProximityMultiCriterionAction action = new BlockBuildingsDeletionProximityMultiCriterionAction(
        (BlockAgent) source, constraint, 1.0);
    setAction(action);
    action.compute();
  }

}
