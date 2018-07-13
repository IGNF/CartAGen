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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.apache.log4j.Logger;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.appli.agents.AgentConfigurationFrame;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
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
 * @author GTouya
 *
 */
public class TensorFlowPlugin extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static TensorFlowPlugin instance = null;
  private static Logger logger = Logger
      .getLogger(TensorFlowPlugin.class.getName());

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
    this.add(new BuildingAggrAction());
  }

  private class TestAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {

          try (Graph g = new Graph()) {
            final String value = "Hello from " + TensorFlow.version();

            // Construct the computation graph with a single operation, a
            // constant
            // named "MyConst" with a value "value".
            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
              // The Java API doesn't yet include convenience functions for
              // adding operations.
              g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType())
                  .setAttr("value", t).build();
            } catch (UnsupportedEncodingException e1) {
              e1.printStackTrace();
            }

            // Execute the "MyConst" operation in a Session.
            try (Session s = new Session(g);
                Tensor output = s.runner().fetch("MyConst").run().get(0)) {
              System.out.println(new String(output.bytesValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
            }
          }

        }
      });
      th.start();
    }

    public TestAction() {
      super();
      this.putValue(Action.NAME, "Custom test");
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
          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();
          try {
            // get the building population

            IPopulation<IBuilding> pop = new Population<IBuilding>();
            for (IBuilding b : CartAGenDoc.getInstance().getCurrentDataset()
                .getBuildings()) {
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
                for (int i = 0; i < ((IMultiSurface<IOrientableSurface>) union)
                    .getList().size(); i++) {
                  IGeometry newGeom = ((IMultiSurface<IOrientableSurface>) union)
                      .getList().get(i);
                  if (i == 0) {
                    remainingB.cancelElimination();
                    remainingB.setGeom(newGeom);
                  } else {
                    IBuilding newBuilding = dataset.getCartAGenDB()
                        .getGeneObjImpl().getCreationFactory().createBuilding(
                            (IPolygon) newGeom, remainingB.getNature(),
                            remainingB.getBuildingCategory());
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

          int imageSize = 1000;
          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();
          Legend.setSYMBOLISATI0N_SCALE(50000.0);

          // first create agents from the buildings of the dataset
          AgentUtil.createAgentAgentsInDataset(dataset);
          AgentConfigurationFrame.getInstance().validateValues();
          AgentUtil.instanciateConstraints();
          // initialisation
          AgentGeneralisationScheduler.getInstance().initList();
          // attach an observer to the tree-based lifecycle to be able to stop
          // during the process and log the generalisation
          TreeExplorationLifeCycle.getInstance().attach(
              (AgentObserver) CartAGenPlugin.getInstance().getApplication());
          // generalise all buildings
          for (IBuilding building : dataset.getBuildings()) {
            if (building.isEliminated())
              continue;

            // then generalize the geometry
            GeographicAgentGeneralisation ago = AgentUtil
                .getAgentFromGeneObj(building);
            if (ago == null) {
              continue;
            }
            logger.info("Chargement de " + ago);
            AgentGeneralisationScheduler.getInstance().add(ago);
          }
          System.out.println(
              AgentGeneralisationScheduler.getInstance().getList().size()
                  + " features in the scheduler");

          // run generalisation
          ExecutorService service = Executors
              .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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
                toImageCoords(polygon.exteriorLineString(), imageSize));

            // Generate an image with the initial building
            BufferedImage bi = new BufferedImage(imageSize, imageSize,
                BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setBackground(Color.WHITE);
            g2d.clearRect(0, 0, imageSize, imageSize);

            if (shape != null) {
              g2d.setColor(Color.DARK_GRAY);
              g2d.fill(shape);
            }

            File outputfile = new File(
                "F://tensorflow//building_" + building.getId() + ".png");
            try {
              ImageIO.write(bi, "png", outputfile);
            } catch (IOException e) {
              e.printStackTrace();
            }

            // AgentGeneralisationScheduler.getInstance().activate();

            // write the new geometry in an image file
            BufferedImage biGen = new BufferedImage(imageSize, imageSize,
                BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dGen = biGen.createGraphics();
            g2dGen.setBackground(Color.WHITE);
            g2dGen.clearRect(0, 0, imageSize, imageSize);
            IPolygon genGeom = building.getGeom();
            Polygon genShape = (Polygon) toPolygonShape(
                toImageCoords(genGeom.exteriorLineString(), imageSize));

            if (genShape != null) {
              g2dGen.setColor(Color.DARK_GRAY);
              g2dGen.fill(genShape);
            }

            File outputfile2 = new File(
                "F://tensorflow//building_" + building.getId() + "_output.png");
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
     * @param viewDirectPositionList a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolygonShape(
        final IDirectPositionList viewDirectPositionList) {
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

    public GenerateInitialImageAction() {
      super();
      this.putValue(Action.NAME, "Generate initial images for buildings");
    }

  }

}
