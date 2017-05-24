package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.util.ReflectionUtil;

/**
 * This class is a registration mapping kind of semantic annotation. It contains
 * the mappings between schema information and a geographic ontology.
 * @author GTouya
 * 
 */
public class SchemaAnnotation {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private Logger logger = Logger.getLogger(SchemaAnnotation.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private String name;
  private Map<String, String> classAnnotation;
  private Map<String, String> linkAnnotation;
  private Map<String, String[]> attributeAnnotation = new HashMap<String, String[]>();
  private Map<GeographicRelation, String> relationAnnotation;
  private Map<GeographicRelation, String> relValidationMethod;
  private Map<GeographicRelation, String> relNeighMethod;
  private Set<GeneralisationConcept> concepts;
  private Set<ProcessingConcept> availableProcs;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public SchemaAnnotation(File file, Set<GeneralisationConcept> concepts)
      throws ParserConfigurationException, SAXException, IOException {
    this.setConcepts(concepts);
    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc.getElementsByTagName("schema-annotation")
        .item(0);

    // on récupère le nom de l'appariement
    Element nomElem = (Element) root.getElementsByTagName("name").item(0);
    name = nomElem.getChildNodes().item(0).getNodeValue();

    // puis on charge les descriptions de processus
    /*
     * Element descrElem = (Element) root.getElementsByTagName(
     * "processus-disponibles").item(0); this.availableProcs = new
     * HashSet<ProcessingConcept>();
     * 
     * for (int i = 0; i < descrElem.getElementsByTagName("description")
     * .getLength(); i++) { Element procElem = (Element) descrElem
     * .getElementsByTagName("description").item(i); String path =
     * procElem.getChildNodes().item(0).getNodeValue(); availableProcs.add(new
     * ProcessingConcept(new File(path))); }
     */

    // on récupère les classes appariées
    this.classAnnotation = new HashMap<String, String>();
    Element classElem = (Element) root.getElementsByTagName(
        "appariement-classes").item(0);
    for (int i = 0; i < classElem.getElementsByTagName("appariement")
        .getLength(); i++) {
      Element appElem = (Element) classElem.getElementsByTagName("appariement")
          .item(i);
      Element ontoElem = (Element) appElem.getElementsByTagName(
          "classe-ontologie").item(0);
      Element gothElem = (Element) appElem.getElementsByTagName("classe-java")
          .item(0);
      classAnnotation.put(ontoElem.getChildNodes().item(0).getNodeValue(),
          gothElem.getChildNodes().item(0).getNodeValue());
    }

    // on récupère les attributs appariés
    this.attributeAnnotation = new HashMap<String, String[]>();
    Element attrElem = (Element) root.getElementsByTagName(
        "appariement-attributs").item(0);
    for (int i = 0; i < attrElem.getElementsByTagName("appariement")
        .getLength(); i++) {
      Element appElem = (Element) attrElem.getElementsByTagName("appariement")
          .item(i);
      Element ontoElem = (Element) appElem.getElementsByTagName(
          "attribut-ontologie").item(0);
      Element gothElem = (Element) appElem
          .getElementsByTagName("attribut-java").item(0);
      String[] key = new String[] { gothElem.getChildNodes().item(0)
          .getNodeValue() };
      if (gothElem.hasAttribute("classe-java"))
        key = new String[] { gothElem.getAttribute("classe-java"),
            gothElem.getChildNodes().item(0).getNodeValue() };
      attributeAnnotation.put(ontoElem.getChildNodes().item(0).getNodeValue(),
          key);
    }

    // on récupère les liens appariés
    this.linkAnnotation = new HashMap<String, String>();
    Element lienElem = (Element) root.getElementsByTagName("appariement-liens")
        .item(0);
    for (int i = 0; i < lienElem.getElementsByTagName("appariement")
        .getLength(); i++) {
      Element appElem = (Element) lienElem.getElementsByTagName("appariement")
          .item(i);
      Element ontoElem = (Element) appElem.getElementsByTagName(
          "lien-ontologie").item(0);
      Element gothElem = (Element) appElem.getElementsByTagName("lien-java")
          .item(0);
      linkAnnotation.put(ontoElem.getChildNodes().item(0).getNodeValue(),
          gothElem.getChildNodes().item(0).getNodeValue());
    }

    // on récupère les relations appariées
    this.relationAnnotation = new HashMap<GeographicRelation, String>();
    this.relValidationMethod = new HashMap<GeographicRelation, String>();
    this.relNeighMethod = new HashMap<GeographicRelation, String>();
    Element relElem = (Element) root.getElementsByTagName(
        "appariement-relations").item(0);
    for (int i = 0; i < relElem.getElementsByTagName("appariement").getLength(); i++) {
      Element appElem = (Element) relElem.getElementsByTagName("appariement")
          .item(i);
      Element ontoElem = (Element) appElem
          .getElementsByTagName("rel-ontologie").item(0);
      Element conc1Elem = (Element) appElem.getElementsByTagName("rel-concept")
          .item(0);
      Element conc2Elem = (Element) appElem.getElementsByTagName("rel-concept")
          .item(1);
      Element gothElem = (Element) appElem.getElementsByTagName("rel-java")
          .item(0);
      Element validElem = (Element) appElem.getElementsByTagName(
          "methode-est-valide").item(0);
      Element voisElem = (Element) appElem.getElementsByTagName(
          "methode-voisins").item(0);
      String nomRel = ontoElem.getChildNodes().item(0).getNodeValue();
      String nomConc1 = conc1Elem.getChildNodes().item(0).getNodeValue();
      String nomConc2 = conc2Elem.getChildNodes().item(0).getNodeValue();
      String validMeth = validElem.getChildNodes().item(0).getNodeValue();
      String voisMeth = voisElem.getChildNodes().item(0).getNodeValue();
      GeographicRelation rel = GeographicRelation.getRelFromName(nomRel,
          nomConc1, nomConc2, concepts);
      relationAnnotation.put(rel, gothElem.getChildNodes().item(0)
          .getNodeValue());
      this.relValidationMethod.put(rel, validMeth);
      this.relNeighMethod.put(rel, voisMeth);
    }
  }

  // Getters and setters //
  public void setAttributeAnnotation(Map<String, String[]> mapAttr) {
    this.attributeAnnotation = mapAttr;
  }

  public Map<String, String[]> getAttributeAnnotation() {
    return this.attributeAnnotation;
  }

  public void setClassAnnotation(Map<String, String> classAnnotation) {
    this.classAnnotation = classAnnotation;
  }

  public Map<String, String> getClassAnnotation() {
    return this.classAnnotation;
  }

  public void setLinkAnnotation(Map<String, String> linkAnnotation) {
    this.linkAnnotation = linkAnnotation;
  }

  public Map<String, String> getLinkAnnotation() {
    return this.linkAnnotation;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setRelationAnnotation(
      Map<GeographicRelation, String> relationAnnotation) {
    this.relationAnnotation = relationAnnotation;
  }

  public Map<GeographicRelation, String> getRelationAnnotation() {
    return this.relationAnnotation;
  }

  public void setRelValidationMethod(
      Map<GeographicRelation, String> relValidationMethod) {
    this.relValidationMethod = relValidationMethod;
  }

  public Map<GeographicRelation, String> getRelValidationMethod() {
    return this.relValidationMethod;
  }

  public void setRelNeighMethod(Map<GeographicRelation, String> relNeighMethod) {
    this.relNeighMethod = relNeighMethod;
  }

  public Map<GeographicRelation, String> getRelNeighMethod() {
    return this.relNeighMethod;
  }

  public Set<GeneralisationConcept> getConcepts() {
    return concepts;
  }

  public void setConcepts(Set<GeneralisationConcept> concepts) {
    this.concepts = concepts;
  }

  public Set<ProcessingConcept> getAvailableProcs() {
    return availableProcs;
  }

  public void setAvailableProcs(Set<ProcessingConcept> availableProcs) {
    this.availableProcs = availableProcs;
  }

  // Other public methods //
  public HashMap<GeographicRelation, String> getRelValidateMeth() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Get the ontology concept corresponding to the schema class name used as
   * parameter or corresponding to the closest super class in schema hierarchy.
   * @param gothicName
   * @return
   * @throws ClassNotFoundException
   */
  public String getConceptFromClassR(String schemaName)
      throws ClassNotFoundException {
    int heritageMin = Integer.MAX_VALUE;
    String concept = null;
    Class<?> schemaClass = Class.forName(schemaName);
    for (String c : this.getClassAnnotation().keySet()) {
      if (schemaName.equals(this.getClassAnnotation().get(c))) {
        return c;
      }
      if (!schemaClass.isAssignableFrom(Class.forName(this.getClassAnnotation()
          .get(c)))) {
        continue;
      }
      int heritage = ReflectionUtil.getInheritanceDepth(
          Class.forName(this.getClassAnnotation().get(c)), schemaClass);
      if (heritage < heritageMin) {
        concept = c;
        heritageMin = heritage;
      }
    }
    return concept;
  }

  /**
   * Get the first key of the spatial relations mapping with a value
   * corresponding to the parameter schema name.
   * @param schemaName
   * @return
   */
  public GeographicRelation getRelationNameFromClassName(String schemaName) {
    for (GeographicRelation rel : this.relationAnnotation.keySet()) {
      this.logger.fine("onto: " + rel);
      if (this.relationAnnotation.get(rel).equals(schemaName)) {
        return rel;
      }
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
  // TODO saveToXml methods
}
