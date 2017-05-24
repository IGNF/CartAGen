/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;

/**
 * @author CDuchene
 * 
 */
public class RelationalConstraintDescriptor {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  private Class<? extends ICartAComAgentGeneralisation> firstAgentsClass;
  private Class<? extends ICartAComAgentGeneralisation> secondAgentsClass;
  private Class<? extends MicroMicroRelation> relationClass;
  private double importance;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * @param firstAgentsClass
   * @param secondAgentsClass
   * @param relationClass
   */
  public RelationalConstraintDescriptor(
      Class<? extends ICartAComAgentGeneralisation> agent1Class,
      Class<? extends ICartAComAgentGeneralisation> agent2Class,
      Class<? extends MicroMicroRelation> relationClass, double importance) {
    super();
    this.firstAgentsClass = agent1Class;
    this.secondAgentsClass = agent2Class;
    this.relationClass = relationClass;
    this.importance = importance;
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  public Class<? extends ICartAComAgentGeneralisation> getFirstAgentsClass() {
    return this.firstAgentsClass;
  }

  public Class<? extends ICartAComAgentGeneralisation> getSecondAgentsClass() {
    return this.secondAgentsClass;
  }

  public Class<? extends MicroMicroRelation> getRelationClass() {
    return this.relationClass;
  }

  /**
   * Getter for importance.
   * @return the importance
   */
  public double getImportance() {
    return this.importance;
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////
  public boolean holds(Class<? extends ICartAComAgentGeneralisation> ag1cl,
      Class<? extends ICartAComAgentGeneralisation> ag2cl) {
    boolean result = false;
    if (this.firstAgentsClass.isAssignableFrom(ag1cl)
        && this.secondAgentsClass.isAssignableFrom(ag2cl)) {
      result = true;
    }
    return result;
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
