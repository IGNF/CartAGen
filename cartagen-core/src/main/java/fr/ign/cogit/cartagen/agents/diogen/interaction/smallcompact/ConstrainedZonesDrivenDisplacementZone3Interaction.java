package fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact;

import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;

public class ConstrainedZonesDrivenDisplacementZone3Interaction
    extends ConstrainedZonesDrivenDisplacementInteraction {

  private static ConstrainedZonesDrivenDisplacementZone3Interaction singletonObject;

  public static synchronized ConstrainedZonesDrivenDisplacementZone3Interaction getInstance() {
    if (singletonObject == null) {
      singletonObject = new ConstrainedZonesDrivenDisplacementZone3Interaction();
    }
    return singletonObject;
  }

  @Override
  public int getLimitZoneNumber() {
    return 3;
  }

  @Override
  public void aggregateInteraction(
      AggregableInteraction aggregableInteraction) {
    // TODO Auto-generated method stub

  }

}
