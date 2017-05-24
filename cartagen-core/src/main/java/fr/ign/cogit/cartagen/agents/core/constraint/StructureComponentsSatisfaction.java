package fr.ign.cogit.cartagen.agents.core.constraint;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.StructureComponentsActivation;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * 
 * A default meso components constraints. It proposes the default meso
 * components activation action
 * 
 * @author JGaffuri
 * 
 */
public class StructureComponentsSatisfaction
    extends GeographicObjectConstraintImpl {

  /**
   * value of the measure: the satisfaction of the components
   */
  private double componentsSatisfaction;

  /**
   * @param agent
   * @param importance
   */
  public StructureComponentsSatisfaction(GeographicObjectAgent agent,
      double importance) {
    super(agent, importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#
   * computeCurrentValue ()
   */
  @Override
  public void computeCurrentValue() {
    this.componentsSatisfaction = ((InternStructureAgent) this.getAgent())
        .getComponentsSatisfaction();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#computePriority
   * ()
   */
  @Override
  public void computePriority() {
    this.setPriority(5);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#
   * computeSatisfaction ()
   */
  @Override
  public void computeSatisfaction() {
    if (this.getAgent().getFeature().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();

    // the satisfaction is directly the components statisfaction value
    this.setSatisfaction(this.componentsSatisfaction);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#getActions()
   */
  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new StructureComponentsActivation(this.getAgent(),
        this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
