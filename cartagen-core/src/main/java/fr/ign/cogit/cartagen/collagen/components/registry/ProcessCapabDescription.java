package fr.ign.cogit.cartagen.collagen.components.registry;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.ProcessingConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRulesDatabase;

@Entity
@Table(name = "process_capabilities_description")
public class ProcessCapabDescription {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  @Column(name = "name")
  private String name;
  private ProcessingConcept processType;
  private String processJavaClass;
  private HashSet<PreConditionProcess> preConditions;
  private HashSet<PostConditionProcess> postConditions;
  private HashMap<GeographicConcept, Boolean> enrichs;// les concepts avec
  // lesquels les données
  // doivent être enrichies pour utiliser correctement ce processus et un
  // booléen valant
  // true si l'enrichissement est local et false s'il est global (sur ttes les
  // données).
  private ScaleRange scales;
  private String translatorName;
  private String path;// le chemin du fichier XML stockant la description de

  // façon persistente

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ProcessCapabDescription(String name, ProcessingConcept processType,
      String processJavaClass, HashSet<PreConditionProcess> preConditions,
      HashSet<PostConditionProcess> postConditions,
      HashMap<GeographicConcept, Boolean> enrichs, ScaleRange scales,
      String translatorName, String path) {
    super();
    this.name = name;
    this.processType = processType;
    this.processJavaClass = processJavaClass;
    this.preConditions = preConditions;
    this.postConditions = postConditions;
    this.enrichs = enrichs;
    this.scales = scales;
    this.translatorName = translatorName;
    this.path = path;
  }

  /**
   * Constructeur d'une description d'un processus à partir d'un fichier XML
   * dans lequel on a stocké la description et d'une base de données Gothic dans
   * laquelle on a stocké les ressources nécessaires à cette description
   * (contraintes et règles).
   * @param fic : le fichier XML contenant la description
   * @throws Exception
   */
  public ProcessCapabDescription(File fic) throws Exception {
    this.path = fic.getPath();

    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc.getElementsByTagName("process-description")
        .item(0);

    // on récupère le nom des ressources associées à ce processus
    Element ressources = (Element) root.getElementsByTagName("resources-used")
        .item(0);
    // d'abord l'ontologie
    Element ontoElem = (Element) ressources.getElementsByTagName("ontology")
        .item(0);
    String uri = ontoElem.getChildNodes().item(0).getNodeValue();
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    IRI physicalURI = IRI.create(uri);
    OWLOntology ontology = manager
        .loadOntologyFromOntologyDocument(physicalURI);
    HashSet<GeneralisationConcept> concepts = GeneralisationConcept
        .ontologyToGeneralisationConcepts(ontology);
    // puis les contraintes
    Element bdcElem = (Element) ressources
        .getElementsByTagName("constraints-database").item(0);
    String nom = bdcElem.getChildNodes().item(0).getNodeValue();
    ConstraintDatabase bdc = ConstraintDatabase.getInstance(nom);
    if (bdc == null)
      bdc = new ConstraintDatabase(nom, concepts, uri);
    // enfin, les règles
    Element bdrElem = (Element) ressources
        .getElementsByTagName("operation-rules-database").item(0);
    nom = bdrElem.getChildNodes().item(0).getNodeValue();
    OperationRulesDatabase bdr = OperationRulesDatabase.getInstance(nom);
    if (bdr == null)
      bdr = new OperationRulesDatabase(nom, concepts, uri);

    // on récupère le processus
    Element nomElem = (Element) root.getElementsByTagName("name").item(0);
    this.name = nomElem.getChildNodes().item(0).getNodeValue();
    Element procElem = (Element) root.getElementsByTagName("process-type")
        .item(0);
    this.processType = (ProcessingConcept) GeneralisationConcept
        .getElemGeoFromName(procElem.getChildNodes().item(0).getNodeValue(),
            concepts);
    Element classeElem = (Element) root.getElementsByTagName("java-class")
        .item(0);
    processJavaClass = classeElem.getChildNodes().item(0).getNodeValue();
    // on récupère le nom du traducteur
    Element tradElem = (Element) root.getElementsByTagName("translator-name")
        .item(0);
    this.translatorName = tradElem.getChildNodes().item(0).getNodeValue();

    // on récupère les pre-conditions
    preConditions = new HashSet<PreConditionProcess>();
    for (int i = 0; i < root.getElementsByTagName("pre-condition")
        .getLength(); i++) {
      Element preCondElem = (Element) root.getElementsByTagName("pre-condition")
          .item(i);
      preConditions.add(new PreConditionProcess(preCondElem, concepts));
    }
    // on récupère les post-conditions
    postConditions = new HashSet<PostConditionProcess>();
    for (int i = 0; i < root.getElementsByTagName("post-condition")
        .getLength(); i++) {
      Element postCondElem = (Element) root
          .getElementsByTagName("post-condition").item(i);
      postConditions.add(new PostConditionProcess(postCondElem, bdc, bdr));
    }
    // on récupère les autres caractéristiques
    Element gammeElem = (Element) root.getElementsByTagName("scale-range")
        .item(0);
    Element iniBasElem = (Element) gammeElem
        .getElementsByTagName("ini-scale-low").item(0);
    String iniBas = iniBasElem.getChildNodes().item(0).getNodeValue();
    Element iniHautElem = (Element) gammeElem
        .getElementsByTagName("ini-scale-high").item(0);
    String iniHaut = iniHautElem.getChildNodes().item(0).getNodeValue();
    Element genBasElem = (Element) gammeElem
        .getElementsByTagName("gen-scale-low").item(0);
    String genBas = genBasElem.getChildNodes().item(0).getNodeValue();
    Element genHautElem = (Element) gammeElem
        .getElementsByTagName("gen-scale-high").item(0);
    String genHaut = genHautElem.getChildNodes().item(0).getNodeValue();
    Element iniOptElem = (Element) gammeElem
        .getElementsByTagName("ini-scale-opt").item(0);
    String iniOpt = iniOptElem.getChildNodes().item(0).getNodeValue();
    Element genOptElem = (Element) gammeElem
        .getElementsByTagName("gen-scale-opt").item(0);
    String genOpt = genOptElem.getChildNodes().item(0).getNodeValue();
    this.scales = new ScaleRange(Integer.valueOf(iniBas),
        Integer.valueOf(iniHaut), Integer.valueOf(genBas),
        Integer.valueOf(genHaut), Integer.valueOf(iniOpt),
        Integer.valueOf(genOpt));

    // on récupère les enrichissements
    enrichs = new HashMap<GeographicConcept, Boolean>();
    Element enrichElem = (Element) root.getElementsByTagName("enrichments")
        .item(0);
    for (int i = 0; i < enrichElem.getElementsByTagName("concept-meso")
        .getLength(); i++) {
      Element mesoElem = (Element) root.getElementsByTagName("concept-meso")
          .item(i);
      Boolean local = Boolean.valueOf(mesoElem.getAttribute("Local"));
      enrichs.put(
          (GeographicConcept) GeneralisationConcept.getElemGeoFromName(
              mesoElem.getChildNodes().item(0).getNodeValue(), concepts),
          local);
    }
  }

  // Getters and setters //
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProcessingConcept getProcessType() {
    return processType;
  }

  public void setProcessType(ProcessingConcept processType) {
    this.processType = processType;
  }

  public String getProcessJavaClass() {
    return processJavaClass;
  }

  public void setProcessJavaClass(String processJavaClass) {
    this.processJavaClass = processJavaClass;
  }

  public HashSet<PreConditionProcess> getPreConditions() {
    return preConditions;
  }

  public void setPreConditions(HashSet<PreConditionProcess> preConditions) {
    this.preConditions = preConditions;
  }

  public HashSet<PostConditionProcess> getPostConditions() {
    return postConditions;
  }

  public void setPostConditions(HashSet<PostConditionProcess> postConditions) {
    this.postConditions = postConditions;
  }

  public HashMap<GeographicConcept, Boolean> getEnrichs() {
    return enrichs;
  }

  public void setEnrichs(HashMap<GeographicConcept, Boolean> enrichs) {
    this.enrichs = enrichs;
  }

  public ScaleRange getScales() {
    return scales;
  }

  public void setScales(ScaleRange scales) {
    this.scales = scales;
  }

  public String getTranslatorName() {
    return translatorName;
  }

  public void setTranslatorName(String translatorName) {
    this.translatorName = translatorName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  // Other public methods //
  /**
   * True if the process described by {@code this} handles the given
   * specification element.
   */
  public boolean handlesSpecification(SpecificationElement spec) {
    for (PostConditionProcess post : getPostConditions()) {
      if (post.getElement().equals(spec))
        return true;
    }
    return false;
  }

  /**
   * Récupère la post-condition de cette description qui correspond à un élément
   * de spécification (contrainte ou règle opérationnelle) donné. Get the
   * post-condition from {@code this} that is related to the given specification
   * element (i.e. constraint or operation rule).
   * @param elem
   * @return
   */
  public PostConditionProcess getPostConditionFromSpec(
      SpecificationElement elem) {
    for (PostConditionProcess post : getPostConditions()) {
      if (post.getElement().equals(elem))
        return post;
    }
    return null;
  }

  /**
   * Get the pre-condition of {@code this} that is related to space's concept,
   * if exists.
   * @param space
   * @return
   */
  public PreConditionProcess getPreConditionFromSpace(GeographicSpace space) {
    for (PreConditionProcess pre : this.preConditions) {
      if (pre.getSpace().equals(space.getConcept()))
        return pre;
    }
    return null;
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
