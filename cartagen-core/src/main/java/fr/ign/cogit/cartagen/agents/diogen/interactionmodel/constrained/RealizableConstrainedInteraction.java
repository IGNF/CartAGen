package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.ISubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.RealizableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class RealizableConstrainedInteraction
    extends RealizableInteraction<ConstrainedInteraction> {

  /**
   * The constraints
   */
  protected Set<GeographicConstraint> constraints;

  private Map<GeographicConstraint, Integer> constraintAdvicesMap = new HashMap<>();

  public RealizableConstrainedInteraction(ConstrainedInteraction interaction,
      IDiogenAgent source, IDiogenAgent target, Environment environment) {
    super(interaction, source, target, environment);
    Set<IAgent> targets = new HashSet<>();
    targets.add(target);
    this.constraints = new HashSet<>();
    for (Constraint c : source.getConstraints()) {
      this.constraints.add((GeographicConstraint) c);
    }

    if (source instanceof GeographicPointAgent) {
      Set<ISubmicroAgent> set = ((GeographicPointAgent) source)
          .getSubmicroAgents();
      for (ISubmicroAgent a : set) {
        for (Constraint cToAdd : a.getConstraints()) {
          if (cToAdd instanceof GeographicConstraint) {
            this.constraints.add((GeographicConstraint) cToAdd);
          }
        }
      }
    }
  }

  public Set<GeographicConstraint> getConstraints() {
    return constraints;
  }

  public RealizableConstrainedInteraction(ConstrainedInteraction interaction,
      IDiogenAgent source, Environment environment) {
    super(interaction, source, environment);
    this.constraints = interaction.getConstraints(source);
  }

  /**
   * Compute the preconditionsValue and triggerValue attributes.
   */
  @Override
  public void computeConditions() {
    // System.out.println("computeCondition Env " + getEnvironment() +
    // " source "
    // + getSource() + " targets " + getTargets() + " constraints "
    // + constraints);
    try {
      this.setPreconditionsValue(this.getInteraction().preconditions(
          this.getEnvironment(), this.getSource(), this.getTargets(),
          this.constraints, constraintAdvicesMap));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    // System.out.println("interaction " + this.getInteraction()
    // + " ; constraints type "
    // + this.getInteraction().getConstraintsTypeNameList());

    // System.out.println("map " + constraintAdvicesMap);

    this.constraints = new HashSet<>();
    for (GeographicConstraint c : constraintAdvicesMap.keySet()) {
      if (constraintAdvicesMap.get(c) > 0) {
        this.constraints.add(c);
      }
    }
    // System.out.println("constraints " + constraints);
    this.setTriggerValue(
        this.getInteraction().trigger(this.getEnvironment(), this.getSource(),
            this.getTargets(), this.constraints, constraintAdvicesMap));

  }

  /**
   * Perform this realizable interaction
   * 
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  public void perform() throws InterruptedException, ClassNotFoundException {
    this.getInteraction().perform(this.getEnvironment(), this.getSource(),
        this.getTargets(), this.constraints);
  }

  public String toString() {
    return "Interaction " + this.getInteraction() + ", source "
        + this.getSource() + ", targets " + this.getTargets() + ", constraints "
        + this.getConstraints();
  }
}
