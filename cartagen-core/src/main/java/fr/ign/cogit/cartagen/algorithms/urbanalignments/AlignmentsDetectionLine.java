package fr.ign.cogit.cartagen.algorithms.urbanalignments;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/*
 * ###### IGN / CartAGen ###### Title: AlignmentsDetectionLine Description:
 * Straight line used to detect building straight alignments Author: J. Renard
 * Date: 03/12/2010
 */

public class AlignmentsDetectionLine {

  // The anchor point of the line (ie. its origin)
  private IDirectPosition anchor;

  public IDirectPosition getAnchor() {
    return this.anchor;
  }

  public void setAnchor(IDirectPosition anchor) {
    this.anchor = anchor;
  }

  // The angle with horizontal
  private double angle;

  public double getAngle() {
    return this.angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  // The packs of buildings grouped along the line through their projections
  private ArrayList<IFeatureCollection<IUrbanElement>> buildingPacks;

  public ArrayList<IFeatureCollection<IUrbanElement>> getBuildingPacks() {
    return this.buildingPacks;
  }

  public void setBuildingPacks(
      ArrayList<IFeatureCollection<IUrbanElement>> buildingPacks) {
    this.buildingPacks = buildingPacks;
  }

  public void addBuildingPack(IFeatureCollection<IUrbanElement> buildingPack) {
    this.buildingPacks.add(buildingPack);
  }

  /**
   * Constructor
   * @param angle
   */
  public AlignmentsDetectionLine(IDirectPosition anchor, double angle) {
    this.anchor = anchor;
    this.angle = angle;
    this.buildingPacks = new ArrayList<IFeatureCollection<IUrbanElement>>();
  }

}
