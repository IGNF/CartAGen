/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.ConversationalObject;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationState;

/**
 * A default implementation of the {@link ProcessingTaskWithinConv} interface.
 * The three methods initialiseBasedOnXxx are implemented as stubs (doing
 * nothing). At least one of them should be overrided on concrete task
 * subclasses.
 * 
 * @author CDuchene
 * 
 */
public abstract class ProcessingTaskWithinConvImpl
    extends TaskWithinConversationImpl implements ProcessingTaskWithinConv {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(ProcessingTaskWithinConv.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The argument to send after the task has completed, in case this task
   * actually has a dependent conversation. Should be assigned before the status
   * of this task is turned to {@code TaskStatus.FINISHED}.
   */
  private Object argumentToSend = null;

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
  public Object getArgumentToSend() {
    return this.argumentToSend;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setArgumentToSend(Object argumentToSend) {
    this.argumentToSend = argumentToSend;
  }

  /**
   * {@inheritDoc}
   * <p>
   * On this class, implemented as a stub: empty, just logs an error message.
   * Not abstract on purpose: some task classes do not need to implement it
   * because they will call another initialiseBasedOn... method.
   */
  @Override
  public void initialiseBasedOnDependentTask(ConversationalObject convObj,
      ConversationState convState, Task dependentTask) {
    // Stub - left empty on purpose.
    // Not abstract on purpose (some task classes do not need it because they
    // will call another initialiseBasedOn... method)
    ProcessingTaskWithinConvImpl.logger
        .error("Stub method " + this.getClass().getSimpleName()
            + ".initialiseBasedOnDependentTask triggered."
            + "It must be overriden on the concrete subclasses needing it.");
  }

  /**
   * {@inheritDoc}
   * <p>
   * On this class, implemented as a stub: empty, just logs an error message.
   * Not abstract on purpose: some task classes do not need to implement it
   * because they will call another initialiseBasedOn... method.
   */
  @Override
  public void initialiseBasedOnPreviousTask(ConversationalObject convObj,
      ConversationState convState, ProcessingTaskWithinConv previousTask) {
    // Stub - left empty on purpose.
    ProcessingTaskWithinConvImpl.logger
        .error("Stub method " + this.getClass().getSimpleName()
            + ".initialiseBasedOnPreviousTask triggered."
            + "It must be overriden on the concrete subclasses needing it.");
  }

  /**
   * {@inheritDoc}
   * <p>
   * On this class, implemented as a stub: empty, just logs an error message.
   * Not abstract on purpose: some task classes do not need to implement it
   * because they will call another initialiseBasedOn... method. *
   */
  @Override
  public void initialiseBasedOnReceivedMessage(ConversationalObject convObj,
      ConversationState convState, Object receivedArgument) {
    // Stub - left empty on purpose.
    ProcessingTaskWithinConvImpl.logger
        .error("Stub method " + this.getClass().getSimpleName()
            + ".initialiseBasedOnReceivedMessage triggered."
            + "It must be overriden on the concrete subclasses needing it.");
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
