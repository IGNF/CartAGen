package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseFieldAgent;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.LandUseMenu;

public class LandUseMenuAgentComplement {

  private Logger logger = Logger
      .getLogger(LandUseMenuAgentComplement.class.getName());

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mOccSolCharger = new JMenuItem(new LoadLandUseAction());
  private JMenuItem mVoirOccSolInfos = new JMenuItem(new DisplayInfoAction());
  public JCheckBoxMenuItem mSegmentsOccSolVoir = new JCheckBoxMenuItem(
      "Display segments");
  public JCheckBoxMenuItem mPointsOccSolVoir = new JCheckBoxMenuItem(
      "Display points");

  public LandUseMenuAgentComplement() {

    LandUseMenu menu = DataThemesGUIComponent.getInstance().getLandUseMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mOccSolCharger);
    menu.add(this.mVoirOccSolInfos);
    menu.add(this.mSegmentsOccSolVoir);
    menu.add(this.mPointsOccSolVoir);
  }

  private class LoadLandUseAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      AgentGeneralisationScheduler.getInstance().initList();
      LandUseMenuAgentComplement.this.logger
          .info("Chargement de " + AgentUtil.getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getLandUseField()));
      AgentGeneralisationScheduler.getInstance()
          .add(AgentUtil.getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getLandUseField()));
    }

    public LoadLandUseAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Load a land use agent");
      this.putValue(Action.NAME, "Load");
    }
  }

  private class DisplayInfoAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      System.out.println("----field info-----");
      ((LandUseFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getLandUseField()))
              .printInfosConsole();
      System.out.println("----end field info-----");
    }

    public DisplayInfoAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Display information on the land use agent");
      this.putValue(Action.NAME, "Display Information");
    }
  }
}
