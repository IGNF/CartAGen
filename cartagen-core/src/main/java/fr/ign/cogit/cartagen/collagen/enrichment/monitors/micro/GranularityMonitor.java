package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class GranularityMonitor extends MicroConstraintMonitor {

  /**
   * Default constructor.
   * @param obj
   * @param constraint
   */
  public GranularityMonitor(IGeneObj obj, FormalGenConstraint constraint) {
    super(obj, constraint);
  }

  @Override
  public void computeSatisfaction() {
    if (getSujet().isEliminated()) {
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
      return;
    }
    // on compare le but à la valeur courante
    double seuilDensite = 0.05;// 5 vertices pour 100 m, à vérifier
    ValeurGranularite but = (ValeurGranularite) getValeurBut();
    double longMinSeg = ((ValeurGranularite) getValeurCourante()).longMinSeg;
    double densite = ((ValeurGranularite) getValeurCourante()).densiteVertices;
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
      setSatisfaction(
          ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
    else
      setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
  }

  @Override
  public void calculerValeurCourante() {
    IGeometry geom = this.getSujet().getGeom();
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
    this.setValeurCourante(new ValeurGranularite(longMinSeg, densiteVert));
  }

  @Override
  public void calculerValeurBut() {
    // on commence par récupèrer la valeur min de la contrainte
    FormalMicroConstraint contrainte = (FormalMicroConstraint) getElementSpec();
    double min = UnitsTranslation.getValeurContrUniteTerrain(
        Legend.getSYMBOLISATI0N_SCALE(), contrainte);
    setValeurBut(new ValeurGranularite(min, 0.05));
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

  @Override
  public IPoint getPointGeom() {
    return super.getPointGeom();
  }

}
