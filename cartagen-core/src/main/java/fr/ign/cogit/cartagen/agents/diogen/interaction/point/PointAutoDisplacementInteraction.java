package fr.ign.cogit.cartagen.agents.diogen.interaction.point;

import java.lang.reflect.Method;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interaction.aggregation.AggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedDegenerateInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedMultipleTargetsInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstraintType;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public class PointAutoDisplacementInteraction
    extends ConstrainedDegenerateInteraction implements AggregableInteraction {

  /**
   * The singleton object for the unique instance of the class.
   */
  private static PointAutoDisplacementInteraction singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private PointAutoDisplacementInteraction() {
    this.addConstraintTypeName(getConstraintType(
        "fr.ign.cogit.cartagen.agentGeneralisation.gael.gaelDeformation.constraint.simple.singletonPoint.Position"));
    this.setName("Point Displacement without target");
    this.setWeight(2);

  }

  private ConstraintType getConstraintType(String s) {
    ConstraintType constraintTypeObject = new ConstraintType(s, 1, 0);
    Method method = null;
    try {
      method = this.getClass().getMethod("isUnsatisfied", signature);
    } catch (SecurityException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    this.addConstraintTypeName(constraintTypeObject);
    constraintTypeObject.addInfluence(this.getValueFromInfluence("favorable"),
        method, "");

    return constraintTypeObject;
  }

  /**
   * Get the unique instance.
   * @return
   */
  public static synchronized PointAutoDisplacementInteraction getInstance() {
    if (singletonObject == null) {
      singletonObject = new PointAutoDisplacementInteraction();
    }
    return singletonObject;
  }

  @Override
  public boolean testAggregableWithInteraction(
      AggregableInteraction aggregableInteraction) {
    return (aggregableInteraction == this
        || aggregableInteraction == PointDisplacementAggregableInteraction
            .getInstance());
  }

  @Override
  public ConstrainedMultipleTargetsInteraction getAggregatedInteraction() {
    return PointDisplacementInteraction.getInstance();
  }

  @Override
  public void perform(Environment environment, IDiogenAgent source,
      Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

}
