/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.building;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.action.micro.RotationAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public class Orientation extends GeographicObjectConstraintImpl {
  // orientation courante entre 0 et Pi
  /**
   */
  private double orientationCourante;

  /**
   * Getter for orientationCourante.
   * 
   * @return
   * @author AMaudet
   */
  public double getOrientationCourante() {
    return orientationCourante;
  }

  public Orientation(GeographicAgent agent, double importance) {
    super(agent, importance);
    ((IMicroAgentGeneralisation) agent).computeInitialGeneralOrientation();
  }

  @Override
  public void computeCurrentValue() {
    if (this.getAgent().isDeleted()) {
      this.orientationCourante = 999.9;
      return;
    }
    this.orientationCourante = ((IMicroAgentGeneralisation) this.getAgent())
        .getGeneralOrientation();
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    this.setPriority(1);
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgent().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }

    this.computeCurrentValue();

    if (this.orientationCourante == 999.9) {
      this.setSatisfaction(50);
      return;
    }
    double dOrientation = this.orientationCourante
        - ((IMicroAgentGeneralisation) this.getAgent())
            .getInitialGeneralOrientation();

    // dOrientation doit etre entre -PI/2 et PI/2
    if (dOrientation < -Math.PI / 2) {
      dOrientation += Math.PI;
    }
    if (dOrientation > Math.PI / 2) {
      dOrientation -= Math.PI;
    }
    this.setSatisfaction(100 - (int) (100.0 * Math.abs(dOrientation)
        / AgentSpecifications.ORIENTATION_BUILDING_POINT_SATISFACTION));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();

    // propose nothing if current orientation has no meaning
    if (this.orientationCourante == 999.9) {
      return null;
    }

    // propose rotation
    double dOrientation = this.orientationCourante
        - ((IMicroAgentGeneralisation) this.getAgent())
            .getInitialGeneralOrientation();
    if (dOrientation < -Math.PI / 2) {
      dOrientation += Math.PI;
    }
    if (dOrientation > Math.PI / 2) {
      dOrientation -= Math.PI;
    }
    Action actionToPropose = new RotationAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 2.0, -dOrientation);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

    return actionProposals;
  }

}
