package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.algorithms.block.BuildingsAggregation;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionCongestion;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionSize;
import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRandom;
import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRuas;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockTriangulation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.BlockMenu;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;

public class BlockMenuCogitComplement extends JMenu {

  private static final long serialVersionUID = 1L;

  private Logger logger = Logger
      .getLogger(BlockMenuCogitComplement.class.getName());

  private JLabel lblCogit = new JLabel("          COGIT");

  private JMenuItem mSuppressionBatimentsDensite = new JMenuItem(
      new DeleteBuildingsDensityAction());
  private JMenuItem mSuppressionBatimentsSize = new JMenuItem(
      new DeleteBuildingsSizeAction());
  private JMenuItem mDeplacementBatimentsRandom = new JMenuItem(
      new DisplaceBuildingsRandAction());
  private JMenuItem mDeplacementBatimentsRuas = new JMenuItem(
      new DisplaceBuildingsRuasAction());
  private JMenuItem mAgregationBatiments = new JMenuItem(
      new Aggregate2BuildingsAction());
  private JMenuItem mIlotTriangulation = new JMenuItem(new TriangulateAction());

  public BlockMenuCogitComplement() {

    BlockMenu menu = DataThemesGUIComponent.getInstance().getBlockMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblCogit.setForeground(Color.RED);
    menu.add(this.lblCogit);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mSuppressionBatimentsDensite);
    menu.add(this.mSuppressionBatimentsSize);
    menu.add(this.mDeplacementBatimentsRandom);
    menu.add(this.mDeplacementBatimentsRuas);
    menu.add(this.mAgregationBatiments);
    menu.add(this.mIlotTriangulation);

  }

  private class DeleteBuildingsDensityAction extends AbstractAction {

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

            IUrbanBlock ab = (IUrbanBlock) sel;
            BlockMenuCogitComplement.this.logger
                .info("Suppression des bâtiments de l'îlot " + sel);
            ArrayList<IUrbanElement> builds = BuildingsDeletionCongestion
                .compute(ab, 1,
                    GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE,
                    GeneralisationSpecifications.RATIO_BLOCK_DENSITY);
            BlockMenuCogitComplement.this.logger.info(" fin");
            for (IUrbanElement build : builds) {
              build.eliminate();
            }
          }
        }
      });
      th.start();
    }

    public DeleteBuildingsDensityAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Delete buildings algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Delete buildings");
    }
  }

  private class DeleteBuildingsSizeAction extends AbstractAction {

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

            IUrbanBlock ai = (IUrbanBlock) sel;
            BlockMenuCogitComplement.this.logger
                .info("Suppression (2) des bâtiments de l'îlot " + sel);
            BuildingsDeletionSize.compute(ai, 1);
            BlockMenuCogitComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public DeleteBuildingsSizeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Delete buildings (2) algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Delete buildings (2)");
    }
  }

  private class DisplaceBuildingsRandAction extends AbstractAction {

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

            IUrbanBlock ai = (IUrbanBlock) sel;
            BlockMenuCogitComplement.this.logger
                .info("Déplacement des bâtiments de l'îlot " + sel);
            BuildingDisplacementRandom.compute(ai);
            BlockMenuCogitComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public DisplaceBuildingsRandAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Displace buildings random algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Displace buildings random");
    }
  }

  private class DisplaceBuildingsRuasAction extends AbstractAction {

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

            IUrbanBlock ab = (IUrbanBlock) sel;
            BlockMenuCogitComplement.this.logger
                .info("Déplacement des bâtiments de l'îlot " + sel);
            BuildingDisplacementRuas.compute(ab, 5.0, 2);
            BlockMenuCogitComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public DisplaceBuildingsRuasAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Displace buildings Ruas algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Displace buildings Ruas");
    }
  }

  private class Aggregate2BuildingsAction extends AbstractAction {

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

            IUrbanBlock ai = (IUrbanBlock) sel;
            BlockMenuCogitComplement.this.logger
                .info("Agregation de 2 bâtiments de l'îlot " + ai);
            BuildingsAggregation.compute(ai);
            BlockMenuCogitComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public Aggregate2BuildingsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Aggregate 2 buildings algorithm on the selected block agents");
      this.putValue(Action.NAME, "Trigger Aggregate 2 buildings");
    }
  }

  private class TriangulateAction extends AbstractAction {

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
            BlockMenuCogitComplement.this.logger
                .info("Triangulation de l'îlot " + sel);
            BlockTriangulation.buildTriangulation((IUrbanBlock) sel,
                GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);
            BlockMenuCogitComplement.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public TriangulateAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Delaunay Triangulation in the selected block agents");
      this.putValue(Action.NAME, "Trigger Triangulation");
    }
  }

}
