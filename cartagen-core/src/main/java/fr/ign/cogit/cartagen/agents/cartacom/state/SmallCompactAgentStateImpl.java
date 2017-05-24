/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.state;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;

/**
 * A state of a SmallCompact agent. Described by: a boolean indicating if the
 * agent is deleted or not, the geometry of the agent, its centroid, its
 * satisfaction, and a map of the satisfactins of the constraints. It also holds
 * pointers to its parent state and child states.
 * @author CDuchene
 * 
 */
public class SmallCompactAgentStateImpl extends CartacomAgentStateImpl
    implements SmallCompactAgentState {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The centroid of the small compact at this state
   */
  private IDirectPosition centroid = null;
  /**
   * The limit zones, related to the centroid, at this state
   */
  private List<IPolygon> centroidRelatedLimitZones = new ArrayList<IPolygon>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * @param ag
   * @param previousState
   * @param action
   */
  public SmallCompactAgentStateImpl(ISmallCompactAgent ag,
      SmallCompactAgentState previousState, Action action) {
    // Constructs a state like in CartacomAgentState
    super(ag, previousState, action);
    // Also stores a copy of the centroid, and the centroid related
    // limit zones
    if (ag.getCentroid() != null) {
      this.setCentroid((IDirectPosition) ag.getCentroid().clone());
    }
    this.setCentroidRelatedLimitZones(ag.getCentroidRelatedLimitZones());
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Getter for centroid.
   * @return the centroid
   */
  public IDirectPosition getCentroid() {
    return this.centroid;
  }

  /**
   * Setter for centroid.
   * @param centroid the centroid to set
   */
  public void setCentroid(IDirectPosition centroid) {
    this.centroid = centroid;
  }

  /**
   * Getter for centroidRelatedLimitZones.
   * @return the centroidRelatedLimitZones
   */
  public List<IPolygon> getCentroidRelatedLimitZones() {
    return this.centroidRelatedLimitZones;
  }

  /**
   * Setter for centroidRelatedLimitZones.
   * @param centroidRelatedLimitZones the centroidRelatedLimitZones to set
   */
  public void setCentroidRelatedLimitZones(
      List<IPolygon> centroidRelatedLimitZones) {
    this.centroidRelatedLimitZones = centroidRelatedLimitZones;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ISmallCompactAgent getAgent() {
    return (ISmallCompactAgent) super.getAgent();
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
    this.getCentroidRelatedLimitZones().clear();
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
