package fr.ign.cogit.cartagen.collagen.components.registry;

import java.util.HashSet;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;

public class PreConditionProcess implements Comparable<PreConditionProcess>{
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private GeoSpaceConcept space;
  private ScaleRange scales;
  private ConfidenceRate confidenceRate;
  // densité d'objets qualitative (faible, moyenne ou forte)
  private int density;
  // homogeneous
  private boolean homogeneous;
  // hierarchy
  private boolean hierarchy;
  private GeographicConcept mainConcept, secondaryConcept;

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //  
  public PreConditionProcess(GeoSpaceConcept space, ScaleRange scales,
      ConfidenceRate confidenceRate, int density, boolean homogeneous,
      boolean hierarchy, GeographicConcept mainConcept,
      GeographicConcept secondaryConcept) {
    super();
    this.space = space;
    this.scales = scales;
    this.confidenceRate = confidenceRate;
    this.density = density;
    this.homogeneous = homogeneous;
    this.hierarchy = hierarchy;
    this.mainConcept = mainConcept;
    this.secondaryConcept = secondaryConcept;
  }
  
  public PreConditionProcess(GeoSpaceConcept space, ScaleRange scales,
      ConfidenceRate confidenceRate) {
    super();
    this.space = space;
    this.scales = scales;
    this.confidenceRate = confidenceRate;
  }

  /**
   * Constructeur à partir d'un élément XML au format de stockage des pre-conditions.
   * L'ensemble des concepts géographiques utilisés est nécessaire.
   * @param xmlElem : l'élément XML décrivant la post-condition à construire
   * @param ontology : l'ensemble des concepts formant notre ontologie
   */
  public PreConditionProcess(Element xmlElem,HashSet<GeneralisationConcept> ontology) {
    Element nomElem = (Element)xmlElem.getElementsByTagName("geographic-space").item(0);
    String nomElement = nomElem.getChildNodes().item(0).getNodeValue();
    this.space = 
      (GeoSpaceConcept) GeneralisationConcept.getElemGeoFromName(nomElement, ontology);

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
    Element caracElem = (Element)xmlElem.getElementsByTagName("characteristics").item(0);
    Element densiteElem = (Element)caracElem.getElementsByTagName("density").item(0);
    if(densiteElem!=null) 
      this.density = Integer.valueOf(densiteElem.getChildNodes().item(0).getNodeValue());
    Element homogElem = (Element)caracElem.getElementsByTagName("homogeneous").item(0);
    if(homogElem!=null) 
      this.homogeneous = Boolean.valueOf(homogElem.getChildNodes().item(0).getNodeValue());
    Element hierarElem = (Element)caracElem.getElementsByTagName("hierarchy").item(0);
    if(hierarElem!=null) 
      this.hierarchy = Boolean.valueOf(hierarElem.getChildNodes().item(0).getNodeValue());
    Element dominantElem = (Element)caracElem.getElementsByTagName("main-concept").item(0);
    if(dominantElem!=null) {
      String nomDominant = dominantElem.getChildNodes().item(0).getNodeValue();
      this.mainConcept = (GeographicConcept) GeneralisationConcept.getElemGeoFromName(
          nomDominant, ontology);
    }
    Element secondElem = (Element)caracElem.getElementsByTagName("secondary-concept").item(0);
    if(secondElem!=null) {
      String nomSecond = secondElem.getChildNodes().item(0).getNodeValue();
      this.secondaryConcept = (GeographicConcept) GeneralisationConcept.getElemGeoFromName(
          nomSecond, ontology);
    }   
  }
  
  // Getters and setters //
  public GeoSpaceConcept getSpace() {
    return space;
  }
  public void setSpace(GeoSpaceConcept space) {
    this.space = space;
  }
  public ScaleRange getScales() {
    return scales;
  }
  public void setScales(ScaleRange scales) {
    this.scales = scales;
  }
  public ConfidenceRate getConfidenceRate() {
    return confidenceRate;
  }
  public void setConfidenceRate(ConfidenceRate confidenceRate) {
    this.confidenceRate = confidenceRate;
  }
  public int getDensity() {
    return density;
  }
  public void setDensity(int density) {
    this.density = density;
  }
  public boolean isHomogeneous() {
    return homogeneous;
  }
  public void setHomogeneous(boolean homogeneous) {
    this.homogeneous = homogeneous;
  }
  public boolean isHierarchy() {
    return hierarchy;
  }
  public void setHierarchy(boolean hierarchy) {
    this.hierarchy = hierarchy;
  }
  public GeographicConcept getMainConcept() {
    return mainConcept;
  }
  public void setMainConcept(GeographicConcept mainConcept) {
    this.mainConcept = mainConcept;
  }
  public GeographicConcept getSecondaryConcept() {
    return secondaryConcept;
  }
  public void setSecondaryConcept(GeographicConcept secondaryConcept) {
    this.secondaryConcept = secondaryConcept;
  }

  // Other public methods //
  @Override
  public int compareTo(PreConditionProcess o) {
    if(!this.space.equals(o.space)) return 0;
    int ecart = 100 * (this.confidenceRate.ordinal()-o.confidenceRate.ordinal());
    return ecart + this.scales.compareTo(o.scales);
  }

  @Override
  public boolean equals(Object obj) {
    PreConditionProcess pc = (PreConditionProcess)obj;
    if(!this.space.equals(pc.space)) return false;
    if(!this.scales.equals(pc.scales)) return false;
    if(this.confidenceRate != pc.confidenceRate) return false;
    if(this.density != pc.density) return false;
    if(this.homogeneous != pc.homogeneous) return false;
    if(this.hierarchy != pc.hierarchy) return false;
    if(this.mainConcept!=null) 
      if(!this.mainConcept.equals(pc.mainConcept)) return false;
    if(this.secondaryConcept!=null) 
      if(!this.secondaryConcept.equals(pc.secondaryConcept)) return false;
    return true;
  }

  @Override
  public int hashCode() {return space.hashCode();}

  @Override
  public String toString() {
    return space.getName()+" ("+confidenceRate+") ";
  }

  public boolean estCompatible(GeoSpaceConcept espace, int echMin, int echMax){
    if(!this.space.estUn(espace)) return false;
    if(!this.scales.isScaleGapInRange(echMin, echMax)) return false;
    return true;
  }

  /**
   * Stocke la pre-condition dans un fichier XML de description de processus.
   * 
   * @param xmlDoc : le document XML en construction
   * @param racine : la racine du document XML à partir de laquelle on stocke la pre-condition
   */
  public void writeToXML(DocumentImpl xmlDoc,Element racine) {
    Node n = null;
    // on crée un élément pour la gamme d'échelles
    Element pre = xmlDoc.createElement("pre-condition");
    racine.appendChild(pre);
    // on crée l'élément espace
    Element espElem = xmlDoc.createElement("geographic-space");
    n = xmlDoc.createTextNode(space.getName());
    espElem.appendChild(n);
    pre.appendChild(espElem);
    // on crée l'élément élément-spécification
    Element caracElem = xmlDoc.createElement("characteristics");
    pre.appendChild(caracElem);
    // on crée le sous élément densité
    Element densiteElem = xmlDoc.createElement("density");
    n = xmlDoc.createTextNode(String.valueOf(density));
    densiteElem.appendChild(n);
    caracElem.appendChild(densiteElem);
    // on crée le sous élément homogénéité
    Element homogElem = xmlDoc.createElement("homogeneous");
    n = xmlDoc.createTextNode(String.valueOf(homogeneous));
    homogElem.appendChild(n);
    caracElem.appendChild(homogElem);
    // on crée le sous élément hiérarchie
    Element hierElem = xmlDoc.createElement("hierarchy");
    n = xmlDoc.createTextNode(String.valueOf(hierarchy));
    hierElem.appendChild(n);
    caracElem.appendChild(hierElem);
    if(mainConcept!=null){
      // on crée le sous élément concept-dominant
      Element domElem = xmlDoc.createElement("main-concept");
      n = xmlDoc.createTextNode(mainConcept.getName());
      domElem.appendChild(n);
      caracElem.appendChild(domElem);
    }
    if(secondaryConcept!=null){
      // on crée le sous élément concept-secondaire
      Element secElem = xmlDoc.createElement("secondary-concept");
      n = xmlDoc.createTextNode(secondaryConcept.getName());
      secElem.appendChild(n);
      caracElem.appendChild(secElem);
    }
    // on crée l'élément gamme d'échelle
    this.scales.writeToXML(xmlDoc, pre);
    // on crée l'élément indice-confiance
    Element genHauteElem = xmlDoc.createElement("confidence-rate");
    n = xmlDoc.createTextNode(confidenceRate.name());
    genHauteElem.appendChild(n);
    pre.appendChild(genHauteElem);
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

