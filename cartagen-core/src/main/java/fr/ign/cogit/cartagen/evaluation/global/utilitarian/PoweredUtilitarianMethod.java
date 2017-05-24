package fr.ign.cogit.cartagen.evaluation.global.utilitarian;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * This is a utilitarian method that favours more the satisfied constraints from
 * the distribution.
 * @author GTouya
 * 
 */
public class PoweredUtilitarianMethod implements GlobalEvaluationMethod {

  private double p = 2;

  public PoweredUtilitarianMethod() {
    super();
  }

  public PoweredUtilitarianMethod(double power) {
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
    double denominator = 0.0;
    double numerator = 0.0;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      denominator += Math.pow(m.getImportance(), p);
      numerator += Math.pow(m.getImportance(), p)
          * Math.pow(distribution.getDistribution().get(m).ordinal(), p);
    }
    return Math.pow(numerator / denominator, 1 / p);
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return evaluate(distribution);
  }

  @Override
  public String getName() {
    return "PoweredUtilitarianMethod (" + p + ")";
  }

}
