package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

@Entity
public class ControlExpressionType extends ExpressionType {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Object value;
  private ValueUnit unit;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ControlExpressionType(FormalGenConstraint constraint,
      ConstraintOperator keyword, Object value, ValueUnit unit) {
    this.setConstraint(constraint);
    this.setKeyWord(keyword);
    this.setValue(value);
    this.setValueUnit(unit);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public ValueUnit getValueUnit() {
    return unit;
  }

  @Override
  public void setValue(Object valeur) {
    this.value = valeur;
  }

  @Override
  public void setValueUnit(ValueUnit unit) {
    this.unit = unit;
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
