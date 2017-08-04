/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.algorithms.polygon;

import java.util.Set;

import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridCell;
import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridTessellation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * Implementation of the hexagonal tessellation line simplification algorithm
 * from (Raposo 2010). It's the same principle as Li-Openshaw algorithm but with
 * hexagonal tessellation instead of a grid. All options from the paper are
 * implemented. Be careful, it uses the scale as parameter.
 * 
 * @author GTouya
 * 
 */
public class LiOpenshawSimplification {

    private boolean method1 = false, toblerRes = false;
    private Double initialScale;
    private GridTessellation<Integer> tess;

    /**
    *
     */
    public LiOpenshawSimplification(boolean method1, boolean toblerRes, Double initialScale) {
        this.method1 = method1;
        this.toblerRes = toblerRes;
        this.initialScale = initialScale;
    }

    /**
     * Triggers the Raposo simplification on the line given as parameter. The
     * options provide the degree of generalisation.
     * 
     * @param line
     * @return
     */
    public ILineString simplify(ILineString line) {
        IDirectPositionList points = new DirectPositionList();
        // compute the hexagonal tessellation for this line
        // first, the width of a cell that depends on the options of the
        // algorithm.
        double width = 0.0;// FIXME

        // the envelope of the line is computed to delimit the tessallation.
        IEnvelope envelope = line.getEnvelope();
        tess = new GridTessellation<Integer>(envelope, width);
        int currentIndex = 0;
        GridCell<Integer> prevCell = null;
        while (currentIndex < line.coord().size() - 1) {
            GridCell<Integer> current = null;
            // now loop on the vertices from current index
            // builds a point cloud as a multi-point geometry with all line
            // vertices
            // contained in the current cell.
            IMultiPoint ptCloud = new GM_MultiPoint();
            for (int i = currentIndex; i < line.coord().size(); i++) {
                Set<GridCell<Integer>> containingCells = tess.getContainingCells(line.coord().get(i));
                if (current == null) {
                    containingCells.remove(prevCell);
                    current = containingCells.iterator().next();
                    ptCloud.add(line.coord().get(i).toGM_Point());
                    continue;
                }
                if (containingCells.contains(current)) {
                    ptCloud.add(line.coord().get(i).toGM_Point());
                    currentIndex = i + 1;
                } else {
                    currentIndex = i;
                    prevCell = current;
                    break;
                }
            }
            if (method1)
                // get the centroid of the point cloud to replace all the
                // vertices
                points.add(ptCloud.centroid());
            else {
                // in this option, the vertices are replaced by the nearest
                // point in the
                // line of the centroid of the point cloud.
                points.add(CommonAlgorithmsFromCartAGen.getNearestVertexFromPoint(line, ptCloud.centroid()));
            }
            if (currentIndex == line.coord().size() - 1)
                points.add(line.endPoint());
        }

        // on ajoute le point initial et le point final s'ils n'y sont pas
        if (!points.get(0).equals(line.startPoint()))
            points.add(0, line.startPoint());
        if (!points.get(points.size() - 1).equals(line.endPoint()))
            points.add(line.endPoint());
        return new GM_LineString(points);
    }

    /**
     * Raposo simplification algorithm for a polygon. The algorithm is applied
     * on outer ring and on each inner ring.
     * 
     * @param polygon
     * @return
     */
    public IPolygon simplify(IPolygon polygon) {
        IPolygon newPol = new GM_Polygon(simplify(polygon.exteriorLineString()));
        for (int i = 0; i < polygon.getInterior().size(); i++) {
            ILineString hole = simplify(polygon.interiorLineString(i));
            newPol.addInterior(new GM_Ring(hole));
        }

        return newPol;
    }

    public GridTessellation<Integer> getTess() {
        return tess;
    }

    public void setTess(GridTessellation<Integer> tess) {
        this.tess = tess;
    }
}
