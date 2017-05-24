/**
 * 
 */
package fr.ign.cogit.cartagen.algorithms.block.deletion;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;

/**
 * Action qui supprime le batiment le plus contraint (en terme de
 * superposition/proximité) d'un ilot ce batiment n'est supprimé que dans le cas
 * où il est 'très superposé' avec ses voisins (seuil en paramètre)
 * 
 * @author julien Gaffuri 5 févr. 2009
 * 
 */
public class MostConflictedBuildingDeletion {

  public static void compute(IUrbanBlock ai, double seuilTaux) {

    // recupere le batiment le plus contraint
    IUrbanElement ab_ = null;
    double taux_ = 0;
    for (IUrbanElement ab : ai.getUrbanElements()) {
      if (ab.isDeleted()) {
        continue;
      }
      double taux = BlockBuildingsMeasures.getBuildingOverlappingRate(ab, ai);
      if (taux > taux_) {
        taux_ = taux;
        ab_ = ab;
      }
    }

    if (taux_ < seuilTaux || ab_ == null) {
      return;
    }

    // supprime le batiment
    ab_.eliminate();
  }
}
