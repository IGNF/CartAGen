package fr.ign.cogit.cartagen.appli.plugins.spatialanalysis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.ForkCrossRoad;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PlusCrossRoad;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStructureDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.SimpleCrossRoad;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.StarCrossRoad;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.TCrossRoad;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.YCrossRoad;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.IlotImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.NoeudRoutierImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayerFactory;

public class SpatialAnalysisComponent extends JMenu {

  private GeOxygeneApplication application = null;
  private Map<TronconDeRoute, IRoadLine> roadsMap = new HashMap<>();

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public SpatialAnalysisComponent(String title) {
    super(title);
    this.application = CartAGenPlugin.getInstance().getApplication();

    JMenu menuStrokes = new JMenu("Strokes");
    menuStrokes.add(new JMenuItem(new ShowStrokesAction()));
    this.add(menuStrokes);
    JMenu menuRoads = new JMenu("Roads");
    JMenu menuCross = new JMenu("Crossroads");
    menuCross.add(new JMenuItem(new ShowSimpleCrossroadsAction()));
    menuRoads.add(menuCross);
    JMenu menuRound = new JMenu("Roundabouts");
    menuRound.add(new JMenuItem(new RoundaboutSelAction()));
    menuRound.add(new JMenuItem(new RoundaboutAllAction()));
    menuRound.add(new JMenuItem(new RoundaboutBranchingAllAction()));
    menuRoads.add(menuRound);
    JMenu menuBranch = new JMenu("Branching Crossroads");
    menuBranch.add(new JMenuItem(new RoundaboutBranchingAllAction()));
    menuRoads.add(menuBranch);
    JMenu menuDual = new JMenu("Dual Carriageways");
    menuDual.add(new JMenuItem(new DualCarriagewaysAllAction()));
    menuDual.add(new JMenuItem(new DualCarriagewaysDefaultAction()));
    menuRoads.add(menuDual);
    JMenu menuEscape = new JMenu("Escape Crossroads");
    menuEscape.add(new JMenuItem(new ShowEscapeAction()));
    menuRoads.add(menuEscape);
    JMenu menuDeadEnd = new JMenu("Dead End Streets");
    menuDeadEnd.add(new JMenuItem(new DeadEndsWindowAction()));
    menuRoads.add(menuDeadEnd);
    JMenu menuRest = new JMenu("Rest Areas");
    menuRoads.add(menuRest);
    JMenu menuInterchange = new JMenu("Interchanges");
    menuInterchange.add(new JMenuItem(new ShowInterchangesAction()));
    menuRoads.add(menuInterchange);
    this.add(menuRoads);
    this.add(menuStrokes);
    this.addSeparator();
  }

  /**
   * Action abstraite qui lance les strokes sur la couche sélectionnée et les
   * affiche dans le geometry pool.
   * 
   * @author GTouya
   * 
   */
  class ShowStrokesAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      // enrich the network if necessary
      Set<IFeature> selectedObjs = SelectionUtil.getSelectedObjects(appli);
      if (selectedObjs.size() == 0)
        return;
      IFeature feature = selectedObjs.iterator().next();
      if (!(feature instanceof IGeneObj))
        return;
      INetwork net = dataset
          .getNetworkFromClass((Class<? extends IGeneObj>) feature.getClass());
      if (net.getNodes().size() == 0) {
        if (net.getSections().size() == 0) {
          for (IFeature section : selectedObjs)
            net.addSection((INetworkSection) section);
        }
        NetworkEnrichment.buildTopology(dataset, net, false);
      }

      HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
      HashSet<NoeudReseau> noeuds = new HashSet<NoeudReseau>();
      for (IFeature feat : selectedObjs) {
        if (feat instanceof IGeneObj) {
          arcs.add((ArcReseau) ((IGeneObj) feat).getGeoxObj());
          NoeudReseau noeudIni = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
              .getNoeudInitial();
          NoeudReseau noeudFin = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
              .getNoeudFinal();
          noeuds.add(noeudIni);
          noeuds.add(noeudFin);
        }
      }

      StrokesNetwork network = new StrokesNetwork(arcs);
      HashSet<String> attributeNames = new HashSet<String>();
      // attributeNames.add("nom");
      network.buildStrokes(attributeNames, 112.5, 45.0, true);

      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      Random red = new Random();
      Random green = new Random();
      Random blue = new Random();
      for (Stroke stroke : network.getStrokes()) {
        if (stroke.getLength() < 400.0) {
          continue;
        }
        if (stroke.getGeom().coord().size() == 1)
          continue;
        Color color = new Color(red.nextInt(254), green.nextInt(254),
            blue.nextInt(254));
        pool.addFeatureToGeometryPool(stroke.getGeomStroke(), color, 4);
      }
    }

    public ShowStrokesAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Show Strokes in the geometry pool from the selected objects");
      this.putValue(Action.NAME, "Show Strokes from selection");
    }
  }

  /**
   * Identifies the roundabouts in the selected road layer, and creates a new
   * layer with the roundabouts.
   * 
   * @author GTouya
   * 
   */
  class RoundaboutSelAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      // create the road feature collection from the selected features
      IFeatureCollection<TronconDeRoute> roads = new FT_FeatureCollection<>();
      Reseau res = new ReseauImpl();
      for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
        TronconDeRoute road = (TronconDeRoute) ((IRoadLine) feat).getGeoxObj();
        roads.add(road);
        roadsMap.put(road, (IRoadLine) feat);
      }
      // enrich the roads collection by building its topology

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(roads, true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      // create the node objects
      for (Noeud n : carteTopo.getPopNoeuds()) {
        NoeudRoutier noeud = new NoeudRoutierImpl(res, n.getGeometrie());
        for (Arc a : n.getEntrants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudFinal(noeud);
          noeud.getArcsEntrants().add((TronconDeRoute) a.getCorrespondant(0));
        }
        for (Arc a : n.getSortants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudInitial(noeud);
          noeud.getArcsSortants().add((TronconDeRoute) a.getCorrespondant(0));
        }
      }

      // create the blocks
      IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
      // use the same topology map
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();
      for (Face face : carteTopo.getListeFaces()) {
        blocks.add(new IlotImpl(face.getGeom()));
      }

      CrossRoadDetection detect = new CrossRoadDetection();
      detect.detectRoundaboutsAndBranching(roads, blocks, false);

      // cartagen objects creations
      IFeatureCollection<IRoadNode> nodes = detect
          .getNodesFromRoadsCartAGen(dataset.getRoads());
      // first the roundabouts
      for (RondPoint round : detect.getRoundabouts()) {
        dataset.getRoundabouts()
            .add(CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
                .getGeneObjImpl().getCreationFactory()
                .createRoundAbout(round, dataset.getRoads(), nodes));
      }

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName(CartAGenDataSet.ROUNDABOUTS_POP);

      factory.setGeometryType(IPolygon.class);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public RoundaboutSelAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create roundabouts in the selected roads and add them as a new layer");
      this.putValue(Action.NAME, "Create roundabouts in selected roads");
    }
  }

  /**
   * Identifies the roundabouts in the selected road layer, and creates a new
   * layer with the roundabouts.
   * 
   * @author GTouya
   * 
   */
  class RoundaboutAllAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      IFeatureCollection<TronconDeRoute> roads = new FT_FeatureCollection<>();
      Reseau res = new ReseauImpl();
      for (IRoadLine feat : dataset.getRoads()) {
        TronconDeRoute road = (TronconDeRoute) (feat).getGeoxObj();
        roads.add(road);
        roadsMap.put(road, feat);
      }
      // enrich the roads collection by building its topology

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(roads, true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      // create the node objects
      for (Noeud n : carteTopo.getPopNoeuds()) {
        NoeudRoutier noeud = new NoeudRoutierImpl(res, n.getGeometrie());
        for (Arc a : n.getEntrants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudFinal(noeud);
          noeud.getArcsEntrants().add((TronconDeRoute) a.getCorrespondant(0));
        }
        for (Arc a : n.getSortants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudInitial(noeud);
          noeud.getArcsSortants().add((TronconDeRoute) a.getCorrespondant(0));
        }
      }

      // create the blocks
      IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
      // use the same topology map
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();
      for (Face face : carteTopo.getListeFaces()) {
        blocks.add(new IlotImpl(face.getGeom()));
      }

      CrossRoadDetection detect = new CrossRoadDetection();
      detect.detectRoundaboutsAndBranchingCartagen(dataset);

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName(CartAGenDataSet.ROUNDABOUTS_POP);

      factory.setGeometryType(IPolygon.class);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public RoundaboutAllAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create roundabouts in all dataset roads and add them as a new layer");
      this.putValue(Action.NAME, "Create roundabouts in all dataset roads");
    }
  }

  /**
   * Identifies the roundabouts and the branching crossroads in the selected
   * road layer, and creates two new layers.
   * 
   * @author GTouya
   * 
   */
  class RoundaboutBranchingAllAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      IFeatureCollection<TronconDeRoute> roads = new FT_FeatureCollection<>();
      Reseau res = new ReseauImpl();
      for (IRoadLine feat : dataset.getRoads()) {
        TronconDeRoute road = (TronconDeRoute) (feat).getGeoxObj();
        roads.add(road);
        roadsMap.put(road, feat);
      }
      // enrich the roads collection by building its topology

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(roads, true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      // create the node objects
      for (Noeud n : carteTopo.getPopNoeuds()) {
        NoeudRoutier noeud = new NoeudRoutierImpl(res, n.getGeometrie());
        for (Arc a : n.getEntrants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudFinal(noeud);
          noeud.getArcsEntrants().add((TronconDeRoute) a.getCorrespondant(0));
        }
        for (Arc a : n.getSortants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudInitial(noeud);
          noeud.getArcsSortants().add((TronconDeRoute) a.getCorrespondant(0));
        }
      }

      // create the blocks
      IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
      // use the same topology map
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();
      for (Face face : carteTopo.getListeFaces()) {
        blocks.add(new IlotImpl(face.getGeom()));
      }

      CrossRoadDetection detect = new CrossRoadDetection();
      detect.detectRoundaboutsAndBranchingCartagen(dataset);

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName(CartAGenDataSet.ROUNDABOUTS_POP);

      factory.setGeometryType(IPolygon.class);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);

      factory.setModel(frame.getSld());
      factory.setName(CartAGenDataSet.BRANCHINGS_POP);

      factory.setGeometryType(IPolygon.class);
      Layer layer2 = factory.createLayer();
      frame.getSld().add(layer2);
    }

    public RoundaboutBranchingAllAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create roundaboutsand branching crossroads in all dataset roads and add them as new layesr");
      this.putValue(Action.NAME, "Create roundabouts and branching crossroads");
    }
  }

  /**
   * Identifies the dual carriageways in the selected road layer, and creates
   * two new layers.
   * 
   * @author GTouya
   * 
   */
  class DualCarriagewaysDefaultAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

      RoadStructureDetection algo = new RoadStructureDetection();
      IPopulation<IDualCarriageWay> duals = algo
          .detectAndBuildDualCarriageways("Dual Carriageways", -1);

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName("Dual Carriageways");
      factory.setGeometryType(IPolygon.class);
      dataset.addPopulation(duals);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public DualCarriagewaysDefaultAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create dual carriageways in all dataset roads with default parameters and add them as a new layer");
      this.putValue(Action.NAME, "Identify dual carriageways");
    }
  }

  /**
   * Identifies the dual carriageways in the selected road layer, and creates
   * two new layers.
   * 
   * @author GTouya
   * 
   */
  class DualCarriagewaysAllAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      DualCarriageWaysFrame frame = new DualCarriageWaysFrame(dataset);
      frame.setVisible(true);
    }

    public DualCarriagewaysAllAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create dual carriageways in all dataset roads and add them as a new layer");
      this.putValue(Action.NAME,
          "Identify dual carriageways with custom parameters");
    }

    class DualCarriageWaysFrame extends JFrame implements ActionListener {

      /****/
      private static final long serialVersionUID = 1L;
      private CartAGenDataSet dataset;
      private JSlider impSlider;
      private JSpinner elongSpinner, compSpinner, concSpinner, areaSpinner,
          widthSpinner;
      private JCheckBox checkDebug;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
          RoadStructureDetection algo = new RoadStructureDetection();
          algo.setElongLimit((Double) elongSpinner.getValue());
          algo.setAreaLimit((Double) areaSpinner.getValue());
          algo.setCompLimit((Double) compSpinner.getValue());
          algo.setConcLimit((Double) concSpinner.getValue());
          if (checkDebug.isSelected())
            algo.setDebugMode(true);
          IPopulation<IDualCarriageWay> duals = algo
              .detectAndBuildDualCarriageways("Dual Carriageways",
                  impSlider.getValue());

          // put the roundabouts in a new layer
          ProjectFrame frame = application.getMainFrame()
              .getSelectedProjectFrame();

          if (checkDebug.isSelected()) {
            ProjectFrame project = application.getMainFrame()
                .getSelectedProjectFrame();
            project.addUserLayer(algo.getFaces(), "faces", null);
          }

          NamedLayerFactory factory = new NamedLayerFactory();
          factory.setModel(frame.getSld());
          factory.setName("Dual Carriageways");
          factory.setGeometryType(IPolygon.class);
          dataset.addPopulation(duals);
          Layer layer = factory.createLayer();
          frame.getSld().add(layer);
          this.dispose();
        } else {
          this.dispose();
        }
      }

      public DualCarriageWaysFrame(CartAGenDataSet dataset)
          throws HeadlessException {
        super("Detect dual carriageways");
        this.dataset = dataset;
        this.setSize(550, 150);
        this.setPreferredSize(new Dimension(550, 150));

        JPanel panel1 = new JPanel();
        SpinnerModel areaModel = new SpinnerNumberModel(80000.0, 5000.0,
            500000.0, 5000.0);
        areaSpinner = new JSpinner(areaModel);
        areaSpinner.setMinimumSize(new Dimension(80, 20));
        areaSpinner.setMaximumSize(new Dimension(80, 20));
        areaSpinner.setPreferredSize(new Dimension(80, 20));
        impSlider = new JSlider(-1, 5, 5);
        impSlider.setPaintTicks(true);
        impSlider.setMajorTickSpacing(1);
        impSlider.setPaintLabels(true);
        impSlider.setPreferredSize(new Dimension(90, 40));
        impSlider.setMinimumSize(new Dimension(90, 40));
        impSlider.setMaximumSize(new Dimension(90, 40));
        impSlider.setToolTipText("-1 means motorways are not searched");
        checkDebug = new JCheckBox("Debug mode");
        checkDebug.setToolTipText(
            "Debug mode creates a layer with the road graph faces");
        panel1.add(new JLabel("motorway importance"));
        panel1.add(impSlider);
        panel1.add(Box.createHorizontalGlue());
        panel1.add(new JLabel("max area"));
        panel1.add(areaSpinner);
        panel1.add(Box.createHorizontalGlue());
        panel1.add(checkDebug);
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        JPanel panel2 = new JPanel();
        SpinnerModel elongModel = new SpinnerNumberModel(5.0, 0.0, 50.0, 0.2);
        elongSpinner = new JSpinner(elongModel);
        elongSpinner.setMinimumSize(new Dimension(50, 20));
        elongSpinner.setMaximumSize(new Dimension(50, 20));
        elongSpinner.setPreferredSize(new Dimension(50, 20));
        SpinnerModel compModel = new SpinnerNumberModel(0.1, 0.0, 1.0, 0.02);
        compSpinner = new JSpinner(compModel);
        compSpinner.setMinimumSize(new Dimension(50, 20));
        compSpinner.setMaximumSize(new Dimension(50, 20));
        compSpinner.setPreferredSize(new Dimension(50, 20));
        SpinnerModel concModel = new SpinnerNumberModel(0.8, 0.0, 1.0, 0.02);
        concSpinner = new JSpinner(concModel);
        concSpinner.setMinimumSize(new Dimension(50, 20));
        concSpinner.setMaximumSize(new Dimension(50, 20));
        concSpinner.setPreferredSize(new Dimension(50, 20));
        SpinnerModel widthModel = new SpinnerNumberModel(20.0, 0.0, 100.0, 1.0);
        widthSpinner = new JSpinner(widthModel);
        widthSpinner.setMinimumSize(new Dimension(50, 20));
        widthSpinner.setMaximumSize(new Dimension(50, 20));
        widthSpinner.setPreferredSize(new Dimension(50, 20));
        panel2.add(new JLabel("elongation"));
        panel2.add(elongSpinner);
        panel2.add(Box.createHorizontalGlue());
        panel2.add(new JLabel("compactness"));
        panel2.add(compSpinner);
        panel2.add(Box.createHorizontalGlue());
        panel2.add(new JLabel("concavity ratio"));
        panel2.add(concSpinner);
        panel2.add(Box.createHorizontalGlue());
        panel2.add(new JLabel("width"));
        panel2.add(widthSpinner);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
        JButton bouton0 = new JButton("OK");
        bouton0.addActionListener(this);
        bouton0.setActionCommand("OK");
        JButton bouton1 = new JButton("Cancel");
        bouton1.addActionListener(this);
        bouton1.setActionCommand("Cancel");
        panelBtn.add(bouton0);
        panelBtn.add(bouton1);

        this.getContentPane().add(panel1);
        this.getContentPane().add(Box.createVerticalGlue());
        this.getContentPane().add(panel2);
        this.getContentPane().add(Box.createVerticalGlue());
        this.getContentPane().add(panelBtn);
        this.getContentPane()
            .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.pack();
      }

    }
  }

  /**
   * Identifies the dead ends in the selected road layer, and creates two new
   * layers.
   * 
   * @author GTouya
   * 
   */
  class DeadEndsWindowAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      IFeatureCollection<IGeneObj> roads = new FT_FeatureCollection<>();
      for (IGeneObj feat : SelectionUtil.getWindowObjects(application,
          CartAGenDataSet.ROADS_POP)) {
        roads.add(feat);
      }
      // enrich the roads collection by building its topology
      NetworkEnrichment.buildTopology(dataset, dataset.getRoadNetwork(), false);

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(roads, true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);

      // create the blocks
      IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
      // use the same topology map
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();
      for (Face face : carteTopo.getListeFaces()) {
        blocks.add(new IlotImpl(face.getGeom()));
      }
      IPolygon windowGeom = application.getMainFrame().getSelectedProjectFrame()
          .getLayerViewPanel().getViewport().getEnvelopeInModelCoordinates()
          .getGeom();
      HashSet<DeadEndGroup> deadEnds = DeadEndGroup
          .buildFromRoads(dataset.getRoads(), windowGeom, carteTopo);
      IPopulation<DeadEndGroup> deadEndColl = new Population<DeadEndGroup>(
          "Dead End Groups");
      for (DeadEndGroup deadEnd : deadEnds) {
        deadEndColl.add(deadEnd);
      }

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName("Dead End Groups");
      factory.setGeometryType(IMultiCurve.class);
      dataset.addPopulation(deadEndColl);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public DeadEndsWindowAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create roundaboutsand branching crossroads in the window and add them as new layesr");
      this.putValue(Action.NAME, "Identify dead ends in the window");
    }
  }

  /**
   * Identifies the escape crossroads in the selected road layer, and show them
   * in the geometry pool.
   * 
   * @author GTouya
   * 
   */
  class ShowEscapeAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      // enrich the roads collection by building its topology

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(dataset.getRoads(), true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();

      CrossRoadDetection detect = new CrossRoadDetection();
      Collection<Face> escapes = detect.detectEscapeCrossroads(carteTopo);

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(frame.getSld());
      for (Face face : escapes)
        pool.addFeatureToGeometryPool(face.getGeom(), Color.RED, 4);

    }

    public ShowEscapeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Show escape crossroads in the geometry pool");
      this.putValue(Action.NAME, "Show escape crossroads");
    }
  }

  /**
   * Identifies the interchanges in the selected road layer, and show them in
   * the geometry pool.
   * 
   * @author GTouya
   * 
   */
  class ShowInterchangesAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      RoadStructureDetection detect = new RoadStructureDetection();
      Collection<IPolygon> interchanges = detect.detectInterchanges(4);
      System.out.println(
          interchanges.size() + " interchanges detected in the dataset");
      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(frame.getSld());
      for (IPolygon face : interchanges)
        pool.addFeatureToGeometryPool(face, Color.RED, 4);

      for (SimpleCrossRoad simple : detect.getSimples()) {
        if (simple instanceof ForkCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.RED, 4,
              "triangle");
        if (simple instanceof YCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.PINK, 4,
              "circle");
        if (simple instanceof TCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.ORANGE, 4,
              "square");
        if (simple instanceof PlusCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.MAGENTA, 4,
              "cross");
        if (simple instanceof StarCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.DARK_GRAY,
              4, "star");
      }

    }

    public ShowInterchangesAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Show interchanges in the geometry pool");
      this.putValue(Action.NAME, "Show interchanges");
    }
  }

  /**
   * Identifies the interchanges in the selected road layer, and show them in
   * the geometry pool.
   * 
   * @author GTouya
   * 
   */
  class ShowSimpleCrossroadsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

      // enrich the network if necessary
      NetworkEnrichment.buildTopology(dataset, dataset.getRoadNetwork(), false);
      Set<TronconDeRoute> roads = new HashSet<TronconDeRoute>();
      for (IRoadLine feat : dataset.getRoads()) {
        roads.add((TronconDeRoute) feat.getGeoxObj());
      }

      // put the roundabouts in a new layer
      ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(frame.getSld());

      CrossRoadDetection algo = new CrossRoadDetection();
      Set<SimpleCrossRoad> simples = algo.classifyCrossRoads(roads);
      for (SimpleCrossRoad simple : simples) {
        if (simple instanceof ForkCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.RED, 4,
              "triangle");
        if (simple instanceof YCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.PINK, 4,
              "circle");
        if (simple instanceof TCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.ORANGE, 4,
              "square");
        if (simple instanceof PlusCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.MAGENTA, 4,
              "cross");
        if (simple instanceof StarCrossRoad)
          pool.addPointFeatureToGeometryPool(simple.getGeom(), Color.DARK_GRAY,
              4, "star");
      }

    }

    public ShowSimpleCrossroadsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Show simple crossroads classification in the geometry pool");
      this.putValue(Action.NAME, "Show simple crossroads classification");
    }
  }
}
