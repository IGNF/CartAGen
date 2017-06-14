package fr.ign.cogit.cartagen.appli.core;

import fr.ign.cogit.cartagen.appli.core.themes.BlockMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.BuildingMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.HydroNetworkMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.LandUseMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.ReliefMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.RoadNetworkMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.TownMenuAgentComplement;
import fr.ign.cogit.cartagen.appli.core.themes.UrbanAlignmentMenuAgentComplement;

public class DataThemesAgentComplementGUIComponent {

  private static DataThemesAgentComplementGUIComponent menu;

  public static DataThemesAgentComplementGUIComponent getInstance() {
    if (DataThemesAgentComplementGUIComponent.menu == null) {
      return new DataThemesAgentComplementGUIComponent();
    }
    return DataThemesAgentComplementGUIComponent.menu;
  }

  private ReliefMenuAgentComplement reliefMenu;
  private LandUseMenuAgentComplement landUseMenu;
  private RoadNetworkMenuAgentComplement roadNetMenu;
  private HydroNetworkMenuAgentComplement hydroNetMenu;
  private BlockMenuAgentComplement blockMenu;
  private BuildingMenuAgentComplement buildingMenu;
  private UrbanAlignmentMenuAgentComplement alignmentMenu;
  private TownMenuAgentComplement townMenu;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DataThemesAgentComplementGUIComponent() {

    this.reliefMenu = new ReliefMenuAgentComplement();
    this.landUseMenu = new LandUseMenuAgentComplement();
    this.roadNetMenu = new RoadNetworkMenuAgentComplement();
    this.hydroNetMenu = new HydroNetworkMenuAgentComplement();
    this.buildingMenu = new BuildingMenuAgentComplement();
    this.blockMenu = new BlockMenuAgentComplement();
    this.alignmentMenu = new UrbanAlignmentMenuAgentComplement();
    this.townMenu = new TownMenuAgentComplement();

    DataThemesAgentComplementGUIComponent.menu = this;

  }

  public ReliefMenuAgentComplement getReliefMenu() {
    return this.reliefMenu;
  }

  public void setReliefMenu(ReliefMenuAgentComplement reliefMenu) {
    this.reliefMenu = reliefMenu;
  }

  public LandUseMenuAgentComplement getLandUseMenu() {
    return this.landUseMenu;
  }

  public void setLandUseMenu(LandUseMenuAgentComplement landUseMenu) {
    this.landUseMenu = landUseMenu;
  }

  public RoadNetworkMenuAgentComplement getRoadNetMenu() {
    return this.roadNetMenu;
  }

  public void setRoadNetMenu(RoadNetworkMenuAgentComplement roadNetMenu) {
    this.roadNetMenu = roadNetMenu;
  }

  public HydroNetworkMenuAgentComplement getHydroNetMenu() {
    return this.hydroNetMenu;
  }

  public void setHydroNetMenu(HydroNetworkMenuAgentComplement hydroNetMenu) {
    this.hydroNetMenu = hydroNetMenu;
  }

  public BlockMenuAgentComplement getBlockMenu() {
    return this.blockMenu;
  }

  public void setBlockMenu(BlockMenuAgentComplement blockMenu) {
    this.blockMenu = blockMenu;
  }

  public BuildingMenuAgentComplement getBuildingMenu() {
    return this.buildingMenu;
  }

  public void setBuildingMenu(BuildingMenuAgentComplement buildingMenu) {
    this.buildingMenu = buildingMenu;
  }

  public UrbanAlignmentMenuAgentComplement getUrbanAlignmentMenu() {
    return this.alignmentMenu;
  }

  public void setUrbanAlignmentMenu(
      UrbanAlignmentMenuAgentComplement alignmentMenu) {
    this.alignmentMenu = alignmentMenu;
  }

  public TownMenuAgentComplement getTownMenu() {
    return this.townMenu;
  }

  public void setTownMenu(TownMenuAgentComplement townMenu) {
    this.townMenu = townMenu;
  }

}
