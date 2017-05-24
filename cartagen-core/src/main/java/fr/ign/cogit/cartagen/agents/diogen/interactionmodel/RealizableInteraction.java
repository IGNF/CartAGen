package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;

/**
 * RealizableInteraction express a possible instance of interaction between two
 * object. This class needs to implements Comparable, since
 * RealizableInteraction are sorted in Set.
 * http://www.lifl.fr/SMAC/projects/ioda/
 * @author AMaudet
 * 
 */
public abstract class RealizableInteraction<InteractionClass extends Interaction>
    implements Comparable<RealizableInteraction<InteractionClass>> {

  /**
   * The interaction object
   */
  private InteractionClass interaction;

  /**
   * The source agent for this Realizable Interaction
   */
  private IDiogenAgent source;

  /**
   * The target agent for this Realizable Interaction, may be null for
   * Degenerate Interaction.
   */
  private IDiogenAgent target;

  /**
   * The target agent for this Realizable Interaction, may be null for
   * Degenerate Interaction.
   */
  private Set<IDiogenAgent> targets = new HashSet<IDiogenAgent>();

  /**
   * The environment
   */
  private Environment environment;

  /**
   * The value of the trigger computed by the interaction object.
   */
  private double triggerValue;

  /**
   * The value of the preconditions computed by the interaction object.
   */
  private double preconditionsValue;

  /**
   * Constructor for RealizableInteraction
   * @param interaction
   * @param source
   * @param target
   * @param environment
   */
  public RealizableInteraction(InteractionClass interaction,
      IDiogenAgent source, IDiogenAgent target, Environment environment) {
    super();
    this.interaction = interaction;
    this.setSource(source);
    this.setTarget(target);
    this.setEnvironment(environment);
  }

  /**
   * Constructor for realizable interaction without target.
   * @param interaction
   * @param agent
   * @param environment
   */
  public RealizableInteraction(InteractionClass interaction,
      IDiogenAgent source, Environment environment) {
    super();
    this.interaction = interaction;
    this.setSource(source);
    this.setEnvironment(environment);
  }

  /**
   * Getter for triggerValue.
   * @return
   */
  public double getTriggerValue() {
    return this.triggerValue;
  }

  /**
   * Getter for preconditionsValue.
   * @return
   */
  public double getPreconditionsValue() {
    return this.preconditionsValue;
  }

  /**
   * Setter for interaction.
   * @param interaction the interaction to set
   */
  public void setInteraction(InteractionClass interaction) {
    this.interaction = interaction;
  }

  /**
   * Setter for triggerValue.
   * @param triggerValue the triggerValue to set
   */
  public void setTriggerValue(double triggerValue) {
    this.triggerValue = triggerValue;
  }

  /**
   * Setter for preconditionsValue.
   * @param preconditionsValue the preconditionsValue to set
   */
  public void setPreconditionsValue(double preconditionsValue) {
    this.preconditionsValue = preconditionsValue;
  }

  /**
   * Getter for interaction.
   * 
   * @return
   */
  public InteractionClass getInteraction() {
    return this.interaction;
  }

  /**
   * Setter for source.
   * @param source the source to set
   */
  public void setSource(IDiogenAgent source) {
    this.source = source;
  }

  /**
   * Getter for source.
   * @return the source
   */
  public IDiogenAgent getSource() {
    return this.source;
  }

  /**
   * Setter for target.
   * @param target the target to set
   */
  public void setTarget(IDiogenAgent target) {
    this.targets.add(target);
  }

  /**
   * Getter for target.
   * @return the target
   */
  public IDiogenAgent getTarget() {
    if (this.targets.isEmpty()) {
      return null;
    }
    return this.targets.iterator().next();
  }

  /**
   * 
   * @return
   */
  public Set<IDiogenAgent> getTargets() {
    return targets;
  }

  /**
   * 
   * @param targets
   */
  public void setTargets(Set<IDiogenAgent> targets) {
    this.targets = targets;
  }

  /**
   * Setter for environment.
   * @param environment the environment to set
   */
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  /**
   * Getter for environment.
   * @return the environment
   */
  public Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Comparison function. {@inheritDoc}
   */
  @Override
  public int compareTo(RealizableInteraction<InteractionClass> arg0) {
    if (this.getPreconditionsValue() > arg0.getPreconditionsValue()) {
      return 1;
    } else if (this.getPreconditionsValue() < arg0.getPreconditionsValue()) {
      return -1;
    } else if (this.getTriggerValue() > arg0.getTriggerValue()) {
      return 1;
    } else if (this.getTriggerValue() < arg0.getTriggerValue()) {
      return -1;
    } else {
      return 0;
    }
  }

  public boolean equals(RealizableInteraction<InteractionClass> arg0) {
    return (this.compareTo(arg0) == 0);
  }

  /**
   * 
   */
  public abstract void computeConditions();

}
