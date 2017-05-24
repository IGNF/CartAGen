package fr.ign.cogit.cartagen.algorithms.block.deletion;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;

/**
 * action qui supprime un a un les batiments en conflit de
 * proximite/superposition d'un ilot jusqu'à ce qu'il n'y ait plus de conflit de
 * proximite/superposition, ou qu'il ne reste pluq qu'un unique batiment.
 * 
 * @author patrick Taillandier 5 févr. 2009
 * 
 */
public class BuildingsDeletionProximity {

  public static ArrayList<IUrbanElement> compute(IUrbanBlock ai) {

    ArrayList<IUrbanElement> removedBuildings = new ArrayList<IUrbanElement>();

    while (true) {

      // le batiment le plus contraint et son taux de suppression
      IUrbanElement ab_ = null;
      double taux_ = 0;

      // le nombre de batiments non supprimes
      int nbBat = 0;

      for (IUrbanElement ab : ai.getUrbanElements()) {
        if (ab.isDeleted()) {
          continue;
        }
        nbBat++;
        double taux = BlockBuildingsMeasures
            .getBuildingOverlappingRateWithOtherBuildings(ab, ai);
        if (taux > taux_) {
          taux_ = taux;
          ab_ = ab;
        }
      }

      // s'il n'y a plus qu'un batiment ou aucun batiment n'est contraint,
      // sortir
      if (nbBat == 1 || ab_ == null) {
        return removedBuildings;
      }

      // suppression du batiment
      ab_.eliminate();
      removedBuildings.add(ab_);
    }

  }

}
