/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.relation.Relation;

/**
 * Ah hoc argument for a performative AskToDo, RefuseTodo or FinishedToDo,
 * composed of:
 * <ul>
 * <li>the {@link Action} the sender asks/refuses/finished to perform (the
 * <code>Action</code> has been constructed with the relevant parameters)</li>
 * <li>the {@link Relation} this action is intended to improve the satisfaction
 * of.</li>
 * </ul>
 * 
 * @author CDuchene
 * 
 */
public class AskToDoAdHocArgument extends AdHocArgument {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The action the sender of the message asks/refuses/accepts to do
   */
  private Action action;
  /**
   * The Relation this action is expected to improve
   */
  private Relation relationToImprove;

  // Very private fields (no public getter) //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  /**
   * Constructs an AskToDoAdHocArgument from a (parameterised) {@link Action}
   * and a {@link Relation} to improve.
   * @param action the (parameterised) <code>Action</code> the sender
   *          asks/refuses/accepts to do
   * @param relationToImprove
   */
  public AskToDoAdHocArgument(Action action, Relation relationToImprove) {
    this.action = action;
    this.relationToImprove = relationToImprove;
  }

  // Public getters and setters //

  /**
   * Returns the action this argument is about.
   * @return the action the sender asks/refuses/finished to perform (depending
   *         on the performative of the message)
   */
  public Action getAction() {
    return this.action;
  }

  /**
   * Returns the relation this argument is about.
   * @return the relation that
   */
  public Relation getRelationToImprove() {
    return this.relationToImprove;
  }
  // Other public methods //

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
