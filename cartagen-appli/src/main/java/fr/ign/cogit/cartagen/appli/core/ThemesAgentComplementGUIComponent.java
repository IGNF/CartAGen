package fr.ign.cogit.cartagen.appli.core;

import javax.swing.JMenu;

import fr.ign.cogit.cartagen.appli.core.themes.BlockMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.BuildingMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.HydroNetworkMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.LandUseMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.ReliefMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.RoadNetworkMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.TownMenuCogitComplement;
import fr.ign.cogit.cartagen.appli.core.themes.UrbanAlignmentMenuCogitComplement;

public class ThemesAgentComplementGUIComponent extends JMenu {

  private static ThemesAgentComplementGUIComponent menu;

  public static ThemesAgentComplementGUIComponent getInstance() {
    if (ThemesAgentComplementGUIComponent.menu == null) {
      return new ThemesAgentComplementGUIComponent();
    }
    return ThemesAgentComplementGUIComponent.menu;
  }

  private ReliefMenuCogitComplement reliefMenu;
  private LandUseMenuCogitComplement landUseMenu;
  private RoadNetworkMenuCogitComplement roadNetMenu;
  private HydroNetworkMenuCogitComplement hydroNetMenu;
  private BlockMenuCogitComplement blockMenu;
  private BuildingMenuCogitComplement buildingMenu;
  private UrbanAlignmentMenuCogitComplement alignmentMenu;
  private TownMenuCogitComplement townMenu;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ThemesAgentComplementGUIComponent() {

    this.reliefMenu = new ReliefMenuCogitComplement();
    this.landUseMenu = new LandUseMenuCogitComplement();
    this.roadNetMenu = new RoadNetworkMenuCogitComplement();
    this.hydroNetMenu = new HydroNetworkMenuCogitComplement();
    this.buildingMenu = new BuildingMenuCogitComplement();
    this.blockMenu = new BlockMenuCogitComplement();
    this.alignmentMenu = new UrbanAlignmentMenuCogitComplement();
    this.townMenu = new TownMenuCogitComplement();

    ThemesAgentComplementGUIComponent.menu = this;

  }

  public ReliefMenuCogitComplement getReliefMenu() {
    return this.reliefMenu;
  }

  public void setReliefMenu(ReliefMenuCogitComplement reliefMenu) {
    this.reliefMenu = reliefMenu;
  }

  public LandUseMenuCogitComplement getLandUseMenu() {
    return this.landUseMenu;
  }

  public void setLandUseMenu(LandUseMenuCogitComplement landUseMenu) {
    this.landUseMenu = landUseMenu;
  }

  public RoadNetworkMenuCogitComplement getRoadNetMenu() {
    return this.roadNetMenu;
  }

  public void setRoadNetMenu(RoadNetworkMenuCogitComplement roadNetMenu) {
    this.roadNetMenu = roadNetMenu;
  }

  public HydroNetworkMenuCogitComplement getHydroNetMenu() {
    return this.hydroNetMenu;
  }

  public void setHydroNetMenu(HydroNetworkMenuCogitComplement hydroNetMenu) {
    this.hydroNetMenu = hydroNetMenu;
  }

  public BlockMenuCogitComplement getBlockMenu() {
    return this.blockMenu;
  }

  public void setBlockMenu(BlockMenuCogitComplement blockMenu) {
    this.blockMenu = blockMenu;
  }

  public BuildingMenuCogitComplement getBuildingMenu() {
    return this.buildingMenu;
  }

  public void setBuildingMenu(BuildingMenuCogitComplement buildingMenu) {
    this.buildingMenu = buildingMenu;
  }

  public UrbanAlignmentMenuCogitComplement getUrbanAlignmentMenu() {
    return this.alignmentMenu;
  }

  public void setUrbanAlignmentMenu(
      UrbanAlignmentMenuCogitComplement alignmentMenu) {
    this.alignmentMenu = alignmentMenu;
  }

  public TownMenuCogitComplement getTownMenu() {
    return this.townMenu;
  }

  public void setTownMenu(TownMenuCogitComplement townMenu) {
    this.townMenu = townMenu;
  }

}
