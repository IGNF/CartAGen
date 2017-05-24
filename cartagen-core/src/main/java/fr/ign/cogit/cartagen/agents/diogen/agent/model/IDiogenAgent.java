/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

public interface IDiogenAgent extends IAgent {

  /**
   * Integration of the PADAWAN model: {@code this} can border some
   * environments.
   * @return
   */
  Set<Environment> getBorderedEnvironments();

  /**
   * Integration of the PADAWAN model: setter for the bordered environments.
   * @return
   */
  void setBorderedEnvironments(Set<Environment> borderedEnvironments);

  /**
   * Integration of the PADAWAN model: method to add bordered environments.
   * @return
   */
  void addBorderedEnvironment(Environment borderedEnvironment);

  /**
   * Integration of the PADAWAN model: method to remove bordered environments.
   * @return
   */
  void removeBorderedEnvironment(Environment borderedEnvironment);

  /**
   * The strategy design pattern is used to differiantate behaviours of agents
   * being borders and the others.
   * @return
   */
  BorderStrategy getBorderStrategy();

  /**
   * Setter for the BorderStrategy: strategy design pattern that is used to
   * differiantate behaviours of agents being borders and the others.
   * @return
   */
  void setBorderStrategy(BorderStrategy borderStrategy);

  /**
   * Activate the agent in a specific environment.
   * 
   * @param environment
   * @return
   * @throws InterruptedException
   */
  AgentSatisfactionState activate(Environment environment)
      throws InterruptedException;

  /**
   * Integration of the PADAWAN model: agents may encapsulate an environment
   * that contains agents.
   * @return
   */
  Environment getEncapsulatedEnv();

  /**
   * The setter for the encapsulated environment of the agent.
   * @param encapsulatedEnv
   */
  void setEncapsulatedEnv(Environment encapsulatedEnv);

  /**
   * Integration of the PADAWAN model: agents may be contained by environments.
   * @return
   */
  Set<Environment> getContainingEnvironments();

  /**
   * Integration of the PADAWAN model: update the reference as {@code this} is
   * not contained by the containingEnvironment anymore.
   * @return
   */
  void removeContainingEnvironments(Environment containingEnvironment);

  /**
   * Integration of the PADAWAN model: update the reference as {@code this} is
   * now contained by the containingEnvironment.
   * @return
   */
  void addContainingEnvironments(Environment containingEnvironment);

  /**
   * Integration of the PADAWAN model: setter for the containing environments of
   * {@code this}.
   * @return
   */
  void setContainingEnvironments(Set<Environment> containingEnvironments);

  /**
   * Integration of the PADAWAN model: The strategy design pattern is used to
   * differiantate behaviours of agents encapsulating environments and the
   * others.
   * @return
   */
  EnvironmentStrategy getEnvironmentStrategy();

  /**
   * Setter for the EnvironmentStrategy: strategy design pattern that is used to
   * differiantate behaviours of agents encapsulating environments and the
   * others.
   * @return
   */
  void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy);
}
