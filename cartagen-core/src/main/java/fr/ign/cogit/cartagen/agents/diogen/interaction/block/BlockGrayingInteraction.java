package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockGrayingAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Density;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockGrayingInteraction is a degenerate interaction changing the color of an
 * urban block to grey and removing all contained building.
 * 
 * @author AMaudet
 * 
 */
public class BlockGrayingInteraction extends ConstrainedDegenerateInteraction {

  private static BlockGrayingInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockGrayingInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.DENSITY_CLASS_NAME);
    this.setWeight(2);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized BlockGrayingInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new BlockGrayingInteraction();
    }
    return singletonObject;
  }

  /**
   * Perform the BlockGrayingInteraction on source. Use the original
   * BlockGrayingAction {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Density) {
        constraint = c;
        break;
      }
    }

    BlockGrayingAction action = new BlockGrayingAction((BlockAgent) source,
        constraint, 2.0);
    setAction(action);
    action.compute();

  }

  /**
   * 
   * {@inheritDoc}
   */
  // @Override
  // public int preconditionByConstraint(Environment environment, Agent source,
  // GeographicConstraint constraint) {
  // String name = constraint.getClass().getName();
  // BlockAgent csource = (BlockAgent) source;
  //
  // // if they are a density constraint, the initial density needs to be taller
  // // than the threshold
  // // to be grayed.
  // if (name.equals(InteractionConfiguration.DENSITY_CLASS_NAME)) {
  // if (csource.getInitialSimulatedDensity() <
  // GeneralisationSpecifications.DENSITE_LIMITE_GRISAGE_ILOT) {
  // return Integer.MIN_VALUE;
  // }
  // }
  // return super.preconditionByConstraint(environment, source, constraint);
  // }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isDensityTaller(Environment environment, IAgent source,
      GeographicConstraint constraint) {

    BlockAgent csource = (BlockAgent) source;
    return csource
        .getInitialSimulatedDensity() < GeneralisationSpecifications.DENSITE_LIMITE_GRISAGE_ILOT;
  }

}
