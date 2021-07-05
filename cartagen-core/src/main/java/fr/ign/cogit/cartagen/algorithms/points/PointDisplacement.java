/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.algorithms.points;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * 
 * Algorithm that displaces a set of point features based on a stochastic
 * gradient descent. The principle is as follows: 1. randomly select one of the
 * point features; 2. randomly displace this point feature; 3. check that this
 * displacement improved the proximity constraints of the point (the area of the
 * symbol overlap is actually checked). The process is repeated until all point
 * features are far enough from each other. This algorithm is adapted from the
 * displacement algorithm for buildings proposed by J. Gaffuri.
 * 
 * @author JGaffuri
 * @author GTouya
 * 
 */
public class PointDisplacement {
	private static Logger logger = Logger.getLogger(PointDisplacement.class.getName());

	private static double facteurLongueurDeplacement = 2;
	private static int nbIterations = 5;

	/**
	 * 
	 * @param points            the set of point features
	 * @param minSep
	 * @param displacementRatio
	 */
	public static void compute(IFeatureCollection<IGeneObjPoint> points, double minSep, double displacementRatio) {

		// compute the overlap rate of the point symbols in the point set
		double overlapRate = computeOverlapRate(points, minSep);

		// set the maximum number of iterations of the algorithm.
		int tryMax = nbIterations * points.size();
		// initialise the number of tries
		int nbTries = 0;

		// main loop of the algorithm: as long as there is overlap, displace the points,
		// until the maximum
		// number of iterations is reached.
		while (overlapRate > 0 && nbTries < tryMax) {

			// randomly select a point from the set
			IGeneObjPoint point = points.get((int) (Math.random() * points.size()));

			// check that the point is not already deleted
			if (point.isDeleted()) {
				continue;
			}

			// compute the overlap rate of this point
			double pointOverlapRate = computeOverlapRatePoint(point, points, minSep);

			// if this point does not overlap the others, continue
			if (pointOverlapRate == 0.0) {
				continue;
			}

			// compute a random direction for the displacement
			double angle = Math.random() * 2 * Math.PI;

			// compute the displacement length
			double lg = Math.random() * facteurLongueurDeplacement * displacementRatio;
			double dx = Math.cos(angle) * lg;
			double dy = Math.sin(angle) * lg;

			// do the translation of the point
			point.setGeom(new DirectPosition(point.getGeom().getPosition().getX() + dx,
					point.getGeom().getPosition().getY() + dy).toGM_Point());

			// compute the new value of overlap rate
			double newOverlapRate = computeOverlapRate(points, minSep);

			// cancel displacement if overlap rate is worse, or if the point is too far from
			// its initial position
			if (newOverlapRate >= overlapRate) {

				// cancel displacement
				point.setGeom(new DirectPosition(point.getGeom().getPosition().getX() - dx,
						point.getGeom().getPosition().getY() - dy).toGM_Point());

				nbTries++;
				continue;
			}

			// arrived here, the displacement is validated
			nbTries = 0;
			overlapRate = newOverlapRate;
			if (logger.isTraceEnabled()) {
				logger.trace("taux=" + overlapRate);
			}
		}
	}

	private static double computeOverlapRate(IFeatureCollection<? extends IGeneObjPoint> points, double minSep) {
		double totalArea = 0.0;

		for (IGeneObjPoint point : points)
			totalArea += computeOverlapRatePoint(point, points, minSep);

		return totalArea / points.size();
	}

	private static double computeOverlapRatePoint(IGeneObjPoint point,
			IFeatureCollection<? extends IGeneObjPoint> points, double minSep) {
		if (point.isDeleted()) {
			return 0.0;
		}

		double overlapArea = 0.0;

		IGeometry geomSymbol = point.getSymbolGeom();
		IGeometry bufferedSymbol = geomSymbol.buffer(minSep);

		for (IGeneObjPoint neighbour : points) {
			if (neighbour.equals(point))
				continue;
			IGeometry neighbourGeom = neighbour.getSymbolGeom();
			IGeometry intersection = bufferedSymbol.intersection(neighbourGeom);
			if (intersection == null)
				continue;
			overlapArea += intersection.area();
		}

		return overlapArea;
	}

}
