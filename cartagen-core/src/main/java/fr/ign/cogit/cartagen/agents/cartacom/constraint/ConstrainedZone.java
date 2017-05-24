/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * A spatial object associated to a relational constraint: the agent concerned
 * by the constraint should stay within (or outside, depending on the
 * constriant) the geometry of this constrained zone.
 * @author CDuchene
 * 
 */
public class ConstrainedZone {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The geometry with which the agent concerned by the associated constraint
   * should respect special relations indicated by the
   * {@code constrainedZoneType}.
   */
  IPolygon zoneGeom;
  /**
   * The geometry with which the centroid of the agent concerned by the
   * associated constraint should respect special relations indicated by the
   * {@code constrainedZoneType}.
   */
  IPolygon centroidRelatedZoneGeom;
  /**
   * The type of relation the agent (resp. its centroid) should respect with the
   * {@code zoneGeom} (resp. the {@code centroidRelatedZoneGeom})
   */
  ConstrainedZoneType constrainedZoneType;

  // Very private fields (no public getter) //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  /**
   * @param zoneGeom The geometry of the constrained zone
   * @param constrainedZoneType The type of the constrained zone:
   *          ConstrainedZoneType.OUTSIDE if the agent is expected to stay
   *          outside the zone, ConstrainedZoneType.INSIDE if it is expected to
   *          stay inside the zone
   */
  public ConstrainedZone(IPolygon zoneGeom,
      ConstrainedZoneType constrainedZoneType) {
    this.zoneGeom = zoneGeom;
    this.constrainedZoneType = constrainedZoneType;
  }

  // Public getters and setters //

  /**
   * Getter for geom.
   * @return the zone geom
   */
  public IPolygon getZoneGeom() {
    return this.zoneGeom;
  }

  /**
   * Setter for zoneGeom.
   * @param geom the zone geom to set
   */
  public void setZoneGeom(IPolygon zoneGeom) {
    this.zoneGeom = zoneGeom;
  }

  /**
   * Getter for centroidRelatedZoneGeom.
   * @return the centroidRelatedZoneGeom
   */
  public IPolygon getCentroidRelatedZoneGeom() {
    return this.centroidRelatedZoneGeom;
  }

  /**
   * Setter for centroidRelatedZoneGeom.
   * @param centroidRelatedZoneGeom the centroidRelatedZoneGeom to set
   */
  public void setCentroidRelatedZoneGeom(IPolygon centroidRelatedGeom) {
    this.centroidRelatedZoneGeom = centroidRelatedGeom;
  }

  /**
   * Getter for constrainedZoneType.
   * @return the constrainedZoneType
   */
  public ConstrainedZoneType getConstrainedZoneType() {
    return this.constrainedZoneType;
  }

  /**
   * Setter for constrainedZoneType.
   * @param constrainedZoneType the constrainedZoneType to set
   */
  public void setConstrainedZoneType(ConstrainedZoneType constrainedZoneType) {
    this.constrainedZoneType = constrainedZoneType;
  }

  // Other public methods //

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
