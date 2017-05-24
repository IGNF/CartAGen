package fr.ign.cogit.cartagen.agents.diogen.agent.urban;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.environment.PolylinearEnvironment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.schema.BuildingsBorder;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;

/**
 * Agent for border between two buildings.
 * @author AMaudet
 * 
 */
public class BuildingsBorderAgent extends GeographicObjectAgentGeneralisation
    implements IBuildingsBorderAgent, IDiogenAgent {

  public BuildingsBorderAgent(BuildingsBorder border) {
    super();
    setFeature(border);
  }

  public IPointAgent getBeginPointAgent() {
    PolylinearEnvironment environment = (PolylinearEnvironment) this
        .getEncapsulatedEnv();
    return environment.getEdgePointAgentWithCurrentPosition(
        this.getFeature().getGeom().coord().get(0));
  }

  public IPointAgent getEndPointAgent() {
    PolylinearEnvironment environment = (PolylinearEnvironment) this
        .getEncapsulatedEnv();
    return environment.getEdgePointAgentWithCurrentPosition(this.getFeature()
        .getGeom().coord().get(this.getFeature().getGeom().coord().size() - 1));
  }

  @Override
  public void instantiateConstraints() {
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
