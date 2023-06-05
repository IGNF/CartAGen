package fr.ign.cogit.cartagen.agents.diogen.interaction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedAbstractInteraction;

/**
 * Static class to store all constraint type.
 * 
 * @author AMaudet
 * 
 */
public class InteractionConfiguration {

  private static Logger logger = LogManager
      .getLogger(InteractionConfiguration.class.getName());

  private static Map<String, Interaction> interactionsMap = null;
  public static String GRANULARITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Granularity";
  public static String SIZE_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Size";
  public static String SQUARENESS_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Squareness";
  public static String ORIENTATION_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Orientation";
  public static String MESO_COMPONENTS_SATISFACTION_CLASS_NAME = "fr.ign.cogit.cartagen.common.agentGeoxygene.constraint.MesoComponentsSatisfaction";
  public static String DENSITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.block.Density";
  public static String PROXIMITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.block.Proximity";
  public static String NON_OVERLAPPING_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.NonOverlappingOfAlignments";
  public static String ELONGATION_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Elongation";
  public static String CONVEXITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.agentGeneCore.constraint.building.Convexity";
  public static String BUILDING_ROAD_ORIENTATION_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingroad.BuildingOrientation";
  public static String ROAD_BUILDING_ORIENTATION_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingroad.RoadOrientation";
  public static String BUILDINGS_PROXIMITY_ROAD_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingbuilding.BuildingProximity";
  public static String BUILDING_ROAD_PROXIMITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingroad.BuildingProximity";
  public static String ROAD_BUILDING_PROXIMITY_CLASS_NAME = "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingroad.RoadProximity";
  // public static String BUILDING_ORIENTATION_BUILDING_CLASS_NAME =
  // "fr.ign.cogit.cartagen.agentGeneralisation.cartacom.constraint.buildingbuilding.BuildingOrientation";

  private static String CONFIGURATION_FILE = "/padawan/interactions.xml";

  public static Map<String, Interaction> getInteractionsMap() {

    if (interactionsMap != null)
      return interactionsMap;

    Map<String, Interaction> toReturn = new Hashtable<String, Interaction>();

    // we create a SAXBuilder instance
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    // domFactory.setNamespaceAware(true); // never forget this!
    DocumentBuilder builder = null;

    try {
      builder = domFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    if (builder == null)
      return null;

    Document document = null;

    // Get the path of the conf file
    String filePath = ConstrainedAbstractInteraction.class
        .getResource(CONFIGURATION_FILE).getFile();
    // logger.debug("Path of the file : " + filePath);

    // Get the document
    try {
      document = builder.parse(filePath);
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (document == null) {
      return null;
    }

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();

    XPathExpression expr = null;
    try {
      expr = xpath.compile("//interaction");
    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (expr == null)
      return null;

    NodeList result = null;

    try {
      result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (result == null)
      return null;

    for (int i = 0; i < result.getLength(); i++) {
      Element interactionNode = (Element) result.item(i);

      if (interactionNode == null) {
        return null;
      }

      String name = interactionNode.getAttribute("name");

      Class<?> interactionClass;
      try {

        logger.debug(
            "Interaction class " + interactionNode.getAttribute("class"));

        interactionClass = Class.forName(interactionNode.getAttribute("class"));

        Method instanceMethod = interactionClass
            .getDeclaredMethod("getInstance", new Class<?>[] {});

        Interaction interaction = (Interaction) instanceMethod.invoke(null,
            new Object[] {});

        logger.debug("Interaction instance  " + interaction);

        toReturn.put(name, interaction);
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    interactionsMap = toReturn;

    return toReturn;

  }

  public static Set<Interaction> getInteractionList() {

    return new HashSet<Interaction>(getInteractionsMap().values());

  }

}
