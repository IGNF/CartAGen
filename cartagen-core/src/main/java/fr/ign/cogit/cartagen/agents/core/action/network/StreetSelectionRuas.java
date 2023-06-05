package fr.ign.cogit.cartagen.agents.core.action.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.ActionCartagen;
import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.algorithms.network.roads.EliminateTownDeadEnds;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class StreetSelectionRuas extends ActionCartagen {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = LogManager
      .getLogger(StreetSelectionRuas.class.getSimpleName());
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private StreetNetwork network;
  private double deadEndLength;
  private IFeatureCollection<DeadEndGroup> deadEnds;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public StreetSelectionRuas(IAgent agent, Constraint constraint, double weight,
      StreetNetwork network, double deadEndLength,
      IFeatureCollection<DeadEndGroup> deadEnds) {
    super(agent, constraint, weight);
    this.network = network;
    this.deadEndLength = deadEndLength;
    this.setDeadEnds(new FT_FeatureCollection<DeadEndGroup>(
        deadEnds.select(network.getGeom())));
  }

  // Getters and setters //
  public StreetNetwork getNetwork() {
    return this.network;
  }

  public void setNetwork(StreetNetwork network) {
    this.network = network;
  }

  public double getDeadEndLength() {
    return this.deadEndLength;
  }

  public void setDeadEndLength(double deadEndLength) {
    this.deadEndLength = deadEndLength;
  }

  public void setDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds) {
    this.deadEnds = deadEnds;
  }

  public IFeatureCollection<DeadEndGroup> getDeadEnds() {
    return this.deadEnds;
  }

  // Other public methods //
  @Override
  public ActionResult compute() throws InterruptedException {
    try {
      StreetSelectionRuas.logger.info("Computation of the street selection");
      this.network.limitedAggregationAlgorithm();
      StreetSelectionRuas.logger.debug("Dead end elimination");
      this.eliminateDeadEnds();
      StreetSelectionRuas.logger.debug("update the blocks of the town");
      ((ITownAgent) this.getAgent()).updateBlocks();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ActionResult.UNKNOWN;
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

  /**
   * <p>
   * Méthode qui marque les impasses qui sont éliminées par le processus. Cette
   * méthode 2 est issue de la thèse d'Anne Ruas : prend en compte la densité en
   * bâtiments de sa situation et un seuil de longueur (utilise le champ
   * deadEndLength).
   * 
   */
  private void eliminateDeadEnds() {
    EliminateTownDeadEnds algo = new EliminateTownDeadEnds(this.deadEnds,
        this.network, this.deadEndLength);
    algo.execute();
  }
}
