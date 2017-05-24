package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;

public class IdentifyBlankSpace {

  private double BUFFER_SIZE;

  private IUrbanBlock block;

  public IdentifyBlankSpace(IUrbanBlock block) {

  }

  public void compute() {

    for (IUrbanElement urbanElement : block.getUrbanElements()) {
      if (!(urbanElement instanceof IBuilding)) {
        continue;
      }
      IBuilding building = (IBuilding) urbanElement;

      building.getFeature().getGeom().buffer(BUFFER_SIZE);
    }

    // use buffer to unify building in big shapes

    // separate blank rooms

    // retains only the bigger

    // concave shapes are maybe better
  }

}
