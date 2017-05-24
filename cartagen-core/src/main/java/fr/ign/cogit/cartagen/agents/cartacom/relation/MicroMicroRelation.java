package fr.ign.cogit.cartagen.agents.cartacom.relation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

/**
 * WARNING Concrete classes must implement the static method : public static
 * void checkRelationRelevance(CartAComAgentGeneralisation ag1,
 * CartAComAgentGeneralisation ag2), which returns TRUE if it is relevant to
 * instantiate the concrete relation between the two considered agents and FALSE
 * if it is not relevant.
 * 
 * 
 * @author CDuchene
 * @author GTouya
 * 
 */
public abstract class MicroMicroRelation extends RelationImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The constraint on ag1 for this relation.
   */
  private MicroMicroRelationalConstraint constraint1;

  /**
   * The constraint on ag2 for this relation.
   */
  private MicroMicroRelationalConstraint constraint2;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * On concrete subclasses, should also instantiate the associated relational
   * constraints.
   * @param ag1 first agent sharing the relation
   * @param ag2 second agent sharing the relation
   */
  public MicroMicroRelation(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  /**
   * Checks the relevance of implementing the constrained relation between the
   * two agents passed as arguments. This method should be declared
   * @param ag1
   * @param ag2
   * @return
   */
  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {
    Exception e = new Exception();
    System.out.println("The method "
        + "public static boolean checkRelationRelevance(CartAComAgentGeneralisation ag1, "
        + "							CartAComAgentGeneralisation ag2) "
        + "should be implemented in each subclass of MicroMicroRelation");
    e.printStackTrace();

    return false;
  }

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  @Override
  public ICartAComAgentGeneralisation getAgentGeo1() {
    return (ICartAComAgentGeneralisation) super.getAgentGeo1();
  }

  @Override
  public ICartAComAgentGeneralisation getAgentGeo2() {
    return (ICartAComAgentGeneralisation) super.getAgentGeo2();
  }

  public MicroMicroRelationalConstraint getConstraint1() {
    return constraint1;
  }

  public void setConstraint1(MicroMicroRelationalConstraint constraint1) {
    this.constraint1 = constraint1;
  }

  public MicroMicroRelationalConstraint getConstraint2() {
    return constraint2;
  }

  public void setConstraint2(MicroMicroRelationalConstraint constraint2) {
    this.constraint2 = constraint2;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////
  @Override
  public String toString() {
    String result = "";
    result += "Relation " + this.getClass().getSimpleName() + " between \n";
    result += this.getAgentGeo1().toString() + " and "
        + this.getAgentGeo2().toString() + "\n";
    return result;
  }

  /**
   * Given one of the relational constraints of the relation, get the other one.
   * @param constraint
   * @return
   */
  public MicroMicroRelationalConstraint getOtherConstraint(
      MicroMicroRelationalConstraint constraint) {
    if (constraint.equals(getConstraint1()))
      return getConstraint2();
    else
      return getConstraint1();
  }

  /**
   * Given one of the agents of the relation, get the other one.
   * @param agent on of both {@link ICartAComAgentGeneralisation} agents of the
   *          relation
   * @return
   */
  public ICartAComAgentGeneralisation getOtherAgent(
      ICartAComAgentGeneralisation agent) {
    if (agent.equals(getAgentGeo1()))
      return getAgentGeo2();
    else
      return getAgentGeo1();
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
