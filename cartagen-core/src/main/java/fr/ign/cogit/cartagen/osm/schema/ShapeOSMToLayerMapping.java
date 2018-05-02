/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;

/**
 * Objects from this class are mappings between a shapefile data structure and
 * the CartAGen core data schema.
 * @author GTouya
 *
 */
public class ShapeOSMToLayerMapping {

  // ******************
  // Properties (ShapeToLayerMapping)
  // ******************
  public GeneObjImplementation implementation;
  public Set<ShapeOSMToLayerMatching> matchings;

  // ******************
  // Subclass ShapeToLayerMatching
  // ******************
  public class ShapeOSMToLayerMatching {

    // ******************
    // Properties (PostGISToLayerMatching)
    // ******************
    private String shapeLayer;
    private Method creationMethod;
    private String scale;
    private String theme;
    private Hashtable<String, String> listTags = new Hashtable<String, String>();
    private OsmTagFilter filter;

    // ******************
    // Constructor (PostGISToLayerMatching)
    // ******************
    public ShapeOSMToLayerMatching(String shapeLayer, Method creationMethod,
        String scale, String theme, Hashtable<String, String> listTags,
        OsmTagFilter filter) {
      super();
      this.setShapeLayer(shapeLayer);
      this.setCreationMethod(creationMethod);
      this.setScale(scale);
      this.setTheme(theme);
      this.setListTags(listTags);
      this.setFilter(filter);
    }

    // ******************
    // Getters (PostGISToLayerMatching)
    // ******************

    public String getShapeLayer() {
      return shapeLayer;
    }

    public Method getCreationMethod() {
      return creationMethod;
    }

    public String getScale() {
      return scale;
    }

    public String getTheme() {
      return theme;
    }

    public Hashtable<String, String> getListTags() {
      return this.listTags;
    }

    // ******************
    // Setters (PostGISToLayerMatching)
    // ******************

    public void setShapeLayer(String shapeLayer) {
      this.shapeLayer = shapeLayer;
    }

    public void setCreationMethod(Method creationMethod) {
      this.creationMethod = creationMethod;
    }

    public void setScale(String scale) {
      this.scale = scale;
    }

    public void setTheme(String theme) {
      this.theme = theme;
    }

    public void setListTags(Hashtable<String, String> listTags) {
      this.listTags = listTags;
    }

    public OsmTagFilter getFilter() {
      return filter;
    }

    public void setFilter(OsmTagFilter filter) {
      this.filter = filter;
    }

    @Override
    public String toString() {
      return "ShapeOSMToLayerMatching [shapeLayer=" + shapeLayer
          + ", creationMethod=" + creationMethod + ", listTags=" + listTags
          + "]";
    }

  }

  // ******************
  // Constructor (PostGISToLayerMapping)
  // ******************
  // Default constructor with default mapping

  /**
   * Constructor with an implementation. The mapping is empty.
   * @param implementation
   * @throws NoSuchMethodException
   */
  public ShapeOSMToLayerMapping(GeneObjImplementation implementation)
      throws NoSuchMethodException {
    this.implementation = implementation;
    this.matchings = new HashSet<>();
  }

  /**
   * Constructor with an XML file that contains the mapping. The file is parsed
   * to fill this mapping.
   * @param xmlFile
   * @throws NoSuchMethodException
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public ShapeOSMToLayerMapping(File xmlFile) throws NoSuchMethodException,
      ParserConfigurationException, SAXException, IOException {
    this.matchings = new HashSet<>();
    this.parseShapeMapping(xmlFile);
  }

  // ******************
  // Getters (PostGISToLayerMapping)
  // ******************

  public GeneObjImplementation getGeneObjImplementation() {
    return this.implementation;
  }

  public void setGeneObjImplementation(GeneObjImplementation impl) {
    this.implementation = impl;
  }

  public Method getCreationMethod(String shapeLayer) {
    Method creationMethod = null;
    for (ShapeOSMToLayerMatching matching : this.matchings) {
      if (matching.shapeLayer.equals(shapeLayer)) {
        creationMethod = matching.creationMethod;
        break;
      }
    }
    return creationMethod;
  }

  public Hashtable<String, String> getListAttr(String shapeLayer) {
    Hashtable<String, String> listAttr = null;
    for (ShapeOSMToLayerMatching matching : this.matchings) {
      if (matching.shapeLayer.equals(shapeLayer)) {
        listAttr = matching.listTags;
        break;
      }
    }
    return listAttr;
  }

  public Integer getSize() {
    return matchings.size();
  }

  public Set<ShapeOSMToLayerMatching> getMatchings() {
    return this.matchings;
  }

  /**
   * Parse the XML File to produce a {@link ShapeOSMToLayerMapping} instance, if
   * the file has the correct structure.
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public void parseShapeMapping(File xmlFile)
      throws ParserConfigurationException, SAXException, IOException {

    try {

      // open the xml file
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc;
      doc = db.parse(xmlFile);
      doc.getDocumentElement().normalize();

      // get the root of the XML document
      Element root = (Element) doc.getElementsByTagName("mapping").item(0);

      Element implElem = (Element) root
          .getElementsByTagName("gene-obj-implementation").item(0);
      AbstractCreationFactory factory = (AbstractCreationFactory) Class
          .forName(
              implElem.getElementsByTagName("factory").item(0).getTextContent())
          .newInstance();
      String name = implElem.getElementsByTagName("name").item(0)
          .getTextContent();
      String rootPackageName = implElem
          .getElementsByTagName("root-package-name").item(0).getTextContent();
      Class<?> rootClass = Class.forName(
          implElem.getElementsByTagName("root-class").item(0).getTextContent());
      // create a new CalacMapping
      GeneObjImplementation implementation = new GeneObjImplementation(name,
          rootPackageName, rootClass, factory);
      this.setGeneObjImplementation(implementation);

      // for every matching
      for (int itMatchings = 0; itMatchings < root
          .getElementsByTagName("matching").getLength(); itMatchings++) {
        Element matching = (Element) root.getElementsByTagName("matching")
            .item(itMatchings);

        // get matching properties
        String postgisLayer = matching.getElementsByTagName("shapeName").item(0)
            .getTextContent();
        String creationMethod = matching.getElementsByTagName("creationMethod")
            .item(0).getTextContent();
        String scaleRef = matching.getElementsByTagName("scaleRef").item(0)
            .getTextContent();
        String theme = matching.getElementsByTagName("theme").item(0)
            .getTextContent();

        // get matching attributes mapping
        Element attributes = (Element) matching
            .getElementsByTagName("attributes").item(0);
        Hashtable<String, String> attrMappingStorage = new Hashtable<String, String>();
        if (attributes != null) {
          // get and store each attribute mapping
          for (int itAttrMapping = 0; itAttrMapping < attributes
              .getElementsByTagName("attribute").getLength(); itAttrMapping++) {
            Element attribute = (Element) attributes
                .getElementsByTagName("attribute").item(itAttrMapping);
            String shapeAttr = attribute.getElementsByTagName("shapeAttr")
                .item(0).getTextContent();
            String key = attribute.getElementsByTagName("key").item(0)
                .getTextContent();
            attrMappingStorage.put(shapeAttr, key);
          }
        }

        // get the filters if any
        Set<String> filters = new HashSet<>();
        Element filter = (Element) matching.getElementsByTagName("filter")
            .item(0);
        OsmTagFilter tagFilter = null;
        if (filter != null) {
          String key = filter.getElementsByTagName("key").item(0)
              .getTextContent();
          for (int i = 0; i < filter.getElementsByTagName("value")
              .getLength(); i++) {
            String value = filter.getElementsByTagName("value").item(i)
                .getTextContent();
            filters.add(value);
          }
          tagFilter = new OsmTagFilter(key, filters);
        }

        // create the corresponding CalacMatching
        ShapeOSMToLayerMatching matchingObj = this.new ShapeOSMToLayerMatching(
            postgisLayer, factory.getClass().getDeclaredMethod(creationMethod),
            scaleRef, theme, attrMappingStorage, tagFilter);
        // add it to CalacMatchings
        this.getMatchings().add(matchingObj);
      }
      return;

    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (DOMException e) {
      e.printStackTrace();
    }

    return;
  }

  /**
   * Get the matching that matches the given file name.
   * @param file
   * @return
   */
  public ShapeOSMToLayerMatching getMatchingFromFile(File file) {
    String filename = file.getName().substring(0, file.getName().length() - 4);
    for (ShapeOSMToLayerMatching matching : getMatchings()) {
      if (matching.getShapeLayer().equals(filename))
        return matching;
    }
    return null;
  }

  public class OsmTagFilter {

    private String key;
    private Set<String> values;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Set<String> getValues() {
      return values;
    }

    public void setValues(Set<String> values) {
      this.values = values;
    }

    public OsmTagFilter(String key, Set<String> values) {
      super();
      this.key = key;
      this.values = values;
    }
  }
}
