package fr.ign.cogit.cartagen.tactilemaps.monitors;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class MonitorFileParser {

	private List<MonitorInstantiation> monitors;

	@SuppressWarnings("resource")
	public MonitorFileParser(File file) throws IOException, ClassNotFoundException, CsvValidationException {
		this.monitors = new ArrayList<>();

		Reader reader = new FileReader(file);
		CSVParser parser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).build();
		CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

		String[] nextRecord;
		while ((nextRecord = csvReader.readNext()) != null) {
			if ("name".equals(nextRecord[0]))
				continue;
			this.monitors.add(new MonitorInstantiation(nextRecord[0], nextRecord[1], nextRecord[2], nextRecord[3],
					nextRecord[4]));
		}
		reader.close();
		csvReader.close();
	}

	public List<MonitorInstantiation> getMonitors() {
		return monitors;
	}
}
