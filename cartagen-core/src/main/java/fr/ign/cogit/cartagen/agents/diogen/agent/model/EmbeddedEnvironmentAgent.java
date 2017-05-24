package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.diogen.constraint.EmbeddedEnvironmentSatisfaction;
import fr.ign.cogit.cartagen.agents.diogen.constraint.EnoughSpaceAtLeftRayTracing;
import fr.ign.cogit.cartagen.agents.diogen.constraint.EnoughSpaceAtRightRayTracing;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentTypesList;
import fr.ign.cogit.cartagen.agents.diogen.schema.IEmbeddedDeadEndArea;
import fr.ign.cogit.cartagen.algorithms.network.roads.SlideDeadEnd;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class EmbeddedEnvironmentAgent
    extends GeographicObjectAgentGeneralisation implements IDiogenAgent {

  @Override
  public void instantiateConstraints() {
    new EnoughSpaceAtLeftRayTracing(this, 5);
    new EnoughSpaceAtRightRayTracing(this, 5);

    // instantiate the constraint for the host block agent
    for (Environment env : this.getContainingEnvironments()) {
      if (env.getHostAgent() instanceof BlockAgent) {
        for (Constraint c : env.getHostAgent().getConstraints()) {
          if (c instanceof EmbeddedEnvironmentSatisfaction) {
            return;
          }
        }
      }
      new EmbeddedEnvironmentSatisfaction((BlockAgent) env.getHostAgent(), 5);
    }
  }

  public EmbeddedEnvironmentAgent(IEmbeddedDeadEndArea edea) {
    super();
    this.setFeature(edea);
    if (edea.getGeom() != null) {
      this.setInitialGeom((IGeometry) edea.getGeom().clone());
    }
    Environment encapsulatedEnv = new Environment();
    encapsulatedEnv
        .setEnvironmentType(EnvironmentTypesList.getDeadEndEnvironmentType());
    this.setEncapsulatedEnv(encapsulatedEnv);
    // this
  }

  public double getRoadWidth() {
    return 2 * ((IEmbeddedDeadEndArea) this.getFeature()).getDeadEnd().getRoot()
        .getWidth();
  }

  private IGeometry oldGeometry;
  private Map<IUrbanElement, IGeometry> buildingOldGeometries = new Hashtable<IUrbanElement, IGeometry>();
  private Map<IGeneObj, IGeometry> sectionsOldGeometries = new Hashtable<IGeneObj, IGeometry>();

  @Override
  public void goBackToInitialState() {

    IEmbeddedDeadEndArea feature = ((IEmbeddedDeadEndArea) this.getFeature());

    feature.setGeom(this.oldGeometry);

    for (IUrbanElement urbanElement : this.buildingOldGeometries.keySet()) {
      urbanElement.setGeom(this.buildingOldGeometries.get(urbanElement));
    }

    for (IGeneObj section : this.sectionsOldGeometries.keySet()) {
      // this.sectionsOldGeometries.put(section, section.getGeom());
      section.setGeom(this.sectionsOldGeometries.get(section));
    }

    this.buildingOldGeometries = new Hashtable<IUrbanElement, IGeometry>();
    this.sectionsOldGeometries = new Hashtable<IGeneObj, IGeometry>();

    super.goBackToInitialState();
  }

  @Override
  public void goBackToState(AgentState state) {

    IEmbeddedDeadEndArea feature = ((IEmbeddedDeadEndArea) this.getFeature());

    feature.setGeom(this.oldGeometry);

    for (IUrbanElement urbanElement : this.buildingOldGeometries.keySet()) {
      urbanElement.setGeom(this.buildingOldGeometries.get(urbanElement));
    }

    for (IGeneObj section : this.sectionsOldGeometries.keySet()) {
      // this.sectionsOldGeometries.put(section, section.getGeom());
      section.setGeom(this.sectionsOldGeometries.get(section));
    }

    this.buildingOldGeometries = new Hashtable<IUrbanElement, IGeometry>();
    this.sectionsOldGeometries = new Hashtable<IGeneObj, IGeometry>();

    super.goBackToState(state);
  }

  public void goToLeft(double distance) {
    this.goToDirection(distance, true);
  }

  public void goToRight(double distance) {
    this.goToDirection(distance, false);
  }

  private void goToDirection(double distance, boolean left) {

    IEmbeddedDeadEndArea feature = ((IEmbeddedDeadEndArea) this.getFeature());
    // get the point of the other side of the root section
    IDirectPosition point1;
    IDirectPosition startTest = feature.getDeadEnd().getRoot().getGeom()
        .startPoint();
    IDirectPosition endTest = feature.getDeadEnd().getRoot().getGeom()
        .endPoint();

    if (feature.getRootDirectPosition().distance(startTest) > feature
        .getRootDirectPosition().distance(endTest)) {
      point1 = feature.getDeadEnd().getRoot().getGeom()
          .getSegment(
              feature.getDeadEnd().getRoot().getGeom().sizeSegment() - 1)
          .startPoint();
    } else {
      point1 = feature.getDeadEnd().getRoot().getGeom().getSegment(0)
          .endPoint();
    }

    // Calculation of the coordinate of point1-root vector
    double x10 = point1.getX() - feature.getRootDirectPosition().getX();
    double y10 = point1.getY() - feature.getRootDirectPosition().getY();

    INetworkSection toReturn = null;
    double thresholdtAngle;
    if (!left) {
      thresholdtAngle = Double.MAX_VALUE;
    } else {
      thresholdtAngle = Double.MIN_VALUE;
    }

    // for each connected element from the network, find the angle
    for (INetworkSection section : feature.getConnectedNetwork()) {
      IDirectPosition start = section.getGeom().startPoint();
      IDirectPosition end = section.getGeom().endPoint();

      ICurveSegment segment;
      IDirectPosition point3;

      if (feature.getRootDirectPosition().distance(start) > feature
          .getRootDirectPosition().distance(end)) {
        segment = section.getGeom()
            .getSegment(section.getGeom().sizeSegment() - 1);
        point3 = segment.startPoint();
        // pointForSegment.put(section, segment.endPoint());
      } else {
        segment = section.getGeom().getSegment(0);
        point3 = segment.endPoint();
        // pointForSegment.put(section, segment.startPoint());
      }

      // compute the angles

      // Calculation of the coordinate of root-point3 vector
      double x12 = feature.getRootDirectPosition().getX() - point3.getX();
      double y12 = feature.getRootDirectPosition().getY() - point3.getY();

      double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);
      if (angle < 0) {
        angle = angle + 2 * +Math.PI;
      }

      // Absolute value of the angle
      // angle = Math.abs(angle);

      // segmentToSupress.put(section, segment);
      // pointForSegment.put(section, point3);

      // logger.debug("Angle between dead end and " + section + " = " + angle);

      if (((angle > thresholdtAngle) && (left))
          || ((angle < thresholdtAngle) && (!left))) {
        thresholdtAngle = angle;
        toReturn = section;
        // toGo = point3;
      }

    }

    // logger.debug("Slide Dead End parameters " + deadEnd + " slide on "
    // + toReturn + " distance " + distance);
    SlideDeadEnd sde = new SlideDeadEnd(feature.getDeadEnd(), toReturn,
        distance);

    boolean sectionToAdd = (sde).execute();

    double xTranslate = sde.getTranslationVector().getX();
    double yTranslate = sde.getTranslationVector().getY();

    // logger.debug("Translation vector x= " + xTranslate + " y= " +
    // yTranslate);

    // change the geometry of the dead end zone
    this.oldGeometry = (IGeometry) feature.getGeom().clone();
    feature.setGeom(
        CommonAlgorithms.translation(this.getGeom(), xTranslate, yTranslate));

    for (IUrbanElement urbanElement : feature.getUrbanElements()) {
      this.buildingOldGeometries.put(urbanElement,
          (IGeometry) urbanElement.getGeom().clone());
      urbanElement.setGeom(CommonAlgorithms.translation(urbanElement.getGeom(),
          xTranslate, yTranslate));
    }

    for (IGeneObj section : sde.getNewGeometries().keySet()) {
      this.sectionsOldGeometries.put(section,
          (IGeometry) section.getGeom().clone());
      section.setGeom(sde.getNewGeometries().get(section));
    }

    if (sectionToAdd && toReturn != null) {
      INetworkSection newSection = new RoadLine(sde.getNewSectionGeometry(),
          toReturn.getImportance());

      CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()
          .addSection(newSection);
    }
  }

  @Override
  public Set<Environment> getBorderedEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public BorderStrategy getBorderStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Environment getEncapsulatedEnv() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getContainingEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    // TODO Auto-generated method stub

  }

}
