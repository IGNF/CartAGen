package fr.ign.cogit.cartagen.evaluation.global.egalitarian;

import java.util.List;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.evaluation.global.GlobalEvaluationMethod;

public class StandardLeximin implements GlobalEvaluationMethod {

  @Override
  public int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2) {
    List<ConstraintSatisfaction> list1 = distribution1.toList();
    List<ConstraintSatisfaction> list2 = distribution2.toList();
    for (int i = 0; i < distribution1.getCardinal(); i++) {
      if (list1.get(i).ordinal() > list2.get(i).ordinal())
        return 1;
      if (list2.get(i).ordinal() > list1.get(i).ordinal())
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

  @Override
  public String getName() {
    return "StandardLeximin";
  }

}
