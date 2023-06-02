/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default implementation of interface EndOfConvTask, based on
 * TaskWithinConversationImpl. Only particularity: an execute() method that just
 * prints an error message, as this method is not pertinent for
 * "end of conversation" tasks. The parametrised method,
 * execute(ConversationalObject partner, ConversationState convState, Object
 * receivedArgument) should be used instead.
 * @author CDuchene
 */
public abstract class EndOfConvTaskImpl extends TaskWithinConversationImpl
    implements EndOfConvTask {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager.getLogger(EndOfConvTaskImpl.class
      .getName());

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
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  /**
   * Does nothing but printing an error message: for "end of conversation"
   * tasks, this method is not pertinent. The parameterised execute method which
   * is specific to every concrete class) should be used instead.
   */
  @Override
  public void execute() {
    EndOfConvTaskImpl.logger
        .error("Attempt to run the unparameterised execute() method on a task "
            + "extending " + EndOfConvTask.class.getName() + ".");
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
