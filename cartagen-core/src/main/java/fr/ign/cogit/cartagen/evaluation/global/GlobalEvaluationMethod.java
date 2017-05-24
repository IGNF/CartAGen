package fr.ign.cogit.cartagen.evaluation.global;

import java.util.Comparator;

public interface GlobalEvaluationMethod extends
    Comparator<ConstraintSatisfactionDistribution> {

  /**
   * Compare two constraint satisfaction distributions according to this method.
   * 0 means that the distributions are equal. 1 means that distribution1 is
   * better than distribution2. -1 means that distribution2 is better than
   * distribution1. It corresponds to the SWO (Social Welfare Ordering) of the
   * method.
   * @param distribution1
   * @param distribution2
   * @return
   */
  @Override
  int compare(ConstraintSatisfactionDistribution distribution1,
      ConstraintSatisfactionDistribution distribution2);

  /**
   * Compute an aggregated value for the global evaluation of the distribution.
   * Corresponds to the CUF (Collective Utility Function) of the method.
   * @param distribution
   * @return
   */
  double evaluate(ConstraintSatisfactionDistribution distribution);

  /**
   * Compute a normalised aggregated value for the global evaluation of the
   * distribution.
   * @param distribution
   * @return
   */
  double normalisedEvaluation(ConstraintSatisfactionDistribution distribution);

  /**
   * The name of the name for display purposes
   * @return
   */
  String getName();
}
