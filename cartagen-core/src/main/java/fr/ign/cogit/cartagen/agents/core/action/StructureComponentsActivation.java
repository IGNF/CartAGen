package fr.ign.cogit.cartagen.agents.core.action;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * A default meso action which consists in triggering its components
 * 
 * @author JGaffuri
 * 
 */
public class StructureComponentsActivation extends ActionCartagen {
  final static Logger logger = Logger
      .getLogger(StructureComponentsActivation.class.getName());

  /**
   * @param agent
   * @param cont
   * @param weight
   */
  public StructureComponentsActivation(IAgent agent, Constraint cont,
      double weight) {
    super(agent, cont, weight);
    if (!(agent instanceof InternStructureAgent)) {
      StructureComponentsActivation.logger.error(
          "Error in " + StructureComponentsActivation.class.getSimpleName()
              + ": the agent is not meso.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.ActionImpl#getAgent()
   */
  @Override
  public InternStructureAgent getAgent() {
    return (InternStructureAgent) super.getAgent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.ActionImpl#compute()
   */
  @Override
  public ActionResult compute() throws InterruptedException {

    // NB: to be enriched according to [Ruas99]: choose 'the best' agent to
    // trigger, legifere, control the result

    if (StructureComponentsActivation.logger.isDebugEnabled()) {
      StructureComponentsActivation.logger
          .debug("Components activation of  of " + this.getAgent());
    }

    // Get the components to trigger (the non-deleted ones)

    ArrayList<GeographicObjectAgent> components = new ArrayList<GeographicObjectAgent>();
    for (GeographicObjectAgent aGeo : this.getAgent().getComponents()) {
      if (!aGeo.getFeature().isDeleted()) {
        components.add(aGeo);
      }
    }

    int nb = components.size();
    int i = 1;

    while (components.size() > 0) {

      GeographicObjectAgent a = this.getAgent()
          .getBestComponentToActivate(components);
      if (StructureComponentsActivation.logger.isDebugEnabled()) {
        StructureComponentsActivation.logger
            .debug("Activation of " + a + " (" + i++ + "/" + nb + ")");
      }

      a.activate();
      this.getAgent().manageInternalSideEffects(a);
      components.remove(a);

    }

    if (StructureComponentsActivation.logger.isDebugEnabled()) {
      StructureComponentsActivation.logger
          .debug("End of activation of the components of " + this.getAgent());
    }
    return ActionResult.UNKNOWN;

  }

}
