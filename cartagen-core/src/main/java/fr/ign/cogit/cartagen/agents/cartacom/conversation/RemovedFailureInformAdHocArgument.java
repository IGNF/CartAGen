/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionFailure;

/**
 * Argument for a message of which the performative is
 * {@link Performative#INFORM INFORM}, and where the sender informs the receiver
 * that it has removed a failure
 * @author CDuchene
 * 
 */
public class RemovedFailureInformAdHocArgument extends InformAdHocArgument {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The removed Action Failure
   */
  private ActionFailure removedFailure;

  /**
   * Getter for removedFailure.
   * @return the removedFailure
   */
  public ActionFailure getRemovedFailure() {
    return this.removedFailure;
  }

  /**
   * Setter for removedFailure.
   * @param removedFailure the removedFailure to set
   */
  public void setRemovedFailure(ActionFailure removedFailure) {
    this.removedFailure = removedFailure;
  }

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a remove failure argument from a removed action failure
   * @param removedFailure
   */
  public RemovedFailureInformAdHocArgument(ActionFailure removedFailure) {
    super(InformationContent.REMOVED_FAILURE);
    this.setRemovedFailure(removedFailure);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

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
