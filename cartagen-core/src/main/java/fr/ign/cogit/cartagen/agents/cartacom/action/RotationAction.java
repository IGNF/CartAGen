package fr.ign.cogit.cartagen.agents.cartacom.action;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingroad.BuildingOrientation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class RotationAction extends CartacomActionImpl {

  /**
   * Logger for this class
   */
  private static Logger LOGGER = Logger
      .getLogger(RotationAction.class.getName());

  public RotationAction(ICartacomAgent agent, GeographicConstraint constraint,
      double weight) {
    super(agent, constraint, weight);
  }

  // @Override
  // public CartacomAction getAggregatedAction() {
  // return new RotationAggregatedAction(this.getAgent(), this.getConstraint(),
  // this.getWeight());
  // }

  @Override
  public Object computeDescribingArgument() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc} Here the validity is: until the agent's environment is
   * modified. TODO: could take into account a number of time this action has
   * already failed on this agent.
   */
  @Override
  public FailureValidity computeFailureValidity() {
    return FailureValidity.ENVIRONMENT_MODIFIED;
  }

  @Override
  public ActionResult compute() throws InterruptedException {
    double angle = ((BuildingOrientation) this.getConstraint())
        .getRotationAngle();
    LOGGER.debug("Apply rotation of " + angle);
    this.getAgent().setGeom(
        CommonAlgorithms.rotation((IPolygon) this.getAgent().getGeom(), angle));
    return ActionResult.MODIFIED;
  }

}
