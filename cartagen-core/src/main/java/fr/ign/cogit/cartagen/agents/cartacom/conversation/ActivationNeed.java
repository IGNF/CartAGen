package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * the activation need state of an agent
 * 
 * @author GAltay
 */
public enum ActivationNeed {

  /**
   * no need
   */
  NONE,

  /**
   * need just for update
   */
  TO_UPDATE,

  /**
   * need to continue to a help procedure (urgent)
   */
  HELP,

  /**
   * need for initial generalisation (urgent)
   */
  INITIAL
}
