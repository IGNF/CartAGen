package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.action.micro.SimplificationAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Granularity;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * SimplificationInteraction simplie the shape of an object.
 * 
 * @author AMaudet
 * 
 */
public class SimplificationInteraction
    extends ConstrainedDegenerateInteraction {

  private static SimplificationInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private SimplificationInteraction() {
    super();
    loadSpecification();
    // this.addConstraintTypeName(InteractionConfiguration.GRANULARITY_CLASS_NAME);
    this.setWeight(5);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized SimplificationInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new SimplificationInteraction();
    }
    return singletonObject;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    IMicroAgentGeneralisation csource = ((IMicroAgentGeneralisation) source);
    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof Granularity) {
        constraint = c;
        break;
      }
    }
    Action action = new SimplificationAction(csource, constraint, 5.0,
        GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE
            * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
    setAction(action);
    action.compute();

  }

}
