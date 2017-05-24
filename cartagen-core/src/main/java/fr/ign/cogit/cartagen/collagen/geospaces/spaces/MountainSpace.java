package fr.ign.cogit.cartagen.collagen.geospaces.spaces;

import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.ArealSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class MountainSpace extends ArealSpace {

  private GeoSpaceConcept geoConcept;

  public MountainSpace(IPolygon polygon) {
    super(polygon);
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("mountain_area");
  }

  @Override
  public GeoSpaceConcept getConcept() {
    return geoConcept;
  }

  @Override
  public Set<SpecElementMonitor> getPartitionSample(int idLastStop,
      GeneralisationProcess process) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SpecElementMonitor> getSimpleSample(GeneralisationProcess process) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getAire() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRatioBati() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRatioNoirBlanc() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public GeographicConcept getThemeDominant() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isHierarchique() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<SpecElementMonitor> getRandomSample(GeneralisationProcess process,
      double ratio) {
    // TODO Auto-generated method stub
    return null;
  }

}
