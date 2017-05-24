package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;

public class ConstrainedZonesDrivenDisplacementZone2Interaction
    extends ConstrainedZonesDrivenDisplacementInteraction {

  private static ConstrainedZonesDrivenDisplacementZone2Interaction singletonObject;

  public static synchronized ConstrainedZonesDrivenDisplacementZone2Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZonesDrivenDisplacementZone2Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 2;
  }

  @Override
  public void aggregateInteraction(
      AggregableInteraction aggregableInteraction) {
    // TODO Auto-generated method stub

  }

}
