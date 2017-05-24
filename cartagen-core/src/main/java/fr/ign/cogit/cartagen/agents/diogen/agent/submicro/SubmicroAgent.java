package fr.ign.cogit.cartagen.agents.diogen.agent.submicro;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.ISubmicroGeneObj;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

public class SubmicroAgent extends CartAComAgentGeneralisation
    implements ISubmicroAgent {

  private boolean suppressed = false;

  public boolean isSuppressed() {
    return suppressed;
  }

  public void setSuppressed(boolean suppressed) {
    this.suppressed = suppressed;
  }

  public SubmicroAgent(ISubmicroGeneObj submicro) {
    super(submicro);
  }

  public ISubmicroGeneObj getFeature() {
    return (ISubmicroGeneObj) super.getFeature();
  }

  @Override
  public HashSet<Constraint> getConstraints() {
    HashSet<Constraint> toReturn = super.getConstraints();
    toReturn.addAll(
        ((ISubmicroGeneObj) this.getFeature()).getSubmicroConstraints());
    return toReturn;
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
