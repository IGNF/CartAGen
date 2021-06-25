package fr.ign.cogit.cartagen.appli.plugins.tactilemaps;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import com.opencsv.exceptions.CsvValidationException;

import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.utilities.filters.CSVFileFilter;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.cartagen.evaluation.global.ConstraintSatisfactionDistribution;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.style.Layer;

public class TactileMapsComponent extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TactileMapsComponent(String title) {
		super(title);

		JMenu constraintsMenu = new JMenu("Constraints");
		constraintsMenu.add(new JMenuItem(new ComputeMonitorsAction()));
		constraintsMenu.add(new JMenuItem(new UpdateMonitorsAction()));
		constraintsMenu.add(new JMenuItem(new ExportMonitorsAction()));
		JMenu algoMenu = new JMenu("Algorithms");

		this.add(constraintsMenu);
		this.add(algoMenu);
	}

	class ComputeMonitorsAction extends AbstractAction {

		/****/
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// get the map as an image
			GeOxygeneApplication application = CartAGenPlugin.getInstance().getApplication();

			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new CSVFileFilter());
			fc.setCurrentDirectory(new File("src/main/resources"));
			int returnVal = fc.showDialog(null, "Open a constraint list file");
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File constraintFile = fc.getSelectedFile();

			// compute the constraint monitors
			try {
				ComputeTactileMonitorsFrame frame = new ComputeTactileMonitorsFrame(application, constraintFile);
				frame.setVisible(true);
			} catch (DOMException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (CsvValidationException e1) {
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

			GeOxygeneApplication application = CartAGenPlugin.getInstance().getApplication();
			Layer monitorLayer = application.getMainFrame().getSelectedProjectFrame().getLayer("constraintMonitors");
			if (monitorLayer != null) {
				Map<SpecificationMonitor, ConstraintSatisfaction> distribution = new HashMap<>();
				for (IFeature feat : monitorLayer.getFeatureCollection()) {
					ConstraintMonitor monitor = (ConstraintMonitor) feat;
					distribution.put(monitor, monitor.getSatisfaction());
				}
				ConstraintSatisfactionDistribution distrib = new ConstraintSatisfactionDistribution(
						CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB().getName(), distribution);
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
			this.putValue(Action.NAME, "Export the constraints monitors of the map in XML");
		}
	}

	class UpdateMonitorsAction extends AbstractAction {

		/****/
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			GeOxygeneApplication application = CartAGenPlugin.getInstance().getApplication();
			Layer monitorLayer = application.getMainFrame().getSelectedProjectFrame().getLayer("constraintMonitors");
			if (monitorLayer != null) {
				for (IFeature feat : monitorLayer.getFeatureCollection()) {
					SpecificationMonitor monitor = (SpecificationMonitor) feat;
					monitor.computeSatisfaction();
				}
			}
		}

		public UpdateMonitorsAction() {
			super();
			this.putValue(Action.NAME, "Update the constraints monitors satisfaction");
		}
	}
}
