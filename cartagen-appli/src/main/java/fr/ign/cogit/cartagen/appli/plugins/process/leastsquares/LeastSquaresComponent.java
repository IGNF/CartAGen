package fr.ign.cogit.cartagen.appli.plugins.process.leastsquares;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.diffusion.leastsquares.LeastSquaresDiffusion;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.cartagen.mrdb.enrichment.MakeNetworkPlanar;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.conflation.ConflationVector;
import fr.ign.cogit.geoxygene.contrib.conflation.RubberSheetingConflation;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.ConflationScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.LSVectorDisplConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.LSVectorDisplConstraint1;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCrossingConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCurvatureConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementDirConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.EndVertexStrategy;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSSideOrientConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSStiffnessConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class LeastSquaresComponent extends JMenu
        implements MouseListener, ChangeListener {

    /****/
    private static final long serialVersionUID = 1L;
    private Set<DefaultFeature> loadedVectors = new HashSet<DefaultFeature>();
    private static LeastSquaresComponent instance;
    private static LSScheduler sched = null;
    private JCheckBoxMenuItem chkVectorCaptureMode = new JCheckBoxMenuItem(
            "Displacement vector capture Mode");
    private List<IDirectPosition> vectorPoints;
    private boolean vectorCaptureMode = false;

    public LeastSquaresComponent() {
        // Exists only to defeat instantiation.
        super();
    }

    public static LeastSquaresComponent getInstance() {
        if (LeastSquaresComponent.instance == null) {
            LeastSquaresComponent.instance = new LeastSquaresComponent(
                    "Least Squares");
        }
        return LeastSquaresComponent.instance;
    }

    public LeastSquaresComponent(String title) {
        super(title);

        JMenu geneMenu = new JMenu("Generalisation");
        geneMenu.add(new JMenuItem(new LSGeneAction()));
        geneMenu.add(new JMenuItem(new GenSelectedAction()));
        JMenu conflationMenu = new JMenu("Conflation");
        conflationMenu.add(new JMenuItem(new ConflateRoadsAction()));
        conflationMenu.add(new JMenuItem(new RubberConflationAction()));
        conflationMenu.add(new JMenuItem(new ExportConflationAction()));
        conflationMenu.add(new JMenuItem(new ShowConflationVectorsAction()));
        conflationMenu.add(new JMenuItem(new ShowLSPointsAction()));
        conflationMenu.add(new JMenuItem(new ShowRubberVectorAtPtAction()));
        this.add(geneMenu);
        this.add(conflationMenu);
        this.addSeparator();
        JMenu diffMenu = new JMenu("Diffusion");
        diffMenu.add(chkVectorCaptureMode);
        chkVectorCaptureMode.addChangeListener(this);
        diffMenu.add(new JMenuItem(new LSDiffusionAction()));
        diffMenu.add(new JMenuItem(new LSSingleDiffusionAction()));
        this.add(diffMenu);
        this.add(new JMenuItem(new TestsAction()));
        JMenu embankmentMenu = new JMenu("Embankment Displacement");
        embankmentMenu.add(new JMenuItem(new LSEmbankmentAction(
                CartAGenPlugin.getInstance().getApplication())));
        this.add(embankmentMenu);
        LeastSquaresComponent.instance = this;
        this.vectorPoints = new ArrayList<IDirectPosition>();
    }

    public Set<DefaultFeature> getLoadedVectors() {
        return loadedVectors;
    }

    public void setLoadedVectors(Set<DefaultFeature> loadedVectors) {
        this.loadedVectors = loadedVectors;
    }

    class TestsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            GeOxygeneApplication application = CartAGenPlugin.getInstance()
                    .getApplication();
            StyledLayerDescriptor sld = application.getMainFrame()
                    .getSelectedProjectFrame().getSld();
            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            pool.setSld(sld);
            String options = "pczeBQ"; // on convertit les entrées dans le
                                       // format
                                       // pivot
            List<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
            List<TriangulationSegment> segments = new ArrayList<TriangulationSegment>();
            Map<IDirectPosition, TriangulationPoint> pointsMap = new HashMap<IDirectPosition, TriangulationPoint>();
            for (IFeature obj : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = obj.getGeom();
                List<Segment> objSegments = new ArrayList<Segment>();
                if (geom instanceof ILineString)
                    objSegments
                            .addAll(Segment.getSegmentList((ILineString) geom));
                if (geom instanceof IPolygon)
                    objSegments.addAll(Segment.getSegmentList((IPolygon) geom,
                            geom.coord().get(0)));
                for (Segment seg : objSegments) {
                    IDirectPosition pt1 = seg.getStartPoint();
                    IDirectPosition pt2 = seg.getEndPoint();
                    TriangulationPoint triPt1 = null, triPt2 = null;
                    if (!pointsMap.containsKey(pt1)) {
                        triPt1 = new TriangulationPointImpl(pt1);
                        points.add(triPt1);
                        pointsMap.put(pt1, triPt1);
                    } else
                        triPt1 = pointsMap.get(pt1);
                    if (!pointsMap.containsKey(pt2)) {
                        triPt2 = new TriangulationPointImpl(pt2);
                        points.add(triPt2);
                        pointsMap.put(pt2, triPt2);
                    } else
                        triPt2 = pointsMap.get(pt2);

                    segments.add(new TriangulationSegmentImpl(triPt1, triPt2));
                }
            }

            // on lance la triangulation
            Triangulation tri = new Triangulation(points, segments,
                    new TriangulationSegmentFactoryImpl(),
                    new TriangulationTriangleFactoryImpl());
            tri.compute(true,
                    application.getMainFrame().getSelectedProjectFrame()
                            .getLayerViewPanel().getViewport()
                            .getEnvelopeInModelCoordinates().getGeom(),
                    options);

            for (TriangulationSegment seg : tri.getSegments()) {
                pool.addFeatureToGeometryPool(seg.getGeom(), Color.RED, 3);
            }
            for (TriangulationPoint pt : tri.getPoints()) {
                pool.addFeatureToGeometryPool(pt.getGeom(), Color.ORANGE, 4);
            }

            /*
             * LeastSquaresDiffusion diff = new LeastSquaresDiffusion(null);
             * diff.setDefaultMapspecs(); MapspecsLS mapspecs =
             * diff.getMapspecs(); try { mapspecs.saveToXml(new
             * File("d:\\ls_default_mapspecs.xml")); } catch
             * (TransformerException | IOException e1) { e1.printStackTrace(); }
             */
        }

        public TestsAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Miscellaneous testing on Least Squares");
            this.putValue(Action.NAME, "Tests");
        }
    }

    class LSGeneAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            LeastSquaresFrame frame = new LeastSquaresFrame();
            frame.setVisible(true);
        }

        public LSGeneAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Least Squares generalisation with the complete configuration");
            this.putValue(Action.NAME, "Least Squares generalisation");
        }
    }

    class ConflateRoadsAction extends AbstractAction {
        private static final long serialVersionUID = 1412206733083449293L;

        @Override
        public void actionPerformed(ActionEvent arg0) {

            CartAGenDataSet dataset = CartAGenDoc.getInstance()
                    .getCurrentDataset();
            // make the road network planar
            try {
                Set<Class<? extends IGeneObj>> classes = new HashSet<Class<? extends IGeneObj>>();
                classes.add(RoadLine.class);
                MakeNetworkPlanar process = MakeNetworkPlanar.getInstance();
                process.setProcessedClasses(classes);
                process.execute(CartAGenDoc.getInstance().getCurrentDataset()
                        .getCartAGenDB());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // enrich the road network
            NetworkEnrichment.enrichNetwork(dataset, dataset.getRoadNetwork(),
                    false, dataset.getCartAGenDB().getGeneObjImpl()
                            .getCreationFactory());

            // construction des mapspecs LSA
            Set<String> contraintesMalleables = new HashSet<String>();
            contraintesMalleables.add(LSMovementConstraint.class.getName());
            contraintesMalleables.add(LSCurvatureConstraint.class.getName());
            contraintesMalleables.add(LSMovementDirConstraint.class.getName());
            contraintesMalleables.add(LSCrossingConstraint.class.getName());
            // contraintesMalleables.add(LSStiffnessConstraint.class.getName());
            Set<String> classesMalleables = new HashSet<String>();
            classesMalleables.add(IRoadLine.class.getName());
            Map<String[], Double> contraintesExternes = new HashMap<String[], Double>();

            Map<String, Double> poidsContraintes = new HashMap<String, Double>();
            poidsContraintes.put(LSMovementConstraint.class.getName(), 1.0);
            poidsContraintes.put(LSCurvatureConstraint.class.getName(), 16.0);
            poidsContraintes.put(LSCrossingConstraint.class.getName(), 16.0);
            poidsContraintes.put(LSMovementDirConstraint.class.getName(), 15.0);
            poidsContraintes.put(LSSideOrientConstraint.class.getName(), 5.0);
            poidsContraintes.put(LSStiffnessConstraint.class.getName(), 15.0);
            poidsContraintes.put(LSVectorDisplConstraint1.class.getName(),
                    15.0);

            MapspecsLS mapspecs = new MapspecsLS(
                    Legend.getSYMBOLISATI0N_SCALE(), new HashSet<IFeature>(),
                    new HashSet<String>(), new HashSet<String>(),
                    contraintesMalleables, contraintesExternes,
                    new HashSet<String>(), new HashSet<String>(),
                    classesMalleables, poidsContraintes);
            mapspecs.getSelectedObjects()
                    .addAll(SelectionUtil.getWindowObjects(
                            CartAGenPlugin.getInstance().getApplication(),
                            CartAGenDataSet.ROADS_POP));
            mapspecs.setDensStep(25.0);
            mapspecs.setFilter(true);
            mapspecs.setFilterThreshold(0.5);
            // puis on construit un scheduler
            sched = new ConflationScheduler(mapspecs, loadedVectors,
                    LSVectorDisplConstraint1.class);
            sched.setSolver(MatrixSolver.JAMA);
            // on lance la généralisation
            sched.triggerAdjustment(EndVertexStrategy.FIX, true);

            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            for (IGeometry geom : sched.getMapObjGeom().values()) {
                pool.addFeatureToGeometryPool(geom, Color.ORANGE, 2);
            }

            // clear the road enrichment
            NetworkEnrichment.destroyTopology(CartAGenDoc.getInstance()
                    .getCurrentDataset().getRoadNetwork());
        }

        public ConflateRoadsAction() {
            this.putValue(Action.NAME, "Test the conflation on roads");
        }
    }

    class RubberConflationAction extends AbstractAction {
        private static final long serialVersionUID = 1412206733083449293L;

        @Override
        public void actionPerformed(ActionEvent arg0) {

            // make the road network planar
            try {
                Set<Class<? extends IGeneObj>> classes = new HashSet<Class<? extends IGeneObj>>();
                classes.add(RoadLine.class);
                MakeNetworkPlanar process = MakeNetworkPlanar.getInstance();
                process.setProcessedClasses(classes);
                process.execute(CartAGenDoc.getInstance().getCurrentDataset()
                        .getCartAGenDB());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            CartAGenDataSet dataset = CartAGenDoc.getInstance()
                    .getCurrentDataset();
            // enrich the road network
            NetworkEnrichment.enrichNetwork(dataset, dataset.getRoadNetwork(),
                    false, dataset.getCartAGenDB().getGeneObjImpl()
                            .getCreationFactory());

            // construction de l'algorithme
            Set<ConflationVector> vectors = new HashSet<ConflationVector>();
            for (DefaultFeature feat : loadedVectors) {
                Vector2D vect = new Vector2D(feat.getGeom().coord().get(0),
                        feat.getGeom().coord().get(1));
                vectors.add(new ConflationVector(feat.getGeom().coord().get(0),
                        vect));
            }
            RubberSheetingConflation rubber = new RubberSheetingConflation(
                    CartAGenDoc.getInstance().getCurrentDataset()
                            .getRoadNetwork().getSections(),
                    vectors);
            rubber.conflation();

            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            for (IGeometry geom : rubber.getConflatedGeoms().values()) {
                pool.addFeatureToGeometryPool(geom, Color.BLUE, 2);
            }

            // clear the road enrichment
            NetworkEnrichment.destroyTopology(CartAGenDoc.getInstance()
                    .getCurrentDataset().getRoadNetwork());
        }

        public RubberConflationAction() {
            this.putValue(Action.NAME, "Conflate roads with rubber sheeting");
        }
    }

    class ExportConflationAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            String shapefileName = null;
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // initialisation après le choix du fichier
                File shapefile = fc.getSelectedFile();
                shapefileName = shapefile.getPath();
            } else {
                return;
            }
            try {
                if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
                    shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
                }
                ShapefileDataStore store = new ShapefileDataStore(
                        new File(shapefileName).toURI().toURL());

                // specify the geometry type
                String specs = "geom:"; //$NON-NLS-1$
                specs += AdapterFactory.toJTSGeometryType(ILineString.class)
                        .getSimpleName();

                String featureTypeName = shapefileName.substring(
                        shapefileName.lastIndexOf("/") + 1, //$NON-NLS-1$
                        shapefileName.lastIndexOf(".")); //$NON-NLS-1$
                featureTypeName = featureTypeName.replace('.', '_');
                SimpleFeatureType type = DataUtilities
                        .createType(featureTypeName, specs);
                store.createSchema(type);
                String typeName = store.getTypeNames()[0];
                SimpleFeatureSource featureSource = store
                        .getFeatureSource(typeName);
                Transaction t = new DefaultTransaction();
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                featureStore.setTransaction(t);
                List<SimpleFeature> list = new ArrayList<SimpleFeature>();
                int i = 1;
                for (IFeature feat : sched.getMapObjGeom().keySet()) {
                    if (feat.isDeleted()) {
                        continue;
                    }
                    List<Object> liste = new ArrayList<Object>(0);
                    // change the CRS if needed
                    IGeometry geom = feat.getGeom();
                    if ((geom instanceof ILineString)
                            && (geom.coord().size() < 2))
                        continue;
                    liste.add(AdapterFactory.toGeometry(new GeometryFactory(),
                            geom));
                    SimpleFeature simpleFeature = SimpleFeatureBuilder
                            .build(type, liste.toArray(), String.valueOf(i++));
                    list.add(simpleFeature);
                }
                SimpleFeatureCollection collection = new ListFeatureCollection(
                        type, list);
                featureStore.addFeatures(collection);
                t.commit();
                t.close();
                store.dispose();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (SchemaException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

        public ExportConflationAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Export conflated roads in a shapefile");
            this.putValue(Action.NAME, "Export conflated roads");
        }
    }

    class ShowConflationVectorsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            for (DefaultFeature vect : loadedVectors) {
                pool.addFeatureToGeometryPool(vect.getGeom(), Color.GREEN, 2);
            }

        }

        public ShowConflationVectorsAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Display conflation vectors in the geometry pool");
            this.putValue(Action.NAME, "Show conflation vectors");
        }
    }

    class ShowLSPointsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            for (IFeature point : sched.getPoints()) {
                if (((LSPoint) point).isFixed())
                    pool.addFeatureToGeometryPool(point.getGeom(), Color.PINK,
                            2);
                else if (((LSPoint) point).isCrossing())
                    pool.addFeatureToGeometryPool(point.getGeom(), Color.BLUE,
                            2);
                else
                    pool.addFeatureToGeometryPool(point.getGeom(), Color.RED,
                            2);
            }
            for (IFeature feat : SelectionUtil.getSelectedObjects(null)) {
                for (LSPoint pt : sched.getMapObjPts().get(feat)) {
                    System.out.println(pt);
                    System.out.println(pt.getInternalConstraints());
                }
            }
            for (int i = 0; i < sched.getSystemeGlobal()
                    .getColumnNumber(); i += 2) {
                LSConstraint constr = sched.getSystemeGlobal().getConstraints()
                        .get(i);
                if (constr instanceof LSCrossingConstraint) {
                    System.out.println("résidu: "
                            + sched.getSystemeGlobal().getResiduals().get(i)
                            + "  obs: "
                            + sched.getSystemeGlobal().getObsVector().get(i));
                }
                if (constr instanceof LSVectorDisplConstraint) {
                    System.out.println("résidu conflation: "
                            + sched.getSystemeGlobal().getResiduals().get(i)
                            + "  obs: "
                            + sched.getSystemeGlobal().getObsVector().get(i));
                }
            }
        }

        public ShowLSPointsAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Display least square points in the geometry pool");
            this.putValue(Action.NAME, "Show least square points");
        }
    }

    class ShowRubberVectorAtPtAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String xS = JOptionPane
                    .showInputDialog("x coordinate of the point");
            String yS = JOptionPane
                    .showInputDialog("y coordinate of the point");
            double x = Double.valueOf(xS);
            double y = Double.valueOf(yS);

            Set<ConflationVector> vectors = new HashSet<ConflationVector>();
            for (DefaultFeature feat : loadedVectors) {
                Vector2D vect = new Vector2D(feat.getGeom().coord().get(0),
                        feat.getGeom().coord().get(1));
                vectors.add(new ConflationVector(feat.getGeom().coord().get(0),
                        vect));
            }
            RubberSheetingConflation rubber = new RubberSheetingConflation(
                    CartAGenDoc.getInstance().getCurrentDataset()
                            .getRoadNetwork().getSections(),
                    vectors);
            IDirectPosition point = new DirectPosition(x, y);
            Vector2D vect = rubber.computeAggregatedVector(point);

            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();

            if (vect != null)
                pool.addVectorToGeometryPool(vect, point, Color.GREEN, 2);

        }

        public ShowRubberVectorAtPtAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Display conflation vectors from rubber sheeting at a point in the geometry pool");
            this.putValue(Action.NAME, "Show rubber sheeting vector at point");
        }
    }

    class LSDiffusionAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            IRoadLine selectedRoad = (IRoadLine) SelectionUtil
                    .getSelectedObjects(
                            CartAGenPlugin.getInstance().getApplication())
                    .iterator().next();
            ILineString newGeom = CommonAlgorithms
                    .translation(selectedRoad.getGeom(), 3.0, 3.0);
            CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                    .addFeatureToGeometryPool(newGeom, Color.GREEN, 2);
            Map<IFeature, IGeometry> changedFeats = new HashMap<IFeature, IGeometry>();
            changedFeats.put(selectedRoad, newGeom);
            LeastSquaresDiffusion diff = new LeastSquaresDiffusion(null);
            diff.setMinimumDisplacement(0.2);
            diff.setDefaultMapspecs();
            Map<IFeature, IGeometry> result = diff.applyDiffusion(changedFeats,
                    CartAGenDoc.getInstance().getCurrentDataset().getRoads());
            for (IGeometry geom : result.values()) {
                CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                        .addFeatureToGeometryPool(geom, Color.RED, 2);
            }
            CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame().getLayerViewPanel().validate();
        }

        public LSDiffusionAction() {
            this.putValue(Action.NAME, "Least Squares diffusion");
        }
    }

    class LSSingleDiffusionAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            GeOxygeneApplication application = CartAGenPlugin.getInstance()
                    .getApplication();
            StyledLayerDescriptor sld = application.getMainFrame()
                    .getSelectedProjectFrame().getSld();
            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            pool.setSld(sld);

            IFeature selected = SelectionUtil
                    .getSelectedObjects(
                            CartAGenPlugin.getInstance().getApplication())
                    .iterator().next();
            IDirectPositionList newCoord = new DirectPositionList();
            selected.getGeom().coord();
            newCoord.addAll(selected.getGeom().coord());
            // displace the vertex according to the vector
            int vertex = CommonAlgorithmsFromCartAGen
                    .getNearestVertexPositionFromPoint(selected.getGeom(),
                            vectorPoints.get(0));
            newCoord.set(vertex, vectorPoints.get(1));
            IPolygon newGeom = new GM_Polygon(new GM_LineString(newCoord));
            pool.addFeatureToGeometryPool(newGeom, Color.GREEN, 2);
            LeastSquaresDiffusion diff = new LeastSquaresDiffusion(null);
            diff.setMinimumDisplacement(0.2);
            diff.setDefaultMapspecs();
            IGeometry result = diff.applySingleDiffusion(selected, newGeom);
            pool.addFeatureToGeometryPool(result, Color.RED, 2);

            application.getMainFrame().getSelectedProjectFrame()
                    .getLayerViewPanel().validate();
        }

        public LSSingleDiffusionAction() {
            this.putValue(Action.NAME, "Least Squares single diffusion");
        }
    }

    class GenSelectedAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            GeOxygeneApplication application = CartAGenPlugin.getInstance()
                    .getApplication();
            StyledLayerDescriptor sld = application.getMainFrame()
                    .getSelectedProjectFrame().getSld();
            GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
                    .getGeometryPool();
            pool.setSld(sld);

            Collection<IFeature> selectedObjects = new HashSet<IFeature>();
            for (IFeature feat : SelectionUtil
                    .getSelectedObjects(application)) {
                CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                        .addFeatureToGeometryPool(feat.getGeom(), Color.GREEN,
                                2);
                selectedObjects.add(feat);
            }

            // on commence par créer des mapspecs
            MapspecsLS mapspecs;
            try {
                mapspecs = new MapspecsLS(new File(
                        "src/main/resources/xml/ls_default_mapspecs.xml"),
                        selectedObjects);

                // puis on construit un scheduler
                LSScheduler sched = new LSScheduler(mapspecs);

                // on lance la généralisation
                sched.triggerAdjustment(EndVertexStrategy.FIX, true);

            } catch (SAXException e1) {
                e1.printStackTrace();
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame().getLayerViewPanel().validate();
        }

        public GenSelectedAction() {
            this.putValue(Action.NAME, "Generalise selected objects");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        GeOxygeneApplication application = CartAGenPlugin.getInstance()
                .getApplication();
        ProjectFrame frame = application.getMainFrame()
                .getSelectedProjectFrame();
        if (chkVectorCaptureMode.isSelected() && !vectorCaptureMode) {
            this.vectorPoints.clear();
            frame.getLayerViewPanel().addMouseListener(this);
            vectorCaptureMode = true;
        } else if (!chkVectorCaptureMode.isSelected() && vectorCaptureMode) {
            this.vectorPoints.clear();
            frame.getLayerViewPanel().removeMouseListener(this);
            vectorCaptureMode = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            Point point = e.getPoint();
            try {
                IDirectPosition pos = CartAGenPlugin.getInstance()
                        .getApplication().getMainFrame()
                        .getSelectedProjectFrame().getLayerViewPanel()
                        .getViewport().toModelDirectPosition(point);
                if (vectorPoints.contains(pos))
                    return;
                vectorPoints.add(pos);
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }
    }

}
