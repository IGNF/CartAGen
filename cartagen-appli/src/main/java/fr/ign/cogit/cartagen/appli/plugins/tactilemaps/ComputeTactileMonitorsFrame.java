package fr.ign.cogit.cartagen.appli.plugins.tactilemaps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

import com.opencsv.exceptions.CsvValidationException;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.tactilemaps.monitors.MonitorFileParser;
import fr.ign.cogit.cartagen.tactilemaps.monitors.MonitorInstantiation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * This frame shows all the possible constraint monitors that are available for
 * tactile maps, and allows the instantiation of these monitors with the given
 * parameters.
 * 
 * @author GTouya
 *
 */
public class ComputeTactileMonitorsFrame extends JFrame implements ActionListener, ChangeListener {

	/****/
	private static final long serialVersionUID = 1L;
	private Map<MonitorInstantiation, JCheckBox> checks = new HashMap<MonitorInstantiation, JCheckBox>();
	private JCheckBox chkToutes;
	private JRadioButton rbWindow, rbDataset;
	private GeOxygeneApplication application;
	private ProjectFrame pFrame;
	private Set<ConstraintMonitor> monitors;
	private List<MonitorInstantiation> monitorInstantiations;

	@Override
	public void stateChanged(ChangeEvent e) {
		if (chkToutes.isSelected())
			for (MonitorInstantiation elem : checks.keySet()) {
				checks.get(elem).setSelected(true);
				checks.get(elem).setEnabled(false);
			}
		else
			for (MonitorInstantiation elem : checks.keySet())
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
				Layer layer = pFrame.getSld().createLayer("constraintMonitors", IPoint.class, Color.RED);
				StyledLayerDescriptor defaultSld;
				try {
					defaultSld = StyledLayerDescriptor.unmarshall(
							IGeneObj.class.getClassLoader().getResourceAsStream("sld/sld_constraint_monitors.xml"));
					layer.getStyles().addAll(defaultSld.getLayer("constraintMonitors").getStyles());
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

	public ComputeTactileMonitorsFrame(GeOxygeneApplication application, File constraintFile)
			throws ClassNotFoundException, IOException, CsvValidationException {
		super("Compute Constraint Monitors");
		this.application = application;
		this.monitorInstantiations = new MonitorFileParser(constraintFile).getMonitors();
		this.pFrame = application.getMainFrame().getSelectedProjectFrame();
		this.monitors = new HashSet<>();
		this.setSize(500, 400);
		this.setAlwaysOnTop(true);

		// a panel with checkboxes
		JPanel pCheck = new JPanel();
		chkToutes = new JCheckBox("All constraints");
		chkToutes.addChangeListener(this);
		chkToutes.setFont(chkToutes.getFont().deriveFont(Font.BOLD));
		Border espacement = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border ligne = BorderFactory.createLineBorder(Color.BLACK);
		// micro constraints
		JPanel pCheckMic = new JPanel();
		Border bMic = BorderFactory.createTitledBorder(ligne, "Micro Constraints");
		pCheckMic.setBorder(BorderFactory.createCompoundBorder(bMic, espacement));
		pCheckMic.setLayout(new BoxLayout(pCheckMic, BoxLayout.Y_AXIS));
		// meso constraints
		JPanel pCheckMes = new JPanel();
		Border bMes = BorderFactory.createTitledBorder(ligne, "Meso Constraints");
		pCheckMes.setBorder(BorderFactory.createCompoundBorder(bMes, espacement));
		pCheckMes.setLayout(new BoxLayout(pCheckMes, BoxLayout.Y_AXIS));
		// relational constraints
		JPanel pCheckRel = new JPanel();
		Border bRel = BorderFactory.createTitledBorder(ligne, "Relational Constraints");
		pCheckRel.setBorder(BorderFactory.createCompoundBorder(bRel, espacement));
		pCheckRel.setLayout(new BoxLayout(pCheckRel, BoxLayout.Y_AXIS));
		// loop on the constraints contained in the input file
		for (MonitorInstantiation monitor : monitorInstantiations) {
			JCheckBox check = new JCheckBox(monitor.getName());
			if (monitor.getConstraintType().equals("micro"))
				pCheckMic.add(check);
			else if (monitor.getConstraintType().equals("meso"))
				pCheckMes.add(check);
			else if (monitor.getConstraintType().equals("relational"))
				pCheckRel.add(check);
			checks.put(monitor, check);
		}
		pCheck.setLayout(new BoxLayout(pCheck, BoxLayout.X_AXIS));
		pCheck.add(pCheckMic);
		pCheck.add(Box.createHorizontalGlue());
		pCheck.add(pCheckMes);
		pCheck.add(Box.createHorizontalGlue());
		pCheck.add(pCheckRel);

		// a panel to choose the workspace
		JPanel pWorkspace = new JPanel();
		rbDataset = new JRadioButton("Complete dataset");
		rbWindow = new JRadioButton("In the current window");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbDataset);
		bg.add(rbWindow);
		rbDataset.setSelected(true);
		pWorkspace.add(rbDataset);
		pWorkspace.add(rbWindow);
		pWorkspace.setBorder(espacement);
		pWorkspace.setLayout(new BoxLayout(pWorkspace, BoxLayout.X_AXIS));

		// a panel for buttons
		JPanel panelBoutons = new JPanel();
		// OK button
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnOK.setActionCommand("ok");
		btnOK.setPreferredSize(new Dimension(100, 50));
		// a cancel button
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
		this.getContentPane().add(pWorkspace);
		this.getContentPane().add(panelBoutons);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.pack();
	}

	private void instanciate() {
		// simple case where all constraints have been selected
		if (chkToutes.isSelected() && rbDataset.isSelected()) {
			try {
				for (MonitorInstantiation monitor : this.monitorInstantiations) {
					Layer layer = this.application.getMainFrame().getSelectedProjectFrame()
							.getLayer(monitor.getLayerName());
					for (IFeature feat : layer.getFeatureCollection()) {
						monitors.add(monitor.instantiateOnObject((IGeneObj) feat));
					}
				}
			} catch (SecurityException | IllegalArgumentException e) {
				e.printStackTrace();
			}
			return;
		}

		// general case with a complete dataset workspace
		if (rbDataset.isSelected()) {
			Set<MonitorInstantiation> selected = new HashSet<MonitorInstantiation>();
			for (MonitorInstantiation elem : checks.keySet())
				if (checks.get(elem).isSelected())
					selected.add(elem);
			try {
				for (MonitorInstantiation monitor : selected) {
					Layer layer = this.application.getMainFrame().getSelectedProjectFrame()
							.getLayer(monitor.getLayerName());
					for (IFeature feat : layer.getFeatureCollection()) {
						monitors.add(monitor.instantiateOnObject((IGeneObj) feat));
					}
				}
			} catch (SecurityException | IllegalArgumentException e) {
				e.printStackTrace();
			}
			return;
		}

		// compute the window geometry

		IEnvelope geom = application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getViewport()
				.getEnvelopeInModelCoordinates();
		if (chkToutes.isSelected())
			try {
				for (MonitorInstantiation monitor : this.monitorInstantiations) {
					Layer layer = this.application.getMainFrame().getSelectedProjectFrame()
							.getLayer(monitor.getLayerName());
					for (IFeature feat : layer.getFeatureCollection()) {
						if (geom.getGeom().intersects(feat.getGeom()))
							monitors.add(monitor.instantiateOnObject((IGeneObj) feat));
					}
				}
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		else {
			Set<MonitorInstantiation> selected = new HashSet<MonitorInstantiation>();
			for (MonitorInstantiation elem : checks.keySet())
				if (checks.get(elem).isSelected())
					selected.add(elem);
			try {
				for (MonitorInstantiation monitor : this.monitorInstantiations) {
					Layer layer = this.application.getMainFrame().getSelectedProjectFrame()
							.getLayer(monitor.getLayerName());
					for (IFeature feat : layer.getFeatureCollection()) {
						if (geom.getGeom().intersects(feat.getGeom()))
							monitors.add(monitor.instantiateOnObject((IGeneObj) feat));
					}
				}
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
