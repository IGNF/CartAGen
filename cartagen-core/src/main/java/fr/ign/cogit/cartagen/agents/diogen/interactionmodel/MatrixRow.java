package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * Row of an Assignation matrix for IODA/PADAWAN models. Is associated in the
 * Assignation Matrix with a family of source agent.
 * http://www.lifl.fr/SMAC/projects/ioda/
 * @author AMaudet
 * 
 */
public class MatrixRow<InteractionClass extends Interaction> {

  /**
   * The cell for degenrate assignations (ie. the source is the target).
   */
  private MatrixCell<InteractionClass> degenerateAssignations = new MatrixCell<InteractionClass>();

  /**
   * The cell for host assignations (ie. the target is the host)
   */
  private MatrixCell<InteractionClass> hostAssignations = new MatrixCell<InteractionClass>();

  /**
   * The cells for interaction with other families.
   */
  private Map<Class<? extends IAgent>, MatrixCell<InteractionClass>> cells = new Hashtable<Class<? extends IAgent>, MatrixCell<InteractionClass>>();

  /**
   * Setter for degenerateAssignations
   * 
   * @param degenerateAssignations
   */
  public void setDegenerateAssignations(
      MatrixCell<InteractionClass> degenerateAssignations) {
    this.degenerateAssignations = degenerateAssignations;
  }

  /**
   * Getter for degenerateAssignations
   * 
   * @return
   */
  public MatrixCell<InteractionClass> getDegenerateAssignations() {
    return degenerateAssignations;
  }

  /**
   * Setter for hostAssignations
   * 
   * @param hostAssignations
   */
  public void setHostAssignations(
      MatrixCell<InteractionClass> hostAssignations) {
    this.hostAssignations = hostAssignations;
  }

  /**
   * Getter for hostAssignations
   * 
   * @return
   */
  public MatrixCell<InteractionClass> getHostAssignations() {
    return hostAssignations;
  }

  /**
   * Setter for cells.
   * 
   * @param cells
   */
  public void setCells(
      Map<Class<? extends IAgent>, MatrixCell<InteractionClass>> cells) {
    this.cells = cells;
  }

  /**
   * Geter for cells.
   * 
   * @return
   */
  public Map<Class<? extends IAgent>, MatrixCell<InteractionClass>> getCells() {
    return cells;
  }

  /**
   * Method adding a new assignation with the family target in this row.
   * 
   * @param target
   * @param assignation
   */
  public void addSingleTargetAssignation(Class<? extends IAgent> target,
      Assignation<InteractionClass> assignation) {
    MatrixCell<InteractionClass> cell = this.cells.get(target);
    if (cell == null) {
      cell = new MatrixCell<InteractionClass>();
      this.cells.put(target, cell);
    }
    cell.addAssignation(assignation);

  }

  /**
   * Method adding a new degenerate assignation.
   * 
   * @param assignation
   */
  public void addDegenerateAssignation(
      Assignation<InteractionClass> assignation) {
    MatrixCell<InteractionClass> cell = degenerateAssignations;
    cell.addAssignation(assignation);
  }

  /**
   * Return the set of assignations of the targetClass.
   * 
   * @param targetClass
   * @return
   */
  public Set<Assignation<InteractionClass>> getAssignations(
      Class<? extends IAgent> targetClass) {
    MatrixCell<InteractionClass> cell = cells.get(targetClass);
    if (cell != null)
      return cell.getAssignations();
    return null;
  }

}
