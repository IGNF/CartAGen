package fr.ign.cogit.cartagen.collagen.resources.ontology;



public class Relationship {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private String name;
  private GeneralisationConcept initialElement, finalElement;
  private int cardinality;
  private Relationship inverse;


  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public Relationship(String name, GeneralisationConcept initialElement,
      GeneralisationConcept finalElement, int cardinality) {
    this.name = name;
    this.initialElement = initialElement;
    this.finalElement = finalElement;
    this.cardinality = cardinality;
  }
  
  // Getters and setters //
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public GeneralisationConcept getInitialElement() {
    return initialElement;
  }
  public void setInitialElement(GeneralisationConcept initialElement) {
    this.initialElement = initialElement;
  }
  public GeneralisationConcept getFinalElement() {
    return finalElement;
  }
  public void setFinalElement(GeneralisationConcept finalElement) {
    this.finalElement = finalElement;
  }
  public int getCardinality() {
    return cardinality;
  }
  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }
  public Relationship getInverse() {
    return inverse;
  }
  public void setInverse(Relationship inverse) {
    this.inverse = inverse;
  }
  
  // Other public methods //
  @Override
  public boolean equals(Object obj) {
    Relationship assoc = (Relationship)obj;
      if(!this.name.equals(assoc.name)) return false;
      if(!this.initialElement.equals(assoc.initialElement)) return false;
      if(!this.finalElement.equals(assoc.finalElement)) return false;
      return true;
  }
  @Override
  public int hashCode() {return name.hashCode();}
  
  @Override
  public String toString() {
      return initialElement.getName()+" - "+name+" - "+finalElement.getName()+" : "+cardinality;
  }
  
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

