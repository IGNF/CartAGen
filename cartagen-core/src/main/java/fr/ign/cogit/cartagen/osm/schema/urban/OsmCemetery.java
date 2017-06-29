package fr.ign.cogit.cartagen.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmCemetery extends OsmGeneObjSurf implements IBoundedArea {

  public static final String FEAT_TYPE_NAME = "Cemeteries"; //$NON-NLS-1$

  public OsmCemetery(IPolygon geom) {
    super(geom);
  }

}
