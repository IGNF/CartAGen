package fr.ign.cogit.cartagen.evaluation.global.egalitarian;

import java.util.List;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * Like LeximinPovertyLine class, it is leximin-based under the poverty line
 * than utilitarian if the differences are above the poverty line. But, the
 * compare method returns a more realistic gap than 1 or -1. The method has a
 * isSignificantlyBetter() method based on this advance compare method.
 * @author GTouya
 * 
 */
public class LeximinPovertyLine2 implements GlobalEvaluationMethod {

  private double povertyLine = ConstraintSatisfaction.UNACCEPTABLE.ordinal();

  public LeximinPovertyLine2() {
    super();
  }

  public LeximinPovertyLine2(double povertyLine) {
    super();
    this.povertyLine = povertyLine;
  }

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    List<ConstraintSatisfaction> list1 = distribution1.toList();
    List<ConstraintSatisfaction> list2 = distribution2.toList();
    for (int i = 0; i < distribution1.getCardinal(); i++) {
      double val1 = list1.get(i).ordinal();
      double val2 = list2.get(i).ordinal();
      if (val1 > povertyLine && val2 > povertyLine)
        return utilCompare(distribution1, distribution2);
      if (val1 > val2 && val2 <= povertyLine) {
        // compute the gap between the distributions
        int j;
        for (j = i + 1; j < distribution2.getCardinal(); j++)
          if (list2.get(j).ordinal() > povertyLine)
            break;
        int gap = j - i;
        return gap;
      }
      if (val2 > val1 && val1 <= povertyLine) {
        // compute the gap between the distributions
        int j;
        for (j = i + 1; j < distribution1.getCardinal(); j++)
          if (list1.get(j).ordinal() > povertyLine)
            break;
        int gap = j - i;
        return -gap;
      }
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
    return "LeximinPovertyLine (" + povertyLine + ")";
  }

  public boolean isSignificantlyBetter(
      ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2, double ratioThreshold) {
    int compareValue = this.compare(distribution1, distribution2);
    double ratio = new Double(compareValue)
        / new Double(distribution1.getCardinal());
    if (ratio >= ratioThreshold)
      return true;
    return false;
  }
}
