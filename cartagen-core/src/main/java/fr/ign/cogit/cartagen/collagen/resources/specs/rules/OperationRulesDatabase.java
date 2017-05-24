package fr.ign.cogit.cartagen.collagen.resources.specs.rules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.dom.DocumentImpl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.ProcessingConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.CharacterValueType;
import fr.ign.cogit.cartagen.collagen.resources.specs.SimpleOperator;
import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.geoxygene.util.XMLUtil;

@Entity
public class OperationRulesDatabase {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static HashSet<OperationRulesDatabase> currentDbs = new HashSet<OperationRulesDatabase>();
  private static HashSet<String> currentDbNames = new HashSet<String>();
  private Logger logger = Logger.getLogger(ConstraintDatabase.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Set<OperationRule> rules;
  private HashSet<GeneralisationConcept> concepts;
  private String uriConcepts;
  private String name;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  public static OperationRulesDatabase getInstance(String name) {
    for (OperationRulesDatabase db : OperationRulesDatabase.currentDbs) {
      if (db.getName().equals(name)) {
        return db;
      }
    }
    return null;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public OperationRulesDatabase(String name,
      HashSet<GeneralisationConcept> ontology, String uri) {
    this.rules = new HashSet<OperationRule>();
    this.name = name;
    this.concepts = ontology;
    this.uriConcepts = uri;
    OperationRulesDatabase.currentDbs.add(this);
    OperationRulesDatabase.currentDbNames.add(name);
  }

  /**
   * Constructor from XML file.
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws OWLOntologyCreationException
   * 
   */
  public OperationRulesDatabase(File fic) throws ParserConfigurationException,
      SAXException, IOException, OWLOntologyCreationException {
    this.rules = new HashSet<OperationRule>();

    // on ouvre le fichier xml
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();
    // on parcours le document XML
    Element root = (Element) doc
        .getElementsByTagName("operation-rules-database").item(0);

    // on récupère le nom
    Element nomBdElem = (Element) root.getElementsByTagName("nom").item(0);
    this.name = nomBdElem.getChildNodes().item(0).getNodeValue();
    if (OperationRulesDatabase.currentDbNames.contains(this.name)) {
      this.logger.warning("A constraint DB with the same name already exists");
      return;
    }

    // on récupère l'ontologie
    Element ontoElem = (Element) root.getElementsByTagName("ontology").item(0);
    this.uriConcepts = ontoElem.getChildNodes().item(0).getNodeValue();
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    IRI physicalURI = IRI.create(this.uriConcepts);
    OWLOntology ontology = manager
        .loadOntologyFromOntologyDocument(physicalURI);
    this.concepts = GeneralisationConcept
        .ontologyToGeneralisationConcepts(ontology);

    // on charge les règles opérationnelles
    for (int i = 0; i < root.getElementsByTagName("operation-rule")
        .getLength(); i++) {
      Element regleElem = (Element) root.getElementsByTagName("operation-rule")
          .item(i);
      Element nomElem = (Element) regleElem.getElementsByTagName("name")
          .item(0);
      String nomRegle = nomElem.getChildNodes().item(0).getNodeValue();
      Element impElem = (Element) regleElem.getElementsByTagName("importance")
          .item(0);
      int importance = Integer
          .valueOf(impElem.getChildNodes().item(0).getNodeValue());
      // on récupère la conclusion
      Element conclElem = (Element) regleElem.getElementsByTagName("conclusion")
          .item(0);
      Element opElem = (Element) conclElem.getElementsByTagName("action")
          .item(0);
      String nomConcl = opElem.getChildNodes().item(0).getNodeValue();
      ProcessingConcept action = (ProcessingConcept) GeneralisationConcept
          .getElemGeoFromName(nomConcl, this.concepts);
      Element adviceElem = (Element) conclElem.getElementsByTagName("advice")
          .item(0);
      boolean advice = Boolean
          .valueOf(adviceElem.getChildNodes().item(0).getNodeValue());
      Element positiveElem = (Element) conclElem
          .getElementsByTagName("positive").item(0);
      boolean positive = Boolean
          .valueOf(positiveElem.getChildNodes().item(0).getNodeValue());
      ORConclusion conclusion = new ORConclusion(positive, advice, action);
      // on récupère la prémisse
      Element premElem = (Element) regleElem.getElementsByTagName("premises")
          .item(0);
      HashSet<ORPremise> premisse = new HashSet<ORPremise>();
      for (int j = 0; j < premElem.getElementsByTagName("premise")
          .getLength(); j++) {
        Element condElem = (Element) premElem.getElementsByTagName("premise")
            .item(j);
        // le concept
        Element concElem = (Element) condElem.getElementsByTagName("concept")
            .item(0);
        String nomConc = concElem.getChildNodes().item(0).getNodeValue();
        GeneralisationConcept concept = GeneralisationConcept
            .getElemGeoFromName(nomConc, this.concepts);
        // le caractère
        Element carElem = (Element) condElem.getElementsByTagName("character")
            .item(0);
        String nomCar = carElem.getChildNodes().item(0).getNodeValue();
        Character caractere = concept.getCaracFromNom(nomCar);
        // l'unité
        Element unitElem = (Element) condElem.getElementsByTagName("unit")
            .item(0);
        ValueUnit unite = ValueUnit
            .valueOf(unitElem.getChildNodes().item(0).getNodeValue());
        // le mot-clé
        Element motCleElem = (Element) condElem.getElementsByTagName("operator")
            .item(0);
        String motCleNom = motCleElem.getChildNodes().item(0).getNodeValue();
        SimpleOperator motCle = SimpleOperator
            .shortcut(StringEscapeUtils.unescapeXml(motCleNom));
        // la valeur
        Element valElem = (Element) condElem.getElementsByTagName("value")
            .item(0);
        String valeurS = valElem.getChildNodes().item(0).getNodeValue();
        Element typeElem = (Element) condElem.getElementsByTagName("value-type")
            .item(0);
        CharacterValueType type = CharacterValueType
            .valueOf(typeElem.getChildNodes().item(0).getNodeValue());
        Object valeur = valeurS;
        if (type.equals(CharacterValueType.INT)) {
          valeur = Integer.valueOf(valeurS);
        } else if (type.equals(CharacterValueType.REAL)) {
          valeur = Double.valueOf(valeurS);
        } else if (type.equals(CharacterValueType.BOOLEAN)) {
          valeur = Boolean.valueOf(valeurS);
        }
        // on crée la condition
        ORPremise condition = new ORPremise(concept, caractere, valeur, type,
            unite, motCle);
        premisse.add(condition);
      }
      // on récupère la restriction d'espace s'il y en a une
      HashMap<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
      if (regleElem.getElementsByTagName("space-restriction")
          .getLength() != 0) {
        Element restElem = (Element) regleElem
            .getElementsByTagName("space-restriction").item(0);
        for (int j = 0; j < restElem.getElementsByTagName("geo-space")
            .getLength(); j++) {
          Element espElem = (Element) restElem.getElementsByTagName("geo-space")
              .item(j);
          String nomEsp = espElem.getAttribute("name");
          GeoSpaceConcept esp = (GeoSpaceConcept) GeneralisationConcept
              .getElemGeoFromName(nomEsp, this.concepts);
          Double cle = Double
              .valueOf(espElem.getChildNodes().item(0).getNodeValue());
          restriction.put(esp, cle);
        }
      }
      // on construit la règle
      OperationRule rule = new OperationRule(nomRegle, importance, this,
          conclusion, premisse, restriction);
      this.rules.add(rule);
    }
    OperationRulesDatabase.currentDbs.add(this);
  }

  // Getters and setters //
  public Set<OperationRule> getRules() {
    return this.rules;
  }

  public void setRules(Set<OperationRule> reglesOper) {
    this.rules = reglesOper;
  }

  public HashSet<GeneralisationConcept> getConcepts() {
    return this.concepts;
  }

  public void setConcepts(HashSet<GeneralisationConcept> concepts) {
    this.concepts = concepts;
  }

  public String getUriConcepts() {
    return this.uriConcepts;
  }

  public void setUriConcepts(String uriConcepts) {
    this.uriConcepts = uriConcepts;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  // Other public methods //
  /**
   * Delete a rule in the database.
   * @param rule : a rule to be deleted
   */
  public void deleteRule(OperationRule rule) {
    this.rules.remove(rule);
    rule.clear();
  }

  /**
   * Renvoie les concepts géographiques sur lesquels portent une ou plusieurs
   * règles opérationnelles de la base.
   * @return
   */
  public HashSet<GeographicConcept> getRulesConcepts() {
    HashSet<GeographicConcept> conceptsRules = new HashSet<GeographicConcept>();
    for (OperationRule r : this.rules) {
      for (ORPremise p : r.getPremises()) {
        GeneralisationConcept elem = p.getConcept();
        if (!GeographicConcept.class.isInstance(elem)) {
          continue;
        }
        conceptsRules.add((GeographicConcept) elem);
      }
    }
    return conceptsRules;
  }

  /**
   * Save the operation rule database into XML file
   * 
   * @param fic
   * @throws GothicException
   * @author GTouya
   * @throws TransformerException
   * @throws IOException
   */
  public void saveToXml(File fic) throws TransformerException, IOException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("operation-rules-database");

    // LES INFOS GENERALES
    Element nomBaseElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.getName());
    nomBaseElem.appendChild(n);
    root.appendChild(nomBaseElem);
    Element ontoElem = xmlDoc.createElement("ontology");
    n = xmlDoc.createTextNode(this.uriConcepts);
    ontoElem.appendChild(n);
    root.appendChild(ontoElem);

    // ********************************************
    // REGLES OPERATIONNELLES
    for (OperationRule r : this.rules) {
      Element regleElem = xmlDoc.createElement("operation-rule");
      Element nomElem = xmlDoc.createElement("name");
      n = xmlDoc.createTextNode(r.getName());
      nomElem.appendChild(n);
      regleElem.appendChild(nomElem);
      Element impElem = xmlDoc.createElement("importance");
      n = xmlDoc.createTextNode(String.valueOf(r.getImportance()));
      impElem.appendChild(n);
      regleElem.appendChild(impElem);
      // on stocke la prémisse
      Element premElem = xmlDoc.createElement("premises");
      regleElem.appendChild(premElem);
      for (ORPremise p : r.getPremises()) {
        Element condElem = xmlDoc.createElement("o-rule-premise");
        Element concRElem = xmlDoc.createElement("concept");
        n = xmlDoc.createTextNode(p.getConcept().getName());
        concRElem.appendChild(n);
        condElem.appendChild(concRElem);
        Element caracRElem = xmlDoc.createElement("character");
        n = xmlDoc.createTextNode(p.getCharacter().getName());
        caracRElem.appendChild(n);
        condElem.appendChild(caracRElem);
        Element valElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(p.getValue().toString());
        valElem.appendChild(n);
        condElem.appendChild(valElem);
        Element typeElem = xmlDoc.createElement("value-type");
        n = xmlDoc.createTextNode(p.getValueType().name());
        typeElem.appendChild(n);
        condElem.appendChild(typeElem);
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(p.getUnit().name());
        uniteElem.appendChild(n);
        condElem.appendChild(uniteElem);
        Element opeElem = xmlDoc.createElement("operator");
        n = xmlDoc.createTextNode(
            StringEscapeUtils.escapeXml(p.getOperator().toShortcut()));
        opeElem.appendChild(n);
        condElem.appendChild(opeElem);
        premElem.appendChild(condElem);
      }
      // on stocke la conclusion
      Element conclElem = xmlDoc.createElement("conclusion");
      regleElem.appendChild(conclElem);
      Element nomOperElem = xmlDoc.createElement("action");
      n = xmlDoc.createTextNode(r.getConclusion().getAction().getName());
      nomOperElem.appendChild(n);
      conclElem.appendChild(nomOperElem);
      Element conseilElem = xmlDoc.createElement("advice");
      n = xmlDoc.createTextNode(String.valueOf(r.getConclusion().isAdvice()));
      conseilElem.appendChild(n);
      conclElem.appendChild(conseilElem);
      Element positiveElem = xmlDoc.createElement("positive");
      n = xmlDoc.createTextNode(String.valueOf(r.getConclusion().isPositive()));
      positiveElem.appendChild(n);
      conclElem.appendChild(positiveElem);
      // on stocke la restriction d'espaces
      if (r.getRestriction().size() > 0) {
        Element restrElem = xmlDoc.createElement("space-restriction");
        for (GeoSpaceConcept es : r.getRestriction().keySet()) {
          Element espElem = xmlDoc.createElement("geo-space");
          espElem.setAttribute("name", es.getName());
          n = xmlDoc.createTextNode(String.valueOf(r.getRestriction().get(es)));
          espElem.appendChild(n);
          restrElem.appendChild(espElem);
        }
        regleElem.appendChild(restrElem);
      }
      root.appendChild(regleElem);
    }

    // ECRITURE DU FICHIER
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, fic);
  }

  /**
   * Renvoie les règles opérationnelles de la base de règle dont la prémisse
   * contient une condition qui porte sur le concept qui a pour nom celui passé
   * en entrée de la méthode.
   * 
   * @param conceptName : le nom de la règle que l'on veut obtenir.
   * @return
   */
  public HashSet<OperationRule> getRulesFromPremiseConcept(String conceptName) {
    HashSet<OperationRule> regles = new HashSet<OperationRule>();
    for (OperationRule regle : this.rules) {
      for (ORPremise prem : regle.getPremises()) {
        if (prem.getConcept().getName().equals(conceptName)) {
          regles.add(regle);
          break;
        }
      }
    }

    return regles;
  }

  /**
   * Renvoie les règles opérationnelles de la base de règle dont la conclusion
   * porte sur le concept a pour nom celui passé en entrée de la méthode.
   * 
   * @param actionName : le nom de l'action de généralisation pour lequel on
   *          cherche des règles.
   * @return
   */
  public HashSet<OperationRule> getReglesFromConclusionConcept(
      String actionName) {
    HashSet<OperationRule> rulesOut = new HashSet<OperationRule>();
    for (OperationRule rule : this.rules) {
      if (rule.getConclusion().getAction().getName().equals(actionName)) {
        rulesOut.add(rule);
      }
    }
    return rulesOut;
  }

  /**
   * Renvoie la règle opérationnelle de la base de règle dont le nom est celui
   * passé en entrée de la méthode.
   * 
   * @param ruleName : le nom de la règle que l'on veut obtenir.
   * @return
   */
  public OperationRule getRuleFromName(String ruleName) {
    for (OperationRule regle : this.rules) {
      if (regle.getName().equals(ruleName)) {
        return regle;
      }
    }
    return null;
  }

  /**
   * Clears the database by removing all rules.
   */
  public void clear() {
    this.getRules().clear();
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
