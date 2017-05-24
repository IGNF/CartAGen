/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.state;

import java.util.Map;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;

/**
 * A state of a CartACom agent. Described by: a boolean indicating if the agent
 * is deleted or not, the geometry of the agent, its satisfaction, and a map of
 * the satisfactins of the constraints. It also holds pointers to its parent
 * state and child states.
 * @author CDuchene
 * 
 */
public interface CartacomAgentState extends GeographicObjectAgentState {

  /**
   * Getter for constraintsSatisfactions.
   * @return the constraintsSatisfactions
   */
  public Map<GeographicConstraint, Double> getConstraintsSatisfactions();

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public ICartAComAgentGeneralisation getAgent();

}
