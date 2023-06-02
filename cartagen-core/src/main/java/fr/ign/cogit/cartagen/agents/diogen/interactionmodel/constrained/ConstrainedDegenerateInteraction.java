package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * 
 * @author AMaudet
 * 
 */
public abstract class ConstrainedDegenerateInteraction
    extends ConstrainedAbstractInteraction {

  private static Logger logger = LogManager
      .getLogger(ConstrainedDegenerateInteraction.class.getName());

  /**
   * Return the constraints of source used by this interaction.
   * 
   * @param source
   * @return
   */
  @Override
  public Set<GeographicConstraint> getConstraints(IDiogenAgent source) {
    // logger.setLevel(Level.OFF);
    Set<ConstraintType> constraintsTypes = this.getConstraintsTypeNameList();
    Set<Constraint> constraintsList = source.getConstraints();
    Set<GeographicConstraint> returnSet = new HashSet<GeographicConstraint>();
    for (ConstraintType t : constraintsTypes) {
      // logger.log(Level.INFO, "Type " + t);
      // ConstrainedDegenerateInteraction.logger.debug("Type " + t.getName());
      for (Constraint e : constraintsList) {
        // logger.log(Level.INFO, "Object " + e.getClass() + " S=" +
        // ((GeographicConstraint)e).getSatisfaction());
        // ConstrainedDegenerateInteraction.logger.debug("Object of class "
        // + e.getClass());
        if (t.getName().equals(e.getClass().getName())) {
          // ConstrainedDegenerateInteraction.logger
          // .debug("Constraint identified");
          // logger.log(Level.INFO, "Constraint identified");
          returnSet.add((GeographicConstraint) e);
        }
      }
    }
    return returnSet;
  }

  @Override
  public Set<GeographicConstraint> getConstraints(IDiogenAgent source,
      Set<IDiogenAgent> targets) {
    return getConstraints(source);
  }

  /**
   * Compute the value of the motivation of source.
   * 
   * @param environment
   * @param source
   * @param constraints
   * @return
   */
  public double trigger(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {
    double res = 0;
    for (GeographicConstraint constraint : constraints) {
      int preconditionForThisconstraint = this.preconditionByConstraint(
          environment, source, constraint, constraintAdvicesMap);
      if (preconditionForThisconstraint == Integer.MIN_VALUE) {
        return 0;
      } else if (preconditionForThisconstraint > 0) {
        constraint.computeSatisfaction();
        constraint.computePriority();
        res += constraint.getPriority() * 10000
            + constraint.getImportance() * 100 + 100
            - constraint.getSatisfaction() + this.getWeight();
        ConstrainedDegenerateInteraction.logger.debug("Interaction "
            + this.toString() + " Constraint " + constraint + " P="
            + constraint.getPriority() + " I=" + constraint.getImportance()
            + " S=" + constraint.getSatisfaction());
      } else if (preconditionForThisconstraint < 0) {

      }
    }
    // System.out.println("Actual Trigger : " + res);
    return res;
  }

  /**
   * Return an int to express the influence of the constraint on this
   * interaction.
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public int preconditionByConstraint(Environment environment,
      IDiogenAgent source, GeographicConstraint constraint,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {

    ConstrainedDegenerateInteraction.logger.debug(
        "Evaluate precondition for " + this + " constraint " + constraint);

    Integer toReturn = constraintAdvicesMap.get(constraintAdvicesMap);
    if (toReturn != null) {
      return toReturn;
    }

    // parse all the constraint types
    for (ConstraintType ct : this.getConstraintsTypeNameList()) {

      ConstrainedDegenerateInteraction.logger
          .debug("Compare type " + ct + "to constraint " + constraint);

      // if the constraint is of the given type
      if (constraint.getClass().getName().equals(ct.getName())) {
        Object[] arguments = new Object[] { environment, source, constraint };
        // parse all the possible influencing method
        try {
          Method method = ct.getInfluencesMap().get(ConstraintType.OPPOSITE);
          if ((method != null) && ((Boolean) method.invoke(this, arguments))) {
            constraintAdvicesMap.put(constraint, ConstraintType.OPPOSITE);
            return ConstraintType.OPPOSITE;
          }
          method = ct.getInfluencesMap()
              .get(ConstraintType.STRONGHLY_FAVORABLE);
          if ((method != null) && ((Boolean) method.invoke(this, arguments))) {
            constraintAdvicesMap.put(constraint, ConstraintType.FAVORABLE);
            return ConstraintType.FAVORABLE;
          }

          method = ct.getInfluencesMap().get(ConstraintType.INDIFFERENT);
          if ((method != null) && ((Boolean) method.invoke(this, arguments))) {
            constraintAdvicesMap.put(constraint, ConstraintType.INDIFFERENT);
            return ConstraintType.INDIFFERENT;
          }

          method = ct.getInfluencesMap().get(ConstraintType.FAVORABLE);
          boolean favorable = false;
          if ((method != null)) {
            favorable = ((Boolean) method.invoke(this, arguments));
          }

          method = ct.getInfluencesMap().get(ConstraintType.UNFAVORABLE);
          if ((method != null) && ((Boolean) method.invoke(this, arguments))) {
            if (favorable) {
              constraintAdvicesMap.put(constraint, ConstraintType.INDIFFERENT);
              return ConstraintType.INDIFFERENT;
            } else {
              constraintAdvicesMap.put(constraint, ConstraintType.UNFAVORABLE);
              return ConstraintType.UNFAVORABLE;
            }
          } else if (favorable) {
            constraintAdvicesMap.put(constraint, ConstraintType.FAVORABLE);
            return ConstraintType.FAVORABLE;
          }
          constraintAdvicesMap.put(constraint, ConstraintType.INDIFFERENT);
          return ConstraintType.INDIFFERENT;

          // for (int influence : ct.getInfluencesMap().keySet()) {
          // boolean result = false;
          // // compute the value of the method
          // result = (Boolean) ct.getInfluencesMap().get(influence)
          // .invoke(this, new Object[] { environment, source, constraint });
          //
          // ConstrainedDegenerateInteraction.logger
          // .debug("The influence of constaint " + ct + " on interaction "
          // + this + " is " + influence);
          //
          // if (result) {
          // set.add(influence);
          // }
          // }
        } catch (IllegalArgumentException | IllegalAccessException
            | InvocationTargetException e) {
          e.printStackTrace();
        }

        constraintAdvicesMap.put(constraint, ct.getDefaultInfluence());
        return ct.getDefaultInfluence();
      }

    }

    return 0;

    // if (this.hasConstraintTypeName(constraint.getClass().getName())
    // && constraint.getSatisfaction() <= 99.5) {
    // logger.debug("Agent " + source + " has for constraint " + constraint
    // + " S=" + constraint.getSatisfaction());
    // return 1;
    // }
    // return 0;

  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraint
   * @return
   */
  public boolean isUnsatisfied(Environment environment, IDiogenAgent source,
      GeographicConstraint constraint) {
    return constraint.getSatisfaction() <= 99.5;
  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraints
   * @return
   * @throws ClassNotFoundException
   */
  public int preconditions(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {
    ConstrainedDegenerateInteraction.logger
        .debug("Preconditions for " + constraints);
    int res = 0;
    boolean pos = false;
    for (GeographicConstraint c : constraints) {
      int preconditionForThisconstraint = this.preconditionByConstraint(
          environment, source, c, constraintAdvicesMap);

      logger.debug("Prec for " + c + " =" + preconditionForThisconstraint);
      if (preconditionForThisconstraint < 0) {
        res += preconditionForThisconstraint;
      } else if (preconditionForThisconstraint == 1) {
        // res += 1;
        pos = true;
      }
    }
    if (!pos) {
      return Integer.MIN_VALUE;
    }
    if (res < 0) {
      return res;
    }
    return 1;
  }

  /**
   * 
   * 
   * @param environment
   * @param source
   * @param constraints
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  public abstract void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException;

  /**
   * Call trigger method without target argument as a degenerate interaction
   * don't use target.
   * 
   * @param environment
   * @param source
   * @param target
   * @param constraints
   * @return
   */
  @Override
  public double trigger(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {
    return this.trigger(environment, source, constraints, constraintAdvicesMap);
  }

  /**
   * Call precondition method without target argument as a degenerate
   * interaction don't use target.
   * 
   * @param environment
   * @param source
   * @param target
   * @param constraints
   * @return
   * @throws ClassNotFoundException
   */
  @Override
  public int preconditions(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap)
      throws ClassNotFoundException {
    return this.preconditions(environment, source, constraints,
        constraintAdvicesMap);
  }

  /**
   * Call perform method without target argument as a degenerate interaction
   * don't use target.
   * 
   * @param environment
   * @param source
   * @param target
   * @param constraints
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    this.perform(environment, source, constraints);
  }

}
