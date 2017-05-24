package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.micro.SquarringAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * 
 * @author AMaudet
 * 
 */
public class SquarringInteraction extends ConstrainedDegenerateInteraction {

  private static SquarringInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private SquarringInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.GRANULARITY_CLASS_NAME);
    // this.addConstraintTypeName(InteractionConfiguration.SQUARENESS_CLASS_NAME);
    this.setWeight(2);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized SquarringInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new SquarringInteraction();
    }
    return singletonObject;
  }

  /**
   * This method calls a SquarringAction. {@inheritDoc}
   */
  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    IMicroAgentGeneralisation csource = ((IMicroAgentGeneralisation) source);
    Action action = new SquarringAction(csource, null, 2.0,
        GeneralisationSpecifications.TOLERANCE_ANGLE, 500);
    setAction(action);
    action.compute();

  }

}
