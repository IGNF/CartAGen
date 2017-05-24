package fr.ign.cogit.cartagen.agents.core.action.block;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.action.StructureActivationAction;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionProximity;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

public class BlockBuildingsDeletionProximityMultiCriterionAction
    extends ActionCartagen {

  public BlockBuildingsDeletionProximityMultiCriterionAction(BlockAgent ag,
      Constraint cont, double poids) {
    super(ag, cont, poids);
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    ArrayList<IUrbanElement> removedBuilds = BuildingsDeletionProximity
        .compute(((BlockAgent) this.getAgent()).getFeature());
    int compteur = 0;
    List<IUrbanElementAgent> removedBuildings = new ArrayList<IUrbanElementAgent>();
    for (IUrbanElement a : removedBuilds) {
      removedBuildings
          .add((IUrbanElementAgent) AgentUtil.getAgentFromGeneObj(a));
    }
    for (IUrbanElementAgent a : removedBuildings) {
      a.deleteAndRegister();
      compteur++;
      if (a.getAlignments().size() > 0) {
        break;
      }
    }
    if (compteur < removedBuildings.size()) {
      for (int i = compteur; i < removedBuildings.size(); i++) {
        removedBuildings.get(i).setDeleted(false);
      }
    }
    if (compteur > 0) {
      StructureActivationAction action = new StructureActivationAction(
          removedBuildings.get(compteur - 1), this.getConstraint(),
          this.getWeight());
      action.compute();
    }
    return ActionResult.UNKNOWN;
  }

}
