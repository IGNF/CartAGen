package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.town.StreetDensity;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * @author
 */
public class TownAgent extends MesoAgentGeneralisation<IBlockAgent>
    implements ITownAgent {
  @SuppressWarnings("unused")
  private static Logger logger = LogManager.getLogger(TownAgent.class.getName());

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public double getArea() {
    return this.getGeom().area();
  }

  @Override
  public IPolygon getInitialGeom() {
    return (IPolygon) super.getInitialGeom();
  }

  @Override
  public ITown getFeature() {
    return (ITown) super.getFeature();
  }

  /**
   * Blocks and road enrichments must have been created in the
   * GeneralisationDataSet jdd of the method for this constructor to build the
   * street network. Be careful as the deadEnds field is not filled by this
   * constructor.
   * @param town
   * @param jdd
   * @param id
   * @author GTouya
   */
  public TownAgent(ITown town, CartAGenDataSet jdd, int id) {
    super();
    this.setFeature(town);
    jdd.getTowns().add(town);
    this.setId(id);
  }

  /**
   * MAIN CONSTRUCTOR
   * @param town
   */
  public TownAgent(ITown town) {
    super();
    this.setFeature(town);
    // Links to the blocks
    List<IBlockAgent> blockAgents = new ArrayList<IBlockAgent>();
    for (IUrbanBlock block : town.getTownBlocks()) {
      IBlockAgent blockAgent = (IBlockAgent) AgentUtil
          .getAgentFromGeneObj(block);
      blockAgent.setMesoAgent(this);
      blockAgents.add(blockAgent);
    }
    this.setComponents(blockAgents);
    this.updateBlocks();
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (AgentSpecifications.TOWN_BLOCK_SATISFACTION) {
      this.ajouterContrainteSatisfactionComposants(
          AgentSpecifications.TOWN_BLOCK_SATISFACTION_IMP);
    }
    this.ajouterContrainteDensiteRues(1.0);
  }

  public void ajouterContrainteDensiteRues(double importance) {
    new StreetDensity(this, importance);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new TownAgent(
        CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
            .getGeneObjImpl().getCreationFactory().createTown(this.getGeom()),
        CartAGenDoc.getInstance().getCurrentDataset(), this.getId());
  }

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj) {
    // Do nothing as default
  }

  @Override
  public IFeatureCollection<DeadEndGroup> getDeadEnds() {
    return this.getFeature().getDeadEnds();
  }

  public void setDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds) {
    this.getFeature().setDeadEnds(deadEnds);
  }

  @Override
  public StreetNetwork getStreetNetwork() {
    return this.getFeature().getStreetNetwork();
  }

  public void setStreetNetwork(StreetNetwork net) {
    this.getFeature().setStreetNetwork(net);
  }

  @Override
  public void updateBlocks() {
    for (IBlockAgent ab : this.getComponents()) {
      BlockAgent meso = (BlockAgent) ab;
      IUrbanBlock block = meso.getFeature();
      // on vérifie l'état de cet ilot dans le réseau de rues
      // teste si le block a subi une agrégation
      if (block.getAggregLevel() == 0) {
        continue;
      }
      // arrived here, the block is the result of an aggregation
      // get the inside meso agents
      Set<IBlockAgent> blockAgents = new HashSet<IBlockAgent>();
      Set<IUrbanElementAgent> buildingAgents = new HashSet<IUrbanElementAgent>();
      for (IUrbanBlock inside : block.getInsideBlocks()) {
        IBlockAgent agent = (IBlockAgent) AgentUtil.getAgentFromGeneObj(inside);
        buildingAgents.addAll(agent.getComponents());
        blockAgents.add(agent);
      }

      // get the road agents for the aggregated block agent
      Set<ISectionAgent> blockRoadAgents = new HashSet<ISectionAgent>();
      for (INetworkSection section : block.getSurroundingNetwork()) {
        ISectionAgent agent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(section);
        blockRoadAgents.add(agent);
      }

      // build the new block agent
      new BlockAgent(block, this, buildingAgents, blockRoadAgents,
          block.getId());

      // eliminate the aggregated blocks
      for (IBlockAgent agent : blockAgents) {
        this.getComponents().remove(agent);
        agent.deleteAndRegister();
      }
    }
  }

}
