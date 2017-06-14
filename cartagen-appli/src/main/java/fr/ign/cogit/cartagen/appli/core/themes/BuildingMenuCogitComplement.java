package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenu;

import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.BuildingMenu;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;

public class BuildingMenuCogitComplement extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private Logger logger = Logger.getLogger(BuildingMenuCogitComplement.class
      .getName());

  private JLabel lblCogit = new JLabel("          COGIT");

  private JLabel mDefault = new JLabel("        empty menu        ");

  public BuildingMenuCogitComplement() {

    BuildingMenu menu = DataThemesGUIComponent.getInstance().getBuildingMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblCogit.setForeground(Color.RED);
    menu.add(this.lblCogit);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mDefault);

  }

}
