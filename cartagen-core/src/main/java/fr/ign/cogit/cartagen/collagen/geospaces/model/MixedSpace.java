package fr.ign.cogit.cartagen.collagen.geospaces.model;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public abstract class MixedSpace extends GeographicSpace implements
    ArealProperties, ThematicProperties {

  public MixedSpace(IPolygon polygon) {
    super(polygon);
  }

  @Override
  protected void buildAdjacency() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void buildEdges() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void findConnates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void buildIntersection() {
    // TODO Auto-generated method stub

  }

}
