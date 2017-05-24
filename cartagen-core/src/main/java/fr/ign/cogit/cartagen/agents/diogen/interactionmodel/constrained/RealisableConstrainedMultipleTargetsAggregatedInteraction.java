package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class RealisableConstrainedMultipleTargetsAggregatedInteraction
    extends RealisableConstrainedMultipleTargetsInteraction {

  private Set<GeographicConstraint> constraintsToSatisfy;

  private Set<GeographicConstraint> triggeringConstraints;

  private Set<GeographicConstraint> requestLinkedConstraints;

  /**
   * Constructor for RealizableInteraction
   * @param interaction
   * @param source
   * @param target
   * @param environment
   */
  public RealisableConstrainedMultipleTargetsAggregatedInteraction(
      ConstrainedMultipleTargetsInteraction interaction, IDiogenAgent source,
      Set<IDiogenAgent> targets, Environment environment,
      Set<GeographicConstraint> constraintsToSatisfy,
      Set<GeographicConstraint> triggeringConstraints,
      Set<GeographicConstraint> requestLinkedConstraints) {
    super(interaction, source, targets, environment);
    this.constraintsToSatisfy = constraintsToSatisfy;
    this.triggeringConstraints = triggeringConstraints;
    this.requestLinkedConstraints = requestLinkedConstraints;
  }

  /**
   * Perform this realizable interaction
   * 
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  public void perform() throws InterruptedException, ClassNotFoundException {
    ((ConstrainedMultipleTargetsAggregatedInteraction) this.getInteraction())
        .perform(this.getEnvironment(), this.getSource(), targets,
            this.constraintsToSatisfy, this.triggeringConstraints,
            this.requestLinkedConstraints);
  }

}
