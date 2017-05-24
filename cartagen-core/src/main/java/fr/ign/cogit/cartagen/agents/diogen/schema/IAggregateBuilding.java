package fr.ign.cogit.cartagen.agents.diogen.schema;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;

public interface IAggregateBuilding extends IBuilding {

  public Set<IBuilding> getAggregatedBuidlings();

  public void addAggregatedBuilding(IBuilding building);

  public void addAggregatedBuildings(Set<IBuilding> buildings);

  public Set<BuildingsBorder> getBuildingBorders();

  public void addBorder(BuildingsBorder border);

  public void addBorders(Set<BuildingsBorder> borders);

}
