package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.collagen.resources.specs.CharacterValueType;
import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;

public abstract class ExpressionType {
  ////////////////////////////////////////////
  // Fields //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private FormalGenConstraint constraint;
  private ConstraintOperator keyWord;

  ////////////////////////////////////////////
  // Static methods //
  ////////////////////////////////////////////
  public static ExpressionType construire(Element elemType) {
    ExpressionType expr = null;
    Element classeElem = (Element) elemType
        .getElementsByTagName("expression-class").item(0);
    String classe = classeElem.getChildNodes().item(0).getNodeValue();
    Element motCleElem = (Element) elemType.getElementsByTagName("keyword")
        .item(0);
    String motCleNom = motCleElem.getChildNodes().item(0).getNodeValue();
    ConstraintOperator keyWord = ConstraintOperator
        .shortcut(StringEscapeUtils.unescapeXml(motCleNom));
    if (classe.equals(ConfigExpressionType.class.getSimpleName())) {
      expr = new ConfigExpressionType(null, keyWord);
    } else if (classe.equals(MarginExpressionType.class.getSimpleName())) {
      Element pourElem = (Element) elemType.getElementsByTagName("margin")
          .item(0);
      Double pourcent = Double
          .valueOf(pourElem.getChildNodes().item(0).getNodeValue());
      double pourcentage = 0.0;
      if (pourcent != null)
        pourcentage = pourcent.doubleValue();
      // on récupère l'unité
      Element unitElem = (Element) elemType.getElementsByTagName("unit")
          .item(0);
      ValueUnit unite = ValueUnit
          .valueOf(unitElem.getChildNodes().item(0).getNodeValue());
      expr = new MarginExpressionType(null, keyWord, pourcentage, unite);
    } else if (classe.equals(ReductionExpressionType.class.getSimpleName())) {
      Element pourElem = (Element) elemType.getElementsByTagName("value")
          .item(0);
      double pourcentage = Double
          .valueOf(pourElem.getChildNodes().item(0).getNodeValue());
      expr = new ReductionExpressionType(null, keyWord, pourcentage);
    } else {
      // on récupère la valeur
      Element valElem = (Element) elemType.getElementsByTagName("value")
          .item(0);
      String valeurS = valElem.getChildNodes().item(0).getNodeValue();
      CharacterValueType type = getTypeSimple(valeurS);
      Object valeur = valeurS;
      if (type.equals(CharacterValueType.INT))
        valeur = Integer.valueOf(valeurS);
      else if (type.equals(CharacterValueType.REAL))
        valeur = Double.valueOf(valeurS);
      else if (type.equals(CharacterValueType.BOOLEAN))
        valeur = Boolean.valueOf(valeurS);
      // on récupère l'unité
      Element unitElem = (Element) elemType.getElementsByTagName("unit")
          .item(0);
      ValueUnit unite = ValueUnit
          .valueOf(unitElem.getChildNodes().item(0).getNodeValue());
      if (classe.equals(ThreshExpressionType.class.getSimpleName()))
        expr = new ThreshExpressionType(null, keyWord, valeur, unite);
      else
        expr = new ControlExpressionType(null, keyWord, valeur, unite);
    }
    return expr;
  }

  private static CharacterValueType getTypeSimple(String s) {
    try {
      Integer.valueOf(s);
      return CharacterValueType.INT;
    } catch (NumberFormatException e) {
      try {
        Double.valueOf(s);
        return CharacterValueType.REAL;
      } catch (NumberFormatException e2) {
        if (s.equals("true") || s.equals("false"))
          return CharacterValueType.BOOLEAN;
      } // catch pour l'essai Double.valueOf
    } // catch pour l'essai Integer.valueOf
    return CharacterValueType.STRING;
  }

  ////////////////////////////////////////////
  // Public methods //
  ////////////////////////////////////////////

  // Public constructors //

  // Getters and setters //
  public FormalGenConstraint getConstraint() {
    return constraint;
  }

  public void setConstraint(FormalGenConstraint contrainte) {
    this.constraint = contrainte;
  }

  public ConstraintOperator getKeyWord() {
    return keyWord;
  }

  public void setKeyWord(ConstraintOperator keyWord) {
    this.keyWord = keyWord;
  }

  // Other public methods //
  public abstract Object getValue();

  public abstract void setValue(Object valeur);

  public abstract ValueUnit getValueUnit();

  public abstract void setValueUnit(ValueUnit unit);

  ////////////////////////////////////////////
  // Protected methods //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  // Package visible methods //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  // Private methods //
  //////////////////////////////////////////

}
