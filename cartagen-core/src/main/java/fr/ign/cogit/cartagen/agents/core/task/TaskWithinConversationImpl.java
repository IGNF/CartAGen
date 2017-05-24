/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.conversation.OnGoingConversation;

/**
 * @author CDuchene
 * 
 */
public abstract class TaskWithinConversationImpl extends TaskImpl
    implements TaskWithinConversation {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The dependent conversation, i.e. the conversation that generated this task
   * (bidirectional reference, automatically managed).
   */
  private OnGoingConversation dependentConversation;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public OnGoingConversation getDependentConversation() {
    return this.dependentConversation;
  }

  /**
   * {@inheritDoc} Also updates the reverse reference from dependentConversation
   * to {@code this}. To break the reference use {@code
   * this.setDependentConversation(null)}.
   */
  @Override
  public void setDependentConversation(
      OnGoingConversation dependentConversation) {
    OnGoingConversation oldDependentConversation = this.dependentConversation;
    this.dependentConversation = dependentConversation;
    if (oldDependentConversation != null) {
      oldDependentConversation.setGeneratedTask(null);
    }
    if (dependentConversation != null) {
      if (dependentConversation.getGeneratedTask() != this) {
        dependentConversation.setGeneratedTask(this);
      }
    }
  }

  // /////////////////////////////////////////////
  // Public methods - Others //
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
