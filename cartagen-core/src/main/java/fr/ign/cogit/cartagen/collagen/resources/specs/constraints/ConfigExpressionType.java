package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

public class ConfigExpressionType extends ExpressionType {


  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public ConfigExpressionType(FormalGenConstraint constraint,ConstraintOperator keyword) {
    this.setConstraint(constraint);
    this.setKeyWord(keyword);
  }
  
  // Getters and setters //

  // Other public methods //
  @Override
  public Object getValue() {return null;}

  @Override
  public ValueUnit getValueUnit() {return null;}

  @Override
  public void setValue(Object valeur) {}

  @Override
  public void setValueUnit(ValueUnit unit) {}
  
  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////

}

