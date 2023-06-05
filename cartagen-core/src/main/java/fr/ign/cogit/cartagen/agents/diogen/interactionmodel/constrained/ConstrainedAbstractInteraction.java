package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * Abstract class for interaction with constraints.
 * @author AMaudet
 * 
 */
public abstract class ConstrainedAbstractInteraction
    implements ConstrainedInteraction {

  private static Logger logger = LogManager
      .getLogger(ConstrainedAbstractInteraction.class.getName());

  private static String CONFIGURATION_FILE = "/padawan/interactions.xml";

  protected static Class<?>[] signature = new Class[] { Environment.class,
      IAgent.class, GeographicConstraint.class };

  protected static Class<?>[] signature2 = new Class[] { Environment.class,
      IAgent.class, IAgent.class, GeographicConstraint.class };

  private int weight = 0;

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getWeight() {
    return weight;
  }

  /**
   * The types of the constraints.
   */
  private Set<ConstraintType> constraintsTypeNameList = new HashSet<>();

  /**
   * Getter for constraintsTypesList.
   * 
   * @return
   */
  @Override
  public Set<ConstraintType> getConstraintsTypeNameList() {
    return this.constraintsTypeNameList;
  }

  // /**
  // * Setter for constraintsTypesList.
  // *
  // * @param constraintsTypesList
  // */
  // public void setConstraintsTypeNameList(
  // Set<ConstraintType> constraintsTypesList) {
  // this.constraintsTypeNameList = constraintsTypesList;
  // }

  /**
   * Method adding constraintType to constraintsTypesList.
   * 
   * @param constraintType
   */
  public void addConstraintTypeName(ConstraintType constraintType) {
    this.constraintsTypeNameList.add(constraintType);
  }

  /**
   * Method removing constraintType to constraintsTypesList.
   * 
   * @param constraintType
   */
  public void removeConstraintTypeName(String constraintType) {
    this.constraintsTypeNameList.remove(constraintType);
  }

  /**
   * Return true if the name of the constraint with the name ConstraintType is
   * considered.
   * 
   * @param ConstraintType
   * @return
   */
  public boolean hasConstraintTypeName(String ConstraintType) {
    return this.constraintsTypeNameList.contains(ConstraintType);
  }

  @Override
  public String toString() {
    return this.getName();
  }

  private Action action;

  @Override
  public Action getAction() {
    return this.action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  private String name = "";

  /**
   * Getter for name.
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Setter for name.
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  private String description = "";

  /**
   * Getter for description.
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Setter for description.
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Static method to obtain constraint object from the class.
   * 
   * @param constraints
   * @param classes
   * @return
   */
  // public static Set<GeographicConstraint>
  // getConstraintByType(Set<GeographicConstraint> constraints,
  // Set<Class<GeographicConstraint>> classes) {
  // Set<GeographicConstraint> returnList = new HashSet<GeographicConstraint>();
  // for ( GeographicConstraint co : constraints) {
  // for (Class<GeographicConstraint> cl : classes) {
  // if ((co.getClass()).equals(cl) ) {
  // returnList.add(co);
  // }
  // }
  // }
  // return returnList;
  // }

  public void loadSpecification() {

    try {

      // we create a SAXBuilder instance
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      // domFactory.setNamespaceAware(true); // never forget this!
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document document = null;

      // Get the path of the conf file
      String filePath = ConstrainedAbstractInteraction.class
          .getResource(ConstrainedAbstractInteraction.CONFIGURATION_FILE)
          .getFile();
      ConstrainedAbstractInteraction.logger
          .debug("Path of the file : " + filePath);

      // Get the document
      document = builder.parse(filePath);

      if (document == null) {
        ConstrainedAbstractInteraction.logger
            .error("The document shouldn't be null.");
        return;
      }

      // logger.debug("Document : " + document);
      // logger.debug("Document has children ? " + document.hasChildNodes());

      // Create factory to read xml document.
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();

      XPathExpression expr = xpath.compile(
          "//interaction[@class='" + this.getClass().getCanonicalName() + "']");

      // Get the node for this interaction
      Object result = expr.evaluate(document, XPathConstants.NODESET);
      Node interactionNode = ((NodeList) result).item(0);
      // if the node doesn't exist, return error msg
      if (interactionNode == null) {
        ConstrainedAbstractInteraction.logger
            .error("The interaction " + this.getClass() + " is not defined in "
                + ConstrainedAbstractInteraction.CONFIGURATION_FILE);
        return;
      }
      this.setName(((Element) interactionNode).getAttribute("name"));
      this.setDescription(
          ((Element) interactionNode).getAttribute("description"));

      // logger.debug("Root node " + interactionNode);
      // logger.debug("Root has children ? " + interactionNode.hasChildNodes());
      XPathExpression expr2;
      expr2 = xpath.compile("//interaction[@class='"
          + this.getClass().getCanonicalName() + "']/constraint_type");
      // Get he list of constraint type.
      NodeList constraintTypeElements = (NodeList) expr2.evaluate(document,
          XPathConstants.NODESET);

      // logger.debug("ConstraintType list : " + constraintTypeElements);

      ConstrainedAbstractInteraction.logger
          .debug("ConstraintType has attribute ? " + constraintTypeElements
              .item(0).getAttributes().item(0).getNodeName());

      // parse the list of constraint type
      for (int i = 0; i < constraintTypeElements.getLength(); i++) {

        // Return the element
        Element constraintType = (Element) constraintTypeElements.item(i);
        ConstrainedAbstractInteraction.logger
            .debug("ConstraintType : " + constraintType);

        ConstrainedAbstractInteraction.logger
            .debug("Attribute : " + constraintType.getAttribute("class"));
        ConstrainedAbstractInteraction.logger
            .debug("Attribute : " + constraintType.getAttribute("position"));
        ConstrainedAbstractInteraction.logger
            .debug("Attribute : " + constraintType.getAttribute("default"));

        int influenceDefault = 0;
        // getValueFromInfluence(constraintType
        // .getAttribute("default"));

        ConstraintType constraintTypeObject = new ConstraintType(
            constraintType.getAttribute("class"), 1, influenceDefault);

        // ConstrainedAbstractInteraction.logger.debug("Test1");

        NodeList influenceElements = constraintType
            .getElementsByTagName("influence");

        for (int j = 0; j < influenceElements.getLength(); j++) {

          // ConstrainedAbstractInteraction.logger.debug("Test2");

          Element influence = (Element) influenceElements.item(j);
          Method method = null;
          try {
            method = this.getClass().getMethod(influence.getAttribute("method"),
                ConstrainedAbstractInteraction.signature);
          } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (NoSuchMethodException e) {
            try {
              method = this.getClass().getMethod(
                  influence.getAttribute("method"),
                  ConstrainedAbstractInteraction.signature2);
            } catch (SecurityException e2) {
              e.printStackTrace();
            } catch (NoSuchMethodException e2) {

              e.printStackTrace();
            }
          }

          String influenceValue = influence.getAttribute("value");

          int influenceResult = this.getValueFromInfluence(influenceValue);

          String influenceDescription = influence.getAttribute("description");

          constraintTypeObject.addInfluence(influenceResult, method,
              influenceDescription);
          // if (Integer.parseInt(influence.getAttribute("default")) == 1) {
          // this.se
          // }

        }

        this.addConstraintTypeName(constraintTypeObject);
      }

      ConstrainedAbstractInteraction.logger.debug("End for " + this);
      for (ConstraintType cc : this.getConstraintsTypeNameList()) {

        ConstrainedAbstractInteraction.logger
            .debug("this.constraintObject = " + cc);
        ConstrainedAbstractInteraction.logger
            .debug("this.constraintObject = " + cc.getName());
        ConstrainedAbstractInteraction.logger
            .debug("this.constraintObject = " + cc.getInfluencesMap());
      }

    } catch (XPathExpressionException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Get the int value for a type of influence.
   * 
   * @param influenceValue
   * @return
   */
  protected int getValueFromInfluence(String influenceValue) {

    int influenceResult = ConstraintType.INDIFFERENT;

    if (influenceValue.equals("strongly_favorable")) {
      influenceResult = ConstraintType.STRONGHLY_FAVORABLE;
    } else if (influenceValue.equals("opposite")) {
      influenceResult = ConstraintType.OPPOSITE;
    } else if (influenceValue.equals("indifferent")) {
      influenceResult = ConstraintType.INDIFFERENT;
    } else if (influenceValue.equals("favorable")) {
      influenceResult = ConstraintType.FAVORABLE;
    } else if (influenceValue.equals("unfavorable")) {
      influenceResult = ConstraintType.UNFAVORABLE;
    }
    ConstrainedAbstractInteraction.logger
        .debug(influenceValue + " = " + influenceResult);
    return influenceResult;
  }
}
