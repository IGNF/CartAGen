package fr.ign.cogit.cartagen.evaluation.harmonisation;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class BuildingInterBuiltUpMonitor extends HarmonisationMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param constraint
   */
  public BuildingInterBuiltUpMonitor(IGeneObj obj) {
    super(obj);
    this.setName("BuildingInterGranularity");
  }

  @Override
  public void computeSatisfaction() {
    computeCurrentValue();

    // on compare le but à la valeur courante
    int current = (Integer) getCurrentValue();
    // si la valeur courante vaut le but à epsilon près,
    if (current == 0)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if (current <= 2)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (current <= 5)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if (current <= 8)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if (current <= 10)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if (current <= 20)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if (current <= 30)
      setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
    this.getEtatsSatisf().add(this.getSatisfaction());
  }

  @Override
  public void computeCurrentValue() {
    IGeometry geom = this.getFeat().getGeom().buffer(7.5);
    Collection<IBuilding> buildings = new HashSet<>();
    for (IBuilding building : CartAGenDoc.getInstance().getCurrentDataset()
        .getBuildings().select(geom)) {
      if (geom.contains(building.getGeom()))
        continue;
      buildings.add(building);
    }
    System.out.println(getFeat().getId() + ": " + buildings.size());
    this.setCurrentValue(buildings.size());
  }

  public void computeGoalValue() {
    setGoalValue(0);
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
