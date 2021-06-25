package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.MarginExpressionType;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class PreserveConcavityMonitor extends MicroConstraintMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param contr
   */
  public PreserveConcavityMonitor(IGeneObj obj, FormalMicroConstraint contr) {
    super(obj, contr);
  }

  @Override
  public void computeSatisfaction() {
    FormalMicroConstraint contrainte = (FormalMicroConstraint) getElementSpec();
    // récupère une marge éventuelle
    double pourcent = ((MarginExpressionType) contrainte.getExprType())
        .getMargin();
    // récupère la valeur but
    double but = (Double) getGoalValue();
    // calcule la marge
    double marge = but * pourcent;
    // récupère la valeur courante
    double val = (Double) getCurrentValue();
    double epsilon = 0.05;
    if (Math.abs(val - but) < epsilon + marge)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if (val < but)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (val - but < 1.5 * (marge + epsilon))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if (val - but < 2 * (marge + epsilon))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (val - but < 3 * (marge + epsilon))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if (val - but < 4 * (marge + epsilon))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (val - but < 5 * (marge + epsilon))
      setSatisfaction(ConstraintSatisfaction
          .valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
  }

  @Override
  public void computeGoalValue() {
    // c'est la valeur initiale
    setGoalValue(getInitialValue());
  }

  @Override
  public void computeCurrentValue() {
    IPolygon geom = (IPolygon) this.getSubject().getGeom();
    double area = geom.area();
    IGeometry hull = geom.convexHull();
    double areaHull = hull.area();

    this.setCurrentValue(new Double(area / areaHull));
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
