/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.BehavioralAgent;

/**
 * the activator interface
 * 
 * This class activates the agents in each step according to the policy
 * 
 * @author GAltay
 * 
 */
public interface Activator {

  /**
   * to check if loophole
   * 
   * @return if continue process or not
   */
  public boolean testStop();

  /**
   * chooses and activates the agent or agents in each step according to the
   * policy
   */
  public void activationPolicy();

  /**
   * calls activation policy in each step
   */
  public void run();

  /**
   * 
   * @return the agents participating generalisation at the moment (initial /
   *         removed)
   */
  public List<BehavioralAgent> getAgentsToGeneralise();

  /**
   * 
   * @param agentsToGeneralise
   */
  public void setAgentsToGeneralise(List<BehavioralAgent> agentsToGeneralise);

  /**
   * changes the state of activation list if it is ranged or not
   * 
   * @param value
   */
  public void setRanged(boolean value);

  /**
   * 
   * @return the initial set of agents that participate generalisation
   */
  public Set<BehavioralAgent> getInitialAgentsToGeneralise();

}
