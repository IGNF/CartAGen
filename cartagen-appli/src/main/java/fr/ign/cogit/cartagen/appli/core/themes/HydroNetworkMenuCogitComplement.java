package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenu;

public class HydroNetworkMenuCogitComplement extends JMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(HydroNetworkMenuCogitComplement.class.getName());

    private JLabel lblCogit = new JLabel("          COGIT");

    private JLabel mDefault = new JLabel("        empty menu        ");

    public HydroNetworkMenuCogitComplement() {

        HydroNetworkMenu menu = DataThemesGUIComponent.getInstance().getHydroNetMenu();

        menu.addSeparator();
        menu.addSeparator();

        this.lblCogit.setForeground(Color.RED);
        menu.add(this.lblCogit);

        menu.addSeparator();
        menu.addSeparator();

        menu.add(this.mDefault);

    }

}
