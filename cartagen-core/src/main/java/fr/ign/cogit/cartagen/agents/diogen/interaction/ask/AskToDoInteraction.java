package fr.ign.cogit.cartagen.agents.diogen.interaction.ask;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.constraint.points.RoadNonOverlappingPoint;
import fr.ign.cogit.cartagen.agents.diogen.conversation.InteractionArgument;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedSingleTargetInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstraintType;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class AskToDoInteraction extends ConstrainedSingleTargetInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static Map<ConstrainedInteraction, AskToDoInteraction> singletonObjectsMap = new HashMap<ConstrainedInteraction, AskToDoInteraction>();;

  /**
   * A private Constructor prevents any other class from instantiating.
   * @return
   */
  protected static Class<?>[] signature = new Class[] { Environment.class,
      IAgent.class, IAgent.class, GeographicConstraint.class };

  private AskToDoInteraction(ConstrainedInteraction interaction) {
    ConstraintType constraintTypeObject = new ConstraintType(
        "fr.ign.cogit.cartagen.agentGeneralisation.padawan.constraint.points.RoadNonOverlappingPoint",
        1, 0);

    Method method = null;
    try {
      method = this.getClass().getMethod("isUnsatisfied", signature);
    } catch (SecurityException | NoSuchMethodException e) {
      e.printStackTrace();
    }

    constraintTypeObject.addInfluence(this.getValueFromInfluence("favorable"),
        method, "");

    Method method2 = null;
    try {
      method2 = this.getClass().getMethod("isNotSameTarget", signature);
    } catch (SecurityException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    constraintTypeObject.addInfluence(this.getValueFromInfluence("indifferent"),
        method2, "");
    this.addConstraintTypeName(constraintTypeObject);
    this.setWeight(2);
    singletonObjectsMap.put(interaction, this);

    this.setName("Ask to " + interaction.getName());
    // this.addConstraintTypeName(InteractionConfiguration.ELONGATION_CLASS_NAME);
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized AskToDoInteraction getInstance(
      ConstrainedInteraction interaction) {
    AskToDoInteraction askToDoInteraction = singletonObjectsMap
        .get(interaction);
    if (askToDoInteraction == null) {
      askToDoInteraction = new AskToDoInteraction(interaction);
    }
    return askToDoInteraction;
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      IDiogenAgent target, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {

    GeographicConstraint constraint = null;
    for (GeographicConstraint c : constraints) {
      if (c instanceof RoadNonOverlappingPoint && ((RoadNonOverlappingPoint) c)
          .getAgentSharingConstraint() == target) {
        constraint = c;
        break;
      }
    }

    ICartAComAgentGeneralisation cagSource = (ICartAComAgentGeneralisation) source;
    ICartAComAgentGeneralisation cagTarget = (ICartAComAgentGeneralisation) target;
    cagSource.initiateConversation(cagTarget, Performative.ASK_TO_DO,
        new InteractionArgument(this, (RoadNonOverlappingPoint) constraint,
            environment));
  }
}
