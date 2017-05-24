package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

@Entity
public class ReductionExpressionType extends ExpressionType {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private double value;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ReductionExpressionType(FormalGenConstraint constraint,
      ConstraintOperator keyword, double value) {
    this.setConstraint(constraint);
    this.setKeyWord(keyword);
    this.value = value;
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public ValueUnit getValueUnit() {
    return null;
  }

  @Override
  public void setValue(Object valeur) {
    this.value = (Double) valeur;
  }

  @Override
  public void setValueUnit(ValueUnit unit) {
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
