package fr.ign.cogit.cartagen.agents.diogen.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class LinearEnvironment extends Environment {

  private Map<IAgent, EncapsulatedAgent> containedAgentsMap = new HashMap<>();

  public void addContainedAgentsWithCoordinate(IDiogenAgent containedAgent,
      double coord) {
    this.containedAgentsMap.put(containedAgent,
        new EncapsulatedAgent(containedAgent, coord));
    super.addContainedAgents(containedAgent);
    // containedAgent.addContainingEnvironments(this);
    this.updateAbsolutePosition(containedAgent);
  }

  public void removeContainedAgents(IDiogenAgent containedAgent) {
    containedAgentsMap.remove(containedAgentsMap.get(containedAgent));
    super.removeContainedAgents(containedAgent);
  }

  public double getAgentCoordinate(IAgent agent) {
    // Double toReturn = containedAgents.get(agent);
    if (agent == null) {
      return Double.NaN;
    }
    return containedAgentsMap.get(agent).getD();
  }

  public void updateAbsolutePosition(IAgent agent) {
    if (!(agent instanceof IPointAgent)
        || !(this.getHostAgent() instanceof SegmentSubmicroAgent)) {
      return;
    }
    SegmentSubmicroAgent segmentAgent = (SegmentSubmicroAgent) this
        .getHostAgent();

    IPointAgent pointAgent1 = segmentAgent.getP1();
    IPointAgent pointAgent2 = segmentAgent.getP2();
    IPointAgent pointAgentM = (IPointAgent) agent;
    //
    ILineString lineString = new GM_LineString(pointAgent1.getPosition(),
        pointAgent2.getPosition());

    IDirectPosition updatedPosition = Operateurs
        .pointEnAbscisseCurviligne(lineString, getAgentCoordinate(pointAgentM)
            * pointAgent1.getPosition().distance(pointAgent2.getPosition()));
    pointAgentM.updatePosition(updatedPosition);
  }

  public void updateAllPositions() {
    for (IAgent agent : this.getContainedAgents()) {
      updateAbsolutePosition(agent);
    }
  }

  public List<IAgent> getOrderedPointAgents() {
    List<EncapsulatedAgent> encapsulatedAgents = new ArrayList<>(
        containedAgentsMap.values());
    // System.out.println("number of points supported by the segment "
    // + containedAgentsMap.values().size());
    Collections.sort(encapsulatedAgents, new LocalComparator());
    List<IAgent> toReturn = new ArrayList<>();
    for (EncapsulatedAgent e : encapsulatedAgents) {
      toReturn.add(e.getAgent());
    }
    return toReturn;
  }

  public void removeAllContainedAgents() {
    for (IDiogenAgent a : this.getContainedAgents()) {
      this.removeContainedAgents(a);
    }
  }

  private class EncapsulatedAgent {

    private IAgent agent;
    private Double d;

    public EncapsulatedAgent(IAgent agent, Double d) {
      this.agent = agent;
      this.d = d;
    }

    public Double getD() {
      return d;
    }

    public IAgent getAgent() {
      return agent;
    }
  }

  private class LocalComparator implements Comparator<EncapsulatedAgent> {

    @Override
    public int compare(EncapsulatedAgent o1, EncapsulatedAgent o2) {
      // System.out.println(o1 + " : " + o1.getD() + " ; " + o2 + " : "
      // + o2.getD());
      // System.out.println("Comparison = " + (int) (o1.getD() - o2.getD()));
      return (int) Math.signum(o1.getD() - o2.getD());
    }

  }
}
