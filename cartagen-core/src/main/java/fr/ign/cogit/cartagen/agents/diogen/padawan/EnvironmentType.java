package fr.ign.cogit.cartagen.agents.diogen.padawan;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.InteractionMatrix;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;

/**
 * An environment type with its interactions matrix.
 * @author AMaudet
 * 
 */
public class EnvironmentType {

  /**
   * 
   */
  private InteractionMatrix<ConstrainedInteraction> interactionMatrix;

  /**
   * 
   */
  private EnvironmentType parent = null;

  /**
   * 
   */
  private String environmentTypeName = "Unamed Environment Type";

  /**
   * Setter for interactionMatrix
   * 
   * @param interactionMatrix
   */
  public void setInteractionMatrix(
      InteractionMatrix<ConstrainedInteraction> interactionMatrix) {
    this.interactionMatrix = interactionMatrix;
  }

  /**
   * Getter for interactionMatrix
   * 
   * @return
   */
  public InteractionMatrix<ConstrainedInteraction> getInteractionMatrix() {
    return this.interactionMatrix;
  }

  /**
   * Setter for parent.
   * 
   * @param parent
   */
  public void setParent(EnvironmentType parent) {
    this.parent = parent;
  }

  /**
   * Getter for parent.
   * 
   * @return
   */
  public EnvironmentType getParent() {
    return this.parent;
  }

  /**
   * Setter for environmentTypeName
   * 
   * @param environmentTypeName
   */
  public void setEnvironmentTypeName(String environmentTypeName) {
    this.environmentTypeName = environmentTypeName;
  }

  /**
   * Getter for environmentTypeName
   * 
   * @return
   */
  public String getEnvironmentTypeName() {
    return this.environmentTypeName;
  }

  /**
   * Constructor
   */
  public EnvironmentType() {
    super();
    interactionMatrix = new InteractionMatrix<ConstrainedInteraction>();
  }

  /**
   * 
   * @param type
   * @return
   */
  public boolean isA(EnvironmentType type) {
    if (type == null)
      return false;
    return this.equals(type) || this.isA(type.getParent())
        || type.isA(this.getParent());
  }

  /**
   * 
   * @param interactionMatrix
   * @param parent
   * @param name
   */
  public EnvironmentType(
      InteractionMatrix<ConstrainedInteraction> interactionMatrix,
      EnvironmentType parent, String name) {
    this.parent = parent;
    this.interactionMatrix = interactionMatrix;
    this.environmentTypeName = name;
  }

  @Override
  public String toString() {
    // System.out.println(this.getEnvironmentTypeName());
    return this.getEnvironmentTypeName();
  }

}
