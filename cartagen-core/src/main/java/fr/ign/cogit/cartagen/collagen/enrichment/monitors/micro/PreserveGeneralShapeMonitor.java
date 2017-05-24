package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * This monitor class preserves initial general shape by minimising the surface
 * distance between the generalised and the initial geometry.
 * @author gtouya
 * 
 */
public class PreserveGeneralShapeMonitor extends MicroConstraintMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param contr
   */
  public PreserveGeneralShapeMonitor(IGeneObj obj, FormalMicroConstraint contr) {
    super(obj, contr);
  }

  @Override
  public void computeSatisfaction() {
    // on teste si l'objet a été éliminé
    if (getSujet().isEliminated()) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    // on récupère la valeur initiale
    IPolygon geomIni = (IPolygon) getValeurIni();
    // on récupère la valeur courante
    IGeometry geomCourante = getSujet().getGeom();
    if (geomIni.equals(geomCourante)) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    // on réalise une translation pour recaler le centroïde
    IDirectPosition ptIni = geomIni.centroid();
    IDirectPosition pt = geomCourante.centroid();
    IGeometry geom = CommonAlgorithms.translation(geomCourante, ptIni.getX()
        - pt.getX(), ptIni.getY() - pt.getY());
    // on calcule la distance surfacique
    double dist = Distances.distanceSurfacique(geomIni, (IPolygon) geom);
    if (dist <= 0.05)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if (dist < 0.1)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (dist < 0.2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if (dist < 0.3)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (dist < 0.4)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if (dist < 0.5)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (dist < 0.6)
      setSatisfaction(ConstraintSatisfaction
          .valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));

  }

  @Override
  public void calculerValeurBut() {
  }

  @Override
  public void calculerValeurCourante() {
    // il s'agit ici de la géométrie courante
    setValeurCourante(sujet.getGeom());
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
