package fr.ign.cogit.cartagen.agents.diogen.tasks;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationTransition;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.FSMBasedOnGoingConversation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.TransitionExecutionException;
import fr.ign.cogit.cartagen.agents.core.task.ProcessingTaskWithinConvImpl;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.conversation.EndOfInteractionArgument;
import fr.ign.cogit.cartagen.agents.diogen.conversation.InteractionArgument;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.RealizableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

public class TryInteractionTask extends ProcessingTaskWithinConvImpl {

  private RealizableInteraction<?> realisableInteraction;

  private Interaction interaction;

  private ConversationState convState;

  private boolean finished = false;

  @Override
  public void initialiseBasedOnReceivedMessage(ConversationalObject convObj,
      ConversationState convState, Object receivedArgument) {
    this.setTaskOwner(convObj);
    this.convState = convState;
    if (receivedArgument instanceof InteractionArgument) {
      this.interaction = ((InteractionArgument) receivedArgument)
          .getInteraction();
      RelationalConstraint c = ((InteractionArgument) receivedArgument)
          .getRelationalConstraint();
      realisableInteraction = new RealizableConstrainedInteraction(
          (ConstrainedInteraction) interaction,
          (IDiogenAgent) c.getAgentSharingConstraint(),
          (IDiogenAgent) c.getAgent(),
          ((InteractionArgument) receivedArgument).getEnv());
    }
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  @Override
  public void execute() {

    // System.out.println("Argument = " + this.getDependentConversation());
    Performative performative;
    if (finished) {
      performative = Performative.FINISHED_TO_DO;
    } else {
      performative = Performative.REFUSE_TO_DO;
    }

    ConversationTransition transition = null;
    for (ConversationTransition c : this.convState
        .getFollowingTransitionsSet()) {
      if (c.getPerformativeToSend() == performative) {
        transition = c;
        break;
      }
    }

    // Message message = new AdHocArgumentBasedMessage(this
    // .getDependentConversation().getConversationId(), null, null);
    // this.getTaskOwner().sendMessage(
    // this.getDependentConversation().getPartner(), message);

    try {
      this.getTaskOwner().executeTransitionInConversation(
          (FSMBasedOnGoingConversation) this.getDependentConversation(),
          transition, new EndOfInteractionArgument(finished));
    } catch (TransitionExecutionException e) {
      e.printStackTrace();
    }

  }

  public Interaction getInteraction() {
    return interaction;
  }

  public RealizableInteraction<?> getRealisableInteraction() {
    return realisableInteraction;
  }

}
