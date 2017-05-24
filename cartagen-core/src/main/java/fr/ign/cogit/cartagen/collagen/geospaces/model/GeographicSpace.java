package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenAgent;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

public abstract class GeographicSpace extends DefaultFeature implements
    CollaGenAgent {

  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public GeographicSpace() {
    super();
    id = counter.getAndIncrement();
  }

  public GeographicSpace(IGeometry geom) {
    super(geom);
    id = counter.getAndIncrement();
    this.geom = geom;
  }

  @Override
  public void computeSatisfaction() {
    // TODO Auto-generated method stub

  }

  @Override
  public int lifeCycle() throws InterruptedException {
    // TODO Auto-generated method stub
    return 0;
  }

  public abstract GeoSpaceConcept getConcept();

  public int getId() {
    return id;
  }

  public void setGeom(IGeometry geom) {
    this.geom = geom;
  }

  public IGeometry getGeom() {
    return geom;
  }

  public abstract Set<SpecElementMonitor> getMonitors();

  /**
   * Get a complete sample of the {@link SpecElementMonitor} instances contained
   * in {@code this}, i.e. all the monitors on the generalised features. Useful
   * for online evaluation, during the generalisation of {@code this} by a
   * {@link GeneralisationProcess}.
   * 
   * @return
   */
  public abstract Set<SpecElementMonitor> getSimpleSample(
      GeneralisationProcess process);

  /**
   * Get a sample of the {@link SpecElementMonitor} instances contained in
   * {@code this}, with monitors from all the partitions given as parameters.
   * Useful for online evaluation, during the generalisation of {@code this} by
   * a {@link GeneralisationProcess}.
   * 
   * @return
   */
  public abstract Set<SpecElementMonitor> getPartitionSample(int idLastStop,
      GeneralisationProcess process);

  /**
   * Get a random sample of the {@link SpecElementMonitor} instances contained
   * in {@code this}. Useful for online evaluation, during the generalisation of
   * {@code this} by a {@link GeneralisationProcess}.
   * 
   * @return
   */
  public abstract Set<SpecElementMonitor> getRandomSample(
      GeneralisationProcess process, double ratio);

  public abstract boolean contains(IGeneObj obj);

  protected abstract void buildIntersection();

  protected abstract void findConnates();

  protected abstract void buildEdges();

  protected abstract void buildAdjacency();

  public abstract Set<IGeneObj> getInsideFeatures();

  @Override
  public String getName() {
    return this.getConcept().getName() + getId();
  }

}
