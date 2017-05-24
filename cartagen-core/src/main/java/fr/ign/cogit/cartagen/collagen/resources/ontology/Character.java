package fr.ign.cogit.cartagen.collagen.resources.ontology;

import java.util.HashSet;


public class Character {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private String name;
  private Class<? extends Object> dataType;
  private HashSet<GeneralisationConcept> concepts;
  private CharacterType type;
  private boolean macro;

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public Character(String name, Class<? extends Object> dataType, 
      CharacterType type,boolean macro) {
    this.name = name;
    this.dataType = dataType;
    this.type = type;
    concepts = new HashSet<GeneralisationConcept>();
    this.macro = macro;
  }
  
  // Getters and setters //
  public String getName() {return name;}
  public void setName(String name) {this.name = name;}
  public Class<? extends Object> getDataType() {return dataType;}
  public void setDataType(Class<? extends Object> dataType) {this.dataType = dataType;}
  public HashSet<GeneralisationConcept> getConcepts() {return concepts;}
  public void setConcepts(HashSet<GeneralisationConcept> concept) {this.concepts = concept;}
  public CharacterType getType() {return type;}
  public void setType(CharacterType type) {this.type = type;}
  public boolean isMacro() {return macro;}
  public void setMacro(boolean macro) {this.macro = macro;}

  // Other public methods //

  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////


}

