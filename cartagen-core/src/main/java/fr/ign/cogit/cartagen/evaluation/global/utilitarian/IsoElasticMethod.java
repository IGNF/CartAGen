package fr.ign.cogit.cartagen.evaluation.global.utilitarian;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

public class IsoElasticMethod implements GlobalEvaluationMethod {

  /**
   * the iso-elastic should be way bigger than 1 and cannot be equal to 1. If it
   * is close to 1, the method is close to Nash welfare. If it is close to 0,
   * the method is close to Rawlsian welfare (take the minimum)
   */
  private double a = 5;

  public IsoElasticMethod() {
  }

  public IsoElasticMethod(double a) {
    this.a = a;
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
    double total = 0.0;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      total += Math.pow(distribution.getDistribution().get(m).ordinal()
          * m.getImportance(), 1 - a);
    }
    return total / (1 - a);
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return evaluate(distribution);
  }

  @Override
  public String getName() {
    return "IsoElasticMethod (" + a + ")";
  }

}
