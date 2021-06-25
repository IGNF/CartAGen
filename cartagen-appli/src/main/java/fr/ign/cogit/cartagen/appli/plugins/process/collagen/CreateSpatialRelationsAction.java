package fr.ign.cogit.cartagen.appli.plugins.process.collagen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.xml.bind.JAXBException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import fr.ign.cogit.cartagen.appli.utilities.filters.CSVFileFilter;
import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.RelationsDetection;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class CreateSpatialRelationsAction extends AbstractAction {

	private GeOxygeneApplication application;

	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new CSVFileFilter());
		fc.setCurrentDirectory(new File("src/main/resources"));
		int returnVal = fc.showDialog(null, "Open a file listing relations");
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File constraintFile = fc.getSelectedFile();

		Reader reader;
		List<ParsedSpatialRelation> spatialRelations = new ArrayList<>();

		try {
			reader = new FileReader(constraintFile);
			CSVParser parser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).build();
			CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
			String[] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				if ("name".equals(nextRecord[0]))
					continue;
				spatialRelations
						.add(new ParsedSpatialRelation(nextRecord[0], nextRecord[1], nextRecord[2], nextRecord[3]));
			}
			reader.close();
			csvReader.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (CsvValidationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (ParsedSpatialRelation psr : spatialRelations) {
			if (psr.getName().equals("Proximity2Networks")) {
				Set<String> layers = new HashSet<>();
				layers.add(psr.getLayer1());
				layers.add(psr.getLayer2());
				RelationsDetection.findProxiRelationsBetweenNetworks(Double.valueOf(psr.getParameter()), layers);

				Layer layer = pFrame.getSld().createLayer("Network2ProximityRelation", IPoint.class, Color.RED);
				StyledLayerDescriptor defaultSld;
				try {
					defaultSld = StyledLayerDescriptor.unmarshall(
							IGeneObj.class.getClassLoader().getResourceAsStream("sld/sld_spatial_relations.xml"));
					layer.getStyles().addAll(defaultSld.getLayer("Network2ProximityRelations").getStyles());
				} catch (JAXBException e1) {
					e1.printStackTrace();
				}
				IPopulation<IFeature> pop = new Population<>("Network2ProximityRelations");
				pop.addAll(CollaGenEnvironment.getInstance().getRelations());
				pFrame.getSld().getDataSet().addPopulation(pop);
				pFrame.getSld().add(layer);
			} else {
				// TODO
			}
		}
	}

	public CreateSpatialRelationsAction(GeOxygeneApplication application) {
		super();
		this.application = application;
		this.putValue(Action.SHORT_DESCRIPTION, "Create spatial relation objects and display them in the map");
		this.putValue(Action.NAME, "Create Spatial Relation Objects");
	}

	class ParsedSpatialRelation {
		private String name, layer1, layer2, parameter;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLayer1() {
			return layer1;
		}

		public void setLayer1(String layer1) {
			this.layer1 = layer1;
		}

		public String getLayer2() {
			return layer2;
		}

		public void setLayer2(String layer2) {
			this.layer2 = layer2;
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		public ParsedSpatialRelation(String name, String layer1, String layer2, String parameter) {
			super();
			this.name = name;
			this.layer1 = layer1;
			this.layer2 = layer2;
			this.parameter = parameter;
		}
	}
}
