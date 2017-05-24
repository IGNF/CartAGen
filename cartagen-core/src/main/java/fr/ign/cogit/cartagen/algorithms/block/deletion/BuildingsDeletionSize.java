package fr.ign.cogit.cartagen.algorithms.block.deletion;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.DensityMeasures;

/**
 * algorithmes de suppression des batiments d'un ilot des batiments sont
 * supprimes les uns apres les autres pour que la densite simulee soit la plus
 * proche possible de la densite initiale les batiments sont choisis en fonction
 * de leur taille (les plus petits d'abord) 4 sept. 2007
 * 
 * @author julien Gaffuri
 */
public class BuildingsDeletionSize {

  public static void compute(IUrbanBlock ai, int nbLimite) {

    int nbBat = BlockBuildingsMeasures.getBlockNonDeletedBuildingsNumber(ai);

    double initialDensity = DensityMeasures.getBlockBuildingsDensity(ai);

    // surplus de densite simulee
    double dDensite = DensityMeasures.getBlockBuildingsSimulatedDensity(ai)
        - initialDensity;
    if (dDensite <= 0) {
      return;
    }
    while (dDensite > 0 && nbBat > nbLimite) {

      // tente de supprimer le plus petit batiment de l'ilot.
      IBuilding ab = BlockBuildingsMeasures.getBlockSmallestBuilding(ai);

      // marque le batiment comme supprime provisoirement
      ab.setDeleted(true);

      // calcule la nouvelle densite
      double dDensite_ = DensityMeasures.getBlockBuildingsSimulatedDensity(ai)
          - initialDensity;

      // la densite simulee est inferieure a la densite initiale, mais elle
      // devient trop faible si on supprime le batiment:
      if (dDensite_ < 0 && -dDensite_ > dDensite) {

        // le batiment doit etre garde
        ab.setDeleted(false);
        return;
      }

      // supprime effectivement le batiment
      ab.eliminate();

      dDensite = dDensite_;
      nbBat--;
    }

  }

}
