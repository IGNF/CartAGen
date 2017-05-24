package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IGeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.ICarryingRoadStrokeAgent;
import fr.ign.cogit.cartagen.agents.diogen.constraint.points.PointNotUnderRoad;
import fr.ign.cogit.cartagen.agents.diogen.constraint.points.RoadNonOverlappingPoint;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;
import fr.ign.cogit.cartagen.agents.diogen.preprocessing.StrokeSymbol;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierObject;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class NonOverlappingHikingRoad extends MicroPointRelation {

  private static double DISTANCE = 6.0;

  public NonOverlappingHikingRoad(ICartAComAgentGeneralisation microAgent,
      IGeographicPointAgent pointAgent, double importance) {
    super(microAgent, pointAgent, importance);
    new PointNotUnderRoad(pointAgent, this, 3);
    new RoadNonOverlappingPoint(microAgent, this, 3);
  }

  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {
    ICarryingRoadStrokeAgent agentDeformable = (ICarryingRoadStrokeAgent) ag1;
    IGeographicPointAgent agentPoint = (IGeographicPointAgent) ag2;

    INetworkSection carrierSection = agentDeformable.getFeature();
    IDirectPosition point = agentPoint.getPosition();
    // Get the width of the road and the supported objects
    double width = carrierSection.getWidth();
    if (carrierSection instanceof ICarrierObject) {
      for (ICarriedObject carried : ((ICarrierObject) carrierSection)
          .getCarriedObjects()) {
        width += ((INetworkSection) carried).getWidth();
      }
    }
    width *= Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    width += DISTANCE;

    IGeometry buffer = CommonAlgorithms
        .buffer(agentDeformable.getFeature().getGeom(), width);

    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();
    return (jtsAlgorithms.contains(buffer, point.toGM_Point()));

  }

  private boolean currentValue = false;
  private boolean initialValue = false;

  @Override
  public void computeCurrentValue() {
    IPolygon sym2l = (IPolygon) StrokeSymbol.getSymbolExtentWithCarriedObjects(
        (IHikingRouteStroke) this.getAgentGeo1().getFeature(), true);
    IPolygon sym2r = (IPolygon) StrokeSymbol.getSymbolExtentWithCarriedObjects(
        (IHikingRouteStroke) this.getAgentGeo1().getFeature(), false);

    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(sym2l, Color.RED, 2);
    //
    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(sym2r, Color.BLUE, 2);

    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();
    currentValue = !(jtsAlgorithms.contains(sym2l,
        this.getPointAgent().getGeom())
        || jtsAlgorithms.contains(sym2r, this.getPointAgent().getGeom()));

  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    this.setSatisfaction(
        this.currentValue ? CartacomSpecifications.SATISFACTION_5
            : CartacomSpecifications.SATISFACTION_1);
  }

  @Override
  public void computeInitialValue() {
    IPolygon sym2l = (IPolygon) StrokeSymbol.getSymbolExtentWithCarriedObjects(
        (IHikingRouteStroke) this.getAgentGeo1().getFeature(), true);
    IPolygon sym2r = (IPolygon) StrokeSymbol.getSymbolExtentWithCarriedObjects(
        (IHikingRouteStroke) this.getAgentGeo1().getFeature(), false);

    JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();
    initialValue = !(jtsAlgorithms.contains(sym2l,
        this.getPointAgent().getGeom())
        || jtsAlgorithms.contains(sym2r, this.getPointAgent().getGeom()));

  }

  @Override
  public void computeGoalValue() {
    // TODO Auto-generated method stub

  }

}
