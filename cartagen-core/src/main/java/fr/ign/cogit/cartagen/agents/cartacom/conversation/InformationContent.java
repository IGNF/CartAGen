/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.conversation;

/**
 * Possible informations contained in an InformAdHocArgument.
 * @author CDuchene
 * 
 */
public enum InformationContent {
  /**
   * The sender has eliminated itsel
   */
  ELIMINATED,
  /**
   * The sender has modified itsel
   */
  MODIFIED,
  /**
   * The sender has removed a failure
   */
  REMOVED_FAILURE
}
