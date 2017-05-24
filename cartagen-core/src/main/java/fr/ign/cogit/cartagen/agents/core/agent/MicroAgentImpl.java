/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.core.state.MicroAgentState;
import fr.ign.cogit.cartagen.agents.core.state.MicroAgentStateImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgentImpl;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;

/**
 * An implementation of the MicroAgent interface
 * 
 * @author jgaffuri
 * 
 */
public abstract class MicroAgentImpl extends GeographicObjectAgentImpl
    implements MicroAgent {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#buildCurrentState(fr.ign.cogit
   * .agentgeoxygene.state.MicroAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public MicroAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new MicroAgentStateImpl(this, (MicroAgentState) previousState,
        action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getGeneralOrientation()
   */
  public double getGeneralOrientation() {
    return new OrientationMeasure(this.getFeature().getGeom())
        .getGeneralOrientation();
  }

  /**
   */
  private double initialGeneralOrientation;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialGeneralOrientation()
   */
  /**
   * @return
   */
  public double getInitialGeneralOrientation() {
    return this.initialGeneralOrientation;
  }

  public void computeInitialGeneralOrientation() {
    this.initialGeneralOrientation = new OrientationMeasure(
        this.getInitialGeom()).getGeneralOrientation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getSidesOrientation()
   */
  public double getSidesOrientation() {
    return new OrientationMeasure(this.getFeature().getGeom())
        .getSidesOrientation();
  }

  /**
   */
  private double initialSidesOrientation;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialSidesOrientation()
   */
  /**
   * @return
   */
  public double getInitialSidesOrientation() {
    return this.initialSidesOrientation;
  }

  public void computeInitialSidesOrientation() {
    this.initialSidesOrientation = new OrientationMeasure(this.getInitialGeom())
        .getGeneralOrientation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getSidesOrientationIndicator()
   */
  public double getSidesOrientationIndicator() {
    return new OrientationMeasure(this.getFeature().getGeom())
        .getSidesOrientationIndicator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getConvexity()
   */
  public double getConvexity() {
    return CommonAlgorithms.convexity(this.getFeature().getGeom());
  }

  /**
   */
  private double initialConvexity;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialConvexity()
   */
  /**
   * @return
   */
  public double getInitialConvexity() {
    return this.initialConvexity;
  }

  public void computeInitialConvexity() {
    this.initialConvexity = CommonAlgorithms.convexity(this.getInitialGeom());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getElongation()
   */
  public double getElongation() {
    return CommonAlgorithms.elongation(this.getFeature().getGeom());
  }

  /**
   */
  private double initialElongation;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialElongation()
   */
  /**
   * @return
   */
  public double getInitialElongation() {
    return this.initialElongation;
  }

  public void computeInitialElongation() {
    this.initialElongation = CommonAlgorithms.elongation(this.getInitialGeom());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgentImpl#
   * printInfosConsole ()
   */
  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
  }
}
