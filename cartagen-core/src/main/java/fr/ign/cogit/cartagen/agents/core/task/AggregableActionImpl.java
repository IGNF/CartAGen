/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomActionImpl;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * @author CDuchene
 * 
 */
public abstract class AggregableActionImpl extends CartacomActionImpl
    implements AggregableAction {

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
  private TryActionTask encapsulatingTask;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an aggregable action from an agent, a constraint and a weight.
   * @param agent the agent supposed to do this action
   * @param constraint the constraint this action is supposed to satisfy
   * @param weight the weight with which the action is proposed
   */
  public AggregableActionImpl(ICartacomAgent agent,
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
   * {@inheritDoc} Also updates the reverse reference from encapsulatingTask to
   * {@code this}. To break the reference use
   * {@code this.setEncapsulatingTask(null)}
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

  /**
   * {@inheritDoc}
   * <p>
   * An action is aggregable with this action if it is of the same class
   * whatever the parameters.
   */
  @Override
  public boolean testAggregableWithAction(CartacomAction actionToTest) {
    if (actionToTest == null) {
      return false;
    }
    if (this.getClass().equals(actionToTest.getClass())) {
      return true;
    }
    return false;
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
