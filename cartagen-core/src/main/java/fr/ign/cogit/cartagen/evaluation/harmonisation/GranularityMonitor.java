package fr.ign.cogit.cartagen.evaluation.harmonisation;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class GranularityMonitor extends HarmonisationMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param constraint
   */
  public GranularityMonitor(IGeneObj obj) {
    super(obj);
    this.setName("Granularity");
  }

  @Override
  public void computeSatisfaction() {
    computeCurrentValue();
    // on compare le but à la valeur courante
    double seuilDensite = 0.05;// 5 vertices pour 100 m, à vérifier
    ValeurGranularite but = (ValeurGranularite) getGoalValue();
    double longMinSeg = ((ValeurGranularite) getCurrentValue()).longMinSeg;
    double densite = ((ValeurGranularite) getCurrentValue()).densiteVertices;
    // si la valeur courante vaut le but � epsilon pr�s,
    if (longMinSeg >= but.longMinSeg)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
    else if ((longMinSeg > 3 * but.longMinSeg / 4) && (densite < seuilDensite))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
    else if (densite < seuilDensite)
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
    else if ((longMinSeg > 3 * but.longMinSeg / 4)
        && (densite < 2 * seuilDensite))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
    else if ((longMinSeg > but.longMinSeg / 2) && (densite < 2 * seuilDensite))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
    else if ((longMinSeg > but.longMinSeg / 2) && (densite > 2 * seuilDensite))
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
    else if ((longMinSeg > but.longMinSeg / 4) && (densite > 2 * seuilDensite))
      setSatisfaction(ConstraintSatisfaction
          .valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
    this.getEtatsSatisf().add(this.getSatisfaction());
  }

  @Override
  public void computeCurrentValue() {
    IGeometry geom = this.getFeat().getGeom();
    double longMinSeg = CommonAlgorithmsFromCartAGen
        .getShortestEdgeLength(geom);
    double densiteVert = 0.0;
    if (geom instanceof ILineString) {
      double nbVert = geom.numPoints();
      double longueur = geom.length();
      densiteVert = nbVert / longueur;
    } else {
      double nbVert = ((IPolygon) geom).getExterior().numPoints();
      double perim = geom.length();
      densiteVert = nbVert / perim;
    }
    this.setCurrentValue(new ValeurGranularite(longMinSeg, densiteVert));
  }

  public void computeGoalValue() {
    ValeurGranularite initial = (ValeurGranularite) this.getInitialValue();
    setGoalValue(new ValeurGranularite(initial.longMinSeg,
        initial.densiteVertices));
  }

  public static class ValeurGranularite {
    public double longMinSeg;
    public double densiteVertices;

    public ValeurGranularite(double longMinSeg, double densiteVertices) {
      super();
      this.longMinSeg = longMinSeg;
      this.densiteVertices = densiteVertices;
    }
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
