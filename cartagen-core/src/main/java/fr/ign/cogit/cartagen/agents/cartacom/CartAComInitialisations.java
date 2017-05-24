/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;

/**
 * @author CDuchene
 * 
 */
public class CartAComInitialisations {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger
      .getLogger(CartAComInitialisations.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  /**
   * Loads the constraints from the XML specifications file
   */
  public static void loadConstraintsDescrFromXMLs(
      CartacomSpecifications cartacomSpecifications) {

    CartAComInitialisations.logger
        .debug(" file : " + CartAComInitialisations.class.getResource(
            CartacomSpecifications.ALL_CAC_CONSTRAINTS_DESCRIPTORS_XML_FILE));

    String constraintsDescriptorsFile = CartAComInitialisations.class
        .getResource(
            CartacomSpecifications.ALL_CAC_CONSTRAINTS_DESCRIPTORS_XML_FILE)
        .getFile();
    CartAComInitialisations.logger
        .debug("XML file for all constraints descriptor : "
            + constraintsDescriptorsFile);
    String constraintsToConsiderFile = CartAComInitialisations.class
        .getResource(
            CartacomSpecifications.CAC_CONSTRAINTS_TO_CONSIDER_XML_FILE)
        .getFile();
    CartAComInitialisations.logger.debug(
        "XML file for constraints to consider : " + constraintsToConsiderFile);

    // we create a SAXBuilder instance
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    // domFactory.setNamespaceAware(true); // never forget this!
    DocumentBuilder builder = null;
    try {
      builder = domFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      CartAComInitialisations.logger.error("Error when creating DOM builder.");
      e.printStackTrace();
    }

    if (builder != null) {
      Document constraintsDescriptorsDocument = null;

      try {
        constraintsDescriptorsDocument = builder
            .parse(constraintsDescriptorsFile);

      } catch (SAXException e) {
        CartAComInitialisations.logger
            .error("Error, invalide XML file: " + constraintsDescriptorsFile);
        e.printStackTrace();
      } catch (IOException e) {
        CartAComInitialisations.logger
            .error("Error when readin XML file: " + constraintsDescriptorsFile);
        e.printStackTrace();
      }

      Document constraintsToConsiderDocument = null;

      try {
        constraintsToConsiderDocument = builder
            .parse(constraintsToConsiderFile);

      } catch (SAXException e) {
        CartAComInitialisations.logger
            .error("Error, invalide XML file: " + constraintsToConsiderFile);
        e.printStackTrace();
      } catch (IOException e) {
        CartAComInitialisations.logger
            .error("Error when readin XML file: " + constraintsToConsiderFile);
        e.printStackTrace();
      }
      if (constraintsDescriptorsDocument != null
          && constraintsToConsiderDocument != null) {

        // Create factory to read xml document.
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        // Get all the nodes with relational-constraint label.
        NodeList relationalConstraintsList = null;
        try {
          XPathExpression expr = xpath.compile("//relational-constraint");
          // Get the node set
          relationalConstraintsList = ((NodeList) expr.evaluate(
              constraintsDescriptorsDocument, XPathConstants.NODESET));
        } catch (XPathExpressionException e) {
          e.printStackTrace();
        }

        // if the node doesn't exist, return error message and stop the method.
        if (relationalConstraintsList == null) {
          CartAComInitialisations.logger
              .error("No relational-constraint element in file "
                  + constraintsDescriptorsFile);
          return;
        }

        for (int i = 0; i < relationalConstraintsList.getLength(); i++) {
          Element relationalConstraint = (Element) relationalConstraintsList
              .item(i);
          CartAComInitialisations.logger.debug("Node relational-constraint n° "
              + i + " : " + relationalConstraint);

          boolean toConsider = Boolean
              .parseBoolean(((Element) (relationalConstraint
                  .getElementsByTagName("to-consider")).item(0))
                      .getTextContent());

          CartAComInitialisations.logger
              .debug("Node to-consider for relational-constraint node n° " + i
                  + " : " + toConsider);

          if (toConsider) {
            String name = relationalConstraint.getAttribute("name");

            CartAComInitialisations.logger
                .debug("Attribute name for relational-constraint node n° " + i
                    + " : " + name);

            double importance = Double
                .parseDouble(((Element) (relationalConstraint
                    .getElementsByTagName("importance")).item(0))
                        .getTextContent());

            CartAComInitialisations.logger
                .debug("Node importance for relational-constraint node n° " + i
                    + " : " + importance);

            Element relationalConstraintsElement = null;

            try {
              String xPathQueryString = "//relational-constraint[@name='" + name
                  + "']";
              XPathExpression expr = xpath.compile(xPathQueryString);
              // Get the node set
              CartAComInitialisations.logger
                  .debug("XPath query for relational-constraint node n° " + i
                      + " : " + xPathQueryString);

              relationalConstraintsElement = ((Element) ((NodeList) expr
                  .evaluate(constraintsToConsiderDocument,
                      XPathConstants.NODESET)).item(0));

            } catch (XPathExpressionException e) {
              e.printStackTrace();
            }

            if (relationalConstraintsElement == null) {
              // break;

            } else {
              try {

                @SuppressWarnings("unchecked")
                Class<? extends MicroMicroRelation> relationJavaClass = (Class<? extends MicroMicroRelation>) Class
                    .forName(((Element) (relationalConstraintsElement
                        .getElementsByTagName("relation-java-class")).item(0))
                            .getTextContent());

                @SuppressWarnings("unchecked")
                Class<? extends ICartAComAgentGeneralisation> agentType1 = (Class<? extends ICartAComAgentGeneralisation>) Class
                    .forName(((Element) (relationalConstraintsElement
                        .getElementsByTagName("agent-type-1")).item(0))
                            .getTextContent());

                @SuppressWarnings("unchecked")
                Class<? extends ICartAComAgentGeneralisation> agentType2 = (Class<? extends ICartAComAgentGeneralisation>) Class
                    .forName(((Element) (relationalConstraintsElement
                        .getElementsByTagName("agent-type-2")).item(0))
                            .getTextContent());

                cartacomSpecifications.getConstraintsToConsider()
                    .add(new RelationalConstraintDescriptor(agentType1,
                        agentType2, relationJavaClass, importance));

              } catch (ClassNotFoundException e) {

                CartAComInitialisations.logger.error(
                    "Can't instanciate new RelationalConstraintDescriptor. Check the validity of te file "
                        + constraintsToConsiderDocument);
                e.printStackTrace();
              }
            }

          }

        }
      }
    }

  }
  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
