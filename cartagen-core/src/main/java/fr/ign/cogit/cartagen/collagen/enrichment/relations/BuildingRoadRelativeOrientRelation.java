package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class BuildingRoadRelativeOrientRelation extends CollaGenRelation {

  public static final double SEUIL_DIST = 50.0; // en mètres
  public static final double SEUIL_ECART_ANGLE = Math.PI / 5;

  private double ecartAngulaire = Double.POSITIVE_INFINITY;
  private IDirectPosition ptRoute;

  /**
   * Constructeur à partir des composants de la relation. Construit la relation
   * Gothic en plus de la relation Java.
   * @param obj1
   * @param obj2
   * @param vac
   */
  public BuildingRoadRelativeOrientRelation(IGeneObj bati, IGeneObj route,
      GeographicRelation concept) {
    super(bati, route, concept);
    computeGeom();
    if (geom instanceof ILineSegment)
      ptRoute = geom.coord().get(1);
    else
      ptRoute = geom.coord().get(0);
    calculerEcartAngulaire();
  }

  private void calculerEcartAngulaire() {
    Geometry geomBati = null;
    try {
      geomBati = JtsGeOxygene.makeJtsGeom(obj1.getGeom());
    } catch (Exception e) {
      e.printStackTrace();
    }
    double orientBati = MesureOrientationV2.getOrientationGenerale(geomBati);
    double orientRoute = CommonAlgorithmsFromCartAGen.lineAbsoluteOrientation(
        (GM_LineString) obj2.getGeom(), ptRoute).getValeur();
    // on ramène l'orientation de la route entre 0 et Pi
    if (orientRoute < 0.0)
      orientRoute += Math.PI;
    ecartAngulaire = Math.abs(orientBati - orientRoute);
  }

  public double getEcartAngulaire() {
    if (ecartAngulaire == Double.POSITIVE_INFINITY)
      calculerEcartAngulaire();
    return ecartAngulaire;
  }

  public static boolean estValide(IBuilding bati, IRoadLine route) {
    GeometryProximity proxi = new GeometryProximity(bati.getGeom(),
        route.getGeom());
    if (proxi.getDistance() > SEUIL_DIST)
      return false;
    return true;
  }

  @Override
  public int qualiteRelation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public void computeGeom() {
    GeometryProximity proxi = new GeometryProximity(obj1.getGeom(),
        obj2.getGeom());
    geom = proxi.toSegment();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }
}
