/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.Behavior;

/**
 * @author GAltay
 * 
 */
public interface ForceBasedBehavior extends Behavior {

  /**
   * 
   * @return the force of the agent (of the behavior)
   */
  public double getForce();

}
