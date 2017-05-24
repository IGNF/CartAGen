/**
 * @author julien Gaffuri 9 juil. 2009
 */
package fr.ign.cogit.cartagen.algorithms.block;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;

/**
 * Algorithme generique de coloriage d'un objet meso. Tous les composants de
 * l'objet sont supprimes, et l'objet est marque comme colorie
 * 
 * @author julien Gaffuri 23 juil. 2008
 * 
 */
public class BlockGraying {
  /**
	 */
  private IUrbanBlock block;

  public BlockGraying(IUrbanBlock block) {
    this.block = block;
  }

  public void compute() {
    // affecte une couleur au meso
    this.block.setColored(true);

    // marque les composants du meso comme supprimes
    for (IUrbanElement obj : this.block.getUrbanElements()) {
      obj.eliminate();
    }
  }

}
