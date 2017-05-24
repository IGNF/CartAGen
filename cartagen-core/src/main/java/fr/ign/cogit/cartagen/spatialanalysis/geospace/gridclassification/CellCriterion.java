package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

/**
 * Contient les critères appliqués à une cellule. Chaque élément de la classe
 * est donc lié à une cellule et peut déterminer la classe de la cellule selon
 * son propre critère.
 * 
 */
public abstract class CellCriterion {
  private GridCell cellule;
  private int classif;// classification du critère
  private Number seuilBas, seuilHaut;// les seuils de la classif
  private Number valeur;// la valeur du critere
  private double poids;// poids du critère (compris entre 0 et 1)

  public GridCell getCellule() {
    return this.cellule;
  }

  public void setCellule(GridCell cellule) {
    this.cellule = cellule;
  }

  public int getClassif() {
    return this.classif;
  }

  public void setClassif(int classif) {
    this.classif = classif;
  }

  public Number getSeuilBas() {
    return this.seuilBas;
  }

  public void setSeuilBas(Number seuilBas) {
    this.seuilBas = seuilBas;
  }

  public Number getSeuilHaut() {
    return this.seuilHaut;
  }

  public void setSeuilHaut(Number seuilHaut) {
    this.seuilHaut = seuilHaut;
  }

  public Number getValeur() {
    return this.valeur;
  }

  public void setValeur(Number valeur) {
    this.valeur = valeur;
  }

  public double getPoids() {
    return this.poids;
  }

  public void setPoids(double poids) {
    this.poids = poids;
  }

  CellCriterion(GridCell cell, double poids, Number seuilBas, Number seuilHaut) {
    this.cellule = cell;
    this.poids = poids;
    this.seuilHaut = seuilHaut;
    this.seuilBas = seuilBas;
  }

  @Override
  public boolean equals(Object obj) {
    if (!this.getClass().equals(obj.getClass())) {
      return false;
    }
    CellCriterion crit = (CellCriterion) obj;
    if (!this.cellule.equals(crit.cellule)) {
      return false;
    }
    if (this.poids != crit.poids) {
      return false;
    }
    if (!this.seuilBas.equals(crit.seuilBas)) {
      return false;
    }
    if (!this.seuilHaut.equals(crit.seuilHaut)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " " + this.poids + " "
        + this.seuilBas + " " + this.seuilBas;
  }

  public abstract void setValue();

  public abstract void setCategory();

  public String getNom() {
    return this.getClass().getSimpleName();
  }
}
