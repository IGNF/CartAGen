package fr.ign.cogit.cartagen.agents.diogen.agent.road;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentState;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentStateImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

public class CarryingRoadStrokeAgent extends NetworkSectionAgent
    implements ICarryingRoadStrokeAgent {

  public CarryingRoadStrokeAgent(IHikingRouteStroke obj) {
    super(obj);
  }

  @Override
  public IHikingRouteStroke getFeature() {
    return (IHikingRouteStroke) super.getFeature();
  }

  @Override
  public void goBackToState(CartacomMicroAgentState state) {
    // TODO Auto-generated method stub

  }

  @Override
  public double getSidesOrientation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void instantiateConstraints() {
    // TODO Auto-generated method stub

  }

  @Override
  public CartacomMicroAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new CartacomMicroAgentStateImpl(this,
        (CartacomMicroAgentState) previousState, action);
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
