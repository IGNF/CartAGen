package fr.ign.cogit.cartagen.evaluation.global.nash;

import java.math.BigInteger;

import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * Classical Nash welfare method excluding any zero as, in this case, even the
 * worse satisfaction in one case should not put the global evaluation to zero.
 * @author GTouya
 * 
 */
public class NashWelfare implements GlobalEvaluationMethod {

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    Double eval1 = evaluate(distribution1);
    Double eval2 = evaluate(distribution2);
    return eval1.compareTo(eval2);
  }

  @Override
  public double evaluate(ConstraintSatisfactionDistribution distribution) {
    BigInteger total = BigInteger.ONE;
    for (SpecificationMonitor m : distribution.getDistribution().keySet()) {
      BigInteger factor = new BigInteger(""
          + distribution.getDistribution().get(m).ordinal() + 1).pow(m
          .getImportance());
      total = total.multiply(factor);
    }
    return total.doubleValue();
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    double power = 1.0 / new Double(distribution.getCardinal());
    return Math.pow(evaluate(distribution), power);
  }

  @Override
  public String getName() {
    return "NashWelfare";
  }

}
