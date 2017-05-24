/*
 * Créé le 13 sept. 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * A point agent state
 * 
 * @author JGaffuri
 * 
 */
public interface PointAgentState extends AgentState {

  public double getX();

  public double getY();

  public GAELLinkedFeatureState getLinkedFeatureState();

}
