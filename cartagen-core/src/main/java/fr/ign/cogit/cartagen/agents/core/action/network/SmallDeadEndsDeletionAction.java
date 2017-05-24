package fr.ign.cogit.cartagen.agents.core.action.network;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author JGaffuri
 * 
 */
public class SmallDeadEndsDeletionAction extends ActionCartagen {
  // private static Logger logger =
  // Logger.getLogger(SuppressionPetitesImpasses.class.getName());
  /**
   */
  private final double longueurMin;

  public SmallDeadEndsDeletionAction(NetworkAgent ag, Constraint cont,
      double poids, double longueurMin) {
    super(ag, cont, poids);
    this.longueurMin = longueurMin;
  }

  @Override
  public ActionResult compute() {
    NetworkAgent res = (NetworkAgent) this.getAgent();

    // supprime les impasses
    NetworkEnrichment.supprimerImpasses(res.getFeature(), this.longueurMin);

    // agrege les troncons analogues adjacents
    NetworkEnrichment.aggregateAnalogAdjacentSections(
        CartAGenDoc.getInstance().getCurrentDataset(), res.getFeature());
    return ActionResult.UNKNOWN;
  }

}
