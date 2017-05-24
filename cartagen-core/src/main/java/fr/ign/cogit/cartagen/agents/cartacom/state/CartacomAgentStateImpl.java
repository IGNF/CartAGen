/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.state;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentStateImpl;

/**
 * A state of a CartACom agent. Described by: a boolean indicating if the agent
 * is deleted or not, the geometry of the agent, its satisfaction, and a map of
 * the satisfactins of the constraints. It also holds pointers to its parent
 * state and child states.
 * @author CDuchene
 * 
 */
public class CartacomAgentStateImpl extends GeographicObjectAgentStateImpl
    implements CartacomAgentState {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * Map storing the satisfactions of the constraints at this state
   */
  private Map<GeographicConstraint, Double> constraintsSatisfactions = new HashMap<GeographicConstraint, Double>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a new state from a cartacom agent, the previous state and the
   * action leading to this new state. The constraints satisfactions are stored
   * with the sate during its construction, so they should be up to date when
   * the state is built.
   * @param ag the agent for which the state is constructed
   * @param previousState the previous state
   * @param action the action leading to this state
   */
  public CartacomAgentStateImpl(ICartacomAgent ag,
      CartacomAgentState previousState, Action action) {
    // Constructs a state as in GeographicObjectAgentState
    super(ag, previousState, action);
    // Instantiates the map describing the constraints satisfactions
    for (Constraint constraint : ag.getConstraints()) {
      GeographicConstraint geoConstraint = (GeographicConstraint) constraint;
      this.getConstraintsSatisfactions().put(geoConstraint,
          new Double(geoConstraint.getSatisfaction()));
    }
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for constraintsSatisfactions.
   * @return the constraintsSatisfactions
   */
  public Map<GeographicConstraint, Double> getConstraintsSatisfactions() {
    return this.constraintsSatisfactions;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public ICartAComAgentGeneralisation getAgent() {
    return (ICartAComAgentGeneralisation) super.getAgent();
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public void clean() {
    super.clean();
    this.getConstraintsSatisfactions().clear();
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
