package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.core.agent.IBlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.SmallCompactAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseParcelAgent;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.UrbanAlignmentsMeasures;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author JRenard
 */

public abstract class UrbanElementAgent extends SmallCompactAgent
    implements IUrbanElementAgent {

  @Override
  public IPolygon getSymbolGeom() {
    return this.getFeature().getSymbolGeom();
  }

  @Override
  public IUrbanElement getFeature() {
    return (IUrbanElement) super.getFeature();
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
  }

  /**
   * The potential alignment the building takes part
   */
  private ArrayList<UrbanAlignmentAgent> alignments = new ArrayList<UrbanAlignmentAgent>();

  @Override
  public ArrayList<UrbanAlignmentAgent> getAlignments() {
    return this.alignments;
  }

  @Override
  public void setAlignments(ArrayList<UrbanAlignmentAgent> alignments) {
    this.alignments = alignments;
  }

  @Override
  public void addAlignment(UrbanAlignmentAgent alignment) {
    this.alignments.add(alignment);
  }

  @Override
  public double getGoalArea() {
    return BlockBuildingsMeasures.getBuildingGoalArea(this.getFeature());
  }

  /**
   * @return the part of the urban element which is overlapping with the other
   *         objects buildings and roads of the block - takes into account a
   *         separation distance.
   */
  @Override
  public IGeometry getOverlappingGeometry() {
    return BlockBuildingsMeasures.getBuildingOverlappingGeometry(
        this.getFeature(), ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  /**
   * @return the urban element overlapping rates, within [0,1]
   */
  @Override
  public double getOverlappingRate() {
    return BlockBuildingsMeasures.getBuildingOverlappingRate(this.getFeature(),
        ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  /**
   * @return la partie du batiment qui se superpose avec les autres batiments
   *         d'un alignement
   */
  @Override
  public IGeometry getOverlappingGeometryInAlignment(
      UrbanAlignmentAgent align) {
    return UrbanAlignmentsMeasures.getOverlappingGeometryInAlignment(
        this.getFeature(), align.getFeature());
  }

  /**
   * @return the urban element overlapping rates in an alignment, within [0,1]
   */
  @Override
  public double getOverlappingRateInAlignment(UrbanAlignmentAgent align) {
    return UrbanAlignmentsMeasures.getBuildingOverlappingRateInAlignment(
        this.getFeature(), align.getFeature());
  }

  /**
   * @return la partie de l'element urbain qui se superpose avec les autres
   *         batiments de l'ilot
   */
  @Override
  public IGeometry getOverlappingGeometryBetweenUrbanElements() {
    return BlockBuildingsMeasures
        .getBuildingOverlappingGeometryWithOtherBuildings(this.getFeature(),
            ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  @Override
  public double getOverlappingRateBetweenBuildings() {
    return BlockBuildingsMeasures.getBuildingOverlappingRateWithOtherBuildings(
        this.getFeature(), ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  /**
   * @return the id of the urban element's block
   */
  public int getBlockId() {
    return ((BlockAgent) this.getMesoAgent()).getId();
  }

  public double getDistanceLimiteDomaineOccSol(LandUseParcelAgent dom) {
    return dom.getGeom().distance(this.getSymbolGeom());
  }

  @Override
  public boolean isOverlappingMeso() {
    return BlockBuildingsMeasures.isBuildingOverlappingBlock(this.getFeature(),
        ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  @Override
  public IGeometry getDirectOverlapGeometryBetweenUrbanElements() {
    return BlockBuildingsMeasures
        .getBuildingDirectOverlapGeometryWithOtherBuildings(this.getFeature(),
            ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

  @Override
  public double getDirectOverlapRateBetweenUrbanElements() {
    return BlockBuildingsMeasures
        .getBuildingDirectOverlapRateWithOtherBuildings(this.getFeature(),
            ((IBlockAgent) this.getMesoAgent()).getFeature());
  }

}
