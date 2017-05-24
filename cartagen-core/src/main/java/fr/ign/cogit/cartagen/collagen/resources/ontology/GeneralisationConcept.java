package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLHasValueRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class GeneralisationConcept {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //
  protected String ontology;
  protected String name;
  protected Set<Character> characters;
  protected OWLClass ontologyClass;
  protected Set<Relationship> relations;
  protected Map<String, String> labels;
  // ontology reification
  protected Set<GeneralisationConcept> superConcepts;
  protected Set<GeneralisationConcept> subConcepts;
  protected Set<GeneralisationConcept> thematicNeighbours;

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  public static Class<? extends Object> convertDatatypeOwl(OWLDatatype type) {
    if (type == null) {
      return null;
    }
    String datatype = type.getIRI().getFragment();
    if (datatype.equals("float")) {
      return Float.class;
    } else if (datatype.equals("double")) {
      return Double.class;
    } else if (datatype.equals("boolean")) {
      return Double.class;
    } else if (datatype.equals("int")) {
      return Integer.class;
    } else if (datatype.equals("long")) {
      return Long.class;
    } else if (datatype.equals("string")) {
      return String.class;
    } else if (datatype.equals("date")) {
      return Date.class;
    } else {
      return null;
    }
  }

  public static GeneralisationConcept getElemGeoFromName(String text,
      Set<GeneralisationConcept> concepts) {
    for (GeneralisationConcept e : concepts) {
      if (e.getName().equals(text)) {
        return e;
      }
    }
    return null;
  }

  public static OWLClass getOntoNamedClass(OWLOntology onto, String name) {
    for (OWLClass c : onto.getClassesInSignature()) {
      if (c.getIRI().getFragment().equals(name)) {
        return c;
      }
    }
    return null;
  }

  public static OWLDataProperty getOntoDataProperty(OWLOntology onto,
      String name) {
    for (OWLDataProperty p : onto.getDataPropertiesInSignature()) {
      if (p.getIRI().getFragment().equals(name)) {
        return p;
      }
    }
    return null;
  }

  public static OWLObjectProperty getOntoObjectProperty(OWLOntology onto,
      String name) {
    for (OWLObjectProperty p : onto.getObjectPropertiesInSignature()) {
      if (p.getIRI().getFragment().equals(name)) {
        return p;
      }
    }
    return null;
  }

  public static Set<OWLClass> getAllSuperClasses(OWLClass classe,
      OWLOntology onto) {
    Set<OWLClass> set = new HashSet<OWLClass>();
    Stack<OWLClass> pile = new Stack<OWLClass>();
    for (OWLClassExpression superC : classe.getSuperClasses(onto)) {
      if (OWLClass.class.isInstance(superC)) {
        pile.addElement((OWLClass) superC);
      }
    }
    while (!pile.empty()) {
      OWLClass c = pile.pop();
      set.add(c);
      for (OWLClassExpression superC : c.getSuperClasses(onto)) {
        if (OWLClass.class.isInstance(superC) && !set.contains(superC))
          pile.addElement((OWLClass) superC);
      }
    }
    return set;
  }

  /**
   * Cette méthode statique permet de transformer une ontologie au format OWL en
   * objets ConceptDeGeneralisation et Caracteres. Renvoie un set des objets
   * créés.
   * 
   * @param owlModel : l'objet java permettant de manipuler une ontologie OWL.
   * @return un set d'éléments géographiques structurés.
   */
  public static HashSet<GeneralisationConcept> ontologyToGeneralisationConcepts(
      OWLOntology ontology) {
    // initialisation
    HashSet<GeneralisationConcept> set = new HashSet<GeneralisationConcept>();
    OWLClass racine = GeneralisationConcept.getOntoNamedClass(ontology,
        "entité_de_généralisation");
    OWLClass classeRel = GeneralisationConcept.getOntoNamedClass(ontology,
        "relation_géographique");
    OWLClass classeOp = GeneralisationConcept.getOntoNamedClass(ontology,
        "opération_de_généralisation");
    OWLClass classeProc = GeneralisationConcept.getOntoNamedClass(ontology,
        "processus_de_généralisation");
    OWLClass classeEspace = GeneralisationConcept.getOntoNamedClass(ontology,
        "espace_géographique");
    OWLClass classePart = GeneralisationConcept.getOntoNamedClass(ontology,
        "ValuePartition");
    OWLObjectProperty rel = GeneralisationConcept.getOntoObjectProperty(
        ontology, "relation_concerne_entites");
    OWLObjectProperty rep = GeneralisationConcept.getOntoObjectProperty(
        ontology, "a_pour_représentation_dans_bdg");
    // on commence par récupérer toutes les classes
    Collection<OWLClass> classes = ontology.getClassesInSignature();
    // puis toutes les propriétés
    Collection<OWLDataProperty> props = ontology.getDataPropertiesInSignature();
    OWLObjectProperty primitive = GeneralisationConcept.getOntoObjectProperty(
        ontology, "a_une_primitive_géométrique");

    // enfin, tous les liens
    Collection<OWLObjectProperty> liens = ontology
        .getObjectPropertiesInSignature();

    // on stocke petit à petit les concepts traduits en objets java dans une
    // map
    HashMap<OWLClass, GeneralisationConcept> mapE = new HashMap<OWLClass, GeneralisationConcept>();
    HashMap<OWLDataProperty, Character> mapC = new HashMap<OWLDataProperty, Character>();

    // on construit une fois pour toutes les caractères macro
    Character caracQuantLin = new Character("quantité_occupation_linéaire",
        Double.class, CharacterType.STAT, true);
    Character caracQuantSurf = new Character("quantité_occupation_surfacique",
        Double.class, CharacterType.STAT, true);
    Character caracQuantObj = new Character("nombre_d_objets", Integer.class,
        CharacterType.STAT, true);
    Character caracDensSurf = new Character("densité_occupation_surfacique",
        Double.class, CharacterType.STAT, true);

    // on fait une boucle sur les classes
    for (OWLClass classe : classes) {
      // on ne garde pas la racine de l'ontologie
      if (classe.equals(racine)) {
        continue;
      }
      // on ne garde pas non plus les sous-concepts de primitive et
      // représentation
      Set<OWLClass> superClasses = GeneralisationConcept.getAllSuperClasses(
          classe, ontology);
      if (superClasses.contains(classePart) || classe.equals(classePart)) {
        continue;
      }
      // on crée le nouveau ConceptDeGeneralisation
      String onto = ontology.getOntologyID().getOntologyIRI().toString();
      // on teste si c'est une relation ou un concept
      GeneralisationConcept elem = null;
      String nom = classe.getIRI().getFragment();
      if (superClasses.contains(classeRel)) {
        elem = new GeographicRelation(ontology, onto, nom, classe);
      } else if (superClasses.contains(classeOp)
          || superClasses.contains(classeProc)) {
        elem = new ProcessingConcept(ontology, onto, nom, classe);
      } else if (superClasses.contains(classeEspace)
          || classe.equals(classeEspace)) {
        elem = new GeoSpaceConcept(ontology, onto, nom, classe,
            new HashSet<GeographicConcept>());
      } else {
        elem = new GeographicConcept(ontology, onto, nom, classe);
      }

      set.add(elem);
      mapE.put(classe, elem);
      // on crée les propriétés de l'élément
      for (OWLDataProperty prop : props) {
        if (prop.getDomains(ontology).contains(classe)) {
          elem.addCharactersToElem(prop, ontology, mapC);
        } else {
          if (prop.getDomains(ontology).size() == 0) {
            continue;
          }
          OWLClassExpression descr = prop.getDomains(ontology).iterator()
              .next();
          if (!OWLObjectUnionOf.class.isInstance(descr)) {
            continue;
          }
          if (((OWLObjectUnionOf) descr).getOperands().contains(classe)) {
            elem.addCharactersToElem(prop, ontology, mapC);
          }
        }
      }// boucle sur les propriétés
       // on crée les caractères macro du concept
       // on cherche d'abord si le concept a une primitive géom
       // restreinte
      AllRestrictVisitor visitor = new AllRestrictVisitor(ontology, primitive);
      classe.accept(visitor);
      if (visitor.getRestrictedClass() != null) {
        // on teste le type de géométrie restreinte
        OWLClass prim = visitor.getRestrictedClass();
        if (prim.getIRI().getFragment().equals("polyligne")) {
          caracQuantLin.getConcepts().add(elem);
          elem.getCharacters().add(caracQuantLin);
        } else if (prim.getIRI().getFragment().equals("polygone")) {
          caracQuantSurf.getConcepts().add(elem);
          caracDensSurf.getConcepts().add(elem);
          elem.getCharacters().add(caracQuantSurf);
          elem.getCharacters().add(caracDensSurf);
        }
        // on ajoute les caractères macro non spécifiques à la géométrie
        caracQuantObj.getConcepts().add(elem);
        elem.getCharacters().add(caracQuantObj);
      }

      // on détermine maintenant les héritages de concepts
      // on commence par les surConcepts
      for (OWLClassExpression superC : classe.getSuperClasses(ontology)) {
        if (!OWLClass.class.isInstance(superC)) {
          continue;
        }
        if (mapE.keySet().contains(superC)) {
          elem.superConcepts.add(mapE.get(superC));
          mapE.get(superC).subConcepts.add(elem);
        }
      }
      // puis on traite les sousConcepts
      for (Object sousC : classe.getSubClasses(ontology)) {
        if (!OWLClass.class.isInstance(sousC)) {
          continue;
        }
        if (mapE.keySet().contains(sousC)) {
          elem.subConcepts.add(mapE.get(sousC));
          mapE.get(sousC).superConcepts.add(elem);
        }
      }
    }// for, boucle sur les classes OWL de l'ontologie

    // on détermine enfin les liens entre concepts (dont voisinage
    // thématique)
    for (GeneralisationConcept elem : set) {
      // on fait une boucle sur les liens
      for (OWLObjectProperty lien : liens) {
        if (lien.toString().equals("a_une_primitive_géométrique")) {
          continue;
        }
        if (lien.equals(rel)) {
          continue;
        }
        if (lien.toString().equals("a_une_structure")) {
          continue;
        }
        if (lien.equals(rep)) {
          continue;
        }
        if (lien.toString().equals("contient_les_entités")) {
          // on teste si elem est concerné par ce lien
          if (!(elem instanceof GeoSpaceConcept)) {
            continue;
          }
          RelSomeRestrictVisitor visit = new RelSomeRestrictVisitor(ontology);
          elem.ontologyClass.accept(visit);
          for (OWLClass c : visit.getRestrictedClasses()) {
            ((GeoSpaceConcept) elem).getContainsRestriction().add(
                (GeographicConcept) mapE.get(c));
          }
          continue;
        }
        if (lien.toString().equals("voisinage_thématique")) {
          // TODO à traiter quand la notion de voisinage thématique
          // sera plus
          // claire
          continue;
        }
        // on teste si elem est concerné par ce lien
        if (!lien.getDomains(ontology).contains(elem.ontologyClass)) {
          continue;
        }
        // on récupère les infos sur ce lien
        // on récupère la classe
        GeneralisationConcept elem2 = mapE.get(lien.getRanges(ontology)
            .iterator().next().asOWLClass());
        // on calcule la cardinalité du lien
        int card = -1;
        if (lien.isFunctional(ontology)) {
          card = 1;
        }
        CardRestrictVisitor visitor = new CardRestrictVisitor(ontology, lien);
        elem.ontologyClass.accept(visitor);
        if (visitor.getCardinality() != 0) {
          card = visitor.getCardinality();
        }
        // on crée un objet association avec ce lien
        Relationship assoc = new Relationship(lien.getIRI().getFragment(),
            elem, elem2, card);
        elem.getRelations().add(assoc);
        // on cherche s'il a un lien inverse
        if (lien.getInverses(ontology).size() != 0) {
          OWLObjectProperty inverse = (OWLObjectProperty) lien
              .getInverses(ontology).iterator().next();
          liens.remove(inverse);
          // on construit l'association de l'inverse
          // on calcule la cardinalité du lien
          int cardI = -1;
          if (inverse.isFunctional(ontology)) {
            cardI = 1;
          }
          visitor = new CardRestrictVisitor(ontology, inverse);
          elem2.ontologyClass.accept(visitor);
          if (visitor.getCardinality() != 0) {
            cardI = visitor.getCardinality();
          }
          // on crée un objet association avec ce lien
          Relationship assocI = new Relationship(
              inverse.getIRI().getFragment(), elem2, elem, cardI);
          elem2.getRelations().add(assocI);
          assoc.setInverse(assocI);
          assocI.setInverse(assoc);
        }
      }
    }

    // on détermine les restrictions conceptuelles sur les relations
    for (OWLClass classe : mapE.keySet()) {
      if (!GeographicRelation.class.isInstance(mapE.get(classe))) {
        continue;
      }

      // on cherche si le lien "relation_concerne_entites" est restreint
      // pour
      // cette relation dans l'ontologie.
      RelSomeRestrictVisitor visitSome = new RelSomeRestrictVisitor(ontology);
      RelOnlyRestrictVisitor visitOnly = new RelOnlyRestrictVisitor(ontology);
      classe.accept(visitSome);
      classe.accept(visitOnly);
      HashSet<GeographicConcept> concepts = new HashSet<GeographicConcept>();
      for (OWLClass restric : visitSome.getRestrictedClasses()) {
        concepts.add((GeographicConcept) mapE.get(restric));
      }
      for (OWLClass restric : visitOnly.getRestrictedClasses()) {
        concepts.add((GeographicConcept) mapE.get(restric));
      }
      ((GeographicRelation) mapE.get(classe)).getConcepts().addAll(concepts);
    }

    return set;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public GeneralisationConcept(OWLOntology ontology, String ontoName,
      String name, OWLClass classOnto) {
    this.ontology = ontoName;
    this.name = name;
    this.ontologyClass = classOnto;
    this.superConcepts = new HashSet<GeneralisationConcept>();
    this.subConcepts = new HashSet<GeneralisationConcept>();
    this.thematicNeighbours = new HashSet<GeneralisationConcept>();
    this.characters = new HashSet<Character>();
    this.relations = new HashSet<Relationship>();
    this.labels = new HashMap<String, String>();
    for (OWLAnnotation annotation : classOnto.getAnnotations(ontology)) {
      if (!annotation.getProperty().isLabel())
        continue;
      if (annotation.getValue() instanceof OWLLiteral) {
        OWLLiteral val = (OWLLiteral) annotation.getValue();
        this.labels.put(val.getLang(), val.getLiteral());
      }
    }
  }

  public GeneralisationConcept(String ontology, String name,
      Set<Character> characters, OWLClass ontologyClass,
      Set<Relationship> relations, Map<String, String> labels,
      Set<GeneralisationConcept> superConcepts,
      Set<GeneralisationConcept> subConcepts,
      Set<GeneralisationConcept> thematicNeighbours) {
    super();
    this.ontology = ontology;
    this.name = name;
    this.characters = characters;
    this.ontologyClass = ontologyClass;
    this.relations = relations;
    this.labels = labels;
    this.superConcepts = superConcepts;
    this.subConcepts = subConcepts;
    this.thematicNeighbours = thematicNeighbours;
  }

  // Getters and setters //
  public String getOntology() {
    return this.ontology;
  }

  public void setOntology(String ontology) {
    this.ontology = ontology;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Character> getCharacters() {
    return this.characters;
  }

  public void setCharacters(Set<Character> characters) {
    this.characters = characters;
  }

  public OWLClass getOntologyClass() {
    return this.ontologyClass;
  }

  public void setOntologyClass(OWLClass ontologyClass) {
    this.ontologyClass = ontologyClass;
  }

  public Set<Relationship> getRelations() {
    return this.relations;
  }

  public void setRelations(Set<Relationship> relations) {
    this.relations = relations;
  }

  public Set<GeneralisationConcept> getSuperConcepts() {
    return this.superConcepts;
  }

  public void setSuperConcepts(Set<GeneralisationConcept> superConcepts) {
    this.superConcepts = superConcepts;
  }

  public Set<GeneralisationConcept> getSubConcepts() {
    return this.subConcepts;
  }

  public void setSubConcepts(Set<GeneralisationConcept> subConcepts) {
    this.subConcepts = subConcepts;
  }

  public Set<GeneralisationConcept> getThematicNeighbours() {
    return this.thematicNeighbours;
  }

  public void setThematicNeighbours(
      Set<GeneralisationConcept> thematicNeighbours) {
    this.thematicNeighbours = thematicNeighbours;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  // Other public methods //
  @Override
  public boolean equals(Object obj) {
    return this.name.equals(((GeneralisationConcept) obj).name);
  }

  @Override
  public int hashCode() {
    return this.ontologyClass.getIRI().hashCode();
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   * Renvoie tous les caractères de ce concept (les siens propres et ceux
   * hérités).
   * 
   * @return
   */
  public Set<Character> getTousCaracteres() {
    Set<Character> set = new HashSet<Character>();
    set.addAll(this.characters);
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept elem = concepts.pop();
      set.addAll(elem.getCharacters());
      concepts.addAll(elem.getSuperConcepts());
    }
    return set;
  }

  /**
   * Renvoie tous les liens de ce concept (les siens propres et ceux hérités).
   * 
   * @return
   */
  public Set<Relationship> getToutesAssociations() {
    Set<Relationship> set = new HashSet<Relationship>();
    set.addAll(this.relations);
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept elem = concepts.pop();
      set.addAll(elem.getRelations());
      concepts.addAll(elem.getSuperConcepts());
    }
    return set;
  }

  /**
   * Renvoie tous les caractères simples de ce concept (les siens propres et
   * ceux hérités).
   * 
   * @return
   */
  public Set<Character> getTousCaracteresSimples() {
    Set<Character> set = new HashSet<Character>();
    for (Character c : this.characters) {
      if (!c.isMacro()) {
        set.add(c);
      }
    }
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept elem = concepts.pop();
      for (Character c : elem.getCharacters()) {
        if (!c.isMacro()) {
          set.add(c);
        }
      }
      concepts.addAll(elem.getSuperConcepts());
    }
    return set;
  }

  /**
   * Renvoie tous les caractères d'un ensemble de concepts.
   * 
   * @return
   */
  public static Set<Character> getTousCaracteres(
      HashSet<GeneralisationConcept> elems) {
    Set<Character> set = new HashSet<Character>();
    for (GeneralisationConcept e : elems) {
      set.addAll(e.getCaracteresSimples());
    }
    return set;
  }

  public Set<Character> getCaracteresSimples() {
    Set<Character> set = new HashSet<Character>();
    for (Character c : this.characters) {
      if (!c.isMacro()) {
        set.add(c);
      }
    }
    return set;
  }

  /**
   * Renvoie toutes les associations d'un ensemble de concepts.
   * 
   * @return
   */
  public static Set<Relationship> getToutesAssociations(
      HashSet<GeneralisationConcept> elems) {
    Set<Relationship> set = new HashSet<Relationship>();
    for (GeneralisationConcept e : elems) {
      set.addAll(e.getRelations());
    }
    return set;
  }

  /**
   * Renvoie toutes les relations géo d'un ensemble de concepts.
   * 
   * @return
   */
  public static Set<GeographicRelation> getToutesRelations(
      HashSet<GeneralisationConcept> elems) {
    Set<GeographicRelation> set = new HashSet<GeographicRelation>();
    for (GeneralisationConcept e : elems) {
      if (GeographicRelation.class.isInstance(e)) {
        set.add((GeographicRelation) e);
      }
    }
    return set;
  }

  /**
   * Renvoie tous les caractères macro de ce concept (les siens propres et ceux
   * hérités). Les caractères macro portent sur la population et non l'instance
   * (comme une propriété static en java).
   * 
   * @return
   */
  public Set<Character> getCaracteresMacro() {
    Set<Character> set = new HashSet<Character>();
    for (Character c : this.characters) {
      if (c.isMacro()) {
        set.add(c);
      }
    }
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept elem = concepts.pop();
      for (Character c : elem.getCharacters()) {
        if (c.isMacro()) {
          set.add(c);
        }
      }
      concepts.addAll(elem.getSuperConcepts());
    }
    return set;
  }

  /**
   * Renvoie l'objet caractère du concept this qui a pour nom "nom".
   * 
   * @param name : le nom du caractère que l'on cherche à obtenir
   * @return
   */
  public Character getCaracFromNom(String characterName) {
    for (Character c : this.getTousCaracteres()) {
      if (c.getName().equals(characterName)) {
        return c;
      }
    }
    return null;
  }

  /**
   * Récupère récursivement tous les sousConcepts de this et leurs propres
   * sousConcepts.
   * 
   * @return le set des tous les sousConcepts récursivement
   */
  public HashSet<GeneralisationConcept> getTousSousElements() {
    HashSet<GeneralisationConcept> set = new HashSet<GeneralisationConcept>();
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.subConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept c = concepts.pop();
      set.add(c);
      concepts.addAll(c.getSubConcepts());
    }
    return set;
  }

  /**
   * Récupère récursivement tous les sousConcepts de this et leurs propres
   * sousConcepts.
   * 
   * @return le set des tous les sousConcepts récursivement
   */
  public HashSet<GeneralisationConcept> getTousSurElements() {
    HashSet<GeneralisationConcept> set = new HashSet<GeneralisationConcept>();
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept c = concepts.pop();
      set.add(c);
      concepts.addAll(c.getSuperConcepts());
    }
    return set;
  }

  /**
   * Détermine si l'élément "est un" autre élément : les concepts sont égaux ou
   * this est un sous-élément de elem.
   * 
   * @param elem
   * @return
   */
  public boolean estUn(GeneralisationConcept elem) {
    if (this.equals(elem)) {
      return true;
    }
    if (elem.getTousSousElements().contains(this)) {
      return true;
    }
    return false;
  }

  /**
   * Détermine l'élément ontologique parent à la fois de this et elem. S'il
   * s'agit de "Thing", null est renvoyé.
   * 
   * @param elem
   * @return
   */
  public GeneralisationConcept getParentCommun(GeneralisationConcept elem) {
    if (this.estUn(elem)) {
      return elem;
    }
    if (elem.estUn(this)) {
      return this;
    }
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    concepts.addAll(this.superConcepts);
    while (!concepts.isEmpty()) {
      GeneralisationConcept e = concepts.pop();
      if (elem.estUn(e)) {
        return e;
      }
      concepts.addAll(e.getSuperConcepts());
    }
    return null;
  }

  public int distanceParent(GeneralisationConcept parent) {
    if (!this.estUn(parent)) {
      return Integer.MAX_VALUE;
    }
    HashMap<GeneralisationConcept, Integer> distances = new HashMap<GeneralisationConcept, Integer>();
    Stack<GeneralisationConcept> concepts = new Stack<GeneralisationConcept>();
    for (GeneralisationConcept e : this.superConcepts) {
      concepts.add(e);
      distances.put(e, 1);
    }
    while (!concepts.isEmpty()) {
      GeneralisationConcept e = concepts.pop();
      if (parent.equals(e)) {
        return distances.get(e);
      }
      for (GeneralisationConcept el : e.getSuperConcepts()) {
        concepts.add(el);
        distances.put(el, distances.get(e) + 1);
      }
    }
    return Integer.MAX_VALUE;
  }

  /**
   * Détermine la distance de Wu-Palmer dans l'ontologie entre les concepts this
   * et elem. Il s'agit de la somme des distances entre les 2 éléments et leur
   * plus proche parent commun.
   * 
   * @param elem
   * @return
   */
  public int distanceOnto(GeneralisationConcept elem) {
    if (this.equals(elem)) {
      return 0;
    }
    // on cherche le concept parent des deux
    GeneralisationConcept parent = this.getParentCommun(elem);
    if (parent == null) {
      return this.getTousSurElements().size()
          + elem.getTousSurElements().size();
    }
    if (parent.equals(this)) {
      return elem.distanceParent(this);
    }
    if (parent.equals(elem)) {
      return this.distanceParent(elem);
    }
    return elem.distanceParent(parent) + this.distanceParent(parent);
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
  private void addCharactersToElem(OWLDataProperty prop, OWLOntology ontology1,
      HashMap<OWLDataProperty, Character> mapC) {
    // on teste s'il le caractère est déjà créé
    if (mapC.keySet().contains(prop)) {
      Character carac = mapC.get(prop);
      carac.getConcepts().add(this);
      this.getCharacters().add(carac);
    } else {
      // sinon, on crée un nouveau caractère associé à elem
      // on commence par récupérer la famille du caractère
      CharacterType famille = CharacterType
          .valueofFrench(((OWLDataProperty) prop.getSuperProperties(ontology1)
              .iterator().next()).getIRI().getFragment());
      Class<? extends Object> datatype = null;// cas sans type prévu dans
      // l'ontologie
      if (prop.getRanges(ontology1).size() != 0) {
        datatype = GeneralisationConcept.convertDatatypeOwl((OWLDatatype) prop
            .getRanges(ontology1).iterator().next());
      }
      Character carac = new Character(prop.getIRI().getFragment(), datatype,
          famille, false);
      carac.getConcepts().add(this);
      this.getCharacters().add(carac);
      mapC.put(prop, carac);
    }
  }

  // ////////////////////////////////////////
  // Enumerations and internal classes //
  // ////////////////////////////////////////
  private static class AllRestrictVisitor extends
      OWLClassExpressionVisitorAdapter {
    private boolean processInherited = true;
    private Set<OWLClass> processedClasses;
    private OWLClass restrictedClass = null;
    private OWLOntology ont;
    private OWLObjectProperty prop;

    public AllRestrictVisitor(OWLOntology ont, OWLObjectProperty prop) {
      this.processedClasses = new HashSet<OWLClass>();
      this.ont = ont;
      this.prop = prop;
    }

    public OWLClass getRestrictedClass() {
      return this.restrictedClass;
    }

    @Override
    public void visit(OWLClass desc) {
      if (this.processInherited && !this.processedClasses.contains(desc)) {
        // If we are processing inherited restrictions then
        // we recursively visit named supers. Note that we
        // need to keep track of the classes that we have processed
        // so that we don't get caught out by cycles in the taxonomy
        this.processedClasses.add(desc);
        for (OWLSubClassOfAxiom ax : this.ont
            .getSubClassAxiomsForSubClass(desc)) {
          ax.getSuperClass().accept(this);
        }
      }
    }

    @Override
    public void visit(OWLObjectAllValuesFrom desc) {
      if (desc.getProperty().asOWLObjectProperty().equals(this.prop)) {
        this.restrictedClass = desc.getFiller().asOWLClass();
      }
    }
  }

  private static class CardRestrictVisitor extends
      OWLClassExpressionVisitorAdapter {
    private boolean processInherited = true;
    private Set<OWLClass> processedClasses;
    private int cardinality = 0;
    private OWLOntology ont;
    private OWLObjectProperty lien;

    public CardRestrictVisitor(OWLOntology ont, OWLObjectProperty lien) {
      this.processedClasses = new HashSet<OWLClass>();
      this.ont = ont;
      this.lien = lien;
    }

    public int getCardinality() {
      return this.cardinality;
    }

    @Override
    public void visit(OWLClass desc) {
      if (this.processInherited && !this.processedClasses.contains(desc)) {
        // If we are processing inherited restrictions then
        // we recursively visit named supers. Note that we
        // need to keep track of the classes that we have processed
        // so that we don't get caught out by cycles in the taxonomy
        this.processedClasses.add(desc);
        for (OWLSubClassOfAxiom ax : this.ont
            .getSubClassAxiomsForSubClass(desc)) {
          ax.getSuperClass().accept(this);
        }
      }
    }

    @SuppressWarnings({ "unused" })
    public void visit(
        OWLCardinalityRestriction<OWLObjectProperty, OWLPropertyRange> desc) {
      if (desc.getProperty().equals(this.lien)) {
        this.cardinality = desc.getCardinality();
      }
    }
  }

  private static class RelSomeRestrictVisitor extends
      OWLClassExpressionVisitorAdapter {
    private boolean processInherited = true;
    private Set<OWLClass> processedClasses;
    private Set<OWLClass> restrictedClasses;
    private OWLOntology ont;
    private IRI prim;

    public RelSomeRestrictVisitor(OWLOntology ont) {
      this.processedClasses = new HashSet<OWLClass>();
      this.restrictedClasses = new HashSet<OWLClass>();
      this.ont = ont;
      this.prim = IRI.create(ont.getOntologyID().getOntologyIRI()
          + "#relation_concerne_entites");

    }

    public Set<OWLClass> getRestrictedClasses() {
      return this.restrictedClasses;
    }

    @Override
    public void visit(OWLClass desc) {
      if (this.processInherited && !this.processedClasses.contains(desc)) {
        // If we are processing inherited restrictions then
        // we recursively visit named supers. Note that we
        // need to keep track of the classes that we have processed
        // so that we don't get caught out by cycles in the taxonomy
        this.processedClasses.add(desc);
        for (OWLSubClassOfAxiom ax : this.ont
            .getSubClassAxiomsForSubClass(desc)) {
          ax.getSuperClass().accept(this);
        }
      }
    }

    @SuppressWarnings({ "unused" })
    public void visit(OWLHasValueRestriction<?, OWLNamedObject> desc) {
      if (desc.getValue().getIRI().equals(this.prim)) {
        this.restrictedClasses.add(desc.asSomeValuesFrom().asOWLClass());
      }
    }
  }

  private static class RelOnlyRestrictVisitor extends
      OWLClassExpressionVisitorAdapter {
    private boolean processInherited = true;
    private Set<OWLClass> processedClasses;
    private Set<OWLClass> restrictedClasses;
    private OWLOntology ont;
    private IRI prim;

    public RelOnlyRestrictVisitor(OWLOntology ont) {
      this.processedClasses = new HashSet<OWLClass>();
      this.restrictedClasses = new HashSet<OWLClass>();
      this.ont = ont;
      this.prim = IRI.create(ont.getOntologyID().getOntologyIRI()
          + "#relation_concerne_entites");

    }

    public Set<OWLClass> getRestrictedClasses() {
      return this.restrictedClasses;
    }

    @Override
    public void visit(OWLClass desc) {
      if (this.processInherited && !this.processedClasses.contains(desc)) {
        // If we are processing inherited restrictions then
        // we recursively visit named supers. Note that we
        // need to keep track of the classes that we have processed
        // so that we don't get caught out by cycles in the taxonomy
        this.processedClasses.add(desc);
        for (OWLSubClassOfAxiom ax : this.ont
            .getSubClassAxiomsForSubClass(desc)) {
          ax.getSuperClass().accept(this);
        }
      }
    }

    @Override
    public void visit(OWLObjectAllValuesFrom desc) {
      if (desc.getProperty().asOWLObjectProperty().getIRI().equals(this.prim)) {
        this.restrictedClasses.add(desc.getFiller().asOWLClass());
      }
    }
  }

}
