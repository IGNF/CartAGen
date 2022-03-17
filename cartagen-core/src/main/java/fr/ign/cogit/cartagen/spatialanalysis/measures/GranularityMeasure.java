package fr.ign.cogit.cartagen.spatialanalysis.measures;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * This class contains several measures for the granularity of a line or a
 * polygon.
 * 
 * @author GTouya
 *
 */
public class GranularityMeasure {

	private ILineString linework;

	public ILineString getLinework() {
		return linework;
	}

	public void setLinework(ILineString linework) {
		this.linework = linework;
	}

	public GranularityMeasure(ILineString linework) {
		super();
		this.linework = linework;
	}

	/**
	 * gives the width of the minimum protrusion in the linework. The measure is
	 * inspired from the Perkal Disk measure proposed by Nakos et al. (2008).
	 * 
	 * @param perkalStep
	 * @return
	 */
	public double minProtrusionWidth(double perkalStep) {
		double minWidth = linework.length();
		double radius = perkalStep;

		// iteratively increase the radius as long as it is not too big and protrusion
		// has not been found
		while (radius < linework.length() / 5) {
			// create a buffer of the linework
			IPolygon buffer = (IPolygon) linework.buffer(radius);
			// get the outer ring of the buffer
			ILineString outerBuffer = buffer.exteriorLineString();
			IPolygon secondBuffer = (IPolygon) outerBuffer.buffer(radius);
			// get the largest inner hole of the second buffer
			if (secondBuffer.getInterior().size() == 0) {
				radius += perkalStep;
				continue;
			}
			double maxHole = 0.0;
			for (IRing hole : secondBuffer.getInterior()) {
				IPolygon polHole = new GM_Polygon(hole);
				if (polHole.area() > maxHole)
					maxHole = polHole.area();
			}

			if (maxHole > radius * radius / 3) {
				// the hole is significant, and there is a protrusion of width radius
				minWidth = radius;
				break;
			} else {
				// increment the radius
				radius += perkalStep;
			}
		}

		return minWidth;
	}

}
