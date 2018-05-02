package fr.ign.cogit.cartagen.osm.schema.hydro;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmWaterArea extends OsmGeneObjSurf implements IWaterArea {

  private WaterAreaNature nature = WaterAreaNature.UNKNOWN;

  public OsmWaterArea(IPolygon polygon) {
    super(polygon);
  }

  public OsmWaterArea() {
    super();
  }

  @Override
  public WaterAreaNature getNature() {
    return nature;
  }
}
