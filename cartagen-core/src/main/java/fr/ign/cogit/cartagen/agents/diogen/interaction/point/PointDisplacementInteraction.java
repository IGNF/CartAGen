package fr.ign.cogit.cartagen.agents.diogen.interaction.point;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.constraint.points.ForPointDisplacementConstraint;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregatedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsAggregatedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class PointDisplacementInteraction
    extends ConstrainedMultipleTargetsAggregatedInteraction
    implements AggregatedInteraction {

  private static Logger LOGGER = Logger
      .getLogger(PointDisplacementInteraction.class.getName());

  /**
   * The singleton object for the unique instance of the class.
   */
  private static PointDisplacementInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private PointDisplacementInteraction() {
    super();
    this.setWeight(2);
    this.setName("Point Displacement Aggregated");
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized PointDisplacementInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new PointDisplacementInteraction();
    }
    return singletonObject;
  }

  @Override
  public void aggregateInteraction(
      AggregableInteraction aggregableInteraction) {
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<? extends IDiogenAgent> targets,
      Set<GeographicConstraint> constraintsToSatisfy,
      Set<GeographicConstraint> triggeringConstraints,
      Set<GeographicConstraint> requestLinkedConstraints)
      throws InterruptedException, ClassNotFoundException {

    IPointAgent pt = (IPointAgent) source;

    // pt.updateForces();
    // System.out.println("targets size " + targets.size());

    pt.setActionsToTry(new HashSet<ActionProposal>());

    double importancesSum = 0.0;
    for (GeographicConstraint c : constraintsToSatisfy) {
      importancesSum += c.getImportance();
    }

    for (GeographicConstraint c : constraintsToSatisfy) {
      // System.out.println("GeographicConstraint " + c);
      if (c instanceof ForPointDisplacementConstraint) {
        // System.out.println("ForPointDisplacementConstraint " + c);
        ((ForPointDisplacementConstraint) c).proposeDisplacement(pt,
            c.getImportance() / importancesSum);
      } else if (c instanceof SubmicroConstraint) {
        ((SubmicroConstraint) c).proposeDisplacement(pt,
            c.getImportance() / importancesSum);
      }
    }

    // calcule somme
    double dx = 0.0, dy = 0.0;
    for (ActionProposal actionProposal : pt.getActionProposals()) {
      PointAgentDisplacementAction dep = (PointAgentDisplacementAction) actionProposal
          .getAction();
      // System.out.println(dep);
      // System.out.println(actionProposal);
      if (dep != null) {
        LOGGER.debug("Displacement proposed by constraint "
            + actionProposal.getHandledConstraint() + " with satisfaction "
            + ((GeographicConstraint) actionProposal.getHandledConstraint())
                .getSatisfaction());
        LOGGER.debug("Add forces dx: " + dep.getDx() + ", dy: " + dep.getDy());
        dx += dep.getDx();
        dy += dep.getDy();
      }
    }

    // IDirectPosition position = pt.getPosition();

    IAgent host = environment.getHostAgent();

    // System.out.println("Host " + host);
    if (!(host instanceof GAELDeformable)) {
      return;
    }

    IGeneObj f = (IGeneObj) ((ICartacomAgent) host).getFeature();
    IDirectPositionList newPositionList = new DirectPositionList();

    // System.out.println("Search point to apply translation");
    for (IDirectPosition p : f.getGeom().coord()) {
      if (p.equals(pt.getPosition())) {
        LOGGER
            .debug("Apply translation on " + p + " of dx=" + dx + " dy=" + dy);
        pt.setX(pt.getX() + dx);
        pt.setY(pt.getY() + dy);
        pt.getFeature().setGeom(pt.getPosition().toGM_Point());
        newPositionList.add(pt.getPosition());
      } else {
        newPositionList.add(p);
      }
    }

    if (f.getGeom() instanceof ILineString) {
      f.setGeom(new GM_LineString(newPositionList));
    }

  }
}
