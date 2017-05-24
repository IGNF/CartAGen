package fr.ign.cogit.cartagen.algorithms.urbanalignments;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;

/*
 * ###### IGN / CartAGen ###### Title: StraightAlignmentsDetection Description:
 * Recenter and merge of two overlapping urban alignments Author: J. Renard
 * Date: 09/2011
 */

public class DeleteAlignmentSmallestBuilding {

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
  public DeleteAlignmentSmallestBuilding(IUrbanAlignment align) {
    this.align = align;
  }

  /**
   * Deletes the smallest building of the alignment to make some place
   */
  public void compute() {

    double minArea = Double.MAX_VALUE, area;
    IUrbanElement minBuild = null;
    for (IUrbanElement ag : this.align.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      area = ag.getGeom().area();
      if (area < minArea) {
        minArea = area;
        minBuild = ag;
      }
    }
    if (minBuild != null) {
      minBuild.eliminate();
    }

    EnsureAlignmentHomogeneousSpatialRepartition algo = new EnsureAlignmentHomogeneousSpatialRepartition(
        this.align);
    algo.compute();
    this.align.computeShapeLine();

  }

}
