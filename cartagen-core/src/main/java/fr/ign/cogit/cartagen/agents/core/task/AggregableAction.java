/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;

/**
 * An action that may be aggregated with other actions. If a
 * {@link TryActionTask} encapsulates such an action, its execute() method first
 * tries to generate an aggregated task. The aggregable action has a reference
 * to the task it is encapsulated in, and a method that tries to generate an
 * aggregated action and the corresponding aggregated task.
 * @author CDuchene
 * 
 */
public interface AggregableAction extends CartacomAction {

  /**
   * Gets the task that encapsulates this action
   * @return the encapsulating task
   */
  public TryActionTask getEncapsulatingTask();

  /**
   * Sets the task that encapsulates this action.
   * @param encapsulatingTask the encapsulatingTask to set
   */
  public void setEncapsulatingTask(TryActionTask encapsulatingTask);

  /**
   * Tests if the action can be aggregated with another action, by testing the
   * compatibility of the parametric values.
   * @param actionToTest the other action with which the aggregability is tested
   * @return {@code true} if the action has been aggregated, {@code false} if
   *         not.
   */
  public boolean testAggregableWithAction(CartacomAction actionToTest);

  /**
   * Gives the (parameterised) action into which this action should be
   * aggregated. Even if an action is returned it does not mean that this action
   * will actually be aggregated: the task encapsulating this action decides for
   * this.
   * @return the aggregated action.
   */
  public CartacomAction getAggregatedAction();

}
