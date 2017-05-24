package fr.ign.cogit.cartagen.collagen.components.registry;

import java.util.Comparator;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;

public class DescrProcessPreComparator implements
    Comparator<ProcessCapabDescription> {

  private GeoSpaceConcept space;

  public DescrProcessPreComparator(GeoSpaceConcept space) {
    super();
    this.space = space;
  }

  @Override
  public int compare(ProcessCapabDescription arg0, ProcessCapabDescription arg1) {
    PreConditionProcess pre0 = null;
    for (PreConditionProcess p : arg0.getPreConditions()) {
      if (p.getSpace().equals(this.space)) {
        pre0 = p;
        break;
      }
    }
    PreConditionProcess pre1 = null;
    for (PreConditionProcess p : arg1.getPreConditions()) {
      if (p.getSpace().equals(this.space)) {
        pre1 = p;
        break;
      }
    }
    if (pre0 == null) {
      return -1;
    }
    return pre0.compareTo(pre1);
  }
}
