package fr.ign.cogit.cartagen.agents.diogen.interaction.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDeletionCongestionAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Density;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * BlockBuildingsDeletionCongestionInteraction class is a ConstrainedInteraction
 * used to resolve a density constraint.
 * @author AMaudet
 * 
 */
public class BlockBuildingsDeletionCongestionInteraction
    extends ConstrainedDegenerateInteraction {

  private static BlockBuildingsDeletionCongestionInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private BlockBuildingsDeletionCongestionInteraction() {
    super();
    this.loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.DENSITY_CLASS_NAME);
    this.setWeight(2);
  }

  public static synchronized BlockBuildingsDeletionCongestionInteraction getInstance() {
    if (BlockBuildingsDeletionCongestionInteraction.singletonObject == null) {
      BlockBuildingsDeletionCongestionInteraction.singletonObject = new BlockBuildingsDeletionCongestionInteraction();
    }
    return BlockBuildingsDeletionCongestionInteraction.singletonObject;
  }

  /**
   * 
   * {@inheritDoc}
   */
  // @Override
  // public int preconditionByConstraint(Environment environment, Agent source,
  // GeographicConstraint constraint) {
  // String name = constraint.getClass().getName();
  //
  // // if they are a density constraint, and the density is actually smaller
  // // than the
  // // original density, this interaction is forbidden.
  // if (name.equals(InteractionConfiguration.DENSITY_CLASS_NAME)) {
  // if (((Density) constraint).getSimulatedDensity() < ((BlockAgent) source)
  // .getInitialDensity()) {
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
  public boolean isDensityDecreasing(Environment environment, IAgent source,
      GeographicConstraint constraint) {
    return ((Density) constraint).getSimulatedDensity() < ((BlockAgent) source)
        .getInitialDensity();
  }

  /**
   * 
   * {@inheritDoc}
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

    BlockBuildingsDeletionCongestionAction action = new BlockBuildingsDeletionCongestionAction(
        (BlockAgent) source, constraint, 1,
        GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE,
        ((Density) constraint).getRate(), 0);
    this.setAction(action);
    action.compute();

  }
}
