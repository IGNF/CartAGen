package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;

public class ConstrainedZoneDrivenDisplacementZone1Interaction
    extends ConstrainedZoneDrivenDisplacementInteraction {

  private static ConstrainedZoneDrivenDisplacementZone1Interaction singletonObject;

  public static synchronized ConstrainedZoneDrivenDisplacementZone1Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZoneDrivenDisplacementZone1Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 1;
  }

  public ConstrainedMultipleTargetsInteraction getAggregatedInteraction() {
    return ConstrainedZonesDrivenDisplacementZone1Interaction.getInstance();
  }

}
