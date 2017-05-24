/*
 * Créé le 13 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentStateImpl;

/**
 * A point agent state
 * 
 * @author JGaffuri
 * 
 */
public class PointAgentStateImpl extends AgentStateImpl
    implements PointAgentState {

  /**
   * The point coordinates
   */
  private double x;
  /**
   * The point coordinates
   */
  private double y;

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  /**
   * The state of the feature possibilly linked to the point
   */
  private GAELLinkedFeatureState linkedFeatureState;

  public GAELLinkedFeatureState getLinkedFeatureState() {
    return this.linkedFeatureState;
  }

  /**
   * The constructor
   * 
   * @param pa
   */
  public PointAgentStateImpl(IPointAgent pa, AgentState previousState,
      Action action) {
    super(pa, previousState, action);

    this.x = pa.getX();
    this.y = pa.getY();

    // save the state of the possibely linked object
    if (pa.getLinkedFeature() != null) {
      this.linkedFeatureState = new GAELLinkedFeatureState(
          pa.getLinkedFeature());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.AgentState#clean()
   */
  @Override
  public void clean() {
    super.clean();
    if (this.getLinkedFeatureState() != null) {
      this.getLinkedFeatureState().clean();
      this.linkedFeatureState = null;
    }
  }

}
