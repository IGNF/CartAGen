package fr.ign.cogit.cartagen.evaluation.global.nash;

import java.util.logging.Logger;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * Nash welfare method with total utility powered by 1/N. Corresponds to the
 * geometric mean of the distribution.
 * @author GTouya
 * 
 */
public class BernoulliNashWelfare implements GlobalEvaluationMethod {

  private static Logger logger = Logger.getLogger(BernoulliNashWelfare.class
      .getName());

  public BernoulliNashWelfare() {
    super();
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
    double power = 1.0 / new Double(distribution.getCardinal());
    logger.fine("power: " + power);
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      double value = Math.pow(new Double(distribution.getDistribution().get(m)
          .ordinal() + 1), new Double(m.getImportance()) * power);
      logger.fine("value: " + value);
      total *= value;
    }
    return total;
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return evaluate(distribution) - 1;
  }

  @Override
  public String getName() {
    return "BernoulliNashWelfare";
  }

}
