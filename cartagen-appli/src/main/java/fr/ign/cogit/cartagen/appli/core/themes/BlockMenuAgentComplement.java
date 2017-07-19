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
import fr.ign.cogit.cartagen.agents.core.action.MesoComponentsActivation;
import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDisplacementGAELAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.BlockMenu;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;

public class BlockMenuAgentComplement {

  private static final long serialVersionUID = 1L;

  private Logger logger = Logger
      .getLogger(BlockMenuAgentComplement.class.getName());

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mIlotChargerTous = new JMenuItem(new LoadAction());
  private JMenuItem mIlotRetourEtatInitialTous = new JMenuItem(
      new RestoreAction());

  public JCheckBoxMenuItem mIdIlotVoir = new JCheckBoxMenuItem("Display id");
  public JCheckBoxMenuItem mComponentsDisplay = new JCheckBoxMenuItem(
      "Display buildings link");

  public JCheckBoxMenuItem mPointsIlotVoir = new JCheckBoxMenuItem(
      "Display points");
  public JCheckBoxMenuItem mSegmentsIlotVoir = new JCheckBoxMenuItem(
      "Display segments");
  public JCheckBoxMenuItem mSegmentsPlusCourtsIlotVoir = new JCheckBoxMenuItem(
      "Voir segments plus courts");
  public JCheckBoxMenuItem mValeurSegmentsIlotVoir = new JCheckBoxMenuItem(
      "Voir valeur segments");

  public JCheckBoxMenuItem mVoirTexteSatisfactionIlot = new JCheckBoxMenuItem(
      "Voir satisfaction");
  public JCheckBoxMenuItem mVoirCouleurSatisfactionIlot = new JCheckBoxMenuItem(
      "Voir couleur satisfaction");
  public JCheckBoxMenuItem mVoirSatisfactionComposants = new JCheckBoxMenuItem(
      "Voir satisfaction composants");

  public JCheckBoxMenuItem mVoirCoutSuppressionBatiments = new JCheckBoxMenuItem(
      "Voir cout suppression batiments");

  public JCheckBoxMenuItem mVoirDensiteInitiale = new JCheckBoxMenuItem(
      "Voir densite initiale");
  public JCheckBoxMenuItem mVoirDensiteSimulee = new JCheckBoxMenuItem(
      "Voir densite simulee");
  public JCheckBoxMenuItem mVoirSatisfactionDensite = new JCheckBoxMenuItem(
      "Voir satisfaction densite");
  public JCheckBoxMenuItem mVoirTauxSuperpositionBatiments = new JCheckBoxMenuItem(
      "Voir moyenne taux superposition batiments");
  public JCheckBoxMenuItem mVoirSatisfactionProximite = new JCheckBoxMenuItem(
      "Voir satisfaction proximite");

  private JMenuItem mGeneralisationBatiments = new JMenuItem(
      new GeneralisationAction());
  private JMenuItem mDeplacementBatimentsGAEL = new JMenuItem(
      new DisplaceBuildingsGAELAction());

  public BlockMenuAgentComplement() {

    BlockMenu menu = DataThemesGUIComponent.getInstance().getBlockMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mIlotChargerTous);

    menu.addSeparator();

    menu.add(this.mComponentsDisplay);
    menu.add(this.mIlotRetourEtatInitialTous);

    menu.addSeparator();

    menu.add(this.mPointsIlotVoir);
    menu.add(this.mSegmentsIlotVoir);
    menu.add(this.mSegmentsPlusCourtsIlotVoir);
    menu.add(this.mValeurSegmentsIlotVoir);

    menu.addSeparator();

    menu.add(this.mVoirTexteSatisfactionIlot);
    menu.add(this.mVoirCouleurSatisfactionIlot);
    menu.add(this.mVoirSatisfactionComposants);

    menu.addSeparator();

    menu.add(this.mVoirSatisfactionDensite);

    menu.addSeparator();

    menu.add(this.mGeneralisationBatiments);
    menu.add(this.mDeplacementBatimentsGAEL);

  }

  private class LoadAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      AgentGeneralisationScheduler.getInstance().initList();
      for (IUrbanBlock obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getBlocks()) {
        AgentGeneralisationScheduler.getInstance()
            .add(AgentUtil.getAgentFromGeneObj(obj));
      }
    }

    public LoadAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load all blocks in the agent scheduler");
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
          for (IUrbanBlock obj : CartAGenDoc.getInstance().getCurrentDataset()
              .getBlocks()) {
            ((GeographicObjectAgentGeneralisation) AgentUtil
                .getAgentFromGeneObj(obj)).goBackToInitialState();
          }
          for (IUrbanAlignment obj : CartAGenDoc.getInstance()
              .getCurrentDataset().getUrbanAlignments()) {
            ((UrbanAlignmentAgent) AgentUtil.getAgentFromGeneObj(obj))
                .computeShapeLine();
          }
        }
      });
      th.start();
    }

    public RestoreAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Restore all blocks to their previous state");
      this.putValue(Action.NAME, "Restore all blocks");
    }
  }

  private class GeneralisationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (!(sel instanceof IUrbanBlock)) {
              continue;
            }

            BlockAgent ab = (BlockAgent) AgentUtil
                .getAgentFromGeneObj((IUrbanBlock) sel);
            BlockMenuAgentComplement.this.logger
                .info("Generalisation des batiments de l'ilot " + sel);
            try {
              new MesoComponentsActivation<BuildingAgent>(ab, null, 1.0)
                  .compute();
            } catch (InterruptedException exc) {
            }
            BlockMenuAgentComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public GeneralisationAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger buildings generalisation of the selected block agent");
      this.putValue(Action.NAME, "Trigger buildings generalisation");
    }
  }

  private class DisplaceBuildingsGAELAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (!(sel instanceof IUrbanBlock)) {
              continue;
            }

            BlockAgent ab = (BlockAgent) AgentUtil
                .getAgentFromGeneObj((IUrbanBlock) sel);
            BlockMenuAgentComplement.this.logger
                .info("Déplacement des bâtiments de l'îlot (GAEL)" + sel);
            try {
              new BlockBuildingsDisplacementGAELAction(ab, null, 1.0).compute();
            } catch (InterruptedException exc) {
            }
            BlockMenuAgentComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public DisplaceBuildingsGAELAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Displace buildings GAEL algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Displace buildings GAEL");
    }
  }

}
