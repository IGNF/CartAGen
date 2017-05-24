/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AskToDoAdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionFailureImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;

/**
 * A task that is automatically executed when the agent receives a the answer to
 * a request for action message it has sent.
 * 
 * @author CDuchene
 * 
 */
public class AcknowledgeRequestForActionResultTask extends EndOfConvTaskImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(AcknowledgeRequestForActionResultTask.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  /**
   * {@inheritDoc} From the name of the conversation state that triggered this
   * task, deduces if the requested action has succeeded or failed. If it has
   * succeeded, nothing to do. If it has failed, stores a failure for this
   * action.
   */
  @Override
  public void execute(ConversationalObject partner, ConversationState convState,
      Object receivedArgument) {
    // this.setStatus(TaskStatus.PROCESSING);
    // if (convState.getName() == "FinalActionSucceededInitiator") {
    // if (this.getTaskOwner() instanceof CartacomAgent) {
    // Action failedAction = ((AskToDoAdHocArgument)
    // receivedArgument).getAction();
    // ((CartacomAgent) this.getTaskOwner()).getFailures().add(new
    // ActionFailureImpl(failedAction, FailureValidity.INFO_FROM_OTHER,
    // false));
    // }
    // } else if (convState.getName() != "FinalActionFailedInitiator") {
    // logger.error(AcknowledgeRequestForActionResultTask.class.getSimpleName()
    // + ": I do not recognise the final state of the conversation.");
    // }
    // this.setStatus(TaskStatus.FINISHED);

    // gkhn

    this.setStatus(TaskStatus.PROCESSING);
    if (convState.getName() == "FinalActionSucceededInitiator") {
    } else if (convState.getName() == "FinalActionFailedInitiator") {
      if (this.getTaskOwner() instanceof ICartacomAgent) {
        Action failedAction = ((AskToDoAdHocArgument) receivedArgument)
            .getAction();
        ((ICartacomAgent) this.getTaskOwner()).getFailures()
            .add(new ActionFailureImpl(failedAction,
                FailureValidity.INFO_FROM_OTHER, false));
      }
    } else if (convState.getName() != "FinalActionFailedInitiator") {
      AcknowledgeRequestForActionResultTask.logger
          .error(AcknowledgeRequestForActionResultTask.class.getSimpleName()
              + ": I do not recognise the final state of the conversation.");
    }
    this.setStatus(TaskStatus.FINISHED);
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
