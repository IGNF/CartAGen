package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.SelectionCriterion.Request;
import fr.ign.cogit.geoxygene.util.XMLUtil;

@Entity
public class ConstraintDatabase {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static HashSet<ConstraintDatabase> currentDbs = new HashSet<ConstraintDatabase>();
  private static HashSet<String> currentDbNames = new HashSet<String>();
  private Logger logger = Logger.getLogger(ConstraintDatabase.class.getName());
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private String name;
  private HashSet<FormalMicroConstraint> microConstraints;
  private HashSet<FormalMesoConstraint> mesoConstraints;
  private HashSet<FormalMacroConstraint> macroConstraints;
  private HashSet<FormalRelationalConstraint> relationalConstraints;
  private HashSet<GeneralisationConcept> concepts;
  private String uriConcepts;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  public static ConstraintDatabase getInstance(String name) {
    for (ConstraintDatabase db : ConstraintDatabase.currentDbs) {
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
  /**
   * Constructeur à partir d'une base de données Gothic.
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws OWLOntologyCreationException
   * @throws VersionsIncompatiblesException
   * @throws OWLOntologyCreationException
   * 
   */
  public ConstraintDatabase(File fic) throws ParserConfigurationException,
      SAXException, IOException, OWLOntologyCreationException {
    this.microConstraints = new HashSet<FormalMicroConstraint>();
    this.mesoConstraints = new HashSet<FormalMesoConstraint>();
    this.macroConstraints = new HashSet<FormalMacroConstraint>();
    this.relationalConstraints = new HashSet<FormalRelationalConstraint>();
    // on ouvre le fichier xml
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();
    // on parcours le document XML
    Element root = (Element) doc.getElementsByTagName("constraints-database")
        .item(0);

    // on récupère le nom
    Element nomBdElem = (Element) root.getElementsByTagName("name").item(0);
    this.name = nomBdElem.getChildNodes().item(0).getNodeValue();
    if (ConstraintDatabase.currentDbNames.contains(this.name)) {
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

    // on charge (en java puis gothic) les contraintes micros
    Element microElem = (Element) root.getElementsByTagName("micro-constraints")
        .item(0);
    for (int i = 0; i < microElem.getElementsByTagName("constraint")
        .getLength(); i++) {
      Element contrElem = (Element) microElem.getElementsByTagName("constraint")
          .item(i);
      Element nomElem = (Element) contrElem.getElementsByTagName("name")
          .item(0);
      String nomContr = nomElem.getChildNodes().item(0).getNodeValue();
      Element impElem = (Element) contrElem.getElementsByTagName("importance")
          .item(0);
      int importance = Integer
          .valueOf(impElem.getChildNodes().item(0).getNodeValue());
      // on récupère nom et importance
      Element concElem = (Element) contrElem.getElementsByTagName("concept")
          .item(0);
      String nomConc = concElem.getChildNodes().item(0).getNodeValue();
      GeneralisationConcept concept = GeneralisationConcept
          .getElemGeoFromName(nomConc, this.concepts);
      Element carElem = (Element) contrElem.getElementsByTagName("character")
          .item(0);
      String nomCar = carElem.getChildNodes().item(0).getNodeValue();
      Character caractere = concept.getCaracFromNom(nomCar);
      // on récupère le type d'expression
      Element exprElem = (Element) contrElem
          .getElementsByTagName("expression-type").item(0);
      ExpressionType typeExpr = ExpressionType.construire(exprElem);
      // on récupère le critère de sélection
      SelectionCriterion critereSel = new SelectionCriterion(null,
          new HashSet<Request>());
      if (contrElem.getElementsByTagName("selection-criterion")
          .getLength() != 0) {
        Element critElem = (Element) contrElem
            .getElementsByTagName("selection-criterion").item(0);
        critereSel = SelectionCriterion.buildCriterion(critElem, this.concepts);
      }
      // on récupère la restriction d'espace s'il y en a une
      HashMap<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
      if (contrElem.getElementsByTagName("space-restriction")
          .getLength() != 0) {
        Element restElem = (Element) contrElem
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
      // on construit la contrainte
      FormalMicroConstraint contr = new FormalMicroConstraint(typeExpr,
          importance, critereSel, concept, caractere, nomContr, this,
          restriction);
      typeExpr.setConstraint(contr);
      if (critereSel != null) {
        critereSel.setConstraint(contr);
      }
      this.microConstraints.add(contr);
    }

    // on charge (en java puis gothic) les contraintes mesos
    Element mesoElem = (Element) root.getElementsByTagName("meso-constraints")
        .item(0);
    for (int i = 0; i < mesoElem.getElementsByTagName("constraint")
        .getLength(); i++) {
      Element contrElem = (Element) mesoElem.getElementsByTagName("constraint")
          .item(i);
      Element nomElem = (Element) contrElem.getElementsByTagName("name")
          .item(0);
      String nomContr = nomElem.getChildNodes().item(0).getNodeValue();
      Element impElem = (Element) contrElem.getElementsByTagName("importance")
          .item(0);
      int importance = Integer
          .valueOf(impElem.getChildNodes().item(0).getNodeValue());
      // on récupère nom et importance
      Element concElem = (Element) contrElem.getElementsByTagName("concept")
          .item(0);
      String nomConc = concElem.getChildNodes().item(0).getNodeValue();
      GeneralisationConcept concept = GeneralisationConcept
          .getElemGeoFromName(nomConc, this.concepts);
      Element carElem = (Element) contrElem.getElementsByTagName("character")
          .item(0);
      String nomCar = carElem.getChildNodes().item(0).getNodeValue();
      Character caractere = concept.getCaracFromNom(nomCar);
      // on récupère le type d'expression
      Element exprElem = (Element) contrElem
          .getElementsByTagName("expression-type").item(0);
      ExpressionType typeExpr = ExpressionType.construire(exprElem);
      // on récupère le critère de sélection
      SelectionCriterion critereSel = new SelectionCriterion(null,
          new HashSet<Request>());
      if (contrElem.getElementsByTagName("selection-criterion")
          .getLength() != 0) {
        Element critElem = (Element) contrElem
            .getElementsByTagName("selection-criterion").item(0);
        critereSel = SelectionCriterion.buildCriterion(critElem, this.concepts);
      }
      // on récupère la restriction d'espace s'il y en a une
      HashMap<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
      if (contrElem.getElementsByTagName("space-restriction")
          .getLength() != 0) {
        Element restElem = (Element) contrElem
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
      FormalMesoConstraint contr = new FormalMesoConstraint(typeExpr,
          importance, critereSel, concept, caractere, nomContr, this,
          restriction);
      typeExpr.setConstraint(contr);
      if (critereSel != null) {
        critereSel.setConstraint(contr);
      }
      this.mesoConstraints.add(contr);
    }

    // on charge (en java puis gothic) les contraintes macros
    Element macroElem = (Element) root.getElementsByTagName("macro-constraints")
        .item(0);
    for (int i = 0; i < macroElem.getElementsByTagName("constraint")
        .getLength(); i++) {
      Element contrElem = (Element) macroElem.getElementsByTagName("constraint")
          .item(i);
      Element nomElem = (Element) contrElem.getElementsByTagName("name")
          .item(0);
      String nomContr = nomElem.getChildNodes().item(0).getNodeValue();
      Element impElem = (Element) contrElem.getElementsByTagName("importance")
          .item(0);
      int importance = Integer
          .valueOf(impElem.getChildNodes().item(0).getNodeValue());
      // on récupère nom et importance
      Element concElem = (Element) contrElem.getElementsByTagName("concept")
          .item(0);
      String nomConc = concElem.getChildNodes().item(0).getNodeValue();
      GeneralisationConcept concept = GeneralisationConcept
          .getElemGeoFromName(nomConc, this.concepts);
      Element carElem = (Element) contrElem.getElementsByTagName("character")
          .item(0);
      String nomCar = carElem.getChildNodes().item(0).getNodeValue();
      Character caractere = concept.getCaracFromNom(nomCar);
      // on récupère le type d'expression
      Element exprElem = (Element) contrElem
          .getElementsByTagName("expression-type").item(0);
      ExpressionType typeExpr = ExpressionType.construire(exprElem);
      // on récupère le critère de sélection
      SelectionCriterion critereSel = new SelectionCriterion(null,
          new HashSet<Request>());
      if (contrElem.getElementsByTagName("selection-criterion")
          .getLength() != 0) {
        Element critElem = (Element) contrElem
            .getElementsByTagName("selection-criterion").item(0);
        critereSel = SelectionCriterion.buildCriterion(critElem, this.concepts);
      }
      // on récupère la restriction d'espace s'il y en a une
      HashMap<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
      if (contrElem.getElementsByTagName("space-restriction")
          .getLength() != 0) {
        Element restElem = (Element) contrElem
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
      FormalMacroConstraint contr = new FormalMacroConstraint(typeExpr,
          importance, critereSel, concept, caractere, nomContr, this,
          restriction);
      typeExpr.setConstraint(contr);
      if (critereSel != null) {
        critereSel.setConstraint(contr);
      }
      this.macroConstraints.add(contr);
    }

    // on charge (en java puis gothic) les contraintes rels
    Element relElem = (Element) root
        .getElementsByTagName("relational-constraints").item(0);
    for (int i = 0; i < relElem.getElementsByTagName("constraint")
        .getLength(); i++) {
      Element contrElem = (Element) relElem.getElementsByTagName("constraint")
          .item(i);
      Element nomElem = (Element) contrElem.getElementsByTagName("name")
          .item(0);
      String nomContr = nomElem.getChildNodes().item(0).getNodeValue();
      Element impElem = (Element) contrElem.getElementsByTagName("importance")
          .item(0);
      int importance = Integer
          .valueOf(impElem.getChildNodes().item(0).getNodeValue());
      // on récupère nom et importance
      Element concElem = (Element) contrElem.getElementsByTagName("concept")
          .item(0);
      String nomConc = concElem.getChildNodes().item(0).getNodeValue();
      GeographicRelation concept = (GeographicRelation) GeneralisationConcept
          .getElemGeoFromName(nomConc, this.concepts);
      Element carElem = (Element) contrElem.getElementsByTagName("character")
          .item(0);
      String nomCar = carElem.getChildNodes().item(0).getNodeValue();
      Character caractere = concept.getCaracFromNom(nomCar);
      Element conc1Elem = (Element) contrElem.getElementsByTagName("concept1")
          .item(0);
      String nomConc1 = conc1Elem.getChildNodes().item(0).getNodeValue();
      Element conc2Elem = (Element) contrElem.getElementsByTagName("concept2")
          .item(0);
      String nomConc2 = conc2Elem.getChildNodes().item(0).getNodeValue();
      GeographicConcept concept1 = (GeographicConcept) GeneralisationConcept
          .getElemGeoFromName(nomConc1, this.concepts);
      GeographicConcept concept2 = (GeographicConcept) GeneralisationConcept
          .getElemGeoFromName(nomConc2, this.concepts);
      // on récupère le type d'expression
      Element exprElem = (Element) contrElem
          .getElementsByTagName("expression-type").item(0);
      ExpressionType typeExpr = ExpressionType.construire(exprElem);
      // on récupère le critère de sélection
      SelectionCriterion critereSel = new SelectionCriterion(null,
          new HashSet<Request>());
      if (contrElem.getElementsByTagName("selection-criterion")
          .getLength() != 0) {
        Element critElem = (Element) contrElem
            .getElementsByTagName("selection-criterion").item(0);
        critereSel = SelectionCriterion.buildCriterion(critElem, this.concepts);
      }
      // on récupère la restriction d'espace s'il y en a une
      HashMap<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
      if (contrElem.getElementsByTagName("space-restriction")
          .getLength() != 0) {
        Element restElem = (Element) contrElem
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
      FormalRelationalConstraint contr = new FormalRelationalConstraint(
          typeExpr, importance, critereSel, concept, caractere, nomContr, this,
          concept1, concept2, restriction);
      typeExpr.setConstraint(contr);
      if (critereSel != null) {
        critereSel.setConstraint(contr);
      }
      this.relationalConstraints.add(contr);
    }
    ConstraintDatabase.currentDbs.add(this);
    ConstraintDatabase.currentDbNames.add(this.name);
  }

  public ConstraintDatabase(String name,
      HashSet<GeneralisationConcept> ontology, String uri) {
    this.microConstraints = new HashSet<FormalMicroConstraint>();
    this.mesoConstraints = new HashSet<FormalMesoConstraint>();
    this.macroConstraints = new HashSet<FormalMacroConstraint>();
    this.relationalConstraints = new HashSet<FormalRelationalConstraint>();
    this.name = name;
    this.concepts = ontology;
    this.uriConcepts = uri;
    ConstraintDatabase.currentDbs.add(this);
    ConstraintDatabase.currentDbNames.add(name);
  }

  // Getters and setters //
  public HashSet<FormalMicroConstraint> getMicroConstraints() {
    return this.microConstraints;
  }

  public void setMicroConstraints(HashSet<FormalMicroConstraint> contrsMicro) {
    this.microConstraints = contrsMicro;
  }

  public HashSet<FormalMesoConstraint> getMesoConstraints() {
    return this.mesoConstraints;
  }

  public void setMesoConstraints(HashSet<FormalMesoConstraint> contrsMeso) {
    this.mesoConstraints = contrsMeso;
  }

  public HashSet<FormalMacroConstraint> getMacroConstraints() {
    return this.macroConstraints;
  }

  public void setMacroConstraints(HashSet<FormalMacroConstraint> contrsMacro) {
    this.macroConstraints = contrsMacro;
  }

  public HashSet<FormalRelationalConstraint> getRelationalConstraints() {
    return this.relationalConstraints;
  }

  public void setRelationalConstraints(
      HashSet<FormalRelationalConstraint> contrsRel) {
    this.relationalConstraints = contrsRel;
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
   * Renvoie l'ensemble des concepts de l'ontologie contraints par une ou
   * plusieurs des contraintes de la base.
   * @return le set des ConceptGeographique contraints.
   */
  public HashSet<GeographicConcept> getConstrainedConcepts() {
    HashSet<GeographicConcept> contraints = new HashSet<GeographicConcept>();
    for (FormalGenConstraint c : this.microConstraints) {
      contraints.add((GeographicConcept) c.getConcept());
    }
    for (FormalGenConstraint c : this.macroConstraints) {
      contraints.add((GeographicConcept) c.getConcept());
    }
    for (FormalGenConstraint c : this.mesoConstraints) {
      contraints.add((GeographicConcept) c.getConcept());
    }
    for (FormalGenConstraint c : this.relationalConstraints) {
      contraints.add(((FormalRelationalConstraint) c).getConcept1());
      contraints.add(((FormalRelationalConstraint) c).getConcept2());
    }

    return contraints;
  }

  /**
   * Save the content of the constraints db into XML file.
   * @param fic
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
    Element root = xmlDoc.createElement("constraints-database");

    // LES INFOS GENERALES
    Element nomBaseElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.getName());
    nomBaseElem.appendChild(n);
    root.appendChild(nomBaseElem);
    Element ontoElem = xmlDoc.createElement("ontology");
    n = xmlDoc.createTextNode(this.uriConcepts);
    ontoElem.appendChild(n);
    root.appendChild(ontoElem);

    // ON STOCKE LES CONTRAINTES MICROS
    Element microElem = xmlDoc.createElement("micro-constraints");
    root.appendChild(microElem);
    for (FormalMicroConstraint c : this.microConstraints) {
      Element contrElem = xmlDoc.createElement("constraint");
      Element nomElem = xmlDoc.createElement("name");
      n = xmlDoc.createTextNode(c.getName());
      nomElem.appendChild(n);
      contrElem.appendChild(nomElem);
      Element impElem = xmlDoc.createElement("importance");
      n = xmlDoc.createTextNode(String.valueOf(c.getImportance()));
      impElem.appendChild(n);
      contrElem.appendChild(impElem);
      Element concElem = xmlDoc.createElement("concept");
      n = xmlDoc.createTextNode(c.getConcept().getName());
      concElem.appendChild(n);
      contrElem.appendChild(concElem);
      Element caracElem = xmlDoc.createElement("character");
      n = xmlDoc.createTextNode(c.getCharacter().getName());
      caracElem.appendChild(n);
      contrElem.appendChild(caracElem);
      // on stocke le type d'expression
      Element exprElem = xmlDoc.createElement("expression-type");
      ExpressionType expr = c.getExprType();
      Element classeElem = xmlDoc.createElement("expression-class");
      n = xmlDoc.createTextNode(expr.getClass().getSimpleName());
      classeElem.appendChild(n);
      exprElem.appendChild(classeElem);
      Element motElem = xmlDoc.createElement("keyword");
      n = xmlDoc.createTextNode(
          StringEscapeUtils.escapeXml(expr.getKeyWord().toShortcut()));
      motElem.appendChild(n);
      exprElem.appendChild(motElem);
      if (expr.getClass().equals(ThreshExpressionType.class)
          || expr.getClass().equals(ControlExpressionType.class)) {
        Element valElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(expr.getValue().toString());
        valElem.appendChild(n);
        exprElem.appendChild(valElem);
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(expr.getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
      } else if (expr.getClass().equals(MarginExpressionType.class)) {
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(
            ((MarginExpressionType) expr).getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
        Element plusElem = xmlDoc.createElement("margin");
        n = xmlDoc.createTextNode(
            String.valueOf(((MarginExpressionType) expr).getMargin()));
        plusElem.appendChild(n);
        exprElem.appendChild(plusElem);
      }
      contrElem.appendChild(exprElem);
      // on stocke le critère de sélection
      if (c.getSelectionCrit() != null) {
        SelectionCriterion crit = c.getSelectionCrit();
        Element critElem = xmlDoc.createElement("selection-criterion");
        for (Request r : crit.getRequests()) {
          Element reqElem = xmlDoc.createElement("request");
          Element concRElem = xmlDoc.createElement("concept");
          n = xmlDoc.createTextNode(r.getConcept().getName());
          concRElem.appendChild(n);
          reqElem.appendChild(concRElem);
          Element caracRElem = xmlDoc.createElement("character");
          n = xmlDoc.createTextNode(r.getCharacter().getName());
          caracRElem.appendChild(n);
          reqElem.appendChild(caracRElem);
          Element valElem = xmlDoc.createElement("value");
          n = xmlDoc.createTextNode(r.getValue().toString());
          valElem.appendChild(n);
          reqElem.appendChild(valElem);
          Element typeElem = xmlDoc.createElement("value-type");
          n = xmlDoc.createTextNode(r.getValueType().name());
          typeElem.appendChild(n);
          reqElem.appendChild(typeElem);
          Element tolElem = xmlDoc.createElement("tolerance");
          n = xmlDoc.createTextNode(String.valueOf(r.getTolerance()));
          tolElem.appendChild(n);
          reqElem.appendChild(tolElem);
          Element uniteElem = xmlDoc.createElement("unit");
          n = xmlDoc.createTextNode(r.getUnit().name());
          uniteElem.appendChild(n);
          reqElem.appendChild(uniteElem);
          Element opeElem = xmlDoc.createElement("operator");
          n = xmlDoc.createTextNode(
              StringEscapeUtils.escapeXml(r.getOperator().toShortcut()));
          opeElem.appendChild(n);
          reqElem.appendChild(opeElem);
          critElem.appendChild(reqElem);
        }
        contrElem.appendChild(critElem);
      }
      // on stocke la restriction d'espaces
      if (c.getRestriction().size() > 0) {
        Element restrElem = xmlDoc.createElement("space-restriction");
        for (GeoSpaceConcept es : c.getRestriction().keySet()) {
          Element espElem = xmlDoc.createElement("geo-space");
          espElem.setAttribute("name", es.getName());
          n = xmlDoc.createTextNode(String.valueOf(c.getRestriction().get(es)));
          espElem.appendChild(n);
          restrElem.appendChild(espElem);
        }
        contrElem.appendChild(restrElem);
      }
      microElem.appendChild(contrElem);
    }

    // ON STOCKE LES CONTRAINTES MESOS
    Element mesoElem = xmlDoc.createElement("meso-constraints");
    root.appendChild(mesoElem);
    for (FormalMesoConstraint c : this.mesoConstraints) {
      Element contrElem = xmlDoc.createElement("constraint");
      Element nomElem = xmlDoc.createElement("name");
      n = xmlDoc.createTextNode(c.getName());
      nomElem.appendChild(n);
      contrElem.appendChild(nomElem);
      Element impElem = xmlDoc.createElement("importance");
      n = xmlDoc.createTextNode(String.valueOf(c.getImportance()));
      impElem.appendChild(n);
      contrElem.appendChild(impElem);
      Element concElem = xmlDoc.createElement("concept");
      n = xmlDoc.createTextNode(c.getConcept().getName());
      concElem.appendChild(n);
      contrElem.appendChild(concElem);
      Element caracElem = xmlDoc.createElement("character");
      n = xmlDoc.createTextNode(c.getCharacter().getName());
      caracElem.appendChild(n);
      contrElem.appendChild(caracElem);
      // on stocke le type d'expression
      Element exprElem = xmlDoc.createElement("expression-type");
      ExpressionType expr = c.getExprType();
      Element classeElem = xmlDoc.createElement("expression-class");
      n = xmlDoc.createTextNode(expr.getClass().getSimpleName());
      classeElem.appendChild(n);
      exprElem.appendChild(classeElem);
      Element motElem = xmlDoc.createElement("keyword");
      n = xmlDoc.createTextNode(
          StringEscapeUtils.escapeXml(expr.getKeyWord().toShortcut()));
      motElem.appendChild(n);
      exprElem.appendChild(motElem);
      if (expr.getClass().equals(ThreshExpressionType.class)
          || expr.getClass().equals(ControlExpressionType.class)) {
        Element valElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(expr.getValue().toString());
        valElem.appendChild(n);
        exprElem.appendChild(valElem);
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(expr.getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
      } else if (expr.getClass().equals(MarginExpressionType.class)) {
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(
            ((MarginExpressionType) expr).getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
        Element plusElem = xmlDoc.createElement("margin");
        n = xmlDoc.createTextNode(
            String.valueOf(((MarginExpressionType) expr).getMargin()));
        plusElem.appendChild(n);
        exprElem.appendChild(plusElem);
      }
      contrElem.appendChild(exprElem);
      // on stocke le critère de sélection
      if (c.getSelectionCrit() != null) {
        SelectionCriterion crit = c.getSelectionCrit();
        Element critElem = xmlDoc.createElement("selection-criterion");
        for (Request r : crit.getRequests()) {
          Element reqElem = xmlDoc.createElement("request");
          Element concRElem = xmlDoc.createElement("concept");
          n = xmlDoc.createTextNode(r.getConcept().getName());
          concRElem.appendChild(n);
          reqElem.appendChild(concRElem);
          Element caracRElem = xmlDoc.createElement("character");
          n = xmlDoc.createTextNode(r.getCharacter().getName());
          caracRElem.appendChild(n);
          reqElem.appendChild(caracRElem);
          Element valElem = xmlDoc.createElement("value");
          n = xmlDoc.createTextNode(r.getValue().toString());
          valElem.appendChild(n);
          reqElem.appendChild(valElem);
          Element typeElem = xmlDoc.createElement("value-type");
          n = xmlDoc.createTextNode(r.getValueType().name());
          typeElem.appendChild(n);
          reqElem.appendChild(typeElem);
          Element tolElem = xmlDoc.createElement("tolerance");
          n = xmlDoc.createTextNode(String.valueOf(r.getTolerance()));
          tolElem.appendChild(n);
          reqElem.appendChild(tolElem);
          Element uniteElem = xmlDoc.createElement("unit");
          n = xmlDoc.createTextNode(r.getUnit().name());
          uniteElem.appendChild(n);
          reqElem.appendChild(uniteElem);
          Element opeElem = xmlDoc.createElement("operator");
          n = xmlDoc.createTextNode(
              StringEscapeUtils.escapeXml(r.getOperator().toShortcut()));
          opeElem.appendChild(n);
          reqElem.appendChild(opeElem);
          critElem.appendChild(reqElem);
        }
        contrElem.appendChild(critElem);
      }
      // on stocke la restriction d'espaces
      if (c.getRestriction().size() > 0) {
        Element restrElem = xmlDoc.createElement("space-restriction");
        for (GeoSpaceConcept es : c.getRestriction().keySet()) {
          Element espElem = xmlDoc.createElement("geo-space");
          espElem.setAttribute("name", es.getName());
          n = xmlDoc.createTextNode(String.valueOf(c.getRestriction().get(es)));
          espElem.appendChild(n);
          restrElem.appendChild(espElem);
        }
        contrElem.appendChild(restrElem);
      }
      mesoElem.appendChild(contrElem);
    }

    // ON STOCKE LES CONTRAINTES MACROS
    Element macroElem = xmlDoc.createElement("macro-constraints");
    root.appendChild(macroElem);
    for (FormalMacroConstraint c : this.macroConstraints) {
      Element contrElem = xmlDoc.createElement("constraint");
      Element nomElem = xmlDoc.createElement("name");
      n = xmlDoc.createTextNode(c.getName());
      nomElem.appendChild(n);
      contrElem.appendChild(nomElem);
      Element impElem = xmlDoc.createElement("importance");
      n = xmlDoc.createTextNode(String.valueOf(c.getImportance()));
      impElem.appendChild(n);
      contrElem.appendChild(impElem);
      Element concElem = xmlDoc.createElement("concept");
      n = xmlDoc.createTextNode(c.getConcept().getName());
      concElem.appendChild(n);
      contrElem.appendChild(concElem);
      Element caracElem = xmlDoc.createElement("character");
      n = xmlDoc.createTextNode(c.getCharacter().getName());
      caracElem.appendChild(n);
      contrElem.appendChild(caracElem);
      // on stocke le type d'expression
      Element exprElem = xmlDoc.createElement("expression-type");
      ExpressionType expr = c.getExprType();
      Element classeElem = xmlDoc.createElement("expression-class");
      n = xmlDoc.createTextNode(expr.getClass().getSimpleName());
      classeElem.appendChild(n);
      exprElem.appendChild(classeElem);
      Element motElem = xmlDoc.createElement("keyword");
      n = xmlDoc.createTextNode(
          StringEscapeUtils.escapeXml(expr.getKeyWord().toShortcut()));
      motElem.appendChild(n);
      exprElem.appendChild(motElem);
      if (expr.getClass().equals(ThreshExpressionType.class)
          || expr.getClass().equals(ControlExpressionType.class)) {
        Element valElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(expr.getValue().toString());
        valElem.appendChild(n);
        exprElem.appendChild(valElem);
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(expr.getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
      } else if (expr.getClass().equals(MarginExpressionType.class)) {
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(
            ((MarginExpressionType) expr).getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
        Element plusElem = xmlDoc.createElement("margin");
        n = xmlDoc.createTextNode(
            String.valueOf(((MarginExpressionType) expr).getMargin()));
        plusElem.appendChild(n);
        exprElem.appendChild(plusElem);
      } else if (expr.getClass().equals(ReductionExpressionType.class)) {
        Element plusElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(
            String.valueOf(((ReductionExpressionType) expr).getValue()));
        plusElem.appendChild(n);
        exprElem.appendChild(plusElem);
      }
      contrElem.appendChild(exprElem);
      // on stocke le critère de sélection
      if (c.getSelectionCrit() != null) {
        SelectionCriterion crit = c.getSelectionCrit();
        Element critElem = xmlDoc.createElement("selection-criterion");
        for (Request r : crit.getRequests()) {
          Element reqElem = xmlDoc.createElement("request");
          Element concRElem = xmlDoc.createElement("concept");
          n = xmlDoc.createTextNode(r.getConcept().getName());
          concRElem.appendChild(n);
          reqElem.appendChild(concRElem);
          Element caracRElem = xmlDoc.createElement("character");
          n = xmlDoc.createTextNode(r.getCharacter().getName());
          caracRElem.appendChild(n);
          reqElem.appendChild(caracRElem);
          Element valElem = xmlDoc.createElement("value");
          n = xmlDoc.createTextNode(r.getValue().toString());
          valElem.appendChild(n);
          reqElem.appendChild(valElem);
          Element typeElem = xmlDoc.createElement("value-type");
          n = xmlDoc.createTextNode(r.getValueType().name());
          typeElem.appendChild(n);
          reqElem.appendChild(typeElem);
          Element tolElem = xmlDoc.createElement("tolerance");
          n = xmlDoc.createTextNode(String.valueOf(r.getTolerance()));
          tolElem.appendChild(n);
          reqElem.appendChild(tolElem);
          Element uniteElem = xmlDoc.createElement("unit");
          n = xmlDoc.createTextNode(r.getUnit().name());
          uniteElem.appendChild(n);
          reqElem.appendChild(uniteElem);
          Element opeElem = xmlDoc.createElement("operator");
          n = xmlDoc.createTextNode(
              StringEscapeUtils.escapeXml(r.getOperator().toShortcut()));
          opeElem.appendChild(n);
          reqElem.appendChild(opeElem);
          critElem.appendChild(reqElem);
        }
        contrElem.appendChild(critElem);
      }
      // on stocke la restriction d'espaces
      if (c.getRestriction().size() > 0) {
        Element restrElem = xmlDoc.createElement("space-restriction");
        for (GeoSpaceConcept es : c.getRestriction().keySet()) {
          Element espElem = xmlDoc.createElement("geo-space");
          espElem.setAttribute("name", es.getName());
          n = xmlDoc.createTextNode(String.valueOf(c.getRestriction().get(es)));
          espElem.appendChild(n);
          restrElem.appendChild(espElem);
        }
        contrElem.appendChild(restrElem);
      }
      macroElem.appendChild(contrElem);
    }

    // ON STOCKE LES CONTRAINTES RELATIONNELLES
    Element relElem = xmlDoc.createElement("relational-constraints");
    root.appendChild(relElem);
    for (FormalRelationalConstraint c : this.relationalConstraints) {
      Element contrElem = xmlDoc.createElement("constraint");
      Element nomElem = xmlDoc.createElement("name");
      n = xmlDoc.createTextNode(c.getName());
      nomElem.appendChild(n);
      contrElem.appendChild(nomElem);
      Element impElem = xmlDoc.createElement("importance");
      n = xmlDoc.createTextNode(String.valueOf(c.getImportance()));
      impElem.appendChild(n);
      contrElem.appendChild(impElem);
      Element concElem = xmlDoc.createElement("concept");
      n = xmlDoc.createTextNode(c.getConcept().getName());
      concElem.appendChild(n);
      contrElem.appendChild(concElem);
      Element conc1Elem = xmlDoc.createElement("concept1");
      n = xmlDoc.createTextNode(c.getConcept1().getName());
      conc1Elem.appendChild(n);
      contrElem.appendChild(conc1Elem);
      Element conc2Elem = xmlDoc.createElement("concept2");
      n = xmlDoc.createTextNode(c.getConcept2().getName());
      conc2Elem.appendChild(n);
      contrElem.appendChild(conc2Elem);
      Element caracElem = xmlDoc.createElement("character");
      n = xmlDoc.createTextNode(c.getCharacter().getName());
      caracElem.appendChild(n);
      contrElem.appendChild(caracElem);
      // on stocke le type d'expression
      Element exprElem = xmlDoc.createElement("expression-type");
      ExpressionType expr = c.getExprType();
      Element classeElem = xmlDoc.createElement("expression-class");
      n = xmlDoc.createTextNode(expr.getClass().getSimpleName());
      classeElem.appendChild(n);
      exprElem.appendChild(classeElem);
      Element motElem = xmlDoc.createElement("keyword");
      n = xmlDoc.createTextNode(
          StringEscapeUtils.escapeXml(expr.getKeyWord().toShortcut()));
      motElem.appendChild(n);
      exprElem.appendChild(motElem);
      if (expr.getClass().equals(ThreshExpressionType.class)
          || expr.getClass().equals(ControlExpressionType.class)) {
        Element valElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(expr.getValue().toString());
        valElem.appendChild(n);
        exprElem.appendChild(valElem);
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(expr.getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
      } else if (expr.getClass().equals(MarginExpressionType.class)) {
        Element uniteElem = xmlDoc.createElement("unit");
        n = xmlDoc.createTextNode(
            ((MarginExpressionType) expr).getValueUnit().name());
        uniteElem.appendChild(n);
        exprElem.appendChild(uniteElem);
        Element plusElem = xmlDoc.createElement("margin");
        n = xmlDoc.createTextNode(
            String.valueOf(((MarginExpressionType) expr).getMargin()));
        plusElem.appendChild(n);
        exprElem.appendChild(plusElem);
      }
      contrElem.appendChild(exprElem);
      // on stocke le critère de sélection
      if (c.getSelectionCrit() != null) {
        SelectionCriterion crit = c.getSelectionCrit();
        Element critElem = xmlDoc.createElement("selection-criterion");
        for (Request r : crit.getRequests()) {
          Element reqElem = xmlDoc.createElement("request");
          Element concRElem = xmlDoc.createElement("concept");
          n = xmlDoc.createTextNode(r.getConcept().getName());
          concRElem.appendChild(n);
          reqElem.appendChild(concRElem);
          Element caracRElem = xmlDoc.createElement("character");
          n = xmlDoc.createTextNode(r.getCharacter().getName());
          caracRElem.appendChild(n);
          reqElem.appendChild(caracRElem);
          Element valElem = xmlDoc.createElement("value");
          n = xmlDoc.createTextNode(r.getValue().toString());
          valElem.appendChild(n);
          reqElem.appendChild(valElem);
          Element typeElem = xmlDoc.createElement("value-type");
          n = xmlDoc.createTextNode(r.getValueType().name());
          typeElem.appendChild(n);
          reqElem.appendChild(typeElem);
          Element tolElem = xmlDoc.createElement("tolerance");
          n = xmlDoc.createTextNode(String.valueOf(r.getTolerance()));
          tolElem.appendChild(n);
          reqElem.appendChild(tolElem);
          Element uniteElem = xmlDoc.createElement("unit");
          n = xmlDoc.createTextNode(r.getUnit().name());
          uniteElem.appendChild(n);
          reqElem.appendChild(uniteElem);
          Element opeElem = xmlDoc.createElement("operator");
          n = xmlDoc.createTextNode(
              StringEscapeUtils.escapeXml(r.getOperator().toShortcut()));
          opeElem.appendChild(n);
          reqElem.appendChild(opeElem);
          critElem.appendChild(reqElem);
        }
        contrElem.appendChild(critElem);
      }
      // on stocke la restriction d'espaces
      if (c.getRestriction().size() > 0) {
        Element restrElem = xmlDoc.createElement("space-restriction");
        for (GeoSpaceConcept es : c.getRestriction().keySet()) {
          Element espElem = xmlDoc.createElement("geo-space");
          espElem.setAttribute("name", es.getName());
          n = xmlDoc.createTextNode(String.valueOf(c.getRestriction().get(es)));
          espElem.appendChild(n);
          restrElem.appendChild(espElem);
        }
        contrElem.appendChild(restrElem);
      }
      relElem.appendChild(contrElem);
    }
    // ECRITURE DU FICHIER
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, fic);
  }

  /**
   * Donne les contrainte de généralisation de la base définies sur le caracte
   * "nom".
   * @param characName name of the character
   * @return
   */
  public HashSet<FormalGenConstraint> getConstraintsFromCharacter(
      String characName) {
    HashSet<FormalGenConstraint> contrs = new HashSet<FormalGenConstraint>();
    for (FormalGenConstraint contr : this.microConstraints) {
      if (contr.getCharacter().getName().equals(characName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.macroConstraints) {
      if (contr.getCharacter().getName().equals(characName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.mesoConstraints) {
      if (contr.getCharacter().getName().equals(characName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.relationalConstraints) {
      if (contr.getCharacter().getName().equals(characName)) {
        contrs.add(contr);
      }
    }
    return contrs;
  }

  /**
   * Donne les contrainte de généralisation de la base définies sur le concept
   * "conceptName".
   * @param conceptName
   * @return
   */
  public HashSet<FormalGenConstraint> getConstraintsFromConcept(
      String conceptName) {
    HashSet<FormalGenConstraint> contrs = new HashSet<FormalGenConstraint>();
    for (FormalGenConstraint contr : this.microConstraints) {
      if (contr.getConcept().getName().equals(conceptName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.macroConstraints) {
      if (contr.getConcept().getName().equals(conceptName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.mesoConstraints) {
      if (contr.getConcept().getName().equals(conceptName)) {
        contrs.add(contr);
      }
    }
    for (FormalGenConstraint contr : this.relationalConstraints) {
      if (contr.getConcept().getName().equals(conceptName)) {
        contrs.add(contr);
      }
    }
    return contrs;
  }

  /**
   * Donne la contrainte de généralisation de la base qui a pour nom
   * "nomElement".
   * @param constraintName
   * @return
   */
  public FormalGenConstraint getConstraintFromName(String constraintName) {
    for (FormalGenConstraint contr : this.microConstraints) {
      if (contr.getName().equals(constraintName)) {
        return contr;
      }
    }
    for (FormalGenConstraint contr : this.macroConstraints) {
      if (contr.getName().equals(constraintName)) {
        return contr;
      }
    }
    for (FormalGenConstraint contr : this.mesoConstraints) {
      if (contr.getName().equals(constraintName)) {
        return contr;
      }
    }
    for (FormalGenConstraint contr : this.relationalConstraints) {
      if (contr.getName().equals(constraintName)) {
        return contr;
      }
    }
    return null;
  }

  /**
   * Supprime une contrainte quelqconque dans la base de données ainsi que de
   * Gothic.
   * @param contr : une contrainte quelconque.
   */
  public void deleteConstraint(FormalGenConstraint contr) {
    if (contr instanceof FormalMicroConstraint) {
      this.microConstraints.remove(contr);
    }
    if (contr instanceof FormalMacroConstraint) {
      this.macroConstraints.remove(contr);
    }
    if (contr instanceof FormalMesoConstraint) {
      this.mesoConstraints.remove(contr);
    }
    if (contr instanceof FormalRelationalConstraint) {
      this.relationalConstraints.remove(contr);
    }
    contr.clear();
  }

  /**
   * Ajoute une contrainte quelqconque dans la base de données.
   * @param contr : une contrainte quelconque.
   */
  public void addConstraint(FormalGenConstraint contr) {
    if (FormalMicroConstraint.class.isInstance(contr)) {
      this.microConstraints.add((FormalMicroConstraint) contr);
    }
    if (FormalMesoConstraint.class.isInstance(contr)) {
      this.mesoConstraints.add((FormalMesoConstraint) contr);
    }
    if (FormalMacroConstraint.class.isInstance(contr)) {
      this.macroConstraints.add((FormalMacroConstraint) contr);
    }
    if (FormalRelationalConstraint.class.isInstance(contr)) {
      this.relationalConstraints.add((FormalRelationalConstraint) contr);
    }
  }

  /**
   * Clears the database by removing all constraints
   */
  public void clear() {
    this.getMicroConstraints().clear();
    this.getMesoConstraints().clear();
    this.getMacroConstraints().clear();
    this.getRelationalConstraints().clear();
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
