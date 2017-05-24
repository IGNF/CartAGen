package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * Interface for interaction of the IODA/PADAWAN model. the same interaction.
 * http://www.lifl.fr/SMAC/projects/ioda/
 * 
 * @author AMaudet
 * 
 */
public interface Interaction {

  public double trigger(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap);

  public int preconditions(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> targets, Set<GeographicConstraint> constraints,
      Map<GeographicConstraint, Integer> constraintAdvicesMap)
      throws ClassNotFoundException;

  public void perform(Environment environment, IDiogenAgent source,
      Set<IDiogenAgent> target, Set<GeographicConstraint> constraints)
      throws InterruptedException, ClassNotFoundException;

}
