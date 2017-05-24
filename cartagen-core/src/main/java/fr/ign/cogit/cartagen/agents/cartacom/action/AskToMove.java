package fr.ign.cogit.cartagen.agents.cartacom.action;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.core.task.AggregableActionImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

/**
 * Cartacom Action class to ask to one or many neighbor agent to move in the
 * given limit zone number.
 * @author AMaudet
 * 
 */
public class AskToMove extends AggregableActionImpl {

  public AskToMove(ICartAComAgentGeneralisation agent,
      RelationalConstraint constraint, int limitZoneNumber,
      ISmallCompactAgent agentToAsk, double weight) {
    super(agent, constraint, weight);
    this.setLimitZoneNumber(limitZoneNumber);
  }

  /**
   * The number of the limit zone the agent should stay within during the
   * displacement.
   */
  private int limitZoneNumber;

  @Override
  public CartacomAction getAggregatedAction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object computeDescribingArgument() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FailureValidity computeFailureValidity() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean testAggregableWithAction(CartacomAction actionToTest) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    // TODO
    return null;
  }

  public int getLimitZoneNumber() {
    return this.limitZoneNumber;
  }

  public void setLimitZoneNumber(int limitZoneNumber) {
    this.limitZoneNumber = limitZoneNumber;
  }

  @Override
  public boolean isActItselfAction() {
    return false;
  }

}
