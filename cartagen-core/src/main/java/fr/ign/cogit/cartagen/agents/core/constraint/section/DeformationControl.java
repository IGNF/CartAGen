package fr.ign.cogit.cartagen.agents.core.constraint.section;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.CoalescenceConflictType;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.LineCoalescence;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

public class DeformationControl extends GeographicObjectConstraintImpl {

  @SuppressWarnings("unused")
  private static Logger logger = LogManager
      .getLogger(DeformationControl.class.getName());

  /**
   */
  double convexHullArea;

  /**
   */
  double initialConvexHullArea;

  /**
   */
  double linearExtent;

  /**
  */
  double initialLinearExtent;

  public DeformationControl(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computePriority() {
    this.setPriority(1);
  }

  @Override
  public void computeSatisfaction() {

    this.computeCurrentValue();
    double ratioConvexHull = this.convexHullArea / this.initialConvexHullArea;
    double ratioLinearExtent = this.linearExtent / this.initialLinearExtent;

    // No deformation => satisfaction perfect
    if (ratioConvexHull < 1.0 || ratioLinearExtent > 1.0) {
      this.setSatisfaction(100.0);
      return;
    }

    double satisfactionConvexHull = 0.0;
    double satisfactionLinearExtent = 0.0;

    // Check if we are in case of a signle bend or not
    boolean isSingleBend = false;
    INetworkSection section = ((ISectionAgent) this.getAgent()).getFeature();
    ILineString currentGeom = section.getGeom();
    section.setGeom(section.getInitialGeom());
    LineCoalescence coalescenceSections = new LineCoalescence(
        ((ISectionAgent) this.getAgent()).getFeature());

    coalescenceSections.compute();

    if (coalescenceSections.getSections() != null
        && coalescenceSections.getSections().size() == 1
        && (coalescenceSections.getCoalescenceTypes()
            .get(0) == CoalescenceConflictType.RIGHT
            || coalescenceSections.getCoalescenceTypes()
                .get(0) == CoalescenceConflictType.LEFT)) {
      isSingleBend = true;
    }
    section.setGeom(currentGeom);

    // Single bend: deformation possibly caused by max break
    if (isSingleBend) {
      if (ratioConvexHull > 5.0) {
        satisfactionConvexHull = 0.0;
      } else {
        satisfactionConvexHull = 25.0 * (5.0 - ratioConvexHull);
      }
      if (ratioLinearExtent > 5.0) {
        satisfactionLinearExtent = 0.0;
      } else {
        satisfactionLinearExtent = 25.0 * (5.0 - ratioLinearExtent);
      }
    }
    // Multiple bends: deformation possibly caused by accordion
    else {
      if (ratioConvexHull > 1.25) {
        satisfactionConvexHull = 0.0;
      } else {
        satisfactionConvexHull = 400.0 * (1.25 - ratioConvexHull);
      }
      if (ratioLinearExtent > 1.25) {
        satisfactionLinearExtent = 0.0;
      } else {
        satisfactionLinearExtent = 400.0 * (1.25 - ratioLinearExtent);
      }
    }

    this.setSatisfaction(
        Math.min(satisfactionConvexHull, satisfactionLinearExtent));

  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computeCurrentValue() {
    ISectionAgent agent = ((ISectionAgent) this.getAgent());
    ILineString geom = (ILineString) agent.getGeom();
    ILineString initialGeom = (ILineString) agent.getInitialGeom();
    this.convexHullArea = geom.convexHull().area();
    this.initialConvexHullArea = initialGeom.convexHull().area();
    this.linearExtent = geom.coord().get(0)
        .distance(geom.coord().get(geom.coord().size() - 1));
    this.initialLinearExtent = initialGeom.coord().get(0)
        .distance(initialGeom.coord().get(initialGeom.coord().size() - 1));
  }

  @Override
  public Set<ActionProposal> getActions() {
    // The role of this constraint is exclusively to control deformation and not
    // to propose actions
    return null;
  }

}
