package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import java.util.Map;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;

public abstract class FormalGenConstraint implements SpecificationElement,
    Comparable<FormalGenConstraint> {

  protected ExpressionType exprType;
  protected SelectionCriterion selectionCrit;
  protected GeneralisationConcept concept;
  protected Character character;
  protected String name;
  protected ConstraintDatabase db;
  protected int importance;
  protected Map<GeoSpaceConcept, Double> restriction;

  public SelectionCriterion getSelectionCrit() {
    return selectionCrit;
  }

  public void setSelectionCrit(SelectionCriterion critereSel) {
    this.selectionCrit = critereSel;
  }

  public GeneralisationConcept getConcept() {
    return concept;
  }

  public void setConcept(GeneralisationConcept concept) {
    this.concept = concept;
  }

  public Character getCharacter() {
    return character;
  }

  public void setCharacter(Character caractere) {
    this.character = caractere;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConstraintDatabase getDb() {
    return db;
  }

  public void setBd(ConstraintDatabase db) {
    this.db = db;
  }

  public ExpressionType getExprType() {
    return exprType;
  }

  public void setExprType(ExpressionType typeExpr) {
    this.exprType = typeExpr;
  }

  @Override
  public int getImportance() {
    return importance;
  }

  public void setImportance(int importance) {
    this.importance = importance;
  }

  public Map<GeoSpaceConcept, Double> getRestriction() {
    return restriction;
  }

  public void setRestriction(Map<GeoSpaceConcept, Double> restriction) {
    this.restriction = restriction;
  }

  /**
   * Constructeur de base à partir des champs (sauf le gothicObject qui n'est
   * pas indispensable à la création de la contrainte java).
   * 
   * @param typesExpr
   * @param critereSel
   * @param concept
   * @param caractere
   * @param nom
   * @param bd
   */
  public FormalGenConstraint(ExpressionType expressionType, int importance,
      SelectionCriterion selCriterion, GeneralisationConcept concept,
      Character character, String name, ConstraintDatabase db,
      Map<GeoSpaceConcept, Double> restriction) {
    super();
    this.exprType = expressionType;
    this.importance = importance;
    this.selectionCrit = selCriterion;
    this.concept = concept;
    this.character = character;
    this.name = name;
    this.db = db;
    this.restriction = restriction;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FormalGenConstraint))
      return false;
    FormalGenConstraint contr = (FormalGenConstraint) obj;
    if (!concept.equals(contr.concept))
      return false;
    if (!character.equals(contr.character))
      return false;
    if (!exprType.equals(contr.exprType))
      return false;
    if (selectionCrit != null)
      if (!selectionCrit.equals(contr.selectionCrit))
        return false;
    return true;
  }

  @Override
  public int hashCode() {
    return concept.hashCode() + character.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  public abstract String traductionOCL();

  @Override
  public int compareTo(FormalGenConstraint o) {
    // on classe par importance décroissante puis par ordre alphabétique
    return 100 * (this.importance - o.importance) + this.name.compareTo(o.name);
  }

  public void clear() {
    this.exprType = null;
    if (this.restriction != null)
      this.restriction.clear();
    if (this.selectionCrit != null)
      this.selectionCrit.getRequests().clear();
  }
}
