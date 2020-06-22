package fr.ign.cogit.cartagen.appli.plugins.process.leastsquares;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.appli.utilities.GeneObjClassTree;
import fr.ign.cogit.cartagen.appli.utilities.JColorSelectionButton;
import fr.ign.cogit.cartagen.appli.utilities.renderer.ClassSimpleNameListRenderer;
import fr.ign.cogit.cartagen.appli.utilities.renderer.ClassSimpleNameTableRenderer;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSExternalConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.EndVertexStrategy;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class LeastSquaresFrame extends JFrame
        implements ActionListener, ListSelectionListener, ChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String filePath = "src/main/resources/xml/";

    public String listeCourante;

    public JList<Class<?>> listeObjFixes;
    public JList<Class<?>> listeObjRigides;
    public JList<Class<?>> listeObjMalleables;
    JCheckBox chkDiffusion, chkCommit, chkDisplay;
    JList<String> listeContraintes;
    JSlider curseurPoids;
    private JColorSelectionButton colorButton;
    private JSlider widthSlider;

    public JTable tableContrRel;

    public JRadioButton fenetre;
    public JRadioButton selectionObjs;
    public JRadioButton selectionSurfs;
    public JTextField txtClasseSurf, txtEchelle;

    public Set<String> contraintesActivees = new HashSet<String>();
    public Set<String> contraintesFixes = new HashSet<String>();
    public Set<String> contraintesRigides = new HashSet<String>();
    public Set<String> contraintesMalleables = new HashSet<String>();
    private Set<Class<?>> externalConstraints = new HashSet<Class<?>>();
    public Set<Class<?>> classesFixes = new HashSet<Class<?>>();
    public Set<Class<?>> classesRigides = new HashSet<Class<?>>();
    public Set<Class<?>> classesMalleables = new HashSet<Class<?>>();

    public Map<String, Double> poidsContraintes = new HashMap<String, Double>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("OK")) {
            this.lancerMoindresCarres();
            this.setVisible(false);
        } else if (e.getActionCommand().equals("annuler")) {
            this.setVisible(false);
        } else if (e.getActionCommand().equals("ajouterFix")) {
            this.listeCourante = "fixe";
            @SuppressWarnings("unused")
            BrowserClassesFrame frame = new BrowserClassesFrame();
        } else if (e.getActionCommand().equals("enleverFix")) {
            this.listeCourante = "fixe";
            this.enleverClasseListe();
            if (this.listeObjFixes.getModel().getSize() == 0) {
                this.contraintesActivees.removeAll(this.contraintesFixes);
                this.removeAllMap(this.poidsContraintes, this.contraintesFixes);
            }
            this.miseAjourFrame();
        } else if (e.getActionCommand().equals("ajouterRig")) {
            this.listeCourante = "rigide";
            @SuppressWarnings("unused")
            BrowserClassesFrame frame = new BrowserClassesFrame();
        } else if (e.getActionCommand().equals("enleverRig")) {
            this.listeCourante = "rigide";
            this.enleverClasseListe();
            if (this.listeObjRigides.getModel().getSize() == 0) {
                this.contraintesActivees.removeAll(this.contraintesRigides);
                this.removeAllMap(this.poidsContraintes,
                        this.contraintesRigides);
            }
            this.miseAjourFrame();
        } else if (e.getActionCommand().equals("ajouterMal")) {
            this.listeCourante = "malleable";
            @SuppressWarnings("unused")
            BrowserClassesFrame frame = new BrowserClassesFrame();
        } else if (e.getActionCommand().equals("enleverMal")) {
            this.listeCourante = "malleable";
            this.enleverClasseListe();
            if (this.listeObjMalleables.getModel().getSize() == 0) {
                this.contraintesActivees.removeAll(this.contraintesMalleables);
                this.removeAllMap(this.poidsContraintes,
                        this.contraintesMalleables);
            }
            this.miseAjourFrame();
        } else if (e.getActionCommand().equals("ajouterExt")) {
            AddExternalConstraintFrame frame = new AddExternalConstraintFrame(
                    this);
            frame.setVisible(true);
        } else if (e.getActionCommand().equals("ajouterSurf")) {
            this.listeCourante = "surf";
            @SuppressWarnings("unused")
            BrowserClassesFrame frame = new BrowserClassesFrame();
        }
    }

    @SuppressWarnings("serial")
    public LeastSquaresFrame() {
        super("Généraliser par moindres carrés");
        this.setSize(500, 500);
        // on remplit les contraintes par d�faut
        this.contraintesFixes.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSMovementConstraint");
        this.contraintesRigides.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSMovementConstraint");
        this.contraintesRigides.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSStiffnessConstraint");
        this.contraintesRigides.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSSideOrientConstraint");
        this.contraintesMalleables.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSMovementConstraint");
        this.contraintesMalleables.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSCurvatureConstraint");
        this.contraintesMalleables.add(
                "fr.ign.cogit.cartagen.leastsquares.core.LSMovementDirConstraint");
        this.findExternalConstraints();
        // contraintesMalleables.add("CIMC_Croisement");

        // *********************************
        // BOUTONS OK / ANNULER
        // *********************************
        JPanel panelOK = new JPanel();
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(this);
        btnOK.setActionCommand("OK");
        btnOK.setPreferredSize(new Dimension(100, 50));
        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(this);
        btnAnnuler.setActionCommand("annuler");
        btnAnnuler.setPreferredSize(new Dimension(100, 50));
        panelOK.add(btnOK);
        panelOK.add(btnAnnuler);
        panelOK.setLayout(new BoxLayout(panelOK, BoxLayout.X_AXIS));

        // *********************************
        // ONGLET Mapspecs
        // *********************************
        JPanel panelMapspecs = new JPanel();
        // Panneau types d'objets
        // ce panneau est composé de trois panneaux verticaux comprenant
        // une JList et un bouton permettant d'ajouter des classes dans la liste
        JPanel panelTypeObjs = new JPanel();
        // ----------------------------
        // Panneau objets fixes
        JPanel panelObjsFixes = new JPanel();
        this.listeObjFixes = new JList<>();
        this.listeObjFixes.setPreferredSize(new Dimension(50, 50));
        this.listeObjFixes.setCellRenderer(new ClassSimpleNameListRenderer());
        JLabel lblFixe = new JLabel("Classes Fixes");
        // on fait un panneau horizontal pour mettre les boutons + et -
        JPanel boutonsFixes = new JPanel();
        JButton btnAjouterFix = new JButton("+");
        btnAjouterFix.addActionListener(this);
        btnAjouterFix.setActionCommand("ajouterFix");
        JButton btnEnleverFix = new JButton("-");
        btnEnleverFix.addActionListener(this);
        btnEnleverFix.setActionCommand("enleverFix");
        boutonsFixes.setLayout(new BoxLayout(boutonsFixes, BoxLayout.X_AXIS));
        boutonsFixes.add(btnAjouterFix);
        boutonsFixes.add(btnEnleverFix);
        panelObjsFixes.add(lblFixe);
        panelObjsFixes.add(new JScrollPane(this.listeObjFixes));
        panelObjsFixes.add(boutonsFixes);
        // layout de panelObjsFixes
        panelObjsFixes.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelObjsFixes
                .setLayout(new BoxLayout(panelObjsFixes, BoxLayout.Y_AXIS));

        // ----------------------------
        // Panneau objets rigides
        JPanel panelObjsRigides = new JPanel();
        this.listeObjRigides = new JList<>();
        this.listeObjRigides.setPreferredSize(new Dimension(50, 50));
        this.listeObjRigides.setCellRenderer(new ClassSimpleNameListRenderer());
        JLabel lblRigide = new JLabel("Classes Rigides");
        // on fait un panneau horizontal pour mettre les boutons + et -
        JPanel boutonsRigides = new JPanel();
        JButton btnAjouterRigides = new JButton("+");
        btnAjouterRigides.addActionListener(this);
        btnAjouterRigides.setActionCommand("ajouterRig");
        JButton btnEnleverRigides = new JButton("-");
        btnEnleverRigides.addActionListener(this);
        btnEnleverRigides.setActionCommand("enleverRig");
        boutonsRigides
                .setLayout(new BoxLayout(boutonsRigides, BoxLayout.X_AXIS));
        boutonsRigides.add(btnAjouterRigides);
        boutonsRigides.add(btnEnleverRigides);
        panelObjsRigides.add(lblRigide);
        panelObjsRigides.add(new JScrollPane(this.listeObjRigides));
        panelObjsRigides.add(boutonsRigides);
        // layout de panelObjsFixes
        panelObjsRigides.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelObjsRigides
                .setLayout(new BoxLayout(panelObjsRigides, BoxLayout.Y_AXIS));

        // ----------------------------
        // Panneau objets rigides
        JPanel panelObjsMalleables = new JPanel();
        this.listeObjMalleables = new JList<>();
        this.listeObjMalleables.setPreferredSize(new Dimension(50, 50));
        this.listeObjMalleables
                .setCellRenderer(new ClassSimpleNameListRenderer());
        JLabel lblMall = new JLabel("Classes Malléables");
        // on fait un panneau horizontal pour mettre les boutons + et -
        JPanel boutonsMalleables = new JPanel();
        JButton btnAjouterMalleables = new JButton("+");
        btnAjouterMalleables.addActionListener(this);
        btnAjouterMalleables.setActionCommand("ajouterMal");
        JButton btnEnleverMalleables = new JButton("-");
        btnEnleverMalleables.addActionListener(this);
        btnEnleverMalleables.setActionCommand("enleverMal");
        boutonsMalleables
                .setLayout(new BoxLayout(boutonsMalleables, BoxLayout.X_AXIS));
        boutonsMalleables.add(btnAjouterMalleables);
        boutonsMalleables.add(btnEnleverMalleables);
        panelObjsMalleables.add(lblMall);
        panelObjsMalleables.add(new JScrollPane(this.listeObjMalleables));
        panelObjsMalleables.add(boutonsMalleables);
        // layout de panelObjsFixes
        panelObjsMalleables
                .setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelObjsMalleables.setLayout(
                new BoxLayout(panelObjsMalleables, BoxLayout.Y_AXIS));

        // layout de panelTypeObjs
        panelTypeObjs.add(panelObjsFixes);
        panelTypeObjs.add(panelObjsRigides);
        panelTypeObjs.add(panelObjsMalleables);
        panelTypeObjs.setLayout(new BoxLayout(panelTypeObjs, BoxLayout.X_AXIS));

        // ------------------------------------
        // Panneau Contraintes Externes
        JPanel panelContrExternes = new JPanel();
        // on crée d'abord une table à 3 colonnes contenant les contraintes
        // externes utilisée et les classes pour lesquelles la contrainte
        // est définie. Les sous-classes de classes du tableau héritent de la
        // contrainte
        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Contrainte", "Classe 1", "Classe 2", "Seuil" },
                0);
        if (this.tableContrRel != null) {
            for (int i = 0; i < this.tableContrRel.getModel()
                    .getRowCount(); i++) {
                model.addRow(new String[] {
                        (String) this.tableContrRel.getModel().getValueAt(i, 0),
                        (String) this.tableContrRel.getModel().getValueAt(i, 1),
                        (String) this.tableContrRel.getModel().getValueAt(i, 2),
                        (String) this.tableContrRel.getModel().getValueAt(i,
                                3) });
            }
        }
        this.tableContrRel = new JTable(model);
        this.tableContrRel.setEnabled(false);
        this.tableContrRel.getColumnModel().getColumn(0)
                .setCellRenderer(new ClassSimpleNameTableRenderer());
        this.tableContrRel.getColumnModel().getColumn(1)
                .setCellRenderer(new ClassSimpleNameTableRenderer());
        this.tableContrRel.getColumnModel().getColumn(2)
                .setCellRenderer(new ClassSimpleNameTableRenderer());
        // on ajoute un bouton pour ajouter une contrainte externe à la table
        JButton btnAjouterContr = new JButton("Ajouter une contrainte externe");
        btnAjouterContr.addActionListener(this);
        btnAjouterContr.setActionCommand("ajouterExt");
        panelContrExternes.add(btnAjouterContr);
        panelContrExternes.add(new JScrollPane(this.tableContrRel));
        panelContrExternes
                .setLayout(new BoxLayout(panelContrExternes, BoxLayout.Y_AXIS));

        // panneau global de l'onglet
        panelMapspecs.add(panelTypeObjs);
        panelMapspecs.add(panelContrExternes);
        panelMapspecs.setLayout(new BoxLayout(panelMapspecs, BoxLayout.Y_AXIS));

        // *********************************
        // ONGLET Pond�ration des contraintes
        // *********************************
        JPanel panelPonderation = new JPanel();
        // panneau contenant une JListe des contraintes
        String[] contraintes = new String[this.contraintesActivees.size()];
        Iterator<String> iter = this.contraintesActivees.iterator();
        int i = 0;
        while (iter.hasNext()) {
            contraintes[i] = iter.next();
            i += 1;
        } // boucle sur contraintesActivees
        this.listeContraintes = new JList<>(contraintes);
        // on ajoute un �couteur de la liste
        this.listeContraintes.addListSelectionListener(this);
        this.listeContraintes
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listeContraintes.setPreferredSize(new Dimension(50, 60));
        JScrollPane scrollPanePds = new JScrollPane(this.listeContraintes);
        // panneau contenant le curseur
        JPanel panelCurseur = new JPanel();
        JLabel lblCurseur = new JLabel("poids de la contrainte : ");
        this.curseurPoids = new JSlider(SwingConstants.HORIZONTAL, 0, 20, 1);
        this.curseurPoids.setMajorTickSpacing(1);
        this.curseurPoids.setMinorTickSpacing(5);
        this.curseurPoids.setPaintTicks(true);
        this.curseurPoids.setPaintLabels(true);
        this.curseurPoids.setPreferredSize(new Dimension(300, 50));
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(0), new JLabel("Min"));
        labelTable.put(new Integer(10), new JLabel("Moyen"));
        labelTable.put(new Integer(20), new JLabel("Max"));
        this.curseurPoids.setLabelTable(labelTable);
        // on ajoute un écouteur au curseur
        this.curseurPoids.addChangeListener(this);
        panelCurseur.add(lblCurseur);
        panelCurseur.add(Box.createHorizontalGlue());
        panelCurseur.add(this.curseurPoids);
        panelCurseur.setLayout(new BoxLayout(panelCurseur, BoxLayout.X_AXIS));
        panelCurseur.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // mise en place du panneau pond�ration
        panelPonderation.add(scrollPanePds);
        panelPonderation.add(panelCurseur);
        panelPonderation
                .setLayout(new BoxLayout(panelPonderation, BoxLayout.Y_AXIS));

        // *********************************
        // ONGLET Zone d'application
        // *********************************
        JPanel panelZoneAppl = new JPanel();
        JPanel panelFenetre = new JPanel();
        this.fenetre = new JRadioButton("Objets de la fenêtre");
        panelFenetre.add(this.fenetre);
        panelFenetre.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel panelSelObj = new JPanel();
        this.selectionObjs = new JRadioButton("Les objets sélectionnés");
        this.selectionObjs.setSelected(true);
        panelSelObj.add(this.selectionObjs);
        panelSelObj.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel panelSelSurf = new JPanel();
        this.selectionSurfs = new JRadioButton("Les surfaces sélectionnées");
        this.txtClasseSurf = new JTextField();
        this.txtClasseSurf.setPreferredSize(new Dimension(100, 20));
        // TODO add a autocomplete decorator
        JButton btnAjouterClasseSurf = new JButton("+");
        btnAjouterClasseSurf.addActionListener(this);
        btnAjouterClasseSurf.setActionCommand("ajouterSurf");
        panelSelSurf.add(this.selectionSurfs);
        panelSelSurf.add(this.txtClasseSurf);
        panelSelSurf.add(btnAjouterClasseSurf);
        panelSelSurf.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelSelSurf.setLayout(new BoxLayout(panelSelSurf, BoxLayout.X_AXIS));
        ButtonGroup bg = new ButtonGroup();
        bg.add(this.fenetre);
        bg.add(this.selectionObjs);
        bg.add(this.selectionSurfs);
        // panneau �chelle de sortie
        JPanel panelEchelle = new JPanel();
        JLabel lblEchelle = new JLabel("échelle de la carte généralisée : ");
        this.txtEchelle = new JTextField();
        this.txtEchelle.setPreferredSize(new Dimension(100, 20));
        this.txtEchelle.setMaximumSize(new Dimension(100, 20));
        this.txtEchelle.setMinimumSize(new Dimension(100, 20));
        // que des chiffres dans cette zone de texte
        this.txtEchelle.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                for (int i = 0; i < str.length(); i++) {
                    if (!Character.isDigit(str.charAt(i))) {
                        return;
                    }
                }
                super.insertString(offs, str, a);
            }
        });
        this.txtEchelle.setText(String.valueOf(
                new Double(Legend.getSYMBOLISATI0N_SCALE()).intValue()));
        panelEchelle.add(lblEchelle);
        panelEchelle.add(this.txtEchelle);
        panelEchelle.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelEchelle.setLayout(new BoxLayout(panelEchelle, BoxLayout.X_AXIS));
        // mise en page de l'onglet
        panelZoneAppl.add(panelFenetre);
        panelZoneAppl.add(Box.createVerticalGlue());
        panelZoneAppl.add(panelSelObj);
        panelZoneAppl.add(Box.createVerticalGlue());
        panelZoneAppl.add(panelSelSurf);
        panelZoneAppl.add(Box.createVerticalGlue());
        panelZoneAppl.add(panelEchelle);
        panelZoneAppl.add(Box.createVerticalGlue());
        panelZoneAppl.setLayout(new BoxLayout(panelZoneAppl, BoxLayout.Y_AXIS));

        // *********************************
        // DEFINITION DES ONGLETS
        // *********************************
        JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
        onglets.addTab("Mapspecs", panelMapspecs);
        onglets.addTab("Pondération", panelPonderation);
        onglets.addTab("Zone d'application", panelZoneAppl);
        onglets.setSelectedIndex(0);

        // *********************************
        // Panneau Param�tres
        // *********************************
        JPanel pParams = new JPanel();
        this.chkDiffusion = new JCheckBox("Permettre la diffusion");
        String commentaire = "Cliquez pour permettre une diffusion des éléments de réseau"
                + " au bord de la zone généralisée. Les bords sont fixés sinon.";
        this.chkDiffusion.setToolTipText(commentaire);
        this.chkCommit = new JCheckBox("Appliquer les modifications");
        JPanel pDisplay = new JPanel();
        this.chkDisplay = new JCheckBox(
                "Afficher la géométrie finale (ou initiale");
        this.colorButton = new JColorSelectionButton(Color.RED);
        this.widthSlider = new JSlider(1, 10, 5);
        this.widthSlider.setMajorTickSpacing(1);
        this.widthSlider.setMaximumSize(new Dimension(150, 20));
        this.widthSlider.setMinimumSize(new Dimension(150, 20));
        this.widthSlider.setPreferredSize(new Dimension(150, 20));
        this.widthSlider.setPaintTicks(true);
        this.widthSlider.setToolTipText("largeur des lignes dessinées");
        pDisplay.add(this.chkDisplay);
        pDisplay.add(Box.createHorizontalStrut(5));
        pDisplay.add(this.colorButton);
        pDisplay.add(Box.createHorizontalStrut(5));
        pDisplay.add(this.widthSlider);
        pDisplay.setLayout(new BoxLayout(pDisplay, BoxLayout.X_AXIS));
        pParams.add(this.chkDiffusion);
        pParams.add(this.chkCommit);
        pParams.add(pDisplay);
        pParams.setLayout(new BoxLayout(pParams, BoxLayout.Y_AXIS));

        // *********************************
        // MENU DE LA FENETRE
        // *********************************
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFichier = new JMenu("Fichier");
        ImageIcon iconeAide = new ImageIcon(
                "C:\\Program Files\\Laser-Scan\\clarity-v2.6\\images\\help.gif");
        JMenuItem aide = new JMenuItem("Aide", iconeAide);
        aide.setAccelerator(KeyStroke.getKeyStroke('N',
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        aide.setActionCommand("aide");
        aide.addActionListener(this);
        ImportAction importAct = new ImportAction(this);
        ExportAction exportAct = new ExportAction(this);
        ImportPrecAction importPrecAct = new ImportPrecAction(this);
        menuFichier.add(importAct);
        menuFichier.add(exportAct);
        menuFichier.add(importPrecAct);
        menuFichier.addSeparator();
        menuFichier.add(aide);
        menuBar.add(menuFichier);
        menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // *********************************
        // LA FRAME
        // *********************************
        this.setJMenuBar(menuBar);
        this.getContentPane().add(onglets);
        this.getContentPane().add(pParams);
        this.getContentPane().add(panelOK);
        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setVisible(true);
        this.pack();

    }

    class ImportAction extends AbstractAction {

        /**
           * 
           */
        private static final long serialVersionUID = 1L;
        /**
           * 
           */
        final JFileChooser fc = new JFileChooser();
        LeastSquaresFrame frame;

        @Override
        public void actionPerformed(ActionEvent e) {
            this.fc.setCurrentDirectory(new File(LeastSquaresFrame.filePath));
            int returnVal = this.fc.showOpenDialog(this.frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // initialisation apr�s le choix du fichier
                File fic = this.fc.getSelectedFile();
                try {
                    this.frame.importFicXML(fic);
                    LastSessionParameters.getInstance().setParameter(
                            "Least Squares Config", fic.getPath(),
                            new HashMap<String, String>());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParserConfigurationException e2) {
                    e2.printStackTrace();
                } catch (SAXException e2) {
                    e2.printStackTrace();
                } catch (TransformerException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                this.frame.pack();
            }
        }

        public ImportAction(LeastSquaresFrame parent) {
            this.frame = parent;
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Import d'un fichier XML de mapspecs");
            this.putValue(Action.NAME, "Import XML");
            this.putValue(Action.MNEMONIC_KEY, new Integer('I'));
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('I',
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.fc.setCurrentDirectory(new File("goth_dataroot"));
            this.fc.setDialogTitle(
                    "Ouvrir un fichier XML de mapspecs pour les Moindres carrés");
            this.fc.setFileFilter(new XMLFileFilter());
        }
    }// class ImportAction

    class ImportPrecAction extends AbstractAction {

        /**
           * 
           */
        private static final long serialVersionUID = 1L;
        LeastSquaresFrame frame;

        @Override
        public void actionPerformed(ActionEvent e) {

            // initialisation après le choix du fichier
            String path = (String) LastSessionParameters.getInstance()
                    .getParameterValue("Least Squares Config");
            File fic = new File(path);
            System.out.println(fic.getName());
            try {
                this.frame.importFicXML(fic);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ParserConfigurationException e2) {
                e2.printStackTrace();
            } catch (SAXException e2) {
                e2.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            this.frame.pack();
        }

        public ImportPrecAction(LeastSquaresFrame parent) {
            this.frame = parent;
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Import du fichier XML de mapspecs utilisé à"
                            + "la dernière session");
            this.putValue(Action.NAME, "Import des dernières mapspecs");
            this.putValue(Action.MNEMONIC_KEY, new Integer('P'));
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('P',
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
    }// class ImportPrecAction

    // *******************************************************************
    // fonction d'import dans la frame de mapspecs stock�es au format XML
    private void importFicXML(File fic) throws ParserConfigurationException,
            SAXException, IOException, ClassNotFoundException {
        // on commence par ouvrir le doucment XML pour le parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        Document doc;
        doc = db.parse(fic);
        doc.getDocumentElement().normalize();
        System.out.println(
                "Root element " + doc.getDocumentElement().getNodeName());

        // **************************************
        // ON PARSE LE FICHIER XML
        // **************************************
        // on commence par les mapspecs
        Element mapspecElem = (Element) doc.getElementsByTagName("mapspecs")
                .item(0);
        // puis on récupère les classes fixes
        Element fixeElem = (Element) mapspecElem
                .getElementsByTagName("objets-fixes").item(0);
        if (fixeElem != null) {
            Class<?>[] classes = new Class<?>[fixeElem
                    .getElementsByTagName("classe").getLength()];
            for (int i = 0; i < classes.length; i++) {
                Element classElem = (Element) fixeElem
                        .getElementsByTagName("classe").item(i);
                String className = classElem.getChildNodes().item(0)
                        .getNodeValue();
                classes[i] = Class.forName(className);
                this.classesFixes.add(classes[i]);
            }
            this.listeObjFixes.setListData(classes);
            this.contraintesActivees.addAll(this.contraintesFixes);
        } // if(fixeElem!=null)

        // puis on récupère les classes rigides
        Element rigideElem = (Element) mapspecElem
                .getElementsByTagName("objets-rigides").item(0);
        if (rigideElem != null) {
            Class<?>[] classes = new Class<?>[rigideElem
                    .getElementsByTagName("classe").getLength()];
            for (int i = 0; i < classes.length; i++) {
                Element classElem = (Element) rigideElem
                        .getElementsByTagName("classe").item(i);
                String className = classElem.getChildNodes().item(0)
                        .getNodeValue();
                classes[i] = Class.forName(className);
                this.classesRigides.add(classes[i]);
            }
            this.listeObjRigides.setListData(classes);
            this.contraintesActivees.addAll(this.contraintesRigides);
        } // if(rigideElem!=null)

        // puis on récupère les classes mall�ables
        Element mallElem = (Element) mapspecElem
                .getElementsByTagName("objets-malleables").item(0);
        if (mallElem != null) {
            Class<?>[] classes = new Class<?>[mallElem
                    .getElementsByTagName("classe").getLength()];
            for (int i = 0; i < classes.length; i++) {
                Element classElem = (Element) mallElem
                        .getElementsByTagName("classe").item(i);
                String className = classElem.getChildNodes().item(0)
                        .getNodeValue();
                classes[i] = Class.forName(className);
                this.classesMalleables.add(classes[i]);
            }
            this.listeObjMalleables.setListData(classes);
            this.contraintesActivees.addAll(this.contraintesMalleables);
        } // if(mallElem!=null)

        // on parse maintenant les contraintes externes
        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Contrainte", "Classe 1", "Classe 2", "Seuil" },
                0);
        Element contrExtElem = (Element) mapspecElem
                .getElementsByTagName("contraintes-externes").item(0);
        for (int i = 0; i < contrExtElem.getElementsByTagName("contrainte")
                .getLength(); i++) {
            Element contrainteElem = (Element) contrExtElem
                    .getElementsByTagName("contrainte").item(i);
            // on récupère le nom
            Element nomElem = (Element) contrainteElem
                    .getElementsByTagName("nom").item(0);
            String nom = nomElem.getChildNodes().item(0).getNodeValue();
            // on récupère la classe1
            Element classe1Elem = (Element) contrainteElem
                    .getElementsByTagName("classe1").item(0);
            String classe1 = classe1Elem.getChildNodes().item(0).getNodeValue();
            // on récupère la classe2
            Element classe2Elem = (Element) contrainteElem
                    .getElementsByTagName("classe2").item(0);
            String classe2 = classe2Elem.getChildNodes().item(0).getNodeValue();
            // on récupère le seuil
            Element seuilElem = (Element) contrainteElem
                    .getElementsByTagName("seuil").item(0);
            String seuil = seuilElem.getChildNodes().item(0).getNodeValue();
            model.addRow(new String[] { nom, classe1, classe2, seuil });
        }
        this.tableContrRel.setModel(model);
        this.tableContrRel.setEnabled(false);
        this.tableContrRel.getColumnModel().getColumn(0)
                .setCellRenderer(new ClassSimpleNameTableRenderer());
        this.tableContrRel.getColumnModel().getColumn(1)
                .setCellRenderer(new ClassSimpleNameTableRenderer());
        this.tableContrRel.getColumnModel().getColumn(2)
                .setCellRenderer(new ClassSimpleNameTableRenderer());

        // on s'occupe maintenant des pondérations
        Element pondElem = (Element) doc.getElementsByTagName("ponderations")
                .item(0);
        DefaultListModel<String> lmodel = new DefaultListModel<>();
        for (int i = 0; i < pondElem.getElementsByTagName("contrainte")
                .getLength(); i++) {
            Element contrainteElem = (Element) pondElem
                    .getElementsByTagName("contrainte").item(i);
            Element nomElem = (Element) contrainteElem
                    .getElementsByTagName("classe").item(0);
            String nom = nomElem.getChildNodes().item(0).getNodeValue();
            Element poidsElem = (Element) contrainteElem
                    .getElementsByTagName("poids").item(0);
            String poidsS = poidsElem.getChildNodes().item(0).getNodeValue();
            Double poids = new Double(poidsS);
            this.poidsContraintes.put(nom, poids);
            lmodel.addElement(nom);
        }
        this.listeContraintes.setModel(lmodel);

        // on s'occupe du mode de sélection
        Element selectionElem = (Element) doc.getElementsByTagName("selection")
                .item(0);
        String choixSel = selectionElem.getChildNodes().item(0).getNodeValue();
        if (choixSel.equals("objets")) {
            this.selectionObjs.setSelected(true);
        }
        if (choixSel.equals("fenetre")) {
            this.fenetre.setSelected(true);
        }
        if (choixSel.equals("surfaces")) {
            this.selectionSurfs.setSelected(true);
        }
        Element clasSurfElem = (Element) selectionElem
                .getElementsByTagName("classe-surf").item(0);
        if (clasSurfElem != null) {
            this.txtClasseSurf.setText(
                    clasSurfElem.getChildNodes().item(0).getNodeValue());
        }

        // on parse enfin l'échelle
        Element echElem = (Element) doc.getElementsByTagName("echelle").item(0);
        this.txtEchelle.setText(echElem.getChildNodes().item(0).getNodeValue());

        this.pack();
    }// importFicXML(File fic)

    // fonction d'export des mapspecs de la frame au format XML
    private void exportFicXML(File fic) {
        try {
            Element e = null;
            Node n = null;
            Node n1 = null;
            Node n2 = null;
            // ********************************************
            // RECUPERATION DES PARAMETRES
            String choixSelection = "surfaces";
            if (this.fenetre.isSelected()) {
                choixSelection = "fenetre";
            }
            if (this.selectionObjs.isSelected()) {
                choixSelection = "objets";
            }

            // ********************************************
            // CREATION DU DOCUMENT XML
            // Document (Xerces implementation only).
            Document xmldoc = new DocumentImpl();
            // Root element.
            Element root = xmldoc.createElement("mapspecs-moindres-carres");

            // les paramètres
            Element mapspecs = xmldoc.createElement("mapspecs");
            root.appendChild(mapspecs);

            // on commence par les objets fixes
            Element objsFixes = xmldoc.createElement("objets-fixes");
            mapspecs.appendChild(objsFixes);
            ListModel<Class<?>> modelFixe = this.listeObjFixes.getModel();
            for (int i = 0; i < modelFixe.getSize(); i++) {
                // Child i.
                Element classe = xmldoc.createElement("classe");
                n = xmldoc.createTextNode(
                        ((Class<?>) modelFixe.getElementAt(i)).getName());
                classe.appendChild(n);
                objsFixes.appendChild(classe);
            }

            // puis les objets rigides
            Element objsRigides = xmldoc.createElement("objets-rigides");
            mapspecs.appendChild(objsRigides);
            ListModel<Class<?>> modelRigide = this.listeObjRigides.getModel();
            for (int i = 0; i < modelRigide.getSize(); i++) {
                // Child i.
                Element classe = xmldoc.createElement("classe");
                n = xmldoc.createTextNode(
                        ((Class<?>) modelRigide.getElementAt(i)).getName());
                classe.appendChild(n);
                objsRigides.appendChild(classe);
            }

            // puis les objets malleables
            Element objsMalleables = xmldoc.createElement("objets-malleables");
            mapspecs.appendChild(objsMalleables);
            ListModel<Class<?>> modelMalleables = this.listeObjMalleables
                    .getModel();
            for (int i = 0; i < modelMalleables.getSize(); i++) {
                // Child i.
                Element classe = xmldoc.createElement("classe");
                n = xmldoc.createTextNode(
                        ((Class<?>) modelMalleables.getElementAt(i)).getName());
                classe.appendChild(n);
                objsMalleables.appendChild(classe);
            }

            // puis les contraintes externes
            Element contrExtElem = xmldoc.createElement("contraintes-externes");
            mapspecs.appendChild(contrExtElem);
            DefaultTableModel dtm = (DefaultTableModel) this.tableContrRel
                    .getModel();
            for (int i = 0; i < this.tableContrRel.getRowCount(); i++) {
                // on la stocke en XML
                Element contrainteElem = xmldoc.createElement("contrainte");
                contrExtElem.appendChild(contrainteElem);
                Element nomElem = xmldoc.createElement("nom");
                contrainteElem.appendChild(nomElem);
                n = xmldoc.createTextNode((String) dtm.getValueAt(i, 0));
                nomElem.appendChild(n);
                Element classe1Elem = xmldoc.createElement("classe1");
                contrainteElem.appendChild(classe1Elem);
                n = xmldoc.createTextNode((String) dtm.getValueAt(i, 1));
                classe1Elem.appendChild(n);
                Element classe2Elem = xmldoc.createElement("classe2");
                contrainteElem.appendChild(classe2Elem);
                n = xmldoc.createTextNode((String) dtm.getValueAt(i, 2));
                classe2Elem.appendChild(n);
                Element seuilElem = xmldoc.createElement("seuil");
                contrainteElem.appendChild(seuilElem);
                n = xmldoc.createTextNode((String) dtm.getValueAt(i, 3));
                seuilElem.appendChild(n);
            }

            // *********************************
            // les pond�rations de contraintes
            Element ponderations = xmldoc.createElement("ponderations");
            root.appendChild(ponderations);

            Iterator<String> iterPds = this.poidsContraintes.keySet()
                    .iterator();
            while (iterPds.hasNext()) {
                // Child i.
                e = xmldoc.createElementNS(null, "contrainte");
                String classe = iterPds.next();
                Element classePond = xmldoc.createElement("classe");
                Element poidsPond = xmldoc.createElement("poids");
                n1 = xmldoc.createTextNode(classe);
                n2 = xmldoc.createTextNode(
                        this.poidsContraintes.get(classe).toString());
                classePond.appendChild(n1);
                poidsPond.appendChild(n2);
                e.appendChild(classePond);
                e.appendChild(poidsPond);
                ponderations.appendChild(e);
            }

            // *****************************
            // les paramètres de sélection
            Element selection = xmldoc.createElement("selection");
            root.appendChild(selection);
            Node choixSel = xmldoc.createTextNode(choixSelection);
            selection.appendChild(choixSel);
            if (this.selectionSurfs.isSelected()) {
                e = xmldoc.createElementNS(null, "classe-surf");
                n = xmldoc.createTextNode(this.txtClasseSurf.getText());
                e.appendChild(n);
                selection.appendChild(e);
            }
            Element echelle = xmldoc.createElement("echelle");
            root.appendChild(echelle);
            n = xmldoc.createTextNode(this.txtEchelle.getText());
            echelle.appendChild(n);

            xmldoc.appendChild(root);
            XMLUtil.writeDocumentToXml(xmldoc, fic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }// exportFicXML(File fic)

    private void removeAllMap(Map<String, Double> map, Set<String> set) {
        for (String str : set) {
            map.remove(str);
        }

    }

    class ExportAction extends AbstractAction {

        /**
           * 
           */
        private static final long serialVersionUID = 1L;
        final JFileChooser fc = new JFileChooser();
        LeastSquaresFrame frame;

        @Override
        public void actionPerformed(ActionEvent e) {
            this.fc.setCurrentDirectory(new File(LeastSquaresFrame.filePath));
            int returnVal = this.fc.showSaveDialog(this.frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // initialisation après le choix du fichier
                File fic = this.fc.getSelectedFile();
                System.out.println(fic.getName());
                this.frame.exportFicXML(fic);
                try {
                    LastSessionParameters.getInstance().setParameter(
                            "Least Squares Config", fic.getAbsolutePath(),
                            new HashMap<String, String>());
                } catch (TransformerException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public ExportAction(LeastSquaresFrame parent) {
            this.frame = parent;
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Export des mapspecs dans un fichier XML");
            this.putValue(Action.NAME, "Export XML");
            this.putValue(Action.MNEMONIC_KEY, new Integer('E'));
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('E',
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
    }// class ExportAction

    public class BrowserClassesFrame extends JFrame implements ActionListener {

        /**
           * 
           */
        private static final long serialVersionUID = 1L;
        GeneObjClassTree classTree;

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
         * ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                Class<?>[] classesChoisies = this.classTree
                        .getSelectedClasses();
                if (LeastSquaresFrame.this.listeCourante.equals("fixe")) {
                    for (Class<?> c : classesChoisies) {
                        LeastSquaresFrame.this.classesFixes.add(c);
                    }
                    LeastSquaresFrame.this.contraintesActivees
                            .addAll(LeastSquaresFrame.this.contraintesFixes);
                    LeastSquaresFrame.putColnMap(
                            LeastSquaresFrame.this.poidsContraintes,
                            LeastSquaresFrame.this.contraintesFixes,
                            new Double(1));
                } else if (LeastSquaresFrame.this.listeCourante
                        .equals("rigide")) {
                    for (Class<?> c : classesChoisies) {
                        LeastSquaresFrame.this.classesRigides.add(c);
                    }
                    LeastSquaresFrame.this.contraintesActivees
                            .addAll(LeastSquaresFrame.this.contraintesRigides);
                    LeastSquaresFrame.putColnMap(
                            LeastSquaresFrame.this.poidsContraintes,
                            LeastSquaresFrame.this.contraintesRigides,
                            new Double(1));
                } else if (LeastSquaresFrame.this.listeCourante
                        .equals("malleable")) {
                    for (Class<?> c : classesChoisies) {
                        LeastSquaresFrame.this.classesMalleables.add(c);
                    }
                    LeastSquaresFrame.this.contraintesActivees.addAll(
                            LeastSquaresFrame.this.contraintesMalleables);
                    LeastSquaresFrame.putColnMap(
                            LeastSquaresFrame.this.poidsContraintes,
                            LeastSquaresFrame.this.contraintesMalleables,
                            new Double(1));
                } else {
                    LeastSquaresFrame.this.txtClasseSurf.setText(
                            this.classTree.getSelectedClasses()[0].getName());
                }
                this.classTree.clearSelection();
                this.setVisible(false);
                LeastSquaresFrame.this.miseAjourFrame();
            } else if (e.getActionCommand().equals("Annuler")) {
                this.classTree.clearSelection();
                this.setVisible(false);
            }
        }

        public BrowserClassesFrame() {
            super("Ajouter des classes");
            this.setSize(300, 450);

            JPanel panelBtn = new JPanel();
            panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
            JButton bouton0 = new JButton("OK");
            bouton0.addActionListener(this);
            bouton0.setActionCommand("OK");
            JButton bouton1 = new JButton("Annuler");
            bouton1.addActionListener(this);
            bouton1.setActionCommand("Annuler");
            panelBtn.add(bouton0);
            panelBtn.add(bouton1);

            JPanel panelTree = new JPanel();
            this.classTree = new GeneObjClassTree(true);
            if (LeastSquaresFrame.this.listeCourante.equals("surf")) {
                this.classTree.filterClasses(IGeneObjSurf.class);
            }
            if (LeastSquaresFrame.this.listeCourante.equals("malleable")) {
                this.classTree.setSelectionMode(
                        TreeSelectionModel.SINGLE_TREE_SELECTION);
            } else {
                this.classTree.setSelectionMode(
                        TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            }
            panelTree.add(new JScrollPane(this.classTree));

            JPanel panelTotal = new JPanel();
            panelTotal.setLayout(new BoxLayout(panelTotal, BoxLayout.Y_AXIS));
            panelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelTotal.add(panelTree);
            panelTotal.add(panelBtn);

            this.getContentPane().add(panelTotal);
            this.setVisible(true);
        }

    }// class BrowserClassFrame

    // enlève la ou les classes sélectionnées dans la liste courante
    void enleverClasseListe() {
        if (this.listeCourante.equals("fixe")) {
            // on récupère la classes sélectionnée
            Class<?> classeChoisie = this.listeObjFixes.getSelectedValue();
            // on enlève les classesChoisies de classe en utilisant des sets
            this.classesFixes.remove(classeChoisie);
        } else if (this.listeCourante.equals("rigide")) {
            // on récupère la classes sélectionnée
            Class<?> classeChoisie = this.listeObjRigides.getSelectedValue();
            // on enlève les classesChoisies de classe en utilisant des sets
            this.classesRigides.remove(classeChoisie);
        } else {
            // on récupère la classes sélectionnée
            Class<?> classeChoisie = this.listeObjMalleables.getSelectedValue();
            // on enlève les classesChoisies de classe en utilisant des sets
            this.classesMalleables.remove(classeChoisie);
        }
        this.miseAjourFrame();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.
     * ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent ev) {

        Double poids = this.poidsContraintes.get(this.listeContraintes
                .getModel().getElementAt(ev.getFirstIndex()));
        this.curseurPoids.setValue(poids.intValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.
     * ChangeEvent )
     */
    @Override
    public void stateChanged(ChangeEvent ev) {

        // on met à jour la map poidsContraintes avec la nouvelle valeur
        if (this.listeContraintes.isSelectionEmpty() == false) {
            String contrainte = (String) this.listeContraintes
                    .getSelectedValue();
            this.poidsContraintes.put(contrainte,
                    new Double(((JSlider) ev.getSource()).getValue()));
        }
    }// stateChanged

    static void putColnMap(Map<String, Double> map, Collection<String> coln,
            Double value) {
        Iterator<String> i = coln.iterator();
        while (i.hasNext()) {
            String obj = i.next();
            map.put(obj, value);
        }
    }// putColnMap

    public void lancerMoindresCarres() {
        // on commence par créer des mapspecs
        MapspecsLS mapspecs = this.buildMapspecs();

        // puis on construit un scheduler
        LSScheduler sched = new LSScheduler(mapspecs);
        sched.setSolver(MatrixSolver.JAMA);

        // on lance la généralisation
        EndVertexStrategy strategy = EndVertexStrategy.FIX;
        if (this.chkDiffusion.isSelected())
            strategy = EndVertexStrategy.DIFFUSION;
        sched.triggerAdjustment(strategy, this.chkCommit.isSelected());

        // affichage de l'autre géométrie
        for (IGeometry geom : sched.getMapObjGeom().values()) {
            CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                    .addFeatureToGeometryPool(geom, this.colorButton.getColor(),
                            this.widthSlider.getValue());
        }
    }

    protected void miseAjourFrame() {
        // on met à jour les listes en fonction des collections courantes
        DefaultListModel<String> dlm = new DefaultListModel<>();
        for (String s : this.contraintesActivees) {
            dlm.addElement(s);
        }
        this.listeContraintes.setModel(dlm);
        DefaultListModel<Class<?>> dlm2 = new DefaultListModel<>();
        for (Class<?> s : this.classesFixes) {
            dlm2.addElement(s);
        }
        this.listeObjFixes.setModel(dlm2);
        dlm2 = new DefaultListModel<>();
        for (Class<?> s : this.classesMalleables) {
            dlm2.addElement(s);
        }
        this.listeObjMalleables.setModel(dlm2);
        dlm2 = new DefaultListModel<>();
        for (Class<?> s : this.classesRigides) {
            dlm2.addElement(s);
        }
        this.listeObjRigides.setModel(dlm2);
        this.pack();
    }

    private void findExternalConstraints() {
        this.externalConstraints.addAll(FileUtil.findClassesInPackage(
                LSExternalConstraint.class.getPackage(),
                LSExternalConstraint.class, false));
    }

    /**
     * Get all the available external constraints.
     * 
     * @return
     */
    public Class<?>[] getExternalConstraintsArray() {
        Class<?>[] array = new Class<?>[this.externalConstraints.size()];
        int i = 0;
        for (Class<?> classObj : this.externalConstraints) {
            array[i] = classObj;
            i++;
        }
        return array;
    }

    public MapspecsLS buildMapspecs() {
        MapspecsLS mapspecs = new MapspecsLS();
        // on remplit les champs des mapspecs à partir des champs de la frame
        // on commence par l'échelle
        mapspecs.setEchelle(
                new Integer(this.txtEchelle.getText()).doubleValue());

        // on récupère les classes fixes, rigides et malléables
        for (int i = 0; i < this.listeObjFixes.getModel().getSize(); i++) {
            Class<?> classe = (Class<?>) this.listeObjFixes.getModel()
                    .getElementAt(i);
            mapspecs.getClassesFixes().add(classe.getName());
        }
        for (int i = 0; i < this.listeObjRigides.getModel().getSize(); i++) {
            Class<?> classe = (Class<?>) this.listeObjRigides.getModel()
                    .getElementAt(i);
            mapspecs.getClassesRigides().add(classe.getName());

        }
        for (int i = 0; i < this.listeObjMalleables.getModel().getSize(); i++) {
            Class<?> classe = (Class<?>) this.listeObjMalleables.getModel()
                    .getElementAt(i);
            mapspecs.getClassesMalleables().add(classe.getName());
        }

        // on récupère les contraintes externes
        for (int i = 0; i < this.tableContrRel.getRowCount(); i++) {
            String nom = (String) this.tableContrRel.getModel().getValueAt(i,
                    0);
            String classe1 = (String) this.tableContrRel.getModel()
                    .getValueAt(i, 1);
            String classe2 = (String) this.tableContrRel.getModel()
                    .getValueAt(i, 2);
            String seuil = (String) this.tableContrRel.getModel().getValueAt(i,
                    3);
            String[] cleMap = { nom, classe1, classe2 };
            mapspecs.getContraintesExternes().put(cleMap, new Double(seuil));
        }

        // on r�cup�re les contraintes activ�es
        Iterator<String> iter = this.contraintesFixes.iterator();
        while (iter.hasNext()) {
            String contrainte = iter.next();
            if (this.contraintesActivees.contains(contrainte) == false) {
                continue;
            }
            mapspecs.getContraintesFixes().add(contrainte);
        }
        iter = this.contraintesRigides.iterator();
        while (iter.hasNext()) {
            String contrainte = iter.next();
            if (this.contraintesActivees.contains(contrainte) == false) {
                continue;
            }
            mapspecs.getContraintesRigides().add(contrainte);
        }
        iter = this.contraintesMalleables.iterator();
        while (iter.hasNext()) {
            String contrainte = iter.next();
            if (this.contraintesActivees.contains(contrainte) == false) {
                continue;
            }
            mapspecs.getContraintesMalleables().add(contrainte);
        }

        // on r�cup�re la pond�ration des contraintes
        mapspecs.setConstraintWeights(new HashMap<String, Double>());
        iter = this.poidsContraintes.keySet().iterator();
        while (iter.hasNext()) {
            String contrainte = iter.next();
            Double poids = this.poidsContraintes.get(contrainte);
            mapspecs.getPoidsContraintes().put(contrainte, poids);
        }

        // si le choix de s�lection est la fen�tre, on r�cup�re les coordonn�es
        if (this.fenetre.isSelected()) {
            // TODO
        } else if (this.selectionObjs.isSelected()) {
            // TODO
        } else {
            // TODO
        }

        // on récupère enfin la sélection d'objets
        mapspecs.setSelectedObjects(SelectionUtil.getSelectedObjects(
                CartAGenPlugin.getInstance().getApplication()));

        return mapspecs;
    }
}
