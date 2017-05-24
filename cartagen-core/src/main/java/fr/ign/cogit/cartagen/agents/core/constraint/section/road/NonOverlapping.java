package fr.ign.cogit.cartagen.agents.core.constraint.section.road;

import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class NonOverlapping extends GeographicObjectConstraintImpl {

  @SuppressWarnings("unused")
  private static Logger logger = Logger
      .getLogger(NonOverlapping.class.getName());

  /**
   */
  boolean isOverlappingRoad;

  public NonOverlapping(GeographicObjectAgentGeneralisation agent,
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
    if (this.isOverlappingRoad) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(100.0);
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computeCurrentValue() {
    IRoadSectionAgent roadAgent = ((IRoadSectionAgent) this.getAgent());
    ILineString geom = roadAgent.getGeom();
    for (ISectionAgent otherRoad : roadAgent.getNetwork().getComponents()) {
      if (otherRoad.equals(roadAgent)) {
        continue;
      }
      if (otherRoad.getGeom().intersects(geom)) {
        this.isOverlappingRoad = true;
        return;
      }
    }
    IPolygon poly = new GM_Polygon(geom);
    if (!poly.isValid()) {
      this.isOverlappingRoad = true;
      return;
    }
    this.isOverlappingRoad = false;
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
