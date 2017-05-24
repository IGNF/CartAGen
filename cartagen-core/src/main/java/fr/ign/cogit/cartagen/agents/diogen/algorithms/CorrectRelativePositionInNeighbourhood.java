package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import fr.ign.cogit.cartagen.agents.diogen.agent.road.RoadNeighbourhoodAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class CorrectRelativePositionInNeighbourhood {

  public static void compute(RoadNeighbourhoodAgent ngbh) {
    for (BuildingAgent building : ngbh.getBuildingAgents()) {
      CorrectRelativePositionInNeighbourhood.compute(ngbh, building);
    }
  }

  private static void compute(RoadNeighbourhoodAgent neighbourhood,
      BuildingAgent building) {
    // compute translation vector
    ILineString line = SectionSymbol
        .getSymbolExtentAsAnOffsetWithCarriedObjects(
            (ICarrierNetworkSection) neighbourhood.getFeature(),
            neighbourhood.isLeftSide(),
            neighbourhood.getBuildingDistanceFromRoad(building));

    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(line, Color.BLUE, 2);

    // project centroid
    System.out.println(
        "Curb absc " + neighbourhood.getBuildingNormedCurvAbsc(building));
    IDirectPosition centroidProjection = Projections
        .pointEnAbscisseCurviligneOutside(line,
            neighbourhood.getBuildingNormedCurvAbsc(building) * line.length());

    double dx = centroidProjection.getX()
        - building.getFeature().getGeom().centroid().getX();
    double dy = centroidProjection.getY()
        - building.getFeature().getGeom().centroid().getY();

    // displace the geometry of the related feature
    building.getFeature().setGeom(
        CommonAlgorithms.translation(building.getFeature().getGeom(), dx, dy));

    // using symbolised geometry

  }
}
