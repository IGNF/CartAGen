package fr.ign.cogit.cartagen.osm.schema.aero;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmRunwayArea extends OsmGeneObjSurf implements IRunwayArea {

  private IAirportArea airport;
  private int z;

  public OsmRunwayArea(IPolygon geom) {
    super(geom);
  }

  @Override
  public IAirportArea getAirport() {
    return airport;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  public void setAirport(IAirportArea airport) {
    this.airport = airport;
  }

}
