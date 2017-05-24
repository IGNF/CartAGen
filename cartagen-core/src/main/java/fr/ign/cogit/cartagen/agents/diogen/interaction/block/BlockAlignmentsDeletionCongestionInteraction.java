package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockAlignmentsDeletionCongestionAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.NonOverlappingOfAlignments;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockAlignmentsDeletionCongestionInteraction class is a Constrained
 * Degenerate Interaction used to resolve an overlapping constraint
 * @author AMaudet
 * 
 */
public class BlockAlignmentsDeletionCongestionInteraction
    extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static BlockAlignmentsDeletionCongestionInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockAlignmentsDeletionCongestionInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.NON_OVERLAPPING_CLASS_NAME);
    this.setWeight(2);
  }

  /**
   * Getter of the unique instance.
   * @return
   */
  public static synchronized BlockAlignmentsDeletionCongestionInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockAlignmentsDeletionCongestionInteraction();
    }
    return singletonObject;
  }

  /**
   * Perform a BlockAlignmentsDeletionCongestionInteraction for the agent
   * source. For now, use the original BlockAlignmentsDeletionCongestionAction.
   * {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof NonOverlappingOfAlignments) {
        constraint = c;
        break;
      }
    }

    BlockAgent csource = (BlockAgent) source;
    Action action = new BlockAlignmentsDeletionCongestionAction(csource,
        constraint, 0);
    setAction(action);
    action.compute();

  }

}
