package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentState;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface ISmallCompactAgent extends ICartAComAgentGeneralisation {

  /**
   * Getter for centroid.
   * 
   * @return the centroid
   */
  public IDirectPosition getCentroid();

  /**
   * Setter for centroid.
   * 
   * @param centroid the centroid to set
   */
  public void setCentroid(IDirectPosition centroid);

  /**
   * Getter for centroidRelatedLimitZones.
   * 
   * @return the centroidRelatedLimitZones
   */
  public List<IPolygon> getCentroidRelatedLimitZones();

  /**
   * Getter for the ith element of centroidRelatedLimitZones.
   * @param i the number of the centroidRelatedLimitZone we want to retrieve
   * @return the ith element of centroidRelatedLimitZones, or {@code null} if
   *         less than i centroidRelatedLimitZones
   */
  public IPolygon getCentroidRelatedLimitZone(int i);

  /**
   * Setter for centroidRelatedLimitZones.
   * 
   * @param centroidRelatedLimitZones the centroidRelatedLimitZones to set
   */
  public void setCentroidRelatedLimitZones(
      List<IPolygon> centroidRelatedLimitZones);

  /**
   * Getter for containingFace.
   * 
   * @return the containingFace
   */
  public NetworkFaceAgent getContainingFace();

  /**
   * Setter for containingFace. Also updates the reverse reference from
   * containingFace to {@code this}. To break the reference use
   * {@code this.setContainingFace(null)}
   * 
   * @param containingFace the containingFace to set
   */
  public void setContainingFace(NetworkFaceAgent containingFace);

  /**
   * Retrieves all constraints of {@code this} that are relational constraints
   * having a constrained zone.
   * @return a set of all relational constriants having a constrained zone of
   *         {@code this}
   */
  public Set<MicroMicroRelationalConstraintWithZone> getRelationalConstraintsWithZone();

  /**
   * Computes the space allowed for this small compact in order to satisfy a
   * given set of constraints that have constrained zones.
   * 
   * @param constraints A set of the constraints to consider
   * @param limitZoneNumber the number of the limit zone to consider (that
   *          represents the maximum allowed displacement)
   * @param centroidRelated if {@code true}, the space allowed for the centroid
   *          of this small compact is computed, if {@code false} the space
   *          allowed for the entire small compact.
   * @return the space allowed for the small compact (resp. its centroid,
   *         depending on {@code centroidRelated}). If no free space, returns
   *         {@code null}.
   */
  public IGeometry computeFreeSpace(
      Set<MicroMicroRelationalConstraintWithZone> constraints,
      int limitZoneNumber, boolean centroidRelated);

  /**
   * Computes the space allowed for the small compact in order to stay outside a
   * given area (the askedArea in parameter), while staying within a given limit
   * zone and satisfying a given set of constraints that have constrained zones.
   * Used to compute if the agent could find a position that respects its
   * constraint, if another agent would set its geometry to a new value (the
   * asked area in parameter is then the geometry of the proximity constrained
   * zone that would be computed from the new geometry of the other agent)
   * @author Gokhan Altay
   * @param askedArea the area the agent is expected to say outside
   * @param constraints the set of constraints to consider
   * @param limitZoneNumber the number of the limit zone to consider (that
   *          represents the maximum allowed displacement)
   * @param centroidRelated if {@code true}, the space allowed for the centroid
   *          of the small compact is computed, if {@code false} the space
   *          allowed for the entire small compact
   * @return the space allowed for the small compact (resp. its centroid,
   *         depending on {@code centroidRelated}). If no free space, returns
   *         {@code null}
   */
  public IGeometry computeFreeSpaceWithAdditionalGeom(IGeometry askedArea,
      Set<MicroMicroRelationalConstraintWithZone> constraints,
      int limitZoneNumber, boolean centroidRelated);

  @Override
  public SmallCompactAgentState getRootState();

  public void goBackToState(SmallCompactAgentState state);

}
