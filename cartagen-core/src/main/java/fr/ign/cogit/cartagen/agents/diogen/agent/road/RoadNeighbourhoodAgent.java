package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.diogen.algorithms.Projections;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class RoadNeighbourhoodAgent extends CartAComAgentGeneralisation
    implements ICartAComAgentGeneralisation, IDiogenAgent {

  private final static double DISTANCE_MIN = 1;

  private boolean leftSide;

  private Map<BuildingAgent, LocalCoordinates> agentsInNeighbourhood = new HashMap<>();

  public RoadNeighbourhoodAgent(IRoadSectionAgent roadAgent, boolean leftSide) {
    super(roadAgent.getFeature());
    this.setLeftSide(leftSide);
    Environment env = new Environment();
    env.changeEnvironmentTypeToRoadNeighbourhood();
    env.setHostAgent(this);
  }

  public void addAgentsInNeighbourhood(BuildingAgent agent) {

    // compute coordinates in the neighbourhood environment ref
    DirectPositionList positions = (DirectPositionList) ((ILineString) (this
        .getFeature().getGeom())).coord().clone();

    int i = Projections.projectAndInsertWithPositionOutside(
        agent.getFeature().getGeom().centroid(), positions.getList());
    ILineString lineString = new GM_LineString(positions);

    // System.out.println("Positions: " + i);

    double curvAbsc;
    if (i == 0) {
      curvAbsc = -positions.get(0).toGM_Point()
          .distance(positions.get(1).toGM_Point());
    } else {
      curvAbsc = Operateurs.abscisseCurviligne(lineString, i);
    }

    // System.out.println("Absc. curviligne: " + curvAbsc);

    // get the symbol of the road without route
    IPolygon polygon = (IPolygon) SectionSymbol
        .getSymbolExtent((INetworkSection) this.getFeature());

    double distanceFromRoad = (polygon.intersects(agent.getFeature().getGeom()))
        ? DISTANCE_MIN
        : Distances.distance(agent.getFeature().getGeom().centroid(), polygon);

    agentsInNeighbourhood.put(agent,
        new LocalCoordinates(curvAbsc, distanceFromRoad));

    this.getEncapsulatedEnv().addContainedAgents((IDiogenAgent) agent);

    // Store the agent and its coordinates.
  }

  public Set<BuildingAgent> getBuildingAgents() {
    return agentsInNeighbourhood.keySet();
  }

  public boolean isLeftSide() {
    return leftSide;
  }

  public void setLeftSide(boolean leftSide) {
    this.leftSide = leftSide;
  }

  public double getBuildingNormedCurvAbsc(IBuildingAgent agent) {
    LocalCoordinates coord = agentsInNeighbourhood.get(agent);
    if (coord == null) {
      return 0.0;
    } else {
      return coord.getCurvAbsc() / this.getFeature().getGeom().length();
    }
  }

  public double getBuildingDistanceFromRoad(IBuildingAgent agent) {
    LocalCoordinates coord = agentsInNeighbourhood.get(agent);
    if (coord == null) {
      return 0.0;
    } else {
      return coord.getDistanceFromRoad();
    }
  }

  public static RoadNeighbourhoodAgent getNeighbourhoodAgent(IRoadLine road,
      boolean left) {

    if (road.getGeneArtifacts() == null) {
      return null;
    }
    RoadNeighbourhoodAgent agent = null;
    for (Object obj : road.getGeneArtifacts()) {
      if (obj instanceof RoadNeighbourhoodAgent) {
        agent = (RoadNeighbourhoodAgent) obj;
        if (agent.isLeftSide() == left) {
          break;
        }
      }
    }
    return agent;
  }

  public class LocalCoordinates {

    private double curvAbsc;
    private double distanceFromRoad;
    private double distanceFromProj;

    public LocalCoordinates(double curvAbsc, double distanceFromRoad) {
      this.setCurvAbsc(curvAbsc);
      this.setDistanceFromRoad(distanceFromRoad);
      this.setDistanceFromProj(0.);
    }

    public LocalCoordinates(double curvAbsc, double distanceFromRoad,
        double distanceFromProj) {
      this.setCurvAbsc(curvAbsc);
      this.setDistanceFromRoad(distanceFromRoad);
      this.setDistanceFromProj(distanceFromProj);
    }

    public double getCurvAbsc() {
      return curvAbsc;
    }

    public void setCurvAbsc(double curvAbsc) {
      this.curvAbsc = curvAbsc;
    }

    public double getDistanceFromRoad() {
      return distanceFromRoad;
    }

    public void setDistanceFromRoad(double distanceFromRoad) {
      this.distanceFromRoad = distanceFromRoad;
    }

    public double getDistanceFromProj() {
      return distanceFromProj;
    }

    public void setDistanceFromProj(double distanceFromProj) {
      this.distanceFromProj = distanceFromProj;
    }
  }

  @Override
  public Set<Environment> getBorderedEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public BorderStrategy getBorderStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Environment getEncapsulatedEnv() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getContainingEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    // TODO Auto-generated method stub

  }

}
