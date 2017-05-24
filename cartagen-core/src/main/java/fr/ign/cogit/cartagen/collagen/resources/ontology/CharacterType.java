package fr.ign.cogit.cartagen.collagen.resources.ontology;

// ////////////////////////////////////////
// Enumerations and internal classes //
// ////////////////////////////////////////
public enum CharacterType {
  SIZE, SHAPE, POSITION, DISTANCE, TOPOLOGY, STRUCTURE, ORIENTATION, STAT, SEMANTIC, CARTO;

  public static CharacterType valueofFrench(String frenchType) {
    if (frenchType.equals("TAILLE"))
      return SIZE;
    if (frenchType.equals("FORME"))
      return SHAPE;
    if (frenchType.equals("POSITION"))
      return POSITION;
    if (frenchType.equals("DISTANCE"))
      return DISTANCE;
    if (frenchType.equals("TOPOLOGIE"))
      return TOPOLOGY;
    if (frenchType.equals("STRUCTURE"))
      return STRUCTURE;
    if (frenchType.equals("ORIENTATION"))
      return ORIENTATION;
    if (frenchType.equals("STAT"))
      return STAT;
    if (frenchType.equals("SEMANTIQUE"))
      return SEMANTIC;
    if (frenchType.equals("CARTO"))
      return CARTO;
    return null;
  }
}
