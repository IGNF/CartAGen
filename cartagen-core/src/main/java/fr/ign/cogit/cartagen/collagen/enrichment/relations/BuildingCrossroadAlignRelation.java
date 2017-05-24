package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.TCrossRoad;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class BuildingCrossroadAlignRelation extends CollaGenRelation {

  public static final double SEARCH_DIST = 75.0;

  private double orientation;
  private TCrossRoad crossRoad;

  /**
   * Constructeur à partir des composants de la relation. Calcule la propriété
   * d'orientation de la relation.
   * @param obj1
   * @param obj2
   * @param vac
   */
  public BuildingCrossroadAlignRelation(IGeneObj bati, IGeneObj noeudRoutier,
      GeographicRelation concept) {
    super(bati, noeudRoutier, concept);
    computeGeom();
    this.crossRoad = new TCrossRoad((NoeudRoutier) noeudRoutier.getGeoxObj(),
        TCrossRoad.FLAT_ANGLE, TCrossRoad.BIS_ANGLE);
    calculerOrientationAlignement();
  }

  private void calculerOrientationAlignement() {
    this.orientation = crossRoad.getOrientation();
    // l'orientation est l'opposée de celle du carrefour en T
    if (this.orientation < Math.PI)
      this.orientation += Math.PI;
    else
      this.orientation -= Math.PI;
  }

  public static boolean estValide(IBuilding bati, IRoadNode carref) {
    if (!TCrossRoad.isTNode((NoeudRoutier) carref.getGeoxObj(),
        TCrossRoad.FLAT_ANGLE, TCrossRoad.BIS_ANGLE))
      return false;
    // on récupère l'orientation du carrefour en T
    TCrossRoad crossRoad = new TCrossRoad((NoeudRoutier) carref.getGeoxObj(),
        TCrossRoad.FLAT_ANGLE, TCrossRoad.BIS_ANGLE);
    double orientation = crossRoad.getOrientation();
    // l'orientation est l'opposée de celle du carrefour en T
    if (orientation < Math.PI)
      orientation += Math.PI;
    else
      orientation -= Math.PI;
    // on construit un segment orienté comme le carrefour
    Vector2D vect = new Vector2D(new Angle(orientation), SEARCH_DIST);
    IDirectPosition initial = carref.getPosition();
    IDirectPosition translated = vect.translate(initial);
    ILineSegment segment = new GM_LineSegment(initial, translated);
    // on vérifie que ce segment coupe le bâtiment
    IGeometry geom = bati.getGeom();
    if (!geom.intersects(segment))
      return false;
    return true;
  }

  @Override
  public int qualiteRelation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IGeometry getGeom() {
    return geom;
  }

  private void computeGeom() {
    IDirectPosition pt1 = obj1.getGeom().centroid();
    IDirectPosition pt2 = obj2.getGeom().centroid();
    geom = new GM_LineSegment(pt1, pt2);
  }

  public double getOrientation() {
    return orientation;
  }

  public void setOrientation(double orientation) {
    this.orientation = orientation;
  }

  public IDirectPosition getPointCarrefour() {
    return obj2.getGeom().centroid();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }
}
