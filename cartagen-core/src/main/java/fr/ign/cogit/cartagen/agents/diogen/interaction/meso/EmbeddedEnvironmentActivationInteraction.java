package fr.ign.cogit.cartagen.agents.diogen.interaction.meso;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.EmbeddedEnvironmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.algorithms.FindConnectedLine;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.schema.IEmbeddedDeadEndArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class EmbeddedEnvironmentActivationInteraction
    extends ConstrainedDegenerateInteraction {

  private static Logger logger = Logger
      .getLogger(EmbeddedEnvironmentActivationInteraction.class.getName());

  /**
   * The singleton object for the unique instance of the class.
   */
  private static EmbeddedEnvironmentActivationInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private EmbeddedEnvironmentActivationInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized EmbeddedEnvironmentActivationInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new EmbeddedEnvironmentActivationInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    // select the embedded area for dead end
    SortedSet<EmbeddedEnvironmentAgent> agentToActivate = new TreeSet<EmbeddedEnvironmentAgent>(
        new DeadEndPositionComparator());
    for (IAgent a : source.getEncapsulatedEnv().getContainedAgents()) {
      if (a instanceof EmbeddedEnvironmentAgent)
        agentToActivate.add((EmbeddedEnvironmentAgent) a);
    }

    for (IAgent a : agentToActivate) {
      a.activate();
    }

  }

  public class DeadEndPositionComparator
      implements Comparator<EmbeddedEnvironmentAgent> {

    @Override
    public int compare(EmbeddedEnvironmentAgent o1,
        EmbeddedEnvironmentAgent o2) {
      IEmbeddedDeadEndArea edea1 = (IEmbeddedDeadEndArea) o1.getFeature();
      IEmbeddedDeadEndArea edea2 = (IEmbeddedDeadEndArea) o2.getFeature();

      double d1 = getDistanceToSurroundingNetwork(edea1);
      double d2 = getDistanceToSurroundingNetwork(edea2);

      if (d1 > d2)
        return 1;
      else if (d1 < d2)
        return -1;
      else
        return 0;
    }

    public double getDistanceToSurroundingNetwork(IEmbeddedDeadEndArea edea) {

      double toReturn = Double.MAX_VALUE;

      INetworkNode node = edea.getDeadEnd().getRootNode();

      INetworkSection rootSection = edea.getDeadEnd().getRoot();

      // choose the good end of the section
      if (!(edea.getRootDirectPosition()
          .distance(edea.getDeadEnd().getRoot().getGeom().startPoint()) > edea
              .getRootDirectPosition()
              .distance(edea.getDeadEnd().getRoot().getGeom().endPoint()))) {
        try {
          rootSection = (INetworkSection) rootSection.cloneGeom();
          rootSection.setGeom(rootSection.getGeom().reverse());
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
      }

      FindConnectedLine algoL = new FindConnectedLine(rootSection, true,
          edea.getConnectedNetwork());

      algoL.compute();

      INetworkSection leftRoad = algoL.getConnectedSection();

      INetworkNode otherNode = null;
      if (node.getPosition().equals2D(leftRoad.getInitialNode().getPosition()))
        otherNode = leftRoad.getFinalNode();
      else
        otherNode = leftRoad.getInitialNode();
      for (INetworkSection section : otherNode.getInSections()) {
        if (edea.getBlock().getSurroundingNetwork().contains(section)) {
          toReturn = Math.min(toReturn,
              otherNode.getPosition().distance2D(node.getPosition()));
          break;
        }
      }
      for (INetworkSection section : otherNode.getOutSections()) {
        if (edea.getBlock().getSurroundingNetwork().contains(section)) {
          toReturn = Math.min(toReturn,
              otherNode.getPosition().distance2D(node.getPosition()));
          break;
        }
      }

      FindConnectedLine algoR = new FindConnectedLine(rootSection, false,
          edea.getConnectedNetwork());

      algoR.compute();

      INetworkSection rightRoad = algoR.getConnectedSection();

      if (node.getPosition().equals2D(rightRoad.getInitialNode().getPosition()))
        otherNode = rightRoad.getFinalNode();
      else
        otherNode = rightRoad.getInitialNode();
      for (INetworkSection section : otherNode.getInSections()) {
        if (edea.getBlock().getSurroundingNetwork().contains(section)) {
          toReturn = Math.min(toReturn,
              otherNode.getPosition().distance2D(node.getPosition()));
          break;
        }
      }
      for (INetworkSection section : otherNode.getOutSections()) {
        if (edea.getBlock().getSurroundingNetwork().contains(section)) {
          toReturn = Math.min(toReturn,
              otherNode.getPosition().distance2D(node.getPosition()));
          break;
        }
      }

      logger.debug(
          "The edea " + edea + " is situated to a distance of " + toReturn);
      return toReturn;

    }
  }
}
