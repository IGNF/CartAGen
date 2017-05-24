/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.action;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * @author CDuchene
 * 
 */
public class InternalGeneralisationAction extends CartacomActionImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs an internal generalisation action for a given agent
   * @param actingAgent the agent concerned with the action
   * 
   */
  public InternalGeneralisationAction(IAgent actingAgent) {
    super((ICartacomAgent) actingAgent, null, 5);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * 
   */
  @Override
  public ActionResult compute() {
    ICartAComAgentGeneralisation agent = ((ICartAComAgentGeneralisation) this
        .getAgent());
    IGeographicAgent agentAgent = agent.getAgentAgent();
    if (agentAgent == null) {
      return ActionResult.UNCHANGED;
    }
    agentAgent.computeSatisfaction();
    double initialSatisfaction = agentAgent.getSatisfaction();

    agentAgent.run();

    if (agentAgent.getSatisfaction() == initialSatisfaction) {
      return ActionResult.UNCHANGED;
    } else {
      return ActionResult.MODIFIED;
    }
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

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
