/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.network;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.action.micro.AffinityAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 * 
 */
public class Elongation extends GeographicObjectConstraintImpl {
  /**
   */
  private double elongationCourante;

  /**
   * Getter for elongationCourante.
   * @return elongationCourante
   * @author AMaudet
   */
  public double getElongationCourante() {
    return this.elongationCourante;
  }

  public Elongation(GeographicAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    if (this.getAgent().isDeleted()) {
      this.elongationCourante = 1.0;
    }
    if (this.getAgent().getGeom().coord().size() < 4) {
      this.elongationCourante = 1.0;
    } else {
      this.elongationCourante = CommonAlgorithms
          .elongation(this.getAgent().getGeom());
    }
    ((IMicroAgentGeneralisation) this.getAgent()).computeInitialElongation();
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
    if (this.getAgent().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();

    double d = Math.abs(this.elongationCourante
        - ((IMicroAgentGeneralisation) this.getAgent()).getInitialElongation());
    if (d < GeneralisationSpecifications.BUILDING_ELONGATION_MINI) {
      this.setSatisfaction(100);
      return;
    }

    this.setSatisfaction(
        100 + (int) ((GeneralisationSpecifications.BUILDING_ELONGATION_MINI - d)
            / AgentSpecifications.ELONGATION_BUILDING_POINT_SATISFACTION));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }

  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    double angle = ((IMicroAgentGeneralisation) this.getAgent())
        .getGeneralOrientation() + Math.PI / 2;
    double coef = ((IMicroAgentGeneralisation) this.getAgent())
        .getInitialElongation() / this.elongationCourante;
    Action actionToPropose = new AffinityAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 1.0, angle, coef);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
