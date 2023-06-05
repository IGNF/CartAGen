/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author jGaffuri
 * 
 */
public class BlocksComputationPolygonizerJTS {

  /**
   * calcul des faces d'un plat de nouilles (avec le polygonizer de JTS)
   * 
   * @param nouilles la liste des lignes
   * @return la liste des polygones
   */
  public static ArrayList<IPolygon> getFaces(ArrayList<ILineString> nouilles) {

    // construction de la liste des lignes JTS
    LineString[] lss = new LineString[nouilles.size()];
    GeometryFactory factory = new GeometryFactory();
    int i = 0;
    try {
      for (ILineString ls : nouilles) {
        lss[i++] = (LineString) AdapterFactory.toGeometry(factory, ls);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    MultiLineString mls = factory.createMultiLineString(lss);
    Geometry union = mls.union();
    mls = null;

    // calcul des polygones
    Polygonizer pg = new Polygonizer();
    pg.add(union);
    Collection<?> col = pg.getPolygons();

    // construction de la liste des polygones geoxygene
    ArrayList<IPolygon> polys = new ArrayList<IPolygon>();
    try {
      for (Object obj : col) {
        polys.add((IPolygon) AdapterFactory.toGM_Object((Polygon) obj));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return polys;
  }

}
