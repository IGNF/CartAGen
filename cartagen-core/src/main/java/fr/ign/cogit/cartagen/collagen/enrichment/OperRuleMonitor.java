package fr.ign.cogit.cartagen.collagen.enrichment;

import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRule;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;

public abstract class OperRuleMonitor extends SpecElementMonitor {

  private OperationRule regle;
  private ConstraintSatisfaction satisfaction1;// un entier entre 0 et 5
  private Object valeurIni, valeurCourante, valeurBut;
  private IGeneObj sujet;

  @Override
  public SpecificationElement getElementSpec() {
    return this.regle;
  }

  @Override
  public ConstraintSatisfaction getSatisfaction() {
    return this.satisfaction1;
  }

  @Override
  public void setSatisfaction(ConstraintSatisfaction satisfaction) {
    this.satisfaction1 = satisfaction;
  }

  public Object getValeurIni() {
    return this.valeurIni;
  }

  public void setValeurIni(Object valeurIni) {
    this.valeurIni = valeurIni;
  }

  public Object getValeurBut() {
    return this.valeurBut;
  }

  public void setValeurBut(Object valeurBut) {
    this.valeurBut = valeurBut;
  }

  public Object getValeurCourante() {
    return this.valeurCourante;
  }

  public void setValeurCourante(Object valeurCourante) {
    this.valeurCourante = valeurCourante;
  }

  public IGeneObj getSujet() {
    return this.sujet;
  }

  public void setSujet(IGeneObj sujet) {
    this.sujet = sujet;
  }

}
