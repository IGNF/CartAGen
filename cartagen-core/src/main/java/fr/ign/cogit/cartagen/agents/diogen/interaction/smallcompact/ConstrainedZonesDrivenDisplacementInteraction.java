package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.action.ConstrainedZonesDrivenDisplacement;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsAggregatedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.algorithms.network.ConstrainedZonesDrivenDisplacementAlgo;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public abstract class ConstrainedZonesDrivenDisplacementInteraction
    extends ConstrainedMultipleTargetsAggregatedInteraction {

  protected ConstrainedZonesDrivenDisplacementInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.DENSITY_CLASS_NAME);
    this.setWeight(2);
  }

  public abstract int getLimitZoneNumber();

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<? extends IDiogenAgent> targets,
      Set<GeographicConstraint> constraintsToSatisfy,
      Set<GeographicConstraint> triggeringConstraints,
      Set<GeographicConstraint> requestLinkedConstraints) {

    Set<MicroMicroRelationalConstraintWithZone> castedConstraintsToSatisfy = new HashSet<MicroMicroRelationalConstraintWithZone>();
    for (GeographicConstraint c : constraintsToSatisfy) {
      if (c instanceof MicroMicroRelationalConstraintWithZone) {
        castedConstraintsToSatisfy
            .add((MicroMicroRelationalConstraintWithZone) c);
      }
    }

    Set<MicroMicroRelationalConstraintWithZone> castedTriggeringConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();
    for (GeographicConstraint c : triggeringConstraints) {
      if (c instanceof MicroMicroRelationalConstraintWithZone) {
        castedTriggeringConstraints
            .add((MicroMicroRelationalConstraintWithZone) c);
      }
    }

    Set<MicroMicroRelationalConstraintWithZone> castedRequestLinkedConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();
    for (GeographicConstraint c : requestLinkedConstraints) {
      if (c instanceof MicroMicroRelationalConstraintWithZone) {
        castedRequestLinkedConstraints
            .add((MicroMicroRelationalConstraintWithZone) c);
      }
    }

    ConstrainedZonesDrivenDisplacement action = new ConstrainedZonesDrivenDisplacement(
        (ISmallCompactAgent) source, null, this.getLimitZoneNumber(), 2.0);
    setAction(action);

    // Launch the actual search for a better position
    ConstrainedZonesDrivenDisplacementAlgo displAlgo = new ConstrainedZonesDrivenDisplacementAlgo(
        (ISmallCompactAgent) source, this.getLimitZoneNumber() - 1,
        castedConstraintsToSatisfy, castedTriggeringConstraints,
        castedRequestLinkedConstraints);
    displAlgo.compute();

  }
}
