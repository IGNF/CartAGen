/**
 * 
 */
package fr.ign.cogit.cartagen.algorithms.block;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;

/**
 * essai
 * 
 * @author JGaffuri
 * 
 */
public class BuildingsAggregation {

  public static IBuilding compute(IUrbanBlock ai) {

    ArrayList<IBuilding> innerBuildings = new ArrayList<IBuilding>();

    for (IUrbanElement urbanElement : ai.getUrbanElements()) {
      if (urbanElement instanceof IBuilding) {
        innerBuildings.add((IBuilding) urbanElement);
      }
    }

    // s'il y a moins de deux batiment, sortir
    if (innerBuildings.size() < 2) {
      return null;
    }

    // recupere le meilleur couple de bâtiments de l'ilot a agreger
    // ce sont les deux batiments qui s'intersectent le plus
    IBuilding ab1_ = null, ab2_ = null;
    double aireIntersectionMax = 0.0;
    for (int i = 1; i < innerBuildings.size(); i++) {
      for (int j = 0; j < i; j++) {
        // recupere deux batiment distincts
        IBuilding ab1 = innerBuildings.get(i);
        IBuilding ab2 = innerBuildings.get(j);

        // si l'un des deux a ete supprime, passe
        if (ab1.isDeleted() || ab2.isDeleted()) {
          continue;
        }

        // calcul de l'aire de leur intersection
        double aireIntersection = ab1.getGeom().intersection(ab2.getGeom())
            .area();

        // si l'aire est superieure a l'aire maximum
        if (aireIntersection > aireIntersectionMax) {
          aireIntersectionMax = aireIntersection;
          ab1_ = ab1;
          ab2_ = ab2;
        }
      }
    }

    // si on n'a pas trouve de couple de batiments qui s'intersectent, sortir
    if (ab1_ == null || ab2_ == null) {
      return null;
    }

    // agrege les batiments: le plus petit est supprime, le plus gros est
    // l'agregat des deux

    // intervertion des deux batiments si le deuxieme est le plus grand
    if (ab1_.getGeom().area() < ab2_.getGeom().area()) {
      IBuilding a = ab1_;
      ab1_ = ab2_;
      ab2_ = a;
    }

    // agregation
    ab1_.setGeom(ab1_.getGeom().union(ab2_.getGeom()));
    // ab1_.etats=null;
    // suppression du plus petit
    ab2_.eliminate();

    return ab1_;

  }

}
