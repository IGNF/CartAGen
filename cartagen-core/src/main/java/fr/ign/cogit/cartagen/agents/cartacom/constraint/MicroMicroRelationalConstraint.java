/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraintImpl;

/**
 * @author JGaffuri
 * @author CDuchene
 * 
 */
public abstract class MicroMicroRelationalConstraint
    extends RelationalConstraintImpl {

  public MicroMicroRelationalConstraint(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance) {
    super(ag, rel, importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.lib.constraints.geographicConstraints.
   * geoRelCont .GeographicRelationnalConstraint#getRelation()
   */
  @Override
  public MicroMicroRelation getRelation() {
    return (MicroMicroRelation) super.getRelation();
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

  /**
   * Updates this constraint knowing if the agent owning it has been modified or
   * only the agent sharing the corresponding relation. The parameters are only
   * used in overriding method on {@link MicroMicroRelationalConstraintWithZone}
   * .
   * @param myShapeModified
   * @param otherModified
   */
  public void update(boolean myShapeModified, boolean otherModified) {
    this.computeCurrentValue();
    this.computeSatisfaction();
    this.computePriority();
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public String toString() {
    String result = "";
    result += "Relational constraint on agent " + this.getAgent().toString();
    result += " Relation = " + this.getRelation().getClass().getSimpleName();
    result += " Other agent = " + this.getAgentSharingConstraint().toString();
    return result;
  }

}
