package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.CorrectAlignmentBuildingsOrientation;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.DeleteAlignmentSmallestBuilding;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.EnsureAlignmentHomogeneousSpatialRepartition;
import fr.ign.cogit.cartagen.algorithms.urbanalignments.StraightAlignmentsDetection;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.mst.MinSpanningTree;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.UrbanAlignmentMenu;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class UrbanAlignmentMenuCogitComplement extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private Logger logger = Logger
      .getLogger(UrbanAlignmentMenuCogitComplement.class.getName());

  private UrbanAlignmentMenu menu;

  private JLabel lblCogit = new JLabel("          COGIT");
  private JMenuItem mCreateAlignmentInBlocks = new JMenuItem(
      new CreateAlignmentsInBlocksAction());
  private JMenuItem mCreateAlignmentFromBuildings = new JMenuItem(
      new CreateAlignmentsFromBuildingsAction());
  private JMenuItem mDestroyAlignments = new JMenuItem(
      new DestroyAlignmentsInBlocksAction());
  private JMenuItem mProjectionsAlignmentsDetection = new JMenuItem(
      new DetectAlignmentsByProjectionsAction());
  private JMenuItem mMSTAlignmentsDetection = new JMenuItem(
      new DetectMSTAlignmentsAction());
  private JMenuItem mDeleteSmallestBuilding = new JMenuItem(
      new DeleteSmallestBuildingAction());
  private JMenuItem mEnsureSpatialRepartition = new JMenuItem(
      new EnsureSpatialReparitionAction());
  private JMenuItem mCorrectOrientations = new JMenuItem(
      new CorrectOrientationsAction());

  public UrbanAlignmentMenuCogitComplement() {

    menu = DataThemesGUIComponent.getInstance().getUrbanAlignmentMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblCogit.setForeground(Color.RED);
    menu.add(this.lblCogit);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mCreateAlignmentInBlocks);
    menu.add(this.mCreateAlignmentFromBuildings);
    menu.add(this.mDestroyAlignments);

    menu.addSeparator();

    menu.add(this.mProjectionsAlignmentsDetection);
    menu.add(this.mMSTAlignmentsDetection);

    menu.addSeparator();

    menu.add(this.mDeleteSmallestBuilding);
    menu.add(this.mEnsureSpatialRepartition);
    menu.add(this.mCorrectOrientations);

  }

  private class CreateAlignmentsInBlocksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil
              .getSelectedObjects(menu.getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanBlock) {
              UrbanEnrichment.createUrbanAlignmentsBasedOnSections(
                  (IUrbanBlock) sel,
                  CartAGenDoc.getInstance().getCurrentDataset(),
                  CartAGenPlugin.getInstance().getCurrentGeneObjImpl()
                      .getCreationFactory());
            }
          }
        }
      });
      th.start();
    }

    public CreateAlignmentsInBlocksAction() {
      super();
      this.putValue(Action.NAME,
          "Automated detection of alignments in selected blocks");
    }

  }

  private class CreateAlignmentsFromBuildingsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          List<IUrbanElementAgent> buildAgents = new ArrayList<IUrbanElementAgent>();
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanElement) {
              buildAgents.add((IUrbanElementAgent) AgentUtil
                  .getAgentFromGeneObj((IUrbanElement) sel));
            }
          }
          new UrbanAlignmentAgent(
              CartAGenPlugin.getInstance().getCurrentGeneObjImpl()
                  .getCreationFactory().createUrbanAlignment(),
              buildAgents);
        }
      });
      th.start();
    }

    public CreateAlignmentsFromBuildingsAction() {
      super();
      this.putValue(Action.NAME,
          "Alignment creation based on selected buildings");
    }

  }

  private class DestroyAlignmentsInBlocksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanAlignment) {
              ((IUrbanAlignment) sel).destroy();
            }
          }
        }
      });
      th.start();
    }

    public DestroyAlignmentsInBlocksAction() {
      super();
      this.putValue(Action.NAME, "Destruction of the selected alignments");
    }

  }

  private class DetectAlignmentsByProjectionsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          long before = Calendar.getInstance().getTimeInMillis();
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IUrbanBlock)) {
              continue;
            }
            IUrbanBlock block = (IUrbanBlock) sel;

            IFeatureCollection<IUrbanElement> coll = new FT_FeatureCollection<IUrbanElement>();
            for (IUrbanElement building : block.getUrbanElements()) {
              coll.add(building);
            }
            StraightAlignmentsDetection.compute(block, coll);
          }
          long after = Calendar.getInstance().getTimeInMillis();
          System.out.println((after - before) / 1000.0);
        }
      });
      th.start();
    }

    public DetectAlignmentsByProjectionsAction() {
      super();
      this.putValue(Action.NAME,
          "Detection of straight alignments based on projections (beta)");
    }

  }

  private class DetectMSTAlignmentsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          long before = Calendar.getInstance().getTimeInMillis();
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IUrbanBlock)) {
              continue;
            }
            IUrbanBlock block = (IUrbanBlock) sel;
            IFeatureCollection<IFeature> coll = new FT_FeatureCollection<IFeature>();
            for (IUrbanElement building : block.getUrbanElements()) {
              if (!(building instanceof IBuilding)) {
                continue;
              }
              coll.add(building);
            }
            ArrayList<MinSpanningTree> trees = MinSpanningTree
                .buildMinSpanningTree(coll, 500);
            if (trees == null) {
              continue;
            }
            for (MinSpanningTree tree : trees) {
              if (tree == null) {
                continue;
              }
              for (IEdge edge : tree.getEdges()) {
                CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                    .addFeatureToGeometryPool(edge.getGeom(), Color.BLACK, 1);
              }
            }
          }
          long after = Calendar.getInstance().getTimeInMillis();
          System.out.println((after - before) / 1000.0);
        }
      });
      th.start();
    }

    public DetectMSTAlignmentsAction() {
      super();
      this.putValue(Action.NAME,
          "Detection of curvilinear alignments based on MST (beta)");
    }

  }

  private class DeleteSmallestBuildingAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanAlignment) {
              DeleteAlignmentSmallestBuilding algo = new DeleteAlignmentSmallestBuilding(
                  (IUrbanAlignment) sel);
              algo.compute();
            }
          }
        }
      });
      th.start();
    }

    public DeleteSmallestBuildingAction() {
      super();
      this.putValue(Action.NAME, "Elimination of the smallest building");
    }

  }

  private class EnsureSpatialReparitionAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanAlignment) {
              EnsureAlignmentHomogeneousSpatialRepartition algo = new EnsureAlignmentHomogeneousSpatialRepartition(
                  (IUrbanAlignment) sel);
              algo.compute();
            }
          }
        }
      });
      th.start();
    }

    public EnsureSpatialReparitionAction() {
      super();
      this.putValue(Action.NAME,
          "Homogeneous spatial repartition of buildings");
    }

  }

  private class CorrectOrientationsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanAlignment) {
              CorrectAlignmentBuildingsOrientation algo = new CorrectAlignmentBuildingsOrientation(
                  (IUrbanAlignment) sel);
              algo.compute();
            }
          }
        }
      });
      th.start();
    }

    public CorrectOrientationsAction() {
      super();
      this.putValue(Action.NAME, "Correction of buildings orientations");
    }

  }

}
