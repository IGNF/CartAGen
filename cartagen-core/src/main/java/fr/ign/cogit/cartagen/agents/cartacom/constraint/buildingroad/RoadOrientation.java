/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.RotationAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

/**
 * @author JGaffuri
 * 
 */
public class RoadOrientation extends MicroMicroRelationalConstraint {

  public RoadOrientation(ICartAComAgentGeneralisation ag,
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
    double sfact = this.getAgent().getSatisfaction();

    if (sfact <= CartacomSpecifications.SATISFACTION_2) {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_3);
    } else {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_1);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {

    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    if (this.getSatisfaction() >= 5) {
      return actionsSet;
    }

    CartacomAction action = new RotationAction(this.getAgent(),
        this.getRelation().getOtherConstraint(this), 1);
    action.setActItselfAction(false);
    actionsSet.add(new ActionProposal(this, false, action, 1));

    return actionsSet;

  }
}
