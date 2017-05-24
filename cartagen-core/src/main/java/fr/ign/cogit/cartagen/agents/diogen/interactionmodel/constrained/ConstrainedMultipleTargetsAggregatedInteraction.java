package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregatedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public abstract class ConstrainedMultipleTargetsAggregatedInteraction extends
    ConstrainedMultipleTargetsInteraction implements AggregatedInteraction {

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    this.perform(environment, source, targets, constraints, constraints,
        constraints);
  }

  public abstract void perform(Environment environment, IDiogenAgent source,
      Set<? extends IDiogenAgent> targets,
      Set<GeographicConstraint> constraintsToSatisfy,
      Set<GeographicConstraint> triggeringConstraints,
      Set<GeographicConstraint> requestLinkedConstraints)
      throws InterruptedException, ClassNotFoundException;
}
