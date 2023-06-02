/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.RotationAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.buildingroad.Parallelism;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

/**
 * @author JGaffuri
 * 
 */
public class BuildingOrientation extends MicroMicroRelationalConstraint {

  /**
   * Logger for this class
   */
  private static Logger LOGGER = LogManager.getLogger(Parallelism.class.getName());

  public BuildingOrientation(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance) {
    super(ag, rel, importance);
    LOGGER.debug("Instanciation");
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

  public double getRotationAngle() {
    return ((Parallelism) this.getRelation()).getAngle();
  }

  @Override
  public Set<ActionProposal> getActions() {

    Set<ActionProposal> actionsSet = new HashSet<ActionProposal>();

    if (this.getSatisfaction() >= 5) {
      return actionsSet;
    }

    CartacomAction action = new RotationAction(
        (ISmallCompactAgent) this.getAgent(), this, 1);
    actionsSet.add(new ActionProposal(this, true, action, 1));
    return actionsSet;
  }

}
