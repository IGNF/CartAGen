package fr.ign.cogit.cartagen.collagen.enrichment.monitors;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public abstract class MicroConstraintMonitor extends ConstraintMonitor {

	FormalMicroConstraint constraint;

	/**
	 * Constructeur à partir des composants du moniteur.
	 * 
	 * @param obj
	 * @param contr
	 * @param vac
	 * @param bdc
	 * @param concepts
	 */
	public MicroConstraintMonitor(IGeneObj obj, FormalGenConstraint constraint) {
		super(obj, constraint);
		this.constraint = (FormalMicroConstraint) constraint;
		// on calcule les valeurs de la contrainte
		this.computeCurrentValue();
		this.setInitialValue(this.getCurrentValue());
		this.computeGoalValue();
		this.computeSatisfaction();
		this.getEtatsSatisf().add(this.getSatisfaction());
	}

	@Override
	public FormalMicroConstraint getConstraint() {
		return this.constraint;
	}

	public void setContrainte(FormalMicroConstraint contrainte) {
		this.constraint = contrainte;
	}

	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public String toString() {
		return this.constraint.toString() + " monitored on " + this.getSubject().toString();
	}

	@Override
	public SpecificationElement getElementSpec() {
		return this.constraint;
	}

	@Override
	public IGeometry getExtent(double facteur) {
		// il s'agit ici d'un buffer autour de la géométrie du sujet du moniteur,
		// buffer de la taille du facteur
		// on commence donc par récupérer la géométrie du sujet
		IGeometry geom = this.getSubject().getGeom();
		IGeometry emprise = geom.buffer(facteur, 10);
		return emprise;
	}

	@Override
	public IDirectPosition toPoint() {
		return this.getSubject().getGeom().centroid();
	}

	@Override
	public int getImportance() {
		return 1;
	}

	@Override
	public IFeature cloneGeom() throws CloneNotSupportedException {
		return null;
	}

	@Override
	public IGeneObj getSubject() {
		return (IGeneObj) super.getSubject();
	}

}
