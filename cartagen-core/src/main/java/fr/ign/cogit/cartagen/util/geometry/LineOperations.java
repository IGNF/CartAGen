package fr.ign.cogit.cartagen.util.geometry;

import java.util.List;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * This class contains static geometrical operations for LineString geometries.
 * 
 * @author GTouya
 *
 */
public class LineOperations {

	private static Logger logger = Logger.getLogger(LineOperations.class.getName());

	/**
	 * Mise bout à bout de plusieurs GM_LineString pour constituer une nouvelle
	 * GM_LineString La liste en entrée contient des GM_LineString. La polyligne
	 * créée commence sur l'extrémité libre de la première polyligne de la liste.
	 * <p>
	 * English: Combination of lines.
	 * 
	 * @param geometries : Linestrings à fusionner
	 * @param tolerance  :distance minimale à laquelle on considère 2 points
	 *                   superposés.
	 */
	public static ILineString compileArcs2D(List<ILineString> geometries) {

		logger.fine("compile geometries");
		for (ILineString l : geometries) {
			logger.fine("\t" + l);
		}
		IDirectPositionList finalPoints = new DirectPositionList();
		if (geometries.isEmpty()) {
			logger.severe("ATTENTION. Erreur à la compilation de lignes : aucune ligne en entrée");
			return null;
		}
		ILineString currentLine = geometries.get(0);
		if (geometries.size() == 1) {
			return currentLine;
		}
		ILineString nextLine = geometries.get(1);
		IDirectPosition currentPoint = null;
		if (currentLine.startPoint().equals2D(nextLine.startPoint())
				|| currentLine.startPoint().equals2D(nextLine.endPoint())) {
			// premier point = point finale de la premiere ligne
			finalPoints.addAll(((ILineString) currentLine.reverse()).getControlPoint());
			currentPoint = currentLine.startPoint();
		} else if (currentLine.endPoint().equals2D(nextLine.startPoint())
				|| currentLine.endPoint().equals2D(nextLine.endPoint())) {
			// premier point = point initial de la premiere ligne
			finalPoints.addAll(currentLine.getControlPoint());
			currentPoint = currentLine.endPoint();
		} else {
			logger.severe("ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
			for (ILineString l : geometries) {
				logger.severe(l.toString());
			}

			return null;
		}
		logger.fine("currentPoint = " + currentPoint.toGM_Point());
		for (int i = 1; i < geometries.size(); i++) {
			nextLine = geometries.get(i);
			logger.fine("copying " + nextLine.getControlPoint().size() + " = " + nextLine);
			ILineString lineCopy = new GM_LineString(nextLine.getControlPoint());
			if (currentPoint.equals2D(nextLine.startPoint())) {
				// LSSuivante dans le bon sens
				lineCopy.removeControlPoint(lineCopy.startPoint());
				finalPoints.addAll(lineCopy.getControlPoint());
				currentPoint = lineCopy.endPoint();
			} else if (currentPoint.equals2D(nextLine.endPoint())) {
				// LSSuivante dans le bon sens
				IDirectPosition toRemove = lineCopy.endPoint();
				ILineString reverse = (ILineString) lineCopy.reverse();
				reverse.removeControlPoint(toRemove);
				finalPoints.addAll(reverse.getControlPoint());
				currentPoint = lineCopy.startPoint();
			} else {
				logger.severe(
						"ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
				for (ILineString l : geometries) {
					logger.severe(l.toString());
				}
				return null;
			}
		}
		logger.fine("new line with " + finalPoints.size());
		return new GM_LineString(finalPoints, false);
	}

}
