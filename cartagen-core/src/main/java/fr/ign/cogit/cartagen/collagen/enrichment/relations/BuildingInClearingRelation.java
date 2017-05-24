package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import java.util.Set;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class BuildingInClearingRelation extends CollaGenRelation {

  public static final double AREA_THRESHOLD = 50000.0;
  public static final double RATIO_THRESHOLD = 0.3;

  private double ratioDansClairiere;

  /**
   * Constructeur à partir des composants de la relation. Construit la relation
   * Gothic en plus de la relation Java.
   * @param obj1
   * @param obj2
   * @param vac
   */
  public BuildingInClearingRelation(IGeneObj bati, IGeneObj foret,
      GeographicRelation concept) {
    super(bati, foret, concept);
    IDirectPosition pt1 = obj1.getGeom().centroid();
    IDirectPosition pt2 = obj2.getGeom().centroid();
    geom = new GM_LineSegment(pt1, pt2);
    calculerRatioDansClairiere();
  }

  public void calculerRatioDansClairiere() {
    this.ratioDansClairiere = 0.0;
    IPolygon geom1 = (IPolygon) obj1.getGeom();
    IPolygon geom2 = (IPolygon) obj2.getGeom();

    // on teste si le batiment intersecte légèrement un trou
    Set<IRing> rings = CommonAlgorithmsFromCartAGen.getIntersectingInnerRings(
        geom2, geom1);
    for (IRing innerRing : rings) {
      IPolygon trou = new GM_Polygon(innerRing);
      IGeometry inter = trou.intersection(geom1);
      // cas d'un bug de JTS
      if (inter == null)
        continue;
      this.ratioDansClairiere += inter.area() / geom1.area();
    }
  }

  public static boolean estValide(IBuilding bati, ISimpleLandUseArea foret) {
    IPolygon geom1 = (IPolygon) bati.getGeom();
    IPolygon geom2 = (IPolygon) foret.getGeom();
    // on teste si bati est contenu dans un trou de foret
    IRing ring = CommonAlgorithmsFromCartAGen.getContainingInnerRing(geom2,
        geom1);
    if (ring != null) {
      IPolygon trou = new GM_Polygon(ring);
      if (trou.area() < AREA_THRESHOLD)
        return true;
      else
        return false;
    }
    // on teste si le batiment intersecte l�g�rement un trou
    Set<IRing> rings = CommonAlgorithmsFromCartAGen.getIntersectingInnerRings(
        geom2, geom1);
    for (IRing innerRing : rings) {
      IPolygon trou = new GM_Polygon(innerRing);
      IGeometry inter = geom1.difference(trou);
      // cas d'un bug de JTS
      if (inter == null)
        continue;
      if (inter.area() / geom1.area() < RATIO_THRESHOLD)
        return true;
    }
    return false;
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

  public double getRatioDansClairiere() {
    return ratioDansClairiere;
  }

  public void setRatioDansClairiere(double ratioDansClairiere) {
    this.ratioDansClairiere = ratioDansClairiere;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }
}
