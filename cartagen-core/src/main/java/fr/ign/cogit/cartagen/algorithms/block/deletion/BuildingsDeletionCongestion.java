package fr.ign.cogit.cartagen.algorithms.block.deletion;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockTriangulation;
import fr.ign.cogit.cartagen.spatialanalysis.measures.DensityMeasures;

/**
 * algorithmes de suppression des batiments d'un ilot des batiments sont
 * supprimes les uns apres les autres pour que la densite simulee soit la plus
 * proche possible de la densite initiale les batiments sont choisis en fonction
 * de la valeur de leur cout de suppression (les plus petits et congestionnes
 * d'abord)
 * @author JGaffuri 4 sept. 2007
 * 
 */
public class BuildingsDeletionCongestion {

  public static ArrayList<IUrbanElement> compute(IUrbanBlock ai, int nbLimite,
      double distanceMax, double rate) {

    ArrayList<IUrbanElement> removedBuildings = new ArrayList<IUrbanElement>();

    int nbBat = BlockBuildingsMeasures.getBlockNonDeletedBuildingsNumber(ai);

    double initialDensity = DensityMeasures.getBlockBuildingsInitialDensity(ai);

    // surplus de densite simulee
    double dDensite = DensityMeasures.getBlockBuildingsSimulatedDensity(ai)
        / initialDensity;
    if (dDensite <= rate) {
      return removedBuildings;
    }
    while (dDensite > rate && nbBat > nbLimite) {

      // effectue triangulation de l'ilot
      BlockBuildingsMeasures.cleanBlockDecomposition(ai);
      BlockTriangulation.buildTriangulation(ai, distanceMax);

      // tente de supprimer le batiment de l'ilot qui a le plus grand cout.
      IBuilding ab = BlockBuildingsMeasures.getNextBuildingToRemoveInBlock(ai,
          distanceMax);

      // marque le batiment comme supprime provisoirement
      ab.setDeleted(true);

      // calcule la nouvelle densite
      double dDensite_ = DensityMeasures.getBlockBuildingsSimulatedDensity(ai)
          / initialDensity;

      // la densite simulee est inferieure a la densite cible, mais elle
      // devient trop faible si on supprime le batiment:
      if (dDensite_ < rate) {

        // le batiment doit etre garde
        ab.setDeleted(false);
        BlockBuildingsMeasures.cleanBlockDecomposition(ai);
        return removedBuildings;
      }

      // supprime effectivement le batiment
      removedBuildings.add(ab);

      dDensite = dDensite_;
      nbBat--;

    }
    BlockBuildingsMeasures.cleanBlockDecomposition(ai);
    return removedBuildings;

  }

}
