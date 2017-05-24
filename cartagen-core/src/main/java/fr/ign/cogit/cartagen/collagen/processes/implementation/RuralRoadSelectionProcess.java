package fr.ign.cogit.cartagen.collagen.processes.implementation;

import java.util.Set;

import fr.ign.cogit.cartagen.algorithms.network.roads.RoadNetworkStrokesBasedSelection;
import fr.ign.cogit.cartagen.algorithms.network.roads.RoadNetworkTrafficBasedSelection;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.RoadNetworkSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

/**
 * Implementation of the rural selection process described in (Touya 2010,
 * Transactions in GIS).
 * @author GTouya
 * 
 */
public class RuralRoadSelectionProcess extends GeneralisationProcess {

  private int minWeightTraffic = 1;
  private double attractionRatio = 0.005;
  private double strokeMinLength = 500.0;
  private int minTs = 0;
  private IMultiSurface<IPolygon> urbanAreas;

  public RuralRoadSelectionProcess(Conductor chefO) {
    super(chefO);
    computeUrbanAreasExtent();
  }

  private void computeUrbanAreasExtent() {
    Set<GeographicSpace> urbanSpaces = chefO
        .getGeoSpacesFromConceptName("urban_area");
    urbanAreas = GeometryEngine.getFactory().createMultiPolygon();
    for (GeographicSpace space : urbanSpaces) {
      urbanAreas.add((IPolygon) space.getGeom());
    }
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    if (!(space instanceof RoadNetworkSpace))
      return;
    RoadNetworkSpace roadSpace = (RoadNetworkSpace) space;

    // first get the roads eliminated by traffic estimation
    RoadNetworkTrafficBasedSelection trafficSel = new RoadNetworkTrafficBasedSelection(
        CartAGenDoc.getInstance().getCurrentDataset(), roadSpace.getNetwork());
    Set<INetworkSection> eliminatedByTraffic = trafficSel
        .randomTrafficBasedSelection(minWeightTraffic, attractionRatio);

    // then get the roads eliminated by strokes
    RoadNetworkStrokesBasedSelection strokesSel = new RoadNetworkStrokesBasedSelection(
        CartAGenDoc.getInstance().getCurrentDataset(), roadSpace.getNetwork());
    Set<INetworkSection> eliminatedByStrokes = strokesSel
        .strokesBasedSelection(strokeMinLength, minTs);

    // carry out the selection
    for (IGeneObj road : roadSpace.getInsideFeatures()) {
      if (eliminatedByTraffic.contains(road)) {
        if (eliminatedByStrokes.contains(road)
            && !urbanAreas.intersects(road.getGeom()))
          road.eliminate();
      }
    }

    // FIXME checks connectivity with a connectivity matrix
  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }

  public int getMinWeightTraffic() {
    return minWeightTraffic;
  }

  public void setMinWeightTraffic(int minWeightTraffic) {
    this.minWeightTraffic = minWeightTraffic;
  }

  public double getAttractionRatio() {
    return attractionRatio;
  }

  public void setAttractionRatio(double attractionRatio) {
    this.attractionRatio = attractionRatio;
  }

}
