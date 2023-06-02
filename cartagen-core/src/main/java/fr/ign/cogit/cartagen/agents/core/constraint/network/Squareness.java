package fr.ign.cogit.cartagen.agents.core.constraint.network;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.micro.LSSquarringAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SimpleSquaringAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SmallestSurroundingRectangleAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * Constraint for the squareness of an object.
 * 
 * @author julien Gaffuri
 * @author AMaudet 07/02/2012
 */
public class Squareness extends GeographicObjectConstraintImpl {
  private static Logger logger = LogManager.getLogger(Squareness.class.getName());

  /**
   * Number of almost right angle in the figure. An almost right angle is an
   * angle with a difference with the right angle between 5 and 15 degrees. For
   * instance, angles of 83, 98, 102 are almost right; those of 89, 94, 107 are
   * not.
   * 
   */
  private int almostRightAnglesNumber = 0;

  /**
   * Same as almostRightAnglesNumber but for flat angles (that can be squared to
   * 0).
   */
  private int almostFlatAnglesNumber = 0;

  private int nbVertices;

  private static int ALMOST_RIGHT_ANGLE_NUMBER_THRESHOLD = 6;

  private static double MINIMUM_THRESHOLD_TOLERANCE_ANGLE = 5.;

  private static int NB_VERTICES_SIMPLE_POLYGONS = 6;

  /**
   * 
   */
  // private double moyenneEcartsAnglesPresqueDroits;

  // private int nbAngles;

  public Squareness(GeographicAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {

    if (this.getAgent().getGeom() == null)
      return;

    // Compute the number of almost right angles.
    // Initialization
    this.almostRightAnglesNumber = 0;
    this.nbVertices = this.getAgent().getGeom().numPoints();
    // this.moyenneEcartsAnglesPresqueDroits = 0.0;
    // this.nbAngles = 0;

    if (this.getAgent().isDeleted()) {
      return;
    }

    // Get the Geometry
    IPolygon poly = (IPolygon) this.getAgent().getGeom();

    // Calculation for the exterior outline of the object.
    this.countAlmostRightAngles(poly.getExterior());

    // Calculation for inner outline
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.countAlmostRightAngles(poly.getInterior(i));
    }

    // if (this.nbAlmostRightAngles != 0) {
    // this.moyenneEcartsAnglesPresqueDroits =
    // this.moyenneEcartsAnglesPresqueDroits
    // / this.nbAlmostRightAngles;
    // }
  }

  /**
   * Parse the angles of ls to count the number of almost right angles. ls needs
   * to be closed.
   * @param ls
   */
  private void countAlmostRightAngles(IRing ls) {

    // Get the coordinates.
    IDirectPositionList coords = ls.coord();

    IDirectPosition c0 = coords.get(0);
    IDirectPosition c1 = coords.get(1);

    // Measure all the angles
    for (int i = 2; i < coords.size(); i++) {
      IDirectPosition c2 = coords.get(i);
      this.testAngle(c0, c1, c2);
      c0 = c1;
      c1 = c2;
    }
    // the last angle.
    this.testAngle(c0, c1, coords.get(1));
  }

  /**
   * If the angle formed by the point (c0, c1, c2) is almost right,
   * this.nbAlmostRightAngles increase of one.
   * 
   * @param c0
   * @param c1
   * @param c2
   */
  private void testAngle(IDirectPosition c0, IDirectPosition c1,
      IDirectPosition c2) {
    // if (logger.isTraceEnabled()) logger.trace("mesure angle");

    // Calculation of the angle (c0, c1, c2) in a radian value between -pi and
    // pi.

    // Calculation of the coordinate of c1-c0 vector
    double x10 = c0.getX() - c1.getX();
    double y10 = c0.getY() - c1.getY();

    // Calculation of the coordinate of c1-c0 vector
    double x12 = c2.getX() - c1.getX();
    double y12 = c2.getY() - c1.getY();

    // calculation if sinus and cosinus
    // double sinus = x10 * y12 - y10 * x12;
    // double cosinus = x10 * x12 + y10 + y12;

    double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);

    // double angle = Math.atan2(
    // (c0.getX() - c1.getX()) * (c2.getY() - c1.getY())
    // - (c0.getY() - c1.getY()) * (c2.getX() - c1.getX()),
    // (c0.getX() - c1.getX()) * (c2.getX() - c1.getX())
    // + (c0.getY() - c1.getY()) * (c2.getY() - c1.getY()));

    // Absolute value of the angle
    angle = Math.abs(angle);

    // test if flat
    double diffFlat = Math.abs(angle - Math.PI) * 180 / Math.PI;
    if ((diffFlat <= GeneralisationSpecifications.TOLERANCE_ANGLE)
        && (diffFlat >= MINIMUM_THRESHOLD_TOLERANCE_ANGLE)) {
      this.almostFlatAnglesNumber++;
      return;
    }

    // Difference to the right angle
    double difference = Math.abs(angle - Math.PI / 2) * 180 / Math.PI;

    logger.trace(
        "Difference to the right angle =" + difference + " with a tolerance of "
            + GeneralisationSpecifications.TOLERANCE_ANGLE);

    // test if this angle is almost right
    if ((difference <= GeneralisationSpecifications.TOLERANCE_ANGLE)
        && (difference >= MINIMUM_THRESHOLD_TOLERANCE_ANGLE)) {
      this.almostRightAnglesNumber++;
      // this.moyenneEcartsAnglesPresqueDroits += ecart;
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
    // Same priority as Granularity
    this.setPriority(7);
  }

  @Override
  public void computeSatisfaction() {
    if (Squareness.logger.isTraceEnabled()) {
      Squareness.logger.trace(
          "Calculation og the satisfaction of the squareness constraint of "
              + this.getAgent());
    }

    // if this agent is deleted, the squareness is satisfied
    if (this.getAgent().isDeleted()) {
      Squareness.logger.trace("The satisfaction of the squareness of "
          + this.getAgent() + " is perfect (deleted).");
      this.setSatisfaction(100);
      return;
    }

    // compute the current amount of almost right angles.
    this.computeCurrentValue();

    logger.trace("This agent got " + this.almostRightAnglesNumber
        + " almost right angles.");
    // if (Squareness.logger.isTraceEnabled()) {
    // Squareness.logger.trace(" Nbangles: " + this.nbAlmostRightAngles
    // + " moyenneEcarts: " + this.moyenneEcartsAnglesPresqueDroits);
    // }

    // if they are no almost right angle, the constraint is satisfied.
    if (this.almostRightAnglesNumber == 0) {
      Squareness.logger.trace("The satisfaction of the squareness of "
          + this.getAgent() + " is perfect.");
      this.setSatisfaction(100);
      return;
    }

    // if they are 4 or more almost right angles
    if (this.almostRightAnglesNumber >= ALMOST_RIGHT_ANGLE_NUMBER_THRESHOLD) {
      Squareness.logger.trace("The satisfaction of the squareness of "
          + this.getAgent() + " is zero.");
      this.setSatisfaction(0);
      return;
    }

    Squareness.logger
        .trace("The satisfaction of the squareness of " + this.getAgent()
            + " is " + (100 - (100 / ALMOST_RIGHT_ANGLE_NUMBER_THRESHOLD)
                * this.almostRightAnglesNumber)
            + ".");
    this.setSatisfaction(100 - (100 / ALMOST_RIGHT_ANGLE_NUMBER_THRESHOLD)
        * this.almostRightAnglesNumber);

    //
    // this.setSatisfaction(100 - (int) (100 *
    // this.moyenneEcartsAnglesPresqueDroits /
    // (GeneralisationSpecifications.TOLERANCE_ANGLE
    // * Math.PI / 180)));
    //
    // if (Squareness.logger.isTraceEnabled()) {
    // Squareness.logger.trace(" satifaction: " + this.getSatisfaction());
    // }
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    if (this.nbVertices < NB_VERTICES_SIMPLE_POLYGONS) {
      actionToPropose = new SimpleSquaringAction(
          (IMicroAgentGeneralisation) this.getAgent(), this, 5.0,
          GeneralisationSpecifications.TOLERANCE_ANGLE * Math.PI / 180,
          0.6 * Math.PI / 180);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 5.0));
    }

    if (this.almostFlatAnglesNumber > 0) {
      actionToPropose = new LSSquarringAction(
          (IMicroAgentGeneralisation) this.getAgent(), this, 4.0,
          GeneralisationSpecifications.TOLERANCE_ANGLE,
          GeneralisationSpecifications.TOLERANCE_ANGLE / 2);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 4.0));
      /*
       * actionToPropose = new SquarringAction( (IMicroAgentGeneralisation)
       * this.getAgent(), this, 2.0,
       * GeneralisationSpecifications.TOLERANCE_ANGLE, 50);
       * actionProposals.add(new ActionProposal(this, true, actionToPropose,
       * 2.0));
       */
    } else {
      /*
       * actionToPropose = new SquarringAction( (IMicroAgentGeneralisation)
       * this.getAgent(), this, 3.0,
       * GeneralisationSpecifications.TOLERANCE_ANGLE, 50);
       * actionProposals.add(new ActionProposal(this, true, actionToPropose,
       * 3.0));
       */
      actionToPropose = new LSSquarringAction(
          (IMicroAgentGeneralisation) this.getAgent(), this, 2.0,
          GeneralisationSpecifications.TOLERANCE_ANGLE,
          GeneralisationSpecifications.TOLERANCE_ANGLE / 2);
      actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));
    }

    /*
     * actionToPropose = new SquarringAction( (IMicroAgentGeneralisation)
     * this.getAgent(), this, 3.0, GeneralisationSpecifications.TOLERANCE_ANGLE,
     * 50); actionProposals.add(new ActionProposal(this, true, actionToPropose,
     * 3.0));
     */

    actionToPropose = new SmallestSurroundingRectangleAction(
        (IMicroAgentGeneralisation) this.getAgent(), this, 1.0,
        this.getAgent().getGeom().area());
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));

    return actionProposals;
  }

}
