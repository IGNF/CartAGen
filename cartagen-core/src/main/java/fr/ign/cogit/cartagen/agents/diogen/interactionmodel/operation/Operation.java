package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.operation;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public interface Operation {
  public void compute(Environment environment, IAgent source,
      Set<IAgent> targets, Map<String, GeographicConstraint> constraints);
}
