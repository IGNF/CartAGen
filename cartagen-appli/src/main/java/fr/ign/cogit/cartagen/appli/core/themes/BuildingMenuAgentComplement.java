package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.micro.SquarringAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.BuildingMenu;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;

public class BuildingMenuAgentComplement {

  private Logger logger = Logger
      .getLogger(BuildingMenuAgentComplement.class.getName());

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mBatimentChargerTous = new JMenuItem(new LoadAction());
  private JMenuItem mBatimentRetourEtatInitialTous = new JMenuItem(
      new RestoreAction());

  public JCheckBoxMenuItem mPointsBatiVoir = new JCheckBoxMenuItem(
      "Display points");
  public JCheckBoxMenuItem mSegmentsBatiVoir = new JCheckBoxMenuItem(
      "Display segments");

  public JCheckBoxMenuItem mVoirSatisfactionBati = new JCheckBoxMenuItem(
      "Display overall satisfaction");
  public JCheckBoxMenuItem mVoirCouleurSatisfactionBati = new JCheckBoxMenuItem(
      "Display satisfaction color");

  public JCheckBoxMenuItem mVoirSatisfactionTaille = new JCheckBoxMenuItem(
      "Voir satisfaction taille");

  public JCheckBoxMenuItem mVoirSatisfactionEquarrite = new JCheckBoxMenuItem(
      "Voir satisfaction equarrite");

  public JCheckBoxMenuItem mVoirSatisfactionAltitude = new JCheckBoxMenuItem(
      "Voir satisfaction altitude");

  public JCheckBoxMenuItem mVoirSatisfactionOrientation = new JCheckBoxMenuItem(
      "Voir satisfaction orientation generale");

  public JCheckBoxMenuItem mVoirSatisfactionElongation = new JCheckBoxMenuItem(
      "Voir satisfaction elongation");

  public JCheckBoxMenuItem mVoirSatisfactionConvexite = new JCheckBoxMenuItem(
      "Voir satisfaction convexite");

  public JCheckBoxMenuItem mVoirSatisfactionGranularite = new JCheckBoxMenuItem(
      "Voir satisfaction granularite");

  private JMenuItem mEquarrissage = new JMenuItem(new SquaringAction());

  /**
   * Constructor a of the menu from a title.
   * @param title
   */
  public BuildingMenuAgentComplement() {

    BuildingMenu menu = DataThemesGUIComponent.getInstance().getBuildingMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mBatimentChargerTous);
    menu.add(this.mBatimentRetourEtatInitialTous);

    menu.addSeparator();

    menu.add(this.mPointsBatiVoir);
    menu.add(this.mSegmentsBatiVoir);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionBati);
    menu.add(this.mVoirCouleurSatisfactionBati);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionTaille);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionEquarrite);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionAltitude);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionOrientation);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionElongation);
    menu.add(this.mVoirSatisfactionConvexite);
    menu.add(this.mVoirSatisfactionGranularite);

    menu.addSeparator();

    menu.add(this.mEquarrissage);

  }

  private class LoadAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      AgentGeneralisationScheduler.getInstance().initList();
      for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings()) {
        AgentGeneralisationScheduler.getInstance()
            .add(AgentUtil.getAgentFromGeneObj(obj));
      }
    }

    public LoadAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load all buildings in the agent scheduler");
      this.putValue(Action.NAME, "Load");
    }
  }

  private class RestoreAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset()
              .getBuildings()) {
            ((GeographicObjectAgentGeneralisation) AgentUtil
                .getAgentFromGeneObj(obj)).goBackToInitialState();
          }
          for (IUrbanAlignment obj : CartAGenDoc.getInstance()
              .getCurrentDataset().getUrbanAlignments()) {
            ((UrbanAlignmentAgent) AgentUtil.getAgentFromGeneObj(obj))
                .computeShapeLine();
          }
          for (IUrbanBlock obj : CartAGenDoc.getInstance().getCurrentDataset()
              .getBlocks()) {
            ((GeographicObjectAgentGeneralisation) AgentUtil
                .getAgentFromGeneObj(obj)).goBackToInitialState();
          }
        }
      });
      th.start();
    }

    public RestoreAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Restore all buildings to their previous state");
      this.putValue(Action.NAME, "Restore all buildings");
    }
  }

  private class SquaringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Valeur tolérance angle (en degres)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double tol = 0.0;
          if (s != null && !s.isEmpty()) {
            tol = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            BuildingAgent ab = (BuildingAgent) AgentUtil
                .getAgentFromGeneObj((IBuilding) sel);
            BuildingMenuAgentComplement.this.logger
                .info("Equarrissage du batiment " + sel);
            try {
              new SquarringAction(ab, null, 1.0, tol, 500).compute();
            } catch (InterruptedException exc) {
            }
            BuildingMenuAgentComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public SquaringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Squaring algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Squaring");
    }
  }

}
