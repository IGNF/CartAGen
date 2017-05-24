package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;

public class MountainGrid extends RasterGrid {

  static final String FC_CONTOURS = "CONTOURS";

  public MountainGrid(int cellule, int radius, double xB, double xH, double yB,
      double yH, double similarite,
      IFeatureCollection<CourbeDeNiveau> contours) {
    super(cellule, radius, xB, xH, yB, yH, similarite);
    this.setMapCriteres(new HashMap<String, Vector<Number>>());
    this.construireCellules();
    this.getData().put(MountainGrid.FC_CONTOURS, contours);
  }

  /**
   * Choisit les critères qui seront utilisés dans le clustering de cette grille
   * pour chacune des cellules. Remplit également la map des critères qui
   * associe un poids à chaque critère. Cette méthode contient tous les
   * paramètres de seuils que l'on définit pour chaque critère choisi. Une fois
   * les critères définis, ils sont affectés à chaque cellule de la grille.
   * 
   * @param critCourbes true si on utilise le critère de densité des courbes de
   *          niveau.
   * @param poidsN poids du critère de densité des courbes de niveau (somme des
   *          poids = 1)
   * @param seuilBasN seuil bas du critère de densité des courbes de niveau
   *          (conseil : 6 * (radiusCellule))
   * @param seuilHautN seuil haut du critère de densité des courbes de niveau
   *          (conseil : 15 * (radiusCellule))
   * @param critDeniv true si on utilise le critère de dénivelée.
   * @param poidsD poids du critère de dénivelée (somme des poids = 1)
   * @param seuilBasD seuil bas du critère de dénivelée (30 m)
   * @param seuilHautD seuil haut du critère de dénivelée (100 m)
   * @param critSlope true si on utilise le critère d'indice de pente.
   * @param poidsS poids du critère d'indice de pente (somme des poids = 1)
   * @param seuilBasS seuil bas du critère de dénivelée (100.0 m)
   * @param seuilHautS seuil haut du critère de dénivelée (200.0 m)
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws GothicException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("boxing")
  public void setCriteres(boolean critCourbes, double poidsC, double seuilBasC,
      double seuilHautC, boolean critDeniv, double poidsD, int seuilBasD,
      int seuilHautD, boolean critSlope, double poidsS, double seuilBasS,
      double seuilHautS) throws IllegalArgumentException, SecurityException,
      InstantiationException, IllegalAccessException, InvocationTargetException,
      ClassNotFoundException, NoSuchMethodException {
    if (critCourbes) {
      // création du critère de densité
      String nomCrit = "ContourLineDensityCriterion";
      // définition des paramètres
      // création du vecteur de paramètres
      Vector<Number> params1 = new Vector<Number>();
      params1.add(0, poidsC);
      params1.add(1, seuilBasC * this.getRadiusCellule());
      params1.add(2, seuilHautC * this.getRadiusCellule());
      // on ajoute le critère
      this.getMapCriteres().put(nomCrit, params1);
    }
    if (critDeniv) {
      // création du critère de dénivelée
      String nomCrit = "HeightDifferenceCriterion";
      // définition des paramètres
      // création du vecteur de paramètres
      Vector<Number> params2 = new Vector<Number>();
      params2.add(0, poidsD);
      params2.add(1, seuilBasD);
      params2.add(2, seuilHautD);
      // on ajoute le critère
      this.getMapCriteres().put(nomCrit, params2);
    }
    if (critSlope) {
      // création du critère de dénivelée
      String nomCrit = "SlopeIndexCriterion";
      // définition des paramètres
      // création du vecteur de paramètres
      Vector<Number> params3 = new Vector<Number>();
      params3.add(0, poidsS);
      params3.add(1, seuilBasS);
      params3.add(2, seuilHautS);
      // on ajoute le critère
      this.getMapCriteres().put(nomCrit, params3);
    }

    // on construit les critères dans chaque cellule
    for (GridCell cell : this.getListCellules()) {
      cell.calculerCriteres();
      cell.setClasseFinale();
    }
  }

}
