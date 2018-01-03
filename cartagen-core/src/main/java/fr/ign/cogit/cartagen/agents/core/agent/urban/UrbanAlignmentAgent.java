package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.InternStructureAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment.BuildingNumberPreservation;
import fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment.BuildingsOrientation;
import fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment.InternProximity;
import fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment.ShapeLineCorrespondance;
import fr.ign.cogit.cartagen.agents.core.constraint.urbanalignment.SpatialRepartition;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.UrbanAlignmentsMeasures;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/*
 * ###### IGN / CartAGen ###### Title: AlignmentAgent Description: Agent
 * representing an alignment of buidings into a urban block Author: J. Renard
 * Date: 02/05/2011
 */

public class UrbanAlignmentAgent extends InternStructureAgentGeneralisation {

  /**
   * The urban elements (mostly buildings) composing the alignment
   */
  private List<IUrbanElementAgent> components = new ArrayList<IUrbanElementAgent>();

  @Override
  public List<IUrbanElementAgent> getComponents() {
    return this.components;
  }

  @Override
  public void setComponents(List<? extends GeographicObjectAgent> components) {
    List<IUrbanElementAgent> list = new ArrayList<IUrbanElementAgent>();
    for (GeographicObjectAgent obj : components) {
      if (obj instanceof IUrbanElementAgent) {
        list.add((IUrbanElementAgent) obj);
      }
    }
    this.components = list;
  }

  /**
   * Alignment shape line
   * @return
   */
  public ILineString getShapeLine() {
    return this.getFeature().getShapeLine();
  }

  /**
   * Alignment initial and final elements
   */
  public IUrbanElement getInitialElement() {
    return this.getFeature().getInitialElement();
  }

  public IUrbanElement getFinalElement() {
    return this.getFeature().getFinalElement();
  }

  /**
   * Alignment geometry surrounding buildings
   */
  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  /***
   * Alignment geneObj
   */
  @Override
  public IUrbanAlignment getFeature() {
    return (IUrbanAlignment) super.getFeature();
  }

  @Override
  public AgentSatisfactionState activate() throws InterruptedException {
    this.computeShapeLine();
    return super.activate();
  }

  /**
   * cree un agent alignement a partir d'un objet alignement de batiments
   * @param alignment l'objet alignement
   * @param buildings les agents batiments constituant l'alignement
   */
  public UrbanAlignmentAgent(IUrbanAlignment alignment,
      List<IUrbanElementAgent> buildings) {
    super();

    // liaison avec le feature pour visualisation
    this.setFeature(alignment);

    // liaison avec les micros
    this.setComponents(buildings);
    for (IUrbanElementAgent build : buildings) {
      build.getStructureAgents().add(this);
      build.addAlignment(this);
      this.getFeature().getUrbanElements().add(build.getFeature());
    }

    // liaison avec le meso
    if (buildings.get(0).getMesoAgent() != null) {
      buildings.get(0).getMesoAgent().getInternStructures().add(this);
      this.setMesoAgent(buildings.get(0).getMesoAgent());
    }

    // Computation of the characteristics
    this.computeInitialAndFinalElements();
    this.computeShapeLine();
    this.getFeature().setInitialShapeLine(
        (ILineString) this.getFeature().getShapeLine().clone());

    // Re-ordering of the list of components
    List<IUrbanElementAgent> orderedComponents = new ArrayList<IUrbanElementAgent>();
    for (IUrbanElement build : this.getFeature().getUrbanElements()) {
      orderedComponents
          .add((IUrbanElementAgent) AgentUtil.getAgentFromGeneObj(build));
    }
    this.setComponents(orderedComponents);

    // Instanciation des contraintes
    this.instantiateConstraints();

  }

  /**
   * cree un agent alignement a partir d'un objet alignement de batiments
   * @param alignment l'objet alignement
   */
  public UrbanAlignmentAgent(IUrbanAlignment alignment) {
    super();
    this.setFeature(alignment);
  }

  public UrbanAlignmentAgent(List<IUrbanElementAgent> buildings) {
    this(
        CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
            .getGeneObjImpl().getCreationFactory().createUrbanAlignment(),
        buildings);
  }

  /**
   * Destroy the urban alignment, dealing with all consequences on meso and
   * components
   */
  public void destroy() {

    // Liaison avec le meso
    this.getMesoAgent().getInternStructures().remove(this);

    // Liaison avec les composants
    for (IUrbanElementAgent build : this.components) {
      build.getStructureAgents().remove(this);
      build.getAlignments().remove(this);
    }
    this.setComponents(new ArrayList<IUrbanElementAgent>());

    // Feature
    this.setFeature(null);

  }

  /**
   * computes the shape line of the alignment based on its buildings
   */
  public void computeShapeLine() {
    this.getFeature().computeShapeLine();
  }

  /**
   * determines the initial and final urban elements of the alignment
   */
  public void computeInitialAndFinalElements() {
    this.getFeature().computeInitialAndFinalElements();
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  public double getBuildingsOverlappingRateMean() {
    return UrbanAlignmentsMeasures
        .getBuildingsOverlappingRateMean(this.getFeature());
  }

  /**
   * @return the buildings overlapping rates sigma, within [0,1]
   */
  public double getBuildingsOverlappingRateSigma() {
    return UrbanAlignmentsMeasures
        .getBuildingsOverlappingRateSigma(this.getFeature());
  }

  /**
   * @return the buildings overlapping rates mean taking into account their
   *         enlargement, within [0,1]
   */
  public double getEnlargedBuildingsOverlappingRateMean() {
    return UrbanAlignmentsMeasures
        .getEnlargedBuildingsOverlappingRateMean(this.getFeature());
  }

  /**
   * @return the buildings overlapping rates sigma taking into account their
   *         enlargement, within [0,1]
   */
  public double getEnlargedBuildingsOverlappingRateSigma() {
    return UrbanAlignmentsMeasures
        .getEnlargedBuildingsOverlappingRateSigma(this.getFeature());
  }

  /**
   * @return the buildings centroid distances sigma
   */
  public double getBuildingsCentroidsDistanceSigma() {
    return UrbanAlignmentsMeasures
        .getBuildingsCentroidsDistanceSigma(this.getFeature());
  }

  /**
   * @return the mean orientation factor of buildings compared to their ideal
   *         orientation in the alignment
   */
  public double getBuildingsMeanOrientationFactor() {
    return UrbanAlignmentsMeasures
        .getBuildingsMeanOrientationFactor(this.getFeature());
  }

  /**
   * @return the part of the urban alignment which is overlapping with the roads
   *         of the block
   */
  public IGeometry getRoadOverlappingGeometry() {
    return UrbanAlignmentsMeasures.getRoadOverlappingGeometry(this.getFeature(),
        ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  /**
   * @return the urban element overlapping rates, within [0,1]
   */
  public double getRoadOverlappingRate() {
    return UrbanAlignmentsMeasures.getRoadOverlappingRate(this.getFeature(),
        ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    this.ajouterContrainteSatisfactionComposantsStructure(5.0);
    this.ajouterContrainteProximite(AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP);
    this.ajouterContrainteRepartitionSpatiale(5.0);
    this.ajouterContrainteShapeLineCorrespondance(5.0);
    this.ajouterContrainteOrientationBatiments(5.0);
    this.ajouterContrainteConservationBatiments(3.0);
  }

  public void ajouterContrainteProximite(double importance) {
    new InternProximity(this, importance);
  }

  public void ajouterContrainteRepartitionSpatiale(double importance) {
    new SpatialRepartition(this, importance);
  }

  public void ajouterContrainteShapeLineCorrespondance(double importance) {
    new ShapeLineCorrespondance(this, importance);
  }

  public void ajouterContrainteOrientationBatiments(double importance) {
    new BuildingsOrientation(this, importance);
  }

  public void ajouterContrainteConservationBatiments(double importance) {
    new BuildingNumberPreservation(this, importance);
  }

}
