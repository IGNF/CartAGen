package fr.ign.cogit.cartagen.agents.cartacom.relation.buildingroad;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.BuildingOrientation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.RoadOrientation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadSectionAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IGenericCurve;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class Parallelism extends MicroMicroRelation {

  /**
   * Logger for this class
   */
  private static Logger LOGGER = LogManager.getLogger(Parallelism.class.getName());

  /**
   * @param ag1
   * @param ag2
   */
  public Parallelism(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2, double importance) {
    super(ag1, ag2, importance);
    LOGGER.debug("Instanciation");
    if (ag1 instanceof ISmallCompactAgent && ag2 instanceof RoadSectionAgent) {
      // FIXME on ne peut pas passer geomConstrainedZone par parametre, il est
      // calculé dans constraint
      // et il utilise lautre agent pour calcul
      MicroMicroRelationalConstraint constr1 = new BuildingOrientation(ag1,
          this, this.getImportance());
      MicroMicroRelationalConstraint constr2 = new RoadOrientation(ag2, this,
          this.getImportance());
      this.setConstraint1(constr1);
      this.setConstraint2(constr2);
    } else {
      // error
    }
  }

  private double currentValue;
  private double goalValue;
  private double initialValue;
  private double flexibility;

  public static boolean checkRelationRelevance(ICartAComAgentGeneralisation ag1,
      ICartAComAgentGeneralisation ag2) {

    // LOGGER.debug("checkRelationRelevance between " + ag1.getFeature() +
    // " and "
    // + ag2.getFeature());

    // Cast agents
    ISmallCompactAgent comAg = (ISmallCompactAgent) ag1;
    INetworkSectionAgent netAg = (INetworkSectionAgent) ag2;
    boolean result = false;

    // Check if the network face in which the
    // small compact agent exists is a border to road section agent
    Set<NetworkFaceAgent> leftBordereds = netAg.getLeftBorderedFaces();
    for (NetworkFaceAgent networkFaceAgent : leftBordereds) {
      if (networkFaceAgent.getContainedSmallCompacts().contains(comAg)) {
        result = true;
        break;
      }
    }
    if (result == false) {
      Set<NetworkFaceAgent> rightBordereds = netAg.getRightBorderingFaces();
      for (NetworkFaceAgent networkFaceAgent : rightBordereds) {
        if (networkFaceAgent.getContainedSmallCompacts().contains(comAg)) {
          result = true;
          break;
        }

      }
    }

    if (!result) {
      return false;
    }

    LOGGER.debug("Relation between building " + comAg + " and road " + netAg);

    // Verify distance
    double distance = netAg.getGeom().distance(comAg.getGeom())
        - netAg.getFeature().getWidth() / 2;
    LOGGER.debug("Distance = " + distance);
    if (distance > CartacomSpecifications.DIST_SMALLCOMPACT_CLOSE_TO_NETWORK_SECTION) {
      return false;
    }

    // Verify biscornuite
    // FIXME verify this method
    String biscornuite;
    try {
      biscornuite = MesureOrientationV2
          .getBiscornuite(JtsGeOxygene.makeJtsGeom(comAg.getGeom()));
      LOGGER.debug("biscornuite = " + biscornuite);
      if (biscornuite.equals("aucune_orientation")) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Check orientation relevance
    IGenericCurve roadGeometry = ((IGenericCurve) netAg.getGeom());
    double orientationRoad = Math.atan((roadGeometry.startPoint().getY()
        - roadGeometry.endPoint().getY())
        / (roadGeometry.startPoint().getX() - roadGeometry.endPoint().getX()));
    double orientationBuilding = 0;
    try {
      orientationBuilding = MesureOrientationV2
          .getGeneralOrientation((JtsGeOxygene.makeJtsGeom(comAg.getGeom())));
    } catch (Exception e) {
      e.printStackTrace();
    }
    double gapRad = Math.abs(orientationRoad - orientationBuilding)
        % (Math.PI / 2);

    double gap = Math.toDegrees(gapRad);
    LOGGER.debug("gap = " + gap);
    if (gap < CartacomSpecifications.PARALLELISM_RELATION_GOAL0) {
      return true;
    }
    return false;

  }

  @Override
  public void computeCurrentValue() {
    // Cast agents
    ISmallCompactAgent comAg = (ISmallCompactAgent) this.getAgentGeo1();
    INetworkSectionAgent netAg = (INetworkSectionAgent) this.getAgentGeo2();

    // Road geometry
    IGenericCurve roadGeometry = ((IGenericCurve) netAg.getGeom());

    double orientationRoad = Math.atan((roadGeometry.startPoint().getY()
        - roadGeometry.endPoint().getY())
        / (roadGeometry.startPoint().getX() - roadGeometry.endPoint().getX()));
    double orientationBuilding = 0;
    try {
      orientationBuilding = MesureOrientationV2
          .getGeneralOrientation((JtsGeOxygene.makeJtsGeom(comAg.getGeom())));
    } catch (Exception e) {
      e.printStackTrace();
    }

    LOGGER.debug("Compute current Value : orientationRoad=" + orientationRoad
        + " , orientationBuilding=" + orientationBuilding);

    this.currentValue = orientationRoad - orientationBuilding;

  }

  @Override
  public void computeGoalValue() {
    this.goalValue = CartacomSpecifications.PARALLELISM_RELATION_GOAL_VALUE;
  }

  @Override
  public void computeInitialValue() {
    // Cast agents
    ISmallCompactAgent comAg = (ISmallCompactAgent) this.getAgentGeo1();
    INetworkSectionAgent netAg = (INetworkSectionAgent) this.getAgentGeo2();

    // Road geometry
    IGenericCurve roadGeometry = ((IGenericCurve) netAg.getGeom());
    double orientationRoad = Math.atan((roadGeometry.startPoint().getY()
        - roadGeometry.endPoint().getY())
        / (roadGeometry.startPoint().getX() - roadGeometry.endPoint().getX()));
    double orientationBuilding = 0;
    try {
      orientationBuilding = MesureOrientationV2
          .getGeneralOrientation((JtsGeOxygene.makeJtsGeom(comAg.getGeom())));
    } catch (Exception e) {
      e.printStackTrace();
    }

    LOGGER.debug("Compute initial Value : orientationRoad=" + orientationRoad
        + " , orientationBuilding=" + orientationBuilding);

    this.initialValue = orientationRoad - orientationBuilding;
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    this.computeGoalValue();
    this.flexibility = CartacomSpecifications.PARALLELISM_RELATION_FLEXIBILITY;

    // double curValRad = Math.toRadians(this.currentValue);
    // double goalValRad = Math.toRadians(this.goalValue);

    // double gapRad = (curValRad - goalValRad) % (Math.PI / 2);
    double gapRad = (this.currentValue - this.goalValue) % (Math.PI / 2);
    double gap = Math.toDegrees(gapRad);

    double result;

    if (Math.abs(gap) <= this.flexibility) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else if (Math
        .abs(gap) > CartacomSpecifications.PARALLELISM_RELATION_GOAL0) {
      result = CartacomSpecifications.SATISFACTION_1;
    } else {
      result = CartacomSpecifications.SATISFACTION_2;
    }

    this.setSatisfaction(result);
  }

  public double getInitialValue() {
    return this.initialValue;
  }

  public double getAngle() {
    double angle = (this.currentValue - this.goalValue) % (Math.PI / 2);
    LOGGER.debug("Get angle = " + angle);
    return angle;
  }

}
