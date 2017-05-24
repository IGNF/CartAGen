package fr.ign.cogit.cartagen.evaluation.harmonisation;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class PreserveGeneralShapeMonitor extends HarmonisationMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param constraint
   */
  public PreserveGeneralShapeMonitor(IGeneObj obj) {
    super(obj);
    this.setName("PreserveGeneralShape");
  }

  @Override
  public void computeSatisfaction() {

    // on récupère la valeur initiale
    IPolygon geomIni = (IPolygon) getInitialValue();
    // on récupère la valeur courante
    IGeometry geomCourante = getFeat().getGeom();
    if (geomIni.equals(geomCourante)) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      this.getEtatsSatisf().add(this.getSatisfaction());
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
    this.getEtatsSatisf().add(this.getSatisfaction());
  }

  @Override
  public void computeCurrentValue() {
    // il s'agit ici de la géométrie courante
    setCurrentValue(getFeat().getGeom());
  }

  public void computeGoalValue() {
    // do nothing, no goal value
  }

  public IPoint getPointGeom() {
    return getFeat().getGeom().centroid().toGM_Point();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getSatisfactionString() {
    return super.getSatisfactionString();
  }

  @Override
  public int getImportance() {
    return 1;
  }

}
