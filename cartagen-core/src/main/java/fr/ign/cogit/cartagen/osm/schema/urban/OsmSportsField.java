/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class OsmSportsField extends OsmGeneObjSurf implements ISportsField {

  private IPolygon symbolGeom;
  private SportsFieldType type = SportsFieldType.UNKNOWN;

  public OsmSportsField(IPolygon polygon) {
    super(polygon);
    this.symbolGeom = polygon;
    // FIXME set type from tags
  }

  public OsmSportsField() {
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
    return symbolGeom;
  }

  @Override
  public SportsFieldType getType() {
    return type;
  }

  @Override
  public String getTypeSymbol() {
    return type.name();
  }

  @Override
  public ILineString getMedianGeom() {
    return null;
  }

  @Override
  public void setGeom(IGeometry geom) {
    super.setGeom(geom);
    this.symbolGeom = (IPolygon) geom;
  }

}
