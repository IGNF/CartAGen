package fr.ign.cogit.cartagen.osm.schema.landuse;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmSimpleLandUseArea extends OsmGeneObjSurf
    implements ISimpleLandUseArea {

  private OsmLandUseTypology type;

  public OsmSimpleLandUseArea(IPolygon geom) {
    super(geom);
  }

  @Override
  public int getType() {
    return type.ordinal();
  }

  @Override
  public void setType(int type) {
    this.type = OsmLandUseTypology.values()[type];
  }

}
