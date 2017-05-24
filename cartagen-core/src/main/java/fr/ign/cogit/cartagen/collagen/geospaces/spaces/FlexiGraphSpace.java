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
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * A FlexiGraph space is a group of overlapping network instances and their
 * network neighbours necessary to propagate the deformation due to
 * generalisation. It is derived from the Flexibility graphs of C. Lemarié
 * developed at IGN to trigger the Beams in the Carto2001 project.
 * @author GTouya
 * 
 */
public class FlexiGraphSpace extends ArealSpace {

  private GeoSpaceConcept geoConcept;
  private Set<INetworkSection> sections = new HashSet<>();

  public FlexiGraphSpace(IPolygon polygon,
      Collection<INetworkSection> sections) {
    super(polygon);
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("graphe_de_flexibilité");
    this.sections.addAll(sections);
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
    Set<IGeneObj> returnSet = new HashSet<>();
    returnSet.addAll(sections);
    return returnSet;
  }

}
