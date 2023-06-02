/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.plugins.machinelearning;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.locationtech.jts.geom.Geometry;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.appli.agents.AgentConfigurationFrame;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * This plugin contains actions to test TensorFlow deep learning capabilities in
 * map generalization.
 * 
 * @author GTouya
 *
 */
public class TensorFlowPlugin extends JMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static TensorFlowPlugin instance = null;
    private static Logger logger = LogManager.getLogger(TensorFlowPlugin.class.getName());

    public TensorFlowPlugin() {
        // Exists only to defeat instantiation.
        super();
    }

    public static TensorFlowPlugin getInstance() {
        if (TensorFlowPlugin.instance == null) {
            TensorFlowPlugin.instance = new TensorFlowPlugin("TensorFlow");
        }
        return TensorFlowPlugin.instance;
    }

    public TensorFlowPlugin(String title) {
        super(title);
        TensorFlowPlugin.instance = this;

        this.add(new TestAction());
        this.add(new GenerateInitialImageAction());
        this.add(new ImageWithEnlargementAction());
        this.add(new BuildingAggrAction());
    }

    private class TestAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    IFeature building = SelectionUtil
                            .getFirstSelectedObject(CartAGenPlugin.getInstance().getApplication());
                    IPolygon polygon = (IPolygon) building.getGeom();
                    // first 512 image size
                    createImageFromPolygon(polygon, 512, 1);
                    // then 128 image size
                    createImageFromPolygon(polygon, 128, 1);
                    // then 64 image size
                    createImageFromPolygon(polygon, 64, 1);
                }
            });
            th.start();
        }

        public TestAction() {
            super();
            this.putValue(Action.NAME, "Custom test");
        }

    }

    private void createImageFromPolygon(IPolygon pol, int imageSize, int id) {

        Polygon shape = (Polygon) toPolygonShape(toImageCoords2(pol.exteriorLineString(), imageSize));

        // Generate an image with the initial building
        BufferedImage bi = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setBackground(Color.LIGHT_GRAY);
        g2d.clearRect(0, 0, imageSize, imageSize);

        if (shape != null) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fill(shape);
        }

        // introduce some noise in the background
        Random random = new Random();
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                // check if pixel is part of the background
                if (bi.getRGB(i, j) == Color.LIGHT_GRAY.getRGB()) {
                    int noise = random.nextInt(3);
                    bi.setRGB(i, j, new Color(Color.LIGHT_GRAY.getRed() - noise, Color.LIGHT_GRAY.getGreen() - noise,
                            Color.LIGHT_GRAY.getBlue() - noise).getRGB());
                }
            }
        }

        File outputfile = new File("F://tensorflow//building_" + imageSize + "_" + id + ".png");
        try {
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class BuildingAggrAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
                    try {
                        // get the building population

                        IPopulation<IBuilding> pop = new Population<IBuilding>();
                        for (IBuilding b : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
                            if (b.getGeom() != null)
                                pop.add(b);
                        }
                        AdjacencyClustering clusters = new AdjacencyClustering(pop);
                        System.out.println("start clustering");
                        Set<Set<IGeneObj>> clusterSet = clusters.getClusters();
                        System.out.println(clusterSet.size() + " clusters");
                        for (Set<IGeneObj> cluster : clusterSet) {
                            IBuilding remainingB = null;
                            double maxArea = 0;
                            List<Geometry> list = new ArrayList<Geometry>();
                            for (IGeneObj build : cluster) {
                                list.add(JtsGeOxygene.makeJtsGeom(build.getGeom()));
                                build.eliminate();
                                if (build.getGeom().area() > maxArea) {
                                    maxArea = build.getGeom().area();
                                    remainingB = (IBuilding) build;
                                }
                            }
                            if (remainingB == null) {
                                return;
                            }
                            // union of the geometries
                            Geometry jtsUnion = JtsAlgorithms.union(list);
                            IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
                            if (union instanceof IPolygon) {
                                remainingB.cancelElimination();
                                remainingB.setGeom(union);
                            } else if (union instanceof IMultiSurface) {
                                for (int i = 0; i < ((IMultiSurface<IOrientableSurface>) union).getList().size(); i++) {
                                    IGeometry newGeom = ((IMultiSurface<IOrientableSurface>) union).getList().get(i);
                                    if (i == 0) {
                                        remainingB.cancelElimination();
                                        remainingB.setGeom(newGeom);
                                    } else {
                                        IBuilding newBuilding = dataset.getCartAGenDB().getGeneObjImpl()
                                                .getCreationFactory().createBuilding((IPolygon) newGeom,
                                                        remainingB.getNature(), remainingB.getBuildingCategory());
                                        pop.add(newBuilding);
                                    }
                                }
                            }
                            // update initial geometry
                            remainingB.setInitialGeom(remainingB.getGeom());
                            System.out.println("1 cluster traité");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            th.start();
        }

        public BuildingAggrAction() {
            super();
            this.putValue(Action.NAME, "Aggregate adjacent buildings");
        }

    }

    private class GenerateInitialImageAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    int imageSize = 128;
                    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
                    Legend.setSYMBOLISATI0N_SCALE(50000.0);

                    // first create agents from the buildings of the dataset
                    AgentUtil.createAgentAgentsInDataset(dataset);
                    AgentConfigurationFrame.getInstance().validateValues();
                    AgentUtil.instanciateConstraints();
                    // initialisation
                    AgentGeneralisationScheduler.getInstance().initList();
                    // attach an observer to the tree-based lifecycle to be able
                    // to stop
                    // during the process and log the generalisation
                    TreeExplorationLifeCycle.getInstance()
                            .attach((AgentObserver) CartAGenPlugin.getInstance().getApplication());
                    // generalise all buildings
                    for (IBuilding building : dataset.getBuildings()) {
                        if (building.isEliminated())
                            continue;

                        // then generalize the geometry
                        GeographicAgentGeneralisation ago = AgentUtil.getAgentFromGeneObj(building);
                        if (ago == null) {
                            continue;
                        }
                        logger.info("Chargement de " + ago);
                        AgentGeneralisationScheduler.getInstance().add(ago);
                    }
                    System.out.println(
                            AgentGeneralisationScheduler.getInstance().getList().size() + " features in the scheduler");

                    // run generalisation
                    ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

                    List<AgentGeneralisationScheduler> futureList = new ArrayList<>();
                    futureList.add(AgentGeneralisationScheduler.getInstance());

                    try {
                        List<Future<Integer>> futures = service.invokeAll(futureList);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    service.shutdown();

                    // then loop on the buildings to create examples from each
                    for (IBuilding building : dataset.getBuildings()) {
                        // get the initial geometry of the building
                        IPolygon polygon = (IPolygon) building.getInitialGeom();
                        Polygon shape = (Polygon) toPolygonShape(
                                toImageCoords2(polygon.exteriorLineString(), imageSize));

                        // Generate an image with the initial building
                        BufferedImage bi = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = bi.createGraphics();
                        g2d.setBackground(Color.LIGHT_GRAY);
                        g2d.clearRect(0, 0, imageSize, imageSize);

                        if (shape != null) {
                            g2d.setColor(Color.DARK_GRAY);
                            g2d.fill(shape);
                        }

                        // introduce some noise in the background
                        Random random = new Random();
                        for (int i = 0; i < imageSize; i++) {
                            for (int j = 0; j < imageSize; j++) {
                                // check if pixel is part of the background
                                if (bi.getRGB(i, j) == Color.LIGHT_GRAY.getRGB()) {
                                    int noise = random.nextInt(3);
                                    bi.setRGB(i, j,
                                            new Color(Color.LIGHT_GRAY.getRed() - noise,
                                                    Color.LIGHT_GRAY.getGreen() - noise,
                                                    Color.LIGHT_GRAY.getBlue() - noise).getRGB());
                                    /*
                                     * bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.getRGB()); if (noise ==
                                     * 1) bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.brighter().getRGB());
                                     * else if (noise == 2) bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.darker().getRGB());
                                     */
                                }
                            }
                        }

                        File outputfile = new File("F://tensorflow//building_" + building.getId() + ".png");
                        try {
                            ImageIO.write(bi, "png", outputfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // AgentGeneralisationScheduler.getInstance().activate();

                        // write the new geometry in an image file
                        BufferedImage biGen = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2dGen = biGen.createGraphics();
                        g2dGen.setBackground(Color.LIGHT_GRAY);
                        g2dGen.clearRect(0, 0, imageSize, imageSize);
                        IPolygon genGeom = building.getGeom();
                        Polygon genShape = (Polygon) toPolygonShape(
                                toImageCoords2(genGeom.exteriorLineString(), imageSize));

                        if (genShape != null) {
                            g2dGen.setColor(Color.DARK_GRAY);
                            g2dGen.fill(genShape);
                        }

                        // introduce some noise in the background
                        for (int i = 0; i < imageSize; i++) {
                            for (int j = 0; j < imageSize; j++) {
                                // check if pixel is part of the background
                                if (biGen.getRGB(i, j) == Color.LIGHT_GRAY.getRGB()) {
                                    int noise = random.nextInt(3);
                                    bi.setRGB(i, j,
                                            new Color(Color.LIGHT_GRAY.getRed() - noise,
                                                    Color.LIGHT_GRAY.getGreen() - noise,
                                                    Color.LIGHT_GRAY.getBlue() - noise).getRGB());
                                    /*
                                     * if (noise == 1) biGen.setRGB(i, j,
                                     * Color.LIGHT_GRAY.brighter().getRGB());
                                     * else if (noise == 2) biGen.setRGB(i, j,
                                     * Color.LIGHT_GRAY.darker().getRGB());
                                     */
                                }
                            }
                        }

                        File outputfile2 = new File("F://tensorflow//building_" + building.getId() + "_output.png");
                        try {
                            ImageIO.write(biGen, "png", outputfile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th.start();
        }

        public GenerateInitialImageAction() {
            super();
            this.putValue(Action.NAME, "Generate initial images for buildings");
        }

    }

    private IDirectPositionList toImageCoords(ILineString line, int imageSize) {
        IDirectPositionList imageCoords = new DirectPositionList();

        // xMin is imageSize/3, xMax is 2*imageSize/3, same for y coordinate.
        // there is a need for a translation and a homothetic transformation
        IEnvelope env = line.getEnvelope();
        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = imageSize / (3 * env.width());
        } else
            ratio = imageSize / (3 * env.length());
        for (IDirectPosition dp : line.coord()) {
            double x = (dp.getX() - env.minX()) * ratio + imageSize / 3;
            double y = (dp.getY() - env.minY()) * ratio + imageSize / 3;
            if (y < imageSize / 2)
                y = y + 2 * (imageSize / 2 - y);
            else
                y = y - 2 * (y - imageSize / 2);
            imageCoords.add(new DirectPosition(x, y));
        }
        return imageCoords;
    }

    /**
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolygonShape(final IDirectPositionList viewDirectPositionList) {
        int numPoints = viewDirectPositionList.size();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            xpoints[i] = (int) p.getX();
            ypoints[i] = (int) p.getY();
        }
        return new Polygon(xpoints, ypoints, numPoints);
    }

    private IDirectPositionList toImageCoords2(ILineString line, int imageSize) {
        IDirectPositionList imageCoords = new DirectPositionList();

        // xMin is 10, xMax is imageSize-10, same for y coordinate.
        // there is a need for a translation and a homothetic transformation
        IEnvelope env = line.getEnvelope();
        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = (imageSize - 20) / env.width();
        } else
            ratio = (imageSize - 20) / env.length();
        for (IDirectPosition dp : line.coord()) {
            double x = (dp.getX() - env.minX()) * ratio + 10;
            double y = (dp.getY() - env.minY()) * ratio + 10;
            if (y < imageSize / 2)
                y = y + 2 * (imageSize / 2 - y);
            else
                y = y - 2 * (y - imageSize / 2);
            imageCoords.add(new DirectPosition(x, y));
        }
        return imageCoords;
    }

    private IDirectPositionList toImageCoordsAreaMin(IPolygon polygon, int imageSize, double areaMin) {
        IDirectPositionList imageCoords = new DirectPositionList();

        // xMin is 10, xMax is imageSize-10, same for y coordinate when the
        // building
        // reaches the areaMin ratio (the building is not enlarged). If the
        // building
        // is smaller, it is displayed smaller on the image.
        // there is a need for a translation and a homothetic transformation in
        // each
        // case.
        double enlargementRatio = polygon.area() / areaMin;

        IEnvelope env = polygon.getEnvelope();

        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = (imageSize - 20) / env.width();
        } else
            ratio = (imageSize - 20) / env.length();
        if (enlargementRatio < 1.0)
            ratio = ratio * enlargementRatio;
        for (IDirectPosition dp : polygon.exteriorLineString().coord()) {
            double x = (dp.getX() - env.minX()) * ratio + 10;
            double y = (dp.getY() - env.minY()) * ratio + 10;
            if (y < imageSize / 2)
                y = y + 2 * (imageSize / 2 - y);
            else
                y = y - 2 * (y - imageSize / 2);
            imageCoords.add(new DirectPosition(x, y));
        }

        return imageCoords;
    }

    private class ImageWithEnlargementAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    int imageSize = 128;
                    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
                    Legend.setSYMBOLISATI0N_SCALE(30000.0);

                    // first create agents from the buildings of the dataset
                    AgentUtil.createAgentAgentsInDataset(dataset);
                    AgentConfigurationFrame.getInstance().validateValues();
                    AgentUtil.instanciateConstraints();
                    // initialisation
                    AgentGeneralisationScheduler.getInstance().initList();
                    // attach an observer to the tree-based lifecycle to be able
                    // to stop
                    // during the process and log the generalisation
                    TreeExplorationLifeCycle.getInstance()
                            .attach((AgentObserver) CartAGenPlugin.getInstance().getApplication());
                    // generalise all buildings
                    for (IBuilding building : dataset.getBuildings()) {
                        if (building.isEliminated())
                            continue;

                        // then generalize the geometry
                        GeographicAgentGeneralisation ago = AgentUtil.getAgentFromGeneObj(building);
                        if (ago == null) {
                            continue;
                        }
                        logger.info("Chargement de " + ago);
                        AgentGeneralisationScheduler.getInstance().add(ago);
                    }
                    System.out.println(
                            AgentGeneralisationScheduler.getInstance().getList().size() + " features in the scheduler");

                    // run generalisation
                    ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

                    List<AgentGeneralisationScheduler> futureList = new ArrayList<>();
                    futureList.add(AgentGeneralisationScheduler.getInstance());

                    try {
                        List<Future<Integer>> futures = service.invokeAll(futureList);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    service.shutdown();

                    double areaMin = GeneralisationSpecifications.BUILDING_MIN_AREA * Legend.getSYMBOLISATI0N_SCALE()
                            * Legend.getSYMBOLISATI0N_SCALE() / 1000000.0;

                    // then loop on the buildings to create examples from each
                    for (IBuilding building : dataset.getBuildings()) {
                        if (building.isEliminated())
                            continue;
                        // get the initial geometry of the building
                        IPolygon polygon = (IPolygon) building.getInitialGeom();
                        Polygon shape = (Polygon) toPolygonShape(toImageCoordsAreaMin(polygon, imageSize, areaMin));

                        // Generate an image with the initial building
                        BufferedImage bi = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = bi.createGraphics();
                        g2d.setBackground(Color.LIGHT_GRAY);
                        g2d.clearRect(0, 0, imageSize, imageSize);

                        if (shape != null) {
                            g2d.setColor(Color.DARK_GRAY);
                            g2d.fill(shape);
                        }

                        // introduce some noise in the background
                        Random random = new Random();
                        for (int i = 0; i < imageSize; i++) {
                            for (int j = 0; j < imageSize; j++) {
                                // check if pixel is part of the background
                                if (bi.getRGB(i, j) == Color.LIGHT_GRAY.getRGB()) {
                                    int noise = random.nextInt(3);
                                    bi.setRGB(i, j,
                                            new Color(Color.LIGHT_GRAY.getRed() - noise,
                                                    Color.LIGHT_GRAY.getGreen() - noise,
                                                    Color.LIGHT_GRAY.getBlue() - noise).getRGB());
                                    /*
                                     * bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.getRGB()); if (noise ==
                                     * 1) bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.brighter().getRGB());
                                     * else if (noise == 2) bi.setRGB(i, j,
                                     * Color.LIGHT_GRAY.darker().getRGB());
                                     */
                                }
                            }
                        }

                        File outputfile = new File("F://tensorflow//building_" + building.getId() + ".png");
                        try {
                            ImageIO.write(bi, "png", outputfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // AgentGeneralisationScheduler.getInstance().activate();

                        // write the new geometry in an image file
                        BufferedImage biGen = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2dGen = biGen.createGraphics();
                        g2dGen.setBackground(Color.LIGHT_GRAY);
                        g2dGen.clearRect(0, 0, imageSize, imageSize);
                        IPolygon genGeom = building.getGeom();
                        Polygon genShape = (Polygon) toPolygonShape(toImageCoordsAreaMin(genGeom, imageSize, areaMin));

                        if (genShape != null) {
                            g2dGen.setColor(Color.DARK_GRAY);
                            g2dGen.fill(genShape);
                        }

                        // introduce some noise in the background
                        for (int i = 0; i < imageSize; i++) {
                            for (int j = 0; j < imageSize; j++) {
                                // check if pixel is part of the background
                                if (biGen.getRGB(i, j) == Color.LIGHT_GRAY.getRGB()) {
                                    int noise = random.nextInt(3);
                                    bi.setRGB(i, j,
                                            new Color(Color.LIGHT_GRAY.getRed() - noise,
                                                    Color.LIGHT_GRAY.getGreen() - noise,
                                                    Color.LIGHT_GRAY.getBlue() - noise).getRGB());
                                    /*
                                     * if (noise == 1) biGen.setRGB(i, j,
                                     * Color.LIGHT_GRAY.brighter().getRGB());
                                     * else if (noise == 2) biGen.setRGB(i, j,
                                     * Color.LIGHT_GRAY.darker().getRGB());
                                     */
                                }
                            }
                        }

                        File outputfile2 = new File("F://tensorflow//building_" + building.getId() + "_output.png");
                        try {
                            ImageIO.write(biGen, "png", outputfile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th.start();
        }

        public ImageWithEnlargementAction() {
            super();
            this.putValue(Action.NAME, "Generate training images for buildings with enlargement");
        }

    }

}
