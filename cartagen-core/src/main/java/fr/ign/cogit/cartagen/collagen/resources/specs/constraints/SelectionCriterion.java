package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.collagen.resources.specs.CharacterValueType;
import fr.ign.cogit.cartagen.collagen.resources.specs.SimpleOperator;
import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

@Entity
public class SelectionCriterion {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private FormalGenConstraint constraint;
  private Set<Request> requests;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////
  /**
   * Builds a selection criterion from an element extracted from xml.
   */
  public static SelectionCriterion buildCriterion(Element critElem,
      HashSet<GeneralisationConcept> concepts) {
    SelectionCriterion crit = new SelectionCriterion(null, null);
    HashSet<Request> reqs = new HashSet<Request>();
    // on fait une boucle sur les requêtes du critère
    for (int j = 0; j < critElem.getElementsByTagName("request")
        .getLength(); j++) {
      Element reqElem = (Element) critElem.getElementsByTagName("request")
          .item(j);
      Element concRElem = (Element) reqElem.getElementsByTagName("concept")
          .item(0);
      String nomConcR = concRElem.getChildNodes().item(0).getNodeValue();
      GeneralisationConcept conceptReq = GeneralisationConcept
          .getElemGeoFromName(nomConcR, concepts);
      Element carRElem = (Element) reqElem.getElementsByTagName("character")
          .item(0);
      String nomCarR = carRElem.getChildNodes().item(0).getNodeValue();
      Character caracReq = conceptReq.getCaracFromNom(nomCarR);
      Element opElem = (Element) reqElem.getElementsByTagName("operator")
          .item(0);
      String opNom = opElem.getChildNodes().item(0).getNodeValue();
      SimpleOperator operateur = SimpleOperator
          .shortcut(StringEscapeUtils.unescapeXml(opNom));
      Element unitElem = (Element) reqElem.getElementsByTagName("unit").item(0);
      ValueUnit unite = ValueUnit
          .valueOf(unitElem.getChildNodes().item(0).getNodeValue());
      Element tolElem = (Element) reqElem.getElementsByTagName("tolerance")
          .item(0);
      int tolerance = Integer
          .valueOf(tolElem.getChildNodes().item(0).getNodeValue());
      Element valElem = (Element) reqElem.getElementsByTagName("value").item(0);
      String valeurS = valElem.getChildNodes().item(0).getNodeValue();
      Element typeElem = (Element) reqElem.getElementsByTagName("value-type")
          .item(0);
      CharacterValueType type = CharacterValueType
          .valueOf(typeElem.getChildNodes().item(0).getNodeValue());
      Object valeur = valeurS;
      if (type.equals(CharacterValueType.INT))
        valeur = Integer.valueOf(valeurS);
      else if (type.equals(CharacterValueType.REAL))
        valeur = Double.valueOf(valeurS);
      else if (type.equals(CharacterValueType.BOOLEAN))
        valeur = Boolean.valueOf(valeurS);
      reqs.add(crit.new Request(crit, conceptReq, caracReq, valeur, tolerance,
          operateur, unite));
    }
    crit.setRequests(reqs);
    return crit;
  }

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public SelectionCriterion() {
    requests = new HashSet<Request>();
  }

  public SelectionCriterion(FormalGenConstraint constraint,
      Set<Request> requests) {
    super();
    this.constraint = constraint;
    this.requests = requests;
  }

  // Getters and setters //
  public FormalGenConstraint getConstraint() {
    return constraint;
  }

  public void setConstraint(FormalGenConstraint c) {
    this.constraint = c;
  }

  public Set<Request> getRequests() {
    return requests;
  }

  public void setRequests(Set<Request> requests) {
    this.requests = requests;
  }

  // Other public methods //
  public boolean verify(IGeneObj obj, SchemaAnnotation app) {
    // on fait une boucle sur les requetes
    for (Request req : requests) {
      // on récupère le nom de l'attribut gothic correspondant au caractère
      String[] result = app.getAttributeAnnotation()
          .get(req.getCharacter().getName());
      String attr = result[result.length - 1];
      Object valeur = obj.getAttribute(attr);
      // on compare la valeur
      if (!req.compareValue(valeur))
        return false;
    }
    return true;
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

  // ////////////////////////////////////////
  // Enumerations and internal classes //
  // ////////////////////////////////////////
  /**
   * Une requête est un objet qui permet de sélectionner une partie des
   * instances d'un concept géographique en décrivant une règle sur un de ses
   * caractères. Une requête s'énonce de la manière suivante : "On prend les
   * objets de Concept tels que caractere.valeurIni() opérateur (< ou =...)
   * valeur (+- tolérance)".
   * 
   * 
   */
  public class Request {

    private HashSet<SelectionCriterion> criteria;
    private GeneralisationConcept concept;
    private Character character;// le caractere sur lequel porte la requête
    private Object value;// la valeur seuil pour le caractere
    private CharacterValueType valueType;
    private int tolerance;// pourcentage de tolérance d'une valeur
    private SimpleOperator operator;// l'opérateur de la requête
    private ValueUnit unit;

    public GeneralisationConcept getConcept() {
      return concept;
    }

    public void setConcept(GeneralisationConcept concept) {
      this.concept = concept;
    }

    public Character getCharacter() {
      return character;
    }

    public void setCharacter(Character caractere) {
      this.character = caractere;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object valeur) {
      this.value = valeur;
    }

    public int getTolerance() {
      return tolerance;
    }

    public void setTolerance(int tolerance) {
      this.tolerance = tolerance;
    }

    public SimpleOperator getOperator() {
      return operator;
    }

    public void setOperateur(SimpleOperator operateur) {
      this.operator = operateur;
    }

    public CharacterValueType getValueType() {
      return valueType;
    }

    public void setTypeValeur(CharacterValueType typeValeur) {
      this.valueType = typeValeur;
    }

    public ValueUnit getUnit() {
      return unit;
    }

    public void setUnite(ValueUnit unite) {
      this.unit = unite;
    }

    @Override
    public boolean equals(Object arg0) {
      Request autre = (Request) arg0;
      if (!this.concept.equals(autre.concept)) {
        return false;
      }
      if (!this.character.equals(autre.character)) {
        return false;
      }
      if (!this.operator.equals(autre.operator)) {
        return false;
      }
      if (!this.value.equals(autre.value)) {
        return false;
      }
      if (this.tolerance != autre.tolerance) {
        return false;
      }
      if (!this.unit.equals(autre.unit)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public String toString() {
      return concept + "." + character + " " + operator.name();
    }

    public Request(SelectionCriterion critere, GeneralisationConcept concept,
        Character caractere, Object valeur, int tolerance,
        SimpleOperator operateur, ValueUnit unite) {
      super();
      this.criteria = new HashSet<SelectionCriterion>();
      this.criteria.add(critere);
      this.concept = concept;
      this.character = caractere;
      this.value = valeur;
      this.valueType = CharacterValueType.getType(valeur);
      this.tolerance = tolerance;
      this.operator = operateur;
      this.unit = unite;
    }

    public boolean compareValue(Object otherValue) {
      if (valueType.equals(CharacterValueType.STRING)) {
        String val = (String) value;
        String valAutre = (String) otherValue;
        if (!val.equals(valAutre))
          return false;
        return true;
      } else if (valueType.equals(CharacterValueType.INT)) {
        int val = (Integer) value;
        int valAutre = (Integer) otherValue;
        return this.operator.compare(valAutre, val);
      } else if (valueType.equals(CharacterValueType.REAL)) {
        double val = (Double) value;
        if (unit.equals(ValueUnit.ANGLE))
          val = val * Math.PI / 180.0;
        double valAutre = (Double) otherValue;
        return this.operator.compare(valAutre, val);
      } else {
        Boolean val = (Boolean) value;
        Boolean valAutre = (Boolean) otherValue;
        if (val.booleanValue() != valAutre.booleanValue())
          return false;
        return true;
      }
    }
  }
}
