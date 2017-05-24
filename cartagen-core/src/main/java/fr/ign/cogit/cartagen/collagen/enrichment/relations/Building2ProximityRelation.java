package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.spatialrelation.relation.ProximityBetweenBuildings;

public class Building2ProximityRelation extends CollaGenRelation {

  private double distMin, maxDist;
  private ProximityBetweenBuildings spatialRelation;

  /**
   * Default constructor from its properties.
   * 
   * @param obj1
   * @param obj2
   * @param concept le concept ontologique qu'implémente cette classe de
   *          relation.
   */
  public Building2ProximityRelation(IGeneObj obj1, IGeneObj obj2,
      GeographicRelation concept, double maxDist) {
    super(obj1, obj2, concept);
    this.setMaxDist(maxDist);
    this.spatialRelation = new ProximityBetweenBuildings(obj1, obj2, maxDist);
    this.distMin = (Double) spatialRelation.getDistProperty().getValue();
    this.setGeom(spatialRelation.getGeom());
  }

  public double getDistMin() {
    return distMin;
  }

  public void setDistMin(double distMin) {
    this.distMin = distMin;
  }

  @Override
  public int qualiteRelation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  public double getMaxDist() {
    return maxDist;
  }

  public void setMaxDist(double maxDist) {
    this.maxDist = maxDist;
  }
}
