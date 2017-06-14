/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.postgis;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostGISToLayerMapping.PostGISToLayerMatching;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;

public class PostGISMappingXMLParser {

  private File xmlFile;

  public PostGISMappingXMLParser(File xmlFile) {
    super();
    this.xmlFile = xmlFile;
  }

  /**
   * Parse the XML File to produce a {@link PostGISToLayerMapping} instance, if
   * the file has the correct structure.
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public PostGISToLayerMapping parsePostGISMapping()
      throws ParserConfigurationException, SAXException, IOException {

    try {

      // open the xml file
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc;
      doc = db.parse(this.xmlFile);
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
      PostGISToLayerMapping mapping = new PostGISToLayerMapping(implementation);
      Set<PostGISToLayerMatching> calacMatchings = mapping.getMatchings();

      // for every matching
      for (int itMatchings = 0; itMatchings < root
          .getElementsByTagName("matching").getLength(); itMatchings++) {
        Element matching = (Element) root.getElementsByTagName("matching")
            .item(itMatchings);

        // get matching properties
        String postgisLayer = matching.getElementsByTagName("postgisLayer")
            .item(0).getTextContent();
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
            String postgisAttr = attribute.getElementsByTagName("postgisAttr")
                .item(0).getTextContent();
            String javaAttr = attribute.getElementsByTagName("javaAttr").item(0)
                .getTextContent();
            attrMappingStorage.put(javaAttr, postgisAttr);
          }
        }

        // create the corresponding CalacMatching
        PostGISToLayerMatching calacMatching = mapping.new PostGISToLayerMatching(
            postgisLayer, factory.getClass().getDeclaredMethod(creationMethod),
            scaleRef, theme, attrMappingStorage);
        // add it to CalacMatchings
        calacMatchings.add(calacMatching);
      }
      return mapping;

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

    return null;
  }

}
