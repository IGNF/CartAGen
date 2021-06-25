package fr.ign.cogit.cartagen.collagen.enrichment.monitors.micro;

import fr.ign.cogit.cartagen.collagen.components.translator.UnitsTranslation;
import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.spatialanalysis.urban.Squareness;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class SquarenessMonitor extends MicroConstraintMonitor {

    private final static double toleranceAngle = 4.0;

    /**
     * @param obj
     * @param contr
     */
    public SquarenessMonitor(IGeneObj obj, FormalGenConstraint contr) {
        super(obj, contr);
    }

    @Override
    public void computeSatisfaction() {
        // on compare le but à la valeur courante
        ValeurEquarrite courante = (ValeurEquarrite) this.getCurrentValue();
        if ((courante.measureAble == 0.0) || (courante.currentHoles == 1.0)) {
            this.setSatisfaction(ConstraintSatisfaction.PERFECT);
        } else if ((courante.currentSquared == 1.0)
                || (courante.currentNearlySquared <= 0.1)) {
            this.setSatisfaction(ConstraintSatisfaction.PERFECT);
        } else if (courante.currentNearlySquared <= 0.1) {
            this.setSatisfaction(ConstraintSatisfaction.VERY_SATISFIED);
        } else if ((0.1 < courante.currentNearlySquared)
                && (courante.currentNearlySquared <= 0.2)) {
            this.setSatisfaction(ConstraintSatisfaction.CORRECT);
        } else if ((0.2 < courante.currentNearlySquared)
                && (courante.currentNearlySquared <= 0.3)) {
            this.setSatisfaction(ConstraintSatisfaction.ACCEPTABLE);
        } else if ((0.3 < courante.currentNearlySquared)
                && (courante.currentNearlySquared <= 0.4)) {
            this.setSatisfaction(ConstraintSatisfaction.FAIR);
        } else if ((0.4 < courante.currentNearlySquared)
                && (courante.currentNearlySquared <= 0.5)) {
            this.setSatisfaction(ConstraintSatisfaction.BARELY_SATISFIED);
        } else if ((0.5 < courante.currentNearlySquared)
                && (courante.currentNearlySquared <= 0.75)) {
            this.setSatisfaction(ConstraintSatisfaction.NOT_SATISFIED);
        } else if (courante.currentNearlySquared > 0.75) {
            this.setSatisfaction(ConstraintSatisfaction.UNACCEPTABLE);
        }
    }

    @Override
    public void computeCurrentValue() {
        double measureAble = 0.0;
        double currentSquared = 0.0;
        double currentNearlySquared = 0.0;
        double currentHoles = 0.0;
        if (!this.getSubject().isEliminated()) {
            IGeometry geom = this.getSubject().getGeom();
            FormalMicroConstraint contrainte = (FormalMicroConstraint) this
                    .getElementSpec();
            double flex = UnitsTranslation.getValeurContrUniteTerrain(
                    Legend.getSYMBOLISATI0N_SCALE(), contrainte);
            Squareness currentSquareness = null;
            try {
                currentSquareness = new Squareness(geom, flex,
                        SquarenessMonitor.toleranceAngle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentSquareness != null) {
                if (currentSquareness.isDone()) {
                    measureAble = 1.0;
                } else {
                    measureAble = 0.0;
                }
                currentSquared = currentSquareness.getSquaredCorners();
                currentNearlySquared = currentSquareness
                        .getNearlySquaredCorners();
                if (currentSquareness.isHasHoles()) {
                    currentHoles = 1.0;
                } else {
                    currentHoles = 0.0;
                }
            } else {
                measureAble = 0.0;
                currentHoles = 0.0;
            }
        }
        this.setCurrentValue(new ValeurEquarrite(measureAble, currentSquared,
                currentNearlySquared, currentHoles));
    }

    @Override
    public void computeGoalValue() {
    }

    public static class ValeurEquarrite {
        public double measureAble;
        public double currentSquared;
        public double currentNearlySquared;
        public double currentHoles;

        public ValeurEquarrite(double measureAble, double currentSquared,
                double currentNearlySquared, double currentHoles) {
            super();
            this.measureAble = measureAble;
            this.currentSquared = currentSquared;
            this.currentNearlySquared = currentNearlySquared;
            this.currentHoles = currentHoles;
        }
    }

    @Override
    public IFeature cloneGeom() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public IPoint getPointGeom() {
        return super.getPointGeom();
    }

}
