package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import fr.ign.cogit.cartagen.collagen.enrichment.ValidateRelationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public class GeographicRelation extends GeneralisationConcept {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Set<GeographicConcept> concepts;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  /**
   * Récupère parmi tous les concepts géographique passés en entrée la relation
   * dont le nom et les concepts concernés correspondent à ceux passés en
   * entrée.
   * 
   * @param relationName
   * @param conceptName1
   * @param conceptName2
   * @param concepts
   * @return
   */
  public static GeographicRelation getRelFromName(String relationName,
      String conceptName1, String conceptName2,
      Set<GeneralisationConcept> concepts) {
    // loop on the ontology concepts
    for (GeneralisationConcept e : concepts) {
      if (!e.getName().equals(relationName))
        continue;
      GeographicConcept conc1 = (GeographicConcept) getElemGeoFromName(
          conceptName1, concepts);
      GeographicConcept conc2 = (GeographicConcept) getElemGeoFromName(
          conceptName2, concepts);
      // case where the relation concepts are restricted according to the
      // ontology
      if (((GeographicRelation) e).getConcepts().size() == 2) {
        if (!((GeographicRelation) e).getConcepts().contains(conc1))
          continue;
        if (!((GeographicRelation) e).getConcepts().contains(conc2))
          continue;
        return (GeographicRelation) e;
      }
      if (((GeographicRelation) e).getConcepts().size() == 1) {
        if (conceptName1.equals(conceptName2)
            && !((GeographicRelation) e).getConcepts().contains(conc1))
          continue;
        else if (conceptName1.equals(conceptName2))
          return (GeographicRelation) e;
        // else, verify that one of the 2 concepts corresponds to the
        // specified
        // concept
        GeneralisationConcept elem = ((GeographicRelation) e).getConcepts()
            .iterator().next();
        if (!elem.equals(conc1) && !elem.equals(conc2))
          continue;
        // then builds a new specialised geographic relation
        GeographicRelation relSpec = ((GeographicRelation) e).specialise(conc1,
            conc2);
        concepts.add(relSpec);
        return relSpec;
      } else {
        // last case without specialisation according to the ontology
        // we build the specialisation
        GeographicRelation relSpec = ((GeographicRelation) e).specialise(conc1,
            conc2);
        concepts.add(relSpec);
        return relSpec;
      }
    }
    return null;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public GeographicRelation(OWLOntology ontology, String ontoName, String name,
      OWLClass classOnto) {
    super(ontology, ontoName, name, classOnto);
    concepts = new HashSet<GeographicConcept>();
  }

  public GeographicRelation(String ontology, String name,
      Set<Character> characters, OWLClass ontologyClass,
      Set<Relationship> relations, Map<String, String> labels,
      Set<GeneralisationConcept> superConcepts,
      Set<GeneralisationConcept> subConcepts,
      Set<GeneralisationConcept> thematicNeighbours,
      Set<GeographicConcept> concepts) {
    super(ontology, name, characters, ontologyClass, relations, labels,
        superConcepts, subConcepts, thematicNeighbours);
    this.concepts = concepts;
  }

  // Getters and setters //
  public Set<GeographicConcept> getConcepts() {
    return concepts;
  }

  public void setConcepts(Set<GeographicConcept> concepts) {
    this.concepts = concepts;
  }

  // Other public methods //
  /**
   * Spécialise une relation restreinte à deux concepts précis. Réalise en fait
   * une sous-relation de this qui n'est qu'une copie pour tout ce qui ne
   * concerne pas le champ 'concepts'.
   * 
   * @param concept1
   * @param concept2
   * @return
   */
  public GeographicRelation specialise(GeographicConcept concept1,
      GeographicConcept concept2) {
    GeographicRelation rel = new GeographicRelation(this.ontology, this.name,
        characters, ontologyClass, relations, labels, superConcepts,
        subConcepts, thematicNeighbours, concepts);
    rel.getConcepts().add(concept1);
    rel.getConcepts().add(concept2);
    rel.getCharacters().addAll(this.getCharacters());
    rel.getRelations().addAll(this.getRelations());
    rel.getThematicNeighbours().addAll(this.getThematicNeighbours());
    rel.getSuperConcepts().add(this);
    this.getSubConcepts().add(rel);
    return rel;
  }

  public boolean relationIsAboutConcept(GeographicConcept concept) {
    if (concepts.contains(concept))
      return true;
    for (GeographicConcept c : concepts)
      if (concept.estUn(c))
        return true;
    return false;
  }

  /**
   * Détermine la validité de la relation this pour deux objets géographiques
   * par introspection grace à l'appariement de schéma qui donne le nom de la
   * méthode spécifique à ce type de relation présente dans la classe Factory
   * ValideRelationFactory. Si l'appariement n'est pas fait pour cette relation,
   * la méthode renvoie false.
   * 
   * @param app
   * @param central
   * @param voisin
   * @return true if both objects are in a "this" type relation.
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public boolean isValid(SchemaAnnotation ann, IGeneObj central,
      IGeneObj neighbour)
      throws SecurityException, NoSuchMethodException, IllegalArgumentException,
      IllegalAccessException, InvocationTargetException {
    // on récupère le nom de la méthode depuis l'appariement de schéma
    String methodName = ann.getRelValidateMeth().get(this);
    // teste si l'appariement a bien été rempli
    if (methodName == null)
      return false;
    // on récupère par introspection la méthode nommée sur la classe
    // ValideRelationFactory
    Class<ValidateRelationFactory> classe = ValidateRelationFactory.class;
    Method meth = classe.getMethod(methodName, IGeneObj.class, IGeneObj.class);
    // on invoque la méthode
    return (Boolean) meth.invoke(new ValidateRelationFactory(), central,
        neighbour);
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
