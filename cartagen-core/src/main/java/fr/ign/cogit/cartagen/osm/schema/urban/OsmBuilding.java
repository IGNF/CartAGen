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

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.urban.BuildingCategory;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class OsmBuilding extends OsmGeneObjSurf implements IBuilding {

  private String nature;
  private BuildingCategory category = BuildingCategory.UNKNOWN;

  public OsmBuilding(String contributor, IGeometry geom, int id, int changeSet,
      int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmBuilding(IPolygon geom) {
    super(geom);
  }

  public OsmBuilding() {
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
  public String getNature() {
    if (nature == null)
      computeNatureFromTags();
    return nature;
  }

  @Override
  public void setNature(String nature) {
    this.nature = nature;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

  private void computeNatureFromTags() {
    if (getTags().containsKey("aeroway"))
      nature = getTags().get("aeroway");
    else if (!getTags().get("building").equals("yes"))
      nature = getTags().get("building");
    else
      nature = "unknown";
  }

  @Override
  public BuildingCategory getBuildingCategory() {
    return category;
  }

  @Override
  public void setBuildingCategory(BuildingCategory category) {
    this.category = category;
  }
}
