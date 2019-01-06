/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.core;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

/**
 * Class for the Semiotics, Legend Conception and Legend improving Application.
 * 
 * @author GTouya
 */
public class CartAGenApplicationAgent extends GeOxygeneApplication implements AgentObserver {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CartAGenApplicationAgent.class);

    private boolean slowMotion = false;

    private CartAGenApplicationAgent() {
        super("CartAGen-GeOxygene");
        // reorganize menus
        reorganizeMenus();
        // add right panel
        CartAGenPlugin.getInstance().addRightPanel();
        new ThemesAgentComplementGUIComponent();
        addAgentModules();

        AbstractGeometryEngine.setGeometrie(this.getProperties().getGeometryEngine());
        GeometryEngine.init();

        CartAGenPlugin.getInstance().getApplication().getMainFrame().getGui().pack();
    }

    /**
     * Puts the GeOxygene plugins in a single menu item.
     */
    private void reorganizeMenus() {
        // System.out.println("im here");
        JMenu plugIns = new JMenu("PlugIns GeOx");
        plugIns.setFont(this.getMainFrame().getMenuBar().getFont());
        this.getMainFrame().getMenuBar().add(plugIns, 2);

        List<JMenu> geoxPlugins = new ArrayList<JMenu>();
        List<JMenu> cartagenPlugins = new ArrayList<JMenu>();
        JMenu cartagenMenu = null;
        for (int i = 3; i < this.getMainFrame().getMenuBar().getComponentCount() - 1; i++) {
            if (this.getMainFrame().getMenuBar().getComponent(i) instanceof JMenu) {
                JMenu menu = (JMenu) this.getMainFrame().getMenuBar().getComponent(i);
                if (menu == null) {
                    // System.out.println("i'm fuckin' null !!");
                    continue;
                }
                if (menu.getText().equals("CartAGen")) {
                    // System.out.println("i'm not null !");
                    cartagenMenu = menu;
                    for (Component comp : menu.getMenuComponents()) {
                        if (comp instanceof JMenu)
                            cartagenPlugins.add((JMenu) comp);
                    }
                } else {
                    geoxPlugins.add(menu);
                }
            }
        }
        // remove the cartagen submenu
        if (cartagenMenu != null)
            this.getMainFrame().getMenuBar().remove(cartagenMenu);
        // remove all geoxygene plugIns and add all geoxygene plugIns under
        // "plugIns" menu
        for (JMenu menu : geoxPlugins) {
            this.getMainFrame().getMenuBar().remove(menu);
            plugIns.add(menu);
        }

        // add the CartAGen menus as main menus of the menuBar
        for (JMenu menu : cartagenPlugins)
            this.getMainFrame().getMenuBar().add(menu, this.getMainFrame().getMenuBar().getMenuCount() - 2);

        this.getMainFrame().getMenuBar().revalidate();
    }

    /**
     * add the interface agent modules
     */

    public void addAgentModules() {

        // ajout des menus agent
        GeneralisationMenuAgentComplement.getInstance().add(this.getMainFrame().getMenuBar());

        // ajout du panneau agent a droite
        JPanel rightPanel = getRightPanel();

        GeneralisationRightPanelAgentComplement.getInstance().add(rightPanel);

        // visualisation agent actif
        GeneralisationRightPanelAgentComplement.getInstance().cAfficherAgentActif.setSelected(false);

        // pauses
        GeneralisationRightPanelAgentComplement.getInstance().cFairePauses.setSelected(false);
    }

    private JPanel getRightPanel() {
        for (Component comp : this.getMainFrame().getGui().getContentPane().getComponents()) {
            if (comp instanceof JSplitPane)
                return (JPanel) ((JSplitPane) comp).getRightComponent();
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        CartAGenApplicationAgent cartoappli = new CartAGenApplicationAgent();
        cartoappli.getMainFrame().getGui().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void update() {
        // refresh the layer view panel
        this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
    }

    @Override
    public boolean isSlowMotion() {
        return slowMotion;
    }

    /**
     * Set the Agent observation in slow motion (generalization waits for
     * refresh).
     * 
     * @param isSlowMotion
     */
    @Override
    public void setSlowMotion(boolean isSlowMotion) {
        this.slowMotion = isSlowMotion;
    }
}
