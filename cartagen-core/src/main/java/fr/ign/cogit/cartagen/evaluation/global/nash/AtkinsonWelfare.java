package fr.ign.cogit.cartagen.evaluation.global.nash;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * Atkinson Welfare (Arrow et al, 2002, Chap. 12) that can vary from utilitarian
 * (when p tends to 1) to egalitarism (when p tends to -infinity). When the
 * parameter p is equal to 0, it's a classical Nash welfare.
 * @author GTouya
 * 
 */
public class AtkinsonWelfare implements GlobalEvaluationMethod {

  private double p = -3.0;

  public AtkinsonWelfare() {
  }

  public AtkinsonWelfare(double p) {
    super();
    this.p = p;
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
    double numerator = 0.0;
    double denominator = 0.0;
    double total = 1.0;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      if (p != 0.0) {
        numerator += Math.pow(distribution.getDistribution().get(m).ordinal()
            * m.getImportance(), p);
        denominator += m.getImportance();
      } else {
        total *= Math.pow(distribution.getDistribution().get(m).ordinal() + 1,
            m.getImportance());
      }
    }
    if (p != 0.0)
      return Math.pow(numerator / denominator, 1 / p);
    return Math.pow(total, 1 / distribution.getCardinal()) - 1;
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return evaluate(distribution);
  }

  @Override
  public String getName() {
    return "AtkinsonWelfare (" + p + ")";
  }

}
