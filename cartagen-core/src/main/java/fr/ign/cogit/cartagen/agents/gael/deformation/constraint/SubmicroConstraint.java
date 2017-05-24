package fr.ign.cogit.cartagen.agents.gael.deformation.constraint;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.ConstraintImpl;

/**
 * The submicro constraint class. Such constraint is on a submicro object and
 * can propose a displacement to a point of this submicro.
 * 
 * @author JGaffuri
 * 
 */
public abstract class SubmicroConstraint extends ConstraintImpl {

  /**
   * Build a constraint with a given importance
   * 
   * @param importance
   */
  public SubmicroConstraint(double importance) {
    super(importance);
  }

  /**
   * Propose a displacement to a point agent
   * 
   * @param p
   */
  public void proposeDisplacement(IPointAgent p) {
    // System.out.println("Propose displacement " + p + " importance "
    // + this.getImportance() + " / somme " + p.getSommeImportances());
    this.proposeDisplacement(p, this.getImportance() / p.getSommeImportances());
  }

  /**
   * Propose a displacement to a point agent in order to improve the submicro
   * character of a ratio alpha
   * 
   * @param p
   * @param alpha
   */
  public abstract void proposeDisplacement(IPointAgent p, double alpha);

}
