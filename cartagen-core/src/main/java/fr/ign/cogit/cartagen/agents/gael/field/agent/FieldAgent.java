/*
 * Créé le 2 juil. 2005
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentState;
import fr.ign.cogit.cartagen.agents.gael.field.FieldAgentState;
import fr.ign.cogit.cartagen.agents.gael.field.FieldAgentStateImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentState;

/**
 * Field agent class
 * 
 * @author julien Gaffuri
 * 
 */
public abstract class FieldAgent extends GeographicAgentGeneralisation {
  // private static Logger logger = LogManager.getLogger(FieldAgent.class);

  public FieldAgent() {
    super();
  }

  @Override
  public GeographicAgentState buildCurrentState(AgentState etatPrecedent,
      Action action) {
    return new FieldAgentStateImpl(this, (FieldAgentState) etatPrecedent,
        action);
  }

  @Override
  public void goBackToState(AgentState eag) {
    super.goBackToState(eag);

    FieldAgentState eac = (FieldAgentState) eag;

    // retrouve l'etat des points
    for (PointAgentState eap : eac.getEtatAgentPoints()) {
      eap.getAgent().goBackToState(eap);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.agentgeneralisation.agent.
   * GeographicAgentGeneralisation#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
    // System.out.println("Field: " + getF);
  }

  @Override
  public void registerDisplacement() {
    // TODO Auto-generated method stub
  }

}
