package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDisplacementRuasAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Proximity;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockBuildingsDisplacementRuasInteraction is an Interaction used to solve
 * proximity constraint.
 * @author AMaudet
 * 
 */
public class BlockBuildingsDisplacementRuasInteraction
    extends ConstrainedDegenerateInteraction {

  private static BlockBuildingsDisplacementRuasInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockBuildingsDisplacementRuasInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.PROXIMITY_CLASS_NAME);
    this.setWeight(3);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized BlockBuildingsDisplacementRuasInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockBuildingsDisplacementRuasInteraction();
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

    BlockBuildingsDisplacementRuasAction action = new BlockBuildingsDisplacementRuasAction(
        (BlockAgent) source, constraint, 3.0, 40, 10);
    setAction(action);
    action.compute();

  }

}
