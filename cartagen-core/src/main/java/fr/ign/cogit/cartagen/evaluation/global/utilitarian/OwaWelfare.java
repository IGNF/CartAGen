package fr.ign.cogit.cartagen.evaluation.global.utilitarian;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

public class OwaWelfare implements GlobalEvaluationMethod {

  private double[] weights = new double[] { 3.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0,
      1.0 };

  public OwaWelfare() {
  }

  public OwaWelfare(double[] weights) {
    super();
    this.weights = weights;
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
      double weight = weights[distribution.getDistribution().get(m).ordinal()];
      denominator += m.getImportance();
      numerator += m.getImportance() * weight
          * distribution.getDistribution().get(m).ordinal();
    }
    return numerator / denominator;
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    double denominator = 0.0;
    double numerator = 0.0;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      double weight = weights[distribution.getDistribution().get(m).ordinal()];
      denominator += m.getImportance() + weight;
      numerator += m.getImportance() * weight
          * distribution.getDistribution().get(m).ordinal();
    }
    return numerator / denominator;
  }

  @Override
  public String getName() {
    StringBuffer name = new StringBuffer("OwaWelfare (");
    for (Double weight : weights) {
      name.append(weight);
      name.append(", ");
    }
    name.append(")");
    return name.toString();
  }

}
