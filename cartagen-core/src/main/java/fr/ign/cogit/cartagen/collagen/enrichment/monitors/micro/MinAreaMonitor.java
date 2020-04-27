package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class MinAreaMonitor extends MicroConstraintMonitor {

  /**
   * Default constructor
   * @param obj
   * @param contr
   */
  public MinAreaMonitor(IGeneObj obj, FormalGenConstraint contr) {
    super(obj, contr);
  }

  @Override
  public void computeSatisfaction() {
    // on compare le but à la valeur courante
    double epsilon = 5.0;
    double but = (Double) getGoalValue();
    double courante = (Double) getCurrentValue();
    if (getSubject().isEliminated()) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    // si la valeur courante vaut le but à epsilon près,
    if (Math.abs(but - courante) < epsilon)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if (courante > but)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (courante > 5 * but / 6)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if (courante > 3 * but / 4)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (courante > but / 2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if (courante > but / 3)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (courante > but / 4)
      setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
  }

  @Override
  public void calculerValeurCourante() {
    this.setCurrentValue(this.getSubject().getGeom().area());
  }

  @Override
  public void calculerValeurBut() {
    // on commence par r�cup�rer la valeur min de la contrainte
    FormalMicroConstraint contrainte = (FormalMicroConstraint) getElementSpec();
    double min = UnitsTranslation.getValeurContrUniteTerrain(
        Legend.getSYMBOLISATI0N_SCALE(), contrainte);
    setGoalValue(min);
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
