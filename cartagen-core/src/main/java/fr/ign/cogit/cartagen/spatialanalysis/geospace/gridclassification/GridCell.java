package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class GridCell {
  private final static Logger logger = LogManager.getLogger(GridCell.class
      .getName());
  private RasterGrid grille;// la grille dont fait partie la cellule
  private int ligne, colonne;// coordonnées de la cellule dans la grille
  private double xCentre, yCentre;// les coord du centre de la cellule

  // classe de la cellule
  private double classeFinale;
  private HashSet<CellCriterion> criteres;

  private static final String nomPack = "fr.ign.cogit.formation.gridcat.";

  public RasterGrid getGrille() {
    return this.grille;
  }

  public void setGrille(RasterGrid grille) {
    this.grille = grille;
  }

  public int getLigne() {
    return this.ligne;
  }

  public void setLigne(int ligne) {
    this.ligne = ligne;
  }

  public int getColonne() {
    return this.colonne;
  }

  public void setColonne(int colonne) {
    this.colonne = colonne;
  }

  public double getxCentre() {
    return this.xCentre;
  }

  public void setxCentre(double xCentre) {
    this.xCentre = xCentre;
  }

  public double getyCentre() {
    return this.yCentre;
  }

  public void setyCentre(double yCentre) {
    this.yCentre = yCentre;
  }

  public double getClasseFinale() {
    return this.classeFinale;
  }

  public void setClasseFinale(double classeFinale) {
    this.classeFinale = classeFinale;
  }

  public HashSet<CellCriterion> getCriteres() {
    return this.criteres;
  }

  public void setCriteres(HashSet<CellCriterion> criteres) {
    this.criteres = criteres;
  }

  GridCell(RasterGrid grille, int l, int c) {
    this.grille = grille;
    this.ligne = l;
    this.colonne = c;
    this.xCentre = grille.getCoordGrille().getX() + (c - 1)
        * grille.getTailleCellule() + grille.getTailleCellule() / 2;
    this.yCentre = grille.getCoordGrille().getY() - (l)
        * grille.getTailleCellule() + grille.getTailleCellule() / 2;
    this.criteres = new HashSet<CellCriterion>();
  }

  @Override
  public boolean equals(Object obj) {
    if (this.ligne != ((GridCell) obj).ligne) {
      return false;
    }
    if (this.colonne != ((GridCell) obj).colonne) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ligne : " + this.ligne + "   colonne : " + this.colonne;
  }

  void afficheCellule(@SuppressWarnings("unused") int classe) {
    // TODO
  }

  /**
   * Renvoie la position du centre de la cellule.
   * @return
   */
  public IDirectPosition getCentre() {
    return new DirectPosition(this.xCentre, this.yCentre);
  }

  IPolygon construireGeom() {
    IDirectPositionList points = new DirectPositionList();
    points.add(new DirectPosition(this.xCentre - this.grille.getTailleCellule()
        / 2, this.yCentre - this.grille.getTailleCellule() / 2));
    points.add(new DirectPosition(this.xCentre + this.grille.getTailleCellule()
        / 2, this.yCentre - this.grille.getTailleCellule() / 2));
    points.add(new DirectPosition(this.xCentre + this.grille.getTailleCellule()
        / 2, this.yCentre + this.grille.getTailleCellule() / 2));
    points.add(new DirectPosition(this.xCentre - this.grille.getTailleCellule()
        / 2, this.yCentre + this.grille.getTailleCellule() / 2));
    points.add(new DirectPosition(this.xCentre - this.grille.getTailleCellule()
        / 2, this.yCentre - this.grille.getTailleCellule() / 2));
    IPolygon geom = new GM_Polygon(new GM_LineString(points));
    return geom;
  }

  /**
   * Crée les critères contenus dans grille.mapCriteres pour cette cellule et
   * les stocke dans le set criteres. La valeur du critère est également
   * calculée ainsi que sa classification.
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IllegalArgumentException
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws SecurityException
   * 
   */
  @SuppressWarnings({ "unchecked" })
  void calculerCriteres() throws IllegalArgumentException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, ClassNotFoundException, SecurityException,
      NoSuchMethodException {
    // on calcule les critères en fonction de la map de critères de la grille
    Iterator<String> iter = this.grille.getMapCriteres().keySet().iterator();
    while (iter.hasNext()) {
      String nomCrit = iter.next();
      if (GridCell.logger.isTraceEnabled()) {
        GridCell.logger.trace("critere : " + nomCrit);
      }
      Class classe = Class.forName(GridCell.nomPack + nomCrit);
      Constructor<? extends CellCriterion> constr = classe.getConstructor(
          GridCell.class, double.class, Number.class, Number.class);
      Vector<Number> params = this.grille.getMapCriteres().get(nomCrit);
      // on construit le critère
      Double poids = (Double) params.get(0);
      Object seuilBas = params.get(1);
      Object seuilHaut = params.get(2);
      CellCriterion critere = constr.newInstance(this, poids, seuilBas,
          seuilHaut);
      // on calcule sa valeur
      critere.setValue();
      // on calcule sa classification
      critere.setCategory();
      if (GridCell.logger.isTraceEnabled()) {
        GridCell.logger.trace(this.toString() + " : " + critere.getClassif());
      }
      this.criteres.add(critere);
    }
  }// calculerCriteres()

  /**
   * Détermine la classe finale de la cellule en agrégeant tous les critères. La
   * classe d'un critère pouvant prendre 3 valeurs, la classe finale peut
   * prendre nbCriteres*3-1 valeurs comprises entre 1 et 3.
   * 
   */
  void setClasseFinale() {
    // on parcourt le set des critères
    this.classeFinale = 0.0;
    Iterator<CellCriterion> iter = this.criteres.iterator();
    while (iter.hasNext()) {
      CellCriterion critere = iter.next();
      // on ajoute au total la classe de ce critere fois son poids
      this.classeFinale += critere.getPoids() * critere.getClassif();
    }
    // le total est forcément compris entre 1 et 3
  }// setClasseFinale()

  /**
   * Récupère les quatre (au mieux) voisines d'une cellule
   * 
   */
  HashSet<GridCell> recupererCellulesVoisines() {
    HashSet<GridCell> voisins = new HashSet<GridCell>();
    // on cherche le voisin du haut
    if (this.ligne > 1) {
      voisins.add(this.grille.getListCellules().get(
          this.grille.getNbColonne() * (this.ligne - 2) + this.colonne - 1));
    }
    // on cherche le voisin du bas
    if (this.ligne < this.grille.getNbLigne()) {
      voisins.add(this.grille.getListCellules().get(
          this.grille.getNbColonne() * (this.ligne) + this.colonne - 1));
    }
    // on cherche le voisin de gauche
    if (this.colonne > 1) {
      voisins
          .add(this.grille.getListCellules().get(
              this.grille.getNbColonne() * (this.ligne - 1) + this.colonne - 1
                  - 1));
    }
    // on cherche le voisin de droite
    if (this.colonne < this.grille.getNbColonne()) {
      voisins
          .add(this.grille.getListCellules().get(
              this.grille.getNbColonne() * (this.ligne - 1) + this.colonne + 1
                  - 1));
    }
    return voisins;
  }// recupererCellulesVoisines()

  public CellCriterion getCritere(String critere) {
    Iterator<CellCriterion> iter = this.criteres.iterator();
    while (iter.hasNext()) {
      CellCriterion critCourant = iter.next();
      if (critCourant.getNom().equals(critere)) {
        return critCourant;
      }
    }// while, boucle sur criteres de this

    return null;
  }// getCritere(String critere)
}
