package fr.ign.cogit.cartagen.agents.diogen.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.AdHocArgument;

public class EndOfInteractionArgument extends AdHocArgument {

  private boolean finished;

  public EndOfInteractionArgument(boolean finished) {
    this.finished = finished;
  }

}
