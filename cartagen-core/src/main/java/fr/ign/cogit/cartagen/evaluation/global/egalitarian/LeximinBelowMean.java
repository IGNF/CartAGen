package fr.ign.cogit.cartagen.evaluation.global.egalitarian;

import java.util.List;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * This method is leximin-based under the mean than utilitarian if the
 * differences are above the mean.
 * @author GTouya
 * 
 */
public class LeximinBelowMean implements GlobalEvaluationMethod {

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    List<ConstraintSatisfaction> list1 = distribution1.toList();
    double mean1 = distribution1.getMean();
    List<ConstraintSatisfaction> list2 = distribution2.toList();
    double mean2 = distribution2.getMean();
    for (int i = 0; i < distribution1.getCardinal(); i++) {
      double val1 = list1.get(i).ordinal();
      double val2 = list2.get(i).ordinal();
      if (val1 > mean1 && val2 > mean2)
        return utilCompare(distribution1, distribution2);
      if (val1 > val2 && val1 <= mean1)
        return 1;
      if (val2 > val1 && val2 <= mean2)
        return -1;
    }
    return 0;
  }

  @Override
  public double evaluate(ConstraintSatisfactionDistribution distribution) {
    return distribution.toList().get(0).ordinal();
  }

  @Override
  public double normalisedEvaluation(
      ConstraintSatisfactionDistribution distribution) {
    return distribution.toList().get(0).ordinal();
  }

  private int utilCompare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    double mean1 = distribution1.getMean();
    double mean2 = distribution2.getMean();
    if (mean1 > mean2)
      return 1;
    if (mean2 > mean1)
      return -1;
    return 0;
  }

  @Override
  public String getName() {
    return "LeximinBelowMean";
  }
}
