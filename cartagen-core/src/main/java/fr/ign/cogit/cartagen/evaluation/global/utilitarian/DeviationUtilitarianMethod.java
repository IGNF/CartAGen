package fr.ign.cogit.cartagen.evaluation.global.utilitarian;

import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * This is a utilitarian method that favours more the distributions with small
 * standard deviation.
 * @author GTouya
 * 
 */
public class DeviationUtilitarianMethod implements GlobalEvaluationMethod {

  /**
   * The mean difference under which the standard deviation is taken into
   * account to compare two distributions.
   */
  private double stdDevThresh = 1;

  public DeviationUtilitarianMethod() {
    super();
  }

  public DeviationUtilitarianMethod(double stdDevThresh) {
    this.stdDevThresh = stdDevThresh;
  }

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    double eval1 = evaluate(distribution1);
    double eval2 = evaluate(distribution2);
    double stdDev1 = distribution1.getStandardDeviation();
    double stdDev2 = distribution2.getStandardDeviation();
    // case with quite different means: the std deviation isn't used
    if (Math.abs(eval1 - eval2) > stdDevThresh) {
      if (eval1 > eval2)
        return 1;
      if (eval2 > eval1)
        return -1;
      return 0;
    }
    // case with close means: the std deviation is used to weight the comparison
    if (eval1 > eval2 && stdDev1 < stdDev2)
      return 1;
    if (eval2 > eval1 && stdDev1 > stdDev2)
      return -1;
    if (eval1 > eval2 && stdDev1 > stdDev2 + 1)
      return -1;
    if (eval2 > eval1 && stdDev2 > stdDev1 + 1)
      return 1;
    return 0;
  }

  @Override
  public double evaluate(ConstraintSatisfactionDistribution distribution) {
    double mean = distribution.getMean();

    return mean;
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return evaluate(distribution);
  }

  @Override
  public String getName() {
    return "DeviationUtilitarianMethod (" + stdDevThresh + ")";
  }

}
