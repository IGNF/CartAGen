package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * Interface for interaction using constraints.
 * @author AMaudet
 * 
 */
public interface ConstrainedInteraction extends Interaction {

  Set<GeographicConstraint> getConstraints(IDiogenAgent source);

  Set<GeographicConstraint> getConstraints(IDiogenAgent source,
      Set<IDiogenAgent> targets);

  Action getAction();

  String getName();

  String getDescription();

  Set<ConstraintType> getConstraintsTypeNameList();

  // int preconditionByConstraint(Environment environment, IAgent source,
  // Set<IAgent> targets, GeographicConstraint constraint,
  // Map<GeographicConstraint, Integer> constraintAdvicesMap);

}
