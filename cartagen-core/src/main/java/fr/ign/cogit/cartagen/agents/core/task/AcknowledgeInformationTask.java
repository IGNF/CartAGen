/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.CartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.InformAdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.InformationContent;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.RemovedFailureInformAdHocArgument;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionFailure;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;

/**
 * @author CDuchene
 * 
 */
public class AcknowledgeInformationTask extends EndOfConvTaskImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = Logger
      .getLogger(AcknowledgeInformationTask.class.getName());

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
   * {@inheritDoc}
   */
  @Override
  public void execute(ConversationalObject partner, ConversationState convState,
      Object receivedArgument) {
    // Retrieves the information content
    InformationContent infoContent = ((InformAdHocArgument) receivedArgument)
        .getInformation();
    // Acts accordingly
    switch (infoContent) {
      // The other agent has just eliminated itself
      case ELIMINATED:
        this.acknowledgeElimination(partner);
        break;
      case MODIFIED:
        this.acknowledgeModification(partner);
        break;
      case REMOVED_FAILURE:
        this.acknowledgeFailureRemoval(receivedArgument);
        break;
      default:
        AcknowledgeInformationTask.logger.error(
            "Unforseen value of infoContent encountered when analysing received message");
        break;
    }
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

  /**
   * Removes all tasks, conversations and failures related to the partner.
   * @param partner
   */
  private void acknowledgeElimination(ConversationalObject partner) {
    // TODO reprendre ici
  }

  /**
   * Removes the failures of the agent linked to a modification of the
   * environment or the other agent and updates constraints shared with the
   * partner.
   * @param partner
   */
  private void acknowledgeModification(ConversationalObject partner) {
    if (!(this.getTaskOwner() instanceof CartacomAgent)) {
      return;
    }
    CartacomAgent agent = (CartacomAgent) this.getTaskOwner();
    // Removes failures
    agent.removeFailuresOfValidity(FailureValidity.ENVIRONMENT_MODIFIED);
    agent.removeFailuresOfValidity(
        FailureValidity.AGENT_OR_ENVIRONMENT_MODIFIED);
    agent.removeFailuresOfValidity(FailureValidity.OTHER_AGENT_MODIFIED);
    // Updates constraints shared with the partner
    // TODO Valable seulement pour les CartacomAgentGeneralisation
    // il faudrait rendre ça plus générique pour ne pas avoir a caster
    if (!(agent instanceof ICartAComAgentGeneralisation)) {
      return;
    }
    ICartAComAgentGeneralisation agentGene = (ICartAComAgentGeneralisation) agent;
    agentGene.updateConstraintsWithAgent((ICartAComAgentGeneralisation) partner,
        false, true);
  }

  /**
   * Removes the failure corresponding to the failure removed by the partner.
   * @param receivedArgument the received argument describing the removed
   *          failure
   */
  private void acknowledgeFailureRemoval(Object receivedArgument) {
    if (!(this.getTaskOwner() instanceof ICartacomAgent)) {
      return;
    }
    ICartacomAgent agent = (ICartacomAgent) this.getTaskOwner();
    // Retrieves the failure removed by the partner
    RemovedFailureInformAdHocArgument arg = (RemovedFailureInformAdHocArgument) receivedArgument;
    ActionFailure removedFailure = arg.getRemovedFailure();
    // Looks for the corresponding failure in the agent failures list and
    // removes it
    Iterator<ActionFailure> iterator = agent.getFailures().iterator();
    while (iterator.hasNext()) {
      ActionFailure failure = iterator.next();
      if (failure.getAction().equals(removedFailure.getAction())) {
        // Removes the failure
        iterator.remove();
      } // if
    } // while
  }
}
