package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.buildingroad.Proximity;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.ICarryingRoadSectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.ProximityBtwPossiblyOverlappingPolygon;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class ProximityBuildingCarryingRoad extends Proximity {

  public ProximityBuildingCarryingRoad(ICartAComAgentGeneralisation ag1,
      ICarryingRoadSectionAgent ag2, double importance) {
    super(ag1, ag2, importance);
  }

  @Override
  public void computeCurrentValue() {

    // System.out.println("enter computeCurrentValue");
    // Get geometry of agents' symboles
    IPolygon sym1 = (IPolygon) this.getAgentGeo1().getFeature().getGeom();
    IPolygon sym2l = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), true);

    IPolygon sym2g = (IPolygon) SectionSymbol.getSymbolExtentWithCarriedObjects(
        (ICarrierNetworkSection) this.getAgentGeo2().getFeature(), false);
    // this.currentValue = ProximityBtwPossiblyOverlappingPolygon
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    double proximityCartagen = ProximityBtwPossiblyOverlappingPolygon
        .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2l);

    // double gothicCartagen = GothicGeomAlgorithms
    // .ProximityBtwPossiblyOverlappingPolygons(sym1, sym2);

    // System.out.println("Proximity between " + this.getAgentGeo1() + " and "
    // + this.getAgentGeo2() + " = " + proximityCartagen);

    // System.out.println("gothicCartagen " + gothicCartagen);

    this.currentValue = proximityCartagen;
  }

}
