package fr.ign.cogit.cartagen.agents.core.action;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionImpl;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri 24 aout 2004
 * 
 */
public abstract class ActionCartagen extends ActionImpl {

  /**
   * build an action for an agent, proposed by a constraint, with a weight
   * 
   * @param agent
   * @param constraint
   * @param weight
   */
  public ActionCartagen(IAgent agent, Constraint constraint, double weight) {
    super(agent, constraint, weight);

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#nettoyer()
   */
  @Override
  public void clean() {
    super.clean();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

  /**
     */
  @ActionField
  private String nom;

  /**
   * @return
   */
  public String getNom() {
    return this.nom;
  }

  /**
   * @param nom
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

}
