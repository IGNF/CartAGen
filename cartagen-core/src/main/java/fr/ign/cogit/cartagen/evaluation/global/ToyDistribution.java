package fr.ign.cogit.cartagen.evaluation.global;

import java.util.HashMap;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;

public enum ToyDistribution {
  FAIR, GOOD_LOOSE, VERY_GOOD_LOOSE, GOOD, MEDIUM_EXTREME, MEDIUM_LOOSE, MEDIUM, MEDIUM_GOOD, SYMMETRICAL, GOOD_EXTREME, VERY_GOOD_EXTREME;

  /**
   * Builds a toy constraint distribution corresponding to the definition of
   * 'this' that can be found in the "echantillon_benchmark.xlsx" file. For
   * instance, a GOOD toy distribution has 10% of monitors with CORRECT
   * satisfaction, 30% of monitors with VERY_SATISFIED satisfaction and 60% of
   * monitors with PERFECT satisfaction.
   * @return the toy distribution of satisfactions.
   */
  public ConstraintSatisfactionDistribution getDistribution() {
    HashMap<SpecificationMonitor, ConstraintSatisfaction> distrib = new HashMap<SpecificationMonitor, ConstraintSatisfaction>();
    ConstraintSatisfaction c0 = ConstraintSatisfaction.valueOf(0);
    ConstraintSatisfaction c1 = ConstraintSatisfaction.valueOf(1);
    ConstraintSatisfaction c2 = ConstraintSatisfaction.valueOf(2);
    ConstraintSatisfaction c3 = ConstraintSatisfaction.valueOf(3);
    ConstraintSatisfaction c4 = ConstraintSatisfaction.valueOf(4);
    ConstraintSatisfaction c5 = ConstraintSatisfaction.valueOf(5);
    ConstraintSatisfaction c6 = ConstraintSatisfaction.valueOf(6);
    ConstraintSatisfaction c7 = ConstraintSatisfaction.valueOf(7);
    if (this.equals(FAIR)) {
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c1), c1);
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c2), c2);
      for (int i = 0; i < 14; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 14; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c5), c5);
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 12; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(GOOD_LOOSE)) {
      for (int i = 0; i < 5; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 8; i++)
        distrib.put(new ToySpecificationMonitor(c1), c1);
      for (int i = 0; i < 2; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 5; i++)
        distrib.put(new ToySpecificationMonitor(c5), c5);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 60; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(VERY_GOOD_LOOSE)) {
      for (int i = 0; i < 5; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 5; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 5; i++)
        distrib.put(new ToySpecificationMonitor(c5), c5);
      for (int i = 0; i < 10; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 75; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(GOOD)) {
      for (int i = 0; i < 10; i++)
        distrib.put(new ToySpecificationMonitor(c5), c5);
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 60; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(MEDIUM_EXTREME)) {
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c1), c1);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(MEDIUM_LOOSE)) {
      for (int i = 0; i < 25; i++)
        distrib.put(new ToySpecificationMonitor(c2), c2);
      for (int i = 0; i < 25; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 25; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 25; i++)
        distrib.put(new ToySpecificationMonitor(c5), c5);
    } else if (this.equals(MEDIUM)) {
      for (int i = 0; i < 50; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 50; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
    } else if (this.equals(MEDIUM_GOOD)) {
      for (int i = 0; i < 40; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 40; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(SYMMETRICAL)) {
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c3), c3);
      for (int i = 0; i < 20; i++)
        distrib.put(new ToySpecificationMonitor(c4), c4);
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(GOOD_EXTREME)) {
      for (int i = 0; i < 25; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 15; i++)
        distrib.put(new ToySpecificationMonitor(c1), c1);
      for (int i = 0; i < 10; i++)
        distrib.put(new ToySpecificationMonitor(c6), c6);
      for (int i = 0; i < 50; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    } else if (this.equals(VERY_GOOD_EXTREME)) {
      for (int i = 0; i < 30; i++)
        distrib.put(new ToySpecificationMonitor(c0), c0);
      for (int i = 0; i < 70; i++)
        distrib.put(new ToySpecificationMonitor(c7), c7);
    }
    return new ConstraintSatisfactionDistribution(name(), distrib);
  }

}
