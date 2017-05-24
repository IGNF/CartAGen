/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * the strategy interface, the strategy of the generalisation process, dynamics
 * 
 * Strategy is class that determines the agent - behavior matchings and chooses
 * the appropriate activator for the process
 * 
 * @author GAltay
 * 
 */
public interface Strategy {

  /**
   * initiates the strategy : defineAgentBehaviorMatchings();
   * assignBehaviorsToAgents(); chooseActivator();
   * 
   */
  public void init();

  /**
   * assigns the behaviors to the agents according to the determined agent
   * behavior matchings
   */
  public void assignBehaviorsToAgents();

  /**
   * chooses the appropriate activator for this strategy
   */
  public void chooseActivator();

  /**
   * defines the appropriate agent - behavior matchings for this strategy
   */
  public void defineAgentBehaviorMatchings();

  /**
   * 
   * @return the activator for this strategy
   */
  public Activator getActivator();

  /**
   * sets the activator of this strategy
   * 
   * @param activator the activator to set
   */
  public void setActivator(Activator activator);

  /**
   * Realizes the generalisation process with respect to the strategy
   */
  public void process();
}
