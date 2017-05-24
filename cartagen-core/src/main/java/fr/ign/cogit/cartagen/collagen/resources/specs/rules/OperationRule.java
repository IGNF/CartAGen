package fr.ign.cogit.cartagen.collagen.resources.specs.rules;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;

@Entity
public class OperationRule implements SpecificationElement {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Map<GeoSpaceConcept, Double> restriction;
  private int importance;
  private String name;
  private OperationRulesDatabase db;
  private ORConclusion conclusion;
  private Set<ORPremise> premises;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  @Override
  public int getImportance() {
    return importance;
  }

  public OperationRule(String name, int importance, OperationRulesDatabase db,
      ORConclusion conclusion, Set<ORPremise> premises,
      Map<GeoSpaceConcept, Double> restriction) {
    super();
    this.restriction = restriction;
    this.importance = importance;
    this.name = name;
    this.db = db;
    this.conclusion = conclusion;
    this.premises = premises;
    for (ORPremise p : premises)
      p.setRule(this);
    conclusion.setRule(this);
  }

  // Getters and setters //
  public Map<GeoSpaceConcept, Double> getRestriction() {
    return restriction;
  }

  public void setRestriction(Map<GeoSpaceConcept, Double> restriction) {
    this.restriction = restriction;
  }

  public OperationRulesDatabase getDb() {
    return db;
  }

  public void setDb(OperationRulesDatabase db) {
    this.db = db;
  }

  public ORConclusion getConclusion() {
    return conclusion;
  }

  public void setConclusion(ORConclusion conclusion) {
    this.conclusion = conclusion;
  }

  public Set<ORPremise> getPremises() {
    return premises;
  }

  public void setPremises(Set<ORPremise> premises) {
    this.premises = premises;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public String getName() {
    return name;
  }

  // Other public methods //
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof OperationRule))
      return false;
    OperationRule regle = (OperationRule) obj;
    if (!this.db.equals(regle.db))
      return false;
    if (!this.conclusion.equals(regle.conclusion))
      return false;
    if (!this.premises.equals(regle.premises))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  public void clear() {
    premises.clear();
    conclusion = null;
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
