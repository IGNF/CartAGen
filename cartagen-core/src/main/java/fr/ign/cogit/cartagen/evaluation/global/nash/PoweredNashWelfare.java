package fr.ign.cogit.cartagen.evaluation.global.nash;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * Nash welfare method with each utility powered by p.
 * @author GTouya
 * 
 */
public class PoweredNashWelfare implements GlobalEvaluationMethod {

  private double p = 2;

  public PoweredNashWelfare() {
    super();
  }

  public PoweredNashWelfare(double power) {
    this.p = power;
  }

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    double eval1 = evaluate(distribution1);
    double eval2 = evaluate(distribution2);
    if (eval1 > eval2)
      return 1;
    if (eval2 > eval1)
      return -1;
    return 0;
  }

  @Override
  public double evaluate(ConstraintSatisfactionDistribution distribution) {
    double total = 1.0;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      total *= Math.pow(distribution.getDistribution().get(m).ordinal() + 1, m
          .getImportance()
          + p);
    }
    return Math.pow(total, 1 / p);
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return Math.pow(evaluate(distribution), 1 / distribution.getCardinal()) - 1;
  }

  @Override
  public String getName() {
    return "PoweredNashWelfare (" + p + ")";
  }

}
