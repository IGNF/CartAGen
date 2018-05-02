package fr.ign.cogit.cartagen.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class OsmCemetery extends OsmGeneObjSurf implements ICemetery {

  public static final String FEAT_TYPE_NAME = "Cemeteries"; //$NON-NLS-1$

  public OsmCemetery(IPolygon geom) {
    super(geom);
  }

  public OsmCemetery() {
    super();
  }

  @Override
  public IUrbanBlock getBlock() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    // TODO Auto-generated method stub

  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

  @Override
  public CemeteryType getType() {
    // TODO handle cemetery type
    return CemeteryType.UNKNOWN;
  }

  @Override
  public String getTypeSymbol() {
    // TODO Auto-generated method stub
    return null;
  }

}
