/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.network;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 * 
 */
public class Convexity extends GeographicObjectConstraintImpl {
  /**
   */
  private double convexiteCourante;

  public Convexity(GeographicAgent agent, double importance) {
    super(agent, importance);
    ((IMicroAgentGeneralisation) agent).computeInitialConvexity();
  }

  @Override
  public void computeCurrentValue() {
    if (this.getAgent().isDeleted()) {
      this.convexiteCourante = 1.0;
    } else {
      this.convexiteCourante = CommonAlgorithms
          .convexity(this.getAgent().getGeom());
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(0);
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgent().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();

    double d = Math.abs(this.convexiteCourante
        - ((IMicroAgentGeneralisation) this.getAgent()).getInitialConvexity());
    if (d < GeneralisationSpecifications.BUILDING_CONVEXITE_MINI) {
      this.setSatisfaction(100);
      return;
    }

    this.setSatisfaction(
        100 + (int) ((GeneralisationSpecifications.BUILDING_CONVEXITE_MINI - d)
            / AgentSpecifications.CONVEXITE_BATIMENT_POINT_SATISFACTION));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
