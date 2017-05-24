package fr.ign.cogit.cartagen.collagen.resources.specs.rules;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.CharacterValueType;
import fr.ign.cogit.cartagen.collagen.resources.specs.SimpleOperator;
import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

public class ORPremise {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private GeneralisationConcept concept;
  private Character character;
  private Object value;
  private CharacterValueType valueType;
  private ValueUnit unit;
  private SimpleOperator operator;
  private OperationRule rule;

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public ORPremise(GeneralisationConcept concept, Character character,
      Object value, CharacterValueType valueType, ValueUnit unit,
      SimpleOperator operator) {
    super();
    this.concept = concept;
    this.character = character;
    this.value = value;
    this.valueType = valueType;
    this.unit = unit;
    this.operator = operator;
  }
  
  // Getters and setters //
  public void setConcept(GeneralisationConcept concept) {
    this.concept = concept;
  }

  public GeneralisationConcept getConcept() {
    return concept;
  }
  public Character getCharacter() {
    return character;
  }
  public void setCharacter(Character character) {
    this.character = character;
  }
  public Object getValue() {
    return value;
  }
  public void setValue(Object value) {
    this.value = value;
  }
  public CharacterValueType getValueType() {
    return valueType;
  }
  public void setValueType(CharacterValueType valueType) {
    this.valueType = valueType;
  }
  public ValueUnit getUnit() {
    return unit;
  }
  public void setUnit(ValueUnit unit) {
    this.unit = unit;
  }
  public SimpleOperator getOperator() {
    return operator;
  }
  public void setOperator(SimpleOperator operator) {
    this.operator = operator;
  }
  public void setRule(OperationRule rule) {
    this.rule = rule;
  }
  public OperationRule getRule() {
    return rule;
  }

  // Other public methods //

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

