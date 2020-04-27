package fr.ign.cogit.cartagen.core.dataset.postgis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.core.dataset.GeographicClass;
import fr.ign.cogit.cartagen.core.dataset.SourceDLM;
import fr.ign.cogit.cartagen.core.dataset.shapefile.ShapeFileClass;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class PostGISCartAGenDB extends CartAGenDB {

    private static Logger logger = Logger
            .getLogger(PostGISCartAGenDB.class.getName());
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

        setClasses(new ArrayList<GeographicClass>());
        // first open the XML document in order to parse it
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        Document doc;
        doc = db.parse(file);
        doc.getDocumentElement().normalize();

        // then read the document to fill the fields
        Element root = (Element) doc.getElementsByTagName("cartagen-dataset")
                .item(0);
        // The DataSet type
        Element typeElem = (Element) root.getElementsByTagName("type").item(0);
        String type = typeElem.getChildNodes().item(0).getNodeValue();
        if (!this.getClass().getName().equals(type)) {
            logger.warning(
                    "The file does not correspond to a PostGIS Dataset !");
            return;
        }
        // get the postgis database information
        String host = typeElem.getAttribute("host");
        String port = typeElem.getAttribute("port");
        String dbName = typeElem.getAttribute("dbName");
        String user = typeElem.getAttribute("user");
        String password = typeElem.getAttribute("password");
        String schema = typeElem.getAttribute("schema");

        // The DataSet name
        Element nameElem = (Element) root.getElementsByTagName("name").item(0);
        this.setName(nameElem.getChildNodes().item(0).getNodeValue());

        // The DataSet symbolisation scale
        Element scaleElem = (Element) root.getElementsByTagName("scale")
                .item(0);
        this.setSymboScale(Integer
                .valueOf(scaleElem.getChildNodes().item(0).getNodeValue()));

        // the source DLM
        Element sourceElem = (Element) root.getElementsByTagName("source-dlm")
                .item(0);
        if (sourceElem != null) {
            SourceDLM source = SourceDLM
                    .valueOf(sourceElem.getChildNodes().item(0).getNodeValue());
            this.setSourceDLM(source);
        }

        // the list of classes
        Element classesElem = (Element) root
                .getElementsByTagName("classes-list").item(0);
        for (int i = 0; i < classesElem.getElementsByTagName("class")
                .getLength(); i++) {
            Element classElem = (Element) classesElem
                    .getElementsByTagName("class").item(i);
            Element classNameElem = (Element) classElem
                    .getElementsByTagName("name").item(0);
            String className = classNameElem.getChildNodes().item(0)
                    .getNodeValue();
            Element popElem = (Element) classElem
                    .getElementsByTagName("feature-type").item(0);
            String featureType = popElem.getChildNodes().item(0).getNodeValue();
            Class<? extends IGeometry> geometryType = IGeometry.class;
            if (classElem.getElementsByTagName("geometry-type")
                    .getLength() != 0) {
                // TODO
            }
            this.addClass(
                    new PostGISClass(className, featureType, geometryType));
        }

        // The mapping file
        Element mappingElem = (Element) root
                .getElementsByTagName("mapping-file").item(0);
        String mappingFile = mappingElem.getChildNodes().item(0).getNodeValue();

        // the GeneObjImplementation
        Element implElem = (Element) root
                .getElementsByTagName("geneobj-implementation").item(0);
        Element implNameElem = (Element) implElem
                .getElementsByTagName("implementation-name").item(0);
        String implName = implNameElem.getChildNodes().item(0).getNodeValue();
        Element implPackElem = (Element) implElem
                .getElementsByTagName("implementation-package").item(0);
        String packName = implPackElem.getChildNodes().item(0).getNodeValue();
        Package rootPackage = Package.getPackage(packName);
        Element implClassElem = (Element) implElem
                .getElementsByTagName("implementation-root-class").item(0);
        String className = implClassElem.getChildNodes().item(0).getNodeValue();
        Class<?> rootClass = Class.forName(className);
        Element factClassElem = (Element) implElem
                .getElementsByTagName("implementation-factory").item(0);
        String factClassName = factClassElem.getChildNodes().item(0)
                .getNodeValue();
        Class<?> factClass = Class.forName(factClassName);
        try {
            this.setGeneObjImpl(new GeneObjImplementation(implName, rootPackage,
                    rootClass, (AbstractCreationFactory) factClass
                            .getConstructor().newInstance()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // the persistent classes
        Element persistElem = (Element) root.getElementsByTagName("persistent")
                .item(0);
        this.setPersistent(Boolean
                .valueOf(persistElem.getChildNodes().item(0).getNodeValue()));
        this.setPersistentClasses(new HashSet<Class<?>>());
        Element persistClassesElem = (Element) root
                .getElementsByTagName("persistent-classes").item(0);
        // get the class loader for the geoxygene-cartagen project
        ClassLoader loader = IGeneObj.class.getClassLoader();
        for (int i = 0; i < persistClassesElem
                .getElementsByTagName("persistent-class").getLength(); i++) {
            Element persistClassElem = (Element) persistClassesElem
                    .getElementsByTagName("persistent-class").item(i);
            String className1 = persistClassElem.getChildNodes().item(0)
                    .getNodeValue();
            this.getPersistentClasses()
                    .add(Class.forName(className1, true, loader));
        }
        this.setPersistentClasses(this.getGeneObjImpl()
                .filterClasses(this.getPersistentClasses()));

        this.setXmlFile(file);
        PostGISToLayerMapping mapping = null;
        MappingXMLParser parser = new MappingXMLParser(new File(mappingFile));
        mapping = parser.parsePostGISMapping();

        this.loader = new PostGISLoader(host, port, dbName, user, password,
                schema);
        this.loader.setMapping(mapping);

    }

    @Override
    public void saveToXml(File file) throws IOException, TransformerException {
        Node n = null;
        // ********************************************
        // CREATION DU DOCUMENT XML
        // Document (Xerces implementation only).
        DocumentImpl xmlDoc = new DocumentImpl();
        // Root element.
        Element root = xmlDoc.createElement("cartagen-dataset");
        // The DataSet name
        Element nameElem = xmlDoc.createElement("name");
        n = xmlDoc.createTextNode(this.getName());
        nameElem.appendChild(n);
        root.appendChild(nameElem);

        // The DataSet type
        Element typeElem = xmlDoc.createElement("type");
        n = xmlDoc.createTextNode(this.getClass().getName());
        typeElem.appendChild(n);
        typeElem.setAttribute("host", loader.getHost());
        typeElem.setAttribute("port", loader.getPort());
        typeElem.setAttribute("password", loader.getPassword());
        typeElem.setAttribute("dbName", loader.getDbName());
        typeElem.setAttribute("user", loader.getUser());
        typeElem.setAttribute("schema", loader.getSchema());
        root.appendChild(typeElem);
        // The DataSet type
        Element datasetTypeElem = xmlDoc.createElement("dataset-type");
        n = xmlDoc.createTextNode(this.getDataSet().getClass().getName());
        datasetTypeElem.appendChild(n);
        root.appendChild(datasetTypeElem);
        // The symbolisation scale
        Element scaleElem = xmlDoc.createElement("scale");
        n = xmlDoc.createTextNode(String.valueOf(this.getSymboScale()));
        scaleElem.appendChild(n);
        root.appendChild(scaleElem);

        // The source DLM
        Element dlmElem = xmlDoc.createElement("source-dlm");
        n = xmlDoc.createTextNode(this.getSourceDLM().name());
        dlmElem.appendChild(n);
        root.appendChild(dlmElem);

        // the list of classes
        Element classesElem = xmlDoc.createElement("classes-list");
        for (GeographicClass c : this.getClasses()) {
            Element classeElem = xmlDoc.createElement("class");
            // the class path
            Element classPathElem = xmlDoc.createElement("path");
            n = xmlDoc.createTextNode(((ShapeFileClass) c).getPath());
            classPathElem.appendChild(n);
            classeElem.appendChild(classPathElem);
            // the population name
            Element popNameElem = xmlDoc.createElement("feature-type");
            n = xmlDoc.createTextNode(c.getFeatureTypeName());
            popNameElem.appendChild(n);
            classeElem.appendChild(popNameElem);
            classesElem.appendChild(classeElem);
        }
        root.appendChild(classesElem);

        // the GeneObj implementation
        Element implElem = xmlDoc.createElement("geneobj-implementation");
        root.appendChild(implElem);
        Element implNameElem = xmlDoc.createElement("implementation-name");
        n = xmlDoc.createTextNode(this.getGeneObjImpl().getName());
        implNameElem.appendChild(n);
        implElem.appendChild(implNameElem);
        Element implPackElem = xmlDoc.createElement("implementation-package");
        n = xmlDoc.createTextNode(
                this.getGeneObjImpl().getRootPackage().getName());
        implPackElem.appendChild(n);
        implElem.appendChild(implPackElem);
        Element implClassElem = xmlDoc
                .createElement("implementation-root-class");
        n = xmlDoc
                .createTextNode(this.getGeneObjImpl().getRootClass().getName());
        implClassElem.appendChild(n);
        implElem.appendChild(implClassElem);
        Element factClassElem = xmlDoc.createElement("implementation-factory");
        n = xmlDoc.createTextNode(this.getGeneObjImpl().getCreationFactory()
                .getClass().getName());
        factClassElem.appendChild(n);
        implElem.appendChild(factClassElem);

        // the persistent classes
        Element persistElem = xmlDoc.createElement("persistent");
        n = xmlDoc.createTextNode(String.valueOf(this.isPersistent()));
        persistElem.appendChild(n);
        root.appendChild(persistElem);
        Element persistClassesElem = xmlDoc.createElement("persistent-classes");
        root.appendChild(persistClassesElem);
        for (Class<?> classObj : this.getPersistentClasses()) {
            Element persistClassElem = xmlDoc.createElement("persistent-class");
            n = xmlDoc.createTextNode(classObj.getName());
            persistClassElem.appendChild(n);
            persistClassesElem.appendChild(persistClassElem);
        }

        // ECRITURE DU FICHIER
        xmlDoc.appendChild(root);
        XMLUtil.writeDocumentToXml(xmlDoc, file);

    }

    @Override
    public void populateDataset(int scale) {

        for (GeographicClass layer : this.getClasses()) {
            System.out.println("\nJe charge : " + layer.getName());
            loader.loadData(this.getDataSet(), layer.getName(), false);
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
