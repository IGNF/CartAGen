package fr.ign.cogit.cartagen.agents.diogen.constraint.points;

import java.awt.Color;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartAComAgentDeformableGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteStroke;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.preprocessing.ClosestPointAdvanced;
import fr.ign.cogit.cartagen.agents.diogen.preprocessing.StrokeSymbol;
import fr.ign.cogit.cartagen.agents.diogen.relation.NonOverlappingHikingRoad;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraintImpl;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class PointNotUnderRoad extends RelationalConstraintImpl
    implements ForPointDisplacementConstraint {

  public PointNotUnderRoad(IGeographicAgent agent,
      NonOverlappingHikingRoad relation, double importance) {
    super(agent, relation, importance);
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {

    this.computeSatisfaction();
    // System.out.println(this.getSatisfaction());
    if (this.getSatisfaction() == 5) {
      new PointAgentDisplacementAction(p, null, 0, 0);
      return;
    }

    GeographicObjectAgent agent = (GeographicObjectAgent) this
        .getAgentSharingConstraint();

    // f is a stroke
    IFeature f = agent.getFeature();

    IDirectPosition previous = null;
    double distanceMin = Double.MAX_VALUE;
    IDirectPosition closest = null;
    IDirectPosition previousClosest = null;

    for (IDirectPosition position : f.getGeom().coord()) {
      if (previous == null) {
        previous = position;
        continue;
      }
      double distance = position.distance(p.getPosition());
      if (distance < distanceMin) {
        distanceMin = distance;
        closest = position;
        previousClosest = previous;
      }
      previous = position;
    }

    // compute the vectorial product
    double pv = (previousClosest.getX() - p.getPosition().getX())
        * (closest.getY() - p.getPosition().getY())
        - (previousClosest.getY() - p.getPosition().getY())
            * (closest.getX() - p.getPosition().getX());

    // compute the scalar product
    double ps = (previousClosest.getX() - p.getPosition().getX())
        * (closest.getX() - p.getPosition().getX())
        + (previousClosest.getY() - p.getPosition().getY())
            * (closest.getY() - p.getPosition().getY());

    double angle = Math.atan2(pv, ps);

    IPolygon polygon = (IPolygon) StrokeSymbol
        .getSymbolExtentWithCarriedObjects((IHikingRouteStroke) f, (angle > 0));

    ILineString offset = StrokeSymbol
        .getOffsetWithCarriedObjects((IHikingRouteStroke) f, (angle > 0));

    IAgent host = null;
    for (Environment env : ((IDiogenAgent) p).getContainingEnvironments()) {
      host = env.getHostAgent();
      if (host == null) {
        System.out.println("Env : " + env);
      }
      if (host instanceof ICartAComAgentDeformableGeneralisation) {
        break;
      }
    }
    if (host == null) {
      System.out
          .println("Env : " + ((IDiogenAgent) p).getContainingEnvironments());
    }

    IGeneObj coastLine = (IGeneObj) ((ICartacomAgent) host).getFeature();

    IDirectPosition previousPoint = null;
    IDirectPosition middlePoint = null;
    IDirectPosition nextPoint = null;

    for (int i = 0; i < coastLine.getGeom().coord().size(); i++) {
      IDirectPosition p1 = coastLine.getGeom().coord().get(i);
      if (p1 == p.getPosition()) {
        if (previous == null || i == 0) {
          previousPoint = p1;
          middlePoint = coastLine.getGeom().coord().get(i + 1);
          nextPoint = coastLine.getGeom().coord().get(i + 2);
        } else if (i == coastLine.getGeom().coord().size() - 1) {
          previousPoint = coastLine.getGeom().coord().get(i - 2);
          middlePoint = coastLine.getGeom().coord().get(i - 1);
          nextPoint = p1;
        } else {
          previousPoint = coastLine.getGeom().coord().get(i - 1);
          middlePoint = p1;
          nextPoint = coastLine.getGeom().coord().get(i + 1);
        }
        break;
      }
    }

    // System.out.println("Polygon " + polygon.getExterior());

    IDirectPosition nearestPoint = ClosestPointAdvanced
        .getClosestPointsInDirection(middlePoint, previousPoint, nextPoint,
            offset, false);
    if (nearestPoint == null) {
      nearestPoint = JtsAlgorithms.getClosestPoint(p.getPosition(), offset);
    }

    // IDirectPosition nearestPoint = JtsAlgorithms.getClosestPoint(
    // p.getPosition(), offset);

    // if (nearestPoint == null) {
    // CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
    // .addFeatureToGeometryPool(polygon, Color.BLUE, 2);
    CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        .addFeatureToGeometryPool(p.getPosition().toGM_Point(), Color.RED, 2);
    CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        .addFeatureToGeometryPool(nearestPoint.toGM_Point(), Color.GREEN, 2);
    // }

    double dx = alpha * (nearestPoint.getX() - p.getPosition().getX());
    double dy = alpha * (nearestPoint.getY() - p.getPosition().getY());

    new PointAgentDisplacementAction(p, this, dx, dy);

  }
}
