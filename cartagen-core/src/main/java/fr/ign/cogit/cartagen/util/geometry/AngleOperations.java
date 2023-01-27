package fr.ign.cogit.cartagen.util.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class AngleOperations {

	/**
	 * Computes the angle between two crossing lines. The angle is not directly
	 * computed between the first vertices after the crossing to avoid capture
	 * artifacts. Only considers the X,Y coordinates of the lines/points
	 * 
	 * @param line1 the first line of the angle
	 * @param line2 the second line of the angle
	 * @return an Angle object between [0, Pi]
	 * @author GTouya
	 */
	public static double angleBetween2Lines2D(ILineString line1, ILineString line2) {
		// search for the intersection point between the two geometries
		DirectPosition coordIni1 = (DirectPosition) line1.startPoint();
		DirectPosition coordFin1 = (DirectPosition) line1.endPoint();
		DirectPosition coordIni2 = (DirectPosition) line2.startPoint();
		DirectPosition coordFin2 = (DirectPosition) line2.endPoint();
		boolean interGeom1 = true, interGeom2 = true;

		DirectPosition coordInter = null;
		if (coordIni2.equals2D(coordIni1)) {
			coordInter = coordIni1;
			interGeom1 = true;
			interGeom2 = true;
		}
		if (coordFin2.equals2D(coordIni1)) {
			coordInter = coordIni1;
			interGeom1 = true;
			interGeom2 = false;
		}
		if (coordFin2.equals2D(coordFin1)) {
			coordInter = coordFin1;
			interGeom1 = false;
			interGeom2 = false;
		}
		if (coordIni2.equals2D(coordFin1)) {
			coordInter = coordIni2;
			interGeom1 = false;
			interGeom2 = true;
		}

		// if there is a topological problem, return false
		if (coordInter == null) {
			return 0.0;
		}

		// count vertices in each geometry : indeed, if one or the other has only 2
		// vertices, the angle continuity cannot be tested.
		int nbVert1 = line1.numPoints();
		int nbVert2 = line2.numPoints();

		// define the nodes to compute the angle
		DirectPosition v1 = null, v2 = null;

		// if nbVert1 > 2, get the second vertex in geometry 1
		if (nbVert1 > 2) {
			if (interGeom1) {
				v1 = (DirectPosition) line1.coord().get(2);
			} else {
				v1 = (DirectPosition) line1.coord().get(nbVert1 - 3);
			}
		} else {

			// get the first vertex on geometry 1
			if (interGeom1) {
				v1 = (DirectPosition) line1.coord().get(1);
			} else {
				v1 = (DirectPosition) line1.coord().get(nbVert1 - 2);
			}
		}

		// si nbVert2 > 2, get the second vertex in geometry 2
		if (nbVert2 > 2) {
			if (interGeom2) {
				v2 = (DirectPosition) line2.coord().get(2);
			} else {
				v2 = (DirectPosition) line2.coord().get(nbVert2 - 3);
			}
		} else {
			// get the first vertex on geometry 2
			if (interGeom2) {
				v2 = (DirectPosition) line2.coord().get(1);
			} else {
				v2 = (DirectPosition) line2.coord().get(nbVert2 - 2);
			}
		}

		// now, compute interAngle between geom and geomFoll
		double angle = Angle.angleTroisPoints(v1, coordInter, v2).getValeur();
		if (angle > Math.PI) {
			angle = 2 * Math.PI - angle;
		}
		return angle;
	}

}
