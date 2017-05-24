package fr.ign.cogit.cartagen.evaluation.global.egalitarian;

import java.util.List;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

/**
 * This egalitarian method uses a poverty line and an affluence line. Two
 * conditions have to be met (see Tungodden, 2009, p.240).
 * @author GTouya
 * 
 */
public class WeakPovertyMean implements GlobalEvaluationMethod {

  private double povertyLine = ConstraintSatisfaction.BARELY_SATISFIED
      .ordinal();
  private double affluenceLine = ConstraintSatisfaction.VERY_SATISFIED
      .ordinal();

  public WeakPovertyMean() {
    super();
  }

  public WeakPovertyMean(double povertyLine, double affluenceLine) {
    super();
    this.povertyLine = povertyLine;
    this.affluenceLine = affluenceLine;
  }

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    List<ConstraintSatisfaction> list1 = distribution1.toList();
    List<ConstraintSatisfaction> list2 = distribution2.toList();
    boolean condition1_1 = false, condition1_2 = false;
    boolean condition2_1 = true, condition2_2 = true;
    for (int i = 0; i < distribution1.getCardinal(); i++) {
      double val1 = list1.get(i).ordinal();
      double val2 = list2.get(i).ordinal();
      if (val1 > val2 && val2 < povertyLine)
        condition1_1 = true;
      if (val2 > val1 && val1 < povertyLine)
        condition1_2 = true;
      if (val1 < affluenceLine && val2 >= affluenceLine)
        condition2_1 = false;
      if (val2 < affluenceLine && val1 >= affluenceLine)
        condition2_2 = false;
    }
    if (condition1_1 && condition2_1)
      return 1;
    if (condition1_2 && condition2_2)
      return -1;
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

  @Override
  public String getName() {
    return "WeakPovertyMean (" + povertyLine + ", " + affluenceLine + ")";
  }

}
