package fr.ign.cogit.cartagen.collagen.components.registry;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRulesDatabase;

public class PostConditionProcess implements Comparable<PostConditionProcess> {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //
  
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private SpecificationElement element;
  private ScaleRange scales;
  private ConfidenceRate confidenceRate;


  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public PostConditionProcess(SpecificationElement element,
      ScaleRange echelles, ConfidenceRate confidenceRate) {
    super();
    this.element = element;
    this.scales = echelles;
    this.confidenceRate = confidenceRate;
  }

  public PostConditionProcess(Element xmlElem, ConstraintDatabase cdb, 
      OperationRulesDatabase rdb){
    Element elemSpecElem = (Element)xmlElem.getElementsByTagName("specification-element").item(0);
    Element typeElem = (Element)elemSpecElem.getElementsByTagName("type").item(0);
    String type = typeElem.getChildNodes().item(0).getNodeValue();
    Element nomElem = (Element)elemSpecElem.getElementsByTagName("name").item(0);
    String elementName = nomElem.getChildNodes().item(0).getNodeValue();
    if(type.equals("rule")) this.element = rdb.getRuleFromName(elementName);
    else this.element = cdb.getConstraintFromName(elementName);
    Element gammeElem = (Element)xmlElem.getElementsByTagName("scale-range").item(0);
    Element iniBasElem = (Element)gammeElem.getElementsByTagName("ini-scale-low").item(0);
    String iniBas = iniBasElem.getChildNodes().item(0).getNodeValue();
    Element iniHautElem = (Element)gammeElem.getElementsByTagName("ini-scale-high").item(0);
    String iniHaut = iniHautElem.getChildNodes().item(0).getNodeValue();
    Element genBasElem = (Element)gammeElem.getElementsByTagName("gen-scale-low").item(0);
    String genBas = genBasElem.getChildNodes().item(0).getNodeValue();
    Element genHautElem = (Element)gammeElem.getElementsByTagName("gen-scale-high").item(0);
    String genHaut = genHautElem.getChildNodes().item(0).getNodeValue();
    this.scales = new ScaleRange(Integer.valueOf(iniBas),Integer.valueOf(iniHaut),
            Integer.valueOf(genBas),Integer.valueOf(genHaut));
    Element indiceElem = (Element)xmlElem.getElementsByTagName("confidence-rate").item(0);
    String indice = indiceElem.getChildNodes().item(0).getNodeValue();
    this.confidenceRate = ConfidenceRate.valueOf(indice);
  }
      
  // Getters and setters //
  public SpecificationElement getElement() {
    return element;
  }
  public void setElement(SpecificationElement element) {
    this.element = element;
  }
  public ScaleRange getScales() {
    return scales;
  }
  public void setScales(ScaleRange echelles) {
    this.scales = echelles;
  }
  public ConfidenceRate getConfidenceRate() {
    return confidenceRate;
  }
  public void setConfidenceRate(ConfidenceRate confidenceRate) {
    this.confidenceRate = confidenceRate;
  }

  
  // Other public methods //
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((confidenceRate == null) ? 0 : confidenceRate.hashCode());
    result = prime * result + ((scales == null) ? 0 : scales.hashCode());
    result = prime * result + ((element == null) ? 0 : element.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PostConditionProcess other = (PostConditionProcess) obj;
    if (confidenceRate == null) {
      if (other.confidenceRate != null)
        return false;
    } else if (!confidenceRate.equals(other.confidenceRate))
      return false;
    if (scales == null) {
      if (other.scales != null)
        return false;
    } else if (!scales.equals(other.scales))
      return false;
    if (element == null) {
      if (other.element != null)
        return false;
    } else if (!element.equals(other.element))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return element.toString()+"-confidence "+confidenceRate+"-"+scales.toString();
  }

  @Override
  public int compareTo(PostConditionProcess o) {
    if(!this.element.equals(o.element)) return 0;
    int ecart = 100 * (this.confidenceRate.ordinal()-o.confidenceRate.ordinal());
    return ecart + this.scales.compareTo(o.scales);
  }
  
  /**
   * Stocke la post-condition dans un fichier XML de description de processus.
   * 
   * @param xmlDoc : le document XML en construction
   * @param root : la racine du document XML à partir de laquelle on stocke la post-condition
   */
  public void writeToXML(DocumentImpl xmlDoc,Element root) {
    Node n = null;
    // on crée un élément pour la gamme d'échelles
    Element post = xmlDoc.createElement("post-condition");
    root.appendChild(post);
    // on crée l'élément élément-spécification
    Element specElem = xmlDoc.createElement("specification-element");
    post.appendChild(specElem);
    // on crée le sous élément type
    String type = "rule";
    Class<? extends Object> superClasse = element.getClass().getSuperclass();
    if(superClasse.equals(FormalGenConstraint.class)) type = "constraint";
    Element typeElem = xmlDoc.createElement("type");
    n = xmlDoc.createTextNode(type);
    typeElem.appendChild(n);
    specElem.appendChild(typeElem);
    // on crée le sous élément nom
    Element nomElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(element.getName());
    nomElem.appendChild(n);
    specElem.appendChild(nomElem);
    // on crée l'élément gamme d'échelle
    this.scales.writeToXML(xmlDoc, post);
    // on crée l'élément indice-confiance
    Element genHauteElem = xmlDoc.createElement("confidence-rate");
    n = xmlDoc.createTextNode(confidenceRate.name());
    genHauteElem.appendChild(n);
    post.appendChild(genHauteElem);
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

