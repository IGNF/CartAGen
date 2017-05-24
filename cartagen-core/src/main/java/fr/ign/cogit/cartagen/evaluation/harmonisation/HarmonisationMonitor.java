package fr.ign.cogit.cartagen.evaluation.harmonisation;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public abstract class HarmonisationMonitor extends AbstractFeature implements
    SpecificationMonitor {

  protected ConstraintSatisfaction satisfaction;
  protected CopyOnWriteArrayList<ConstraintSatisfaction> etatsSatisf;
  private int id;
  private static AtomicInteger counter = new AtomicInteger();
  private IFeature feat;
  private Object initialValue, currentValue, goalValue;
  private String name;
  private IDirectPosition position;

  public HarmonisationMonitor(IFeature feat) {
    this.setId(HarmonisationMonitor.counter.getAndIncrement());
    this.etatsSatisf = new CopyOnWriteArrayList<ConstraintSatisfaction>();
    this.feat = feat;
    computeCurrentValue();
    setInitialValue(currentValue);
    computeGoalValue();
    computeSatisfaction();
    this.computePosition();
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

  public abstract void computeCurrentValue();

  public abstract void computeGoalValue();

  public abstract void computeSatisfaction();

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

  public IFeature getFeat() {
    return feat;
  }

  public void setFeat(IFeature feat) {
    this.feat = feat;
  }

  public Object getInitialValue() {
    return initialValue;
  }

  public void setInitialValue(Object initialValue) {
    this.initialValue = initialValue;
  }

  public Object getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(Object currentValue) {
    this.currentValue = currentValue;
  }

  public Object getGoalValue() {
    return goalValue;
  }

  public void setGoalValue(Object goalValue) {
    this.goalValue = goalValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((feat == null) ? 0 : feat.hashCode());
    result = prime * result + id;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    HarmonisationMonitor other = (HarmonisationMonitor) obj;
    if (feat == null) {
      if (other.feat != null)
        return false;
    } else if (!feat.equals(other.feat))
      return false;
    if (id != other.id)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  private void computePosition() {
    IDirectPosition centroid = getFeat().getGeom().centroid();
    Random shifter = new Random();
    boolean signX = shifter.nextBoolean();
    boolean signY = shifter.nextBoolean();
    double x = centroid.getX();
    double y = centroid.getY();
    int dx = shifter.nextInt(10);
    int dy = shifter.nextInt(10);
    if (signX)
      x = x + dx;
    else
      x = x - dx;
    if (signY)
      y = y + dy;
    else
      y = y - dy;
    this.position = new DirectPosition(x, y);
  }

  @Override
  public IGeometry getGeom() {
    return position.toGM_Point();
  }

}
