package fr.ign.cogit.cartagen.agents.core.action;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * A default meso action which consists in triggering its components
 * 
 * @author JGaffuri
 * 
 */
public class InternStructuresActivation<ComponentClass extends GeographicObjectAgent>
    extends ActionCartagen {
  final static Logger logger = LogManager
      .getLogger(InternStructuresActivation.class.getName());

  /**
   * @param agent
   * @param cont
   * @param weight
   */
  public InternStructuresActivation(IAgent agent, Constraint cont,
      double weight) {
    super(agent, cont, weight);
    if (!(agent instanceof MesoAgent<?>)) {
      InternStructuresActivation.logger
          .error("Error in " + InternStructuresActivation.class.getSimpleName()
              + ": the agent is not meso.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.ActionImpl#getAgent()
   */
  @SuppressWarnings("unchecked")
  @Override
  public MesoAgent<ComponentClass> getAgent() {
    return (MesoAgent<ComponentClass>) super.getAgent();
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

    if (InternStructuresActivation.logger.isDebugEnabled()) {
      InternStructuresActivation.logger
          .debug("Components activation of  of " + this.getAgent());
    }

    // Get the components to trigger (the non-deleted ones)

    ArrayList<InternStructureAgent> components = new ArrayList<InternStructureAgent>();
    for (InternStructureAgent aGeo : this.getAgent().getInternStructures()) {
      if (!aGeo.getFeature().isDeleted()) {
        components.add(aGeo);
      }
    }

    int nb = this.getAgent().getInternStructures().size();
    int i = 1;

    while (components.size() > 0) {

      InternStructureAgent a = this.getAgent()
          .getBestInternStructureToActivate(components);
      if (InternStructuresActivation.logger.isDebugEnabled()) {
        InternStructuresActivation.logger
            .debug("Activation of " + a + " (" + i++ + "/" + nb + ")");
      }

      a.activate();
      this.getAgent().manageInternalSideEffects(a);
      components.remove(a);

    }

    if (InternStructuresActivation.logger.isDebugEnabled()) {
      InternStructuresActivation.logger
          .debug("End of activation of the components of " + this.getAgent());
    }
    return ActionResult.UNKNOWN;

  }

}
