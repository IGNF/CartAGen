/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

/**
 * Describes if a network section is a dead end or so, or not.
 * @author CDuchene
 * 
 */
public enum SectionType {

  /**
   * Network section bordering a network face
   */
  NORMAL,
  /**
   * Dead end which has an extremity connected to the border of the face in
   * which it is contained
   */
  DIRECTLY_CONNECTED_DEAD_END,
  /**
   * Dead end which has its two extremities contained in the network face (it is
   * connnected to a bridge section)
   */
  INDIRECTLY_CONNECTED_DEAD_END,
  /**
   * Connected at two extremities, but contained in a network face (path towards
   * a dead end)
   */
  BRIDGE
}
