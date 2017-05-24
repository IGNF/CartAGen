/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field.constraint;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public abstract class ObjectFieldRelationnalConstraint
    extends RelationalConstraintImpl {

  public ObjectFieldRelationnalConstraint(GeographicAgentGeneralisation ag,
      RelationImpl rel, double importance) {
    super(ag, rel, importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.lib.constraints.geographicConstraints.
   * geoRelCont .GeographicRelationnalConstraint#getRelation()
   */
  @Override
  public ObjectFieldRelation getRelation() {
    return (ObjectFieldRelation) super.getRelation();
  }

  /**
   * @return the field considered by the constraint
   */
  public FieldAgent getAgentChamp() {
    return this.getRelation().getAgentChamp();
  }

  /**
   * @return the geographic object considered by the constraint
   */
  public GeographicObjectAgent getAgentGeo() {
    return this.getRelation().getAgentGeo();
  }
}
