package fr.ign.cogit.cartagen.algorithms.urbanalignments;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/*
 * ###### IGN / CartAGen ###### Title: StraightAlignmentsDetection Description:
 * Recenter and merge of two overlapping urban alignments Author: J. Renard
 * Date: 09/2011
 */

public class EnsureAlignmentHomogeneousSpatialRepartition {

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
  public EnsureAlignmentHomogeneousSpatialRepartition(IUrbanAlignment align) {
    this.align = align;
  }

  /**
   * Ensures an homogeneous spatial repartition for the components of the
   * alignment by moving them along the shape line
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

    if (nonDeletedComponents.size() == 0) {
      return;
    }

    // The distance to respect between each building centroid
    double centroidDistance = this.align.getShapeLine().length()
        / (nonDeletedComponents.size() - 1);

    // Initialisation of the loop variables
    IDirectPosition lastCentroid = this.align.getShapeLine().coord().get(0);
    int lastVertexPosition = 0;

    // Displacement of the first building
    nonDeletedComponents.get(0).displaceAndRegister(
        lastCentroid.getX()
            - nonDeletedComponents.get(0).getGeom().centroid().getX(),
        lastCentroid.getY()
            - nonDeletedComponents.get(0).getGeom().centroid().getY());

    // Displacement of following buildings
    for (int i = 1; i < nonDeletedComponents.size() - 1; i++) {

      double distance = 0.0;
      for (int j = lastVertexPosition; j < this.align.getShapeLine().coord()
          .size(); j++) {

        if (j == lastVertexPosition) {
          distance += lastCentroid.distance(this.align.getShapeLine().coord()
              .get(j + 1));
        } else {
          distance += this.align.getShapeLine().coord().get(j).distance(
              this.align.getShapeLine().coord().get(j + 1));
        }

        // When the distance between building centroids is reached, the new
        // centroid position
        // must be computed. It is situated between the last two vertices
        if (distance >= centroidDistance) {

          // Computation of the new centroid position using Thales
          double ratioThales = (distance - centroidDistance)
              / (this.align.getShapeLine().coord().get(j).distance(this.align
                  .getShapeLine().coord().get(j + 1)));
          double dx = ratioThales
              * (this.align.getShapeLine().coord().get(j + 1).getX() - this.align
                  .getShapeLine().coord().get(j).getX());
          double dy = ratioThales
              * (this.align.getShapeLine().coord().get(j + 1).getY() - this.align
                  .getShapeLine().coord().get(j).getY());
          lastCentroid = new DirectPosition(this.align.getShapeLine().coord()
              .get(j + 1).getX()
              - dx, this.align.getShapeLine().coord().get(j + 1).getY() - dy);
          lastVertexPosition = j;

          // Building displacement
          nonDeletedComponents.get(i).displaceAndRegister(
              lastCentroid.getX()
                  - nonDeletedComponents.get(i).getGeom().centroid().getX(),
              lastCentroid.getY()
                  - nonDeletedComponents.get(i).getGeom().centroid().getY());

          // Overloop to the next building of the alignment
          break;
        }

      }

      // Displacement of the last building
      nonDeletedComponents.get(nonDeletedComponents.size() - 1)
          .displaceAndRegister(
              this.align.getShapeLine().coord().get(
                  this.align.getShapeLine().coord().size() - 1).getX()
                  - nonDeletedComponents.get(nonDeletedComponents.size() - 1)
                      .getGeom().centroid().getX(),
              this.align.getShapeLine().coord().get(
                  this.align.getShapeLine().coord().size() - 1).getY()
                  - nonDeletedComponents.get(nonDeletedComponents.size() - 1)
                      .getGeom().centroid().getY());

    }

    this.align.computeShapeLine();

  }

}
