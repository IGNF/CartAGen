/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomActionImpl;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * @author CDuchene
 * 
 */
public abstract class AggregatedActionImpl extends CartacomActionImpl
    implements AggregatedAction {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * The task encapsulating this aggregable action (bidirectional reference,
   * automatically managed).
   */
  private TryActionTask encapsulatingTask = null;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an aggregable action from an agent. NB: the encapsulting task
   * has to be set after the construction.
   * @param agent the agent supposed to do this action
   */
  public AggregatedActionImpl(ICartacomAgent agent) {
    super(agent, null, 0.0);
  }

  /**
   * Constructs an aggregable action from an agent. NB: the encapsulting task
   * has to be set after the construction.
   * @param agent the agent supposed to do this action
   * @param constraint
   * @param weight
   */
  public AggregatedActionImpl(ICartacomAgent agent,
      GeographicConstraint constraint, double weight) {
    super(agent, constraint, weight);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc} If no associated TryActionTask, returns null.
   */
  @Override
  public TryActionTask getEncapsulatingTask() {
    return this.encapsulatingTask;
  }

  /**
   * Also updates the reverse reference from encapsulatingTask to {@code this}.
   * To break the reference use {@code this.setEncapsulatingTask(null)}
   * @param encapsulatingTask the encapsulatingTask to set
   */
  @Override
  public void setEncapsulatingTask(TryActionTask encapsulatingTask) {
    TryActionTask oldEncapsulatingTask = this.encapsulatingTask;
    this.encapsulatingTask = encapsulatingTask;
    if (oldEncapsulatingTask != null) {
      // oldEncapsulatingTask.setActionToTry(null);
    }
    if (encapsulatingTask != null) {
      if (encapsulatingTask.getActionToTry() != this) {
        encapsulatingTask.setActionToTry(this);
      }
    }
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
