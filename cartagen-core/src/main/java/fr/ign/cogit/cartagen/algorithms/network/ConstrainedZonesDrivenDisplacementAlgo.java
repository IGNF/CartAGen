/**
 * An algorithm that tries and displace a CartACom small compact agent so that
 * it satisfies some of its constraints, while remaining in a given zone among
 * its limit zones. See the compute() method for more details of how this
 * algorithm works.
 */
package fr.ign.cogit.cartagen.algorithms.network;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * @author CDuchene
 * 
 */
public class ConstrainedZonesDrivenDisplacementAlgo {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  private static Logger logger = LogManager
      .getLogger(ConstrainedZonesDrivenDisplacementAlgo.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  /**
   * The CartACom small compact agent this algorithm applies to
   */
  private ISmallCompactAgent smallCompactAgent;

  /**
   * The number of the limit zone the agent should stay within during the
   * displacement.
   */
  private int limitZoneNumber;

  /**
   * Set of the constraints (with constrained zone) this action is expected to
   * satisfy if possible.
   */
  private Set<MicroMicroRelationalConstraintWithZone> constraintsToSatisfy = new HashSet<MicroMicroRelationalConstraintWithZone>();

  /**
   * Among the constraints to satisfy, the ones that had proposed to trigger
   * this action (the other ones have been added during the aggregation).
   */
  private Set<MicroMicroRelationalConstraintWithZone> triggeringConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();

  /**
   * Among the triggering constraints, the ones for which a request for this
   * action has been sent by the other agent sharing the constraint.
   */
  private Set<MicroMicroRelationalConstraintWithZone> requestLinkedConstraints = new HashSet<MicroMicroRelationalConstraintWithZone>();

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an instance of ConstrainedZonesDrivenDisplacementAlgorithm that
   * will try and displace the CartACom small compact agent in parameter so that
   * it satisfies its constraints passed in parameter, while remaining in the
   * limit zone passed in parameter.
   * @param smallCompactAgent the small compact agent to displace
   * @param limitZoneNumber the number of the limit zone in which a position has
   *          to be found (values between 1 = closest zone around the CartACom
   *          agent, and {@link CartacomSpecifications#NB_LIMIT_ZONES} = largest
   *          zone = buffer of offset
   *          {@link GeneralisationSpecifications#DISTANCE_MAX_DEPLACEMENT_BATIMENT}
   * @param constraintsToSatisfy The constraints the displacemnet should try to
   *          satisfy
   * @param triggeringConstraints among the constraintsToSatisfy, the
   *          constraints that have proposed to try this action (the other ones
   *          should be considered if possible, but these ones have priority
   *          over them)
   * @param requestLinkedConstraints among the triggeringConstraints, the
   *          constraints that come from a request by another agent (they are of
   *          highest priority)
   */
  public ConstrainedZonesDrivenDisplacementAlgo(
      ISmallCompactAgent smallCompactAgent, int limitZoneNumber,
      Set<MicroMicroRelationalConstraintWithZone> constraintsToSatisfy,
      Set<MicroMicroRelationalConstraintWithZone> triggeringConstraints,
      Set<MicroMicroRelationalConstraintWithZone> requestLinkedConstraints) {
    super();
    this.smallCompactAgent = smallCompactAgent;
    this.limitZoneNumber = limitZoneNumber;
    this.constraintsToSatisfy = constraintsToSatisfy;
    this.triggeringConstraints = triggeringConstraints;
    this.requestLinkedConstraints = requestLinkedConstraints;
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  /**
   * Tries to find a position for the small compact agent that respects the
   * constraints to satisfy ({@code this.constraintsToSatisfy}), the triggering
   * constraints ({@code this.triggeringConstraints}) and the constraints coming
   * from a request ({@code this.requestLinkedConstraints}), using the
   * constrained zones associated to every task. First the small compact agent
   * seeks for free space taking into account all contraints, then if no free
   * space can be found the constraints are relaxed by decreasing order of
   * importance. For two constraints of the same importance, constraints that
   * are "only" to satisfy are relaxed first, then constrinats that are "only"
   * triggering, then at last constraints linked to a request.
   * @return {@code ActionResult.MODIFIED} if the small compact agent has been
   *         displaced (a better position than the initial one has been found)
   *         <br/>
   *         {@code ActionResult.UNCHANGED} if no better position has been
   *         found, therefore the small compact has been left unchanged
   * 
   */
  public ActionResult compute() {

    // Debug
    ConstrainedZonesDrivenDisplacementAlgo.logger
        .debug("Constraints to satisfy: \n");
    for (MicroMicroRelationalConstraintWithZone constr : this.constraintsToSatisfy) {
      ConstrainedZonesDrivenDisplacementAlgo.logger
          .debug(constr.getClass().getName() + "\n");
    }
    ConstrainedZonesDrivenDisplacementAlgo.logger
        .debug("Triggering constraints: \n");
    for (MicroMicroRelationalConstraintWithZone constr : this.triggeringConstraints) {
      ConstrainedZonesDrivenDisplacementAlgo.logger
          .debug(constr.getClass().getName() + "\n");
    }
    ConstrainedZonesDrivenDisplacementAlgo.logger
        .debug("Request linked constraints: \n");
    for (MicroMicroRelationalConstraintWithZone constr : this.requestLinkedConstraints) {
      ConstrainedZonesDrivenDisplacementAlgo.logger
          .debug(constr.getClass().getName() + "\n");
    }
    // End debug

    // Stores all the importances of the considered constraints which are
    // greater than the min of the importances of the constraints to satisfy
    // in a list (while ensuring unicity)
    List<Double> handledImportances = new ArrayList<Double>();
    for (MicroMicroRelationalConstraintWithZone constraint : this.constraintsToSatisfy) {
      double currentImportance = constraint.getImportance();
      // Debug
      ConstrainedZonesDrivenDisplacementAlgo.logger
          .debug("Contrainte examinée:  " + constraint.getClass().getName()
              + " AVEC AGENT " + constraint.getAgentSharingConstraint()
              + ". Et son importance:" + constraint.getImportance());
      // End debug
      if (!handledImportances.contains(currentImportance)) {
        handledImportances.add(new Double(currentImportance));
      }
    }
    // Sort the list in ascending order
    Collections.sort(handledImportances);
    // Debug
    ConstrainedZonesDrivenDisplacementAlgo.logger
        .debug("Handled importances: " + handledImportances);
    // End debug

    // Define a few needed variables
    // Geometry to hold the "centroid related" free space that will be
    // computed
    IGeometry centroidFreeSpace = null;
    // Boolean to notice if free space has been found
    boolean foundFreeSpace = false;
    // Itarator on the list of constriants importances
    ListIterator<Double> importanceIterator = handledImportances
        .listIterator(0);
    // Denotes if we are at first iteration or not
    boolean firstIteration = true;

    // Loop to find a satisfying position
    // At each iteration, 2 steps will be performed
    // 1. find a position while considering all constraints of
    // importance >= current min importance
    // 2. find a position while considering only 'request linked' constraints
    // of importance >= current min importance
    // The loop stops when a satisfying position has been found, or when all
    // the triggering constraints have been relaxed.
    while (importanceIterator.hasNext() && (!foundFreeSpace)) {
      // Gets the min considered importance at this iteration
      Double currentMinImportance = importanceIterator.next();
      // 1. Try to find a position satisfying all constraints of
      // importance >= current min importance
      // 1.1. Constraints removal stage 1: remove from "constraintsToSatisfy",
      // all constraints with an importance < currentMinImportance
      // Note: this is not needed at first iteration, as all constraints are to
      // be considered at this iteration (none will be found of importnace <
      // currentMinImportance.
      int removedCounter = 0;
      Iterator<MicroMicroRelationalConstraintWithZone> constraintsIterator = this.constraintsToSatisfy
          .iterator();
      if (!firstIteration) {
        while (constraintsIterator.hasNext()) {
          MicroMicroRelationalConstraintWithZone constraint = constraintsIterator
              .next();
          if ((constraint.getImportance() < currentMinImportance
              .doubleValue())) {
            constraintsIterator.remove();
            removedCounter++;
          }
        } // while (constraintsIterator.hasNext())
      } // if (!firstIteration) [1.1. Constraints removal stage 1]
      // If constraints have been removed or first iteration looks for
      // free space
      if (removedCounter > 0 || firstIteration) {
        firstIteration = false; // We are not at the first iteration any more
        centroidFreeSpace = this.smallCompactAgent.computeFreeSpace(
            this.constraintsToSatisfy, this.limitZoneNumber, true);
        // Debug pas beau
        // CecileGUIComponent.DisplayGeomAndPause(centroidFreeSpace,
        // Color.GREEN,
        // 1, "Free space for current min importance " + currentMinImportance
        // + ", all constraints");
        // End debug
        // If free space not empty exit from the loop
        if (!centroidFreeSpace.isEmpty()) {
          foundFreeSpace = true;
          break;
        }
      } // Looks for free space at stage 1

      // 2. Try to find a position satisfying all REQUEST LINKED
      // constraints of
      // importance >= current min importance
      // 2.1.Removes constraints stage 2
      constraintsIterator = this.constraintsToSatisfy.iterator();
      removedCounter = 0;
      while (constraintsIterator.hasNext()) {
        MicroMicroRelationalConstraintWithZone constraint = constraintsIterator
            .next();
        if ((constraint.getImportance() == currentMinImportance.doubleValue())
            && (!this.requestLinkedConstraints.contains(constraint))) {
          constraintsIterator.remove();
          removedCounter++;
        }
      } // 2.1. Removal of constaints at stage 2
      // 2.2. Looks for free space stage 2
      if (removedCounter > 0) {
        centroidFreeSpace = this.smallCompactAgent.computeFreeSpace(
            this.constraintsToSatisfy, this.limitZoneNumber, true);
        // Debug pas beau
        /*
         * CecileGUIComponent.DisplayGeomAndPause(centroidFreeSpace,
         * Color.GREEN, 1, "Free space for current min importance " +
         * currentMinImportance + ", only request linked");
         */
        // End debug
        // If free space not empty exit from the loop
        if (centroidFreeSpace == null || !centroidFreeSpace.isEmpty()) {
          foundFreeSpace = true;
          break;
        }
      } // 2.2. Looks for free space stage 2
    } // Loop to find a satisfying position
    // If no satisfying position found returns unchanged
    if (!foundFreeSpace) {
      ConstrainedZonesDrivenDisplacementAlgo.logger
          .info("No free space found - agent left unchanged by action "
              + this.getClass().getName() + ".");
      return ActionResult.UNCHANGED;
    }
    // Now some free space has been found
    // In this free space, finds the nearest point from the initial position
    // of the centroid
    IDirectPosition nearestFreePoint;
    if (centroidFreeSpace instanceof IPolygon) {
      IPolygon centroidFreeSpacePoly = (IPolygon) centroidFreeSpace;
      nearestFreePoint = JtsAlgorithms.getClosestPoint(
          this.smallCompactAgent.getRootState().getCentroid(),
          centroidFreeSpacePoly);
    } else {
      nearestFreePoint = CommonAlgorithmsFromCartAGen.getNearestVertexFromPoint(
          centroidFreeSpace,
          this.smallCompactAgent.getRootState().getCentroid());
    }
    // TODO A debuguer. Il semble que même lorsque le centroidFreeSpace est un
    // polygone, le point le plus proche renvoyé soit le vertex le plus proche
    // et non le point (possiblement entre 2 vertex) le plus proche.

    // Debug pas beau
    IGeometry[] debugGeoms = {
        this.smallCompactAgent.getCentroid().toGM_Point(),
        nearestFreePoint.toGM_Point() };
    Color[] debugColors = { Color.BLUE, Color.MAGENTA };
    int[] debugWidths = { 1, 1 };
    /*
     * CecileGUIComponent.DisplayGeomsAndPause(debugGeoms, debugColors,
     * debugWidths, "Centroide en Bleu, Nearest free point en Magenta ");
     */
    // End debug
    // Deduces the displacement vector
    Vector2D displVect = new Vector2D(this.smallCompactAgent.getCentroid(),
        nearestFreePoint);
    // If displacement vector null, returns unchanged
    if (displVect.isNull()) {
      ConstrainedZonesDrivenDisplacementAlgo.logger.info(
          "Computed displacement vector null - agent left unchanged by action "
              + this.getClass().getName() + ".");
      return ActionResult.UNCHANGED;
    }
    this.smallCompactAgent.displaceAndRegister(displVect.getX(),
        displVect.getY());
    ConstrainedZonesDrivenDisplacementAlgo.logger
        .info("Agent displaced by vector (" + displVect.getX() + " , "
            + displVect.getY() + ").");
    return ActionResult.MODIFIED;

  }
  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
