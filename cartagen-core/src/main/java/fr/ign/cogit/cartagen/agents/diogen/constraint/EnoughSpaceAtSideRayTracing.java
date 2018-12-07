package fr.ign.cogit.cartagen.agents.diogen.constraint;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.EmbeddedEnvironmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.algorithms.RayTracingFromLinear;
import fr.ign.cogit.cartagen.agents.diogen.schema.IEmbeddedDeadEndArea;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public abstract class EnoughSpaceAtSideRayTracing
    extends GeographicConstraintImpl {

  public EnoughSpaceAtSideRayTracing(IDiogenAgent agent, double importance) {
    super((IGeographicAgent) agent, importance);
  }

  private double space;

  public double getSpace() {
    return this.space;
  }

  public abstract boolean isLeftSide();

  @Override
  public double getSatisfaction() {
    double buildingSize = Math
        .sqrt(GeneralisationSpecifications.BUILDING_MIN_AREA)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double roadWidth = 2
        * ((IEmbeddedDeadEndArea) ((EmbeddedEnvironmentAgent) this.getAgent())
            .getFeature()).getDeadEnd().getRoot().getWidth();
    double distance = 2 * buildingSize + roadWidth;
    double step = buildingSize / 2;

    EmbeddedEnvironmentAgent agent = (EmbeddedEnvironmentAgent) this.getAgent();
    IEmbeddedDeadEndArea ee = (IEmbeddedDeadEndArea) agent.getFeature();

    IFeatureCollection<INetworkSection> roads = ee.getBlock()
        .getSurroundingNetwork();
    IFeatureCollection<INetworkSection> limits = new FT_FeatureCollection<INetworkSection>();
    for (INetworkSection section : roads) {
      if (!ee.getDeadEnd().getFeaturesConnectedToRoot().contains(section)
          && !ee.getDeadEnd().getFeatures().contains(section)) {
        limits.add(section);
      }
    }

    // if the sections are not in the good orientation, change left to right and
    // right t oleft
    boolean goodOrientation = true;
    if (!ee.getDeadEnd().getRoot().getGeom().coord().get(0)
        .equals2D(ee.getDeadEnd().getRootNode().getPosition())) {
      goodOrientation = false;
    }

    Set<INetworkSection> sectionsSet = ee.getDeadEnd().getFeatures();

    RayTracingFromLinear algo = new RayTracingFromLinear(sectionsSet,
        this.isLeftSide() && goodOrientation, step, distance, limits);
    algo.compute();
    this.space = distance - algo.getMaxDistance();
    return 100 - this.space * 100 / distance;
  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
