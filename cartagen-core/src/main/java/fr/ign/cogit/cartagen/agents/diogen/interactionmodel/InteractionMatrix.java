package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * Class InteractionMatrix Represent an IODA interaction matrix. Contain a
 * dictionary with the rows of the matrix http://www.lifl.fr/SMAC/projects/ioda/
 * @author AMaudet
 */
public class InteractionMatrix<InteractionClass extends Interaction> {

  /**
   * A set for all the class concerned by this matrix.
   */
  private Set<Class<? extends IAgent>> families = new HashSet<Class<? extends IAgent>>();

  /**
   * Getter for families.
   * @return the families
   */
  public Set<Class<? extends IAgent>> getFamilies() {
    return this.families;
  }

  /**
   * A special row for the host case.
   */
  private MatrixRow<InteractionClass> hostRow = new MatrixRow<InteractionClass>();

  /**
   * Setter for hostRow attribute.
   * 
   * @param hostRow
   */
  public void setHostRow(MatrixRow<InteractionClass> hostRow) {
    this.hostRow = hostRow;
  }

  /**
   * Getter for hostRow attribute.
   * 
   * @return
   */
  public MatrixRow<InteractionClass> getHostRow() {
    return this.hostRow;
  }

  /**
   * The Dictionary for interactions. The keys are the class (family) of agent.
   */
  private Map<Class<? extends IAgent>, MatrixRow<InteractionClass>> rows = new Hashtable<Class<? extends IAgent>, MatrixRow<InteractionClass>>();

  /**
   * Setter for rows
   * 
   * @param rows
   */
  public void setRows(
      Map<Class<? extends IAgent>, MatrixRow<InteractionClass>> rows) {
    this.rows = rows;
  }

  /**
   * Getter for rows.
   * 
   * @return
   */
  public Map<Class<? extends IAgent>, MatrixRow<InteractionClass>> getRows() {
    return this.rows;
  }

  /**
   * Add a new single target assignation to the matrix
   * 
   * @param source
   * @param target
   * @param assignation
   */
  public void addSingleTargetAssignation(Class<? extends IAgent> source,
      Class<? extends IAgent> target,
      Assignation<InteractionClass> assignation) {
    MatrixRow<InteractionClass> row = this.rows.get(source);
    if (row == null) {
      row = new MatrixRow<InteractionClass>();
      this.rows.put(source, row);
    }
    row.addSingleTargetAssignation(target, assignation);
    this.families.add(source);
    this.families.add(target);
  }

  /**
   * Return the row associated with the family source type.
   * 
   * @param family
   * @return
   */
  public MatrixRow<InteractionClass> getRow(Class<? extends IAgent> family) {
    return this.rows.get(family);
  }

  /**
   * Add a new degenerate interaction (ie. target = source)to the matrix
   * 
   * @param source
   * @param assignation
   */
  public void addDegenerateAssignation(Class<? extends IAgent> source,
      Assignation<InteractionClass> assignation) {
    MatrixRow<InteractionClass> row = this.rows.get(source);
    if (row == null) {
      row = new MatrixRow<InteractionClass>();
      this.rows.put(source, row);
    }
    row.addDegenerateAssignation(assignation);
    this.families.add(source);
  }

  /**
   * Add a new single target host assignation (ie. source = host) to the matrix
   * @param target
   * @param assignation
   */
  public void addSingleTargetHostAssignation(Class<IAgent> target,
      Assignation<InteractionClass> assignation) {
    MatrixRow<InteractionClass> row = this.hostRow;
    row.addSingleTargetAssignation(target, assignation);
    this.families.add(target);
  }

  /**
   * Add a new degenerate host assignation (ie. source = target = host) to the
   * matrix
   * @param assignation
   */
  public void addDegenerateHostAssignation(
      Assignation<InteractionClass> assignation) {
    MatrixRow<InteractionClass> row = this.hostRow;
    row.addDegenerateAssignation(assignation);
  }

  /**
   * Return assignations of the cells (source/target) of the matrix.
   * 
   * @param sourceClass
   * @param targetClass
   * @return
   */
  public Set<Assignation<InteractionClass>> getAssignations(
      Class<? extends IAgent> sourceClass,
      Class<? extends IAgent> targetClass) {
    MatrixRow<InteractionClass> row = this.getRow(sourceClass);
    if (row == null) {
      return null;
    }
    return row.getAssignations(targetClass);
  }

  /**
   * Return the degenerate assignations for sourceClass.
   * 
   * @param sourceClass
   * @return
   */
  public Set<Assignation<InteractionClass>> getDegenerateAssignation(
      Class<? extends IAgent> sourceClass) {
    MatrixRow<InteractionClass> row = this.getRow(sourceClass);
    if (row == null) {
      return null;
    }
    return row.getDegenerateAssignations().getAssignations();
  }

  /**
   * Return the row of the matrix for assignations implying the host as a source
   * and target as family of target agent.
   * 
   * @param targetClass
   * @return
   */
  public Set<Assignation<InteractionClass>> getHostAssignation(
      Class<? extends IAgent> targetClass) {
    MatrixRow<InteractionClass> row = this.getHostRow();
    if (row == null) {
      return null;
    }
    return row.getAssignations(targetClass);
  }

  /**
   * Return the row of the matrix for assignations implying the host as a target
   * and source as family of source agent.
   * 
   * @param targetClass
   * @return
   */
  public Set<Assignation<InteractionClass>> getHostAsTargetAssignation(
      @SuppressWarnings("unused") Class<? extends IAgent> sourceClass) {
    return null;
    // MatrixRow<InteractionClass> row = this.getHostRow();
    // if (row == null)
    // return null;
    // return row.getAssignations(sourceClass);
  }

  /**
   * Return the row of the matrix for degenerate assignations implying the host
   * as a source.
   * 
   * @return
   */
  public Set<Assignation<InteractionClass>> getDegenerateHostAssignation() {
    MatrixRow<InteractionClass> row = this.getHostRow();
    if (row == null) {
      return null;
    }
    return row.getDegenerateAssignations().getAssignations();
  }

  /**
   * Return a set of all class of agents used as target in this matrix.
   * 
   * @return
   */
  public Set<Class<? extends IAgent>> getAllRowFamilies() {
    return this.rows.keySet();
  }

  /**
   * Return a set of all class of agents used as target in this matrix.
   * 
   * @return
   */
  public Set<Class<? extends IAgent>> getAllColumnsFamilies() {
    Set<Class<? extends IAgent>> families1 = new HashSet<Class<? extends IAgent>>();
    families1.addAll(this.hostRow.getCells().keySet());
    for (MatrixRow<InteractionClass> r : this.rows.values()) {
      families1.addAll(r.getCells().keySet());
    }
    return families1;
  }

}
