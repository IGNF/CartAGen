package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;

/**
 * A constraint on two submicro object It can be considered as a relational
 * constraint at the submicro level.
 * 
 * @author JGaffuri
 */
public abstract class SubmicroRelationnalConstraint extends SubmicroConstraint {

  /**
   * The constructor
   * 
   * @param sm1
   * @param sm2
   * @param importance
   */
  public SubmicroRelationnalConstraint(ISubMicro sm1, ISubMicro sm2,
      double importance) {
    super(importance);

    // link submicros - constraint
    sm1.getSubmicroConstraints().add(this);
    sm2.getSubmicroConstraints().add(this);

    // links concerning point agents of sm1
    for (IPointAgent p : sm1.getPointAgents()) {
      // update point agent total importance
      p.incrementerSommeImportances(importance);

      // link point agent - constraint
      p.getConstraints().add(this);

      // accointance links between sm1 and sm2 points agent
      for (IPointAgent p_ : sm2.getPointAgents()) {
        p.addAgentPointAccointants(p_);
      }
    }

    // links concerning point agents of sm2
    for (IPointAgent p : sm2.getPointAgents()) {
      // update point agent total importance
      p.incrementerSommeImportances(importance);

      // link point agent - constraint
      p.getConstraints().add(this);

      // accointance links between sm1 and sm2 points agent
      for (IPointAgent p_ : sm1.getPointAgents()) {
        p.addAgentPointAccointants(p_);
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }

  @Override
  public int hashCode() {
    return this.getClass().getSimpleName().hashCode();
  }

}
