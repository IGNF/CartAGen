package fr.ign.cogit.cartagen.collagen.components.translator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMacroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMesoConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalRelationalConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.ORPremise;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRule;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRulesDatabase;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class ConstraintsInstanciation {

  private Map<OperationRule, String> mapRegles;
  private Map<FormalMicroConstraint, String> mapMicros;
  private Map<FormalMesoConstraint, String> mapMesos;
  private Map<FormalMacroConstraint, String> mapMacros;
  private Map<FormalRelationalConstraint, String> mapRels;
  private ConstraintDatabase bdc;
  private OperationRulesDatabase bdr;
  private Set<GeneralisationConcept> concepts;

  /**
   * Export de l'instanciation des contraintes de la base en XML pour stockage
   * persistent.
   * @param fic
   * @throws GothicException
   * @throws IOException
   * @throws TransformerException
   */
  public void exportToXml(File fic) throws IOException, TransformerException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("instanciation-contraintes");
    // on stocke la base de règles
    /*
     * Element bdrElem = xmlDoc.createElement("base-de-règles"); n =
     * xmlDoc.createTextNode(bdr.getName()); bdrElem.appendChild(n);
     * root.appendChild(bdrElem);
     */
    // on stocke la base de contraintes
    Element bdcElem = xmlDoc.createElement("base-de-contraintes");
    n = xmlDoc.createTextNode(bdc.getName());
    bdcElem.appendChild(n);
    root.appendChild(bdcElem);

    // on stocke les contraintes micros
    Element microElem = xmlDoc.createElement("micro-constraints");
    for (FormalMicroConstraint c : mapMicros.keySet()) {
      Element instElem = xmlDoc.createElement("monitor");
      n = xmlDoc.createTextNode(mapMicros.get(c));
      instElem.appendChild(n);
      instElem.setAttribute("formal", c.getName());
      microElem.appendChild(instElem);
    }
    root.appendChild(microElem);

    // on stocke les contraintes mesos
    Element mesoElem = xmlDoc.createElement("meso-constraints");
    for (FormalMesoConstraint c : mapMesos.keySet()) {
      Element instElem = xmlDoc.createElement("monitor");
      n = xmlDoc.createTextNode(mapMesos.get(c));
      instElem.appendChild(n);
      instElem.setAttribute("formal", c.getName());
      mesoElem.appendChild(instElem);
    }
    root.appendChild(mesoElem);

    // on stocke les contraintes relationnelles
    Element relElem = xmlDoc.createElement("relational-constraints");
    for (FormalRelationalConstraint c : mapRels.keySet()) {
      Element instElem = xmlDoc.createElement("monitor");
      n = xmlDoc.createTextNode(mapRels.get(c));
      instElem.appendChild(n);
      instElem.setAttribute("formal", c.getName());
      relElem.appendChild(instElem);
    }
    root.appendChild(relElem);

    // on stocke les contraintes macros
    Element macroElem = xmlDoc.createElement("macro-constraints");
    for (FormalMacroConstraint c : mapMacros.keySet()) {
      Element instElem = xmlDoc.createElement("monitor");
      n = xmlDoc.createTextNode(mapMacros.get(c));
      instElem.appendChild(n);
      instElem.setAttribute("formal", c.getName());
      macroElem.appendChild(instElem);
    }
    root.appendChild(macroElem);

    // on stocke les règles
    Element reglesElem = xmlDoc.createElement("rules");
    for (OperationRule r : mapRegles.keySet()) {
      Element instElem = xmlDoc.createElement("monitor");
      n = xmlDoc.createTextNode(mapRegles.get(r));
      instElem.appendChild(n);
      instElem.setAttribute("formal", r.getName());
      reglesElem.appendChild(instElem);
    }
    root.appendChild(reglesElem);

    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, fic);
  }

  public ConstraintsInstanciation(File fic, Set<GeneralisationConcept> concepts)
      throws ParserConfigurationException, SAXException, IOException,
      DOMException, OWLOntologyCreationException {
    this.concepts = concepts;
    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc
        .getElementsByTagName("constraints-instanciation").item(0);

    // puis on charge les bases de règles et contraintes
    Element bdcElem = (Element) root.getElementsByTagName("base-de-contraintes")
        .item(0);
    bdc = new ConstraintDatabase(
        new File(bdcElem.getChildNodes().item(0).getNodeValue()));
    /*
     * Element bdrElem = (Element) root.getElementsByTagName("base-de-règles")
     * .item(0); bdr = new OperationRulesDatabase((new
     * File(bdrElem.getChildNodes().item(0) .getNodeValue())));
     */

    // puis on charge les contraintes micros
    Element microElem = (Element) root.getElementsByTagName("micro-constraints")
        .item(0);
    this.mapMicros = new HashMap<FormalMicroConstraint, String>();
    for (int i = 0; i < microElem.getElementsByTagName("monitor")
        .getLength(); i++) {
      Element instElem = (Element) microElem.getElementsByTagName("monitor")
          .item(i);
      String nom = instElem.getChildNodes().item(0).getNodeValue();
      String contrainte = instElem.getAttribute("formal");
      mapMicros.put(
          (FormalMicroConstraint) bdc.getConstraintFromName(contrainte), nom);
    }

    // puis on charge les contraintes mesos
    Element mesoElem = (Element) root.getElementsByTagName("meso-constraints")
        .item(0);
    this.mapMesos = new HashMap<FormalMesoConstraint, String>();
    for (int i = 0; i < mesoElem.getElementsByTagName("monitor")
        .getLength(); i++) {
      Element instElem = (Element) mesoElem.getElementsByTagName("monitor")
          .item(i);
      String nom = instElem.getChildNodes().item(0).getNodeValue();
      String contrainte = instElem.getAttribute("formal");
      mapMesos.put((FormalMesoConstraint) bdc.getConstraintFromName(contrainte),
          nom);
    }

    // puis on charge les contraintes macros
    Element macroElem = (Element) root.getElementsByTagName("macro-constraints")
        .item(0);
    this.mapMacros = new HashMap<FormalMacroConstraint, String>();
    for (int i = 0; i < macroElem.getElementsByTagName("monitor")
        .getLength(); i++) {
      Element instElem = (Element) macroElem.getElementsByTagName("monitor")
          .item(i);
      String nom = instElem.getChildNodes().item(0).getNodeValue();
      String contrainte = instElem.getAttribute("formal");
      mapMacros.put(
          (FormalMacroConstraint) bdc.getConstraintFromName(contrainte), nom);
    }

    // puis on charge les contraintes relationnelles
    Element relElem = (Element) root
        .getElementsByTagName("relational-constraints").item(0);
    this.mapRels = new HashMap<FormalRelationalConstraint, String>();
    for (int i = 0; i < relElem.getElementsByTagName("monitor")
        .getLength(); i++) {
      Element instElem = (Element) relElem.getElementsByTagName("monitor")
          .item(i);
      String nom = instElem.getChildNodes().item(0).getNodeValue();
      String contrainte = instElem.getAttribute("formal");
      mapRels.put(
          (FormalRelationalConstraint) bdc.getConstraintFromName(contrainte),
          nom);
    }

    // puis on charge les règles
    Element regleElem = (Element) root.getElementsByTagName("rules").item(0);
    this.mapRegles = new HashMap<OperationRule, String>();
    for (int i = 0; i < regleElem.getElementsByTagName("monitor")
        .getLength(); i++) {
      Element instElem = (Element) regleElem.getElementsByTagName("monitor")
          .item(i);
      String nom = instElem.getChildNodes().item(0).getNodeValue();
      mapRegles.put((OperationRule) bdr.getRuleFromName(nom), nom);
    }
  }

  public Set<ConstraintMonitor> instanciateConstraints(
      SchemaAnnotation annotation)
      throws ClassNotFoundException, NoSuchMethodException, SecurityException,
      InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    return instanciateConstraints(null, annotation);
  }

  public Set<ConstraintMonitor> instanciateConstraints(
      Set<SpecificationElement> chosen, SchemaAnnotation annotation)
      throws ClassNotFoundException, NoSuchMethodException, SecurityException,
      InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    return instanciateConstraints(chosen, annotation, null);
  }

  public Set<ConstraintMonitor> instanciateConstraints(
      SchemaAnnotation annotation, IEnvelope geom)
      throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException,
      ClassNotFoundException, SecurityException, NoSuchMethodException {

    return instanciateConstraints(null, annotation, geom);
  }

  /**
   * Instanciate the constraint monitors for a given set of specification
   * elements, on the objects located in the given extent.
   * @param chosen
   * @param annotation
   * @param geom
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  public Set<ConstraintMonitor> instanciateConstraints(
      Set<SpecificationElement> chosen, SchemaAnnotation annotation,
      IEnvelope geom) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException,
      ClassNotFoundException, SecurityException, NoSuchMethodException {

    Set<ConstraintMonitor> monitors = new HashSet<>();

    // ****************************************************
    // on commence par les contraintes sur les bâtiments
    monitors.addAll(
        instanciateOnMicroConcept("bâtiment", annotation, chosen, geom));

    // ****************************************************
    // puis on instancie les contraintes sur les zones arborées
    monitors.addAll(
        instanciateOnMicroConcept("zone_arborée", annotation, chosen, geom));

    // ****************************************************
    // puis on instancie les contraintes sur les routes
    monitors
        .addAll(instanciateOnMicroConcept("route", annotation, chosen, geom));

    // ****************************************************
    // puis on construit les contraintes mesos
    // TODO
    HashSet<IGeneObj> mesos = new HashSet<>();
    for (IGeneObj obj : mesos) {

      GeneralisationConcept meso = GeneralisationConcept.getElemGeoFromName(
          annotation.getConceptFromClassR(obj.getClass().getSimpleName()),
          concepts);
      // on vérifie que l'annotation sémantique a été faite pour cet objet
      if (meso == null)
        continue;
      // on récupère les contraintes
      Set<SpecificationElement> contraintes = getElementsFromConcept(2, meso);
      contraintes.retainAll(chosen);

      // on instancie la contrainte
      monitors.addAll(instanciateConstraintOnObj(obj, this.getMapMesos(),
          annotation, contraintes));
    }

    // ****************************************************
    // puis on construit les contraintes relationnelles
    // TODO
    Set<IGeneObj> rels = new HashSet<>();
    for (IGeneObj obj : rels) {
      // System.out.println("nom relation "+obj.getGothicClassName());
      GeneralisationConcept relation = annotation
          .getRelationNameFromClassName(obj.getClass().getSimpleName());
      Set<SpecificationElement> contraintes = getElementsFromConcept(3,
          relation);
      contraintes.retainAll(chosen);
      // on fait une boucle sur les contraintes
      for (SpecificationElement e : contraintes) {
        FormalRelationalConstraint contr = (FormalRelationalConstraint) e;

        // on commence par vérifier le critère de sélection
        if (contr.getSelectionCrit() != null)
          if (!contr.getSelectionCrit().verify(obj, annotation))
            continue;

        // on vérifie la restriction d'espace
        /*
         * if (contr.getRestriction().size() != 0) { boolean restrict = false;
         * IGeometry geom = obj.getGeom(); for (GeoSpaceConcept espace :
         * contr.getRestriction().keySet()) { // on récupère la classe gothic
         * correspondant String gothClass =
         * annotation.getClassAnnotation().get(espace.getName()); // TODO
         * IGeneObj space = null; if (space == null) continue; restrict = true;
         * break; } if (!restrict) continue; }
         */

        // on instancie la contrainte
        // on récupère le nom de la classe java de contrainte instanciée
        String nomJava = this.mapRels.get(contr);
        Class<?> classeJava = Class.forName(nomJava);
        Constructor<?> construc = classeJava.getConstructor(IGeneObj.class,
            FormalGenConstraint.class);

        monitors.add((ConstraintMonitor) construc.newInstance(obj, contr));
      }
      contraintes.clear();
    }
    return monitors;
  }

  /**
   * Return all the specification elements used in this instanciation.
   * @return
   */
  public Set<SpecificationElement> getSpecificationElts() {
    Set<SpecificationElement> elements = new HashSet<>();
    for (FormalMicroConstraint micro : this.mapMicros.keySet())
      elements.add(micro);
    for (FormalMacroConstraint macro : this.mapMacros.keySet())
      elements.add(macro);
    for (FormalMesoConstraint meso : this.mapMesos.keySet())
      elements.add(meso);
    for (FormalRelationalConstraint rel : this.mapRels.keySet())
      elements.add(rel);
    for (OperationRule rule : this.mapRegles.keySet())
      elements.add(rule);
    return elements;
  }

  public Map<OperationRule, String> getMapRegles() {
    return mapRegles;
  }

  public void setMapRegles(Map<OperationRule, String> mapRegles) {
    this.mapRegles = mapRegles;
  }

  public Map<FormalMicroConstraint, String> getMapMicros() {
    return mapMicros;
  }

  public void setMapMicros(Map<FormalMicroConstraint, String> mapMicros) {
    this.mapMicros = mapMicros;
  }

  public Map<FormalMesoConstraint, String> getMapMesos() {
    return mapMesos;
  }

  public void setMapMesos(Map<FormalMesoConstraint, String> mapMesos) {
    this.mapMesos = mapMesos;
  }

  public Map<FormalMacroConstraint, String> getMapMacros() {
    return mapMacros;
  }

  public void setMapMacros(Map<FormalMacroConstraint, String> mapMacros) {
    this.mapMacros = mapMacros;
  }

  public Map<FormalRelationalConstraint, String> getMapRels() {
    return mapRels;
  }

  public void setMapRels(Map<FormalRelationalConstraint, String> mapRels) {
    this.mapRels = mapRels;
  }

  public ConstraintDatabase getBdc() {
    return bdc;
  }

  public void setBdc(ConstraintDatabase bdc) {
    this.bdc = bdc;
  }

  public OperationRulesDatabase getBdr() {
    return bdr;
  }

  public void setBdr(OperationRulesDatabase bdr) {
    this.bdr = bdr;
  }

  public Set<GeneralisationConcept> getConcepts() {
    return concepts;
  }

  public void setConcepts(Set<GeneralisationConcept> concepts) {
    this.concepts = concepts;
  }

  /**
   * Réalise l'instanciation des contraintes parmi celles choisies en entrée sur
   * tous les objets d'un concept donné.
   * @param nomConcept
   * @param vac
   * @param app
   * @param elemsAInstancier si vaut null, toutes les contraintes sont
   *          instanciées
   * @param env si vaut null, les contraintes sont instanciées dans toutes
   *          données
   * @throws GothicException
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private Set<ConstraintMonitor> instanciateOnMicroConcept(String nomConcept,
      SchemaAnnotation app, Collection<SpecificationElement> elemsAInstancier,
      IEnvelope env) throws ClassNotFoundException, SecurityException,
      NoSuchMethodException, IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    // initialisations
    GeneralisationConcept elemGeo = GeneralisationConcept
        .getElemGeoFromName(nomConcept, app.getConcepts());
    Class<?> classe = Class
        .forName(app.getClassAnnotation().get(elemGeo.toString()));
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
    Set<ConstraintMonitor> monitors = new HashSet<>();

    // first get the monitored objects
    Set<IGeneObj> objs = new HashSet<IGeneObj>();
    if (env == null)
      objs.addAll(dataset.getCartagenPop(dataset.getPopNameFromClass(classe)));
    else
      objs.addAll(dataset.getCartagenPop(dataset.getPopNameFromClass(classe))
          .select(env));
    // on récupère toutes les contraintes instanciées sur le concept
    Set<SpecificationElement> contraintes = getElementsFromConcept(1, elemGeo);
    if (elemsAInstancier != null)
      contraintes.retainAll(elemsAInstancier);
    // on fait une boucle sur les objets gothic
    if (contraintes.size() != 0) {
      for (IGeneObj obj : objs) {
        monitors.addAll(instanciateConstraintOnObj(obj, this.getMapMicros(),
            app, contraintes));
      }
    }

    return monitors;
  }

  /**
   * Parmi les contraintes instanciées, renvoie celles qui portent sur le
   * concept passé en entrée.
   * 
   * @param type 0 pour des règles, 1 micro, 2 meso, 3 rel et 4 macro
   * @param concept le concept dont on cherche les contraintes
   * @return
   */
  private Set<SpecificationElement> getElementsFromConcept(int type,
      GeneralisationConcept concept) {
    Set<SpecificationElement> elems = new HashSet<SpecificationElement>();
    if (type == 0) {
      for (OperationRule r : mapRegles.keySet())
        for (ORPremise c : r.getPremises())
          if (concept.estUn(c.getConcept()))
            elems.add(r);
    }
    if (type == 1)
      for (FormalMicroConstraint c : mapMicros.keySet())
        if (concept.estUn(c.getConcept()))
          elems.add(c);
    if (type == 2)
      for (FormalMesoConstraint c : mapMesos.keySet())
        if (concept.estUn(c.getConcept()))
          elems.add(c);
    if (type == 3)
      for (FormalRelationalConstraint c : mapRels.keySet())
        if (concept.estUn(c.getConcept())) {
          GeographicRelation rel = (GeographicRelation) concept;
          if (rel.relationIsAboutConcept(c.getConcept1())
              && rel.relationIsAboutConcept(c.getConcept2()))
            elems.add(c);
        }
    if (type == 4)
      for (FormalMacroConstraint c : mapMacros.keySet())
        if (concept.estUn(c.getConcept()))
          elems.add(c);

    return elems;
  }

  /**
   * Réalise sur un objet l'instanciation de toutes les contraintes passées en
   * entrée.
   * @param obj
   * @param vac
   * @param map
   * @param app
   * @param contraintes
   * @throws GothicException
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private Set<ConstraintMonitor> instanciateConstraintOnObj(IGeneObj obj,
      Map<? extends SpecificationElement, String> map, SchemaAnnotation app,
      Collection<SpecificationElement> constraints)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    Set<ConstraintMonitor> monitors = new HashSet<>();
    // on fait une boucle sur les contraintes
    for (SpecificationElement e : constraints) {
      FormalGenConstraint contr = (FormalGenConstraint) e;

      // on commence par vérifier le critère de sélection
      if (contr.getSelectionCrit() != null)
        if (!contr.getSelectionCrit().verify(obj, app))
          continue;

      // on vérifie la restriction d'espace
      /*
       * if(contr.getRestriction().size()!=0){ boolean restrict = false;
       * IGeometry geom = obj.getGeom(); for(GeoSpaceConcept
       * espace:contr.getRestriction().keySet()){ // on récupère la classe
       * gothic correspondant String gothClass =
       * app.getClassAnnotation().get(espace.getName()); IGeneObj space = null;
       * // TODO if(space==null) continue; restrict = true; break; }
       * if(!restrict) continue; }
       */

      // on instancie la contrainte
      // on récupère le nom de la classe java de contrainte instanciée
      String nomJava = map.get(contr);
      Class<?> classeJava = Class.forName(nomJava);
      Constructor<?> construc = classeJava.getConstructor(IGeneObj.class,
          FormalGenConstraint.class);

      monitors.add((ConstraintMonitor) construc.newInstance(obj, contr));
    } // for(ElementSpecification e:contraintes)

    return monitors;
  }

}
