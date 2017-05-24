package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.action.ConstrainedZoneDrivenDisplacement;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.BuildingProximity;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedSingleTargetInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

public abstract class ConstrainedZoneDrivenDisplacementInteraction extends
    ConstrainedSingleTargetInteraction implements AggregableInteraction {

  protected ConstrainedZoneDrivenDisplacementInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.DENSITY_CLASS_NAME);
    this.setWeight(2);
  }

  public abstract int getLimitZoneNumber();

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      IDiogenAgent target, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      // System.out.println(c);
      if (c instanceof BuildingProximity) {
        constraint = c;
        break;
      } else if (c instanceof fr.ign.cogit.cartagen.agents.cartacom.constraint.building2.BuildingProximity) {
        constraint = c;
        break;
      }
    }

    ConstrainedZoneDrivenDisplacement action = new ConstrainedZoneDrivenDisplacement(
        (ISmallCompactAgent) source, (RelationalConstraint) constraint,
        this.getLimitZoneNumber() - 1, 2.0);
    setAction(action);
    action.compute();
  }

  public boolean testAggregableWithInteraction(
      AggregableInteraction aggregableInteraction) {
    return aggregableInteraction instanceof ConstrainedZoneDrivenDisplacementInteraction;

  }

}
