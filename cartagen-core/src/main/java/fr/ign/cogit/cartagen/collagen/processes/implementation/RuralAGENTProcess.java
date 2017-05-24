package fr.ign.cogit.cartagen.collagen.processes.implementation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.processes.model.StoppableProcess;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.defaultschema.urban.Town;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class RuralAGENTProcess extends GeneralisationProcess
    implements StoppableProcess {

  private static Logger LOGGER = Logger.getLogger(RuralAGENTProcess.class);

  private AgentObserver observer;

  public RuralAGENTProcess(Conductor chefO, AgentObserver observer) {
    super(chefO);
    this.observer = observer;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

    // trigger the road enrichment first
    NetworkEnrichment.buildTopology(dataset, dataset.getRoadNetwork(), false);

    // then, run the process on all block agents
    // initialisation
    AgentGeneralisationScheduler.getInstance().initList();
    /*
     * for (IUrbanBlock block : dataset.getBlocks()) {
     * GeographicAgentGeneralisation ago = AgentUtil.getAgentFromGeneObj(block);
     * if (ago == null) { continue; }
     * AgentGeneralisationScheduler.getInstance().add(ago);
     * 
     * // run generalisation
     * AgentGeneralisationScheduler.getInstance().activate(); }
     */
    // create a town feature from the space geometry
    ITown town = new Town((IPolygon) space.getGeom());
    // create the blocks in the town
    // remplit carte topo avec les troncons
    CarteTopo carteTopo = new CarteTopo("cartetopo");
    /*
     * for (INetwork res : UrbanEnrichment.getStructuringNetworks(dataset)) { if
     * (res.getSections().size() > 0) {
     * carteTopo.importClasseGeo(res.getSections(), true); } }
     */
    // blocks are made only with roads to avoid making planar the hydro-road
    // graph
    carteTopo.importClasseGeo(dataset.getRoads(), true);
    StreetNetwork net = new StreetNetwork(town.getGeom(), dataset.getRoads(),
        new FT_FeatureCollection<IRoadStroke>(),
        new FT_FeatureCollection<IRoundAbout>(),
        new FT_FeatureCollection<IBranchingCrossroad>(),
        new FT_FeatureCollection<IDualCarriageWay>(),
        new FT_FeatureCollection<IUrbanBlock>());
    town.setStreetNetwork(net);

    // remplit carte topo avec limites de villes
    IFeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
    DefaultFeature contour = new DefaultFeature();
    contour.setGeom((town.getGeom()).exteriorLineString());
    contours.add(contour);

    carteTopo.importClasseGeo(contours, true);
    // Set infinite face to true for face creation, because of a bug if not set.
    // Intended to be removed when the bug is corrected
    carteTopo.setBuildInfiniteFace(true);
    carteTopo.creeNoeudsManquants(1.0);
    carteTopo.fusionNoeuds(1.0);
    carteTopo.filtreArcsDoublons();
    carteTopo.rendPlanaire(1.0);
    carteTopo.fusionNoeuds(1.0);
    carteTopo.filtreArcsDoublons();
    carteTopo.creeTopologieFaces();

    carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);
    UrbanEnrichment.buildBlocksInTown(town, dataset, carteTopo, false,
        dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory());
    dataset.getTowns().add(town);
    // Lien de la ville avec ses impasses
    HashSet<DeadEndGroup> deadEnds = DeadEndGroup
        .buildFromRoads(dataset.getRoads(), town.getGeom(), carteTopo);
    IFeatureCollection<DeadEndGroup> deadEndColl = new FT_FeatureCollection<DeadEndGroup>();
    for (DeadEndGroup deadEnd : deadEnds) {
      deadEndColl.add(deadEnd);
    }
    town.setDeadEnds(deadEndColl);
    LOGGER.debug("town created");

    // first, create AGENT agents in the space
    AgentUtil.createAgentAgentsInArea(dataset, (IPolygon) space.getGeom());
    LOGGER.debug("agents created");

    observer.setSlowMotion(true);
    TreeExplorationLifeCycle.getInstance().attach(observer);
    for (IUrbanBlock block : town.getTownBlocks()) {
      LOGGER.debug("block generalised: " + block.getId());
      AgentGeneralisationScheduler.getInstance().initList();
      if (block.getUrbanElements().size() == 0) {
        LOGGER.debug("no building in block");
        continue;
      }
      GeographicAgentGeneralisation ago = AgentUtil.getAgentFromGeneObj(block);
      ((BlockAgent) ago).setGrayingNecessary(false);
      LOGGER.debug("block agent activated");
      AgentGeneralisationScheduler.getInstance().add(ago);
      AgentGeneralisationScheduler.getInstance().activate();
    }
    dataset.getTowns().remove(town);
  }

  @Override
  public String getName() {
    return "Urban AGENT";
  }

  @Override
  public void updateProcess(Map<IGeneObj, IGeometry> objetsModifies,
      Collection<IGeneObj> objetsElimines) {
    // TODO Auto-generated method stub

  }

  @Override
  public void resumeProcess() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isStopped() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setStop(boolean arrete) {
    // TODO Auto-generated method stub

  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }
}
