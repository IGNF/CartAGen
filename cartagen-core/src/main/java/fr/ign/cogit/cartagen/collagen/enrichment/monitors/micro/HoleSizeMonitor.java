package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;

public class HoleSizeMonitor extends MicroConstraintMonitor {

  private double aireTrouMin = 0.0;

  /**
   * Default constructor.
   * @param obj
   * @param contr
   */
  public HoleSizeMonitor(IGeneObj obj, FormalGenConstraint contr) {
    super(obj, contr);
    this.aireTrouMin = UnitsTranslation
        .getValeurContrUniteTerrain(Legend.getSYMBOLISATI0N_SCALE(), contr);
    // on calcule les valeurs de la contrainte de nouveau après initialisation
    // du paramètre
    this.computeCurrentValue();
    this.setInitialValue(this.getCurrentValue());
    this.computeGoalValue();
    this.computeSatisfaction();
    this.getEtatsSatisf().add(this.getSatisfaction());
  }

  @Override
  public void computeSatisfaction() {
    if (this.aireTrouMin == 0.0) {
      this.setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
      return;
    }
    if (this.getSubject().isEliminated()) {
      this.setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }

    // on compare le but à la valeur courante
    double but = ((ValeurTailleTrou) this.getGoalValue()).aireTrouMin1;
    double aireTrouMin = ((ValeurTailleTrou) this
        .getCurrentValue()).aireTrouMin1;
    int nbPetitsTrous = ((ValeurTailleTrou) this
        .getCurrentValue()).nbPetitsTrous;
    // si la valeur courante vaut le but à epsilon près,
    if (nbPetitsTrous == 0) {
      this.setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    } else if ((nbPetitsTrous == 1) && (aireTrouMin > (4.0 * but / 5.0))) {
      this.setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    } else if ((nbPetitsTrous == 2) && (aireTrouMin > 4.0 * but / 5.0)) {
      this.setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    } else if ((nbPetitsTrous > 2) && (aireTrouMin > 4.0 * but / 5.0)) {
      this.setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    } else if ((nbPetitsTrous == 1) && (aireTrouMin > but / 2.0)) {
      this.setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    } else if ((nbPetitsTrous == 2) && (aireTrouMin > but / 2.0)) {
      this.setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    } else if ((nbPetitsTrous > 2) && (aireTrouMin > but / 2.0)) {
      this.setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
    } else {
      this.setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
    }
  }

  @Override
  public void computeGoalValue() {
    // on commence par récupérer la valeur min de la contrainte
    this.setGoalValue(new ValeurTailleTrou(this.aireTrouMin, 0));
  }

  @Override
  public void computeCurrentValue() {
    if (this.aireTrouMin == 0.0) {
      this.setCurrentValue(new ValeurTailleTrou(0.0, 0));
      return;
    }
    IPolygon geom = (IPolygon) this.getSubject().getGeom();
    double min = ((ValeurTailleTrou) this.getGoalValue()).aireTrouMin1;
    double aireTrouMin = min;
    int nbPetitsTrous = 0;
    if (geom.getInterior().size() == 0) {
      aireTrouMin = -1.0;
    } else {
      for (int i = 0; i < geom.getInterior().size(); i++) {
        IRing ring = geom.getInterior(i);
        if (ring.area() < min) {
          nbPetitsTrous++;
          if (ring.area() < aireTrouMin) {
            aireTrouMin = ring.area();
          }
        }
      }
    }
    this.setCurrentValue(new ValeurTailleTrou(aireTrouMin, nbPetitsTrous));
  }

  public static class ValeurTailleTrou {
    public double aireTrouMin1;
    public int nbPetitsTrous;

    public ValeurTailleTrou(double aireTrouMin, int nbPetitsTrous) {
      super();
      this.aireTrouMin1 = aireTrouMin;
      this.nbPetitsTrous = nbPetitsTrous;
    }
  }

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
