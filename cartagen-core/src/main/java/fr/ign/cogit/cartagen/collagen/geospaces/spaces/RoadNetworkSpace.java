package fr.ign.cogit.cartagen.collagen.geospaces.spaces;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.ThematicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class RoadNetworkSpace extends ThematicSpace {

  private GeoSpaceConcept geoConcept;

  public RoadNetworkSpace() {
    super();
    this.setGeom(getEnvelope());
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("réseau_routier");
  }

  @Override
  public double getAire() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRatioNoirBlanc() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isHierarchique() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public double getRatioBati() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public GeographicConcept getThemeDominant() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GeoSpaceConcept getConcept() {
    return geoConcept;
  }

  @Override
  public Set<SpecElementMonitor> getMonitors() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SpecElementMonitor> getSimpleSample(
      GeneralisationProcess process) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SpecElementMonitor> getPartitionSample(int idLastStop,
      GeneralisationProcess process) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SpecElementMonitor> getRandomSample(GeneralisationProcess process,
      double ratio) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IGeneObj> getInsideFeatures() {
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    Set<IGeneObj> returnSet = new HashSet<>();
    returnSet.addAll(dataSet.getRoads());
    return returnSet;
  }

  public Set<IRoundAbout> getInsideRoundabouts() {
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    Set<IRoundAbout> returnSet = new HashSet<>();
    returnSet.addAll(dataSet.getRoundabouts());

    return returnSet;
  }

  public Set<IBranchingCrossroad> getInsideBranchings() {
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    Set<IBranchingCrossroad> returnSet = new HashSet<>();
    returnSet.addAll(dataSet.getBranchings());

    return returnSet;
  }

  public IPolygon getEnvelope() {
    IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<>(
        getInsideFeatures());
    return fc.getEnvelope().getGeom();
  }

  public INetwork getNetwork() {
    return CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork();
  }
}
