/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.block.BigBuildingsPreservation;
import fr.ign.cogit.cartagen.agents.core.constraint.block.BuildingsSpatialDistribution;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Density;
import fr.ign.cogit.cartagen.agents.core.constraint.block.Proximity;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.DensityMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.UrbanAlignmentsMeasures;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * @author julien Gaffuri
 * 
 */
public class BlockAgent extends MesoAgentGeneralisation<IUrbanElementAgent>
    implements IBlockAgent {
  private static Logger logger = Logger.getLogger(BlockAgent.class.getName());

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IUrbanBlock getFeature() {
    return (IUrbanBlock) super.getFeature();
  }

  /**
   * cree un ilot dans une ville
   * @param block l'objet java ilot
   * @param townAgent l'agent ville de l'ilot
   * @param id l'identifiant de l'ilot
   */
  public BlockAgent(IUrbanBlock block, ITownAgent townAgent, int id) {
    super();
    this.setFeature(block);
    this.setId(id);

    this.setInitialGeom(block.getGeom());

    if (BlockAgent.logger.isDebugEnabled()) {
      BlockAgent.logger.debug("liaison entre ilot et ville");
    }
    if (townAgent != null) {
      townAgent.getComponents().add(this);
      this.setMesoAgent(townAgent);
    }
  }

  /**
   * cree un ilot dans une ville
   * @param poly l'objet java ilot
   * @param agentVille l'agent ville de l'ilot
   * @param urbanElements les batiments de l'ilot
   * @param troncons les troncons de l'ilot
   * @param id l'identifiant de l'ilot
   */
  public BlockAgent(IUrbanBlock block, ITownAgent agentVille,
      IFeatureCollection<IUrbanElement> urbanElements,
      IFeatureCollection<INetworkSection> troncons, int id) {
    this(block, agentVille, id);

    // lien avec batiments
    for (IUrbanElement bat : urbanElements) {
      this.getComponents()
          .add((IUrbanElementAgent) AgentUtil.getAgentFromGeneObj(bat));
      ((IUrbanElementAgent) AgentUtil.getAgentFromGeneObj(bat))
          .setMesoAgent(this);
    }

    // lien avec troncons
    for (INetworkSection sec : troncons) {
      super.addSectionAgent((SectionAgent) AgentUtil.getAgentFromGeneObj(sec));
    }
  }

  /**
   * cree un ilot dans une ville
   * @param poly l'objet java ilot
   * @param agentVille l'agent ville de l'ilot
   * @param urbanElements les agents batiments de l'ilot
   * @param troncons les agents troncons de l'ilot
   * @param id l'identifiant de l'ilot
   */
  public BlockAgent(IUrbanBlock block, ITownAgent agentVille,
      Set<IUrbanElementAgent> urbanElements, Set<ISectionAgent> troncons,
      int id) {
    this(block, agentVille, id);

    // lien avec batiments
    for (IUrbanElementAgent bat : urbanElements) {
      this.getComponents().add(bat);
      bat.setMesoAgent(this);
      this.getFeature().getUrbanElements().add(bat.getFeature());
      (bat.getFeature()).setBlock(this.getFeature());
    }

    // lien avec troncons
    for (ISectionAgent sec : troncons) {
      super.addSectionAgent(sec);
    }
  }

  public BlockAgent(IUrbanBlock block) {
    this(block, (ITownAgent) AgentUtil.getAgentFromGeneObj(block.getTown()),
        block.getUrbanElements(), block.getSurroundingNetwork(), block.getId());
  }

  @Override
  public List<ISectionAgent> getSectionAgents() {
    return super.getSectionAgents();
  }

  private double initialDensity = 0.0;

  @Override
  public double getInitialDensity() {
    return this.initialDensity;
  }

  /**
   * @return computes the block's initial density
   */
  @Override
  public void computeInitialDensity() {
    this.initialDensity = DensityMeasures
        .getBlockBuildingsDensity(this.getFeature());
  }

  /**
   * @return the block's simulated density
   */
  @Override
  public double getSimulatedDensity() {
    return DensityMeasures.getBlockBuildingsSimulatedDensity(this.getFeature());
  }

  /**
   * @return the block's simulated density taking onto account all iniital
   *         buildings, even if deleted
   */
  @Override
  public double getInitialSimulatedDensity() {
    return DensityMeasures
        .getBlockBuildingsInitialSimulatedDensity(this.getFeature());
  }

  @Override
  public void computeSatisfaction() {
    if (this.isColored()) {
      this.setSatisfaction(100.0);
      if (BlockAgent.logger.isDebugEnabled()) {
        BlockAgent.logger.debug("    ilot grise: S=100");
      }
      return;
    }
    super.computeSatisfaction();
  }

  @Override
  public void goBackToInitialState() {
    this.setColor(null);
    super.goBackToInitialState();
  }

  @Override
  public void setColor(Color color) {
    super.setColor(color);
    if (color == null) {
      this.getFeature().setColored(false);
    } else {
      this.getFeature().setColored(true);
    }
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
    BlockBuildingsMeasures.cleanBlockDecomposition(this.getFeature());
    for (GeographicObjectAgent ago : this.getComponents()) {
      UrbanElementAgent b = (UrbanElementAgent) ago;
      if (b.getSegmentsProximite() != null) {
        b.getSegmentsProximite().clear();
      }
    }
  }

  /**
   * 
   * renvoit le meilleur batiment a supprimer c'est celui dont le cout de
   * suppression est le plus grand suppose que la triangulation de l'ilot a ete
   * effectuee
   * 
   * @return le meilleur batiment a supprimer
   */
  @Override
  public IBuildingAgent getNextBuildingToRemove(double distanceMax) {
    IBuilding build = BlockBuildingsMeasures
        .getNextBuildingToRemoveInBlock(this.getFeature(), distanceMax);
    return (IBuildingAgent) AgentUtil.getAgentFromGeneObj(build);
  }

  @Override
  public IBuildingAgent getSmallestBuilding() {
    IBuilding build = BlockBuildingsMeasures
        .getBlockSmallestBuilding(this.getFeature());
    return (IBuildingAgent) AgentUtil.getAgentFromGeneObj(build);
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  @Override
  public double getBuildingsOverlappingRateMean() {
    return BlockBuildingsMeasures
        .getBuildingsOverlappingRateMean(this.getFeature());
  }

  @Override
  public IBuildingAgent getMaxOverlapBuilding() {
    IBuilding build = BlockBuildingsMeasures
        .getBlockMaxOverlapBuilding(this.getFeature());
    return (IBuildingAgent) AgentUtil.getAgentFromGeneObj(build);
  }

  /**
   * @return the ratio of overlapped alignments inside the block
   */
  @Override
  public double getOverlappedAlignmentsRatio() {
    return UrbanAlignmentsMeasures
        .getBlockOverlappedAlignmentsRatio(this.getFeature());
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (AgentSpecifications.SATISFACTION_BATIMENTS_ILOT) {
      this.ajouterContrainteSatisfactionComposants(
          AgentSpecifications.SATISFACTION_BATIMENTS_ILOT_IMP);
      this.ajouterContrainteSatisfactionStructuresInternes(
          AgentSpecifications.SATISFACTION_BATIMENTS_ILOT_IMP);
    }
    if (AgentSpecifications.PROXIMITE_BATIMENT) {
      this.ajouterContrainteProximite(
          AgentSpecifications.PROXIMITE_BATIMENT_IMP);
    }
    if (AgentSpecifications.DENSITE_ILOT_BATIMENT) {
      this.ajouterContrainteDensite(
          AgentSpecifications.DENSITE_ILOT_BATIMENT_IMP);
    }
    if (AgentSpecifications.REPARTITION_SPATIALE_BATIMENT) {
      this.ajouterContrainteRepartitionSpatiale(
          AgentSpecifications.REPARTITION_SPATIALE_BATIMENT_IMP);
    }
    if (AgentSpecifications.CONSERVATION_GRANDS_BATIMENTS) {
      this.ajouterContrainteConservationGdBatiments(
          AgentSpecifications.CONSERVATION_GRANDS_BATIMENTS_IMP);
    }
  }

  public void ajouterContrainteDensite(double importance) {
    new Density(this, importance);
  }

  public void ajouterContrainteProximite(double importance) {
    new Proximity(this, importance);
  }

  public void ajouterContrainteRepartitionSpatiale(double importance) {
    new BuildingsSpatialDistribution(this, importance);
  }

  public void ajouterContrainteConservationGdBatiments(double importance) {
    new BigBuildingsPreservation(this, importance);
  }

  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
    System.out.println("nb troncons=" + this.getSectionAgents().size());
  }

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj) {
    // Do nothing as default
  }

  /**
   * Return the standard deviation of the area of the buildings of this block.
   * @return
   */
  public double areaStandardDeviation() {
    // compute the expected value for the area and the area^2
    double expectedValue = 0;
    double expectedValue2 = 0;
    for (IUrbanElementAgent buildingAgent : this.getComponents()) {
      expectedValue += buildingAgent.getGeom().area();
      expectedValue2 += Math.pow(buildingAgent.getGeom().area(), 2);
    }
    expectedValue = expectedValue / this.getComponents().size();
    expectedValue2 = expectedValue2 / this.getComponents().size();
    // compute and return the standard derivation.
    return Math.sqrt(expectedValue2 - Math.pow(expectedValue, 2));

  }

  private Boolean grayingNecessary;

  public void setGrayingNecessary(Boolean grayingNecessary) {
    this.grayingNecessary = grayingNecessary;
  }

  /**
   * Uses the town ELECTRE TRI method to find blocks to gray
   * @return
   */
  public boolean isGrayingNecessary() {
    if (grayingNecessary == null) {
      IUrbanBlock block = this.getFeature();
      ITown town = block.getTown();
      grayingNecessary = town.isTownCentre(block);
    }
    return grayingNecessary;
  }
}
