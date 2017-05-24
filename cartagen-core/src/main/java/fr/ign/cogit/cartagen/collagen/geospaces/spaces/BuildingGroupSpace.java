package fr.ign.cogit.cartagen.collagen.geospaces.spaces;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.ArealSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author GTouya
 * 
 */
public class BuildingGroupSpace extends ArealSpace {

  private GeoSpaceConcept geoConcept;
  private Set<IGeneObj> instances = new HashSet<>();

  public BuildingGroupSpace(IPolygon polygon, Collection<IGeneObj> instances) {
    super(polygon);
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("building_group_space");
    this.instances.addAll(instances);
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
  public boolean isHierarchique() {
    return false;
  }

  @Override
  public GeoSpaceConcept getConcept() {
    return geoConcept;
  }

  @Override
  public Set<SpecElementMonitor> getPartitionSample(int idLastStop,
      GeneralisationProcess process) {
    // initialisation
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();

    // TODO

    return contraintes;
  }

  @Override
  public java.util.Set<SpecElementMonitor> getRandomSample(
      GeneralisationProcess process, double ratio) {
    CollaGenEnvironment env = CollaGenEnvironment.getInstance();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    // TODO
    return contraintes;
  }

  @Override
  public java.util.Set<SpecElementMonitor> getSimpleSample(
      GeneralisationProcess process) {
    CollaGenEnvironment env = CollaGenEnvironment.getInstance();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    // TODO

    return contraintes;
  }

  @Override
  public GeographicConcept getThemeDominant() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IGeneObj> getInsideFeatures() {
    return instances;
  }

}
