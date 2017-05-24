/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint;

/**
 * Indicates where the agent should be compared to a constrained zone, so that
 * the corresponding constriant is satisfied.
 * @author CDuchene
 * 
 */
public enum ConstrainedZoneType {

  /**
   * The agent should be included in the constrained zone so that this one is
   * satisfied
   */
  INSIDE,

  /**
   * The agent should be outside of the constrained zone so that this one is
   * satisfied
   */
  OUTSIDE

}
