/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.building;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.micro.SimplificationAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SmallestSurroundingRectangleAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SquarringAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;
import fr.ign.cogit.geoxygene.generalisation.simplification.PolygonSegment;

/**
 * @author JGaffuri
 * 
 */
public class Granularity extends GeographicObjectConstraintImpl {

  // the granularity, within 0 and 1
  /**
   */
  private double granularity;

  public Granularity(GeographicAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    // retrieves the treshold in m
    double treshold = GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;

    // retrieves the polygon
    IPolygon poly = (IPolygon) this.getAgent().getGeom();

    // the list of edges shorter than the treshold
    ArrayList<PolygonSegment> tooSmallEdges = PolygonSegment.getSmallest(poly,
        treshold);

    // computes the mean of the length deficit of too short edges
    double meanLengthDeficit = 0;
    for (PolygonSegment cls : tooSmallEdges) {
      meanLengthDeficit += treshold - cls.segment.length;
    }
    if (tooSmallEdges.size() != 0) {
      meanLengthDeficit = meanLengthDeficit / tooSmallEdges.size();
    }

    // computes the number of edges of the polygon
    double nb = poly.coord().size() - 1 - (poly.getInterior().size());

    // computes the ratio of too short edges
    double tooShortEdgesRatio = tooSmallEdges.size() / nb;

    // a mix which take into account the tooShortEdgesRatio and the
    // meanLengthDeficit.
    this.granularity = Math
        .sqrt(tooShortEdgesRatio * meanLengthDeficit / treshold);
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    // must be smaller than the size constraint priority
    this.setPriority(8);
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgent().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }

    this.computeCurrentValue();

    if (this.granularity == 0) {
      this.setSatisfaction(100);
      return;
    }
    this.setSatisfaction(100 - (int) (100 * this.granularity));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // simplification
    actionToPropose = new SimplificationAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 5.0,
        GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE
            * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 5.0));

    // equarrissage
    actionToPropose = new SquarringAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 2.0,
        GeneralisationSpecifications.TOLERANCE_ANGLE, 500);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

    // PPRE
    actionToPropose = new SmallestSurroundingRectangleAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 1.0,
        this.getAgent().getGeom().area());
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));

    return actionProposals;
  }

}
