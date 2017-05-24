package fr.ign.cogit.cartagen.agents.diogen.schema;

import java.util.Set;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.BuildingCategory;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class AggregateBuilding extends GeneObjSurfDefault
    implements IAggregateBuilding {

  public AggregateBuilding(IPolygon polygon) {
    super();
    this.setInitialGeom(polygon);
    this.setGeom(polygon);
  }

  @Override
  public String getNature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setNature(String nature) {
    // TODO Auto-generated method stub

  }

  @Override
  public BuildingCategory getBuildingCategory() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBuildingCategory(BuildingCategory category) {
    // TODO Auto-generated method stub

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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IBuilding> getAggregatedBuidlings() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addAggregatedBuilding(IBuilding building) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addAggregatedBuildings(Set<IBuilding> buildings) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<BuildingsBorder> getBuildingBorders() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addBorder(BuildingsBorder border) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorders(Set<BuildingsBorder> borders) {
    // TODO Auto-generated method stub

  }

}
