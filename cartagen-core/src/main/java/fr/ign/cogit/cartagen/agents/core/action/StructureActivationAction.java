package fr.ign.cogit.cartagen.agents.core.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * 
 * @author JRenard
 * 
 */
public class StructureActivationAction extends ActionCartagen {
  final static Logger logger = Logger
      .getLogger(StructureActivationAction.class.getName());

  public StructureActivationAction(GeographicObjectAgent ag, Constraint cont,
      double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {

    if (StructureActivationAction.logger.isDebugEnabled()) {
      StructureActivationAction.logger
          .debug("Components activation of structure " + this.getAgent());
    }

    // Get the components to trigger (the non-deleted ones)

    List<InternStructureAgent> structures = new ArrayList<InternStructureAgent>();
    for (InternStructureAgent aGeo : ((GeographicObjectAgent) this.getAgent())
        .getStructureAgents()) {
      if (!aGeo.getFeature().isDeleted()) {
        structures.add(aGeo);
      }
    }

    while (structures.size() > 0) {
      InternStructureAgent a = getBiggestStructure(structures);
      a.activate();
      structures.remove(a);
    }
    return ActionResult.UNKNOWN;
  }

  private InternStructureAgent getBiggestStructure(
      List<InternStructureAgent> structures) {
    if (structures.size() == 0)
      return null;
    if (structures.size() == 1)
      return structures.get(0);
    InternStructureAgent biggestAlignment = structures.get(0);
    int nbMax = 0;
    for (InternStructureAgent aa : structures) {
      int nb = aa.getComponents().size();
      if (nb > nbMax) {
        nbMax = nb;
        biggestAlignment = aa;
      }
    }
    return biggestAlignment;
  }

}
