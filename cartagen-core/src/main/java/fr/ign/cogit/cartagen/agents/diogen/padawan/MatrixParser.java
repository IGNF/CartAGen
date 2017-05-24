package fr.ign.cogit.cartagen.agents.diogen.padawan;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

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

import fr.ign.cogit.cartagen.agents.cartacom.CartAComInitialisations;
import fr.ign.cogit.cartagen.agents.diogen.interaction.InteractionConfiguration;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.AssignationImpl;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.InteractionMatrix;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

public class MatrixParser {

  private static Logger logger = Logger.getLogger(MatrixParser.class.getName());

  public static String ENVIRONMENTS_XML = "/padawan/matrices_routes.xml";

  public static Map<String, EnvironmentType> parseEnvironmentsXML() {
    return parseEnvironmentsXML(ENVIRONMENTS_XML);
  }

  public static Map<String, EnvironmentType> parseEnvironmentsXML(String xml) {

    MatrixParser.logger.debug(InteractionConfiguration.getInteractionsMap());

    // Set to return
    Map<String, EnvironmentType> toReturn = new Hashtable<String, EnvironmentType>();

    String environmentXML = CartAComInitialisations.class
        .getResource(MatrixParser.ENVIRONMENTS_XML).getFile();

    // we create a SAXBuilder instance
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

    DocumentBuilder builder = null;
    try {
      builder = domFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      MatrixParser.logger.error("Error when creating DOM builder.");
      e.printStackTrace();
    }

    if (builder == null) {
      MatrixParser.logger.error("Error when creating DOM builder.");
      return toReturn;
    }

    Document constraintsDescriptorsDocument = null;

    try {
      constraintsDescriptorsDocument = builder.parse(environmentXML);

    } catch (SAXException e) {
      MatrixParser.logger
          .error("Error, invalide XML file: " + MatrixParser.ENVIRONMENTS_XML);
      e.printStackTrace();
    } catch (IOException e) {
      MatrixParser.logger.error(
          "Error when readin XML file: " + MatrixParser.ENVIRONMENTS_XML);
      e.printStackTrace();
    }

    if (constraintsDescriptorsDocument == null) {
      MatrixParser.logger.error(
          "Error when readin XML file: " + MatrixParser.ENVIRONMENTS_XML);
      return toReturn;
    }

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();

    NodeList environmentsNodeList = null;

    try {
      XPathExpression expr = xpath.compile("//environment");
      // Get the node set
      environmentsNodeList = ((NodeList) expr
          .evaluate(constraintsDescriptorsDocument, XPathConstants.NODESET));
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }

    if (environmentsNodeList == null) {
      return null;
    }

    for (int i = 0; i < environmentsNodeList.getLength(); i++) {
      Element environmentElement = (Element) environmentsNodeList.item(i);

      EnvironmentType environmentType = new EnvironmentType();

      InteractionMatrix<ConstrainedInteraction> matrix = environmentType
          .getInteractionMatrix();

      String envName = environmentElement.getAttribute("name");

      environmentType.setEnvironmentTypeName(envName);

      toReturn.put(envName, environmentType);

      Element matrixElement = ((Element) (environmentElement
          .getElementsByTagName("matrix")).item(0));

      NodeList interactionsElements = (matrixElement
          .getElementsByTagName("assignation"));

      for (int j = 0; j < interactionsElements.getLength(); j++) {
        Element assignationElement = (Element) interactionsElements.item(j);

        MatrixParser.logger.debug("Assignation n° " + j);

        String interactionName = ((Element) (assignationElement
            .getElementsByTagName("interaction")).item(0)).getAttribute("name");

        MatrixParser.logger.debug("Interaction " + interactionName);

        ConstrainedInteraction interaction = (ConstrainedInteraction) InteractionConfiguration
            .getInteractionsMap().get(interactionName);

        MatrixParser.logger.debug("Interaction " + interaction);

        String sourceName = ((Element) (assignationElement
            .getElementsByTagName("source")).item(0)).getAttribute("class");

        MatrixParser.logger.debug("Source " + sourceName);

        String targetName = null;
        if (assignationElement.getElementsByTagName("target") != null) {
          targetName = ((Element) (assignationElement
              .getElementsByTagName("target")).item(0)).getAttribute("class");
        }

        ((Element) (assignationElement.getElementsByTagName("target")).item(0))
            .getAttribute("class");

        // String targetName = ((Element) ((NodeList) assignationElement
        // .getElementsByTagName("target")).item(0)).getAttribute("class");

        try {
          if ((sourceName == null) || sourceName.isEmpty()) {
            @SuppressWarnings("unchecked")
            Class<IAgent> targetAgent = (Class<IAgent>) Class
                .forName(targetName);
            matrix.addSingleTargetHostAssignation(targetAgent,
                new AssignationImpl<ConstrainedInteraction>(interaction));
          } else if (targetName == null || targetName.isEmpty()) {
            @SuppressWarnings("unchecked")
            Class<IAgent> forName = (Class<IAgent>) Class.forName(sourceName);
            matrix.addDegenerateAssignation(forName,
                new AssignationImpl<ConstrainedInteraction>(interaction));
          } else {
            @SuppressWarnings("unchecked")
            Class<IAgent> sourceAgent = (Class<IAgent>) Class
                .forName(sourceName);
            @SuppressWarnings("unchecked")
            Class<IAgent> targetAgent = (Class<IAgent>) Class
                .forName(targetName);
            matrix.addSingleTargetAssignation(sourceAgent, targetAgent,
                new AssignationImpl<ConstrainedInteraction>(interaction));
          }
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }

    }

    return toReturn;

  }
}
