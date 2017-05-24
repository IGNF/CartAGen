package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;

public class RealisableConstrainedMultipleTargetsInteraction
    extends RealizableConstrainedInteraction {

  protected Set<IDiogenAgent> targets;

  /**
   * Constructor for RealizableInteraction
   * @param interaction
   * @param source
   * @param target
   * @param environment
   */
  public RealisableConstrainedMultipleTargetsInteraction(
      ConstrainedMultipleTargetsInteraction interaction, IDiogenAgent source,
      Set<IDiogenAgent> targets, Environment environment) {
    super(interaction, source, environment);
    this.targets = targets;
  }

  /**
   * Perform this realizable interaction
   * 
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  public void perform() throws InterruptedException, ClassNotFoundException {
    ((ConstrainedMultipleTargetsInteraction) this.getInteraction()).perform(
        this.getEnvironment(), this.getSource(), targets, this.constraints);
  }

}
