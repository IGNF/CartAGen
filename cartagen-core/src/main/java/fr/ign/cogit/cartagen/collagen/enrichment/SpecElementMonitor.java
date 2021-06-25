package fr.ign.cogit.cartagen.collagen.enrichment;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;

public abstract class SpecElementMonitor extends AbstractFeature
    implements SpecificationMonitor {

  protected ConstraintSatisfaction satisfaction;
  protected CopyOnWriteArrayList<ConstraintSatisfaction> etatsSatisf;
  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public SpecElementMonitor() {
    this.setId(SpecElementMonitor.counter.getAndIncrement());
    this.etatsSatisf = new CopyOnWriteArrayList<ConstraintSatisfaction>();
  }

  @Override
  public ConstraintSatisfaction getSatisfaction() {
    return this.satisfaction;
  }

  public void setSatisfaction(ConstraintSatisfaction satisfaction) {
    this.satisfaction = satisfaction;
  }

  public CopyOnWriteArrayList<ConstraintSatisfaction> getEtatsSatisf() {
    return this.etatsSatisf;
  }

  public void setEtatsSatisf(
      CopyOnWriteArrayList<ConstraintSatisfaction> etatsSatisf) {
    this.etatsSatisf = etatsSatisf;
  }

  public abstract SpecificationElement getElementSpec();

  public abstract void computeCurrentValue();

  public abstract void computeGoalValue();

  /**
   * Get the importance of the monitor that corresponds to the number of objects
   * it involves.
   * @return
   */
  @Override
  public abstract int getImportance();

  @Override
  public java.util.List<ConstraintSatisfaction> getPreviousStates() {
    return this.etatsSatisf;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public String getSatisfactionString() {
    return this.satisfaction.name();
  }

}
