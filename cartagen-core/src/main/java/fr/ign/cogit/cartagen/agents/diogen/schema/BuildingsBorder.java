package fr.ign.cogit.cartagen.agents.diogen.schema;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class BuildingsBorder extends GeneObjLinDefault {

  private IBuilding b1;
  private IBuilding b2;
  private IBuilding aggregateBuilding;
  private double endPointAbsCurb;
  private double beginPointAbsCurb;

  public BuildingsBorder(ILineString line, IBuilding b1, IBuilding b2) {
    super();
    this.setGeom(line);
    this.b1 = b1;
    this.b2 = b2;
  }

  public void setEndPointCurvAbs(double d) {
    this.endPointAbsCurb = d;
  }

  public double getEndPointCurvAbs() {
    return this.endPointAbsCurb;
  }

  public void setBeginPointCurvAbs(double d) {
    this.beginPointAbsCurb = d;
  }

  public double getBeginPointCurvAbs() {
    return this.beginPointAbsCurb;
  }

  public IBuilding getB1() {
    return b1;
  }

  public IBuilding getB2() {
    return b2;
  }

  public IBuilding getAggregateBuilding() {
    return aggregateBuilding;
  }

  public void setAggregateBuilding(IBuilding aggregateBuilding) {
    this.aggregateBuilding = aggregateBuilding;
  }
}
