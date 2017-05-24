/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;

/**
 * @author JGaffuri
 * 
 */
public interface IRoadSectionAgent extends ISectionAgent {

  @Override
  public IRoadLine getFeature();

  public void ajouterContrainteNonSuperposition(double imp);
}
