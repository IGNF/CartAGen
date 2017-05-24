package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import java.util.Map;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;

@Entity
public class FormalMacroConstraint extends FormalGenConstraint {

  @Override
  public String traductionOCL() {
    // TODO Auto-generated method stub
    return null;
  }

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public FormalMacroConstraint(ExpressionType expressionType, int importance,
      SelectionCriterion selCriterion, GeneralisationConcept concept,
      Character character, String name, ConstraintDatabase db,
      Map<GeoSpaceConcept, Double> restriction) {
    super(expressionType, importance, selCriterion, concept, character, name,
        db, restriction);
    // TODO Auto-generated constructor stub
  }

  // Getters and setters //

  // Other public methods //

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
