package fr.ign.cogit.cartagen.agents.core.action.block;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.action.StructureActivationAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SimplificationAction;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionCongestion;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author julien Gaffuri 4 sept. 2007
 * 
 */
public class BlockBuildingsDeletionCongestionAction extends ActionCartagen {

  static Logger logger = LogManager.getLogger(SimplificationAction.class.getName());
  /**
   */
  private int nbLimite;
  /**
   */
  private double distanceMax;
  private double rate;

  public BlockBuildingsDeletionCongestionAction(BlockAgent ag, Constraint cont,
      int nbLimite, double distanceMax, double rate, double poids) {
    super(ag, cont, poids);
    this.nbLimite = nbLimite;
    this.distanceMax = distanceMax;
    this.rate = rate;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    String message = "Algo BuildingDeletionCongestion: agent = "
        + this.getAgent() + " , nbLimite = " + this.nbLimite
        + " , distanceMax = " + this.distanceMax + " , rate = " + this.rate
        + ".";
    BlockBuildingsDeletionCongestionAction.logger.debug(message);
    ArrayList<IUrbanElement> removedBuilds = BuildingsDeletionCongestion
        .compute(((IBlockAgent) this.getAgent()).getFeature(), this.nbLimite,
            this.distanceMax, this.rate);
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
