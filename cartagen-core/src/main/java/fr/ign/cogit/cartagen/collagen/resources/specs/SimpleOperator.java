package fr.ign.cogit.cartagen.collagen.resources.specs;

public enum SimpleOperator {
  SUP, INF, EQ_SUP, EQ_INF, EQUAL;

  public static SimpleOperator shortcut(String text) {
    SimpleOperator op = EQUAL;
    if (text.equals(">"))
      op = SUP;
    else if (text.equals("<"))
      op = INF;
    else if (text.equals(">="))
      op = EQ_SUP;
    else if (text.equals("<="))
      op = EQ_INF;
    return op;
  }

  public String toShortcut() {
    String rac = "=";
    if (this.equals(SUP))
      rac = ">";
    else if (this.equals(INF))
      rac = "<";
    else if (this.equals(EQ_SUP))
      rac = ">=";
    else if (this.equals(EQ_INF))
      rac = "<=";
    return rac;
  }

  public boolean compare(int nb1, int nb2) {
    if (this.equals(EQUAL)) {
      if (nb1 == nb2)
        return true;
      return false;
    } else if (this.equals(SUP)) {
      if (nb1 > nb2)
        return true;
      return false;
    } else if (this.equals(INF)) {
      if (nb1 < nb2)
        return true;
      return false;
    } else if (this.equals(EQ_SUP)) {
      if (nb1 >= nb2)
        return true;
      return false;
    } else {
      if (nb1 <= nb2)
        return true;
      return false;
    }
  }

  public boolean compare(double nb1, double nb2) {
    if (this.equals(EQUAL)) {
      if (nb1 == nb2)
        return true;
      return false;
    } else if (this.equals(SUP)) {
      if (nb1 > nb2)
        return true;
      return false;
    } else if (this.equals(INF)) {
      if (nb1 < nb2)
        return true;
      return false;
    } else if (this.equals(EQ_SUP)) {
      if (nb1 >= nb2)
        return true;
      return false;
    } else {
      if (nb1 <= nb2)
        return true;
      return false;
    }
  }
}
