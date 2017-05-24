package fr.ign.cogit.cartagen.agents.diogen.schema;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class GeneObjPointPatch extends GeneObjPointDefault {

  public GeneObjPointPatch(IPoint geom) {
    super();
    this.setGeom(geom);
  }

}
