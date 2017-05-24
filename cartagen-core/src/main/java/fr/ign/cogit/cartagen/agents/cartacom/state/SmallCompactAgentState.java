/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.state;

import java.util.List;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * A state of a SmallCompact agent. Described by: a boolean indicating if the
 * agent is deleted or not, the geometry of the agent, its centroid, its
 * satisfaction, and a map of the satisfactins of the constraints. It also holds
 * pointers to its parent state and child states.
 * @author CDuchene
 * 
 */
public interface SmallCompactAgentState extends CartacomAgentState {

  /**
   * Getter for centroid.
   * @return the centroid
   */
  public IDirectPosition getCentroid();

  /**
   * Setter for centroid.
   * @param centroid the centroid to set
   */
  public void setCentroid(IDirectPosition centroid);

  /**
   * Getter for centroidRelatedLimitZones.
   * @return the centroidRelatedLimitZones
   */
  public List<IPolygon> getCentroidRelatedLimitZones();

  /**
   * Setter for centroidRelatedLimitZones.
   * @param centroidRelatedLimitZones the centroidRelatedLimitZones to set
   */
  public void setCentroidRelatedLimitZones(
      List<IPolygon> centroidRelatedLimitZones);

  /**
   * {@inheritDoc}
   */
  @Override
  public ISmallCompactAgent getAgent();

}
