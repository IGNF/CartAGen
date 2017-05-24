package fr.ign.cogit.cartagen.appli.collagen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import fr.ign.cogit.cartagen.collagen.components.translator.ConstraintsInstanciation;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMesoConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalRelationalConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class ComputeMonitorsFrame extends JFrame
    implements ActionListener, ChangeListener {

  /****/
  private static final long serialVersionUID = 1L;
  private Map<SpecificationElement, JCheckBox> checks = new HashMap<SpecificationElement, JCheckBox>();
  private JCheckBox chkToutes;
  private JRadioButton rbFenetre, rbToutes;
  private GeOxygeneApplication application;
  private ConstraintsInstanciation inst;
  private SchemaAnnotation annotation;
  private ProjectFrame pFrame;
  private Set<ConstraintMonitor> monitors;

  @Override
  public void stateChanged(ChangeEvent e) {
    if (chkToutes.isSelected())
      for (SpecificationElement elem : checks.keySet()) {
        checks.get(elem).setSelected(true);
        checks.get(elem).setEnabled(false);
      }
    else
      for (SpecificationElement elem : checks.keySet())
        checks.get(elem).setEnabled(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("annuler"))
      this.setVisible(false);
    else if (e.getActionCommand().equals("ok"))
      try {
        instanciate();
        System.out.println(monitors.size() + " monitors created");
        // Layer layer = new NamedLayer(pFrame.getSld(), "legibilityGrid");
        Layer layer = pFrame.getSld().createLayer("constraintMonitors",
            IPoint.class, Color.RED);
        StyledLayerDescriptor defaultSld;
        try {
          defaultSld = StyledLayerDescriptor
              .unmarshall(IGeneObj.class.getClassLoader()
                  .getResourceAsStream("sld/sld_constraint_monitors.xml"));
          layer.getStyles()
              .addAll(defaultSld.getLayer("constraintMonitors").getStyles());
        } catch (JAXBException e1) {
          e1.printStackTrace();
        }
        IPopulation<IFeature> pop = new Population<>("constraintMonitors");
        pop.addAll(monitors);
        pFrame.getSld().getDataSet().addPopulation(pop);
        pFrame.getSld().add(layer);

      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
      }
  }

  public ComputeMonitorsFrame(GeOxygeneApplication application,
      ConstraintsInstanciation instanciation, SchemaAnnotation annotation) {
    super("Compute Constraint Monitors");
    this.application = application;
    this.inst = instanciation;
    this.annotation = annotation;
    this.pFrame = application.getMainFrame().getSelectedProjectFrame();
    this.setSize(500, 400);
    this.setAlwaysOnTop(true);

    // un panneau avec les checkboxes
    JPanel pCheck = new JPanel();
    chkToutes = new JCheckBox("Toutes les contraintes");
    chkToutes.addChangeListener(this);
    chkToutes.setFont(chkToutes.getFont().deriveFont(Font.BOLD));
    Border espacement = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    Border ligne = BorderFactory.createLineBorder(Color.BLACK);
    // contraintes micros
    JPanel pCheckMic = new JPanel();
    Border bMic = BorderFactory.createTitledBorder(ligne, "Contraintes Micro");
    pCheckMic.setBorder(BorderFactory.createCompoundBorder(bMic, espacement));
    pCheckMic.setLayout(new BoxLayout(pCheckMic, BoxLayout.Y_AXIS));
    // contraintes mesos
    JPanel pCheckMes = new JPanel();
    Border bMes = BorderFactory.createTitledBorder(ligne, "Contraintes Meso");
    pCheckMes.setBorder(BorderFactory.createCompoundBorder(bMes, espacement));
    pCheckMes.setLayout(new BoxLayout(pCheckMes, BoxLayout.Y_AXIS));
    // contraintes relationnelles
    JPanel pCheckRel = new JPanel();
    Border bRel = BorderFactory.createTitledBorder(ligne,
        "Contraintes Relationnelles");
    pCheckRel.setBorder(BorderFactory.createCompoundBorder(bRel, espacement));
    pCheckRel.setLayout(new BoxLayout(pCheckRel, BoxLayout.Y_AXIS));
    // boucles sur les contraintes de l'instanciation
    for (SpecificationElement elem : inst.getSpecificationElts()) {
      JCheckBox check = new JCheckBox(elem.getName());
      if (elem instanceof FormalMicroConstraint)
        pCheckMic.add(check);
      if (elem instanceof FormalMesoConstraint)
        pCheckMes.add(check);
      if (elem instanceof FormalRelationalConstraint)
        pCheckRel.add(check);
      checks.put(elem, check);
    }
    pCheck.setLayout(new BoxLayout(pCheck, BoxLayout.X_AXIS));
    pCheck.add(pCheckMic);
    pCheck.add(Box.createHorizontalGlue());
    pCheck.add(pCheckMes);
    pCheck.add(Box.createHorizontalGlue());
    pCheck.add(pCheckRel);

    // un panneau pour choisir l'espace de travail
    JPanel pEspTravail = new JPanel();
    rbToutes = new JRadioButton("Toute la BD");
    rbFenetre = new JRadioButton("Objets de la fenêtre");
    ButtonGroup bg = new ButtonGroup();
    bg.add(rbToutes);
    bg.add(rbFenetre);
    rbToutes.setSelected(true);
    pEspTravail.add(rbToutes);
    pEspTravail.add(rbFenetre);
    pEspTravail.setBorder(espacement);
    pEspTravail.setLayout(new BoxLayout(pEspTravail, BoxLayout.X_AXIS));

    // un panneau OK/Annuler
    JPanel panelBoutons = new JPanel();
    // le bouton OK
    JButton btnOK = new JButton("OK");
    btnOK.addActionListener(this);
    btnOK.setActionCommand("ok");
    btnOK.setPreferredSize(new Dimension(100, 50));
    // le bouton annuler
    JButton btnAnnuler = new JButton("Annuler");
    btnAnnuler.addActionListener(this);
    btnAnnuler.setActionCommand("annuler");
    btnAnnuler.setPreferredSize(new Dimension(100, 50));
    panelBoutons.add(btnOK);
    panelBoutons.add(btnAnnuler);
    panelBoutons.setBorder(espacement);
    panelBoutons.setLayout(new BoxLayout(panelBoutons, BoxLayout.X_AXIS));

    // ***********************************
    // MISE EN PAGE FINALE
    // ***********************************
    this.getContentPane().add(pCheck);
    this.getContentPane().add(chkToutes);
    this.getContentPane().add(pEspTravail);
    this.getContentPane().add(panelBoutons);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  private void instanciate() {
    // cas simple où toutes les contraintes sont instanciées
    if (chkToutes.isSelected() && rbToutes.isSelected()) {
      try {
        monitors = inst.instanciateConstraints(annotation);
      } catch (ClassNotFoundException | NoSuchMethodException
          | SecurityException | InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
      return;
    }

    // cas général sur toutes les données
    if (rbToutes.isSelected()) {
      Set<SpecificationElement> choisis = new HashSet<SpecificationElement>();
      for (SpecificationElement elem : checks.keySet())
        if (checks.get(elem).isSelected())
          choisis.add(elem);
      try {
        monitors = inst.instanciateConstraints(choisis, annotation);
      } catch (ClassNotFoundException | NoSuchMethodException
          | SecurityException | InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
      return;
    }

    // on calcule ici la géométrie de la fenêtre

    IEnvelope geom = application.getMainFrame().getSelectedProjectFrame()
        .getLayerViewPanel().getViewport().getEnvelopeInModelCoordinates();
    if (chkToutes.isSelected())
      try {
        monitors = inst.instanciateConstraints(annotation, geom);
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | ClassNotFoundException | SecurityException
          | NoSuchMethodException e) {
        e.printStackTrace();
      }
    else {
      Set<SpecificationElement> choisis = new HashSet<SpecificationElement>();
      for (SpecificationElement elem : checks.keySet())
        if (checks.get(elem).isSelected())
          choisis.add(elem);
      try {
        monitors = inst.instanciateConstraints(choisis, annotation, geom);
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | ClassNotFoundException | SecurityException
          | NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
  }
}
