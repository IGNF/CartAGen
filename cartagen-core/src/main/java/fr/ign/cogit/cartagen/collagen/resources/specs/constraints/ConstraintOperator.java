package fr.ign.cogit.cartagen.collagen.resources.specs.constraints;


public enum ConstraintOperator {
  SUP,
  INF,
  EQ_SUP,
  EQ_INF,
  EQUAL,
  MAINTAINED,
  SIMILAR,
  FORCE,
  AVOID,
  FORBIDDEN;

  public static ConstraintOperator shortcut(String text) {
    ConstraintOperator op = EQUAL;
    if(text.equals(">")) op = SUP;
    else if(text.equals("<")) op = INF;
    else if(text.equals(">=")) op = EQ_SUP;
    else if(text.equals("<=")) op = EQ_INF;
    else if(text.equals("maintained")) op = MAINTAINED;
    else if(text.equals("similar")) op = SIMILAR;
    else if(text.equals("forced")) op = FORCE;
    else if(text.equals("avoided")) op = AVOID;
    else if(text.equals("forbidden")) op = FORBIDDEN;
    return op;
  }

  public static ConstraintOperator shortcutFr(String text) {
    ConstraintOperator op = EQUAL;
    if(text.equals(">")) op = SUP;
    else if(text.equals("<")) op = INF;
    else if(text.equals(">=")) op = EQ_SUP;
    else if(text.equals("<=")) op = EQ_INF;
    else if(text.equals("maintenu")) op = MAINTAINED;
    else if(text.equals("similaire")) op = SIMILAR;
    else if(text.equals("forcé")) op = FORCE;
    else if(text.equals("évité")) op = AVOID;
    else if(text.equals("interdit")) op = FORBIDDEN;
    return op;
  }

  public String toShortcutFr(){
    if(this.equals(SUP)) return ">";
    else if(this.equals(INF)) return "<";
    else if(this.equals(EQ_SUP)) return ">=";
    else if(this.equals(EQ_INF)) return "<=";
    else if(this.equals(MAINTAINED)) return "maintenu";
    else if(this.equals(SIMILAR)) return "similaire";
    else if(this.equals(FORCE)) return "forcé";
    else if(this.equals(AVOID)) return "évité";
    else if(this.equals(FORBIDDEN)) return "interdit";
    else if(this.equals(EQUAL)) return "=";
    return null;
  }
  
  public String toShortcut(){
    if(this.equals(SUP)) return ">";
    else if(this.equals(INF)) return "<";
    else if(this.equals(EQ_SUP)) return ">=";
    else if(this.equals(EQ_INF)) return "<=";
    else if(this.equals(MAINTAINED)) return "maintained";
    else if(this.equals(SIMILAR)) return "similar";
    else if(this.equals(FORCE)) return "forced";
    else if(this.equals(AVOID)) return "avoided";
    else if(this.equals(FORBIDDEN)) return "forbidden";
    else if(this.equals(EQUAL)) return "=";
    return null;
  }
}

