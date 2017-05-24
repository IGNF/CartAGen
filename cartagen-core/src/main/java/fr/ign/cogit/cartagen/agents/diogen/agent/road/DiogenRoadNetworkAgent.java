package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.MesoAgentState;

public class DiogenRoadNetworkAgent extends NetworkAgent
    implements IDiogenAgent {

  private int id;

  public DiogenRoadNetworkAgent(INetwork net) {
    super(net);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public AgentSatisfactionState activate() throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AgentLifeCycle getLifeCycle() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setLifeCycle(AgentLifeCycle lifeCycle) {
    // TODO Auto-generated method stub

  }

  @Override
  public double getSatisfaction() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setSatisfaction(double s) {
    // TODO Auto-generated method stub

  }

  @Override
  public void computeSatisfaction() {
    // TODO Auto-generated method stub

  }

  @Override
  public HashSet<Constraint> getConstraints() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateActionProposals() {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<ActionProposal> getActionProposals() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setActionsToTry(Set<ActionProposal> actionsToTry) {
    // TODO Auto-generated method stub

  }

  @Override
  public ActionProposal getBestActionProposal() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void cleanActionsToTry() {
    // TODO Auto-generated method stub

  }

  @Override
  public MesoAgentState buildCurrentState(AgentState previousState,
      Action action) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void goBackToState(AgentState state) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentState getRootState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setRootState(AgentState rootState) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getStatesNumber() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getStatesNumber(AgentState state) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void cleanStates() {
    // TODO Auto-generated method stub

  }

  @Override
  public void cleanSubTree(AgentState state) {
    // TODO Auto-generated method stub

  }

  @Override
  public void clean() {
    // TODO Auto-generated method stub

  }

  @Override
  public void printInfosConsole() {
    // TODO Auto-generated method stub

  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getBorderedEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public BorderStrategy getBorderStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Environment getEncapsulatedEnv() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getContainingEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    // TODO Auto-generated method stub

  }

}
