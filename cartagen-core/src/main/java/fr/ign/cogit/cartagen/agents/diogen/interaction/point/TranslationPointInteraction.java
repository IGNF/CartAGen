package fr.ign.cogit.cartagen.agents.diogen.interaction.point;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class TranslationPointInteraction
    extends ConstrainedDegenerateInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static TranslationPointInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private TranslationPointInteraction() {
    super();
    this.setName("Translate Point");
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized TranslationPointInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new TranslationPointInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

}
