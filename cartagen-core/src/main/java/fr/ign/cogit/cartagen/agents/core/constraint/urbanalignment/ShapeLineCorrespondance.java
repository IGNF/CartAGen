/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.urbanalignment.HomogeneousSpatialRepartitionAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JRenard
 * 
 */
public class ShapeLineCorrespondance extends GeographicObjectConstraintImpl {

  /**
   */
  private double meanBuildingDistanceToShapeLine;

  public ShapeLineCorrespondance(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    double mean = 0.0;
    int nbComponents = 0;
    for (IUrbanElementAgent build : ((UrbanAlignmentAgent) this.getAgent())
        .getComponents()) {
      if (build.isDeleted())
        continue;
      nbComponents++;
      mean += build.getGeom()
          .distance(((UrbanAlignmentAgent) this.getAgent()).getShapeLine());
    }
    meanBuildingDistanceToShapeLine = mean / nbComponents;
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(2);
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    int s = 100 - (int) meanBuildingDistanceToShapeLine;
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new HomogeneousSpatialRepartitionAction(
        (UrbanAlignmentAgent) this.getAgent(), this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
