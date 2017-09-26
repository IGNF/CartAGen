package fr.ign.cogit.cartagen.appli.plugins.vgi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.DataSetZone;
import fr.ign.cogit.cartagen.core.dataset.DigitalCartographicModel;
import fr.ign.cogit.cartagen.core.dataset.GeographicClass;
import fr.ign.cogit.cartagen.core.dataset.SourceDLM;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostgisDB;
import fr.ign.cogit.cartagen.osm.lodharmonisation.gui.HarmonisationFrame;
import fr.ign.cogit.cartagen.osm.schema.OSMLoader;
import fr.ign.cogit.cartagen.osm.schema.OSMLoader.OSMLoaderType;
import fr.ign.cogit.cartagen.osm.schema.OSMLoader.OsmLoadingTask;
import fr.ign.cogit.cartagen.osm.schema.OpenStreetMapDb;
import fr.ign.cogit.cartagen.osm.schema.OsmDataset;
import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.panel.OsmFileFilter;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenProjectPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.osm.util.I18N;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class OSMCartAGenPlugin extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private GeOxygeneApplication application;
  private Runnable fillLayersTask;
  private List<OSMFile> recentFiles;
  private OSMLoader loader;
  private JDialog dialog;
  private JTextArea taskOutput;
  private JProgressBar progressBar;
  private JLabel taskLabel;
  private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;
  private OpenStreetMapDb database;

  public OSMCartAGenPlugin(String title) {
    super(title);
    application = CartAGenPlugin.getInstance().getApplication();
    loadRecentFiles();
    this.add(new JMenuItem(new ImportOSMFileAction()));
    JMenu menuRecent = new JMenu("Import recent file");
    for (OSMFile recentDoc : recentFiles) {
      menuRecent.add(new JMenuItem(new ImportOSMNamedFileAction(recentDoc)));
    }
    this.add(menuRecent);
    this.addSeparator();

    JMenu harmoniseMenu = new JMenu("LoD Harmonisation");
    harmoniseMenu.add(new JMenuItem(new HarmonisationFrameAction()));
    this.add(harmoniseMenu);
  }

  /**
   * Launch the frame that triggers LoD harmonisation processes.
   * 
   * @author GTouya
   * 
   */
  class HarmonisationFrameAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      StyledLayerDescriptor sld = CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getSld();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(sld);
      CartAGenDoc.getInstance().getCurrentDataset().setSld(sld);
      HarmonisationFrame frame = new HarmonisationFrame(SelectionUtil
          .getAllWindowObjects(CartAGenPlugin.getInstance().getApplication()),
          pool);
      frame.setVisible(true);
    }

    public HarmonisationFrameAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Launch the harmonisation frame");
      this.putValue(Action.NAME, "Launch harmonisation");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class ImportOSMNamedFileAction extends AbstractAction
      implements PropertyChangeListener {
    private OSMFile file;
    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDoc doc = CartAGenDoc.getInstance();
      String name = null;
      if (doc.getName() == null) {
        name = file.getFile().getName().substring(0,
            file.getFile().getName().length() - 4);
        doc.setName(name);
        if (file.isCreateDb()) {
          try {
            doc.setPostGisDb(PostgisDB.get(name, true));
          } catch (Exception e) {
            // do nothing
          }
        }
      }
      try {
        if (!recentFiles.contains(file))
          recentFiles.add(0, file);
        else {
          recentFiles.remove(file);
          recentFiles.add(0, file);
        }
        saveRecentFiles();
      } catch (TransformerException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      // build database & dataset
      CartAGenDoc.getInstance().setZone(new DataSetZone(name, null));

      // create the new CartAGen dataset
      database = new OpenStreetMapDb(name);
      database.setSourceDLM(SourceDLM.OpenStreetMap);
      database.setSymboScale(25000);
      database.setDocument(CartAGenDoc.getInstance());
      OsmDataset dataset = new OsmDataset();
      CartAGenDoc.getInstance().addDatabase(name, database);
      CartAGenDoc.getInstance().setCurrentDataset(dataset);
      database.setDataSet(dataset);
      database.setType(new DigitalCartographicModel());

      fillLayersTask = new Runnable() {
        @Override
        public void run() {
          try {
            addOsmDatabaseToFrame();
          } catch (JAXBException e) {
            e.printStackTrace();
          }
          application.getMainFrame().getGui().setCursor(null);
        }
      };

      loader = new OSMLoader(file.getFile(), dataset, fillLayersTask,
          file.getEpsg(), file.getTagFilter(), OSMLoaderType.XML);
      createProgressDialog();
      loader.setDialog(dialog);
      dialog.setVisible(true);
      application.getMainFrame().getGui()
          .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      loader.addPropertyChangeListener(this);
      loader.execute();
    }

    public ImportOSMNamedFileAction(OSMFile file) {
      this.putValue(Action.NAME, file.getFile().getPath());
      this.file = file;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if ("progress" == evt.getPropertyName()) {
        int progress = (Integer) evt.getNewValue();
        progressBar.setValue(progress);
        taskOutput.append(
            String.format("Completed %d%% of task.\n", loader.getProgress()));
        if (!currentTask.equals(loader.getCurrentTask())) {
          currentTask = loader.getCurrentTask();
          taskLabel.setText(currentTask.getLabel() + " loading...");
        }
      }
    }
  }

  /**
   * Relates a {@link OpenSteetMapDB} to a {@link ProjectFrame} of the
   * application. Fills the layers of the project frame with the database
   * objects.
   * 
   * @param db
   */
  public void addOsmDatabaseToFrame() throws JAXBException {
    // s'il y a une seule project frame et qu'elle est vide, on la supprime
    if (application.getMainFrame().getDesktopProjectFrames().length == 1) {
      ProjectFrame frameIni = application.getMainFrame()
          .getDesktopProjectFrames()[0];
      if (frameIni.getLayers().size() == 0) {
        application.getMainFrame().removeAllProjectFrames();
      }
    }
    ProjectFrame frame = application.getMainFrame().newProjectFrame();
    CartAGenPlugin.getInstance().getMapDbFrame().put(database.getName(), frame);
    frame.getSld().setDataSet(database.getDataSet());
    frame.getLayerViewPanel().getRenderingManager().setHandlingDeletion(true);
    StyledLayerDescriptor defaultSld = compileOsmSlds();
    database.getDataSet().setSld(defaultSld);
    StyledLayerDescriptor.unmarshall(OSMLoader.class.getClassLoader()
        .getResourceAsStream("sld/roads_sld.xml")); //$NON-NLS-1$
    float opacity = 0.8f;
    float strokeWidth = 1.0f;
    for (GeographicClass geoClass : database.getClasses()) {
      String populationName = database.getDataSet()
          .getPopNameFromFeatType(geoClass.getFeatureTypeName());
      if (frame.getSld().getLayer(populationName) == null) {
        Color fillColor = new Color((float) Math.random(),
            (float) Math.random(), (float) Math.random());
        Layer layer = new NamedLayer(frame.getSld(), populationName);
        if (layer.getFeatureCollection().size() == 0)
          continue;
        if (defaultSld.getLayer(populationName) != null) {
          layer.getStyles().clear();
          layer.getStyles()
              .addAll(defaultSld.getLayer(populationName).getStyles());
        } else {
          UserStyle style = new UserStyle();
          style.setName("Style créé pour le layer " + populationName);//$NON-NLS-1$
          FeatureTypeStyle fts = new FeatureTypeStyle();
          fts.getRules().add(LayerFactory.createRule(geoClass.getGeometryType(),
              fillColor.darker(), fillColor, opacity, opacity, strokeWidth));
          style.getFeatureTypeStyles().add(fts);
          layer.getStyles().add(style);
        }
        frame.getSld().add(layer);
      }
    }

    // initialise the frame with cartagen plugin
    CartAGenProjectPlugin.getInstance().initialize(frame);
  }

  private StyledLayerDescriptor compileOsmSlds() throws JAXBException {
    // load road sld
    StyledLayerDescriptor defaultSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/roads_sld.xml")); //$NON-NLS-1$
    // load buildings sld
    StyledLayerDescriptor buildingSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/buildings_sld.xml")); //$NON-NLS-1$
    for (Layer layer : buildingSld.getLayers())
      defaultSld.add(layer);
    // load waterway sld
    StyledLayerDescriptor waterSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/waterway_sld.xml")); //$NON-NLS-1$
    for (Layer layer : waterSld.getLayers())
      defaultSld.add(layer);
    // load landuse sld
    StyledLayerDescriptor landuseSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/landuse_sld.xml")); //$NON-NLS-1$
    for (Layer layer : landuseSld.getLayers())
      defaultSld.add(layer);
    // load point features sld
    StyledLayerDescriptor ptsSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/point_features_sld.xml")); //$NON-NLS-1$
    for (Layer layer : ptsSld.getLayers())
      defaultSld.add(layer);
    // load railways sld
    StyledLayerDescriptor railSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/railway_sld.xml")); //$NON-NLS-1$
    for (Layer layer : railSld.getLayers())
      defaultSld.add(layer);
    // load natural sld
    StyledLayerDescriptor naturalSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/natural_sld.xml")); //$NON-NLS-1$
    for (Layer layer : naturalSld.getLayers())
      defaultSld.add(layer);
    // load leisure sld
    StyledLayerDescriptor leisureSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/leisure_sld.xml")); //$NON-NLS-1$
    for (Layer layer : leisureSld.getLayers())
      defaultSld.add(layer);
    // load airport sld
    StyledLayerDescriptor airportSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/airport_sld.xml")); //$NON-NLS-1$
    for (Layer layer : airportSld.getLayers())
      defaultSld.add(layer);
    // load amenity sld
    StyledLayerDescriptor amenitySld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader()
            .getResourceAsStream("sld/amenity_sld.xml")); //$NON-NLS-1$
    for (Layer layer : amenitySld.getLayers())
      defaultSld.add(layer);
    // TODO fill with the other SLDs
    return defaultSld;
  }

  class OSMFile {
    private File file;
    private String epsg;
    private boolean createDb;
    private String tagFilter;

    public OSMFile(File file, String epsg, boolean createDb, String tagFilter) {
      super();
      this.file = file;
      this.epsg = epsg;
      this.createDb = createDb;
      this.tagFilter = tagFilter;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result;
      result = prime * result + ((file == null) ? 0 : file.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      OSMFile other = (OSMFile) obj;
      if (file == null) {
        if (other.file != null)
          return false;
      } else if (!file.equals(other.file))
        return false;
      return true;
    }

    public File getFile() {
      return file;
    }

    public void setFile(File file) {
      this.file = file;
    }

    public String getEpsg() {
      return epsg;
    }

    public void setEpsg(String epsg) {
      this.epsg = epsg;
    }

    public boolean isCreateDb() {
      return createDb;
    }

    public void setCreateDb(boolean createDb) {
      this.createDb = createDb;
    }

    public String getTagFilter() {
      return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
      this.tagFilter = tagFilter;
    }

  }

  private void createProgressDialog() {
    JPanel panel = new JPanel(new BorderLayout());

    taskLabel = new JLabel(loader.getCurrentTask().getLabel() + " loading...");
    progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);

    taskOutput = new JTextArea(5, 20);
    taskOutput.setMargin(new Insets(5, 5, 5, 5));
    taskOutput.setEditable(false);

    JPanel panel1 = new JPanel();
    panel1.add(taskLabel);
    panel1.add(progressBar);

    panel.add(panel1, BorderLayout.PAGE_START);
    panel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    dialog = new JDialog(application.getMainFrame().getGui());
    dialog.add(panel);
    dialog.pack();
  }

  private void loadRecentFiles() {
    this.recentFiles = new ArrayList<OSMFile>();
    LastSessionParameters params = LastSessionParameters.getInstance();
    if (params.hasParameter("Recent OSM file 1")) {
      String path = (String) params.getParameterValue("Recent OSM file 1");
      Map<String, String> attrs = params
          .getParameterAttributes("Recent OSM file 1");
      String epsg = attrs.get("epsg");
      boolean createDb = false;
      if (attrs.get("createDb").equals("true"))
        createDb = true;
      String tagFilter = attrs.get("tagFilter");
      this.recentFiles
          .add(new OSMFile(new File(path), epsg, createDb, tagFilter));
    }
    if (params.hasParameter("Recent OSM file 2")) {
      String path = (String) params.getParameterValue("Recent OSM file 2");
      Map<String, String> attrs = params
          .getParameterAttributes("Recent OSM file 2");
      String epsg = attrs.get("epsg");
      boolean createDb = false;
      if (attrs.get("createDb").equals("true"))
        createDb = true;
      String tagFilter = attrs.get("tagFilter");
      this.recentFiles
          .add(new OSMFile(new File(path), epsg, createDb, tagFilter));
    }
    if (params.hasParameter("Recent OSM file 3")) {
      String path = (String) params.getParameterValue("Recent OSM file 3");
      Map<String, String> attrs = params
          .getParameterAttributes("Recent OSM file 3");
      String epsg = attrs.get("epsg");
      boolean createDb = false;
      if (attrs.get("createDb").equals("true"))
        createDb = true;
      String tagFilter = attrs.get("tagFilter");
      this.recentFiles
          .add(new OSMFile(new File(path), epsg, createDb, tagFilter));
    }
    if (params.hasParameter("Recent OSM file 4")) {
      String path = (String) params.getParameterValue("Recent OSM file 4");
      Map<String, String> attrs = params
          .getParameterAttributes("Recent OSM file 4");
      String epsg = attrs.get("epsg");
      boolean createDb = false;
      if (attrs.get("createDb").equals("true"))
        createDb = true;
      String tagFilter = attrs.get("tagFilter");
      this.recentFiles
          .add(new OSMFile(new File(path), epsg, createDb, tagFilter));
    }
    if (params.hasParameter("Recent OSM file 5")) {
      String path = (String) params.getParameterValue("Recent OSM file 5");
      Map<String, String> attrs = params
          .getParameterAttributes("Recent OSM file 5");
      String epsg = attrs.get("epsg");
      boolean createDb = false;
      if (attrs.get("createDb").equals("true"))
        createDb = true;
      String tagFilter = attrs.get("tagFilter");
      this.recentFiles
          .add(new OSMFile(new File(path), epsg, createDb, tagFilter));
    }
  }

  private void saveRecentFiles() throws TransformerException, IOException {
    LastSessionParameters params = LastSessionParameters.getInstance();
    for (int i = 1; i <= Math.min(5, recentFiles.size()); i++) {
      Map<String, String> attributes = new HashMap<String, String>();
      attributes.put("epsg", recentFiles.get(i - 1).getEpsg());
      attributes.put("createDb",
          String.valueOf(recentFiles.get(i - 1).isCreateDb()));
      attributes.put("tagFilter", recentFiles.get(i - 1).getTagFilter());
      params.setParameter("Recent OSM file " + i,
          recentFiles.get(i - 1).getFile().getPath(), attributes);
    }
  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * 
   * @author GTouya
   * 
   */
  public class ImportOSMFileAction extends AbstractAction
      implements PropertyChangeListener {

    /****/
    private static final long serialVersionUID = 1L;

    public File osmFile;
    public String epsg;
    public String tagFilter;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ImportOsmFileFrame dialogImportOsmFrame = new ImportOsmFileFrame(this);
      if (dialogImportOsmFrame.getAction().equals("OK")) {
        //
        CartAGenDoc doc = CartAGenDoc.getInstance();
        String name = null;
        if (doc.getName() == null) {
          name = osmFile.getName().substring(0, osmFile.getName().length() - 4);
          doc.setName(name);
          if (dialogImportOsmFrame.createPostGis()) {
            try {
              doc.setPostGisDb(PostgisDB.get(name, true));
            } catch (Exception e) {
              // do nothing
            }
          }
        }
        try {
          OSMFile file = new OSMFile(osmFile, epsg,
              dialogImportOsmFrame.createPostGis(), tagFilter);
          if (!recentFiles.contains(file))
            recentFiles.add(0, file);
          else {
            recentFiles.remove(file);
            recentFiles.add(0, file);
          }
          saveRecentFiles();
        } catch (TransformerException e1) {
          e1.printStackTrace();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        fillLayersTask = new Runnable() {
          @Override
          public void run() {
            application.getMainFrame().getGui().setCursor(null);
          }
        };

        // build database & dataset
        CartAGenDoc.getInstance().setZone(new DataSetZone(name, null));

        // create the new CartAGen dataset
        database = new OpenStreetMapDb(name);
        database.setSourceDLM(SourceDLM.OpenStreetMap);
        database.setSymboScale(25000);
        database.setDocument(CartAGenDoc.getInstance());
        OsmDataset dataset = new OsmDataset();
        CartAGenDoc.getInstance().addDatabase(name, database);
        CartAGenDoc.getInstance().setCurrentDataset(dataset);
        database.setDataSet(dataset);
        database.setType(new DigitalCartographicModel());

        loader = new OSMLoader(osmFile, dataset, fillLayersTask, epsg,
            tagFilter, OSMLoaderType.XML);
        createProgressDialog();
        loader.setDialog(dialog);
        dialog.setVisible(true);
        application.getMainFrame().getGui()
            .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loader.addPropertyChangeListener(this);
        loader.execute();

      }
    }

    public ImportOSMFileAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Import OSM data from file in a new Dataset");
      this.putValue(Action.NAME, "Import OSM File");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if ("progress" == evt.getPropertyName()) {
        int progress = (Integer) evt.getNewValue();
        progressBar.setValue(progress);
        taskOutput.append(
            String.format("Completed %d%% of task.\n", loader.getProgress()));
        if (!currentTask.equals(loader.getCurrentTask())) {
          currentTask = loader.getCurrentTask();
          taskLabel.setText(currentTask.getLabel() + " loading...");
        }
      }
    }

    class ImportOsmFileFrame extends JDialog implements ActionListener {

      /** Serial version UID. */
      private static final long serialVersionUID = 1L;

      ImportOSMFileAction importPlugin;

      private String action;

      /** Fields. */
      private JTextField txtPath, txtEpsg;
      private Map<String, String> currentProjections = new HashMap<String, String>();
      private JButton browseBtn, usedBtn, cancelBtn, okBtn;
      private JComboBox<String> usedCombo;
      private JCheckBox postGisCheck;
      private JTextField txtTagFilter;
      private JCheckBox chkTagFilter;

      /**
       * Constructor, build the frame.
       * 
       * @param importPlugin
       */
      public ImportOsmFileFrame(ImportOSMFileAction importPlugin) {
        this.importPlugin = importPlugin;

        setModal(true);
        setTitle("Import OSM File");
        setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/icons/map_add.png"))
                .getImage());

        initPanel();

        pack();
        setLocation(300, 300);
        setSize(600, 200);
        setVisible(true);
      }

      @Override
      public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == cancelBtn) {
          action = "cancel";
          dispose();
        } else if (source == usedBtn) {
          txtEpsg.setText(currentProjections.get(usedCombo.getSelectedItem()));
        } else if (source == browseBtn) {
          JFileChooser fc = new JFileChooser();
          fc.setFileFilter(new OsmFileFilter());
          // fc.setCurrentDirectory(new
          // File("src/main/resources/xml/"));
          fc.setCurrentDirectory(
              new File(application.getProperties().getLastOpenedFile()));
          int returnVal = fc
              .showSaveDialog(application.getMainFrame().getGui());
          if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
          }
          File file = fc.getSelectedFile();
          this.txtPath.setText(file.getAbsolutePath());
        } else if (source == okBtn) {
          action = "OK";
          importPlugin.epsg = txtEpsg.getText();
          importPlugin.osmFile = new File(txtPath.getText());
          if (this.chkTagFilter.isSelected()
              && !this.txtTagFilter.getText().equals(""))
            importPlugin.tagFilter = this.txtTagFilter.getText();
          dispose();
        }
      }

      /**
       * Initialize and display fields.
       */
      private void initPanel() {

        FormLayout layout = new FormLayout(
            "20dlu, pref, pref, 10dlu, pref, pref, pref, pref, pref, pref, 20dlu", // Colonnes
            "10dlu, pref, pref, 20dlu, pref, pref, 20dlu"); // Lignes
        setLayout(layout);

        CellConstraints cc = new CellConstraints();

        currentProjections.put("Lambert93", "2154");
        currentProjections.put("Lambert Belge 2008", "3812");
        currentProjections.put("UTM 18N (New York)", "32618");
        currentProjections.put("UTM 51N (Philippines)", "32651");
        currentProjections.put("UTM 16N (Chicago)", "32616");
        currentProjections.put("UTM 28N (Dakar)", "32628");
        currentProjections.put("UTM 32N (Italie)", "32632");
        currentProjections.put("UTM 33N (Vienna)", "32633");
        currentProjections.put("UTM 35N (Ukraine)", "32635");
        currentProjections.put("UTM 23S (Rio)", "32723");
        currentProjections.put("UTM 55S (Melbourne)", "32755");
        currentProjections.put("Gauss-Krueger zone 4 (East Germany)", "31468");
        currentProjections.put("Gauss-Krueger zone 3 (Center Germany)",
            "31467");
        this.setPreferredSize(new Dimension(350, 150));

        // JPanel filePanel = new JPanel();
        add(new JLabel("File : "), cc.xy(2, 2));
        txtPath = new JTextField(40);
        add(txtPath, cc.xyw(3, 2, 6));
        browseBtn = new JButton(new ImageIcon(
            this.getClass().getResource("/images/icons/magnifier.png")));
        browseBtn.addActionListener(this);
        browseBtn.setActionCommand("browse");
        add(browseBtn, cc.xy(9, 2));
        add(new JLabel(" (*.osm) "), cc.xy(10, 2));

        add(new JLabel("EPSG of projection: "), cc.xy(2, 3));
        txtEpsg = new JTextField();
        txtEpsg.setPreferredSize(new Dimension(60, 20));
        txtEpsg.setMaximumSize(new Dimension(60, 20));
        txtEpsg.setMinimumSize(new Dimension(60, 20));
        txtEpsg.setText("2154");
        add(txtEpsg, cc.xy(3, 3));

        add(new JLabel("( "), cc.xy(5, 3));
        usedBtn = new JButton("<<");
        usedBtn.addActionListener(this);
        usedBtn.setActionCommand("used");
        add(usedBtn, cc.xy(6, 3));
        usedCombo = new JComboBox<String>(
            (String[]) currentProjections.keySet().toArray(new String[0]));
        usedCombo.setPreferredSize(new Dimension(130, 20));
        usedCombo.setMaximumSize(new Dimension(130, 20));
        usedCombo.setMinimumSize(new Dimension(130, 20));
        add(usedCombo, cc.xy(7, 3));
        add(new JLabel(" )  "), cc.xy(8, 3));

        chkTagFilter = new JCheckBox("Filter tags");
        chkTagFilter.setSelected(false);
        add(chkTagFilter, cc.xy(2, 4));
        txtTagFilter = new JTextField();
        txtTagFilter.setPreferredSize(new Dimension(60, 20));
        txtTagFilter.setMaximumSize(new Dimension(60, 20));
        txtTagFilter.setMinimumSize(new Dimension(60, 20));
        txtTagFilter.setText("");
        add(txtTagFilter, cc.xy(3, 4));

        postGisCheck = new JCheckBox("Create PostGis Db");
        postGisCheck.setSelected(false);
        add(postGisCheck, cc.xy(3, 5));

        // Define a panel with the OK and Cancel buttons
        JPanel btnPanel = new JPanel();
        okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        okBtn.setActionCommand("ok");
        cancelBtn = new JButton(I18N.getString("MainLabels.lblCancel"));
        cancelBtn.addActionListener(this);
        cancelBtn.setActionCommand("cancel");
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        add(btnPanel, cc.xy(3, 6));

      }

      /**
       * @return name of action (ok, cancel).
       */
      public String getAction() {
        return action;
      }

      public boolean createPostGis() {
        return postGisCheck.isSelected();
      }
    }
  }
}
