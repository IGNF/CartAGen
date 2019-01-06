package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenu;

public class ReliefMenuCogitComplement extends JMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ReliefMenuCogitComplement.class.getName());

    private JLabel lblCogit = new JLabel("          COGIT");

    private JLabel mDefault = new JLabel("        empty menu        ");

    public ReliefMenuCogitComplement() {

        ReliefMenu menu = DataThemesGUIComponent.getInstance().getReliefMenu();

        menu.addSeparator();
        menu.addSeparator();

        this.lblCogit.setForeground(Color.RED);
        menu.add(this.lblCogit);

        menu.addSeparator();
        menu.addSeparator();

        menu.add(this.mDefault);

    }

}
