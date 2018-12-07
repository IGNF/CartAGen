/**
* 
*/
package fr.ign.cogit.cartagen.agents.core.constraint.building;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.DeletionAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.EnlargementAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SmallestSurroundingRectangleAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * @author JGaffuri
 * 
 */
public class Size extends GeographicObjectConstraintImpl {
  /**
   */
  private double currentArea;
  /**
   */
  private double goalArea;

  /**
   * Getter for goalArea.
   * 
   * @return
   * @author AMaudet
   */
  public double getGoalArea() {
    return this.goalArea;
  }

  public Size(GeographicAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    if (this.getAgent().isDeleted()) {
      this.currentArea = 0.0;
    } else if (this.getAgent().getGeom() == null) {
      this.currentArea = 0.0;
    } else {
      this.currentArea = this.getAgent().getGeom().area();
    }
  }

  @Override
  public void computeGoalValue() {
    if (this.getAgent().isDeleted()) {
      this.goalArea = 0.0;
    } else {
      double area = this.getAgent().getGeom().area();

      // under the specification value AIRE_SEUIL_SUPPRESSION_BATIMENT, the goal
      // is deletion
      if (area < GeneralisationSpecifications.BUILDING_ELIMINATION_AREA_THRESHOLD) {
        this.goalArea = 0.0;
      } else {
        double areaMin = GeneralisationSpecifications.BUILDING_MIN_AREA
            * Legend.getSYMBOLISATI0N_SCALE() * Legend.getSYMBOLISATI0N_SCALE()
            / 1000000.0;
        if (area > areaMin) {
          this.goalArea = area;
        } else {
          this.goalArea = areaMin;
        }
      }
    }
  }

  @Override
  public void computePriority() {
    this.setPriority(10);
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgent().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }

    this.computeCurrentValue();
    this.computeGoalValue();

    // le batiment est trop petit et doit etre supprime
    if (this.goalArea == 0.0) {
      this.setSatisfaction(0);
    } else if (this.goalArea != 0.0 && this.currentArea >= this.goalArea) {
      this.setSatisfaction(100);
    } else {
      this.setSatisfaction(100 - (int) (100
          * Math.abs(this.currentArea - this.goalArea) / this.goalArea));
    }
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // Propose elimination if target area = 0
    if (this.goalArea == 0.0) {
      actionToPropose = new DeletionAction(this.getAgent(), this, 1.0);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    }

    // Else propose enlargement and
    // "enlarge while reshaping to smallest surrounding rectangle"
    else {
      actionToPropose = new EnlargementAction(
          (IMicroAgentGeneralisation) this.getAgent(), this, 2.0,
          this.goalArea);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

      actionToPropose = new SmallestSurroundingRectangleAction(
          (IMicroAgentGeneralisation) this.getAgent(), this, 1.0,
          this.goalArea);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    }

    return actionProposals;
  }

}
