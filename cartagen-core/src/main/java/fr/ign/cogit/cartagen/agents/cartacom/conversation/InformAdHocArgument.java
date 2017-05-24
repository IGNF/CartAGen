/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * @author CDuchene
 * 
 */
public class InformAdHocArgument extends AdHocArgument {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The information the sender of the message conaaining this argument wants to
   * communicate to the receiver
   */
  private InformationContent information;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * @param information
   */
  public InformAdHocArgument(InformationContent information) {
    this.setInformation(information);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for information.
   * @return the information
   */
  public InformationContent getInformation() {
    return this.information;
  }

  /**
   * Setter for information.
   * @param information the information to set
   */
  public void setInformation(InformationContent information) {
    this.information = information;
  }

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
