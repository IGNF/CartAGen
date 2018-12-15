package fr.ign.cogit.cartagen.osm.schema.hydro;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmWaterArea extends OsmGeneObjSurf implements IWaterArea {

  private WaterAreaNature nature = WaterAreaNature.UNKNOWN;
  private String name, typeSymbol;

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

  @Override
  public String getTypeSymbol() {
    return typeSymbol;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTypeSymbol(String typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

}
