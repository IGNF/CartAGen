package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class ConcatLineStrings {

  private static final Logger LOGGER = Logger.getLogger(ConcatLineStrings.class
      .getName());

  private double threshold = 0.5;

  public ILineString concatLineStrings(List<ILineString> lineStrings) {
    List<IDirectPosition> positions = new ArrayList<IDirectPosition>();

    // for (IRouteSection section : routeSections) {
    for (ILineString sectionGeom : lineStrings) {
      if (sectionGeom == null) {
        continue;
      }

      // LOGGER.debug("sectionGeom " + sectionGeom);
      if (positions.isEmpty()) {
        // LOGGER.debug("first ");
        positions.addAll(sectionGeom.coord());
      } else {
        double distFF = sectionGeom.coord().get(sectionGeom.coord().size() - 1)
            .distance(positions.get(positions.size() - 1));

        double distIF = sectionGeom.coord().get(0)
            .distance(positions.get(positions.size() - 1));

        double distFI = sectionGeom.coord().get(sectionGeom.coord().size() - 1)
            .distance(positions.get(0));

        double distII = sectionGeom.coord().get(0).distance(positions.get(0));
        if (distFF < distIF && distFF < distFI && distFF < distII) {

          // if the last element of the list is the same than the last element
          // of
          // the new section : inverse the new section

          // LOGGER.debug("last and last ");
          positions.addAll(sectionGeom.coord().reverse());
        } else if (distIF < distFF && distIF < distFI && distIF < distII) {

          // LOGGER.debug("first and last ");
          positions.addAll(sectionGeom.coord());
        } else {
          Collections.reverse(positions);
          if (distFI < distFF && distFI < distIF && distFI < distII) {

            // LOGGER.debug("last and first ");
            positions.addAll(sectionGeom.coord().reverse());
          } else if (distII < distFF && distII < distIF && distII < distFI) {

            // LOGGER.debug("first and first ");
            positions.addAll(sectionGeom.coord());
          } else {
            positions.addAll(sectionGeom.coord());

            LOGGER.error("nothing, positions : first : " + positions.get(0)
                + " last : " + positions.get(positions.size() - 1)
                + ", section : " + sectionGeom);
          }
        }
      }
    }
    return new GM_LineString(positions);
  }
}
