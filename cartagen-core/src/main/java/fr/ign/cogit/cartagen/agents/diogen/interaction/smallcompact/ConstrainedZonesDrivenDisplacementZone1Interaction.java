package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;

public class ConstrainedZonesDrivenDisplacementZone1Interaction
    extends ConstrainedZonesDrivenDisplacementInteraction {

  private static ConstrainedZonesDrivenDisplacementZone1Interaction singletonObject;

  public static synchronized ConstrainedZonesDrivenDisplacementZone1Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZonesDrivenDisplacementZone1Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 1;
  }

  @Override
  public void aggregateInteraction(
      AggregableInteraction aggregableInteraction) {
    // TODO Auto-generated method stub

  }

}
