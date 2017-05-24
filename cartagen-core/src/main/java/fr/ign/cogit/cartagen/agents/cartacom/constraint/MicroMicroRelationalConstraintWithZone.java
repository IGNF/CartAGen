/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;

/**
 * A relational constraint from a micro to another micro, where the constraint
 * is translated in the form of a constrained zone.
 * @author CDuchene
 * 
 */
public abstract class MicroMicroRelationalConstraintWithZone
    extends MicroMicroRelationalConstraint {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The constrained zone associated to this constraint
   */
  private ConstrainedZone constrainedZone;

  // Very private fields (no public getter) //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  /**
   * @param ag the agent
   * @param rel the relation
   * @param importance the importance
   * @param geomConstrainedZone the geometry of the associated constrained zone
   */
  public MicroMicroRelationalConstraintWithZone(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance,
      ConstrainedZoneType constrainedZoneType) {
    super(ag, rel, importance);
    this.constrainedZone = new ConstrainedZone(null, constrainedZoneType);
  }

  // Public getters and setters //

  /**
   * Getter for constrainedZone.
   * @return the constrainedZone
   */
  public ConstrainedZone getConstrainedZone() {
    return this.constrainedZone;
  }

  /**
   * Setter for constrainedZone.
   * @param constrainedZone the constrainedZone to set
   */
  public void setConstrainedZone(ConstrainedZone constrainedZone) {
    this.constrainedZone = constrainedZone;
  }

  // Other public methods //
  public abstract void computeConstrainedGeom();

  public abstract void computeCentroidRelatedGeom();

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void update(boolean myShapeModified, boolean otherModified) {
    super.update(myShapeModified, otherModified);
    if (otherModified) {
      this.computeConstrainedGeom();
      this.computeCentroidRelatedGeom();
    } else if (myShapeModified) {
      this.computeCentroidRelatedGeom();
    }
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
