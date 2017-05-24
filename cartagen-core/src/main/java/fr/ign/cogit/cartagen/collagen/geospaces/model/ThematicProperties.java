package fr.ign.cogit.cartagen.collagen.geospaces.model;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface ThematicProperties {

  // *****************************************
  // CARACTERISTIQUES D'UN ESPACE THEMATIQUE
  // *****************************************
  /**
   * 
   * @return
   */
  public IGeometry getExtent();

  /**
   * 
   * @return
   */
  public GeographicConcept getConceptParent();

  /**
   * 
   * @return
   */
  public int getDistributionTheme();

  /**
   * 
   * @return
   */
  public int getTypeGeometrie();

  /**
   * 
   * @return
   */
  public double getDensite();

  /**
   * 
   * @return
   */
  public boolean isNaturel();
}
