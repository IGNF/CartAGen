package fr.ign.cogit.cartagen.algorithms.urbanalignments;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;

/*
 * ###### IGN / CartAGen ###### Title: StraightAlignmentsDetection Description:
 * Recenter and merge of two overlapping urban alignments Author: J. Renard
 * Date: 09/2011
 */

public class CorrectAlignmentBuildingsOrientation {

  /**
   * The alignment that needs to be corrected
   */
  private IUrbanAlignment align;

  public IUrbanAlignment getAlign() {
    return this.align;
  }

  public void setAlign(IUrbanAlignment align) {
    this.align = align;
  }

  /**
   * Constructor
   * @param align
   */
  public CorrectAlignmentBuildingsOrientation(IUrbanAlignment align) {
    this.align = align;
  }

  /**
   * Rotates the buildings of the alignment to ensure ideal inner orientation
   */
  public void compute() {

    // Initialisation of the shape line and the non deleted components of the
    // alignment
    List<IUrbanElement> nonDeletedComponents = new ArrayList<IUrbanElement>();
    for (IUrbanElement urbanElement : this.align.getUrbanElements()) {
      if (!urbanElement.isDeleted()) {
        nonDeletedComponents.add(urbanElement);
      }
    }

    for (int i = 1; i < nonDeletedComponents.size(); i++) {
      // current orientation
      double orientation = new OrientationMeasure(nonDeletedComponents.get(i)
          .getGeom()).getGeneralOrientation();
      // ideal orientation
      Angle idealAngle = new Angle();
      if (i == 0) {
        idealAngle = new Angle(
            nonDeletedComponents.get(0).getGeom().centroid(),
            nonDeletedComponents.get(0).getGeom().centroid());
      } else if (i == nonDeletedComponents.size() - 1) {
        idealAngle = new Angle(nonDeletedComponents.get(i - 1).getGeom()
            .centroid(), nonDeletedComponents.get(i).getGeom().centroid());
      } else {
        idealAngle = new Angle(nonDeletedComponents.get(i - 1).getGeom()
            .centroid(), nonDeletedComponents.get(i + 1).getGeom().centroid());
      }
      double idealOrientation = idealAngle.getValeur();
      // rotation of the urban element
      IPolygon newGeom = CommonAlgorithms.rotation(
          (IPolygon) nonDeletedComponents.get(i).getGeom(), idealOrientation
              - orientation);
      nonDeletedComponents.get(i).setGeom(newGeom);
    }

  }

}
