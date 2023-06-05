package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

/**
 * 
 * @author AMaudet
 * 
 */
public abstract class ConstrainedMultipleTargetsInteraction
    extends ConstrainedAbstractInteraction {

  private static Logger logger = LogManager
      .getLogger(ConstrainedMultipleTargetsInteraction.class.getName());

  /**
   * Return the constraints of source used by this interaction.
   * 
   * @param source
   * @return
   */
  @Override
  public Set<GeographicConstraint> getConstraints(IDiogenAgent source) {
    Set<ConstraintType> constraintsTypes = this.getConstraintsTypeNameList();
    Set<Constraint> constraintsList = source.getConstraints();
    Set<GeographicConstraint> returnSet = new HashSet<GeographicConstraint>();
    for (ConstraintType t : constraintsTypes) {
      ConstrainedMultipleTargetsInteraction.logger.debug("Type " + t.getName());
      for (Constraint e : constraintsList) {
        ConstrainedMultipleTargetsInteraction.logger
            .debug("Object of class " + e.getClass());
        if (t.getName().equals(e.getClass().getName())) {
          ConstrainedMultipleTargetsInteraction.logger
              .debug("Constraint identified");
          returnSet.add((GeographicConstraint) e);
        }
      }
    }
    return returnSet;
  }

  @Override
  public Set<GeographicConstraint> getConstraints(IDiogenAgent source,
      Set<IDiogenAgent> targets) {
    Set<ConstraintType> constraintsTypes = this.getConstraintsTypeNameList();
    Set<Constraint> constraintsList = source.getConstraints();
    Set<GeographicConstraint> returnSet = new HashSet<GeographicConstraint>();
    for (ConstraintType t : constraintsTypes) {
      ConstrainedMultipleTargetsInteraction.logger.debug("Type " + t.getName());
      for (Constraint e : constraintsList) {
        ConstrainedMultipleTargetsInteraction.logger
            .debug("Object of class " + e.getClass());
        if (t.getName().equals(e.getClass().getName())) {
          ConstrainedMultipleTargetsInteraction.logger
              .debug("Constraint identified");
          returnSet.add((GeographicConstraint) e);
        }
      }
    }
    return returnSet;
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
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {
    double res = 0;
    for (GeographicConstraint constraint : constraints) {
      int preconditionForThisconstraint = this.preconditionByConstraint(
          environment, source, targets, constraint, constraintAdvicesMap);
      if (preconditionForThisconstraint == Integer.MIN_VALUE) {
        return 0;
      } else if (preconditionForThisconstraint > 0) {
        constraint.computeSatisfaction();
        res += constraint.getPriority() * 10000
            + constraint.getImportance() * 100 + 100
            - constraint.getSatisfaction() + this.getWeight();
        ConstrainedMultipleTargetsInteraction.logger.debug("Interaction "
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
      IDiogenAgent source, Set<IDiogenAgent> targets,
      GeographicConstraint constraint,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {

    ConstrainedMultipleTargetsInteraction.logger.debug(
        "Evaluate precondition for " + this + " constraint " + constraint);
    // parse all the constraint types
    for (ConstraintType ct : this.getConstraintsTypeNameList()) {

      ConstrainedMultipleTargetsInteraction.logger
          .debug("Compare type " + ct + "to constraint " + constraint);

      // if the constraint is of the given type
      if (constraint.getClass().getName().equals(ct.getName())) {

        ConstrainedMultipleTargetsInteraction.logger.debug("OK");

        // parse all the possible influencing method
        for (int influence : ct.getInfluencesMap().keySet()) {
          boolean result = false;
          try {
            // compute the value of the method
            result = (Boolean) ct.getInfluencesMap().get(influence).invoke(this,
                new Object[] { environment, source, constraint });
          } catch (IllegalArgumentException | IllegalAccessException
              | InvocationTargetException e) {
            e.printStackTrace();
          }
          ConstrainedMultipleTargetsInteraction.logger
              .debug("The influence of constaint " + ct + " on interaction "
                  + this + " is " + influence);
          if (result) {
            return influence;
          }
        }
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
      Set<IDiogenAgent> targets, GeographicConstraint constraint) {
    return constraint.getSatisfaction() <= 99.5;
  }

  public boolean isNotSameTarget(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, GeographicConstraint constraint) {

    if (targets.size() > 1) {
      return true;
    } else {
      IAgent target = targets.iterator().next();
      if (!(constraint instanceof RelationalConstraint)) {
        if (!(constraint instanceof SubmicroSimpleConstraint)) {
          return true;
        } else {
          ISubMicro submicro = ((SubmicroSimpleConstraint) constraint)
              .getSubmicro();
          if (source instanceof IPointAgent) {
            if (target instanceof SubmicroAgent) {
              return (((SubmicroAgent) target).getFeature()
                  .getSubMicro() != submicro);
            } else {
              return true;
            }
          } else if (source instanceof SubmicroAgent) {
            return !((SubmicroAgent) target).getFeature().getSubMicro()
                .getPointAgents().contains(target);
          }
        }
      }
      return (target != ((RelationalConstraint) constraint)
          .getAgentSharingConstraint());

      // return (!(constraint instanceof RelationalConstraint))
      // || (target != ((RelationalConstraint) constraint)
      // .getAgentSharingConstraint());
    }
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
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap) {
    ConstrainedMultipleTargetsInteraction.logger
        .debug("Preconditions for " + constraints);
    int res = 0;
    boolean pos = false;
    for (GeographicConstraint c : constraints) {
      int preconditionForThisconstraint = this.preconditionByConstraint(
          environment, source, targets, c, constraintAdvicesMap);

      // logger.debug("Prec for " + c + " =" + preconditionForThisconstraint);
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
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException;

}
