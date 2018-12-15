package fr.ign.cogit.cartagen.osm.schema.rail;

import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjLin;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class OsmCable extends OsmGeneObjLin implements ICable {

  public OsmCable(ILineString line) {
    super(line);
  }

  public OsmCable() {
    super();
  }

  @Override
  public String getNature() {
    // TODO Auto-generated method stub
    return null;
  }
}
