package fr.ign.cogit.cartagen.collagen.components.translator;

import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;

public class UnitsTranslation {

  /**
   * Renvoie la valeur de l'expression de la contrainte en unit� carte.
   * @param echFinale
   * @param contr
   * @return
   */
  public static double getValeurContrUniteCarte(double echFinale,
      FormalGenConstraint contr) {
    ExpressionType expr = contr.getExprType();
    ValueUnit unite = expr.getValueUnit();
    if (unite.equals(ValueUnit.MAP) || unite.equals(ValueUnit.MAP_SQUARE))
      return (Double) expr.getValue();
    else if (unite.equals(ValueUnit.GROUND))
      return (Double) expr.getValue() * 1000.0 / echFinale;
    else
      return (Double) expr.getValue() * 1000000.0 / (echFinale * echFinale);
  }

  /**
   * Renvoie la valeur de l'expression de la contrainte en unit� terrain.
   * @param echFinale
   * @param contr
   * @return
   */
  public static double getValeurContrUniteTerrain(double echFinale,
      FormalGenConstraint contr) {
    ExpressionType expr = contr.getExprType();
    ValueUnit unite = expr.getValueUnit();
    if (unite.equals(ValueUnit.MAP))
      return (Double) expr.getValue() * echFinale / 1000.0;
    else if (unite.equals(ValueUnit.MAP_SQUARE)) {
      double valeurTerrain = ((Double) expr.getValue()).doubleValue()
          * echFinale * echFinale / 1000000.0;
      return valeurTerrain;
    } else
      return (Double) expr.getValue();
  }
}
