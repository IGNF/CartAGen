/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JRenard
 * 
 */
public class BuildingNumberPreservation extends GeographicObjectConstraintImpl {

  /**
   */
  private int deletedBuildingNumber;

  public BuildingNumberPreservation(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    int deletedElements = 0;
    for (IUrbanElementAgent urbanElement : ((UrbanAlignmentAgent) this
        .getAgent()).getComponents()) {
      if (urbanElement.isDeleted())
        deletedElements++;
    }
    this.deletedBuildingNumber = deletedElements;
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(3);
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    int s = 100 - deletedBuildingNumber;
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    // No action proposed, the constraint is just here to influence global
    // satisfaction
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    return actionProposals;
  }
}
