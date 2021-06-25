package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.MarginExpressionType;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * The monitors of this class are satisfied when elongated features preserve
 * their elongation.
 * @author gtouya
 * 
 */
public class PreserveElongationMonitor extends MicroConstraintMonitor {

  private static final double seuilAllonge = 3;

  /**
   * Default constructor.
   * @param obj
   * @param contr
   */
  public PreserveElongationMonitor(IGeneObj obj, FormalMicroConstraint contr) {
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
    double epsilon = 0.2;
    if (but >= seuilAllonge) {
      if (Math.abs(val - but) < epsilon + marge)
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      else if (val > but && val < 1.5 * but)
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
      else if (val > but && val < 2.5 * but)
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
      else if (val > but)
        setSatisfaction(ConstraintSatisfaction
            .valueOfFrench("TRES_PEU_SATISFAIT"));
      else if (but - val < 1.5 * (marge + epsilon))
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
      else if (but - val < 2 * (marge + epsilon))
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
      else if (but - val < 3 * (marge + epsilon))
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
      else if (but - val < 4 * (marge + epsilon))
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
      else if (but - val < 5 * (marge + epsilon))
        setSatisfaction(ConstraintSatisfaction
            .valueOfFrench("TRES_PEU_SATISFAIT"));
      else
        setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
    } else {
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
  }

  @Override
  public void computeGoalValue() {
    // c'est la valeur initiale
    setGoalValue(getInitialValue());
  }

  @Override
  public void computeCurrentValue() {
    IPolygon geom = (IPolygon) this.getSubject().getGeom();
    double elong = CommonAlgorithms.elongation(geom);
    this.setCurrentValue(elong);
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
