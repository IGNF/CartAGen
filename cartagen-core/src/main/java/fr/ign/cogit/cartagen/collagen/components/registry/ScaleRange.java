package fr.ign.cogit.cartagen.collagen.components.registry;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScaleRange implements Comparable<ScaleRange> {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private int iniScaleLow;
  private int iniScaleHi;
  private int genScaleLow;
  private int genScaleHi;
  private int iniScaleOptimal;
  private int genScaleOptimal;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ScaleRange(int iniScaleLow, int iniScaleHi, int genScaleLow,
      int genScaleHi, int iniScaleOptimal, int genScaleOptimal) {
    super();
    this.iniScaleLow = iniScaleLow;
    this.iniScaleHi = iniScaleHi;
    this.genScaleLow = genScaleLow;
    this.genScaleHi = genScaleHi;
    this.iniScaleOptimal = iniScaleOptimal;
    this.genScaleOptimal = genScaleOptimal;
  }

  public ScaleRange(int iniScaleLow, int iniScaleHi, int genScaleLow,
      int genScaleHi) {
    super();
    this.iniScaleLow = iniScaleLow;
    this.iniScaleHi = iniScaleHi;
    this.genScaleLow = genScaleLow;
    this.genScaleHi = genScaleHi;
    this.iniScaleOptimal = -1;
    this.genScaleOptimal = -1;
  }

  // Getters and setters //
  public int getIniScaleLow() {
    return iniScaleLow;
  }

  public void setIniScaleLow(int iniScaleLow) {
    this.iniScaleLow = iniScaleLow;
  }

  public int getIniScaleHi() {
    return iniScaleHi;
  }

  public void setIniScaleHi(int iniScaleHi) {
    this.iniScaleHi = iniScaleHi;
  }

  public int getGenScaleLow() {
    return genScaleLow;
  }

  public void setGenScaleLow(int genScaleLow) {
    this.genScaleLow = genScaleLow;
  }

  public int getGenScaleHi() {
    return genScaleHi;
  }

  public void setGenScaleHi(int genScaleHi) {
    this.genScaleHi = genScaleHi;
  }

  public int getIniScaleOptimal() {
    return iniScaleOptimal;
  }

  public void setIniScaleOptimal(int iniScaleOptimal) {
    this.iniScaleOptimal = iniScaleOptimal;
  }

  public int getGenScaleOptimal() {
    return genScaleOptimal;
  }

  public void setGenScaleOptimal(int genScaleOptimal) {
    this.genScaleOptimal = genScaleOptimal;
  }

  // Other public methods //
  @Override
  public boolean equals(Object obj) {
    ScaleRange ech = (ScaleRange) obj;
    if (this.iniScaleLow != ech.iniScaleLow)
      return false;
    if (this.iniScaleHi != ech.iniScaleHi)
      return false;
    if (this.genScaleLow != ech.genScaleLow)
      return false;
    if (this.genScaleHi != ech.genScaleHi)
      return false;
    if (this.iniScaleOptimal != ech.iniScaleOptimal)
      return false;
    if (this.genScaleOptimal != ech.genScaleOptimal)
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return iniScaleLow;
  }

  @Override
  public String toString() {
    return "1:" + iniScaleLow + " to 1:" + genScaleHi;
  }

  @Override
  public int compareTo(ScaleRange o) {
    int optRange = (this.genScaleOptimal - this.iniScaleOptimal)
        - (o.genScaleOptimal - o.iniScaleOptimal);
    if (optRange != 0)
      return optRange;
    int range = Math.round((this.genScaleHi - this.iniScaleLow) / 1000
        - (o.genScaleHi - o.iniScaleLow) / 1000);
    return range;
  }

  /**
   * Write in an open xml document the scale range in order to store it.
   * 
   * @param xmlDoc
   * @param root
   * @author GTouya
   */
  public void writeToXML(DocumentImpl xmlDoc, Element root) {
    Node n = null;
    // on crée un élément pour la gamme d'échelles
    Element gamme = xmlDoc.createElement("scale-range");
    root.appendChild(gamme);
    // on crée l'élément échelle ini basse
    Element iniBasseElem = xmlDoc.createElement("ini-scale-low");
    n = xmlDoc.createTextNode(String.valueOf(iniScaleLow));
    iniBasseElem.appendChild(n);
    gamme.appendChild(iniBasseElem);
    // on crée l'élément échelle ini haute
    Element iniHauteElem = xmlDoc.createElement("ini-scale-high");
    n = xmlDoc.createTextNode(String.valueOf(iniScaleHi));
    iniHauteElem.appendChild(n);
    gamme.appendChild(iniHauteElem);
    // on crée l'élément échelle gen basse
    Element genBasseElem = xmlDoc.createElement("gen-scale-low");
    n = xmlDoc.createTextNode(String.valueOf(genScaleLow));
    genBasseElem.appendChild(n);
    gamme.appendChild(genBasseElem);
    // on crée l'élément échelle gen haute
    Element genHauteElem = xmlDoc.createElement("gen-scale-high");
    n = xmlDoc.createTextNode(String.valueOf(genScaleHi));
    genHauteElem.appendChild(n);
    gamme.appendChild(genHauteElem);
    // on crée l'élément échelle ini optimal
    if (iniScaleOptimal != -1) {
      Element iniOptElem = xmlDoc.createElement("ini-scale-opt");
      n = xmlDoc.createTextNode(String.valueOf(iniScaleOptimal));
      iniOptElem.appendChild(n);
      gamme.appendChild(iniOptElem);
    }
    // on crée l'élément échelle gen optimal
    if (genScaleOptimal != -1) {
      Element genOptElem = xmlDoc.createElement("gen-scale-opt");
      n = xmlDoc.createTextNode(String.valueOf(genScaleOptimal));
      genOptElem.appendChild(n);
      gamme.appendChild(genOptElem);
    }
  }

  /**
   * Détermine si un saut d'échelle de généralisation est valable dans la gamme
   * d'échelle. Par exemple, pour une gamme (10k-15k vers 25k-50k), le saut
   * d'échelle 15k-50k est valable.
   * 
   * @param initialScale : l'échelle initiale des données
   * @param finalScale : l'échelle de la carte finale (ou BDC finale)
   * @return
   */
  public boolean isScaleGapInRange(int initialScale, int finalScale) {
    if (initialScale < iniScaleLow || initialScale > iniScaleHi)
      return false;
    if (finalScale < genScaleLow || finalScale > genScaleHi)
      return false;
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

}
