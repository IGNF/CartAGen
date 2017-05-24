package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.util.HashSet;
import java.util.Stack;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class GeographicConcept extends GeneralisationConcept {
	// //////////////////////////////////////////
	// Fields //
	// //////////////////////////////////////////

	// All static fields //

	// Public fields //

	// Protected fields //

	// Package visible fields //

	// Private fields //

	// //////////////////////////////////////////
	// Static methods //
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// Public methods //
	// //////////////////////////////////////////

	// Public constructors //

	// Getters and setters //

	// Other public methods //
	/**
	 * Récupère récursivement tous les sousConcepts de this et leurs propres
	 * sousConcepts.
	 * 
	 * @return le set des tous les sousConcepts récursivement
	 */
	public HashSet<GeographicConcept> getAllSubConcepts() {
		HashSet<GeographicConcept> set = new HashSet<GeographicConcept>();
		Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
		concepts.addAll(subConcepts);
		while (!concepts.isEmpty()) {
			GeographicConcept c = (GeographicConcept) concepts.pop();
			set.add(c);
			concepts.addAll(c.getSubConcepts());
		}
		return set;
	}

	public GeographicConcept(OWLOntology ontology, String onto, String name,
			OWLClass ontologyClass) {
		super(ontology, onto, name, ontologyClass);
	}

	/**
	 * Récupère récursivement tous les surConcepts de this et leurs propres
	 * surConcepts.
	 * 
	 * @return le set des tous les surConcepts récursivement
	 */
	public HashSet<GeographicConcept> getAllSuperConcepts() {
		HashSet<GeographicConcept> set = new HashSet<GeographicConcept>();
		Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
		concepts.addAll(superConcepts);
		while (!concepts.isEmpty()) {
			GeographicConcept c = (GeographicConcept) concepts.pop();
			set.add(c);
			concepts.addAll(c.getSuperConcepts());
		}
		return set;
	}

	public boolean isSubof(GeographicConcept parent) {
		Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
		concepts.addAll(superConcepts);
		while (!concepts.isEmpty()) {
			GeographicConcept c = (GeographicConcept) concepts.pop();
			if (c.equals(parent))
				return true;
			concepts.addAll(c.getSuperConcepts());
		}
		return false;
	}
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
