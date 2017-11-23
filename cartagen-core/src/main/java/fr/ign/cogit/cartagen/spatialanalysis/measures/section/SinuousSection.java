/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;

/**
 * A sinuous section is an object that describes how a sinuous line section
 * (e.g. a road, a river, a coastline) can be decomposed into
 * {@link SectionPart} instances, that can be {@link Bend} instances,
 * {@link BendSeries} instances, or simple unbended parts.
 * @author gtouya
 *
 */
public class SinuousSection {

  private List<SectionPart> sectionParts;

  /**
   * The section GeneObj feature this instance refers to.
   */
  private IGeneObjLin section;

  public SinuousSection(IGeneObjLin section) {
    super();
    this.section = section;
  }

  public IGeneObjLin getSection() {
    return section;
  }

  public void setSection(IGeneObjLin section) {
    this.section = section;
  }

  public List<SectionPart> getSectionParts() {
    return sectionParts;
  }

  public void setSectionParts(List<SectionPart> sectionParts) {
    this.sectionParts = sectionParts;
  }

}
