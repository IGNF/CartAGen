package fr.ign.cogit.cartagen.evaluation;

import java.util.List;

public interface SpecificationMonitor {

  /**
   * The importance of the specification.
   * @return
   */
  public int getImportance();

  public ConstraintSatisfaction getSatisfaction();

  public void computeSatisfaction();

  public List<ConstraintSatisfaction> getPreviousStates();

  /**
   * Get the satisfaction as a String Value for display purposes.
   * @return
   */
  public String getSatisfactionString();
}
