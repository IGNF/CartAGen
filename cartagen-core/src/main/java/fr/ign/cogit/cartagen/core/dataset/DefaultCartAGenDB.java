package fr.ign.cogit.cartagen.core.dataset;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * A default implementation of the CartAGen DB
 * 
 * @author GTouya
 *
 */
public class DefaultCartAGenDB extends CartAGenDB {

    public DefaultCartAGenDB(File file) throws ParserConfigurationException,
            SAXException, IOException, ClassNotFoundException {
        super(file);
    }

    public DefaultCartAGenDB(String name) {
        this.setName(name);
        this.setPersistentClasses(new HashSet<Class<?>>());
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
        // TODO Auto-generated method stub

    }

    @Override
    protected void load(GeographicClass geoClass, int scale) {
        // TODO Auto-generated method stub

    }

    @Override
    public void overwrite(GeographicClass geoClass) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void triggerEnrichments() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addCartagenId() {
        // TODO Auto-generated method stub

    }

}
