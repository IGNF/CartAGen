package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class BuildingRoadProximityRelation extends CollaGenRelation {

  private Double distMin, distMinSymb;
  private double largeurSymb;

  /**
   * Constructeur à partir des composants de la relation. Construit la relation
   * Gothic en plus de la relation Java.
   * @param obj1
   * @param obj2
   * @param concept
   */
  public BuildingRoadProximityRelation(IGeneObj bati, IGeneObj route,
      GeographicRelation concept) {
    super(bati, route, concept);
    computeGeom();
    this.largeurSymb = ((IRoadLine) route).getWidth() / 2.0;
    calculerDistMin();
    calculerDistMinSymb();
  }

  public double getDistMin() {
    return distMin;
  }

  public void setDistMin(Double distMin) {
    this.distMin = distMin;
  }

  public Double getDistMinSymb() {
    return distMinSymb;
  }

  public void setDistMinSymb(Double distMin) {
    this.distMinSymb = distMin;
  }

  public double getLargeurSymb() {
    return largeurSymb;
  }

  public void setLargeurSymb(double largeurSymb) {
    this.largeurSymb = largeurSymb;
  }

  private void calculerDistMin() {
    GeometryProximity proxi = new GeometryProximity(obj1.getGeom(), obj2
        .getGeom());
    distMin = proxi.getDistance();
  }

  private void calculerDistMinSymb() {
    IGeometry buffer = this.getObj2().getGeom().buffer(largeurSymb);
    GeometryProximity proxi = new GeometryProximity(obj1.getGeom(), buffer);
    distMinSymb = proxi.getDistance();
  }

  @Override
  public int qualiteRelation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public void computeGeom() {
    GeometryProximity proxi = new GeometryProximity(obj1.getGeom(), obj2
        .getGeom());
    geom = proxi.toSegment();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }
}
