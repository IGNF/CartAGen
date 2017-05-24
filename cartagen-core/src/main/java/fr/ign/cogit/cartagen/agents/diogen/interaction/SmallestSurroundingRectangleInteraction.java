package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.agents.core.action.micro.SmallestSurroundingRectangleAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Convexity;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Size;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * SmallestSurroundingRectangleInteraction change the original object to the
 * rectangle with the given area. This area may be the objective value of the
 * size constraint or the actual area of the object.
 * @author AMaudet
 * 
 */
public class SmallestSurroundingRectangleInteraction
    extends ConstrainedDegenerateInteraction {

  private static Logger logger = Logger
      .getLogger(SmallestSurroundingRectangleInteraction.class.getName());

  private static SmallestSurroundingRectangleInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private SmallestSurroundingRectangleInteraction() {
    super();
    loadSpecification();
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized SmallestSurroundingRectangleInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new SmallestSurroundingRectangleInteraction();
    }
    return singletonObject;
  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isConcave(Environment environment, IDiogenAgent source,
      GeographicConstraint constraint) {
    Convexity cconstraint = (Convexity) constraint;
    cconstraint.computeSatisfaction();
    return CommonAlgorithms
        .convexity(((GeographicObjectAgent) source).getGeom()) < 1;
  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isGoalAreaNull(Environment environment, IDiogenAgent source,
      GeographicConstraint constraint) {
    Size cconstraint = (Size) constraint;
    cconstraint.computeSatisfaction();
    return cconstraint.getGoalArea() <= 0.0;
  }

  /**
   * Compute a SSR Interaction. Use the size goal area if they are asize
   * constraint or the actual area value else. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    IMicroAgentGeneralisation csource = ((IMicroAgentGeneralisation) source);
    // if they are a size constraint, the action will aim the goal area.
    // Else, the aimed area will be the actual area value.
    Size sizeConstraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Size) {
        sizeConstraint = (Size) c;
        break;
      }
    }
    double aimedArea = csource.getGeom().area();
    if (sizeConstraint != null) {
      if (sizeConstraint.getGoalArea() != 0) {
        aimedArea = sizeConstraint.getGoalArea();
      } else {
        logger.log(Level.SEVERE,
            "Smallest Surrounding Rectangle Interaction used with a goal of area of 0. This shouldn't append!");
      }
    }
    Action action = new SmallestSurroundingRectangleAction(csource, null, 1.0,
        aimedArea);
    setAction(action);
    action.compute();
  }

}
