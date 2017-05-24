package fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;

public interface AggregableInteraction extends Interaction {

  boolean testAggregableWithInteraction(
      AggregableInteraction aggregableInteraction);

  ConstrainedMultipleTargetsInteraction getAggregatedInteraction();

}
