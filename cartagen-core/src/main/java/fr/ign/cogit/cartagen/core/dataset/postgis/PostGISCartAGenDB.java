package fr.ign.cogit.cartagen.core.dataset.postgis;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.GeographicClass;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;

public class PostGISCartAGenDB extends CartAGenDB {

  private PostGISLoader loader;

  public PostGISCartAGenDB(File file, PostGISLoader loader)
      throws ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
    super(file);
    this.setLoader(loader);
  }

  public PostGISCartAGenDB(String name) {
    this.setName(name);
  }

  public PostGISCartAGenDB(File file) throws ClassNotFoundException,
      ParserConfigurationException, SAXException, IOException {
    super();
    openFromXml(file);
  }

  @Override
  public void openFromXml(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public void saveToXml(File file) throws IOException, TransformerException {
    // TODO Auto-generated method stub

  }

  @Override
  public void populateDataset(int scale) {

    for (GeographicClass layer : this.getClasses()) {
      System.out.println("\nJe charge : " + layer);
      loader.loadData(this.getDataSet(), layer.getName(), true);
    }

    // now build the dataset networks from the loaded data
    INetwork roadNet = this.getDataSet().getRoadNetwork();
    for (IRoadLine road : this.getDataSet().getRoads()) {
      roadNet.addSection(road);
    }
    INetwork railNet = this.getDataSet().getRailwayNetwork();
    for (IRailwayLine rail : this.getDataSet().getRailwayLines()) {
      railNet.addSection(rail);
    }
    INetwork waterNet = this.getDataSet().getHydroNetwork();
    for (IWaterLine water : this.getDataSet().getWaterLines()) {
      waterNet.addSection(water);
    }
  }

  @Override
  protected void load(GeographicClass geoClass, int scale) {
    // unused for now
  }

  @Override
  public void overwrite(GeographicClass geoClass) {
    loader.loadData(this.getDataSet(), geoClass.getName(), true);

    if (geoClass.getName().equals(CartAGenDataSet.ROADS_POP)) {
      INetwork roadNet = this.getDataSet().getRoadNetwork();
      for (IRoadLine road : this.getDataSet().getRoads()) {
        roadNet.addSection(road);
      }
    }
    if (geoClass.getName().equals(CartAGenDataSet.RAILWAY_LINES_POP)) {
      INetwork railNet = this.getDataSet().getRailwayNetwork();
      for (IRailwayLine rail : this.getDataSet().getRailwayLines()) {
        railNet.addSection(rail);
      }
    }
    if (geoClass.getName().equals(CartAGenDataSet.WATER_LINES_POP)) {
      INetwork waterNet = this.getDataSet().getHydroNetwork();
      for (IWaterLine water : this.getDataSet().getWaterLines()) {
        waterNet.addSection(water);
      }
    }
  }

  @Override
  protected void triggerEnrichments() {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCartagenId() {
    // TODO Auto-generated method stub

  }

  public PostGISLoader getLoader() {
    return loader;
  }

  public void setLoader(PostGISLoader loader) {
    this.loader = loader;
  }

}
