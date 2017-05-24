package fr.ign.cogit.cartagen.agents.diogen.constraint.points;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

public interface ForPointDisplacementConstraint extends GeographicConstraint {

  public void proposeDisplacement(IPointAgent p, double alpha);

}
