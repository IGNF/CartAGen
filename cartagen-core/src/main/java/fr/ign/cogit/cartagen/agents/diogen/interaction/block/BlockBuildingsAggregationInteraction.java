package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsAggregationAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Elongation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class BlockBuildingsAggregationInteraction
    extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static BlockBuildingsAggregationInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockBuildingsAggregationInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized BlockBuildingsAggregationInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockBuildingsAggregationInteraction();
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
    BlockAgent csource = ((BlockAgent) source);
    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Elongation) {
        constraint = c;
        break;
      }
    }
    Action action = new BlockBuildingsAggregationAction(csource, constraint, 0);
    setAction(action);
    action.compute();
  }

}
