package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.ProximityBtwPossiblyOverlappingPolygon;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public abstract class ProximityCarryingRoad extends MicroMicroRelation {

  private static double MINIMUM_DISTANCE_THRESHOLD = 50;
  protected double currentValueLeft;
  protected double currentValueRight;
  protected double goalValueLeft;
  protected double goalValueRight;
  protected double initialValueLeft;
  protected double initialValueRight;
  protected double flexibility;

  public ProximityCarryingRoad(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
  }

  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {

    IPolygon sym1 = (IPolygon) ag1.getFeature().getGeom();
    IPolygon sym2 = (IPolygon) SectionSymbol
        .getMaxSymbolExtentWithCarriedObjects(
            (ICarrierNetworkSection) ag2.getFeature());
    if (sym1.distance(sym2) < MINIMUM_DISTANCE_THRESHOLD) {
      return true;
    }
    return false;

  }

  @Override
  public void computeSatisfaction() {

    // the satisfaction depends of the position of carried objects.
    // We need to know the position of the other object (left or right), and the
    // number of carried objects.

    this.computeGoalValue();
    this.computeCurrentValue();

    // get the position of the building

    double result;
    if (this.currentValueLeft - this.goalValueLeft >= -this.flexibility
        && this.currentValueRight - this.goalValueRight >= -this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else if (this.currentValueLeft < -this.flexibility
        || this.currentValueRight < -this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_1;
    } else if (this.currentValueLeft < 0.5 * this.goalValueLeft
        - this.flexibility
        || this.currentValueRight < 0.5 * this.goalValueRight
            - this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_2;
    } else {
      result = CartacomSpecifications.SATISFACTION_3;
    }

    this.setSatisfaction(result);
  }

  @Override
  public void computeCurrentValue() {
    // Get geometry of agents' symboles
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    IPolygon sym2l = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), true);
    IPolygon sym2r = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), false);

    // this.currentValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    this.currentValueLeft = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2l);
    this.currentValueRight = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2r);
  }

  @Override
  public void computeInitialValue() {
    // Get geometry of agents' symboles
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    IPolygon sym2l = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), true);
    IPolygon sym2r = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), false);

    // this.currentValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);
    this.initialValueLeft = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2l);
    this.initialValueRight = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2r);
  }

  @Override
  public void computeGoalValue() {
    this.goalValueLeft = GeneralisationSpecifications.DISTANCE_SEPARATION_WATER_AREA_ROAD;
    this.goalValueRight = GeneralisationSpecifications.DISTANCE_SEPARATION_WATER_AREA_ROAD;
  }

}
