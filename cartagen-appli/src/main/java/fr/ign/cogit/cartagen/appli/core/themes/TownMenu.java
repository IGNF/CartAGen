/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.opencsv.CSVWriter;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.appli.utilities.ProgressFrame;
import fr.ign.cogit.cartagen.core.GeneralisationLegend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.defaultschema.urban.Town;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BlockDensityCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BlockSizeCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BuildingSizeCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.CentroidCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.LimitCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.NeighbourDensityCriterion;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayerFactory;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class TownMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenuItem mVilleCreer = new JMenuItem(new CreateTownAction());
	private JMenuItem mVilleCreerPart = new JMenuItem(new CreateTownPartitionAction());
	private JMenuItem mTownLinks = new JMenuItem(new TownLinksAction());
	public JCheckBoxMenuItem mIdVilleVoir = new JCheckBoxMenuItem("See id");
	public JCheckBoxMenuItem mVoirAireVille = new JCheckBoxMenuItem("See town area");
	private GeOxygeneApplication application;

	public TownMenu(String title, GeOxygeneApplication application) {
		super(title);

		this.application = application;
		this.add(this.mVilleCreer);
		this.add(this.mVilleCreerPart);
		this.add(this.mTownLinks);

		this.addSeparator();

		this.add(this.mIdVilleVoir);
		this.add(this.mVoirAireVille);

		this.addSeparator();
		this.add(new JMenuItem(new PrintElectreBlockAction()));
		this.add(new JMenuItem(new ExportElectreBlockAction()));
		this.add(new JMenuItem(new IsTownCentreAction()));
	}

	public GeOxygeneApplication getApplication() {
		return application;
	}

	public void setApplication(GeOxygeneApplication application) {
		this.application = application;
	}

	private class CreateTownAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
					ProgressFrame progressFrame = new ProgressFrame("Enrichement in progress...", true);
					progressFrame.setVisible(true);
					progressFrame.setTextAndValue("Urban enrichment in progress", 0);
					UrbanEnrichment.buildTowns(dataset, false,
							dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory());
					progressFrame.setTextAndValue("Urban enrichment in progress", 100);
					progressFrame.setVisible(false);
					progressFrame = null;

					ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();
					FeatureType ft = new FeatureType();
					ft.setNomClasse(CartAGenDataSet.BLOCKS_POP);
					ft.setGeometryType(IPolygon.class);
					dataset.getBlocks().setFeatureType(ft);
					FeatureType ft2 = new FeatureType();
					ft2.setNomClasse(CartAGenDataSet.TOWNS_POP);
					ft2.setGeometryType(IPolygon.class);
					dataset.getTowns().setFeatureType(ft2);

					NamedLayerFactory factory = new NamedLayerFactory();
					factory.setModel(frame.getSld());
					factory.setName(CartAGenDataSet.BLOCKS_POP);

					factory.setGeometryType(IPolygon.class);
					Layer blockLayer = factory.createLayer();
					factory.setName(CartAGenDataSet.TOWNS_POP);
					Layer townLayer = factory.createLayer();
					frame.getSld().add(blockLayer);
					frame.getSld().add(townLayer);
				}
			});
			th.start();
		}

		public CreateTownAction() {
			this.putValue(Action.SHORT_DESCRIPTION, "Create town agent with buffers around buildings (Boffet, 2001)");
			this.putValue(Action.NAME, "Create town with buildings");
		}
	}

	private class CreateTownPartitionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
					ProgressFrame progressFrame = new ProgressFrame("Enrichement in progress...", true);
					progressFrame.setVisible(true);
					progressFrame.setTextAndValue("Urban enrichment in progress", 0);
					UrbanEnrichment.buildTownsPartition(dataset, 4, false, 2,
							dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory());
					progressFrame.setTextAndValue("Urban enrichment in progress", 100);
					progressFrame.setVisible(false);
					progressFrame = null;

					ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();
					FeatureType ft = new FeatureType();
					ft.setNomClasse(CartAGenDataSet.BLOCKS_POP);
					ft.setGeometryType(IPolygon.class);
					dataset.getBlocks().setFeatureType(ft);
					FeatureType ft2 = new FeatureType();
					ft2.setNomClasse(CartAGenDataSet.TOWNS_POP);
					ft2.setGeometryType(IPolygon.class);
					dataset.getTowns().setFeatureType(ft2);

					NamedLayerFactory factory = new NamedLayerFactory();
					factory.setModel(frame.getSld());
					factory.setName(CartAGenDataSet.BLOCKS_POP);

					factory.setGeometryType(IPolygon.class);
					Layer blockLayer = factory.createLayer();
					factory.setName(CartAGenDataSet.TOWNS_POP);
					Layer townLayer = factory.createLayer();
					frame.getSld().add(blockLayer);
					frame.getSld().add(townLayer);
				}
			});
			th.start();
		}

		public CreateTownPartitionAction() {
			this.putValue(Action.SHORT_DESCRIPTION,
					"Create town agent with buffers around buildings (Boffet, 2001), with a quad tree partition of the dataset");
			this.putValue(Action.NAME, "Create town (method for large datasets)");
		}
	}

	private class TownLinksAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// first build the town/block link
			for (ITown town : CartAGenDoc.getInstance().getCurrentDataset().getTowns()) {
				IPolygon geom = town.getGeom();
				Collection<IUrbanBlock> blocks = CartAGenDoc.getInstance().getCurrentDataset().getBlocks().select(geom);
				town.setTownBlocks(new FT_FeatureCollection<IUrbanBlock>(blocks));
			}
		}

		public TownLinksAction() {
			this.putValue(Action.SHORT_DESCRIPTION, I18N.getString("TownMenu.ttipTownLinks"));
			this.putValue(Action.NAME, I18N.getString("TownMenu.lblTownLinks"));
		}
	}

	private class IsTownCentreAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool();
			pool.setSld(
					CartAGenPlugin.getInstance().getApplication().getMainFrame().getSelectedProjectFrame().getSld());
			for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
				if (!(sel instanceof IUrbanBlock)) {
					continue;
				}
				IUrbanBlock block = (IUrbanBlock) sel;
				ITown town = block.getTown();
				if (town == null) {
					System.out.println("null");
					continue;
				}
				boolean townCentre = town.isTownCentre(block);
				if (townCentre) {
					System.out.println(block.toString() + " is town centre");
					pool.addFeatureToGeometryPool(block.getGeom(), GeneralisationLegend.ILOTS_GRISES_COULEUR, 4);
				} else {
					System.out.println(block.toString() + " is not town centre");
				}
			}
		}

		public IsTownCentreAction() {
			this.putValue(Action.SHORT_DESCRIPTION,
					"Trigger the multicriteria decision technique to check if a block is part of town centre");
			this.putValue(Action.NAME, "Is block a town centre?");
		}
	}

	/**
	 * Show with a specific style the block classification results with ELECTRE TRI
	 * method.
	 * 
	 * @author GTouya
	 * 
	 */
	class PrintElectreBlockAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			IFeature feat = SelectionUtil.getFirstSelectedObject(application);

			if (((IUrbanBlock) feat).getTown() == null) {
				System.out.println("no town");
				return;
			}

			ITown town = ((IUrbanBlock) feat).getTown();

			// ******************** compute ELECTRE classification
			// compute the statistics on the town blocks
			if (((Town) town).getMeanBlockArea() == null) {
				((Town) town).computeTownStats();
			}

			// build the decision method and its criteria
			RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
			Set<Criterion> criteria = new HashSet<Criterion>();

			criteria.add(new BlockDensityCriterion("Density"));
			criteria.add(new BlockSizeCriterion("Area"));
			criteria.add(new CentroidCriterion("Centroid"));
			criteria.add(new BuildingSizeCriterion("BuildingArea"));
			criteria.add(new LimitCriterion("Limit"));
			criteria.add(new NeighbourDensityCriterion("Neighbour"));
			ConclusionIntervals conclusion = initCentreConclusion(criteria);
			electre.setCriteriaParamsFromCriteria(criteria);

			// make the decision
			Map<String, Double> valeursCourantes = new HashMap<String, Double>();
			for (Criterion crit : criteria) {
				Map<String, Object> param = getCentreParamMap(((IUrbanBlock) feat), crit, (Town) town);
				valeursCourantes.put(crit.getName(), Double.valueOf(crit.value(param)));
			}
			String res = electre.decision(criteria, valeursCourantes, conclusion).getCategory();

			int electreClass = 0;
			if ("very good".equals(res))
				electreClass = 4;
			else if ("good".equals(res))
				electreClass = 3;
			else if ("average".equals(res))
				electreClass = 2;
			else if ("bad".equals(res))
				electreClass = 1;
			System.out.println(valeursCourantes);
			System.out.println("ELECTRE class: " + electreClass);

		}

		public PrintElectreBlockAction() {
			this.putValue(Action.NAME, "Print Block classification and values");
		}
	}

	/**
	 * Export the block classification results with ELECTRE TRI method, into a CSV
	 * file with WKT geometries.
	 * 
	 * @author GTouya
	 * 
	 */
	class ExportElectreBlockAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(CartAGenPlugin.getInstance().getApplication().getMainFrame().getGui());
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File file = fc.getSelectedFile();
			// create FileWriter object with file as parameter
			FileWriter outputfile;
			try {
				outputfile = new FileWriter(file);

				// create CSVWriter object filewriter object as parameter
				CSVWriter writer = new CSVWriter(outputfile);
				// adding header to csv
				String[] header = { "Geometry", "Class" };
				writer.writeNext(header);

				for (IFeature town : SelectionUtil.getSelectedObjects(application)) {

					// ******************** compute ELECTRE classification
					// compute the statistics on the town blocks
					if (((Town) town).getMeanBlockArea() == null) {
						((Town) town).computeTownStats();
					}

					// build the decision method and its criteria
					RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
					Set<Criterion> criteria = new HashSet<Criterion>();

					criteria.add(new BlockDensityCriterion("Density"));
					criteria.add(new BlockSizeCriterion("Area"));
					criteria.add(new CentroidCriterion("Centroid"));
					criteria.add(new BuildingSizeCriterion("BuildingArea"));
					criteria.add(new LimitCriterion("Limit"));
					criteria.add(new NeighbourDensityCriterion("Neighbour"));
					ConclusionIntervals conclusion = initCentreConclusion(criteria);
					electre.setCriteriaParamsFromCriteria(criteria);

					for (IUrbanBlock block : ((ITown) town).getTownBlocks()) {

						// make the decision
						Map<String, Double> valeursCourantes = new HashMap<String, Double>();
						for (Criterion crit : criteria) {
							Map<String, Object> param = getCentreParamMap(block, crit, (Town) town);
							valeursCourantes.put(crit.getName(), Double.valueOf(crit.value(param)));
						}
						String res = electre.decision(criteria, valeursCourantes, conclusion).getCategory();

						int electreClass = 0;
						if ("very good".equals(res))
							electreClass = 4;
						else if ("good".equals(res))
							electreClass = 3;
						else if ("average".equals(res))
							electreClass = 2;
						else if ("bad".equals(res))
							electreClass = 1;
						System.out.println(valeursCourantes);
						System.out.println("ELECTRE class: " + electreClass);
						String[] data = { block.getGeom().toString(), String.valueOf(electreClass) };
						writer.writeNext(data);
					}
				}

				// closing writer connection
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public ExportElectreBlockAction() {
			this.putValue(Action.NAME, "Export Block classification in CSV");
		}
	}

	/**
	 * Initialisation of the block classification output classes.
	 * 
	 * @param criteria
	 * @return
	 */
	private ConclusionIntervals initCentreConclusion(Set<Criterion> criteria) {
		ConclusionIntervals conclusion = new ConclusionIntervals(criteria);
		Map<String, Double> borneSupTB = new Hashtable<String, Double>();
		Map<String, Double> borneInfTB = new Hashtable<String, Double>();
		Map<String, Double> borneInfB = new Hashtable<String, Double>();
		Map<String, Double> borneInfMy = new Hashtable<String, Double>();
		Map<String, Double> borneInfMv = new Hashtable<String, Double>();
		Map<String, Double> borneInfTMv = new Hashtable<String, Double>();

		Iterator<Criterion> itc = criteria.iterator();
		while (itc.hasNext()) {
			Criterion ct = itc.next();
			borneSupTB.put(ct.getName(), Double.valueOf(1));
			borneInfTB.put(ct.getName(), Double.valueOf(0.6));
			borneInfB.put(ct.getName(), Double.valueOf(0.35));
			borneInfMy.put(ct.getName(), Double.valueOf(0.25));
			borneInfMv.put(ct.getName(), Double.valueOf(0.15));
			borneInfTMv.put(ct.getName(), Double.valueOf(0));
		}
		conclusion.addInterval(borneInfTMv, borneInfMv, "very bad");
		conclusion.addInterval(borneInfMv, borneInfMy, "bad");
		conclusion.addInterval(borneInfMy, borneInfB, "average");
		conclusion.addInterval(borneInfB, borneInfTB, "good");
		conclusion.addInterval(borneInfTB, borneSupTB, "very good");
		return conclusion;
	}

	/**
	 * Initialisation of the block classification parameters.
	 * 
	 * @param block
	 * @param crit
	 * @param town
	 * @return
	 */
	private Map<String, Object> getCentreParamMap(IUrbanBlock block, Criterion crit, ITown town) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("block", block);
		param.put("buildingAreaStats", ((Town) town).getBuildAreaStats());
		if (crit.getName().equals("BuildingArea")) {
			param.put("meanBuildingArea", ((Town) town).getMeanBlockArea());
		} else if (crit.getName().equals("Centroid")) {
			IDirectPosition centrePos = ((Town) town).getCentre();
			if (centrePos == null)
				centrePos = town.getGeom().centroid();
			param.put("centroid", centrePos);
			Point pt;
			try {
				pt = CommonAlgorithms.getPointLePlusLoin((Point) JtsGeOxygene.makeJtsGeom(centrePos.toGM_Point()),
						(Polygon) JtsGeOxygene.makeJtsGeom(town.getGeom()));
				IDirectPosition dp = JtsGeOxygene.makeDirectPosition(pt.getCoordinateSequence());
				double maxDist = dp.distance2D(centrePos);
				param.put("max_dist", maxDist);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (crit.getName().equals("Area")) {
			param.put("meanBlockArea", ((Town) town).getMeanBlockArea());
		} else if (crit.getName().equals("Limit")) {
			param.put("outline", ((Town) town).getOutline());
		}
		return param;
	}

}
