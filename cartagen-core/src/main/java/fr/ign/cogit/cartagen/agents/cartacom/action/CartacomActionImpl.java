/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.action;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionImpl;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * An action having an execute() method that returns a result indicating if the
 * acting agent has been modified, eliminated or is unchanged.
 * @author CDuchene
 * 
 */
public abstract class CartacomActionImpl extends ActionImpl
    implements CartacomAction {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an action for a cartacom agent given a proposing constraint and
   * the weight with which the action is proposed.
   * @param agent
   * @param constraint
   * @param weight
   */
  public CartacomActionImpl(ICartacomAgent agent,
      GeographicConstraint constraint, double weight) {
    super(agent, constraint, weight);
  }

  /**
   * 
   * @param agent
   * @param constraint
   * @param weight
   * @param actItselfAction
   */
  public CartacomActionImpl(ICartacomAgent agent,
      GeographicConstraint constraint, double weight, boolean actItselfAction) {
    super(agent, constraint, weight);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public ICartacomAgent getAgent() {
    return (ICartacomAgent) super.getAgent();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GeographicConstraint getConstraint() {
    // TODO Auto-generated method stub
    return (GeographicConstraint) super.getConstraint();
  }

  private boolean actItselfAction = true;

  @Override
  public boolean isActItselfAction() {
    return actItselfAction;
  }

  @Override
  public void setActItselfAction(boolean actItselfAction) {
    this.actItselfAction = actItselfAction;
  }

  /**
   * {@inheritDoc}
   */
  public boolean modifieEnvironmentRepresentation() {
    return true;
  }

  public Object clone() {
    CartacomAction o = null;
    try {
      o = (CartacomAction) super.clone();
    } catch (CloneNotSupportedException cnse) {
      cnse.printStackTrace(System.err);
    }
    System.out.println(o);
    System.out.println(o.getAgent());
    System.out.println(this.getAgent());
    return o;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (actItselfAction ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CartacomActionImpl other = (CartacomActionImpl) obj;
    if (actItselfAction != other.actItselfAction)
      return false;
    if (!this.getConstraint().equals(other.getConstraint()))
      return false;
    if (!this.getAgent().equals(other.getAgent()))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "CartacomActionImpl [actItselfAction=" + actItselfAction
        + ", getWeight()=" + getWeight() + ", getClass()=" + getClass() + "]";
  }

}
