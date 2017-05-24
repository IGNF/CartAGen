package fr.ign.cogit.cartagen.agents.core.action;

import java.util.ArrayList;

import org.apache.log4j.Logger;

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
public class MesoComponentsActivation<ComponentClass extends GeographicObjectAgent>
    extends ActionCartagen {
  final static Logger logger = Logger
      .getLogger(MesoComponentsActivation.class.getName());

  /**
   * @param agent
   * @param cont
   * @param weight
   */
  public MesoComponentsActivation(IAgent agent, Constraint cont,
      double weight) {
    super(agent, cont, weight);
    if (!(agent instanceof MesoAgent<?>)) {
      MesoComponentsActivation.logger
          .error("Error in " + MesoComponentsActivation.class.getSimpleName()
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

    if (MesoComponentsActivation.logger.isDebugEnabled()) {
      MesoComponentsActivation.logger
          .debug("Components activation of  of " + this.getAgent());
    }

    // Get the components to trigger (the non-deleted ones)

    ArrayList<ComponentClass> components = new ArrayList<ComponentClass>();
    for (ComponentClass aGeo : this.getAgent().getComponents()) {
      if (!aGeo.getFeature().isDeleted()) {
        components.add(aGeo);
      }
    }

    int nb = components.size();
    int i = 1;

    while (components.size() > 0) {

      GeographicObjectAgent a = this.getAgent()
          .getBestComponentToActivate(components);
      if (MesoComponentsActivation.logger.isDebugEnabled()) {
        MesoComponentsActivation.logger
            .debug("Activation of " + a + " (" + i++ + "/" + nb + ")");
      }

      if (a.getStructureAgents().size() == 0) {
        a.activate();
        this.getAgent().manageInternalSideEffects(a);
        components.remove(a);
      }

      else {
        StructureActivationAction action = new StructureActivationAction(a,
            this.getConstraint(), this.getWeight());
        action.compute();
        for (InternStructureAgent structure : a.getStructureAgents()) {
          for (GeographicObjectAgent agent : structure.getComponents()) {
            components.remove(agent);
          }
        }
      }

    }

    if (MesoComponentsActivation.logger.isDebugEnabled()) {
      MesoComponentsActivation.logger
          .debug("End of activation of the components of " + this.getAgent());
    }
    return ActionResult.UNKNOWN;

  }

}
