package fr.ign.cogit.cartagen.appli.plugins.evaluation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.media.jai.TiledImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.appli.collagen.ComputeMonitorsFrame;
import fr.ign.cogit.cartagen.appli.plugins.process.CollaGenComponent;
import fr.ign.cogit.cartagen.collagen.components.translator.ConstraintsInstanciation;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.clutter.RasterClutterMethod;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.ImageFileFilter;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridTessellation;
import fr.ign.cogit.carto.evaluation.clutter.MapLegibilityMethod;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.ontology.owl.OwlUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class EvaluationComponent extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public EvaluationComponent(String title) {
    super(title);

    JMenu clutterMenu = new JMenu("Clutter");
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterAction()));
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterFileAction()));
    clutterMenu.add(new JMenuItem(new GridEdgeDensityClutterAction()));
    clutterMenu.add(new JMenuItem(new OlssonMapLegibilityAction()));
    clutterMenu.add(new JMenuItem(new TopferAction()));
    JMenu socialMenu = new JMenu("Social Welfare Evaluation");
    socialMenu.add(new JMenuItem(new ComputeMonitorsAction()));
    socialMenu.add(new JMenuItem(new UpdateMonitorsAction()));
    socialMenu.add(new JMenuItem(new ExportMonitorsAction()));

    this.add(clutterMenu);
    this.add(socialMenu);
  }

  class EdgeDensityClutterAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      LayerViewPanel panel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      Color bg = panel.getBackground();
      BufferedImage image = new BufferedImage(panel.getWidth(),
          panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      graphics.setColor(bg);
      graphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
      ((MultithreadedRenderingManager) panel.getRenderingManager())
          .copyTo(graphics);
      panel.paintOverlays(graphics);
      graphics.dispose();

      TiledImage pImage = new TiledImage(image, true);
      RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
      System.out.println(clutterMethod.getEdgeDensityClutter());
    }

    public EdgeDensityClutterAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute an Edge Density Clutter global measure of the current displayed map as an image");
      this.putValue(Action.NAME, "Compute Edge Density Clutter of the map");
    }
  }

  class EdgeDensityClutterFileAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the filename
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ImageFileFilter());
      int returnVal = fc.showDialog(null, "Choose the image to assess");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File path = fc.getSelectedFile();
      BufferedImage image;
      try {
        image = ImageIO.read(path);
        TiledImage pImage = new TiledImage(image, true);
        RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
        System.out.println(clutterMethod.getEdgeDensityClutter());
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public EdgeDensityClutterFileAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute an Edge Density Clutter global measure of the given file image");
      this.putValue(Action.NAME, "Compute Edge Density Clutter of the file");
    }
  }

  class GridEdgeDensityClutterAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      LayerViewPanel panel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      Color bg = panel.getBackground();
      BufferedImage image = new BufferedImage(panel.getWidth(),
          panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      graphics.setColor(bg);
      graphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
      ((MultithreadedRenderingManager) panel.getRenderingManager())
          .copyTo(graphics);
      panel.paintOverlays(graphics);
      graphics.dispose();

      TiledImage pImage = new TiledImage(image, true);
      RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
      Map<Integer, Map<Integer, Double>> result = clutterMethod
          .getGridEdgeDensityClutter(10);
      IEnvelope env = panel.getViewport().getEnvelopeInModelCoordinates();
      StyledLayerDescriptor sld = panel.getProjectFrame().getSld();
      GeometryPool pool = new GeometryPool(sld.getDataSet(), sld);
      pool.setSld(sld);
      pool.addGridValueToPool(result, Color.RED, env);
    }

    public GridEdgeDensityClutterAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute a grid partition of the current displayed map and compute an Edge Density Clutter measure in each cell of the grid");
      this.putValue(Action.NAME,
          "Compute a Grid Edge Density Clutter of the map");
    }
  }

  class OlssonMapLegibilityAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      LayerViewPanel panel = pFrame.getLayerViewPanel();

      MapLegibilityMethod method = new MapLegibilityMethod(pFrame.getSld(),
          panel.getViewport().getEnvelopeInModelCoordinates());
      method.setScale(Legend.getSYMBOLISATI0N_SCALE());
      method.setUseOverlap(true);

      GridTessellation<Boolean> grid = method
          .getOlssonThresholdLegibility(50.0);

      // Layer layer = new NamedLayer(pFrame.getSld(), "legibilityGrid");
      Layer layer = pFrame.getSld().createLayer("legibilityGrid",
          IPolygon.class, Color.RED);
      UserStyle style = new UserStyle();
      // style.setName("Style créé pour le layer legibilityGrid");//$NON-NLS-1$
      FeatureTypeStyle fts = new FeatureTypeStyle();
      Rule rule = LayerFactory.createRule(IPolygon.class, Color.RED.darker(),
          Color.RED, 0.8f, 0.8f, 1.0f);
      rule.setTitle("grid display false");
      Filter filter = new PropertyIsEqualTo(new PropertyName("value"),
          new Literal("false"));
      rule.setFilter(filter);
      fts.getRules().add(rule);
      Rule rule2 = LayerFactory.createRule(IPolygon.class, Color.GREEN.darker(),
          Color.GREEN, 0.8f, 0.5f, 1.0f);
      rule2.setTitle("grid display true");
      Filter filter2 = new PropertyIsEqualTo(new PropertyName("value"),
          new Literal("true"));
      rule2.setFilter(filter2);
      fts.getRules().add(rule2);
      style.getFeatureTypeStyles().add(fts);
      layer.getStyles().add(style);
      IPopulation<IFeature> pop = new Population<>("legibilityGrid");
      pop.addAll(grid.getCells());
      pFrame.getSld().getDataSet().addPopulation(pop);
      pFrame.getSld().add(layer);

    }

    public OlssonMapLegibilityAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute the grid method for map legibility from Olsson et al 2011");
      this.putValue(Action.NAME, "Compute Olsson Grid Map Legibility");
    }
  }

  class TopferAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      double initialScale = 15000.0;
      double finalScale = Legend.getSYMBOLISATI0N_SCALE();

      JFileChooser fc = new JFileChooser();
      int returnVal = fc.showSaveDialog(null);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      // path est le chemin jusqu'au fichier
      File path = fc.getSelectedFile();
      try {
        WritableWorkbook workbook = Workbook.createWorkbook(path);
        WritableSheet sheet = workbook
            .createSheet("radical_law_" + String.valueOf(finalScale), 1);
        // write the column headers
        sheet.addCell(new Label(0, 0, "Layer"));
        sheet.addCell(new Label(1, 0, "number"));
        sheet.addCell(new Label(2, 0, "total_length"));
        sheet.addCell(new Label(3, 0, "total_area"));
        sheet.addCell(new Label(4, 0, "radical_law_number"));
        sheet.addCell(new Label(5, 0, "radical_law_size"));

        StyledLayerDescriptor sld = pFrame.getSld();
        int i = 1;
        for (Layer layer : sld.getLayers()) {
          sheet.addCell(new Label(0, i, layer.getName()));
          Collection<IGeneObj> objects = SelectionUtil
              .getWindowObjects(application, layer.getName());
          sheet.addCell(new jxl.write.Number(1, i, objects.size()));
          double total = 0.0;
          boolean linear = true;
          boolean first = true;
          for (IGeneObj obj : objects) {
            if (first) {
              if (obj.getGeom() instanceof IPolygon)
                linear = false;
            }
            if (linear)
              total += obj.getGeom().length();
            else
              total += obj.getGeom().area();
            first = false;
          }
          if (linear)
            sheet.addCell(new jxl.write.Number(2, i, total));
          else
            sheet.addCell(new jxl.write.Number(3, i, total));
          double radicalNumber = objects.size()
              * Math.sqrt(initialScale / finalScale);
          double radicalSize = total * Math.sqrt(initialScale / finalScale);
          sheet.addCell(new jxl.write.Number(4, i, radicalNumber));
          sheet.addCell(new jxl.write.Number(5, i, radicalSize));
          i++;
        }

        workbook.write();
        workbook.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      } catch (WriteException e1) {
        e1.printStackTrace();
      }
    }

    public TopferAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute Topfer radical law on ungeneralised data for current scale and put values in an Excel sheet");
      this.putValue(Action.NAME,
          "Compute Topfer radical law on ungeneralised data");
    }
  }

  class ComputeMonitorsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();

      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      fc.setCurrentDirectory(new File("src/main/resources"));
      int returnVal = fc.showDialog(null, "Open a schema annotation file");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File pathAnnotation = fc.getSelectedFile();
      returnVal = fc.showDialog(null, "Open a constraint instanciation file");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File pathInst = fc.getSelectedFile();
      OWLOntology onto = null;
      try {
        onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
      } catch (OWLOntologyCreationException e1) {
        e1.printStackTrace();
      }
      Set<GeneralisationConcept> concepts = GeneralisationConcept
          .ontologyToGeneralisationConcepts(onto);

      // compute the constraint monitors
      try {
        SchemaAnnotation annotation = new SchemaAnnotation(pathAnnotation,
            concepts);
        ConstraintsInstanciation instanciation = new ConstraintsInstanciation(
            pathInst, concepts);
        ComputeMonitorsFrame frame = new ComputeMonitorsFrame(application,
            instanciation, annotation);
        frame.setVisible(true);
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (DOMException e1) {
        e1.printStackTrace();
      } catch (OWLOntologyCreationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public ComputeMonitorsAction() {
      super();
      this.putValue(Action.NAME, "Compute constraints monitors in the map");
    }
  }

  class ExportMonitorsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {

      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      Layer monitorLayer = application.getMainFrame().getSelectedProjectFrame()
          .getLayer("constraintMonitors");
      if (monitorLayer != null) {
        Map<SpecificationMonitor, ConstraintSatisfaction> distribution = new HashMap<>();
        for (IFeature feat : monitorLayer.getFeatureCollection()) {
          ConstraintMonitor monitor = (ConstraintMonitor) feat;
          distribution.put(monitor, monitor.getSatisfaction());
        }
        ConstraintSatisfactionDistribution distrib = new ConstraintSatisfactionDistribution(
            CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
                .getName(),
            distribution);
        distrib.print();
        try {
          distrib.exportToXml();
        } catch (TransformerException | IOException e1) {
          e1.printStackTrace();
        }
      }

    }

    public ExportMonitorsAction() {
      super();
      this.putValue(Action.NAME,
          "Export the constraints monitors of the map in XML");
    }
  }

  class UpdateMonitorsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {

      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      Layer monitorLayer = application.getMainFrame().getSelectedProjectFrame()
          .getLayer("constraintMonitors");
      if (monitorLayer != null) {
        for (IFeature feat : monitorLayer.getFeatureCollection()) {
          SpecificationMonitor monitor = (SpecificationMonitor) feat;
          monitor.computeSatisfaction();
        }
      }
    }

    public UpdateMonitorsAction() {
      super();
      this.putValue(Action.NAME,
          "Update the constraints monitors satisfaction");
    }
  }
}
