package fr.ign.cogit.cartagen.appli.core.themes;

import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.appli.core.actions.BuildingAmalgamationAction;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.BuildingMenu;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;

public class BuildingMenuCogitComplement extends JMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(BuildingMenuCogitComplement.class.getName());

    public BuildingMenuCogitComplement() {

        BuildingMenu menu = DataThemesGUIComponent.getInstance().getBuildingMenu();

        menu.addSeparator();
        JMenuItem amalgamationItem = new JMenuItem(new BuildingAmalgamationAction());
        menu.add(amalgamationItem);

    }

}
