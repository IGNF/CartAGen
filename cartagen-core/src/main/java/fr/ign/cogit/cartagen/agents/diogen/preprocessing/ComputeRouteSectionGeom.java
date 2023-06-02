package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IRouteSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class ComputeRouteSectionGeom {

  private static final Logger LOGGER = LogManager
      .getLogger(ComputeRouteSectionGeom.class.getName());

  /**
   * Compute a new geom for section in order to allow continuity with next and
   * previous (if next and previous a modified by the same algorithm).
   * @param section
   * @param previous
   * @param next
   * @return
   */
  public ILineString computeRouteSectionGeom(IRouteSection section,
      IRouteSection previous, IRouteSection next) {

    // Get the offset of section
    ILineString tempGeom = section.getOffsetGeom();
    if (tempGeom == null) {
      return null;
    }

    JtsAlgorithms jts = new JtsAlgorithms();

    if (previous != null) {
      // Get the offset geom of previous
      ILineString otherTempGeom = previous.getOffsetGeom();
      if (otherTempGeom != null) {

        // if intersection between the two linestring, we had a new point and
        // suppress points beyond.
        // test intersection
        IGeometry intersection = jts.intersection(otherTempGeom, tempGeom);
        // if multiple intersection
        if (intersection instanceof IPoint) {
          IPoint point = (IPoint) intersection;
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int posi = Operateurs
              .projectAndInsertWithPosition(point.getPosition(), myLinePoints);
          for (int k = 0; k < posi; k++) {
            myLinePoints.remove(0);
          }
        } else if (intersection instanceof IMultiPoint) {
          // if multiple intersection, we take the nearest of the common node.
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int minPosi = Integer.MAX_VALUE;

          for (IPoint point : ((IMultiPoint) intersection).getList()) {
            int posi = Operateurs.projectAndInsertWithPosition(
                point.getPosition(), myLinePoints);
            if (posi < minPosi) {
              minPosi = posi;
            }
          }
          for (int k = 0; k < minPosi; k++) {
            myLinePoints.remove(0);
          }

          // tempGeom = new GM_LineString(myLinePoints);
        } else if (intersection instanceof ILineString) {
          // if intersection is a line string, this may be an empty linestring
          // (no intersection).
          // We had a new point at the same distance of the extremity of the two
          // linestrings.
          ILineString line = (ILineString) intersection;
          if (line.sizeControlPoint() == 0) {
            IDirectPosition point1 = null;

            point1 = tempGeom.getControlPoint(0);
            IDirectPosition point2A = otherTempGeom.getControlPoint(0);
            IDirectPosition point2B = otherTempGeom
                .getControlPoint(otherTempGeom.sizeControlPoint() - 1);
            IDirectPosition point2 = null;
            if (point2A.distance(
                section.getInitialNode().getGeom().getPosition()) > point2B
                    .distance(
                        section.getInitialNode().getGeom().getPosition())) {
              point2 = point2B;
            } else {
              point2 = point2A;
            }
            IDirectPosition directPosition = new DirectPosition(
                (point2.getX() + point1.getX()) / 2,
                (point2.getY() + point1.getY()) / 2);

            tempGeom.coord().getList().add(0, directPosition);
          } else {
            LOGGER.error(
                "Intersection of two line string resulting in another line string.");
          }
        }
      }
    }

    if (next != null) {

      // Get the geom.
      ILineString otherTempGeom = next.getOffsetGeom();

      if (otherTempGeom != null) {

        // test intersection
        IGeometry intersection = jts.intersection(otherTempGeom, tempGeom);
        // if multiple intersection

        if (intersection instanceof IPoint) {

          tempGeom.coord().inverseOrdre();

          // Project intersection point
          IPoint point = (IPoint) intersection;
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int posi = Operateurs
              .projectAndInsertWithPosition(point.getPosition(), myLinePoints);
          for (int k = 0; k < posi; k++) {
            myLinePoints.remove(0);
          }

          tempGeom.coord().inverseOrdre();

        } else if (intersection instanceof IMultiPoint) {

          tempGeom.coord().inverseOrdre();
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          // double minCurvAbsc = Double.MAX_VALUE;
          int minPosi = Integer.MAX_VALUE;

          for (IPoint point : ((IMultiPoint) intersection).getList()) {
            int posi = Operateurs.projectAndInsertWithPosition(
                point.getPosition(), myLinePoints);
            if (posi < minPosi) {
              minPosi = posi;
            }
          }
          for (int k = 0; k < minPosi; k++) {
            myLinePoints.remove(0);
          }
          // tempGeom = new GM_LineString(myLinePoints);

          tempGeom.coord().inverseOrdre();

        }

        if (intersection instanceof ILineString) {
          ILineString line = (ILineString) intersection;
          if (line.sizeControlPoint() == 0) {
            IDirectPosition point1 = null;

            point1 = tempGeom.getControlPoint(tempGeom.sizeControlPoint() - 1);

            IDirectPosition point2A = otherTempGeom.getControlPoint(0);
            IDirectPosition point2B = otherTempGeom
                .getControlPoint(otherTempGeom.sizeControlPoint() - 1);
            IDirectPosition point2 = null;

            if (point2A.distance(
                section.getFinalNode().getGeom().getPosition()) > point2B
                    .distance(section.getFinalNode().getGeom().getPosition())) {
              point2 = point2B;
            } else {
              point2 = point2A;
            }

            IDirectPosition directPosition = new DirectPosition(
                (point2.getX() + point1.getX()) / 2,
                (point2.getY() + point1.getY()) / 2);
            tempGeom.coord().getList().add(directPosition);

          } else {
            LOGGER.error(
                "Intersection of two line string resulting in another line string.");
          }
        }
      }
    }
    return tempGeom;
  }

  /**
   * 
   * @param section
   * @param previous
   * @param next
   * @return
   */
  public IPolygon computeRouteSectionPolygon(IRouteSection section,
      IRouteSection previous, IRouteSection next) {

    // Get the offset of section
    ILineString tempGeom = section.getOffsetGeomForPolygon();
    if (tempGeom == null) {
      return null;
    }

    JtsAlgorithms jts = new JtsAlgorithms();

    if (previous != null) {
      // Get the offset geom of previous
      ILineString otherTempGeom = previous.getOffsetGeomForPolygon();
      if (otherTempGeom != null) {

        // if intersection between the two linestring, we had a new point and
        // suppress points beyond.
        // test intersection
        IGeometry intersection = jts.intersection(otherTempGeom, tempGeom);
        // if multiple intersection
        if (intersection instanceof IPoint) {
          IPoint point = (IPoint) intersection;
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int posi = Operateurs
              .projectAndInsertWithPosition(point.getPosition(), myLinePoints);
          for (int k = 0; k < posi; k++) {
            myLinePoints.remove(0);
          }
        } else if (intersection instanceof IMultiPoint) {
          // if multiple intersection, we take the nearest of the common node.
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int minPosi = Integer.MAX_VALUE;

          for (IPoint point : ((IMultiPoint) intersection).getList()) {
            int posi = Operateurs.projectAndInsertWithPosition(
                point.getPosition(), myLinePoints);
            if (posi < minPosi) {
              minPosi = posi;
            }
          }
          for (int k = 0; k < minPosi; k++) {
            myLinePoints.remove(0);
          }

          // tempGeom = new GM_LineString(myLinePoints);
        } else if (intersection instanceof ILineString) {
          // if intersection is a line string, this may be an empty linestring
          // (no intersection).
          // We had a new point at the same distance of the extremity of the two
          // linestrings.
          ILineString line = (ILineString) intersection;
          if (line.sizeControlPoint() == 0) {
            IDirectPosition point1 = null;

            point1 = tempGeom.getControlPoint(0);
            IDirectPosition point2A = otherTempGeom.getControlPoint(0);
            IDirectPosition point2B = otherTempGeom
                .getControlPoint(otherTempGeom.sizeControlPoint() - 1);
            IDirectPosition point2 = null;
            if (point2A.distance(
                section.getInitialNode().getGeom().getPosition()) > point2B
                    .distance(
                        section.getInitialNode().getGeom().getPosition())) {
              point2 = point2B;
            } else {
              point2 = point2A;
            }
            IDirectPosition directPosition = new DirectPosition(
                (point2.getX() + point1.getX()) / 2,
                (point2.getY() + point1.getY()) / 2);

            tempGeom.coord().getList().add(0, directPosition);
          } else {
            LOGGER.error(
                "Intersection of two line string resulting in another line string.");
          }
        }
      }
    }

    if (next != null) {

      // Get the geom.
      ILineString otherTempGeom = next.getOffsetGeomForPolygon();

      if (otherTempGeom != null) {

        // test intersection
        IGeometry intersection = jts.intersection(otherTempGeom, tempGeom);
        // if multiple intersection

        if (intersection instanceof IPoint) {

          tempGeom.coord().inverseOrdre();

          // Project intersection point
          IPoint point = (IPoint) intersection;
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          int posi = Operateurs
              .projectAndInsertWithPosition(point.getPosition(), myLinePoints);
          for (int k = 0; k < posi; k++) {
            myLinePoints.remove(0);
          }

          tempGeom.coord().inverseOrdre();

        } else if (intersection instanceof IMultiPoint) {

          tempGeom.coord().inverseOrdre();
          List<IDirectPosition> myLinePoints = tempGeom.coord().getList();
          // double minCurvAbsc = Double.MAX_VALUE;
          int minPosi = Integer.MAX_VALUE;

          for (IPoint point : ((IMultiPoint) intersection).getList()) {
            int posi = Operateurs.projectAndInsertWithPosition(
                point.getPosition(), myLinePoints);
            if (posi < minPosi) {
              minPosi = posi;
            }
          }
          for (int k = 0; k < minPosi; k++) {
            myLinePoints.remove(0);
          }
          // tempGeom = new GM_LineString(myLinePoints);

          tempGeom.coord().inverseOrdre();

        }

        if (intersection instanceof ILineString) {
          ILineString line = (ILineString) intersection;
          if (line.sizeControlPoint() == 0) {
            IDirectPosition point1 = null;

            point1 = tempGeom.getControlPoint(tempGeom.sizeControlPoint() - 1);

            IDirectPosition point2A = otherTempGeom.getControlPoint(0);
            IDirectPosition point2B = otherTempGeom
                .getControlPoint(otherTempGeom.sizeControlPoint() - 1);
            IDirectPosition point2 = null;

            if (point2A.distance(
                section.getFinalNode().getGeom().getPosition()) > point2B
                    .distance(section.getFinalNode().getGeom().getPosition())) {
              point2 = point2B;
            } else {
              point2 = point2A;
            }

            IDirectPosition directPosition = new DirectPosition(
                (point2.getX() + point1.getX()) / 2,
                (point2.getY() + point1.getY()) / 2);
            tempGeom.coord().getList().add(directPosition);

          } else {
            LOGGER.error(
                "Intersection of two line string resulting in another line string.");
          }
        }
      }
    }

    IMultiCurve<ILineString> multiLine = JtsAlgorithms.offsetCurve(tempGeom,
        Math.signum(section.getRoadRelativePosition()) * section.getWidth());

    if (multiLine.size() == 0) {
      return null;
    }

    ILineString offsetTempGeom = multiLine.get(0);

    for (IDirectPosition position : offsetTempGeom.coord().reverse()) {
      tempGeom.addControlPoint(position);
    }
    tempGeom.addControlPoint(tempGeom.getControlPoint(0));
    return new GM_Polygon(tempGeom);

  }
}
