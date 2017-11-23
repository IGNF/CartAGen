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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class SectionPart {

  /**
   * The section this part belongs to.
   */
  private SinuousSection section;

  private ILineString geom;

  public SectionPart(ILineString geom) {
    super();
    this.geom = geom;
  }

  public SectionPart(SinuousSection section, ILineString geom) {
    super();
    this.section = section;
    this.geom = geom;
  }

  public SinuousSection getSection() {
    return section;
  }

  public void setSection(SinuousSection section) {
    this.section = section;
  }

  public ILineString getGeom() {
    return geom;
  }

  public void setGeom(ILineString geom) {
    this.geom = geom;
  }
}
