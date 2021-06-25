package fr.ign.cogit.cartagen.tactilemaps.monitors;

import fr.ign.cogit.cartagen.collagen.enrichment.monitors.MicroConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * A monitor for a constraint of minimal length of line objects in a tactile
 * map.
 * 
 * @author GTouya
 *
 */
public class MinimumLineLengthMonitor extends MicroConstraintMonitor {

	private double threshold = 12.5;

	/**
	 * Default constructor
	 * 
	 * @param obj
	 * @param constraint
	 */
	public MinimumLineLengthMonitor(IGeneObj obj, FormalGenConstraint constraint) {
		super(obj, constraint);
	}

	/**
	 * Constructor with the minimum length threshold
	 * 
	 * @param obj
	 * @param threshold minimum length in map mm
	 */
	public MinimumLineLengthMonitor(IGeneObj obj, double threshold) {
		super(obj, null);
		this.threshold = threshold;
	}

	@Override
	public void computeSatisfaction() {
		if (getSubject().isEliminated()) {
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
			return;
		}

		// compare the current value to the goal value
		double epsilon = 5.0;
		double but = (Double) getGoalValue();
		double courante = (Double) getCurrentValue();

		// si la valeur courante vaut le but � epsilon pr�s,
		if (Math.abs(but - courante) < epsilon)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("PARFAIT"));
		else if (courante > 6 * but / 7)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_SATISFAIT"));
		else if (courante > 5 * but / 6)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("CORRECT"));
		else if (courante > 4 * but / 5)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("MOYEN"));
		else if (courante > 3 * but / 4)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("PASSABLE"));
		else if (courante > but / 2)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("PEU_SATISFAIT"));
		else if (courante > but / 3)
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("TRES_PEU_SATISFAIT"));
		else
			setSatisfaction(ConstraintSatisfaction.valueOfFrench("NON_SATISFAIT"));
	}

	@Override
	public void computeCurrentValue() {
		double length = getSubject().getGeom().length();
		this.setCurrentValue(length);
	}

	@Override
	public void computeGoalValue() {
		// the goal value is the minimum threshold or the initial value if it is above
		// the threshold.
		if ((Double) getInitialValue() < (threshold * Legend.getSYMBOLISATI0N_SCALE() / 1000.0)) {
			setGoalValue(threshold * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
		} else
			setGoalValue(getInitialValue());

	}

	@Override
	public IPoint getPointGeom() {
		return super.getPointGeom();
	}

}
