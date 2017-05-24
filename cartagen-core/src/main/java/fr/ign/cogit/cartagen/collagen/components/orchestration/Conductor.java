package fr.ign.cogit.cartagen.collagen.components.orchestration;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenAgent;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

public class Conductor implements CollaGenAgent {

  private IFeatureCollection<GeographicSpace> geoSpaces;
  public static String GEO_SPACE_LAYER = "Geographic Spaces";

  private static Conductor instance = new Conductor();

  private Conductor() {
    super();
    geoSpaces = new FT_FeatureCollection<>();
    FeatureType ft = new FeatureType();
    ft.setNomClasse(GEO_SPACE_LAYER);
    ft.setGeometryType(IPolygon.class);
    geoSpaces.setFeatureType(ft);
  }

  public static Conductor getInstance() {
    return instance;
  }

  @Override
  public void computeSatisfaction() {
    // TODO Auto-generated method stub

  }

  @Override
  public int lifeCycle() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getName() {
    return "Conductor Agent";
  }

  public IFeatureCollection<GeographicSpace> getGeoSpaces() {
    return geoSpaces;
  }

  public void addGeoSpace(GeographicSpace geoSpace) {
    this.geoSpaces.add(geoSpace);
  }

  /**
   * Get the geographic spaces of a specific type identified by its concept
   * @param concept
   * @return
   */
  public Set<GeographicSpace> getGeoSpacesFromConcept(GeoSpaceConcept concept) {
    Set<GeographicSpace> spaces = new HashSet<>();
    for (GeographicSpace space : geoSpaces) {
      if (space.getConcept().equals(concept))
        spaces.add(space);
    }
    return spaces;
  }

  /**
   * Get the geographic spaces of a specific type identified by its concept
   * @param concept
   * @return
   */
  public Set<GeographicSpace> getGeoSpacesFromConceptName(String conceptName) {
    Set<GeographicSpace> spaces = new HashSet<>();
    for (GeographicSpace space : geoSpaces) {
      if (space.getConcept() == null)
        continue;
      if (space.getConcept().getName().equals(conceptName))
        spaces.add(space);
    }
    return spaces;
  }
}
