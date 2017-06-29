package fr.ign.cogit.cartagen.osm.schema.nature;

import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjLin;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class OsmCoastline extends OsmGeneObjLin implements ICoastLine {

  public OsmCoastline(ILineString line) {
    super(line);
  }
}
