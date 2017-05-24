package fr.ign.cogit.cartagen.agents.diogen.padawan;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentStateImpl;

/**
 * This AgentState class is a class specific for the Padawan based life cycle.
 * This class is an adapater for another AgentState. The specifity of a Padawan
 * Agent is to not use Action.
 * @author AMaudet
 * 
 */
public class PadawanAgentStateImpl extends AgentStateImpl
    implements PadawanAgentState {

  private AgentState state;

  private Interaction interaction;

  private double motivation;

  private PadawanAgentState previousState;

  public PadawanAgentStateImpl(AgentState state) {
    super(state.getAgent(), null, null);
    this.state = state;
  }

  public PadawanAgentStateImpl(AgentState state,
      PadawanAgentState previousState,
      RealizableConstrainedInteraction interaction) {
    super(previousState.getAgent(), previousState, null);
    this.previousState = previousState;
    this.interaction = interaction.getInteraction();
    this.motivation = interaction.getPreconditionsValue();
    this.state = state;
  }

  public AgentState getState() {
    return this.state;
  }

  @Override
  public IAgent getAgent() {
    return this.state.getAgent();
  }

  @Override
  public double getSatisfaction() {
    return this.state.getSatisfaction();
  }

  @Override
  public void setSatisfaction(double satisfaction) {
    this.state.setSatisfaction(satisfaction);
  }

  @Override
  public PadawanAgentState getPreviousState() {
    return this.previousState;
  }

  @Override
  public ArrayList<AgentState> getChildStates() {
    return this.state.getChildStates();
  }

  @Override
  public Action getAction() {
    return null;
  }

  @Override
  public Set<ActionProposal> getActionsToTry() {
    return null;
  }

  @Override
  public boolean isValid(double treshold) {
    return this.state.isValid(treshold);
  }

  @Override
  public void clean() {
    this.state.clean();
  }

  @Override
  public String toString() {
    if (this.getPreviousState() == null) {
      return "Intitial state S=" + this.getSatisfaction();
    }
    return this.interaction + "M= " + this.motivation + " & S="
        + this.getSatisfaction();
  }

  /**
   * @return
   */
  @Override
  public Map<String, Object> getValeursMesures() {
    return this.state.getValeursMesures();
  }

  /**
   * @return
   */
  @Override
  public Map<String, Integer> getApplicationsActions() {
    return this.state.getApplicationsActions();
  }

  @Override
  public void ajouteApplicationAction(String nomAction) {
    this.state.ajouteApplicationAction(nomAction);
  }

}
