package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.Set;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class ConnateNeighbourhood extends SpaceNeighbourhood {

  public ConnateNeighbourhood(IGeometry voisinage, Set<GeographicSpace> espaces) {
    super(voisinage, espaces);
  }

  private Set<GeographicConcept> conceptsConnexes;

  public Set<GeographicConcept> getConceptsConnexes() {
    return conceptsConnexes;
  }

  public void setConceptsConnexes(Set<GeographicConcept> conceptsConnexes) {
    this.conceptsConnexes = conceptsConnexes;
  }

  @Override
  protected void construireTampon() {
    // TODO Auto-generated method stub

  }

}
