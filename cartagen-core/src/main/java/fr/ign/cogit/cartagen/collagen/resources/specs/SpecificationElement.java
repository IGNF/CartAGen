package fr.ign.cogit.cartagen.collagen.resources.specs;

/**
 * Cette interface est implémentée par les éléments qui formalisent les spécifications
 * d'une carte ou d'une base de données cartographique soit les contraintes et les règles
 * opérationnelles.
 * This interface is implemented by the elements that formalise the map (or DCM) specifications:
 * the formal constraints and the operation rules.
 * @author GTouya
 *
 */
public interface SpecificationElement {
	public abstract String getName();
	public abstract int getImportance();
}
