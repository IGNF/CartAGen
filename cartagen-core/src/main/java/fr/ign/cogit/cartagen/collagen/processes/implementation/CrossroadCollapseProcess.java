package fr.ign.cogit.cartagen.collagen.processes.implementation;

import fr.ign.cogit.cartagen.algorithms.section.CollapseBranchingCrossRoad;
import fr.ign.cogit.cartagen.algorithms.section.CollapseRoundabout;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.RoadNetworkSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;

public class CrossroadCollapseProcess extends GeneralisationProcess {

  private double minDiameter, minBranchArea;

  public CrossroadCollapseProcess(Conductor chefO) {
    super(chefO);
    this.setMinDiameter(100.0);
    this.setMinBranchArea(500.0);
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    if (!(space instanceof RoadNetworkSpace))
      return;
    // first get the roundabouts and the branching crossroads inside the space
    RoadNetworkSpace roadSpace = (RoadNetworkSpace) space;

    // then collapse roundabouts
    for (IRoundAbout roundAbout : roadSpace.getInsideRoundabouts()) {
      CollapseRoundabout collapseRoundabout = new CollapseRoundabout(
          minDiameter, roundAbout);
      collapseRoundabout.collapseToPoint();
    }

    // then collapse braching crossroads
    for (IBranchingCrossroad branching : roadSpace.getInsideBranchings()) {
      CollapseBranchingCrossRoad collapseBranching = new CollapseBranchingCrossRoad(
          minBranchArea, branching);
      collapseBranching.collapseToPoint();
    }
  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }

  public double getMinDiameter() {
    return minDiameter;
  }

  public void setMinDiameter(double minDiameter) {
    this.minDiameter = minDiameter;
  }

  public double getMinBranchArea() {
    return minBranchArea;
  }

  public void setMinBranchArea(double minBranchArea) {
    this.minBranchArea = minBranchArea;
  }

}
