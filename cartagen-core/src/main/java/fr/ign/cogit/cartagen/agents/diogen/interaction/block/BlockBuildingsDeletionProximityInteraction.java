package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDeletionProximityAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Proximity;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockBuildingsDeletionProximityInteraction class is an interaction used to
 * solve proximity constraint.
 * @author AMaudet
 * 
 */
public class BlockBuildingsDeletionProximityInteraction
    extends ConstrainedDegenerateInteraction {

  private static BlockBuildingsDeletionProximityInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockBuildingsDeletionProximityInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.PROXIMITY_CLASS_NAME);
  }

  public static synchronized BlockBuildingsDeletionProximityInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockBuildingsDeletionProximityInteraction();
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

    BlockBuildingsDeletionProximityAction action = new BlockBuildingsDeletionProximityAction(
        (BlockAgent) source, constraint, 1.0);
    setAction(action);
    action.compute();
  }

}
