package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;

public class ConstrainedZoneDrivenDisplacementZone3Interaction
    extends ConstrainedZoneDrivenDisplacementInteraction {

  private static ConstrainedZoneDrivenDisplacementZone3Interaction singletonObject;

  public static synchronized ConstrainedZoneDrivenDisplacementZone3Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZoneDrivenDisplacementZone3Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 3;
  }

  public ConstrainedMultipleTargetsInteraction getAggregatedInteraction() {
    return ConstrainedZonesDrivenDisplacementZone3Interaction.getInstance();
  }

}
