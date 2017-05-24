package fr.ign.cogit.cartagen.agents.diogen.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgument;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

public class InteractionArgument extends AdHocArgument {

  private Interaction interaction;

  private RelationalConstraint relationalConstraint;

  private Environment env;

  public InteractionArgument(Interaction interaction,
      RelationalConstraint relationalConstraint, Environment env) {
    this.interaction = interaction;
    this.relationalConstraint = relationalConstraint;
    this.env = env;
  }

  public Interaction getInteraction() {
    return interaction;
  }

  public RelationalConstraint getRelationalConstraint() {
    return relationalConstraint;
  }

  public Environment getEnv() {
    return env;
  }

}
