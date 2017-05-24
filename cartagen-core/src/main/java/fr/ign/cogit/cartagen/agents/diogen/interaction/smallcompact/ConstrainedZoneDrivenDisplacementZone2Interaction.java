package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;

public class ConstrainedZoneDrivenDisplacementZone2Interaction
    extends ConstrainedZoneDrivenDisplacementInteraction {

  private static ConstrainedZoneDrivenDisplacementZone2Interaction singletonObject;

  public static synchronized ConstrainedZoneDrivenDisplacementZone2Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZoneDrivenDisplacementZone2Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 2;
  }

  public ConstrainedMultipleTargetsInteraction getAggregatedInteraction() {
    return ConstrainedZonesDrivenDisplacementZone2Interaction.getInstance();
  }

}
