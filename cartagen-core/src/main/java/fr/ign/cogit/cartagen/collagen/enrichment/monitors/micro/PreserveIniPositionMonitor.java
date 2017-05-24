package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * This monitor class makes monitors to preserve the initial position of
 * {@link IGeneObj} features. If the feature is a point, its position is
 * preserved. Either, the centroid of the feature is preserved.
 * @author gtouya
 * 
 */
public class PreserveIniPositionMonitor extends MicroConstraintMonitor {

  public PreserveIniPositionMonitor(IGeneObj obj,
      FormalGenConstraint constraint) {
    super(obj, constraint);
    this.calculerValeurCourante();
    this.setValeurIni(getValeurCourante());
    this.calculerValeurBut();
    this.computeSatisfaction();
    this.getEtatsSatisf().set(0, getSatisfaction());
  }

  @Override
  public void computeSatisfaction() {
    // on teste d'abord si le sujet a été éliminé
    if (this.getSujet().isEliminated()) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    double epsilon = 1.0;
    double but = (Double) getValeurBut();
    IDirectPosition courante = (IDirectPosition) getValeurCourante();
    IDirectPosition ini = (IDirectPosition) getValeurIni();
    double dist = courante.distance2D(ini);
    // si la valeur courante est l'initiale à epsilon près,
    if (dist < epsilon)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if (dist < but / 4)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (dist < but / 2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if (dist <= but)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (dist < 1.5 * but)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if (dist < 2 * but)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (dist < 2.5 * but)
      setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
  }

  @Override
  public void calculerValeurCourante() {
    this.setValeurCourante(this.getSujet().getGeom().centroid());
  }

  @Override
  public void calculerValeurBut() {
    // on commence par récupérer la valeur de la contrainte
    FormalMicroConstraint contrainte = (FormalMicroConstraint) getElementSpec();
    double dist = UnitsTranslation.getValeurContrUniteTerrain(
        Legend.getSYMBOLISATI0N_SCALE(), contrainte);
    setValeurBut(dist);
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
