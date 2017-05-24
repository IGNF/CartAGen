package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

/**
 * Compute the intersections between the buffer of a given line and a set of
 * object. For each object computed, get the min distance between the object and
 * the original line
 * @author AMaudet
 * 
 */

public class IntersectionMeasures {

  private IGeneObjLin section;
  private double distance;
  private Set<IGeneObjSurf> objects;

  public IntersectionMeasures(IGeneObjLin section, double distance,
      Set<IGeneObjSurf> objects) {
    this.section = section;
    this.distance = distance;
    this.objects = objects;
  }

  public void compute() {

  }

}
