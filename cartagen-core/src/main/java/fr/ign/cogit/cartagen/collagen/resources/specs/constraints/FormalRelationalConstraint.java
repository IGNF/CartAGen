package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import java.util.Map;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;

@Entity
public class FormalRelationalConstraint extends FormalGenConstraint {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private GeographicConcept concept1, concept2;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public FormalRelationalConstraint(ExpressionType expressionType,
      int importance, SelectionCriterion selCriterion,
      GeographicRelation concept, Character character, String name,
      ConstraintDatabase db, GeographicConcept concept1,
      GeographicConcept concept2, Map<GeoSpaceConcept, Double> restriction) {
    super(expressionType, importance, selCriterion, concept, character, name,
        db, restriction);
    this.concept1 = concept1;
    this.concept2 = concept2;
  }

  // Getters and setters //
  public GeographicConcept getConcept1() {
    return concept1;
  }

  public void setConcept1(GeographicConcept concept1) {
    this.concept1 = concept1;
  }

  public GeographicConcept getConcept2() {
    return concept2;
  }

  public void setConcept2(GeographicConcept concept2) {
    this.concept2 = concept2;
  }

  // Other public methods //
  @Override
  public String traductionOCL() {
    // TODO Auto-generated method stub
    return null;
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
