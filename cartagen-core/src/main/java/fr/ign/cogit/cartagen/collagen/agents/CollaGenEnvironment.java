package fr.ign.cogit.cartagen.collagen.agents;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.components.translator.ConstraintsInstanciation;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.CollaGenRelation;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.collagen.resources.ontology.ProcessingConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRulesDatabase;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class CollaGenEnvironment {

  private SchemaAnnotation annotation;
  private ConstraintDatabase constraintDb;
  private OperationRulesDatabase rulesDb;
  private ConstraintsInstanciation constraintsInstanciation;
  private IFeatureCollection<ConstraintMonitor> constraintsMonitors;
  private IFeatureCollection<CollaGenRelation> relations;
  private Set<GeoSpaceConcept> geoSpaceConcepts;
  private Set<ProcessingConcept> processingConcepts;
  private Set<GeographicConcept> geoConcepts;
  private Set<GeographicRelation> geoRelations;

  private static CollaGenEnvironment instance = new CollaGenEnvironment();

  /**
   * Constructeur de l'objet.
   */
  private CollaGenEnvironment() {
    super();
    constraintsMonitors = new FT_FeatureCollection<ConstraintMonitor>();
    geoSpaceConcepts = new HashSet<>();
    processingConcepts = new HashSet<>();
    geoConcepts = new HashSet<>();
    geoRelations = new HashSet<>();
  }

  /**
   * Méthode permettant de renvoyer une instance de la classe
   * CollaGenEnvironment
   * 
   * @return Retourne l'instance du singleton.
   */
  public final static CollaGenEnvironment getInstance() {
    return CollaGenEnvironment.instance;
  }

  public void setAnnotation(SchemaAnnotation annotation) {
    this.annotation = annotation;
  }

  public SchemaAnnotation getAnnotation() {
    return annotation;
  }

  public void setConstraintDb(ConstraintDatabase constraintDb) {
    this.constraintDb = constraintDb;
  }

  public ConstraintDatabase getConstraintDb() {
    return constraintDb;
  }

  public void setConstraintsInstanciation(
      ConstraintsInstanciation constraintsInstanciation) {
    this.constraintsInstanciation = constraintsInstanciation;
  }

  public ConstraintsInstanciation getConstraintsInstanciation() {
    return constraintsInstanciation;
  }

  public void setConstraintsMonitors(
      IFeatureCollection<ConstraintMonitor> constraintsPopulation) {
    this.constraintsMonitors = constraintsPopulation;
  }

  public IFeatureCollection<ConstraintMonitor> getConstraintsMonitors() {
    return constraintsMonitors;
  }

  public OperationRulesDatabase getRulesDb() {
    return rulesDb;
  }

  public void setRulesDb(OperationRulesDatabase rulesDb) {
    this.rulesDb = rulesDb;
  }

  public void setRelations(IFeatureCollection<CollaGenRelation> relations) {
    this.relations = relations;
  }

  public IFeatureCollection<CollaGenRelation> getRelations() {
    return relations;
  }

  public Set<GeoSpaceConcept> getGeoSpaceConcepts() {
    return geoSpaceConcepts;
  }

  public void setGeoSpaceConcepts(Set<GeoSpaceConcept> geoSpaceConcepts) {
    this.geoSpaceConcepts = geoSpaceConcepts;
  }

  public Set<ProcessingConcept> getProcessingConcepts() {
    return processingConcepts;
  }

  public void setProcessingConcepts(Set<ProcessingConcept> processingConcepts) {
    this.processingConcepts = processingConcepts;
  }

  public Set<GeographicConcept> getGeoConcepts() {
    return geoConcepts;
  }

  public void setGeoConcepts(Set<GeographicConcept> geoConcepts) {
    this.geoConcepts = geoConcepts;
  }

  public Set<GeographicRelation> getGeoRelations() {
    return geoRelations;
  }

  public void setGeoRelations(Set<GeographicRelation> geoRelations) {
    this.geoRelations = geoRelations;
  }

  /**
   * Get all the relations from {@code this} that exist between obj and other
   * features.
   * 
   * @param obj
   * @return
   */
  public Set<CollaGenRelation> getRelationsWithObj(IGeneObj obj) {
    Set<CollaGenRelation> rels = new HashSet<CollaGenRelation>();
    for (CollaGenRelation rel : relations) {
      if (rel.getObj1().equals(obj)) {
        rels.add(rel);
        continue;
      }
      if (rel.getObj2().equals(obj))
        rels.add(rel);
    }
    return rels;
  }

  /**
   * Get the {@link GeoSpaceConcept} object that is named as the name
   * parametered or that contains the name parameter in one of its labels (which
   * is useful to use multi-language ontologies and keep the code in english).
   * 
   * @param name
   * @return
   */
  public GeoSpaceConcept getGeoSpaceConceptFromName(String name) {
    for (GeoSpaceConcept concept : this.getGeoSpaceConcepts()) {
      if (concept.getName().equals(name))
        return concept;
      for (String label : concept.getLabels().values()) {
        if (label.equals(name))
          return concept;
      }
    }
    return null;
  }

  public void setGeneralisationConcepts(Set<GeneralisationConcept> concepts) {
    for (GeneralisationConcept concept : concepts) {
      if (concept instanceof GeographicConcept)
        this.geoConcepts.add((GeographicConcept) concept);
      else if (concept instanceof GeoSpaceConcept)
        this.geoSpaceConcepts.add((GeoSpaceConcept) concept);
      else if (concept instanceof ProcessingConcept)
        this.processingConcepts.add((ProcessingConcept) concept);
      else if (concept instanceof GeographicRelation)
        this.geoRelations.add((GeographicRelation) concept);
    }
  }
}
