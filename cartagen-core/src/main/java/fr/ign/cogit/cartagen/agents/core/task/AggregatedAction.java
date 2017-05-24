/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;

/**
 * An action that results from the aggreation of several actions. It has a
 * reference to the task it is encapsulated in.
 * @author CDuchene
 * 
 */
public interface AggregatedAction extends CartacomAction {

  /**
   * Gets the task that encapsulated this action
   * @return the encapsulating task
   */
  public TryActionTask getEncapsulatingTask();

  /**
   * Sets the task that encapsulates this action.
   * @param encapsulatingTask the encapsulatingTask to set
   */
  public void setEncapsulatingTask(TryActionTask encapsulatingTask);

}
