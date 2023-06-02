package fr.ign.cogit.cartagen.agents.diogen.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.algorithms.network.roads.SlideDeadEnd;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class EmbeddedDeadEndArea extends GeneObjDefault
    implements IEmbeddedDeadEndArea {

  private static Logger logger = LogManager
      .getLogger(EmbeddedDeadEndArea.class.getName());

  private List<IUrbanElement> urbanElements = new ArrayList<IUrbanElement>();

  @Override
  public List<IUrbanElement> getUrbanElements() {
    return this.urbanElements;
  }

  @Override
  public void setUrbanElements(List<IUrbanElement> urbanElements) {
    this.urbanElements = urbanElements;
  }

  @Override
  public void addUrbanElement(IUrbanElement urbanElement) {
    this.urbanElements.add(urbanElement);
  }

  @Override
  public void removeUrbanElement(IUrbanElement urbanElement) {
    this.urbanElements.remove(urbanElement);
  }

  private DeadEndGroup deadEnd;

  @Override
  public DeadEndGroup getDeadEnd() {
    return this.deadEnd;
  }

  @Override
  public void setDeadEnd(DeadEndGroup deadEnd) {
    this.deadEnd = deadEnd;
  }

  private IUrbanBlock block;

  @Override
  public IUrbanBlock getBlock() {
    return this.block;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

  @Override
  public IDirectPosition getRootDirectPosition() {
    return this.deadEnd.getRootNode().getPosition();
  }

  @Override
  public Set<INetworkSection> getConnectedNetwork() {
    return this.deadEnd.getFeaturesConnectedToRoot();
  }

  @Override
  public void goToLeft(double distance) {
    this.goToDirection(distance, true);
  }

  @Override
  public void goToRight(double distance) {
    this.goToDirection(distance, false);
  }

  private void goToDirection(double distance, boolean left) {

    // get the point of the other side of the root section
    IDirectPosition point1;
    IDirectPosition startTest = this.deadEnd.getRoot().getGeom().startPoint();
    IDirectPosition endTest = this.deadEnd.getRoot().getGeom().endPoint();

    if (this.getRootDirectPosition().distance(startTest) > this
        .getRootDirectPosition().distance(endTest)) {
      point1 = this.deadEnd.getRoot().getGeom()
          .getSegment(this.deadEnd.getRoot().getGeom().sizeSegment() - 1)
          .startPoint();
    } else {
      point1 = this.deadEnd.getRoot().getGeom().getSegment(0).endPoint();
    }

    // Calculation of the coordinate of point1-root vector
    double x10 = point1.getX() - this.getRootDirectPosition().getX();
    double y10 = point1.getY() - this.getRootDirectPosition().getY();

    INetworkSection toReturn = null;
    double thresholdtAngle;
    if (!left) {
      thresholdtAngle = Double.MAX_VALUE;
    } else {
      thresholdtAngle = Double.MIN_VALUE;
    }

    // for each connected element from the network, find the angle
    for (INetworkSection section : this.getConnectedNetwork()) {
      IDirectPosition start = section.getGeom().startPoint();
      IDirectPosition end = section.getGeom().endPoint();

      ICurveSegment segment;
      IDirectPosition point3;

      if (this.getRootDirectPosition().distance(start) > this
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
      double x12 = this.getRootDirectPosition().getX() - point3.getX();
      double y12 = this.getRootDirectPosition().getY() - point3.getY();

      double angle = Math.atan2(x10 * y12 - y10 * x12, x10 * x12 + y10 * y12);
      if (angle < 0) {
        angle = angle + 2 * +Math.PI;
      }

      // Absolute value of the angle
      // angle = Math.abs(angle);

      // segmentToSupress.put(section, segment);
      // pointForSegment.put(section, point3);

      EmbeddedDeadEndArea.logger
          .debug("Angle between dead end and " + section + " = " + angle);

      if (((angle > thresholdtAngle) && (left))
          || ((angle < thresholdtAngle) && (!left))) {
        thresholdtAngle = angle;
        toReturn = section;
        // toGo = point3;
      }

    }

    EmbeddedDeadEndArea.logger.debug("Slide Dead End parameters " + this.deadEnd
        + " slide on " + toReturn + " distance " + distance);
    SlideDeadEnd sde = new SlideDeadEnd(this.deadEnd, toReturn, distance);

    boolean sectionToAdd = (sde).execute();

    double xTranslate = sde.getTranslationVector().getX();
    double yTranslate = sde.getTranslationVector().getY();

    EmbeddedDeadEndArea.logger
        .debug("Translation vector x= " + xTranslate + " y= " + yTranslate);

    this.setGeom(
        CommonAlgorithms.translation(this.getGeom(), xTranslate, yTranslate));

    for (IUrbanElement urbanElement : this.getUrbanElements()) {
      urbanElement.setGeom(CommonAlgorithms.translation(urbanElement.getGeom(),
          xTranslate, yTranslate));
    }

    for (IGeneObj section : sde.getNewGeometries().keySet()) {
      section.setGeom(sde.getNewGeometries().get(section));
    }

    if (sectionToAdd && toReturn != null) {
      INetworkSection newSection = new RoadLine(sde.getNewSectionGeometry(),
          toReturn.getImportance());

      CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()
          .addSection(newSection);
    }
  }

  public double getBuildingsOverlappingRateMean() {
    if (this.block.isDeleted()) {
      return 0.0;
    }
    double mean = 0.0;
    int nb = 0;
    for (IUrbanElement ag : this.block.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      if (!(ag instanceof IBuilding)) {
        continue;
      }
      mean += BlockBuildingsMeasures.getBuildingOverlappingRate(ag, this.block);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }
    return mean;
  }
}
