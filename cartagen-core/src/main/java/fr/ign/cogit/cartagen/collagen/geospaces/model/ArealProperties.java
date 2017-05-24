package fr.ign.cogit.cartagen.collagen.geospaces.model;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;

public interface ArealProperties {

  // *****************************************
  // CARACTERISTIQUES D'UN ESPACE METRIQUE
  // *****************************************
  /**
   * 
   * @return
   */
  public double getAire();

  /**
   * 
   * @return
   */
  public double getRatioNoirBlanc();

  /**
   * 
   * @return
   */
  public boolean isHierarchique();

  /**
   * 
   * @return
   */
  public double getRatioBati();

  /**
   * 
   * @return
   */
  public GeographicConcept getThemeDominant();
}
