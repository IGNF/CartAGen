package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class GeoSpaceConcept extends GeneralisationConcept {
	// //////////////////////////////////////////
	// Fields //
	// //////////////////////////////////////////

	// All static fields //

	// Public fields //

	// Protected fields //

	// Package visible fields //

	// Private fields //
	private Set<GeographicConcept> containsRestriction;

	// //////////////////////////////////////////
	// Static methods //
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// Public methods //
	// //////////////////////////////////////////

	// Public constructors //
	public GeoSpaceConcept(OWLOntology ontology, String ontoName, String name,
			OWLClass classOnto, Set<GeographicConcept> containsRestriction) {
		super(ontology, ontoName, name, classOnto);
		this.containsRestriction = containsRestriction;
	}

	// Getters and setters //
	public Set<GeographicConcept> getContainsRestriction() {
		return containsRestriction;
	}

	public void setContainsRestriction(
			Set<GeographicConcept> containsRestriction) {
		this.containsRestriction = containsRestriction;
	}
	// Other public methods //

	// //////////////////////////////////////////
	// Protected methods //
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// Package visible methods //
	// //////////////////////////////////////////

	// ////////////////////////////////////////
	// Private methods //
	// ////////////////////////////////////////

}
