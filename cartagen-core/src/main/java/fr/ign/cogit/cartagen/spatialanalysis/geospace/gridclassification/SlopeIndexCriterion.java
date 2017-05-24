package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.util.Collection;

import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;

public class SlopeIndexCriterion extends CellCriterion {

  public SlopeIndexCriterion(GridCell cell, double poids, Number seuilBas,
      Number seuilHaut) {
    super(cell, poids, seuilBas, seuilHaut);
  }

  @Override
  public void setCategory() {
    // cas d'un critère double
    double valeurDbl = getValeur().doubleValue();
    if (valeurDbl < getSeuilBas().doubleValue()) {
      setClassif(1);
    } else if (valeurDbl > getSeuilHaut().doubleValue()) {
      setClassif(3);
    } else {
      setClassif(2);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue() {
    Collection<CourbeDeNiveau> setCourbes = (Collection<CourbeDeNiveau>) getCellule()
        .getGrille().getData().get(MountainGrid.FC_CONTOURS).select(
            this.getCellule().getCentre(),
            getCellule().getGrille().getRadiusCellule() * 2);
    if (setCourbes.size() == 0) {
      this.setValeur(0);
    } else {
      int altMin = Integer.MAX_VALUE;
      int altMax = 0;
      int altTotal = 0;
      for (CourbeDeNiveau courbe : setCourbes) {
        int altitude = new Double(courbe.getValeur()).intValue();
        altTotal += altitude;
        if (altitude < altMin)
          altMin = altitude;
        if (altitude > altMax)
          altMax = altitude;
      }
      double moy = altTotal / setCourbes.size();
      this.setValeur(new Double(2.0 * altMax - altMin - moy));
    }
  }

}
