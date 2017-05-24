package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class PointOfRoadForTriangulation extends GeneObjPointDefault implements
    IGeneObj {

  private INetworkSection road;

  private double position;

  public PointOfRoadForTriangulation(IPoint point, INetworkSection road,
      double position) {
    this.setGeom(point);
    this.road = road;
    this.position = position;
  }

  public INetworkSection getRoad() {
    return road;
  }

  public double getPosition() {
    return position;
  }

}
