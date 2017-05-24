package fr.ign.cogit.cartagen.collagen.resources.specs;

import java.util.Date;

public enum CharacterValueType {
  STRING,
  REAL,
  INT,
  BOOLEAN,
  DATE,
  UNKNOWN;
  
  public static CharacterValueType getType(Object value){
    if(value instanceof Double || value instanceof Float) return REAL;
    if(value instanceof Integer || value instanceof Long) return INT;
    if(value instanceof Boolean) return BOOLEAN;
    if(value instanceof Date) return DATE;
    if(value instanceof String) return STRING;
    return UNKNOWN;
  }
}

