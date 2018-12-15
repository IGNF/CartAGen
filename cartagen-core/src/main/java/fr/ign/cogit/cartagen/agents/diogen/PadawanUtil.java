package fr.ign.cogit.cartagen.agents.diogen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkNodeAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.TownAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.hydro.CoastLineAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.hydro.DiogenHydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.hydro.ICoastLineAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.hydro.RiverSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.hydro.WaterAreaAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.EmbeddedEnvironmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IGeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.CarryingRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.CarryingRoadStrokeAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.DiogenRoadNetworkAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.ICarryingRoadStrokeAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.RoadNeighbourhoodAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.road.RoadSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.AngleSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.TriangleSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.BuildingsBorderAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.urban.IBuildingsBorderAgent;
import fr.ign.cogit.cartagen.agents.diogen.algorithms.IdentifyRoadNeighbourhood;
import fr.ign.cogit.cartagen.agents.diogen.constraint.points.BalanceConstraint;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.HikingDataset;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;
import fr.ign.cogit.cartagen.agents.diogen.interaction.ask.AskToDoInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.point.PointDisplacementAggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact.ConstrainedZoneDrivenDisplacementZone1Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact.ConstrainedZoneDrivenDisplacementZone2Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.smallcompact.ConstrainedZoneDrivenDisplacementZone3Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.AssignationImpl;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.lifecycle.PadawanAdvancedLifeCycle;
import fr.ign.cogit.cartagen.agents.diogen.lifecycle.PadawanTreeExplorationCycle;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.schema.BuildingsBorder;
import fr.ign.cogit.cartagen.agents.diogen.schema.EmbeddedDeadEndArea;
import fr.ign.cogit.cartagen.agents.diogen.schema.IEmbeddedDeadEndArea;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.angle.Value;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Length;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment.Orientation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.singletonpoint.Position;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELAngleGeneObj;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELSegmentGeneObj;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELTriangleGeneObj;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.BasicLifeCycle;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * PadawanUtil provides static methods for the creations of new IODA agent based
 * on geographic objects.
 * @author AMaudet
 * 
 */
public class PadawanUtil {

  private static AgentLifeCycle LIFE_CYCLE = PadawanAdvancedLifeCycle
      .getInstance();

  public static AgentLifeCycle getLIFE_CYCLE() {
    return LIFE_CYCLE;
  }

  public static void changeLifeCycleToIODA() {
    PadawanUtil.LIFE_CYCLE = PadawanAdvancedLifeCycle.getInstance();
    PadawanUtil.LIFE_CYCLE.setStoreStates(true);
  }

  public static void changeLifeCycleToBasic() {
    PadawanUtil.LIFE_CYCLE = BasicLifeCycle.getInstance();
    PadawanUtil.LIFE_CYCLE.setStoreStates(true);
  }

  public static void changeLifeCycleToIODATree() {
    PadawanUtil.LIFE_CYCLE = PadawanTreeExplorationCycle.getInstance();
    PadawanUtil.LIFE_CYCLE.setStoreStates(true);
  }

  /**
   * Create IODA agents based on the objects of dataset
   * 
   * @param dataset
   */
  public static void createAgentsInDataset1(CartAGenDataSet dataset) {
    /*
     * ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
     * true); progressFrame.setVisible(true);
     */

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    Environment global = PadawanUtil.createGlobalEnvironment();

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 33);
    PadawanUtil.createRoadNetworkAgentsInDataset(dataset, global);
    PadawanUtil.createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 67);
    PadawanUtil.createUrbanAgentsInDataset(dataset, global);

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset
   * 
   * @param dataset
   */
  public static void createAgentsInDataset2(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    Environment global = PadawanUtil.createGlobalEnvironment();

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 25);
    PadawanUtil.createRoadNetworkAgentsInDataset(dataset, global);

    PadawanUtil.createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);
    PadawanUtil.createUrbanAgentsInDataset(dataset, global, false);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 75);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    createNetworkFacesAgentsInDataset(dataset);

    PadawanUtil.createWaterAreasAgentsInDataset(dataset, global);

    PadawanUtil.instantiateRelationalConstraints(dataset);

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset
   * 
   * @param dataset
   */
  public static void createAgentsInDataset3(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    Environment global = PadawanUtil.createGlobalEnvironment();

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 33);
    PadawanUtil.createRoadNetworkAgentsInDataset(dataset, global);

    PadawanUtil.createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 67);
    PadawanUtil.createUrbanAgentsInDataset(dataset, global);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 80);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    createNetworkFacesAgentsInDataset(dataset);

    PadawanUtil.instantiateRelationalConstraints(dataset);

    // progressFrame.setTextAndValue("Creation of agents complete!", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset
   * 
   * @param dataset
   */
  public static void createAgentsInDataset4(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    Environment global = PadawanUtil.createGlobalEnvironment();
    Class<? extends IAgent> sourceAgent = CarryingRoadStrokeAgent.class;
    Class<? extends IAgent> targetAgent = GeographicPointAgent.class;

    global.getInteractionMatrix().addSingleTargetAssignation(sourceAgent,
        targetAgent,
        new AssignationImpl<ConstrainedInteraction>(
            AskToDoInteraction.getInstance(
                PointDisplacementAggregableInteraction.getInstance())));

    global.getInteractionMatrix().addSingleTargetAssignation(targetAgent,
        sourceAgent, new AssignationImpl<ConstrainedInteraction>(
            PointDisplacementAggregableInteraction.getInstance()));

    createHikingStrokeAgentsFromDataset(dataset, global);
    createCoastLineAgentsFromDataset(dataset, global);
    addPointAgentsToEnvironment(dataset.getCoastlines(), global);
    createSubmicroAgents(dataset.getCoastlines());

    PadawanAgentInitialisation
        .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
            ((HikingDataset) dataset).getTouristRouteStroke(),
            dataset.getCoastlines());

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset for building
   * displacement according to routes experiment
   * 
   * @param dataset
   */
  public static void createAgentsInDataset5(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    // Create interactions matrices
    Environment global = PadawanUtil.createGlobalEnvironment();

    createRoadNetworkAgentsInDataset(dataset, global);
    createHydroNetworkCartacomAgentsInDataset(dataset, global);

    for (IBuilding build : dataset.getBuildings()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
        BuildingAgent agent = PadawanUtil.createIODABuildingAgent(build,
            global);
        agent.instantiateConstraints();
      }
    }

    // createNetworkFacesAgentsInDataset(dataset);
    instantiateRelationalConstraints(dataset);

    createRoadNeighbourhoodAgentsFromDataset(dataset, global);
    // addPointAgentsToEnvironment(dataset.getCoastlines(), global);
    // createSubmicroAgents(dataset.getCoastlines());

    // PadawanAgentInitialisation
    // .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
    // ((CdBDataset) dataset).getTouristRouteStroke(),
    // dataset.getCoastlines());

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;

    Class<? extends IAgent> buildingAgent = BuildingAgent.class;
    Class<? extends IAgent> roadAgent = RoadSectionAgent.class;

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        buildingAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone1Interaction.getInstance()));

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        buildingAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone2Interaction.getInstance()));

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        buildingAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone3Interaction.getInstance()));

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        roadAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone1Interaction.getInstance()));

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        roadAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone2Interaction.getInstance()));

    global.getInteractionMatrix().addSingleTargetAssignation(buildingAgent,
        roadAgent, new AssignationImpl<ConstrainedInteraction>(
            ConstrainedZoneDrivenDisplacementZone3Interaction.getInstance()));

  }

  /**
   * Create IODA agents based on the objects of dataset for deformable
   * deformations according to route positions alongside roads experiments.
   * 
   * @param dataset
   */
  public static void createAgentsInDataset6(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    // Create interactions matrices
    Environment global = PadawanUtil.createGlobalEnvironment();

    Class<? extends IAgent> sourceAgent = CarryingRoadStrokeAgent.class;
    Class<? extends IAgent> targetAgent = GeographicPointAgent.class;

    global.getInteractionMatrix().addSingleTargetAssignation(sourceAgent,
        targetAgent,
        new AssignationImpl<ConstrainedInteraction>(
            AskToDoInteraction.getInstance(
                PointDisplacementAggregableInteraction.getInstance())));

    global.getInteractionMatrix().addSingleTargetAssignation(targetAgent,
        sourceAgent, new AssignationImpl<ConstrainedInteraction>(
            PointDisplacementAggregableInteraction.getInstance()));

    // create roads agents
    createRoadNetworkAgentsInDataset(dataset, global);
    // create
    createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // create relationnal constraints
    // for each point agent, create a relational constraint with the stroke if
    // they may be under a hiking symbol

    createHikingStrokeAgentsFromDataset(dataset, global);

    // for (IPointAgent agent : getPointsAgentsFromPopulation(dataset
    // .getWaterLines())) {
    // System.out.println("Agent " + agent);
    // }

    addPointAgentsToEnvironment(dataset.getWaterLines(), global);
    addPointAgentsToEnvironment(dataset.getRoads(), global);

    createSubmicroAgents(dataset.getWaterLines());
    createSubmicroAgents(dataset.getRoads());

    PadawanAgentInitialisation
        .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
            ((HikingDataset) dataset).getTouristRouteStroke(),
            dataset.getWaterLines());

    PadawanAgentInitialisation
        .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
            ((HikingDataset) dataset).getTouristRouteStroke(),
            dataset.getRoads());

    // for (IBuilding build : dataset.getBuildings()) {
    // if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
    // BuildingAgent agent = PadawanUtil
    // .createIODABuildingAgent(build, global);
    // agent.instantiateConstraints();
    // }
    // }

    // create hydro network agent
    // createRoadNetworkAgentsInDataset(dataset, global);

    // create point agents

    // create strokes agent (or road).

    // create relations between agents. Need a cartacom relation between point
    // and stroke (like coastline).

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset for homothetie
   * experiment
   * 
   * @param dataset
   */
  public static void createAgentsInDataset7(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    // Create interactions matrices
    Environment global = PadawanUtil.createGlobalEnvironment();

    createRoadNetworkAgentsInDataset(dataset, global);

    for (IBuilding build : dataset.getBuildings()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
        BuildingAgent agent = PadawanUtil.createIODABuildingAgent(build,
            global);
        agent.instantiateConstraints();
      }
    }

    createRoadNeighbourhoodAgentsFromDataset(dataset, global);

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset for building
   * displacement according to routes experiment, using cartacom positions
   * 
   * @param dataset
   */
  public static void createAgentsInDataset8(CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    // Create interactions matrices
    Environment global = PadawanUtil.createGlobalEnvironment();

    PadawanUtil.createRoadNetworkAgentsInDataset(dataset, global);

    PadawanUtil.createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);

    for (IBuilding build : dataset.getBuildings()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
        BuildingAgent agent = PadawanUtil.createIODABuildingAgent(build,
            global);
        agent.instantiateConstraints();
      }
    }

    // PadawanUtil.createUrbanAgentsInDataset(dataset, global, false);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 75);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    createNetworkFacesAgentsInDataset(dataset);

    // PadawanUtil.createWaterAreasAgentsInDataset(dataset, global);

    PadawanUtil.instantiateRelationalConstraints(dataset);

    // createRoadNetworkAgentsInDataset(dataset, global);

    createRoadNeighbourhoodAgentsFromDataset(dataset, global);
    // addPointAgentsToEnvironment(dataset.getCoastlines(), global);
    // createSubmicroAgents(dataset.getCoastlines());

    // PadawanAgentInitialisation
    // .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
    // ((CdBDataset) dataset).getTouristRouteStroke(),
    // dataset.getCoastlines());

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  /**
   * Create IODA agents based on the objects of dataset for river displacement
   * according to routes experiment
   * 
   * @param dataset
   */
  public static void createAgentsInDatasetForRiverDisplacement(
      CartAGenDataSet dataset) {
    // ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
    // true);
    // progressFrame.setVisible(true);

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());
    CartacomSpecifications.getInstanceForPadawan();

    // Create interactions matrices
    Environment global = PadawanUtil.createGlobalEnvironment();

    PadawanUtil.createRoadNetworkAgentsInDataset(dataset, global);

    PadawanUtil.createHydroNetworkCartacomAgentsInDataset(dataset, global);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);

    // for (IBuilding build : dataset.getBuildings()) {
    // if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
    // BuildingAgent agent = PadawanUtil
    // .createIODABuildingAgent(build, global);
    // agent.instantiateConstraints();
    // }
    // }

    // PadawanUtil.createUrbanAgentsInDataset(dataset, global, false);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 75);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    // createNetworkFacesAgentsInDataset(dataset);

    // PadawanUtil.createWaterAreasAgentsInDataset(dataset, global);

    PadawanUtil.instantiateRelationalConstraints(dataset);

    // createRoadNetworkAgentsInDataset(dataset, global);

    createRoadNeighbourhoodAgentsFromDataset(dataset, global);
    // addPointAgentsToEnvironment(dataset.getCoastlines(), global);
    // createSubmicroAgents(dataset.getCoastlines());

    // PadawanAgentInitialisation
    // .initialiseRelationsLineareFeaturesAndPointsOfFeatures(
    // ((CdBDataset) dataset).getTouristRouteStroke(),
    // dataset.getCoastlines());

    // progressFrame.setTextAndValue("Creation of agents complete !", 100);
    // progressFrame.setVisible(false);
    // progressFrame = null;
  }

  public static void createRoadNeighbourhoodAgentsFromDataset(
      CartAGenDataSet dataset, Environment global) {
    // create neighbourhood agents for each road
    for (IRoadLine road : dataset.getRoads()) {
      RoadSectionAgent roadAgent = (RoadSectionAgent) PadawanUtil
          .getIODAAgentFromGeneObj(road);
      // System.out.println(roadAgent);
      if (roadAgent != null) {
        RoadNeighbourhoodAgent leftNeighbourhoodAgent = PadawanUtil
            .createRoadNeighbourhoodAgent(roadAgent, true, global);
        RoadNeighbourhoodAgent rightNeighbourhoodAgent = PadawanUtil
            .createRoadNeighbourhoodAgent(roadAgent, false, global);
        global.addContainedAgents((IDiogenAgent) leftNeighbourhoodAgent);
        global.addContainedAgents((IDiogenAgent) rightNeighbourhoodAgent);
        // roadAgent.instantiateConstraints();
      }
    }
    // create relation between neighbourhood agents and buildings.

    IdentifyRoadNeighbourhood.computeUsingTriangulation(dataset.getRoads(),
        dataset.getBuildings());

    // create environment for each neighbourhood
  }

  /**
   * Creation of all IODA agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkAgentsInDataset(CartAGenDataSet dataset,
      Environment global) {

    DiogenRoadNetworkAgent roadNetAgent = (DiogenRoadNetworkAgent) PadawanUtil
        .getIODAAgentFromGeneObj(dataset.getRoadNetwork());
    if (roadNetAgent == null) {
      roadNetAgent = PadawanUtil
          .createIODARoadNetworkAgent(dataset.getRoadNetwork());
    }
    global.addContainedAgents(roadNetAgent);
    Environment env = PadawanUtil.createRoadNetworkEnvironment(roadNetAgent);

    // Sections
    for (IRoadLine road : dataset.getRoads()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(road) == null) {
        RoadSectionAgent roadAgent = PadawanUtil
            .createIODARoadAgent(roadNetAgent, road, global);
        env.addContainedAgents(roadAgent);
        // roadAgent.instantiateConstraints();
      }
    }

    // Nodes
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(roadNode) == null) {
        NetworkNodeAgent nna = PadawanUtil
            .createIODANetworkNodeAgent(roadNetAgent, roadNode);
        env.addContainedAgents((IDiogenAgent) nna);
      }
    }

    // Links between nodes and sections
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) PadawanUtil
          .getIODAAgentFromGeneObj(roadNode);
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) PadawanUtil
            .getIODAAgentFromGeneObj(road);
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) PadawanUtil
            .getIODAAgentFromGeneObj(road);
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    roadNetAgent.instantiateConstraints();

    // Update of the layers related to road network agent
    // CartagenApplication.getInstance().getLayerGroup()
    // .getLayer(AgentLayerGroup.LAYER_ROAD_NETWORK_POINT_AGENT)
    // .setFeatures(roadNetAgent.getPointAgents());
    // CartagenApplication.getInstance().getLayerGroup()
    // .getLayer(AgentLayerGroup.LAYER_ROAD_SEGMENT)
    // .setFeatures(roadNetAgent.getSegments());

  }

  /**
   * Creation of all IODA agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanAgentsInDataset(CartAGenDataSet dataset,
      Environment global) {
    createUrbanAgentsInDataset(dataset, global, true);
  }

  /**
   * Creation of all IODA agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanAgentsInDataset(CartAGenDataSet dataset,
      Environment global, boolean withBlock) {

    // BUILDINGS
    for (IBuilding build : dataset.getBuildings()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(build) == null) {
        BuildingAgent agent = PadawanUtil.createIODABuildingAgent(build,
            global);
        agent.instantiateConstraints();
      }
    }

    if (!withBlock) {
      return;
    }

    // URBAN BLOCKS
    for (IUrbanBlock block : dataset.getBlocks()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(block) == null) {
        BlockAgent agent = PadawanUtil.createIODABlockAgent(block);
        global.addContainedAgents((IDiogenAgent) agent);
        Environment environment = PadawanUtil.createBlockEnvironment(agent);
        List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
        for (IUrbanElement build : block.getUrbanElements()) {
          IUrbanElementAgent buildingAgent = (IUrbanElementAgent) PadawanUtil
              .getIODAAgentFromGeneObj(build);
          buildingAgent.setMesoAgent(agent);
          buildAgents.add(buildingAgent);
          environment.addContainedAgents((IDiogenAgent) buildingAgent);
          Set<Environment> set = new HashSet<Environment>();
          ((IDiogenAgent) buildingAgent).setContainingEnvironments(set);
        }
        agent.setComponents(buildAgents);
        // Links to the delineating sections
        List<ISectionAgent> sectionAgents = new ArrayList<ISectionAgent>();
        for (INetworkSection section : block.getSurroundingNetwork()) {
          ISectionAgent sectionAgent = (ISectionAgent) PadawanUtil
              .getIODAAgentFromGeneObj(section);
          if (sectionAgent != null) {
            sectionAgents.add(sectionAgent);
            environment.addBorderAgent((IDiogenAgent) sectionAgent);
          }
        }
        agent.setSectionAgents(sectionAgents);
        agent.instantiateConstraints();

      }
    }

    // TOWNS
    for (ITown town : dataset.getTowns()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(town) == null) {
        TownAgent agent = PadawanUtil.createIODATownAgent(town);
        global.addContainedAgents((IDiogenAgent) agent);
        Environment env = PadawanUtil.createTownEnvironment(agent);
        // Links to the blocks
        List<IBlockAgent> blockAgents = new ArrayList<IBlockAgent>();
        // System.out.println("Number of Blocks : " +
        // town.getTownBlocks().size());
        for (IUrbanBlock block : town.getTownBlocks()) {
          IBlockAgent blockAgent = (IBlockAgent) PadawanUtil
              .getIODAAgentFromGeneObj(block);
          blockAgent.setMesoAgent(agent);
          blockAgents.add(blockAgent);
          // System.out.println("Add block " + blockAgent + " to town " +
          // agent);
          env.addContainedAgents((IDiogenAgent) blockAgent);
        }
        agent.setComponents(blockAgents);
        agent.instantiateConstraints();
      }
    }

    // URBAN ALIGNMENTS
    /**
     * for (IUrbanAlignment align : dataset.getUrbanAlignments()) { if
     * (AgentUtil.getAgentAgentFromGeneObj(align) == null) {
     * IODAUrbanAlignmentAgent agent = new IODAUrbanAlignmentAgent(align); //
     * Links to the buildings List<IUrbanElementAgent> buildAgents = new
     * ArrayList<IUrbanElementAgent>(); for (IUrbanElement build :
     * align.getUrbanElements()) { IUrbanElementAgent buildingAgent =
     * (IUrbanElementAgent) PadawanUtil .getIODAAgentFromGeneObj(build);
     * buildingAgent.addAlignment(agent);
     * buildingAgent.getStructureAgents().add(agent);
     * buildAgents.add(buildingAgent); } agent.setComponents(buildAgents); //
     * Link to the block IBlockAgent blockAgent = (IBlockAgent)
     * ((IUrbanElementAgent) PadawanUtil
     * .getIODAAgentFromGeneObj(align.getUrbanElements().get(0)))
     * .getMesoAgent(); if (blockAgent != null) {
     * blockAgent.getInternStructures().add(agent);
     * agent.setMesoAgent(blockAgent); } agent.instanciateConstraints(); } }
     */

  }

  /**
   * Return associated IODA agent for geneObj.
   * 
   * @param geneObj
   * @return
   */
  public static IDiogenAgent getIODAAgentFromGeneObj(IGeneObj geneObj) {

    if (geneObj.getGeneArtifacts() == null) {
      return null;
    }
    IDiogenAgent agentGene = null;
    int i = 0;
    for (Object obj : geneObj.getGeneArtifacts()) {
      if (obj instanceof IAgent) {
        if (((IAgent) obj).getLifeCycle().equals(LIFE_CYCLE)) {
          agentGene = (IDiogenAgent) obj;
          i++;
        }
      }
    }
    if (i == 0) {
      // AgentUtil.logger.warn("No agent attached to the object");
    } else if (i > 1) {
      // AgentUtil.logger
      // .warn("WARNING: more than one agent is attached to the object");
    }
    return agentGene;
  }

  /**
   * Instantiate a new IODA building agent for build.
   * 
   * @param build
   * @return
   */
  public static BuildingAgent createIODABuildingAgent(IBuilding build) {
    BuildingAgent agent = new BuildingAgent(build, 0);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA building agent for build.
   * 
   * @param build
   * @return
   */
  public static BuildingAgent createIODABuildingAgent(IBuilding build,
      Environment globalEnvironent) {
    BuildingAgent agent = new BuildingAgent(build, 0);
    agent.addContainingEnvironments(globalEnvironent);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA block agent for block.
   * 
   * @param block
   * @return
   */
  public static BlockAgent createIODABlockAgent(IUrbanBlock block) {
    BlockAgent agent = new BlockAgent(block, null, block.getId());
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA town agent for town.
   * 
   * @param town
   * @return
   */
  public static TownAgent createIODATownAgent(ITown town) {
    TownAgent agent = new TownAgent(town);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA road netawork agent for road network.
   * 
   * @param roadNetwork
   * @return
   */
  public static DiogenRoadNetworkAgent createIODARoadNetworkAgent(
      INetwork roadNetwork) {
    DiogenRoadNetworkAgent agent = new DiogenRoadNetworkAgent(roadNetwork);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA road agent for road.
   * 
   * @param roadNetAgent
   * @param road
   * @return
   */
  public static RoadSectionAgent createIODARoadAgent(
      RoadNetworkAgent roadNetAgent, IRoadLine road) {
    RoadSectionAgent agent = new RoadSectionAgent(roadNetAgent, road);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA road agent for road.
   * 
   * @param roadNetAgent
   * @param road
   * @return
   */
  public static RoadSectionAgent createIODARoadAgent(
      DiogenRoadNetworkAgent roadNetAgent, IRoadLine road,
      Environment globalEnvironment) {
    RoadSectionAgent agent = null;
    if (road instanceof ICarryingRoadLine) {
      agent = new CarryingRoadSectionAgent(roadNetAgent,
          (ICarryingRoadLine) road);
    } else {
      agent = new RoadSectionAgent(roadNetAgent, road);
    }
    agent.addContainingEnvironments(globalEnvironment);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    createDeformableEnvironment(agent);
    return agent;
  }

  public static RiverSectionAgent createRiverSectionAgent(NetworkAgent netAgent,
      IWaterLine river, Environment globalEnvironment) {
    RiverSectionAgent agent = new RiverSectionAgent(netAgent, river);
    agent.addContainingEnvironments(globalEnvironment);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    createDeformableEnvironment(agent);
    return agent;
  }

  public static IBuildingsBorderAgent createBuildingsBorderAgent(
      BuildingsBorder border, Environment globalEnvironment) {
    IBuildingsBorderAgent agent = new BuildingsBorderAgent(border);
    agent.addContainingEnvironments(globalEnvironment);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Instantiate a new IODA network node agent for roadNode.
   * 
   * @param roadNetAgent
   * @param roadNode
   * @return
   */
  public static NetworkNodeAgent createIODANetworkNodeAgent(
      DiogenRoadNetworkAgent roadNetAgent, IRoadNode roadNode) {
    NetworkNodeAgent agent = new NetworkNodeAgent(roadNetAgent, roadNode);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;

  }

  public static DiogenHydroNetworkAgent createIODAHydroNetworkAgent(
      INetwork hydroNetwork) {
    DiogenHydroNetworkAgent agent = new DiogenHydroNetworkAgent(hydroNetwork);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  /**
   * Creation of all Cartacom agents concerning the hydro network of a dataset
   * @param dataset
   */
  public static void createHydroNetworkCartacomAgentsInDataset(
      CartAGenDataSet dataset, Environment globalEnvironment) {

    DiogenHydroNetworkAgent netAgent = (DiogenHydroNetworkAgent) PadawanUtil
        .getIODAAgentFromGeneObj(dataset.getHydroNetwork());
    if (netAgent == null) {
      netAgent = PadawanUtil
          .createIODAHydroNetworkAgent(dataset.getHydroNetwork());
    }
    // System.out.println("network agent " + netAgent);
    globalEnvironment.addContainedAgents(netAgent);

    Environment env = PadawanUtil.createHydroNetworkEnvironment(netAgent);

    for (IWaterLine river : dataset.getWaterLines()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(river) == null) {
        RiverSectionAgent agent = createRiverSectionAgent(netAgent, river,
            globalEnvironment);
        // System.out.println("River agent " + agent);
        env.addContainedAgents(agent);
      }
    }
  }

  public static void createWaterAreasAgentsInDataset(CartAGenDataSet dataset,
      Environment global) {
    for (IWaterArea waterArea : dataset.getWaterAreas()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(waterArea) == null) {
        WaterAreaAgent agent = PadawanUtil.createWaterAreaAgent(waterArea);
        global.addContainedAgents((IDiogenAgent) agent);
      }
    }
  }

  public static WaterAreaAgent createWaterAreaAgent(IWaterArea waterArea) {
    WaterAreaAgent agent = new WaterAreaAgent(waterArea);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  public static void createHikingStrokeAgentsFromDataset(
      CartAGenDataSet dataset, Environment global) {
    for (IHikingRouteStroke feature : ((HikingDataset) dataset)
        .getTouristRouteStroke()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(feature) == null) {
        ICarryingRoadStrokeAgent agent = PadawanUtil
            .createHikingStrokeAgent(feature);
        global.addContainedAgents((IDiogenAgent) agent);
      }
    }
  }

  public static ICarryingRoadStrokeAgent createHikingStrokeAgent(
      IHikingRouteStroke feature) {
    ICarryingRoadStrokeAgent agent = new CarryingRoadStrokeAgent(feature);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  private static RoadNeighbourhoodAgent createRoadNeighbourhoodAgent(
      RoadSectionAgent roadAgent, boolean leftSide, Environment global) {
    RoadNeighbourhoodAgent a = new RoadNeighbourhoodAgent(roadAgent, leftSide);
    a.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return a;
  }

  public static void createCoastLineAgentsFromDataset(CartAGenDataSet dataset,
      Environment global) {
    for (ICoastLine feature : dataset.getCoastlines()) {
      if (PadawanUtil.getIODAAgentFromGeneObj(feature) == null) {
        ICoastLineAgent agent = PadawanUtil.createCoastLineAgent(feature);
        global.addContainedAgents(agent);
      }
    }
  }

  public static ICoastLineAgent createCoastLineAgent(ICoastLine feature) {
    ICoastLineAgent agent = new CoastLineAgent(feature);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    createDeformableEnvironment(agent);
    return agent;
  }

  public static EmbeddedEnvironmentAgent createEmbeddedEnvironmentAgent(
      IEmbeddedDeadEndArea edee, IBlockAgent block) {
    EmbeddedEnvironmentAgent agent = new EmbeddedEnvironmentAgent(edee);
    agent
        .addContainingEnvironments(((IDiogenAgent) block).getEncapsulatedEnv());
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    return agent;
  }

  public static void createEmbeddedEnvironmentAgents(
      IFeatureCollection<IEmbeddedDeadEndArea> edees) {
    for (IEmbeddedDeadEndArea edee : edees) {
      if (edee.getBlock() != null) {
        IBlockAgent block = (IBlockAgent) PadawanUtil
            .getIODAAgentFromGeneObj(edee.getBlock());
        PadawanUtil.createEmbeddedEnvironmentAgent(edee, block)
            .instantiateConstraints();
      }
    }

  }

  /**
   * Creation of all agents concerning the network faces of a dataset
   * @param dataset
   */
  public static void createNetworkFacesAgentsInDataset(
      CartAGenDataSet dataset) {

    // Deleting network faces
    dataset.eraseFacesReseau();

    // Building topological map
    // Enrichment
    // TODO maybe moving this to the enrichment
    CarteTopo carteTopo = NetworkEnrichment.buildNetworksTopoMap(dataset);

    // Loop through CarteTopo faces
    for (Face face : carteTopo.getPopFaces()) {
      // Gets the geometry of the face
      IPolygon polygon = face.getGeometrie();
      // Converts it into 2D
      try {
        polygon = (IPolygon) AdapterFactory.to2DGM_Object(polygon);
      } catch (Exception e) {
        e.printStackTrace();
        // AgentUtil.logger
        // .error("Failed during conversion of face geometry into 2D");
        // AgentUtil.logger.error(polygon.toString());
        continue;
      }

      // Retrieves the network sections bordering the face,
      // distinguishing btw clockwise and anticlockwise sections
      Set<INetworkSectionAgent> antiClockwiseBorderingSections = new HashSet<INetworkSectionAgent>();
      Set<INetworkSectionAgent> clockwiseBorderingSections = new HashSet<INetworkSectionAgent>();

      // First the anticlockwise sections
      // TODO: the code assumes that the direction of the "arc"
      // (topological edge) is the same as the direction of the feature it
      // supports. This is normally true for edges supporting only one feature,
      // and therefore is enough for the case of trans-hydro graph. The code
      // should be robustified to manage the case of multiple linear features
      // (therefore not necessarily having the same direction) supported by a
      // topological edge.
      // Gets the antoclockwise edges of bordering the topological
      // face
      List<Arc> arcsDirects = face.getArcsDirects();
      // Loop through these edges
      for (Arc arc : arcsDirects) {
        // Loop through features supported by this edge
        for (IFeature feature : arc.getCorrespondants()) {
          // Retrieve the NetworkSection Agent associated to the
          // feature
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) PadawanUtil
              .getCartAComAgentFromGeneObj((IGeneObj) feature);
          // Adds it to the set of anticlockwise bordering
          // sections
          antiClockwiseBorderingSections.add(sectAgent);
        }
      }
      // Now the same for clockwise edges
      List<Arc> arcsIndirects = face.getArcsIndirects();
      for (Arc arc : arcsIndirects) {
        // Loop through features supported by this edge
        for (IFeature feature : arc.getCorrespondants()) {
          // Retrieve the NetworkSection Agent associated to the
          // feature
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) PadawanUtil
              .getCartAComAgentFromGeneObj((IGeneObj) feature);
          // Adds it to the set of anticlockwise bordering
          // sections
          clockwiseBorderingSections.add(sectAgent);
        }
      }

      // Retrieves the small compacts contained in the face
      // TODO: do it for all kinds of small compacts rather than just buildings

      // Retrieves buildings intersecting the face
      Collection<IBuilding> intersectingBuildings = CartAGenDoc.getInstance()
          .getCurrentDataset().getBuildings().select(polygon);
      // Deduces a set of small compacts that are considered as
      // contained in the face
      Set<ISmallCompactAgent> containedSmallCompacts = new HashSet<ISmallCompactAgent>();
      for (IBuilding building : intersectingBuildings) {
        // building completely included in face
        if (polygon.contains(building.getGeom())) {
          containedSmallCompacts.add((ISmallCompactAgent) PadawanUtil
              .getCartAComAgentFromGeneObj(building));

          continue;
        }
        // building not completely included. Compute ratio included.
        double ratio = building.getGeom().intersection(polygon).area()
            / (building.getGeom().area());
        // if the ratio is big enough, the building is considered
        // included
        if (ratio > 0.6) {
          containedSmallCompacts.add((ISmallCompactAgent) PadawanUtil
              .getCartAComAgentFromGeneObj(building));
          continue;
        }
      }

      // Now builds the network face agent
      INetworkFace netFace = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getGeneObjImpl().getCreationFactory()
          .createNetworkFace(polygon);
      dataset.getFacesReseau().add(netFace);
      new NetworkFaceAgent(netFace, containedSmallCompacts,
          clockwiseBorderingSections, antiClockwiseBorderingSections);
    }

    // Tidy up
    carteTopo.nettoyer();

    // Remove the large metaFace covering the whole dataset
    INetworkFace metaFace = null;
    for (IFeature mask : dataset.getMasks()) {
      for (INetworkFace face : dataset.getFacesReseau()) {
        if (face.getGeom().contains(mask.getGeom())) {
          metaFace = face;
        }
      }
    }
    if (metaFace != null) {
      dataset.getFacesReseau().remove(metaFace);
    }

  }

  /**
   * 
   * 
   * @param block
   * @return
   */
  public static Environment createBlockEnvironment(BlockAgent block) {
    Environment env = new Environment();
    env.changeEnvironmentTypeToBlock();
    env.setHostAgent((IDiogenAgent) block);
    return env;
  }

  /**
   * 
   * 
   * @param block
   * @return
   */
  public static Environment createDeformableEnvironment(
      GAELDeformable deformable) {
    Environment env = new Environment();
    env.changeEnvironmentTypeToDeformable();
    env.setHostAgent((IDiogenAgent) deformable);
    return env;
  }

  /**
   * 
   * 
   * @param town
   * @return
   */
  public static Environment createTownEnvironment(TownAgent town) {
    Environment env = new Environment();
    env.changeEnvironmentTypeToTown();
    env.setHostAgent((IDiogenAgent) town);
    return env;
  }

  /**
   * Instantiate and return a NetworkEnvironment with roadNetAgent as host
   * agent.
   * 
   * @param roadNetAgent
   * @return
   */
  public static Environment createRoadNetworkEnvironment(
      DiogenRoadNetworkAgent roadNetAgent) {
    Environment env = new Environment();
    env.changeEnvironmentTypeToNetwork();
    env.setHostAgent(roadNetAgent);
    return env;
  }

  public static Environment createHydroNetworkEnvironment(
      DiogenHydroNetworkAgent netAgent) {
    Environment env = new Environment();
    env.changeEnvironmentTypeToNetwork();
    env.setHostAgent(netAgent);
    return env;
  }

  /**
   * Instantiate and return a Global Environment without host agent.
   * 
   * @return
   */
  public static Environment createGlobalEnvironment() {
    Environment env = new Environment();
    env.changeEnvironmentTypeToGlobal();
    return env;
  }

  public static void createEmbededdedEnvironmentForDeadEnds(
      CartAGenDataSet dataset,
      IFeatureCollection<IEmbeddedDeadEndArea> collection) {
    // get the roads
    IFeatureCollection<IRoadLine> roads = dataset.getRoads();

    double buildingSize = Math
        .sqrt(GeneralisationSpecifications.BUILDING_MIN_AREA)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double distance = 1.5 * buildingSize;
    // get the buildings
    // IFeatureCollection<IBuilding> buildings = dataset.getBuildings();
    // list the dead ends
    Set<DeadEndGroup> deadEnds = DeadEndGroup.buildFromRoads(roads, null);
    // for each dead ends, compute the embedded environment
    // create a collection object to save the embedded environment
    // for each dead end of the map, generate the embedded environment and ad it
    // to the collection
    for (DeadEndGroup deadEnd : deadEnds) {
      if (!deadEnd.isEliminated() && !deadEnd.isDeleted()) {
        // the polygon for the environment
        IGeometry environment = new GM_Polygon();
        // for each section of the dead end, generate left and right halfBuffer
        for (INetworkSection feature : deadEnd.getFeatures()) {
          ILineString sectionLine = feature.getGeom();
          IGeometry leftSide = BufferComputing.buildLineHalfBuffer(sectionLine,
              distance, Side.LEFT);
          IPolygon rightSide = BufferComputing.buildLineHalfBuffer(sectionLine,
              distance, Side.RIGHT);
          // fusion of the two buffer, with the buffer of other environments
          IGeometry environmentTemp = environment.union(rightSide);
          if (environmentTemp != null) {
            environment = environmentTemp;
          }
          environmentTemp = environment.union(leftSide);
          if (environmentTemp != null) {
            environment = environmentTemp;
          }
        }
        IEmbeddedDeadEndArea edee = new EmbeddedDeadEndArea();

        edee.setDeadEnd(deadEnd);
        collection.add(edee);

        for (IUrbanBlock block : dataset.getBlocks()) {
          HashSet<INetworkSection> set = edee.getDeadEnd().getLeafs();
          if (set.isEmpty()) {
            continue;
          }
          INetworkSection line = set.iterator().next();

          if (block.getGeom()
              .intersects(edee.getDeadEnd().getLeafNode(line).getGeom())) {
            edee.setBlock(block);

            environment = environment.intersection(block.getGeom());
          }

        }

        edee.setGeom(environment);
        // buildings = edee.getBlock().getUrbanElements();
        if (edee.getBlock() != null) {
          if (edee.getBlock().getUrbanElements() != null) {
            for (IUrbanElement element : edee.getBlock().getUrbanElements()) {
              if (element instanceof IBuilding) {
                IBuilding building = (IBuilding) element;
                if (building.getGeom().intersects(environment)) {
                  edee.addUrbanElement(building);
                  // IGeometry environmentTemp = (IGeometry)
                  // environment.union(building
                  // .getGeom());
                  // if (environmentTemp != null)
                  // environment = environmentTemp;
                }
              }
            }
          }
        }
      }
    }
  }

  private static void instantiateRelationalConstraints(
      CartAGenDataSet dataset) {

    Collection<IGeneObj> allCacGeneObj = CartAComAgentGeneralisation
        .getAllCartAComGeneObjs();
    // En deduit les agents CartACom associes
    Set<ICartAComAgentGeneralisation> allCACAgentsOfWindow = AgentUtil
        .getCartAComAgentSetFromGeneObjs(allCacGeneObj);
    // Lance l'initialisation de l'environnement sur ces agents
    for (ICartAComAgentGeneralisation cacAgent : allCACAgentsOfWindow) {
      cacAgent.initialiseEnvironmentRepresentation();
    }
    // Puis lance l'initialisation des contraintes sur ces agents
    for (ICartAComAgentGeneralisation cacAgent : allCACAgentsOfWindow) {
      cacAgent.initialiseRelationalConstraints();
    }

  }

  /**
   * Returns the CARTACOM agent attached to a generalisation object
   * @param geneObj
   */
  private static ICartAComAgentGeneralisation getCartAComAgentFromGeneObj(
      IGeneObj geneObj) {
    if (geneObj.getGeneArtifacts() == null) {
      return null;
    }
    ICartAComAgentGeneralisation cartacomAgentGene = null;

    for (Object obj : geneObj.getGeneArtifacts()) {
      if (obj instanceof ICartAComAgentGeneralisation) {
        cartacomAgentGene = (ICartAComAgentGeneralisation) obj;
      }
    }
    return cartacomAgentGene;
  }

  public static IPointAgent getPointAgentFromDeformable(
      GAELDeformable deformable, IDirectPosition point) {
    IPointAgent pointAgent = deformable.getPoint(point.getX(), point.getY());
    // System.out.println("Point agent " + pointAgent);
    if (pointAgent == null) {
      pointAgent = new GeographicPointAgent(deformable, point);
      pointAgent.setLifeCycle(LIFE_CYCLE);
      ((IDiogenAgent) pointAgent).addContainingEnvironments(
          ((IDiogenAgent) deformable).getEncapsulatedEnv());
      new Position(
          ((GeographicPointAgent) pointAgent).getSubmicroPoint().getSubMicro(),
          1);
    } else if (!(pointAgent instanceof IGeographicPointAgent)) {
      deformable.getPointAgents().remove(pointAgent);
      pointAgent = new GeographicPointAgent(pointAgent);
      pointAgent.setLifeCycle(LIFE_CYCLE);
      ((IDiogenAgent) pointAgent).addContainingEnvironments(
          ((IDiogenAgent) deformable).getEncapsulatedEnv());
      new Position(
          ((GeographicPointAgent) pointAgent).getSubmicroPoint().getSubMicro(),
          1);
      new BalanceConstraint((GeographicPointAgent) pointAgent, 2);
    }
    // System.out.println("Return " + pointAgent);
    return pointAgent;
  }

  public static IPointAgent getPointAgentFromGeneObj(IGeneObj geneObj,
      IDirectPosition point) {
    Object o = PadawanUtil.getIODAAgentFromGeneObj(geneObj);
    // System.out.println(o + " is instance of deformable ? : "
    // + (o instanceof GAELDeformable));
    if (!(o instanceof GAELDeformable)) {
      return null;
    }
    GAELDeformable deformable = (GAELDeformable) o;
    return getPointAgentFromDeformable(deformable, point);
  }

  private static Set<IPointAgent> getPointsAgentsFromPopulation(
      IFeatureCollection<? extends IGeneObj> collection) {
    Set<IPointAgent> toReturn = new HashSet<IPointAgent>();
    for (IGeneObj obj : collection) {
      // System.out.println("Get points agents for " + obj);
      for (IDirectPosition position : obj.getGeom().coord()) {
        toReturn.add(getPointAgentFromGeneObj(obj, position));
        // System.out.println("Point agent for " + position + " : "
        // + getPointAgentFromGeneObj(obj, position));
      }
    }
    // System.out.println("To return " + toReturn);
    return toReturn;
  }

  private static void addPointAgentsToEnvironment(
      IFeatureCollection<? extends IGeneObj> collection, Environment env) {
    for (IPointAgent agent : getPointsAgentsFromPopulation(collection)) {
      env.addContainedAgents((IDiogenAgent) agent);
    }
  }

  private static void createSubmicroAgents(
      IFeatureCollection<? extends IGeneObj> collection) {
    for (IGeneObj geneObj : collection) {
      createSubmicroAgent(geneObj);
    }
  }

  private static void createSubmicroAgent(IGeneObj geneObj) {

    IDirectPositionList coords = geneObj.getGeom().coord();

    Object o = PadawanUtil.getIODAAgentFromGeneObj(geneObj);
    if (!(o instanceof GAELDeformable)) {
      return;
    }
    GAELDeformable deformable = (GAELDeformable) o;

    Environment env = ((IDiogenAgent) deformable).getEncapsulatedEnv();

    int nb = coords.size();

    // if there are less than 2 points, there is a problem
    if (nb < 2) {
      return;
    }

    // create the two first points and there segment
    IPointAgent ap0 = getPointAgentFromGeneObj(geneObj, coords.get(0));
    IPointAgent ap1 = getPointAgentFromGeneObj(geneObj, coords.get(1));

    // store the two first points (usefull at the end, for angle construction)
    IPointAgent ap0_ = ap0;
    IPointAgent ap1_ = ap1;
    IPointAgent ap2 = null;

    for (int i = 2; i < nb - 1; i++) {

      // build point agent
      ap2 = getPointAgentFromGeneObj(geneObj, coords.get(i));

      // build segment
      createSegmentSubmicroAgent(new GAELSegmentGeneObj(deformable, ap1, ap2),
          env);

      // build angle
      createAngleSubmicroAgent(new GAELAngleGeneObj(deformable, ap0, ap1, ap2),
          env);

      // next
      ap0 = ap1;
      ap1 = ap2;
    }

    // test closure
    boolean closed;
    if (coords.get(0).distance(coords.get(nb - 1)) <= 0.001) {
      closed = true;
    } else {
      closed = false;
    }

    // line is closed
    if (closed) {
      // build the last segment to close the ring
      createSegmentSubmicroAgent(new GAELSegmentGeneObj(deformable, ap1, ap0_),
          env);
      // build the two last angles (if needed)

      createAngleSubmicroAgent(new GAELAngleGeneObj(deformable, ap0, ap1, ap0_),
          env);
      createAngleSubmicroAgent(
          new GAELAngleGeneObj(deformable, ap1, ap0_, ap1_), env);

      // possible link between last coordinate and first point agent
      if (coords.get(0) != coords.get(nb - 1)) {
        ap0_.getPositions().add(coords.get(nb - 1));
      }
    } else {
      // build the last point agent
      ap2 = getPointAgentFromGeneObj(geneObj, coords.get(nb - 1));

      // build the last segment
      createSegmentSubmicroAgent(new GAELSegmentGeneObj(deformable, ap1, ap2),
          env);

      // build the last angle
      createAngleSubmicroAgent(new GAELAngleGeneObj(deformable, ap0, ap1, ap2),
          env);
    }
  }

  private static AngleSubmicroAgent createAngleSubmicroAgent(
      GAELAngleGeneObj geneObj, Environment env) {
    AngleSubmicroAgent agent = new AngleSubmicroAgent(geneObj);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    agent.addContainingEnvironments(env);
    new Value(geneObj.getSubMicro(), 1);
    return agent;
  }

  private static SegmentSubmicroAgent createSegmentSubmicroAgent(
      GAELSegmentGeneObj geneObj, Environment env) {
    SegmentSubmicroAgent agent = new SegmentSubmicroAgent(geneObj);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    agent.addContainingEnvironments(env);
    new Length(geneObj.getSubMicro(), 1);
    new Orientation(geneObj.getSubMicro(), 1);
    return agent;
  }

  private static TriangleSubmicroAgent createTriangleSubmicroAgent(
      GAELTriangleGeneObj geneObj, Environment env) {
    TriangleSubmicroAgent agent = new TriangleSubmicroAgent(geneObj);
    agent.setLifeCycle(PadawanUtil.LIFE_CYCLE);
    agent.addContainingEnvironments(env);
    return agent;
  }
}
