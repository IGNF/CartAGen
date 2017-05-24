package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.lifecycle.PadawanAgentLifeCycle;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.DefaultBorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.geoxygene.contrib.agents.agent.Agent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;

public abstract class DiogenAgent implements IDiogenAgent {

  private static Logger logger = Logger.getLogger(DiogenAgent.class.getName());

  public DiogenAgent() {
    super();
    this.borderStrategy = DefaultBorderStrategy.getInstance();
    this.borderedEnvironments = new HashSet<Environment>();
  }

  /**
   * The encapsulatedEnv (bidirectional reference, automatically managed).
   */
  private Environment encapsulatedEnv;

  /**
   * Getter for encapsulatedEnv. If no associated Environment, returns null.
   * @return the encapsulatedEnv
   */
  @Override
  public Environment getEncapsulatedEnv() {
    return this.encapsulatedEnv;
  }

  /**
   * Setter for encapsulatedEnv. Also updates the reverse reference from
   * encapsulatedEnv to {@code this}. To break the reference use
   * {@code this.setEncapsulatedEnv(null)}
   * @param encapsulatedEnv the encapsulatedEnv to set
   */
  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    Environment oldEncapsulatedEnv = this.encapsulatedEnv;
    this.encapsulatedEnv = encapsulatedEnv;
    if (oldEncapsulatedEnv != null)
      oldEncapsulatedEnv.setHostAgent(null);
    if (encapsulatedEnv != null) {
      if (encapsulatedEnv.getHostAgent() != this)
        encapsulatedEnv.setHostAgent(this);
    }
  }

  /**
   * The containingEnvironmentss set (bidirectional reference, automatically
   * managed).
   */
  private Set<Environment> containingEnvironments = new HashSet<Environment>();

  /**
   * Getter for containingEnvironmentss.
   * @return the containingEnvironmentss. It can be empty but not {@code null}.
   */
  @Override
  public Set<Environment> getContainingEnvironments() {
    return this.containingEnvironments;
  }

  /**
   * Setter for containingEnvironmentss. Also updates the reverse reference from
   * each element of containingEnvironmentss to {@code this}. To break the
   * reference use {@code this.setContainingEnvironmentss(new
   * HashSet<Environment>())}
   * @param containingEnvironmentss the set of containingEnvironmentss to set
   */
  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    Set<Environment> oldContainingEnvironments = new HashSet<Environment>(
        this.containingEnvironments);
    for (Environment containingEnvironment : oldContainingEnvironments) {
      this.containingEnvironments.remove(containingEnvironment);
      containingEnvironment.getContainedAgents().remove(this);
    }
    for (Environment containingEnvironment : containingEnvironments) {
      this.containingEnvironments.add(containingEnvironment);
      containingEnvironment.getContainedAgents().add(this);
    }
  }

  /**
   * Adds a Environment to containingEnvironmentss, and updates the reverse
   * reference from the added Environment to {@code this}.
   * @param containingEnvironments the containingEnvironments to remove
   */
  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    if (containingEnvironments == null)
      return;
    this.containingEnvironments.add(containingEnvironment);
    containingEnvironment.getContainedAgents().add(this);
  }

  /**
   * Removes a Environment from containingEnvironmentss, and updates the reverse
   * reference from the removed Environment by removing {@code this}.
   * @param containingEnvironments the containingEnvironments to remove
   */
  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    if (containingEnvironments == null)
      return;
    this.containingEnvironments.remove(containingEnvironment);
    containingEnvironment.getContainedAgents().remove(this);
  }

  private EnvironmentStrategy environmentStrategy;

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    return environmentStrategy;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    this.environmentStrategy = environmentStrategy;
  }

  /**
   * A specific activate method to use with Padawan model.
   */
  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    IDiogenAgent a = (IDiogenAgent) Agent.getActivatedAgent();
    Agent.setActivatedAgent(this);

    // activation of the agent
    if (!(this.getLifeCycle() instanceof PadawanAgentLifeCycle)) {
      logger.error(
          "Call the Padawan specific activate method for a non-Padawan agent.");
      return AgentSatisfactionState.ERROR;
    }
    AgentSatisfactionState out = ((PadawanAgentLifeCycle) this.getLifeCycle())
        .compute(this, environment);

    Agent.setActivatedAgent(a);

    return out;
  }

  /**
   * The borderedEnvironments set (bidirectional reference, automatically
   * managed).
   */
  private Set<Environment> borderedEnvironments = new HashSet<Environment>();

  /**
   * Getter for borderedEnvironments.
   * @return the borderedEnvironments. It can be empty but not {@code null}.
   */
  @Override
  public Set<Environment> getBorderedEnvironments() {
    return this.borderedEnvironments;
  }

  /**
   * Setter for borderedEnvironments. Also updates the reverse reference from
   * each element of borderedEnvironments to {@code this}. To break the
   * reference use {@code this.setBorderedEnvironments(new
   * HashSet<Environment>())}
   * @param borderedEnvironments the set of borderedEnvironments to set
   */
  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    Set<Environment> oldBorderedEnvironments = new HashSet<Environment>(
        this.borderedEnvironments);
    for (Environment borderedEnvironment : oldBorderedEnvironments) {
      this.borderedEnvironments.remove(borderedEnvironment);
      borderedEnvironment.getBorderAgents().remove(this);
    }
    for (Environment borderedEnvironment : borderedEnvironments) {
      this.borderedEnvironments.add(borderedEnvironment);
      borderedEnvironment.getBorderAgents().add(this);
    }
  }

  /**
   * Adds a Environment to borderedEnvironments, and updates the reverse
   * reference from the added Environment to {@code this}.
   * @param borderedEnvironment the borderedEnvironment to remove
   */
  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    if (borderedEnvironment == null)
      return;
    this.borderedEnvironments.add(borderedEnvironment);
    borderedEnvironment.getBorderAgents().add(this);
  }

  /**
   * Removes a Environment from borderedEnvironments, and updates the reverse
   * reference from the removed Environment by removing {@code this}.
   * @param borderedEnvironment the borderedEnvironment to remove
   */
  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    if (borderedEnvironment == null)
      return;
    this.borderedEnvironments.remove(borderedEnvironment);
    borderedEnvironment.getBorderAgents().remove(this);
  }

  /**
   * The Border Strategy of the agent. The default strategy considers that
   * {@code this} does not border any environment.
   */
  private BorderStrategy borderStrategy;

  @Override
  public BorderStrategy getBorderStrategy() {
    return borderStrategy;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    this.borderStrategy = borderStrategy;
  }

}
