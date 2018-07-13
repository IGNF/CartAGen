package fr.ign.cogit.cartagen.agents.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.MaskAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RailwaySectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RiverSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.IGeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkNodeAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.electricity.ElectricitySectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroSurfaceAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.rail.RailroadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.TownAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.administrative.AdministrativeFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ContourLineAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.DEMPixelAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.SpotHeightAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.BasicLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Static class to link Generalisation objects with their agents if they exist
 * @author J. Renard (d'après M. Duckham) 11/08/2009
 * 
 */

public class AgentUtil {
  protected final static Logger logger = Logger
      .getLogger(AgentUtil.class.getName());

  public static Logger getLogger() {
    return logger;
  }

  // /////////////////////////////////
  // RECUPERATION OF AGENTS (AGENT AND GAEL)
  // /////////////////////////////////

  /**
   * Returns the AGENT agent attached to a generalisation object
   * @param geneObj
   */
  public static GeographicAgentGeneralisation getAgentFromGeneObj(
      IGeneObj geneObj) {
    if (geneObj == null) {
      return null;
    }
    if (geneObj.getGeneArtifacts() == null) {
      return null;
    }
    GeographicAgentGeneralisation agentGene = null;
    int i = 0;
    for (Object obj : geneObj.getGeneArtifacts()) {
      if (obj instanceof GeographicAgentGeneralisation) {
        agentGene = (GeographicAgentGeneralisation) obj;
        i++;
      }
    }
    if (i == 0) {
      AgentUtil.logger.warn("No agent attached to the object");
    } else if (i > 1) {
      AgentUtil.logger
          .warn("WARNING: more than one agent is attached to the object");
    }
    return agentGene;
  }

  /**
   * Returns the set of AGENT agentd attached to a collection of generalisation
   * objects
   * @param geneObjs
   */
  public static Set<GeographicAgentGeneralisation> getAgentCollectionFromGeneObjs(
      IFeatureCollection<IGeneObj> geneObjs) {
    Set<GeographicAgentGeneralisation> agentGeneCollection = new HashSet<GeographicAgentGeneralisation>();
    for (IGeneObj geneObj : geneObjs) {
      agentGeneCollection.add(AgentUtil.getAgentFromGeneObj(geneObj));
    }
    return agentGeneCollection;
  }

  // /////////////////////////////////
  // RECUPERATION OF CARTACOM AGENTS
  // /////////////////////////////////

  /**
   * Returns the CARTACOM agent attached to a generalisation object
   * @param geneObj
   */
  public static ICartAComAgentGeneralisation getCartAComAgentFromGeneObj(
      IGeneObj geneObj) {
    if (geneObj.getGeneArtifacts() == null) {
      return null;
    }
    ICartAComAgentGeneralisation cartacomAgentGene = null;
    int i = 0;
    for (Object obj : geneObj.getGeneArtifacts()) {
      if (obj instanceof ICartAComAgentGeneralisation) {
        cartacomAgentGene = (ICartAComAgentGeneralisation) obj;
        i++;
      }
    }
    if (i == 0) {
      AgentUtil.logger.warn("No agent attached to the object");
    } else if (i > 1) {
      AgentUtil.logger
          .warn("WARNING: more than one agent is attached to the object");
    }
    return cartacomAgentGene;
  }

  /**
   * Returns the CARTACOM agent attached to a generalisation object based on its
   * ID
   * @param id
   */
  public ICartAComAgentGeneralisation getCartacomAgentFromId(int id) {
    ICartAComAgentGeneralisation result = null;
    for (IGeneObj geneObj : CartAComAgentGeneralisation
        .getAllCartAComGeneObjs()) {
      if (geneObj.getId() == id) {
        result = AgentUtil.getCartAComAgentFromGeneObj(geneObj);
      }
    }

    if (result == null) {
      AgentUtil.logger
          .error("No CartAComGeneralisation agent matching to the id");
    }

    return result;
  }

  /**
   * Returns the set of CartACom agents attached to a collection of
   * generalisation objects. Objects that have no corresponding CartACom agent
   * are ignored.
   * @param geneObjs Collection of IGeneObjs from which the CartACom agents are
   *          to be retrieved
   * @return A Set containing the corresponding CartACom agents
   */
  public static Set<ICartAComAgentGeneralisation> getCartAComAgentSetFromGeneObjs(
      Collection<IGeneObj> geneObjs) {
    Set<ICartAComAgentGeneralisation> cartAComAgentCollection = new HashSet<ICartAComAgentGeneralisation>();
    for (IGeneObj geneObj : geneObjs) {
      ICartAComAgentGeneralisation cacAgent = AgentUtil
          .getCartAComAgentFromGeneObj(geneObj);
      if (cacAgent != null) {
        cartAComAgentCollection.add(cacAgent);
      }
    }
    return cartAComAgentCollection;
  }

  /**
   * Returns the set of CartACom agents attached to a IFeatureCollection of
   * generalisation objects. Objects that have no corresponding CartACom agent
   * are ignored.
   * @param geneObjs IFeatureCollectionCollection of IGeneObjs from which the
   *          CartACom agents are to be retrieved
   * @return A Set containing the corresponding CartACom agents
   */
  public static Set<ICartAComAgentGeneralisation> getCartAComAgentSetFromGeneObjs(
      IFeatureCollection<IGeneObj> geneObjs) {
    Set<ICartAComAgentGeneralisation> cartAComAgentCollection = new HashSet<ICartAComAgentGeneralisation>();
    for (IGeneObj geneObj : geneObjs) {
      ICartAComAgentGeneralisation cacAgent = AgentUtil
          .getCartAComAgentFromGeneObj(geneObj);
      if (cacAgent != null) {
        cartAComAgentCollection.add(cacAgent);
      }
    }
    return cartAComAgentCollection;
  }

  // /////////////////////////////////
  // CREATION OF AGENT AGENTS ABOVE GENE
  // OBJECTS IN A DATASET
  // /////////////////////////////////

  /**
   * Creation of all AGENT agents of a dataset, provided the theme is considered
   * as agent in the {@link AgentSpecifications}.
   * @param dataset
   */
  public static void createAgentAgentsInDataset(CartAGenDataSet dataset) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame(
     * "Creating AGENT agents...", true); progressFrame.setVisible(true);
     */

    // Specifications loadin
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // create agents
    createAllAgentAgents(dataset, null);
  }

  public static void createAgentAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon, String specificationFile) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame(
     * "Creating AGENT agents...", true); progressFrame.setVisible(true);
     */

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(specificationFile);

    // create agents
    createAllAgentAgents(dataset, polygon);
  }

  private static void createAllAgentAgents(CartAGenDataSet dataset,
      IPolygon polygon) {
    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 20);
    if (AgentSpecifications.isRoadAgents()) {
      if (polygon == null)
        AgentUtil.createRoadNetworkAgentAgentsInDataset(dataset);
      else
        AgentUtil.createRoadNetworkAgentAgentsInArea(dataset, polygon);
    }
    // Hydro network
    if (AgentSpecifications.isHydroAgents()) {
      if (polygon == null)
        AgentUtil.createHydroNetworkAgentAgentsInDataset(dataset);
      else
        AgentUtil.createHydroNetworkAgentAgentsInArea(dataset, polygon);
    }

    // Railway network
    if (AgentSpecifications.isRailAgents()) {
      if (polygon == null)
        AgentUtil.createRailwayNetworkAgentAgentsInDataset(dataset);
      else
        AgentUtil.createRailwayNetworkAgentAgentsInArea(dataset, polygon);
    }

    // Electricity network
    if (AgentSpecifications.isElecAgents()) {
      if (polygon == null)
        AgentUtil.createElectricityNetworkAgentAgentsInDataset(dataset);
      else
        AgentUtil.createElectricityNetworkAgentAgentsInArea(dataset, polygon);
    }

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 70);
    if (AgentSpecifications.isUrbanAgents()) {
      if (polygon == null)
        AgentUtil.createUrbanAgentAgentsInDataset(dataset);
      else
        AgentUtil.createUrbanAgentAgentsInArea(dataset, polygon);
    }

    // Initialisation of measures lists
    /*
     * ListesMesures.initMesures();
     * 
     * 
     * progressFrame.setTextAndValue("Creation of agents complete !", 100);
     * progressFrame.setVisible(false); progressFrame = null;
     */
  }

  /**
   * Creation of all AGENT agents in a given area, provided the theme is
   * considered as agent in the {@link AgentSpecifications}.
   * @param dataset
   */
  public static void createAgentAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame(
     * "Creating AGENT agents...", true); progressFrame.setVisible(true);
     */

    // Specifications loading
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // create agents
    createAllAgentAgents(dataset, polygon);

  }

  /**
   * Creation of all AGENT agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkAgentAgentsInDataset(
      CartAGenDataSet dataset) {

    RoadNetworkAgent roadNetAgent = (RoadNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRoadNetwork());
    if (roadNetAgent == null) {
      roadNetAgent = new RoadNetworkAgent(dataset.getRoadNetwork());
    }

    // Sections
    for (IRoadLine road : dataset.getRoads()) {
      if (AgentUtil.getAgentFromGeneObj(road) == null) {
        IRoadSectionAgent roadAgent = new RoadSectionAgent(roadNetAgent, road);
        roadAgent.instantiateConstraints();
      }
    }

    // Nodes
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      if (AgentUtil.getAgentFromGeneObj(roadNode) == null) {
        new NetworkNodeAgent(roadNetAgent, roadNode);
      }
    }

    // Links between nodes and sections
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(roadNode);
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    roadNetAgent.instantiateConstraints();

  }

  /**
   * Creation of all AGENT agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkAgentAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon) {

    RoadNetworkAgent roadNetAgent = (RoadNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRoadNetwork());
    if (roadNetAgent == null) {
      roadNetAgent = new RoadNetworkAgent(dataset.getRoadNetwork());
    }

    // Sections
    for (IRoadLine road : dataset.getRoads()) {
      if (AgentUtil.getAgentFromGeneObj(road) == null) {
        if (road.getGeom().intersects(polygon)) {
          IRoadSectionAgent roadAgent = new RoadSectionAgent(roadNetAgent,
              road);
          roadAgent.instantiateConstraints();
        }
      }
    }

    // Nodes
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      if (AgentUtil.getAgentFromGeneObj(roadNode) == null) {
        if (polygon.contains(roadNode.getGeom()))
          new NetworkNodeAgent(roadNetAgent, roadNode);
      }
    }

    // Links between nodes and sections
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(roadNode);
      if (nodeAgent == null)
        continue;
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
        if (sectionAgent == null)
          continue;
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
        if (sectionAgent == null)
          continue;
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    roadNetAgent.instantiateConstraints();

  }

  /**
   * Creation of all AGENT agents concerning the hydro network of a dataset
   * @param dataset
   */
  public static void createHydroNetworkAgentAgentsInDataset(
      CartAGenDataSet dataset) {

    HydroNetworkAgent hydroNetAgent = (HydroNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getHydroNetwork());
    if (hydroNetAgent == null) {
      hydroNetAgent = new HydroNetworkAgent(dataset.getHydroNetwork());
    }

    // Sections
    for (IWaterLine river : dataset.getWaterLines()) {
      if (AgentUtil.getAgentFromGeneObj(river) == null) {
        IHydroSectionAgent riverAgent = new HydroSectionAgent(hydroNetAgent,
            river);
        riverAgent.instantiateConstraints();
      }
    }

    // Nodes
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      if (AgentUtil.getAgentFromGeneObj(waterNode) == null) {
        new NetworkNodeAgent(hydroNetAgent, waterNode);
      }
    }

    // Surfaces
    for (IWaterArea surf : dataset.getWaterAreas()) {
      if (AgentUtil.getAgentFromGeneObj(surf) == null) {
        HydroSurfaceAgent surfAgent = new HydroSurfaceAgent(hydroNetAgent,
            surf);
        surfAgent.instantiateConstraints();
      }
    }

    // Links between nodes and sections
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(waterNode);
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    hydroNetAgent.instantiateConstraints();

  }

  /**
   * Creation of all AGENT agents concerning the hydro network inside a given
   * area.
   * @param dataset
   */
  public static void createHydroNetworkAgentAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {

    HydroNetworkAgent hydroNetAgent = (HydroNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getHydroNetwork());
    if (hydroNetAgent == null) {
      hydroNetAgent = new HydroNetworkAgent(dataset.getHydroNetwork());
    }

    // Sections
    for (IWaterLine river : dataset.getWaterLines()) {
      if (AgentUtil.getAgentFromGeneObj(river) == null) {
        if (river.getGeom().intersects(polygon)) {
          IHydroSectionAgent riverAgent = new HydroSectionAgent(hydroNetAgent,
              river);
          riverAgent.instantiateConstraints();
        }
      }
    }

    // Nodes
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      if (AgentUtil.getAgentFromGeneObj(waterNode) == null) {
        if (polygon.contains(waterNode.getGeom()))
          new NetworkNodeAgent(hydroNetAgent, waterNode);
      }
    }

    // Surfaces
    for (IWaterArea surf : dataset.getWaterAreas()) {
      if (AgentUtil.getAgentFromGeneObj(surf) == null) {
        if (surf.getGeom().intersects(polygon)) {
          HydroSurfaceAgent surfAgent = new HydroSurfaceAgent(hydroNetAgent,
              surf);
          surfAgent.instantiateConstraints();
        }
      }
    }

    // Links between nodes and sections
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(waterNode);
      if (nodeAgent == null)
        continue;
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        if (sectionAgent == null)
          continue;
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        if (sectionAgent == null)
          continue;
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    hydroNetAgent.instantiateConstraints();

  }

  /**
   * Creation of all AGENT agents concerning the railway network of a dataset
   * @param dataset
   */
  public static void createRailwayNetworkAgentAgentsInDataset(
      CartAGenDataSet dataset) {

    NetworkAgent railwayNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRailwayNetwork());
    if (railwayNetAgent == null) {
      railwayNetAgent = new NetworkAgent(dataset.getRailwayNetwork());
    }

    // Sections
    for (IRailwayLine railway : dataset.getRailwayLines()) {
      if (AgentUtil.getAgentFromGeneObj(railway) == null) {
        new RailroadSectionAgent(railwayNetAgent, railway);
      }
    }

    // Nodes
    for (INetworkNode railwayNode : dataset.getRailwayNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(railwayNode) == null) {
        new NetworkNodeAgent(railwayNetAgent, railwayNode);
      }
    }

  }

  /**
   * Creation of all AGENT agents concerning the railway network inside a given
   * area.
   * @param dataset
   */
  public static void createRailwayNetworkAgentAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {

    NetworkAgent railwayNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRailwayNetwork());
    if (railwayNetAgent == null) {
      railwayNetAgent = new NetworkAgent(dataset.getRailwayNetwork());
    }

    // Sections
    for (IRailwayLine railway : dataset.getRailwayLines()) {
      if (AgentUtil.getAgentFromGeneObj(railway) == null) {
        if (railway.getGeom().intersects(polygon))
          new RailroadSectionAgent(railwayNetAgent, railway);
      }
    }

    // Nodes
    for (INetworkNode railwayNode : dataset.getRailwayNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(railwayNode) == null) {
        if (polygon.contains(railwayNode.getGeom()))
          new NetworkNodeAgent(railwayNetAgent, railwayNode);
      }
    }

  }

  /**
   * Creation of all AGENT agents concerning the electricity network of a
   * dataset
   * @param dataset
   */
  public static void createElectricityNetworkAgentAgentsInDataset(
      CartAGenDataSet dataset) {

    NetworkAgent elecNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getElectricityNetwork());
    if (elecNetAgent == null) {
      elecNetAgent = new NetworkAgent(dataset.getElectricityNetwork());
    }

    // Sections
    for (IElectricityLine elecLine : dataset.getElectricityLines()) {
      if (AgentUtil.getAgentFromGeneObj(elecLine) == null) {
        new ElectricitySectionAgent(elecNetAgent, elecLine);
      }
    }

    // Nodes
    for (INetworkNode elecNode : dataset.getElectricityNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(elecNode) == null) {
        new NetworkNodeAgent(elecNetAgent, elecNode);
      }
    }

  }

  /**
   * Creation of all AGENT agents concerning the electricity network inside a
   * given area.
   * @param dataset
   */
  public static void createElectricityNetworkAgentAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {

    NetworkAgent elecNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getElectricityNetwork());
    if (elecNetAgent == null) {
      elecNetAgent = new NetworkAgent(dataset.getElectricityNetwork());
    }

    // Sections
    for (IElectricityLine elecLine : dataset.getElectricityLines()) {
      if (AgentUtil.getAgentFromGeneObj(elecLine) == null) {
        if (elecLine.getGeom().intersects(polygon))
          new ElectricitySectionAgent(elecNetAgent, elecLine);
      }
    }

    // Nodes
    for (INetworkNode elecNode : dataset.getElectricityNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(elecNode) == null) {
        if (polygon.contains(elecNode.getGeom()))
          new NetworkNodeAgent(elecNetAgent, elecNode);
      }
    }

  }

  /**
   * Creation of all AGENT agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanAgentAgentsInDataset(CartAGenDataSet dataset) {

    // BUILDINGS
    for (IBuilding build : dataset.getBuildings()) {
      if (AgentUtil.getAgentFromGeneObj(build) == null) {
        BuildingAgent agent = new BuildingAgent(build, 0);
        agent.instantiateConstraints();
      }
    }

    // URBAN BLOCKS
    for (IUrbanBlock block : dataset.getBlocks()) {
      if (AgentUtil.getAgentFromGeneObj(block) == null) {
        BlockAgent agent = new BlockAgent(block);
        // Links to the buildings
        List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
        for (IUrbanElement build : block.getUrbanElements()) {
          IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
              .getAgentFromGeneObj(build);
          buildingAgent.setMesoAgent(agent);
          buildAgents.add(buildingAgent);
        }
        agent.setComponents(buildAgents);
        // Links to the delineating sections
        List<ISectionAgent> sectionAgents = new ArrayList<ISectionAgent>();
        for (INetworkSection section : block.getSurroundingNetwork()) {
          ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
              .getAgentFromGeneObj(section);
          sectionAgents.add(sectionAgent);
        }
        agent.setSectionAgents(sectionAgents);
        agent.instantiateConstraints();
      }
    }

    // TOWNS
    for (ITown town : dataset.getTowns()) {
      if (AgentUtil.getAgentFromGeneObj(town) == null) {
        TownAgent agent = new TownAgent(town);
        // Links to the blocks
        List<IBlockAgent> blockAgents = new ArrayList<IBlockAgent>();
        for (IUrbanBlock block : town.getTownBlocks()) {
          IBlockAgent blockAgent = (IBlockAgent) AgentUtil
              .getAgentFromGeneObj(block);
          blockAgent.setMesoAgent(agent);
          blockAgents.add(blockAgent);
        }
        agent.setComponents(blockAgents);
        agent.instantiateConstraints();
      }
    }

    // URBAN ALIGNMENTS
    for (IUrbanAlignment align : dataset.getUrbanAlignments()) {
      if (AgentUtil.getAgentFromGeneObj(align) == null) {
        UrbanAlignmentAgent agent = new UrbanAlignmentAgent(align);
        // Links to the buildings
        List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
        for (IUrbanElement build : align.getUrbanElements()) {
          IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
              .getAgentFromGeneObj(build);
          buildingAgent.addAlignment(agent);
          buildingAgent.getStructureAgents().add(agent);
          buildAgents.add(buildingAgent);
        }
        agent.setComponents(buildAgents);
        // Link to the block
        IBlockAgent blockAgent = (IBlockAgent) ((IUrbanElementAgent) AgentUtil
            .getAgentFromGeneObj(align.getUrbanElements().get(0)))
                .getMesoAgent();
        if (blockAgent != null) {
          blockAgent.getInternStructures().add(agent);
          agent.setMesoAgent(blockAgent);
        }
        agent.instantiateConstraints();
      }
    }

  }

  /**
   * Creation of all AGENT agents concerning the urban objects inside a given
   * area
   * @param dataset
   */
  public static void createUrbanAgentAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon) {

    // BUILDINGS
    for (IBuilding build : dataset.getBuildings()) {
      if (AgentUtil.getAgentFromGeneObj(build) == null) {
        if (polygon.intersects(build.getGeom())) {
          BuildingAgent agent = new BuildingAgent(build, 0);
          agent.instantiateConstraints();
        }
      }
    }

    // URBAN BLOCKS
    for (IUrbanBlock block : dataset.getBlocks()) {
      if (AgentUtil.getAgentFromGeneObj(block) == null) {
        if (polygon.intersects(block.getGeom())) {
          BlockAgent agent = new BlockAgent(block);
          // Links to the buildings
          List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
          for (IUrbanElement build : block.getUrbanElements()) {
            IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
                .getAgentFromGeneObj(build);
            if (buildingAgent == null)
              continue;
            buildingAgent.setMesoAgent(agent);
            buildAgents.add(buildingAgent);
          }
          agent.setComponents(buildAgents);
          // Links to the delineating sections
          List<ISectionAgent> sectionAgents = new ArrayList<ISectionAgent>();
          for (INetworkSection section : block.getSurroundingNetwork()) {
            ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
                .getAgentFromGeneObj(section);
            if (sectionAgent == null)
              continue;
            sectionAgents.add(sectionAgent);
          }
          agent.setSectionAgents(sectionAgents);
          agent.instantiateConstraints();
        }
      }
    }

    // TOWNS
    for (ITown town : dataset.getTowns()) {
      if (AgentUtil.getAgentFromGeneObj(town) == null) {
        if (polygon.intersects(town.getGeom())) {
          TownAgent agent = new TownAgent(town);
          // Links to the blocks
          List<IBlockAgent> blockAgents = new ArrayList<IBlockAgent>();
          for (IUrbanBlock block : town.getTownBlocks()) {
            IBlockAgent blockAgent = (IBlockAgent) AgentUtil
                .getAgentFromGeneObj(block);
            if (blockAgent == null)
              continue;
            blockAgent.setMesoAgent(agent);
            blockAgents.add(blockAgent);
          }
          agent.setComponents(blockAgents);
          agent.instantiateConstraints();
        }
      }
    }

    // URBAN ALIGNMENTS
    for (IUrbanAlignment align : dataset.getUrbanAlignments()) {
      if (AgentUtil.getAgentFromGeneObj(align) == null) {
        if (polygon.intersects(align.getGeom())) {
          UrbanAlignmentAgent agent = new UrbanAlignmentAgent(align);
          // Links to the buildings
          List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
          for (IUrbanElement build : align.getUrbanElements()) {
            IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
                .getAgentFromGeneObj(build);
            if (buildingAgent == null)
              continue;
            buildingAgent.addAlignment(agent);
            buildingAgent.getStructureAgents().add(agent);
            buildAgents.add(buildingAgent);
          }
          agent.setComponents(buildAgents);
          // Link to the block
          IBlockAgent blockAgent = (IBlockAgent) ((IUrbanElementAgent) AgentUtil
              .getAgentFromGeneObj(align.getUrbanElements().get(0)))
                  .getMesoAgent();
          if (blockAgent != null) {
            blockAgent.getInternStructures().add(agent);
            agent.setMesoAgent(blockAgent);
          }
          agent.instantiateConstraints();
        }
      }
    }

  }

  // /////////////////////////////////
  // CREATION OF GAEL AGENTS ABOVE GENE
  // OBJECTS IN A DATASET
  // /////////////////////////////////

  /**
   * Creation of all Gael agents of a dataset
   * @param dataset
   */
  public static void createGaelAgentsInDataset(CartAGenDataSet dataset) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame("Creating GAEL agents..."
     * , true); progressFrame.setVisible(true);
     */

    // Specifications loadin
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // Relief elements
    // progressFrame.setTextAndValue("Creating relief agents", 20);
    AgentUtil.createReliefGaelAgentsInDataset(dataset);

    // Enrichment
    // progressFrame.setTextAndValue("Enriching agents", 70);
    AgentUtil.enrichGaelAgents(dataset);

    // Initialisation of measures lists
    /*
     * ListesMesures.initMesures();
     * 
     * 
     * progressFrame.setTextAndValue("Creation of GAEL agents complete !", 100);
     * progressFrame.setVisible(false); progressFrame = null;
     */

  }

  /**
   * Creation of all Gael agents concerning the relief field of a dataset
   * @param dataset
   */
  public static void createReliefGaelAgentsInDataset(CartAGenDataSet dataset) {

    ReliefFieldAgent reliefAgent = (ReliefFieldAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getReliefField());
    if (reliefAgent == null) {
      reliefAgent = new ReliefFieldAgent(dataset.getReliefField());
    }

    // Contour lines
    for (IContourLine line : dataset.getContourLines()) {
      if (AgentUtil.getAgentFromGeneObj(line) == null) {
        new ContourLineAgent(reliefAgent, line);
      }
    }

    // Spot heights
    for (ISpotHeight spot : dataset.getSpotHeights()) {
      if (AgentUtil.getAgentFromGeneObj(spot) == null) {
        new SpotHeightAgent(reliefAgent, spot);
      }
    }

    // DEM pixels
    for (IDEMPixel pix : dataset.getDEMPixels()) {
      if (AgentUtil.getAgentFromGeneObj(pix) == null) {
        new DEMPixelAgent(reliefAgent, pix);
      }
    }

    // Instanciation of constraints
    reliefAgent.instanciateConstraints();

  }

  /**
   * Creation of all Gael agents concerning the land use and administrative
   * units of a dataset
   * @param dataset
   */
  public static void createLandUseAndAdminGaelAgentsInDataset(
      CartAGenDataSet dataset) {

    // Land use
    LandUseFieldAgent landUseAgent = (LandUseFieldAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getLandUseField());
    if (landUseAgent == null) {
      landUseAgent = new LandUseFieldAgent(dataset.getLandUseField());
    }
    landUseAgent.instanciateConstraints();

    // Admin
    AdministrativeFieldAgent adminAgent = (AdministrativeFieldAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getAdminField());
    if (adminAgent == null) {
      adminAgent = new AdministrativeFieldAgent(dataset.getAdminField());
    }
    adminAgent.instanciateConstraints();

  }

  /**
   * Enrichment of relief and land use Gael agents of a dataset
   * @param dataset
   */
  public static void enrichGaelAgents(CartAGenDataSet dataset) {

    ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(dataset.getReliefField()))
        .enrich();

    ((LandUseFieldAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getLandUseField())).enrich();

  }

  // /////////////////////////////////
  // CREATION OF CARTACOM AGENTS ABOVE GENE
  // OBJECTS IN A DATASET
  // /////////////////////////////////

  /**
   * Creation of all CartACom agents of a dataset
   * @param dataset
   */
  public static void createCartacomAgentsInDataset(CartAGenDataSet dataset) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame(
     * "Creating CartACom agents...", true); progressFrame.setVisible(true);
     */

    // Specifications loadin
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 30);
    AgentUtil.createRoadNetworkCartacomAgentsInDataset(dataset);

    // Hydro network
    AgentUtil.createHydroNetworkCartacomAgentsInDataset(dataset);

    // Railway network
    AgentUtil.createRailwayNetworkCartacomAgentsInDataset(dataset);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);
    AgentUtil.createUrbanCartacomAgentsInDataset(dataset);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 80);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    AgentUtil.createNetworkFacesCartacomAgentsInDataset(dataset);
    // }

    // Mask of the dataset
    for (IMask mask : dataset.getMasks()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(mask) == null) {
        new MaskAgent(mask);
      }
    }

    // Initialisation of measures lists
    /*
     * ListesMesures.initMesures();
     * 
     * 
     * progressFrame.setTextAndValue("Creation of agents complete !", 100);
     * progressFrame.setVisible(false); progressFrame = null;
     */

  }

  /**
   * Creation of all CartACom agents of a dataset inside a given area.
   * @param dataset
   */
  public static void createCartacomAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame(
     * "Creating CartACom agents...", true); progressFrame.setVisible(true);
     */

    // Specifications loadin
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 30);
    if (AgentSpecifications.isRoadAgents())
      AgentUtil.createRoadNetworkCartacomAgentsInArea(dataset, polygon);

    // Hydro network
    if (AgentSpecifications.isHydroAgents())
      AgentUtil.createHydroNetworkCartacomAgentsInArea(dataset, polygon);

    // Railway network
    if (AgentSpecifications.isRailAgents())
      AgentUtil.createRailwayNetworkCartacomAgentsInArea(dataset, polygon);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);
    if (AgentSpecifications.isUrbanAgents())
      AgentUtil.createUrbanCartacomAgentsInArea(dataset, polygon);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 80);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    AgentUtil.createNetworkFacesCartacomAgentsInArea(dataset, polygon);
    // }

    // Mask of the dataset
    for (IMask mask : dataset.getMasks()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(mask) == null) {
        new MaskAgent(mask);
      }
    }

    /*
     * // Initialisation of measures lists ListesMesures.initMesures();
     * 
     * 
     * progressFrame.setTextAndValue("Creation of agents complete !", 100);
     * progressFrame.setVisible(false); progressFrame = null;
     */

  }

  /**
   * Creation of all Cartacom agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkCartacomAgentsInDataset(
      CartAGenDataSet dataset) {
    for (IRoadLine road : dataset.getRoads()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(road) == null) {
        new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RoadSectionAgent(
            road);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the hydro network of a dataset
   * @param dataset
   */
  public static void createHydroNetworkCartacomAgentsInDataset(
      CartAGenDataSet dataset) {
    for (IWaterLine river : dataset.getWaterLines()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(river) == null) {
        new RiverSectionAgent(river);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the railway network of a dataset
   * @param dataset
   */
  public static void createRailwayNetworkCartacomAgentsInDataset(
      CartAGenDataSet dataset) {
    for (IRailwayLine railway : dataset.getRailwayLines()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(railway) == null) {
        new RailwaySectionAgent(railway);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanCartacomAgentsInDataset(
      CartAGenDataSet dataset) {
    for (IBuilding build : dataset.getBuildings()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(build) == null) {
        new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.BuildingAgent(
            build);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the network faces of a dataset
   * @param dataset
   */
  public static void createNetworkFacesCartacomAgentsInDataset(
      CartAGenDataSet dataset) {

    System.out.println("createNetworkFacesCartacomAgentsInDataset");

    // Deleting network faces
    dataset.eraseFacesReseau();

    // Building topological map
    CarteTopo carteTopo = NetworkEnrichment.buildNetworksTopoMap(dataset);

    // Construction of the NetworkFaceAgents
    if (AgentUtil.logger.isDebugEnabled()) {
      AgentUtil.logger.debug("Building the NetworkFaceAgents");
    }

    // Loop through CarteTopo faces
    for (Face face : carteTopo.getPopFaces()) {
      // Gets the geometry of the face
      IPolygon polygon = face.getGeometrie();
      // Converts it into 2D
      try {
        polygon = (IPolygon) AdapterFactory.to2DGM_Object(polygon);
      } catch (Exception e) {
        AgentUtil.logger
            .error("Failed during conversion of face geometry into 2D");
        AgentUtil.logger.error(polygon.toString());
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
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) AgentUtil
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
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) AgentUtil
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
          containedSmallCompacts.add((ISmallCompactAgent) AgentUtil
              .getCartAComAgentFromGeneObj(building));

          continue;
        }
        // building not completely included. Compute ratio included.
        double ratio = building.getGeom().intersection(polygon).area()
            / (building.getGeom().area());
        // if the ratio is big enough, the building is considered
        // included
        if (ratio > 0.6) {
          containedSmallCompacts.add((ISmallCompactAgent) AgentUtil
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
   * Creation of all Cartacom agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkCartacomAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {
    for (IRoadLine road : dataset.getRoads()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(road) == null) {
        if (polygon.intersects(road.getGeom())) {
          new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RoadSectionAgent(
              road);
        }
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the hydro network of a dataset
   * @param dataset
   */
  public static void createHydroNetworkCartacomAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {
    for (IWaterLine river : dataset.getWaterLines()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(river) == null) {
        if (polygon.intersects(river.getGeom()))
          new RiverSectionAgent(river);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the railway network of a dataset
   * @param dataset
   */
  public static void createRailwayNetworkCartacomAgentsInArea(
      CartAGenDataSet dataset, IPolygon polygon) {
    for (IRailwayLine railway : dataset.getRailwayLines()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(railway) == null) {
        if (polygon.intersects(railway.getGeom()))
          new RailwaySectionAgent(railway);
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanCartacomAgentsInArea(CartAGenDataSet dataset,
      IPolygon polygon) {
    for (IBuilding build : dataset.getBuildings()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(build) == null) {
        if (polygon.intersects(build.getGeom())) {
          new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.BuildingAgent(
              build);
        }
      }
    }
  }

  /**
   * Creation of all Cartacom agents concerning the network faces of a dataset
   * @param dataset
   */
  public static void createNetworkFacesCartacomAgentsInArea(
      CartAGenDataSet dataset, IPolygon area) {

    System.out.println("createNetworkFacesCartacomAgentsInDataset");

    // Deleting network faces
    dataset.eraseFacesReseau();

    // Building topological map
    CarteTopo carteTopo = NetworkEnrichment.buildNetworksTopoMap(dataset, area);

    // Construction of the NetworkFaceAgents
    if (AgentUtil.logger.isDebugEnabled()) {
      AgentUtil.logger.debug("Building the NetworkFaceAgents");
    }

    // Loop through CarteTopo faces
    for (Face face : carteTopo.getPopFaces()) {
      // Gets the geometry of the face
      IPolygon polygon = face.getGeometrie();
      // Converts it into 2D
      try {
        polygon = (IPolygon) AdapterFactory.to2DGM_Object(polygon);
      } catch (Exception e) {
        AgentUtil.logger
            .error("Failed during conversion of face geometry into 2D");
        AgentUtil.logger.error(polygon.toString());
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
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) AgentUtil
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
          INetworkSectionAgent sectAgent = (INetworkSectionAgent) AgentUtil
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
          containedSmallCompacts.add((ISmallCompactAgent) AgentUtil
              .getCartAComAgentFromGeneObj(building));

          continue;
        }
        // building not completely included. Compute ratio included.
        IGeometry inter = building.getGeom().intersection(polygon);
        double ratio = 0.0;
        if (inter != null)
          ratio = inter.area() / (building.getGeom().area());
        // if the ratio is big enough, the building is considered
        // included
        if (ratio > 0.6) {
          containedSmallCompacts.add((ISmallCompactAgent) AgentUtil
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

  // /////////////////////////////////
  // CREATION OF AGENTS ABOVE GENE
  // OBJECTS IN A DATASET
  // /////////////////////////////////

  /**
   * Creation of all agents of a dataset
   * @param dataset
   */
  public static void createAgentsInDataset(CartAGenDataSet dataset) {

    if (dataset == null) {
      return;
    }

    /*
     * ProgressFrame progressFrame = new ProgressFrame("Creating agents...",
     * true); progressFrame.setVisible(true);
     */

    // Specifications loadin
    // progressFrame.setTextAndValue("Loading specifications", 0);
    AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

    // Relief elements
    // progressFrame.setTextAndValue("Creating relief agents", 10);
    AgentUtil.createReliefGaelAgentsInDataset(dataset);

    // Road network
    // progressFrame.setTextAndValue("Creating network agents", 30);
    AgentUtil.createRoadNetworkAgentsInDataset(dataset);

    // Hydro network
    AgentUtil.createHydroNetworkAgentsInDataset(dataset);

    // Railway network
    AgentUtil.createRailwayNetworkAgentsInDataset(dataset);

    // Electricity network
    AgentUtil.createElectricityNetworkAgentsInDataset(dataset);

    // Urban objects
    // progressFrame.setTextAndValue("Creating urban agents", 50);
    AgentUtil.createUrbanAgentsInDataset(dataset);

    // Network faces
    // progressFrame.setTextAndValue("Creating network faces agents", 70);
    // if (CartagenApplication.getInstance().isConstructNetworkFaces()) {
    AgentUtil.createNetworkFacesCartacomAgentsInDataset(dataset);
    // }

    // Mask of the dataset
    for (IMask mask : dataset.getMasks()) {
      if (AgentUtil.getCartAComAgentFromGeneObj(mask) == null) {
        new MaskAgent(mask);
      }
    }

    // Enrichment
    // progressFrame.setTextAndValue("Enriching agents", 90);
    AgentUtil.enrichGaelAgents(dataset);

    /*
     * // Initialisation of measures lists ListesMesures.initMesures();
     * 
     * 
     * progressFrame.setTextAndValue("Creation of agents complete !", 100);
     * progressFrame.setVisible(false); progressFrame = null;
     */

  }

  /**
   * Creation of all agents concerning the road network of a dataset
   * @param dataset
   */
  public static void createRoadNetworkAgentsInDataset(CartAGenDataSet dataset) {

    RoadNetworkAgent roadNetAgent = (RoadNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRoadNetwork());
    if (roadNetAgent == null) {
      roadNetAgent = new RoadNetworkAgent(dataset.getRoadNetwork());
    }

    // Sections
    for (IRoadLine road : dataset.getRoads()) {
      if (AgentUtil.getAgentFromGeneObj(road) == null) {
        IRoadSectionAgent roadAgent = new RoadSectionAgent(roadNetAgent, road);
        roadAgent.instantiateConstraints();
      }
      if (AgentUtil.getCartAComAgentFromGeneObj(road) == null) {
        new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RoadSectionAgent(
            road);
      }
    }

    // Nodes
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      if (AgentUtil.getAgentFromGeneObj(roadNode) == null) {
        new NetworkNodeAgent(roadNetAgent, roadNode);
      }
    }

    // Links between nodes and sections
    for (IRoadNode roadNode : dataset.getRoadNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(roadNode);
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection road : roadNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(road);
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
   * Creation of all agents concerning the hydro network of a dataset
   * @param dataset
   */
  public static void createHydroNetworkAgentsInDataset(
      CartAGenDataSet dataset) {

    HydroNetworkAgent hydroNetAgent = (HydroNetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getHydroNetwork());
    if (hydroNetAgent == null) {
      hydroNetAgent = new HydroNetworkAgent(dataset.getHydroNetwork());
    }

    // Sections
    for (IWaterLine river : dataset.getWaterLines()) {
      if (AgentUtil.getAgentFromGeneObj(river) == null) {
        IHydroSectionAgent riverAgent = new HydroSectionAgent(hydroNetAgent,
            river);
        riverAgent.instantiateConstraints();
      }
      if (AgentUtil.getCartAComAgentFromGeneObj(river) == null) {
        new RiverSectionAgent(river);
      }
    }

    // Nodes
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      if (AgentUtil.getAgentFromGeneObj(waterNode) == null) {
        new NetworkNodeAgent(hydroNetAgent, waterNode);
      }
    }

    // Surfaces
    for (IWaterArea surf : dataset.getWaterAreas()) {
      if (AgentUtil.getAgentFromGeneObj(surf) == null) {
        HydroSurfaceAgent surfAgent = new HydroSurfaceAgent(hydroNetAgent,
            surf);
        surfAgent.instantiateConstraints();
      }
    }

    // Links between nodes and sections
    for (IWaterNode waterNode : dataset.getWaterNodes()) {
      NetworkNodeAgent nodeAgent = (NetworkNodeAgent) AgentUtil
          .getAgentFromGeneObj(waterNode);
      Set<ISectionAgent> inSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getInSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        inSections.add(sectionAgent);
        sectionAgent.setFinalNode(nodeAgent);
      }
      nodeAgent.setInSections(inSections);
      Set<ISectionAgent> outSections = new HashSet<ISectionAgent>();
      for (INetworkSection river : waterNode.getOutSections()) {
        ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
            .getAgentFromGeneObj(river);
        outSections.add(sectionAgent);
        sectionAgent.setInitialNode(nodeAgent);
      }
      nodeAgent.setOutSections(outSections);
    }

    // Instanciation of constraints
    hydroNetAgent.instantiateConstraints();

    // Update of the layers related to hydro network agent
    // CartagenApplication.getInstance().getLayerGroup()
    // .getLayer(AgentLayerGroup.LAYER_ROAD_NETWORK_POINT_AGENT)
    // .setFeatures(hydroNetAgent.getPointAgents());
    // CartagenApplication.getInstance().getLayerGroup()
    // .getLayer(AgentLayerGroup.LAYER_ROAD_SEGMENT)
    // .setFeatures(hydroNetAgent.getSegments());

  }

  /**
   * Creation of all agents concerning the railway network of a dataset
   * @param dataset
   */
  public static void createRailwayNetworkAgentsInDataset(
      CartAGenDataSet dataset) {

    NetworkAgent railwayNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getRailwayNetwork());
    if (railwayNetAgent == null) {
      railwayNetAgent = new NetworkAgent(dataset.getRailwayNetwork());
    }

    // Sections
    for (IRailwayLine railway : dataset.getRailwayLines()) {
      if (AgentUtil.getAgentFromGeneObj(railway) == null) {
        new RailroadSectionAgent(railwayNetAgent, railway);
      }
      if (AgentUtil.getCartAComAgentFromGeneObj(railway) == null) {
        new RailwaySectionAgent(railway);
      }
    }

    // Nodes
    for (INetworkNode railwayNode : dataset.getRailwayNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(railwayNode) == null) {
        new NetworkNodeAgent(railwayNetAgent, railwayNode);
      }
    }

  }

  /**
   * Creation of all agents concerning the electricity network of a dataset
   * @param dataset
   */
  public static void createElectricityNetworkAgentsInDataset(
      CartAGenDataSet dataset) {

    NetworkAgent elecNetAgent = (NetworkAgent) AgentUtil
        .getAgentFromGeneObj(dataset.getElectricityNetwork());
    if (elecNetAgent == null) {
      elecNetAgent = new NetworkAgent(dataset.getElectricityNetwork());
    }

    // Sections
    for (IElectricityLine elecLine : dataset.getElectricityLines()) {
      if (AgentUtil.getAgentFromGeneObj(elecLine) == null) {
        new ElectricitySectionAgent(elecNetAgent, elecLine);
      }
    }

    // Nodes
    for (INetworkNode elecNode : dataset.getElectricityNetwork().getNodes()) {
      if (AgentUtil.getAgentFromGeneObj(elecNode) == null) {
        new NetworkNodeAgent(elecNetAgent, elecNode);
      }
    }

  }

  /**
   * Creation of all agents concerning the urban objects of a dataset
   * @param dataset
   */
  public static void createUrbanAgentsInDataset(CartAGenDataSet dataset) {

    // BUILDINGS
    for (IBuilding build : dataset.getBuildings()) {
      if (AgentUtil.getAgentFromGeneObj(build) == null) {
        BuildingAgent agent = new BuildingAgent(build, 0);
        agent.instantiateConstraints();
      }
      if (AgentUtil.getCartAComAgentFromGeneObj(build) == null) {
        new fr.ign.cogit.cartagen.agents.cartacom.agent.impl.BuildingAgent(
            build);
      }
    }

    // URBAN BLOCKS
    for (IUrbanBlock block : dataset.getBlocks()) {
      if (AgentUtil.getAgentFromGeneObj(block) == null) {
        BlockAgent agent = new BlockAgent(block);
        // Links to the buildings
        List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
        for (IUrbanElement build : block.getUrbanElements()) {
          IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
              .getAgentFromGeneObj(build);
          buildingAgent.setMesoAgent(agent);
          buildAgents.add(buildingAgent);
        }
        agent.setComponents(buildAgents);
        // Links to the delineating sections
        List<ISectionAgent> sectionAgents = new ArrayList<ISectionAgent>();
        for (INetworkSection section : block.getSurroundingNetwork()) {
          ISectionAgent sectionAgent = (ISectionAgent) AgentUtil
              .getAgentFromGeneObj(section);
          sectionAgents.add(sectionAgent);
        }
        agent.setSectionAgents(sectionAgents);
        agent.instantiateConstraints();
      }
    }

    // TOWNS
    for (ITown town : dataset.getTowns()) {
      if (AgentUtil.getAgentFromGeneObj(town) == null) {
        TownAgent agent = new TownAgent(town);
        // Links to the blocks
        List<IBlockAgent> blockAgents = new ArrayList<IBlockAgent>();
        for (IUrbanBlock block : town.getTownBlocks()) {
          IBlockAgent blockAgent = (IBlockAgent) AgentUtil
              .getAgentFromGeneObj(block);
          blockAgent.setMesoAgent(agent);
          blockAgents.add(blockAgent);
        }
        agent.setComponents(blockAgents);
        agent.instantiateConstraints();
      }
    }

    // URBAN ALIGNMENTS
    for (IUrbanAlignment align : dataset.getUrbanAlignments()) {
      if (AgentUtil.getAgentFromGeneObj(align) == null) {
        UrbanAlignmentAgent agent = new UrbanAlignmentAgent(align);
        // Links to the buildings
        List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
        for (IUrbanElement build : align.getUrbanElements()) {
          IUrbanElementAgent buildingAgent = (IUrbanElementAgent) AgentUtil
              .getAgentFromGeneObj(build);
          buildingAgent.addAlignment(agent);
          buildingAgent.getStructureAgents().add(agent);
          buildAgents.add(buildingAgent);
        }
        agent.setComponents(buildAgents);
        // Link to the block
        IBlockAgent blockAgent = (IBlockAgent) ((IUrbanElementAgent) AgentUtil
            .getAgentFromGeneObj(align.getUrbanElements().get(0)))
                .getMesoAgent();
        if (blockAgent != null) {
          blockAgent.getInternStructures().add(agent);
          agent.setMesoAgent(blockAgent);
        }
        agent.instantiateConstraints();
      }
    }

  }

  // /////////////////////////////////
  // CONSTRAINTS INSTANCIATION
  // AND AGENT SPECS
  // /////////////////////////////////

  /**
   * instancie les contraintes des agents du dataset courant en fonction des
   * specifications de la generalisation
   */
  public static void instanciateConstraints() {
    if (CartAGenDoc.getInstance().getCurrentDataset() != null) {
      AgentUtil.instanciateConstraints(
          CartAGenDoc.getInstance().getCurrentDataset());
    }
  }

  /**
   * instancie les contraintes des agents du dataset en fonction des
   * specifications de la generalisation
   */
  public static void instanciateConstraints(CartAGenDataSet dataset) {

    if (dataset == null) {
      return;
    }

    // buildings
    for (IBuilding a : dataset.getBuildings()) {
      ((BuildingAgent) AgentUtil.getAgentFromGeneObj(a))
          .instantiateConstraints();
    }

    // urban blocks
    for (IUrbanBlock a : dataset.getBlocks()) {
      ((BlockAgent) AgentUtil.getAgentFromGeneObj(a)).instantiateConstraints();
    }

    // towns
    for (ITown a : dataset.getTowns()) {
      ((TownAgent) AgentUtil.getAgentFromGeneObj(a)).instantiateConstraints();
    }

    // urban alignments
    for (IUrbanAlignment a : dataset.getUrbanAlignments()) {
      ((UrbanAlignmentAgent) AgentUtil.getAgentFromGeneObj(a))
          .instantiateConstraints();
    }

    // road network
    RoadNetworkAgent roadNet = (RoadNetworkAgent) AgentUtil.getAgentFromGeneObj(
        CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork());
    if (roadNet != null) {
      roadNet.instantiateConstraints();
    }

    // road lines
    for (IRoadLine tr : dataset.getRoads()) {
      ((IRoadSectionAgent) AgentUtil.getAgentFromGeneObj(tr))
          .instantiateConstraints();
    }

    // hydro network
    HydroNetworkAgent hydroNet = (HydroNetworkAgent) AgentUtil
        .getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork());
    if (hydroNet != null) {
      hydroNet.instantiateConstraints();
    }

    // water lines
    for (IWaterLine tr : dataset.getWaterLines()) {
      GeographicAgentGeneralisation hydroAgent = AgentUtil
          .getAgentFromGeneObj(tr);
      if (hydroAgent != null)
        ((IGeographicObjectAgentGeneralisation) hydroAgent)
            .instantiateConstraints();
    }

    // water areas
    for (IWaterArea se : dataset.getWaterAreas()) {
      HydroSurfaceAgent agent = ((HydroSurfaceAgent) AgentUtil
          .getAgentFromGeneObj(se));
      if (agent != null)
        agent.instantiateConstraints();
    }

    // relief
    ReliefFieldAgent reliefField = (ReliefFieldAgent) AgentUtil
        .getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField());
    if (reliefField != null) {
      reliefField.instanciateConstraints();
    }

    // land use
    LandUseFieldAgent landUseField = (LandUseFieldAgent) AgentUtil
        .getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getLandUseField());
    if (landUseField != null) {
      landUseField.instanciateConstraints();
    }

    // admin
    AdministrativeFieldAgent adminField = (AdministrativeFieldAgent) AgentUtil
        .getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getAdminField());
    if (adminField != null) {
      adminField.instanciateConstraints();
    }

  }

  /**
   * Path to the xml configuration file for agent specifications
   */
  private static String configAgentFile = "/configurationAgent.xml";

  public static String getConfigAgentFile() {
    return AgentUtil.configAgentFile;
  }

  /**
   * Load specifications for agent generalisation by reading an XML
   * configuration file
   */
  public static void loadAgentSpecifications(String specificationFile) {

    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(AgentUtil.class.getResourceAsStream(specificationFile));
    } catch (FileNotFoundException e) {
      AgentUtil.logger.error("Fichier non trouvé: " + specificationFile);
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      AgentUtil.logger
          .error("Erreur lors de la lecture de: " + specificationFile);
      return;
    }

    Element configurationGeneralisationMirageXML = (Element) docXML
        .getElementsByTagName("configurationAgentCartagen").item(0);
    if (configurationGeneralisationMirageXML == null) {
      return;
    }

    // general
    Element generalXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("general").item(0);
    if (generalXML != null) {
      Element elXML = null;

      // typeCycleDeVie
      elXML = (Element) generalXML.getElementsByTagName("typeCycleDeVie")
          .item(0);
      if (elXML != null) {
        int type = Integer.parseInt(elXML.getFirstChild().getNodeValue());

        // choisit le cycle de vie en fonction des specifications
        if (type == 1) {
          BasicLifeCycle.getInstance().setStatesMaxNumber(
              AgentSpecifications.getNB_MAX_ETATS_A_VISITES());
          BasicLifeCycle.getInstance()
              .setStoreStates(AgentSpecifications.STORE_STATES);
          AgentSpecifications.setLifeCycle(BasicLifeCycle.getInstance());
        } else if (type == 2) {
          TreeExplorationLifeCycle.getInstance().setStatesMaxNumber(
              AgentSpecifications.getNB_MAX_ETATS_A_VISITES());
          TreeExplorationLifeCycle.getInstance()
              .setStoreStates(AgentSpecifications.STORE_STATES);
          AgentSpecifications
              .setLifeCycle(TreeExplorationLifeCycle.getInstance());
        } else {
          AgentUtil.logger.fatal("Error: lifecycle type not supported " + type);
        }
      }

      // seuilSatisfactionValidite
      elXML = (Element) generalXML
          .getElementsByTagName("seuilSatisfactionValidite").item(0);
      if (elXML != null) {
        double treshold = Double
            .parseDouble(elXML.getFirstChild().getNodeValue());
        AgentSpecifications.setValiditySatisfactionTreshold(treshold);
        TreeExplorationLifeCycle.setValiditySatisfactionTreshold(treshold);
        BasicLifeCycle.setValiditySatisfactionTreshold(treshold);
      }

      // nb max d'etats a visiter
      elXML = (Element) generalXML.getElementsByTagName("nbMaxEtatsAVisiter")
          .item(0);
      if (elXML != null) {
        AgentSpecifications.setNB_MAX_ETATS_A_VISITES(
            Integer.parseInt(elXML.getFirstChild().getNodeValue()));
      }

    }

    // contraintes
    Element contrainteXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("contrainte").item(0);
    if (contrainteXML != null) {

      Element contXML = null;
      Element contImpXML = null;
      Element submicroXML = null;
      Element elXML = null;

      elXML = (Element) contrainteXML
          .getElementsByTagName("instancierContraintesAuDemarrage").item(0);
      if (elXML != null) {
        AgentSpecifications.STARTUP_INSTANCIATION = Boolean
            .parseBoolean(elXML.getFirstChild().getNodeValue());
      }

      // bati
      Element batiXML = (Element) contrainteXML.getElementsByTagName("bati")
          .item(0);
      if (batiXML != null) {

        // taille
        contXML = (Element) batiXML.getElementsByTagName("taille").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_SIZE_CONSTRAINT = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // granularite
        contXML = (Element) batiXML.getElementsByTagName("granularite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_GRANULARITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BULDING_GRANULARITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // equarrite
        contXML = (Element) batiXML.getElementsByTagName("equarrite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_SQUARENESS = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_SQUARENESS_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // largeur locale
        contXML = (Element) batiXML.getElementsByTagName("largeurLocale")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_LOCAL_WIDTH = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // convexite
        contXML = (Element) batiXML.getElementsByTagName("convexite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_CONVEXITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_CONVEXITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML
              .getElementsByTagName("pointSatisfaction").item(0);
          if (contImpXML != null) {
            AgentSpecifications.CONVEXITE_BUILDING_POINT_SATISFACTION = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // elongation
        contXML = (Element) batiXML.getElementsByTagName("elongation").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_ELONGATION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_ELONGATION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML
              .getElementsByTagName("pointSatisfaction").item(0);
          if (contImpXML != null) {
            AgentSpecifications.ELONGATION_BUILDING_POINT_SATISFACTION = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // orientation
        contXML = (Element) batiXML.getElementsByTagName("orientation").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_ORIENTATION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_ORIENTATION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML
              .getElementsByTagName("pointSatisfaction").item(0);
          if (contImpXML != null) {
            AgentSpecifications.ORIENTATION_BUILDING_POINT_SATISFACTION = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // altitude
        contXML = (Element) batiXML.getElementsByTagName("altitude").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_ALTITUDE = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_ALTITUDE_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML
              .getElementsByTagName("pointSatisfaction").item(0);
          if (contImpXML != null) {
            AgentSpecifications.HEIGHT_DIFFERENCE_POINT_SATISFACTION = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // occ sol
        contXML = (Element) batiXML.getElementsByTagName("occSol").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_LANDUSE = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_LANDUSE_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // proximite
        contXML = (Element) batiXML
            .getElementsByTagName("proximiteBatimentsIlot").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BLOCK_BUILDING_PROXIMITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // densite ilot
        contXML = (Element) batiXML.getElementsByTagName("densiteBatimentsIlot")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BLOCK_BUILDING_DENSITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // repartition spatiale batiments
        contXML = (Element) batiXML
            .getElementsByTagName("repartitionSpatialeBatiments").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BUILDING_SPATIAL_DISTRIBUTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BUILDING_SPATIAL_DISTRIBUTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // conservation grands batiments
        contXML = (Element) batiXML
            .getElementsByTagName("conservationGrandsBatiments").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.LARGE_BUILDING_PRESERVATION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.LARGE_BUILDING_PRESERVATION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // satisfaction batiments ilots
        contXML = (Element) batiXML
            .getElementsByTagName("satisfactionBatimentsIlot").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.BLOCK_MICRO_SATISFACTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.BLOCK_MICRO_SATISFACTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // satisfaction ilots villes
        contXML = (Element) batiXML
            .getElementsByTagName("satisfactionIlotsVille").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.TOWN_BLOCK_SATISFACTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.TOWN_BLOCK_SATISFACTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

      }

      // routier
      Element routierXML = (Element) contrainteXML
          .getElementsByTagName("routier").item(0);
      if (routierXML != null) {

        // satisfactionComposantsReseau
        contXML = (Element) routierXML
            .getElementsByTagName("satisfactionComposantsReseau").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.ROAD_NETWORK_MICRO_SATISFACTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.ROAD_NETWORK_MICRO_SATISFACTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // empatement troncons
        contXML = (Element) routierXML
            .getElementsByTagName("empatementTroncons").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.ROAD_COALESCENCE = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.ROAD_COALESCENCE_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // controle de la deformation des troncons
        contXML = (Element) routierXML
            .getElementsByTagName("controleDeformation").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.ROAD_CONTROL_DISTORTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // presence impasses
        contXML = (Element) routierXML.getElementsByTagName("presenceImpasses")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.DEAD_END_ROADS = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.DEAD_END_ROADS_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // densite
        contXML = (Element) routierXML.getElementsByTagName("densite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.ROAD_DENSITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.ROAD_DENSITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

      }

      // hydro
      Element hydroXML = (Element) contrainteXML.getElementsByTagName("hydro")
          .item(0);
      if (hydroXML != null) {

        // satisfactionComposantsReseau
        contXML = (Element) hydroXML
            .getElementsByTagName("satisfactionComposantsReseau").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.RIVER_NETWORK_MICRO_SATISFACTION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.RIVER_NETWORK_MICRO_SATISFACTION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // empatement troncons
        contXML = (Element) hydroXML.getElementsByTagName("empatementTroncons")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.RIVER_COALESCENCE = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.RIVER_COALESCENCE_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // proximite routier
        contXML = (Element) hydroXML.getElementsByTagName("proximiteRoutier")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.RIVER_ROAD_PROXIMITY = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // ecoulement
        contXML = (Element) hydroXML.getElementsByTagName("ecoulement").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.RIVER_FLOW_PRESERVATION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

        // platitude lac
        contXML = (Element) hydroXML.getElementsByTagName("platitudeLac")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("contraindre").item(0);
          if (elXML != null) {
            AgentSpecifications.LAKE_FLATNESS_PRESERVATION = Boolean
                .parseBoolean(elXML.getFirstChild().getNodeValue());
          }

          contImpXML = (Element) contXML.getElementsByTagName("importance")
              .item(0);
          if (contImpXML != null) {
            AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP = Double
                .parseDouble(contImpXML.getFirstChild().getNodeValue());
          }
        }

      }

      // relief
      Element reliefXML = (Element) contrainteXML.getElementsByTagName("relief")
          .item(0);
      if (reliefXML != null) {

        // point
        submicroXML = (Element) reliefXML.getElementsByTagName("point").item(0);
        if (submicroXML != null) {
          // position
          contXML = (Element) submicroXML.getElementsByTagName("position")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.RELIEF_POSITION_POINT = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_POSITION_POINT_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

        }

        // segmentCN
        submicroXML = (Element) reliefXML.getElementsByTagName("segmentCN")
            .item(0);
        if (submicroXML != null) {
          // longueur
          contXML = (Element) submicroXML.getElementsByTagName("longueur")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.CONTOUR_LINE_SEGMENT_LENGTH = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.CONTOUR_LINE_SEGMENT_LENGTH_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

          // orientation
          contXML = (Element) submicroXML.getElementsByTagName("orientation")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.CONTOUR_LINE_SEGMENT_ORIENTATION = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.CONTOUR_LINE_SEGMENT_ORIENTATION_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }
        }

        // segment
        submicroXML = (Element) reliefXML.getElementsByTagName("segment")
            .item(0);
        if (submicroXML != null) {
          // longueur
          contXML = (Element) submicroXML.getElementsByTagName("longueur")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.RELIEF_SEGMENT_LENGTH = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_SEGMENT_LENGTH_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

          // orientation
          contXML = (Element) submicroXML.getElementsByTagName("orientation")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.RELIEF_SEGMENT_ORIENTATION = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_SEGMENT_ORIENTATION_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }
        }

        // triangle
        submicroXML = (Element) reliefXML.getElementsByTagName("triangle")
            .item(0);
        if (submicroXML != null) {
          // aire
          contXML = (Element) submicroXML.getElementsByTagName("aire").item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.RELIEF_TRIANGLE_AREA = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_TRIANGLE_AREA_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

          // centre de gravite
          contXML = (Element) submicroXML.getElementsByTagName("centreG")
              .item(0);
          if (contXML != null) {
            elXML = (Element) contXML.getElementsByTagName("contraindre")
                .item(0);
            if (elXML != null) {
              AgentSpecifications.RELIEF_TRIANGLE_CENTROID = Boolean
                  .parseBoolean(elXML.getFirstChild().getNodeValue());
            }

            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_TRIANGLE_CENTROID_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

          // altitudeBati
          contXML = (Element) submicroXML.getElementsByTagName("altitudeBati")
              .item(0);
          if (contXML != null) {
            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_ALTITUDE_BUILDING_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }

          // ecoulementHydro
          contXML = (Element) submicroXML
              .getElementsByTagName("ecoulementHydro").item(0);
          if (contXML != null) {
            contImpXML = (Element) contXML.getElementsByTagName("importance")
                .item(0);
            if (contImpXML != null) {
              AgentSpecifications.RELIEF_RIVER_FLOW_IMP = Double
                  .parseDouble(contImpXML.getFirstChild().getNodeValue());
            }
          }
        }
      }

      // occupation du sol
      Element occSolXML = (Element) contrainteXML.getElementsByTagName("occSol")
          .item(0);
      if (occSolXML != null) {
      }

      if (AgentUtil.logger.isDebugEnabled()) {
        AgentUtil.logger
            .debug("fin chargement de la configuration de généralisation");
      }
    }

  }

}
