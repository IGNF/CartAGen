package fr.ign.cogit.cartagen.appli.plugins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.MesoComponentsActivation;
import fr.ign.cogit.cartagen.agents.core.action.block.BlockBuildingsDisplacementGAELAction;
import fr.ign.cogit.cartagen.agents.core.action.micro.SquarringAction;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionCongestion;
import fr.ign.cogit.cartagen.algorithms.section.CollapseRoundabout;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning.DeadEndZone;
import fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning.DeadEndZoning;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.CrossRoadDetection;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks.Bookmark;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks.BookmarkSet;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class DemoPlugin extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static DemoPlugin instance = null;

  protected static BookmarkSet bookmarks;
  protected static Bookmark bookmarkRoads1;
  protected static Bookmark bookmarkRoads2;
  protected static Bookmark bookmarkRoads3;
  protected static Bookmark bookmarkUrban1;
  protected static Bookmark bookmarkUrban2;
  protected static Bookmark bookmarkNet1;
  protected static Bookmark bookmarkNet2;
  protected static Bookmark bookmarkNet3;
  protected static Bookmark bookmarkNet4;

  public DemoPlugin() {
    // Exists only to defeat instantiation.
    super();
  }

  public static DemoPlugin getInstance() {
    if (DemoPlugin.instance == null) {
      DemoPlugin.instance = new DemoPlugin("Démo");
    }
    return DemoPlugin.instance;
  }

  public DemoPlugin(String title) {
    super(title);
    DemoPlugin.instance = this;

    // GENERAL BUTTONS
    JMenuItem initialiseBookmarks = new JMenuItem(
        new InitialiseBookmarksAction());
    JMenuItem createAgents = new JMenuItem(new CreateAgentsAction());
    JMenuItem duplicateDataSet = new JMenuItem(new DuplicateDatasetAction());
    this.add(initialiseBookmarks);
    this.add(createAgents);
    this.add(duplicateDataSet);

    // MENUS
    JMenu roadsMenu = new JMenu("Roads");
    JMenu networkMenu = new JMenu("Networks");
    JMenu urbanMenu = new JMenu("Urban");

    this.add(roadsMenu);
    this.add(networkMenu);
    this.add(urbanMenu);

    // MENU ROADS
    JMenuItem roadBookmark1 = new JMenuItem("=> go to road #1 (100K)");
    JMenuItem roadAccordeon = new JMenuItem(new AccordionAction());
    JMenuItem roadCoalDecomposer = new JMenuItem(new CoalDecomposeAction());
    JMenuItem roadMaxBreak = new JMenuItem(new MaxBreakAction());
    JMenuItem roadBookmark2 = new JMenuItem("=> go to road #2 (50K + 100K)");
    JMenuItem roadGaussianSmoothing = new JMenuItem(
        new GaussianSmoothingAction());
    JMenuItem roadBookmark3 = new JMenuItem("=> go to road #3 (100K)");

    roadsMenu.add(roadBookmark1);
    roadsMenu.add(roadAccordeon);
    roadsMenu.add(roadCoalDecomposer);
    roadsMenu.add(roadMaxBreak);
    roadsMenu.add(roadBookmark2);
    roadsMenu.add(roadGaussianSmoothing);
    roadsMenu.add(roadBookmark3);

    // MENU NETWORK
    JMenuItem netBookmark1 = new JMenuItem("=> go to network #1");
    JMenuItem netBookmark2 = new JMenuItem("=> go to network #2");
    JMenuItem netBookmark3 = new JMenuItem("=> go to network #3");
    JMenuItem netBookmark4 = new JMenuItem("=> go to network #4");
    networkMenu.add(netBookmark1);
    networkMenu.add(new JMenuItem(new DeadEndEnvAction()));
    networkMenu.add(netBookmark2);
    networkMenu.add(new JMenuItem(new DeadEndGroupAction()));
    networkMenu.add(netBookmark3);
    networkMenu.add(new JMenuItem(new DeadEndSelectionAction()));
    networkMenu.add(netBookmark4);
    networkMenu.add(new JMenuItem(new RoundaboutDetectAction()));
    networkMenu.add(new JMenuItem(new RoundaboutCollapseAction()));
    networkMenu.add(new JMenuItem(new RiverStrokesDetectionAction()));
    networkMenu.add(new JMenuItem(new RiverStrokesSelectionAction()));

    // MENU URBAN
    JMenuItem urbanBookmark1 = new JMenuItem(
        "=> go to urban blocks #1 (25K + 40K)");
    JMenuItem buildingParametrisedDilatation = new JMenuItem(
        new ParametrisedDilatationAction());
    JMenuItem buildingDilatation = new JMenuItem(new DilatationAction());
    JMenuItem buildingSimplification = new JMenuItem(
        new SimplificationAction());
    JMenuItem buildingSquarring = new JMenuItem(new ShapeSquarringAction());
    JMenuItem blockBuildingsActivation = new JMenuItem(
        new BuildingsActivationAction());
    JMenuItem blockBuildingsElimination = new JMenuItem(
        new BuildingsEliminationAction());
    JMenuItem blockBuildingsDisplacement = new JMenuItem(
        new BuildingsDisplacementAction());
    JMenuItem urbanBookmark2 = new JMenuItem("=> go to urban blocks #2 (40K)");

    urbanMenu.add(urbanBookmark1);
    urbanMenu.add(buildingParametrisedDilatation);
    urbanMenu.add(buildingDilatation);
    urbanMenu.add(buildingSimplification);
    urbanMenu.add(buildingSquarring);
    urbanMenu.addSeparator();
    // urbanMenu.add(alignmentDetection);
    urbanMenu.add(blockBuildingsActivation);
    urbanMenu.add(blockBuildingsElimination);
    urbanMenu.add(blockBuildingsDisplacement);
    urbanMenu.add(urbanBookmark2);

    // BOOKMARKS ACTIVATION
    roadBookmark1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkRoads1 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkRoads1);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    roadBookmark2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkRoads2 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkRoads2);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    roadBookmark3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkRoads3 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkRoads3);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    urbanBookmark1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkUrban1 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkUrban1);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    urbanBookmark2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkUrban2 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkUrban2);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    netBookmark1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkNet1 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkNet1);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    netBookmark2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkNet2 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkNet2);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    netBookmark3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkNet3 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkNet3);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    netBookmark4.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (DemoPlugin.bookmarkNet4 != null) {
          try {
            DemoPlugin.bookmarks.zoomToBookmark(DemoPlugin.bookmarkNet4);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
  }

  // //////////////////////
  // GENERAL
  // //////////////////////

  private class InitialiseBookmarksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          CartAGenDB dataset = CartAGenDoc.getInstance().getCurrentDataset()
              .getCartAGenDB();
          DemoPlugin.bookmarks = new BookmarkSet(dataset,
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getSelectedProjectFrame());
          IEnvelope env1 = new GM_Envelope(892560.0, 894180.0, 2023840.0,
              2024930.0);
          DemoPlugin.bookmarkRoads1 = new Bookmark(dataset.getName(), env1,
              "roads1");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkRoads1);
          IEnvelope env2 = new GM_Envelope(887420.0, 888950.0, 2014210.0,
              2015240.0);
          DemoPlugin.bookmarkRoads2 = new Bookmark(dataset.getName(), env2,
              "roads2");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkRoads2);
          IEnvelope env3 = new GM_Envelope(890910.0, 893740.0, 2014120.0,
              2016040.0);
          DemoPlugin.bookmarkRoads3 = new Bookmark(dataset.getName(), env3,
              "roads3");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkRoads3);
          IEnvelope env4 = new GM_Envelope(893320.0, 893900.0, 2016810.0,
              2017200.0);
          DemoPlugin.bookmarkUrban1 = new Bookmark(dataset.getName(), env4,
              "urban1");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkUrban1);
          IEnvelope env5 = new GM_Envelope(893690.0, 894270.0, 2016880.0,
              2017270.0);
          DemoPlugin.bookmarkUrban2 = new Bookmark(dataset.getName(), env5,
              "urban2");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkUrban2);

          // network bookmarks
          IEnvelope envNet1 = new GM_Envelope(893528.0, 894111.0, 2016286.0,
              2016851.0);
          DemoPlugin.bookmarkNet1 = new Bookmark(dataset.getName(), envNet1,
              "network1");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkNet1);
          IEnvelope envNet2 = new GM_Envelope(889703.0, 891906.0, 2019827.0,
              2021961.0);
          DemoPlugin.bookmarkNet2 = new Bookmark(dataset.getName(), envNet2,
              "network2");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkNet2);
          IEnvelope envNet3 = new GM_Envelope(892940.0, 893523.0, 2016847.0,
              2017413.0);
          DemoPlugin.bookmarkNet3 = new Bookmark(dataset.getName(), envNet3,
              "network3");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkNet3);
          IEnvelope envNet4 = new GM_Envelope(893478.0, 894356.0, 2016544.0,
              2017395.0);
          DemoPlugin.bookmarkNet4 = new Bookmark(dataset.getName(), envNet4,
              "network4");
          DemoPlugin.bookmarks.addBookmark(DemoPlugin.bookmarkNet4);
        }
      });
      th.start();
    }

    public InitialiseBookmarksAction() {
      super();
      this.putValue(Action.NAME, "Initialise bookmarks");
    }

  }

  private class CreateAgentsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          AgentUtil.createAgentsInDataset(
              CartAGenDoc.getInstance().getCurrentDataset());
        }
      });
      th.start();
    }

    public CreateAgentsAction() {
      super();
      this.putValue(Action.NAME, "Create agents");
    }

  }

  private class DuplicateDatasetAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          // PersoKusayGUIComponent.duplicateDataSet();
        }
      });
      th.start();
    }

    public DuplicateDatasetAction() {
      super();
      this.putValue(Action.NAME, "Duplicate dataset (beta version)");
    }

  }

  // //////////////////////
  // MENU ROADS
  // //////////////////////

  private class AccordionAction extends AbstractAction {

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
            if (!(sel instanceof INetworkSection)) {
              continue;
            }
            /*
             * LineAccordion algo = new LineAccordion((INetworkSection) sel);
             * algo.compute(true);
             */
            // FIXME
          }
        }
      });
      th.start();
    }

    public AccordionAction() {
      super();
      this.putValue(Action.NAME, "Accordeon on selected roads");
    }

  }

  private class CoalDecomposeAction extends AbstractAction {

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
            if (!(sel instanceof INetworkSection)) {
              continue;
            }
            /*
             * LineCoalescenceDetection algo = new LineCoalescenceDetection(
             * (INetworkSection) sel); algo.compute();
             * 
             * for (ILineString ls : algo.getSections()) { IRoadLine road =
             * CartagenApplication.getInstance() .getCreationFactory()
             * .createRoadLine(ls, ((INetworkSection) sel).getImportance());
             * CartAGenDocOld.getInstance().getCurrentDataset().getRoads()
             * .add(road); } ((INetworkSection) sel).setDeleted(true);
             */
            // FIXME
          }
        }
      });
      th.start();
    }

    public CoalDecomposeAction() {
      super();
      this.putValue(Action.NAME, "Decompose selected roads with coalescence");
    }

  }

  private class MaxBreakAction extends AbstractAction {

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
            if (!(sel instanceof INetworkSection)) {
              continue;
            }
            /*
             * LineMaxBreak algo = new LineMaxBreak((INetworkSection) sel);
             * algo.compute(true);
             */
            // FIXME
          }
        }
      });
      th.start();
    }

    public MaxBreakAction() {
      super();
      this.putValue(Action.NAME, "Max Break on selected bends");
    }

  }

  private class GaussianSmoothingAction extends AbstractAction {

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
            if (!(sel instanceof INetworkSection)) {
              continue;
            }
            INetworkSection road = (INetworkSection) sel;
            double symbolWidth = SectionSymbol.getUsedSymbolWidth(road) / 2;
            double sigma = 75.0 * symbolWidth;

            ILineString filteredGeom = GaussianFilter.gaussianFilter(
                road.getGeom(), sigma,
                GeneralisationSpecifications.getRESOLUTION());
            road.setGeom(filteredGeom);
          }
        }
      });
      th.start();
    }

    public GaussianSmoothingAction() {
      super();
      this.putValue(Action.NAME, "Gaussian smoothing on selected roads");
    }

  }

  // //////////////////////
  // MENU NETWORKS
  // //////////////////////

  class DeadEndEnvAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      IPopulation<INetworkFace> popFaces = CartAGenDoc.getInstance()
          .getCurrentDataset().getNetworkFaces();
      IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<IGeneObj>();
      fc.addAll(popFaces);
      for (IFeature feat : SelectionUtil
          .getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
        if (feat instanceof INetworkSection) {
          INetworkFace networkFace = (INetworkFace) SpatialQuery
              .selectContains(feat.getGeom(), fc);
          DeadEndZoning zoning = new DeadEndZoning(networkFace.getGeom(),
              (INetworkSection) feat, 20.0);
          zoning.buildDeadEndZoning();

          for (DeadEndZone zone : zoning.getZones()) {
            CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                .addFeatureToGeometryPool(zone.getGeom(),
                    zone.getType().getColor(), 4);
          }
        }
      }
    }

    public DeadEndEnvAction() {
      super();
      this.putValue(Action.NAME, "Show dead-end neighbourhood");
    }
  }

  class DeadEndGroupAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      ILineString borderLine = CartAGenDoc.getInstance().getCurrentDataset()
          .getMasks().iterator().next().getGeom();

      HashSet<DeadEndGroup> impasses = DeadEndGroup.buildFromRoads(
          CartAGenDoc.getInstance().getCurrentDataset().getRoads(), borderLine);

      Random red = new Random();
      Random green = new Random();
      Random blue = new Random();
      for (DeadEndGroup impasse : impasses) {
        Color color = new Color(red.nextInt(254), green.nextInt(254),
            blue.nextInt(254));
        for (INetworkSection feat : impasse.getFeatures()) {
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(feat.getGeom(), color, 4);
        }
        for (INetworkSection feat : impasse.getLeafs()) {
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(impasse.getLeafNode(feat).getGeom(),
                  color, 2);
        }
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(impasse.getRootNode().getGeom(),
                Color.RED, 3);
      }
    }

    public DeadEndGroupAction() {
      super();
      this.putValue(Action.NAME, "Show dead-end groups");
    }
  }

  class DeadEndSelectionAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      ILineString borderLine = CartAGenDoc.getInstance().getCurrentDataset()
          .getMasks().iterator().next().getGeom();
      String distVal = JOptionPane.showInputDialog("Enter length threshold");
      double dist = new Double(distVal);
      HashSet<DeadEndGroup> impasses = DeadEndGroup.buildFromRoads(
          CartAGenDoc.getInstance().getCurrentDataset().getRoads(), borderLine);
      for (DeadEndGroup impasse : impasses) {
        /*
         * if (!CartagenApplication.getInstance().getFrame().getVisuPanel()
         * .getDisplayEnvelope().contains(impasse.getRootNode().getPosition()))
         * continue;
         */
        // test par feuille
        boolean allElim = true;
        for (INetworkSection leaf : impasse.getLeafs()) {
          double leafLength = leaf.getGeom().length();
          if (leafLength < dist) {
            leaf.eliminate();
          } else {
            allElim = false;
          }
        }

        // finally, if all leaves are eliminated, eliminate the rest
        if (allElim) {
          for (INetworkSection feat : impasse.getFeatures()) {
            feat.eliminate();
          }
        }
      }
    }

    public DeadEndSelectionAction() {
      super();
      this.putValue(Action.NAME, "Dead-end selection");
    }
  }

  class RoundaboutDetectAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CrossRoadDetection algo = new CrossRoadDetection();
      algo.detectRoundaboutsAndBranchingCartagen(
          CartAGenDoc.getInstance().getCurrentDataset());
      for (IRoundAbout roundabout : CartAGenDoc.getInstance()
          .getCurrentDataset().getRoundabouts()) {
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(roundabout.getGeom(), Color.PINK, 4);

        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(roundabout.getGeom(), Color.PINK, 4);
      }
      for (IBranchingCrossroad roundabout : CartAGenDoc.getInstance()
          .getCurrentDataset().getBranchings()) {
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(roundabout.getGeom(), Color.ORANGE, 4);

        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(roundabout.getGeom(), Color.ORANGE, 4);
      }
    }

    public RoundaboutDetectAction() {
      super();
      this.putValue(Action.NAME, "Roundabout detection");
    }
  }

  class RoundaboutCollapseAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CrossRoadDetection algo = new CrossRoadDetection();
      algo.detectRoundaboutsAndBranchingCartagen(
          CartAGenDoc.getInstance().getCurrentDataset());
      for (IRoundAbout roundabout : CartAGenDoc.getInstance()
          .getCurrentDataset().getRoundabouts()) {
        CollapseRoundabout collapse = new CollapseRoundabout(100.0, roundabout);
        collapse.collapseToPoint();
      }
    }

    public RoundaboutCollapseAction() {
      super();
      this.putValue(Action.NAME, "Roundabout collapse");
    }
  }

  class RiverStrokesDetectionAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
      for (IGeneObj feat : CartAGenDoc.getInstance().getCurrentDataset()
          .getWaterLines()) {
        if (feat.isEliminated()) {
          continue;
        }
        arcs.add((ArcReseau) feat.getGeoxObj());
      }
      RiverStrokesNetwork net = new RiverStrokesNetwork(arcs);
      net.findSourcesAndSinks();
      net.buildRiverStrokes();
      Random red = new Random();
      Random green = new Random();
      Random blue = new Random();
      for (Stroke stroke : net.getStrokes()) {
        /*
         * ((RiverStroke) stroke).computeHortonOrder(); if (((RiverStroke)
         * stroke).getHortonOrder() < 2) continue;
         */
        Color color = new Color(red.nextInt(254), green.nextInt(254),
            blue.nextInt(254));
        stroke.buildGeomStroke();
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(stroke.getGeomStroke(), color, 4);
      }
    }

    public RiverStrokesDetectionAction() {
      super();
      this.putValue(Action.NAME, "River Strokes detection");
    }
  }

  class RiverStrokesSelectionAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
      Map<ArcReseau, IWaterLine> map = new HashMap<ArcReseau, IWaterLine>();
      for (IGeneObj feat : CartAGenDoc.getInstance().getCurrentDataset()
          .getWaterLines()) {
        if (feat.isEliminated()) {
          continue;
        }
        arcs.add((ArcReseau) feat.getGeoxObj());
        map.put((ArcReseau) feat.getGeoxObj(), (IWaterLine) feat);
      }
      RiverStrokesNetwork net = new RiverStrokesNetwork(arcs);
      net.findSourcesAndSinks();
      net.buildRiverStrokes();

      StrokesParamFrame frame = new StrokesParamFrame(net, map);
      frame.setVisible(true);
    }

    public RiverStrokesSelectionAction() {
      super();
      this.putValue(Action.NAME, "River Strokes selection");
    }
  }

  class StrokesParamFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private final RiverStrokesNetwork net;
    private final Map<ArcReseau, IWaterLine> map;
    private final JSpinner spinHorton, spinLengthMin;
    private final JCheckBox chkBraided;

    public StrokesParamFrame(RiverStrokesNetwork net,
        Map<ArcReseau, IWaterLine> map) {
      super("River Strokes Selection");
      this.net = net;
      this.map = map;
      this.setSize(300, 300);
      this.spinHorton = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
      this.spinLengthMin = new JSpinner(
          new SpinnerNumberModel(2000.0, 200.0, 10000.0, 200.0));
      this.chkBraided = new JCheckBox("Eliminate braided strokes");
      JPanel panelParams = new JPanel();
      panelParams.add(new JLabel("Horton order"));
      panelParams.add(this.spinHorton);
      panelParams.add(Box.createHorizontalGlue());
      panelParams.add(new JLabel("Length min"));
      panelParams.add(this.spinLengthMin);
      panelParams.add(Box.createHorizontalGlue());
      panelParams.add(this.chkBraided);
      panelParams.setLayout(new BoxLayout(panelParams, BoxLayout.X_AXIS));

      JPanel pBoutons = new JPanel();
      JButton btnFermer = new JButton("OK");
      btnFermer.addActionListener(this);
      btnFermer.setActionCommand("ok");
      btnFermer.setPreferredSize(new Dimension(100, 50));
      JButton btnCharger = new JButton("Cancel");
      btnCharger.addActionListener(this);
      btnCharger.setActionCommand("cancel");
      btnCharger.setPreferredSize(new Dimension(100, 50));
      pBoutons.add(btnCharger);
      pBoutons.add(btnFermer);
      pBoutons.setLayout(new BoxLayout(pBoutons, BoxLayout.X_AXIS));

      this.getContentPane().add(panelParams);
      this.getContentPane().add(pBoutons);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        for (Stroke stroke : this.net.getStrokes()) {
          // selection on length
          if (((RiverStroke) stroke)
              .getLength() < (Double) this.spinLengthMin.getValue()) {
            for (ArcReseau arc : stroke.getFeatures()) {
              this.map.get(arc).eliminate();
            }
          }
          // selection on horton order
          ((RiverStroke) stroke).computeHortonOrder();
          if (((RiverStroke) stroke)
              .getHortonOrder() < (Integer) this.spinHorton.getValue()) {
            for (ArcReseau arc : stroke.getFeatures()) {
              this.map.get(arc).eliminate();
            }
          }

          if (this.chkBraided.isSelected()) {
            if (((RiverStroke) stroke).isBraided()) {
              for (ArcReseau arc : stroke.getFeatures()) {
                this.map.get(arc).eliminate();
              }
            }
          }
        }
      }
      this.setVisible(false);
    }

  }

  // //////////////////////
  // MENU URBAN
  // //////////////////////

  private class ParametrisedDilatationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Dilatation coeff", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double coef = 1.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            ab.setGeom(CommonAlgorithms.homothetie(ab.getGeom(), coef));
          }
        }
      });
      th.start();
    }

    public ParametrisedDilatationAction() {
      super();
      this.putValue(Action.NAME,
          "Parametrised dilatation of selected buildings");
    }

  }

  private class DilatationAction extends AbstractAction {

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
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            double area = ab.getGeom().area();
            if (area < GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT) {
              IBuildingAgent buildAgent = (IBuildingAgent) AgentUtil
                  .getAgentFromGeneObj(ab);
              if (buildAgent == null) {
                ab.eliminate();
              } else {
                buildAgent.deleteAndRegister();
              }
            } else {
              double aireMini = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
                  * Legend.getSYMBOLISATI0N_SCALE()
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000000.0;
              if (area < aireMini) {
                GM_Object geom = (GM_Object) CommonAlgorithms
                    .homothetie(ab.getGeom(), Math.sqrt(aireMini / area));
                ab.setGeom(geom);
              }
            }
          }
        }
      });
      th.start();
    }

    public DilatationAction() {
      super();
      this.putValue(Action.NAME, "Self dilatation of selected buildings");
    }

  }

  private class SimplificationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Minimal length of walls", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double coef = 10.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            ab.setGeom(
                SimplificationAlgorithm.simplification(ab.getGeom(), coef));
          }
        }
      });
      th.start();
    }

    public SimplificationAction() {
      super();
      this.putValue(Action.NAME, "Shape simplification of selected buildings");
    }

  }

  private class ShapeSquarringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Max angle tolerance", "Cartagen", JOptionPane.PLAIN_MESSAGE);
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
            try {
              new SquarringAction(ab, null, 1.0, tol, 500).compute();
            } catch (InterruptedException exc) {
            }
          }
        }
      });
      th.start();
    }

    public ShapeSquarringAction() {
      super();
      this.putValue(Action.NAME, "Squarring of selected buildings");
    }

  }

  private class BuildingsActivationAction extends AbstractAction {

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
            try {
              new MesoComponentsActivation<BuildingAgent>(ab, null, 1.0)
                  .compute();
            } catch (InterruptedException exc) {
            }
          }
        }
      });
      th.start();
    }

    public BuildingsActivationAction() {
      super();
      this.putValue(Action.NAME, "Generalise buildings of the selected blocks");
    }

  }

  private class BuildingsEliminationAction extends AbstractAction {

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
            ArrayList<IUrbanElement> builds = BuildingsDeletionCongestion
                .compute(ab, 1,
                    GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE,
                    GeneralisationSpecifications.RATIO_BLOCK_DENSITY);
            for (IUrbanElement build : builds) {
              build.eliminate();
            }
          }
        }
      });
      th.start();
    }

    public BuildingsEliminationAction() {
      super();
      this.putValue(Action.NAME, "Eliminate buildings in the selected blocks");
    }

  }

  private class BuildingsDisplacementAction extends AbstractAction {

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
            try {
              new BlockBuildingsDisplacementGAELAction(ab, null, 1.0).compute();
            } catch (InterruptedException exc) {
            }
          }
        }
      });
      th.start();
    }

    public BuildingsDisplacementAction() {
      super();
      this.putValue(Action.NAME, "Displace buildings in the selected blocks");
    }

  }

}
