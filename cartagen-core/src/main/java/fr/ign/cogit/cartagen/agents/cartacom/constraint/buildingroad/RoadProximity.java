/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.ConstrainedZoneDrivenDisplacement;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

/**
 * @author JGaffuri
 * 
 */
public class RoadProximity extends MicroMicroRelationalConstraint {

  public RoadProximity(ICartAComAgentGeneralisation ag, MicroMicroRelation rel,
      double importance) {
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
    /*
     * # Priorite 4 si satisfaction < 3 (on laisse la priorite 5 # # aux
     * problemes de superposition avec d'autres lineaires # # Sinon, on relache
     * la priorite a 3 # else priorite := 2.0;
     */
    // FIXME dans la commentaire il dit priorite sera 3 mais dans le code,
    // c'est affecte 2.0
    double sfact = this.getRelation().getSatisfaction();

    if (sfact < 3.0) {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_4);
    } else {
      this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_2);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    if (this.getSatisfaction() >= 5) {
      return actionsSet;
    }

    for (int i = 1; i <= 3; i++) {
      // CartacomAction action2 = new AskToMove(
      // (CartAComAgentGeneralisation) this.getAgent(), this, i - 1,
      // (SmallCompactAgent) this.getAgentSharingConstraint(), i);
      // actionsSet.add(new ActionProposal(this, true, action2, (4 - i)));

      // edit Guillaume: the action ask to move is not implemented yet so this
      // is a temporary hack
      if (this.getAgent() instanceof NetworkSectionAgent) {
        ICartAComAgentGeneralisation agent = this.getRelation()
            .getOtherAgent(this.getAgent());
        CartacomAction action2 = new ConstrainedZoneDrivenDisplacement(agent,
            this.getRelation().getOtherConstraint(this), i - 1, 4 - i);
        action2.setActItselfAction(false);
        actionsSet.add(new ActionProposal(this, false, action2, 4 - i));
      } else {
        CartacomAction action2 = new ConstrainedZoneDrivenDisplacement(
            this.getAgent(), this.getRelation().getOtherConstraint(this), i - 1,
            4 - i);
        action2.setActItselfAction(false);
        actionsSet.add(new ActionProposal(this, false, action2, 4 - i));
      }
    }
    return actionsSet;
  }
}
