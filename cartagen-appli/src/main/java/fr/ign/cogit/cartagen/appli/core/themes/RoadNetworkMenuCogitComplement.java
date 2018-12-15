package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.algorithms.section.CollapseDualCarriageways;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStructureDetection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.RoadNetworkMenu;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;

public class RoadNetworkMenuCogitComplement extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private Logger logger = Logger
      .getLogger(RoadNetworkMenuCogitComplement.class.getName());

  // private JLabel lblCogit = new JLabel(" COGIT");

  private JMenuItem mRoutierDetectMotorways = new JMenuItem(
      new DualCarriageAction());
  private JMenuItem mRoutierCollapseMotorways = new JMenuItem(
      new CollapseDualCarriageAction());

  public RoadNetworkMenuCogitComplement() {

    RoadNetworkMenu menu = DataThemesGUIComponent.getInstance()
        .getRoadNetMenu();

    menu.addSeparator();
    menu.addSeparator();

    /*
     * this.lblCogit.setForeground(Color.RED); menu.add(this.lblCogit);
     * 
     * menu.addSeparator(); menu.addSeparator();
     */

    menu.add(this.mRoutierDetectMotorways);
    menu.add(this.mRoutierCollapseMotorways);

  }

  private class DualCarriageAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      RoadStructureDetection algo = new RoadStructureDetection();
      List<Face> separators = algo.detectDualCarriageways(4);
      for (Face face : separators) {
        // TODO add face geometry to geometry pool
      }
      algo = new RoadStructureDetection();
      separators = algo.detectDualCarriageways(3);
      for (Face face : separators) {
        // TODO add face geometry to geometry pool
      }
    }

    public DualCarriageAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Detect dual carriageways in the road network (stage de P. Danré)");
      this.putValue(Action.NAME, "Detect dual carriageways");
    }
  }

  private class CollapseDualCarriageAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CollapseDualCarriageways algo = new CollapseDualCarriageways(-1);
      RoadStructureDetection detection = new RoadStructureDetection();
      List<Face> separators = detection.detectDualCarriageways(-1);
      algo.simplifyDualCarriageways(separators);
      // TODO refresh visu panel
      /*
       * detection = new RoadStructureDetection(); algo = new
       * CollapseDualCarriageways(3); separators =
       * detection.detectDualCarriageways(3);
       * algo.simplifyDualCarriageways(separators);
       */
      // TODO refresh visu panel
    }

    public CollapseDualCarriageAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Collapse dual carriageways into centreline (stage de P. Danré)");
      this.putValue(Action.NAME, "Collapse dual carriageways");
    }
  }

}
