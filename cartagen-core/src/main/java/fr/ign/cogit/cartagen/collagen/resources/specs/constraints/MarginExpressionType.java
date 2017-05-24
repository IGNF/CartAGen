package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

@Entity
public class MarginExpressionType extends ExpressionType {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private double margin;
  private ValueUnit unit;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public MarginExpressionType(FormalGenConstraint constraint,
      ConstraintOperator keyword, double value, ValueUnit unit) {
    this.setConstraint(constraint);
    this.setKeyWord(keyword);
    this.setValue(value);
    this.setValueUnit(unit);
  }

  // Getters and setters //
  public double getMargin() {
    return margin;
  }

  public void setMargin(double margin) {
    this.margin = margin;
  }

  public ValueUnit getUnit() {
    return unit;
  }

  public void setUnit(ValueUnit unit) {
    this.unit = unit;
  }

  // Other public methods //
  @Override
  public Object getValue() {
    return margin;
  }

  @Override
  public ValueUnit getValueUnit() {
    return unit;
  }

  @Override
  public void setValue(Object valeur) {
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
