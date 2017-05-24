package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;

public abstract class CollaGenRelation extends AbstractFeature {

  protected IGeneObj obj1;
  protected IGeneObj obj2;
  private GeographicRelation concept;

  public CollaGenRelation(IGeneObj obj1, IGeneObj obj2,
      GeographicRelation concept) {
    this.obj1 = obj1;
    this.obj2 = obj2;
    this.concept = concept;
  }

  public IGeneObj getObj1() {
    return obj1;
  }

  public void setObj1(IGeneObj obj1) {
    this.obj1 = obj1;
  }

  public IGeneObj getObj2() {
    return obj2;
  }

  public void setObj2(IGeneObj obj2) {
    this.obj2 = obj2;
  }

  public void setConcept(GeographicRelation concept) {
    this.concept = concept;
  }

  public GeographicRelation getConcept() {
    return concept;
  }

  /**
   * Détermine la qualité de la relation sur une échelle de 1 (mauvaise) à 5. La
   * qualité de la relation exprime le fait que les objets sont en relation sans
   * ambiguité. Par exemple, très proches pour une relation de proximité,
   * parallèles pour une relation d'orientation relative.
   * @return
   */
  public abstract int qualiteRelation();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((concept == null) ? 0 : concept.hashCode());
    result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
    result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CollaGenRelation other = (CollaGenRelation) obj;
    if (concept == null) {
      if (other.concept != null)
        return false;
    } else if (!concept.equals(other.concept))
      return false;
    if (obj1 == null) {
      if (other.obj1 != null)
        return false;
    } else if (!obj1.equals(other.obj1))
      return false;
    if (obj2 == null) {
      if (other.obj2 != null)
        return false;
    } else if (!obj2.equals(other.obj2))
      return false;
    return true;
  }

}
