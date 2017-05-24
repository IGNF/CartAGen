package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

/**
 * behavior interface
 * 
 * this class develops the behavior of an agent during the generalisation
 * process
 * 
 * @author GAltay
 * 
 */
public interface Behavior {

  /**
   * sets the belonging agent to this behavior
   * 
   * @param belongingAgent the agent that this behavior belongs to
   */
  public void setBelongingAgent(BehavioralAgent belongingAgent);

  /**
   * 
   * @return the agent that this behavior belongs to
   */
  public BehavioralAgent getBelongingAgent();

  /**
   * acts for a cycle
   */
  public void act();

  /**
   * initiates agents knowledge
   */
  public void init();

}
