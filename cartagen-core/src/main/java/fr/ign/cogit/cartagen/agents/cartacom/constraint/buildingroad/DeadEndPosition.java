/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

/**
 * The road constraint for the relative position relation between a dead end
 * road and a building. See p. 156-157 in C. Duchêne's PhD.
 * @author GTouya
 * 
 */
public class DeadEndPosition extends MicroMicroRelationalConstraint {

  public DeadEndPosition(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance) {
    super(ag, rel, importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.lib.constraints.geographicConstraints.
   * GeographicConstraint#computePriority()
   */
  @Override
  public void computePriority() {
    // TODO
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    // TODO
    return actionsSet;
  }
}
