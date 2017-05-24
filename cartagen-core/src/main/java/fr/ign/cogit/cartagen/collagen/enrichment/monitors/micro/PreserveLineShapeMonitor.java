package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * This monitor class preserves the initial shape of lines minimising both
 * hausdorff and mean distance between generalised and initial line.
 * @author gtouya
 * 
 */
public class PreserveLineShapeMonitor extends MicroConstraintMonitor {

  private static final double seuilHausdorff = 0.1;
  private static final double seuilDistMoyenne = 0.1;

  /**
   * Default constructor
   * @param obj
   * @param contr
   */
  public PreserveLineShapeMonitor(IGeneObj obj, FormalGenConstraint contr) {
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
    ILineString geomIni = (ILineString) getValeurIni();
    // on récupère la valeur courante
    ILineString geomCourante = (ILineString) getSujet().getGeom();
    if (geomIni.equals(geomCourante)) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    // on réalise une translation pour recaler le centroïde
    IDirectPosition ptIni = geomIni.centroid();
    IDirectPosition pt = geomCourante.centroid();
    IGeometry geom = CommonAlgorithms.translation(geomCourante,
        ptIni.getX() - pt.getX(), ptIni.getY() - pt.getY());
    // on calcule la distance de hausdorff et la distance moyenne
    double haus = Distances.hausdorff(geomIni, (ILineString) geom);
    double distMoy = Distances.distanceMoyenne(geomIni, (ILineString) geom);
    double seuil1 = seuilHausdorff * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    double seuil2 = seuilDistMoyenne * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    if (haus < seuil1 && distMoy < seuil2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if ((haus < seuil1 && distMoy < 2 * seuil2)
        || (haus < 2 * seuil1 && distMoy < seuil2))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (haus < 2 * seuil1 && distMoy < 2 * seuil2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if ((haus < 3 * seuil1 && distMoy < 2 * seuil2)
        || (haus < 2 * seuil1 && distMoy < 3 * seuil2))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (haus < 3 * seuil1 && distMoy < 3 * seuil2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if ((haus < 3 * seuil1 && distMoy < 4 * seuil2)
        || (haus < 4 * seuil1 && distMoy < 3 * seuil2))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (haus < 4 * seuil1 && distMoy < 4 * seuil2)
      setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
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
