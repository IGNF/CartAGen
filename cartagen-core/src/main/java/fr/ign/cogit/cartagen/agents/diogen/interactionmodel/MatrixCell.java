package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

import java.util.HashSet;
import java.util.Set;

/**
 * A cell for an interaction matrix for IODA/PADAWAN model. Contain assignations.
 * http://www.lifl.fr/SMAC/projects/ioda/
 * 
 * @author AMaudet
 *
 */
public class MatrixCell<InteractionClass extends Interaction> {
  
  /**
   * The assignations contained in the cell.
   */
  private Set<Assignation<InteractionClass>> assignations = new HashSet<Assignation<InteractionClass>>();

  /**
   * Setter for assignations.
   *  
   * @param assignations
   */
  public void setAssignations(Set<Assignation<InteractionClass>> assignations) {
    this.assignations = assignations;
  }

  /**
   * Getter for assignations.
   *  
   * @return
   */
  public Set<Assignation<InteractionClass>> getAssignations() {
    return assignations;
  } 
  
  /**
   * Method adding assignation to this.assignations
   *  
   * @param assignation
   */
  public void addAssignation(Assignation<InteractionClass> assignation) {
    if (assignation != null)
      this.assignations.add(assignation);
  }

  /**
   * Method removing assignation to from this.assignations.
   *  
   * @param assignation
   */
  public void removeAssignation(Assignation<InteractionClass> assignation)  {
    if (assignation == null)
      return;
    this.assignations.remove(assignation);
  }
}

