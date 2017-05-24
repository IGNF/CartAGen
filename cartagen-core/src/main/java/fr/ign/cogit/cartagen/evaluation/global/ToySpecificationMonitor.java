package fr.ign.cogit.cartagen.evaluation.global;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;

/**
 * A class for toy specification monitor that simulate a specification monitor
 * in toy distributions.
 * @author GTouya
 * 
 */
public class ToySpecificationMonitor implements SpecificationMonitor {

  private ConstraintSatisfaction satisfaction;
  private ArrayList<ConstraintSatisfaction> previousStates;

  @Override
  public int getImportance() {
    return 1;
  }

  @Override
  public List<ConstraintSatisfaction> getPreviousStates() {
    return this.previousStates;
  }

  @Override
  public ConstraintSatisfaction getSatisfaction() {
    return this.satisfaction;
  }

  public ToySpecificationMonitor(ConstraintSatisfaction satisfaction) {
    this.satisfaction = satisfaction;
    this.previousStates = new ArrayList<ConstraintSatisfaction>();
    this.previousStates.add(satisfaction);
    this.previousStates.add(satisfaction);
  }

  public void addPreviousState(ConstraintSatisfaction satisfaction1) {
    this.previousStates.add(satisfaction1);
  }

  @Override
  public String getSatisfactionString() {
    return satisfaction.name();
  }

  @Override
  public void computeSatisfaction() {
    // do nothing
  }
}
