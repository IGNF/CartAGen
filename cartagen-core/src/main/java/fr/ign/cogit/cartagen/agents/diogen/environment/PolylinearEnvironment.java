package fr.ign.cogit.cartagen.agents.diogen.environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class PolylinearEnvironment extends Environment {

  private Map<IDirectPosition, LocalPointAgent> containedPointAgents = new HashMap<>();

  private Map<CouplePoint, SegmentSubmicroAgent> containedSegmentAgents = new HashMap<>();

  public void addContainedPointAgents(IDiogenAgent containedAgent) {
    super.addContainedAgents(containedAgent);
    containedPointAgents.put(
        (IDirectPosition) ((IPointAgent) containedAgent).getPosition().clone(),
        new LocalPointAgent(((IPointAgent) containedAgent).getPosition(),
            (IPointAgent) containedAgent));
    // System.out.println("Add point agent " + containedAgent + " at position "
    // + containedAgent.getPosition().clone());
  }

  public void removeContainedAgents(IDiogenAgent containedAgent) {
    // System.out.println("Remove agent " + containedAgent);
    super.removeContainedAgents(containedAgent);
    // IPointAgent pointAgent = containedPointAgents.remove(containedAgent
    // .getPosition());
    // if (pointAgent == null) {
    for (IDirectPosition p : containedPointAgents.keySet()) {
      if (containedPointAgents.get(p).agent == containedAgent) {
        System.out.println("Remove point agent " + containedPointAgents.get(p)
            + " with position " + p);
        containedPointAgents.remove(p);
        return;
      }
      // }
    }
    // System.out.println("Remove point agent with its position " + pointAgent);
  }

  public void removeAllContainedAgents() {
    for (IDiogenAgent containedAgent : this.getContainedAgents()) {
      this.removeContainedAgents(containedAgent);
    }
  }

  /**
   * Update the position of the agent point inside the environment
   * @param agent
   */
  public void updateLocalPosition(IPointAgent agent) {
    // System.out.println("Update agent " + agent);
    for (IDirectPosition p : containedPointAgents.keySet()) {
      if (containedPointAgents.get(p).agent == agent) {
        containedPointAgents.get(p).currentPosition = agent.getPosition();
        return;
      }
      // }
    }
  }

  public IPointAgent getPointAgentWithInitialPosition(IDirectPosition p) {
    LocalPointAgent toReturn = containedPointAgents.get(p);
    if (toReturn == null) {
      return null;
    }
    return toReturn.agent;
  }

  public Set<IPointAgent> getPointAgentsWithCurrentPosition(
      IDirectPosition pos) {
    Set<IPointAgent> toReturn = new HashSet<>();
    for (IDirectPosition p : containedPointAgents.keySet()) {
      if (containedPointAgents.get(p).currentPosition.equals(pos)) {
        toReturn.add(containedPointAgents.get(p).agent);
      }
    }
    return toReturn;
  }

  public IPointAgent getEdgePointAgentWithCurrentPosition(IDirectPosition pos) {
    for (IDirectPosition p : containedPointAgents.keySet()) {
      if (containedPointAgents.get(p).currentPosition.equals(pos)
          && containedPointAgents.get(p).edge) {
        return containedPointAgents.get(p).agent;
      }
    }
    return null;
  }

  public void addContainedAgents(SegmentSubmicroAgent containedAgent) {
    super.addContainedAgents(containedAgent);
    this.containedSegmentAgents.put(
        new CouplePoint(containedAgent.getP1(), containedAgent.getP2()),
        containedAgent);
  }

  public void addContainedAgents(SegmentSubmicroAgent containedAgent,
      boolean direction) {
    super.addContainedAgents(containedAgent);
    this.containedSegmentAgents.put(new CouplePoint(containedAgent.getP1(),
        containedAgent.getP2(), direction), containedAgent);
  }

  public void setIsEdge(IPointAgent a, boolean b) {
    for (LocalPointAgent lp : containedPointAgents.values()) {
      if (lp.agent == a) {
        lp.edge = b;
        return;
      }
    }
  }

  public void removeContainedAgent(SegmentSubmicroAgent containedAgent) {
    super.removeContainedAgents(containedAgent);
    SegmentSubmicroAgent agent = containedSegmentAgents.remove(
        new CouplePoint(containedAgent.getP1(), containedAgent.getP2()));
    if (agent == null) {
      for (CouplePoint c : containedSegmentAgents.keySet()) {
        if (containedSegmentAgents.get(c) == containedAgent) {
          containedPointAgents.remove(c);
          return;
        }
      }
    }
  }

  public SegmentSubmicroAgent getSegmentAgentWithExtremities(IPointAgent begin,
      IPointAgent end) {
    CouplePoint cp = new CouplePoint(begin, end);
    // System.out.println(cp);
    // System.out.println(containedSegmentAgents);
    return this.containedSegmentAgents.get(cp);
  }

  public Collection<SegmentSubmicroAgent> getAllSegmentAgents() {
    return this.containedSegmentAgents.values();
  }

  public SegmentSubmicroAgent getNextSegment(
      SegmentSubmicroAgent segmentAgent) {
    boolean direction = true;
    for (CouplePoint c : this.containedSegmentAgents.keySet()) {
      SegmentSubmicroAgent s = this.containedSegmentAgents.get(c);
      if (s == segmentAgent) {
        // System.out.println("segment identified " + s + " with direction "
        // + direction);
        direction = c.direction;
        break;
      }
    }
    for (CouplePoint c : this.containedSegmentAgents.keySet()) {
      SegmentSubmicroAgent s = this.containedSegmentAgents.get(c);
      if (s != segmentAgent) {
        // System.out.println("Test for " + s + " with direction " +
        // c.direction);
        if (direction) {
          if (c.direction && segmentAgent.getP2() == s.getP1()) {
            return s;
          } else if (!c.direction && segmentAgent.getP2() == s.getP2()) {
            return s;
          }
        } else {
          if (c.direction && segmentAgent.getP1() == s.getP1()) {
            return s;
          } else if (!c.direction && segmentAgent.getP1() == s.getP2()) {
            return s;
          }
        }
      }
    }

    // System.out.println("Warning, no segment after " + segmentAgent + " P1= "
    // + segmentAgent.getP1() + " P2= " + segmentAgent.getP2()
    // + " , orientation= " + direction);

    return null;
  }

  public boolean getSegmentRelativeDirection(
      SegmentSubmicroAgent segmentAgent) {
    for (CouplePoint c : this.containedSegmentAgents.keySet()) {
      SegmentSubmicroAgent s = this.containedSegmentAgents.get(c);
      if (s == segmentAgent) {
        return c.direction;
      }
    }
    return true;
  }

  public Collection<IPointAgent> getAllPointAgents() {
    Collection<IPointAgent> toReturn = new HashSet<>();
    for (LocalPointAgent l : this.containedPointAgents.values()) {
      toReturn.add(l.agent);
    }
    return toReturn;
  }

  public SegmentSubmicroAgent getSegmentBeginingBy(IPointAgent p) {
    for (SegmentSubmicroAgent s : containedSegmentAgents.values()) {
      if (s.getP1() == p) {
        return s;
      }
    }
    return null;
  }

  public boolean arePointAgentsAtSamePosition(IPointAgent p1, IPointAgent p2) {
    IDirectPosition p1Position = null;
    for (LocalPointAgent l : this.containedPointAgents.values()) {
      if (l.agent == p1) {
        p1Position = l.currentPosition;
        break;
      }
    }
    if (p1Position == null) {
      return false;
    }
    for (IPointAgent p : getPointAgentsWithCurrentPosition(p1Position)) {
      if (p == p2) {
        return true;
      }
    }
    return false;
  }

  private class CouplePoint {

    private IPointAgent begin;
    private IPointAgent end;
    public boolean direction = true;

    public IPointAgent getBegin() {
      return begin;
    }

    public IPointAgent getEnd() {
      return end;
    }

    public CouplePoint(IPointAgent begin, IPointAgent end) {
      this.begin = begin;
      this.end = end;
    }

    public CouplePoint(IPointAgent begin, IPointAgent end, boolean direction) {
      this.begin = begin;
      this.end = end;
      this.direction = direction;
    }

    public boolean equals(Object o) {
      if (o == null) {
        return false;
      } else if (!(o instanceof CouplePoint)) {
        return false;
      } else {
        CouplePoint c = (CouplePoint) o;

        boolean res = this.getBegin().equals(c.getBegin())
            && this.getEnd().equals(c.getEnd());
        res = res || this.getBegin().equals(c.getEnd())
            && this.getEnd().equals(c.getBegin());
        return res;
      }
    }

    public String toString() {
      String toReturn = "Couple : " + this.begin + " and " + this.end;
      return toReturn;
    }

    public int hashCode() {
      // System.out.println(this.begin.hashCode() + this.end.hashCode());
      return this.begin.hashCode() + this.end.hashCode();
    }
  }

  private class LocalPointAgent {

    IDirectPosition currentPosition;

    IPointAgent agent;

    boolean edge = true;

    LocalPointAgent(IDirectPosition currentPosition, IPointAgent agent) {
      this.currentPosition = currentPosition;
      this.agent = agent;
    }

  }

}
