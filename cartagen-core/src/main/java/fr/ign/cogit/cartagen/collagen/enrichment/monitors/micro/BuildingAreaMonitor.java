package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class BuildingAreaMonitor extends MicroConstraintMonitor {

  /**
   * Default constructor
   * @param obj
   * @param constraint
   */
  public BuildingAreaMonitor(IGeneObj obj, FormalGenConstraint constraint) {
    super(obj, constraint);
  }

  @Override
  public void computeSatisfaction() {
    if (getSujet().isEliminated()) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
      return;
    }

    // on compare le but à la valeur courante
    double epsilon = 5.0;
    double but = (Double) getValeurBut();
    double courante = (Double) getValeurCourante();

    // si la valeur courante vaut le but � epsilon pr�s,
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
    double area = getSujet().getGeom().area();
    this.setValeurCourante(area);
  }

  @Override
  public void calculerValeurBut() {
    // on commence par récupèrer la valeur min de la contrainte
    FormalMicroConstraint contrainte = (FormalMicroConstraint) getElementSpec();
    double min = UnitsTranslation.getValeurContrUniteTerrain(
        Legend.getSYMBOLISATI0N_SCALE(), contrainte);
    if ((Double) getValeurIni() < min)
      setValeurBut(min);
    else if ((Double) getValeurIni() < 2 * min) {
      double epsilon = 5.0;
      double a = (1.0 - 2.0 * epsilon) / min;
      double b = min - 1.0 + 3.0 * epsilon;
      setValeurBut(a * (Double) getValeurIni() + b);
    } else
      setValeurBut(getValeurIni());

  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
