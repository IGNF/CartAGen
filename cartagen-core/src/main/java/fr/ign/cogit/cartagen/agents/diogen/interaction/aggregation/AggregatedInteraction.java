package fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;

public interface AggregatedInteraction extends Interaction {

  void aggregateInteraction(AggregableInteraction aggregableInteraction);

}
