/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.core.state.MicroAgentState;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkableFeature;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkedFeatureState;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * @author JGaffuri
 * 
 */
public interface IMicroAgentGeneralisation
    extends MicroAgent, GAELLinkableFeature, GAELDeformable {

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
      Action action);

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getGeneralOrientation()
   */
  public double getGeneralOrientation();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialGeneralOrientation()
   */
  /**
   * @return
   */
  public double getInitialGeneralOrientation();

  public void computeInitialGeneralOrientation();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getSidesOrientation()
   */
  public double getSidesOrientation();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialSidesOrientation()
   */
  /**
   * @return
   */
  public double getInitialSidesOrientation();

  public void computeInitialSidesOrientation();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getSidesOrientationIndicator()
   */
  public double getSidesOrientationIndicator();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getConvexity()
   */
  public double getConvexity();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialConvexity()
   */
  /**
   * @return
   */
  public double getInitialConvexity();

  public void computeInitialConvexity();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getElongation()
   */
  public double getElongation();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MicroAgent#getInitialElongation()
   */
  /**
   * @return
   */
  public double getInitialElongation();

  public void computeInitialElongation();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgentImpl#
   * printInfosConsole ()
   */
  @Override
  public void printInfosConsole();

  /**
   * @return The agent's general orientation, in degree
   */
  public double getGeneralOrientationDegree();

  /**
   * @return The agent's sides orientation, in degree
   */
  public double getSidesOrientationDegree();

  @Override
  public IPointAgent getAgentPointReferant();

  @Override
  public ArrayList<GAELSegment> getSegmentsProximite();

  @Override
  public void goBackToState(GAELLinkedFeatureState linkedFeatureState);

  @Override
  public void setAgentPointReferant(IPointAgent agentPointReferant);

  // ---- truc package carto

  public IGeometry getSymbolExtent();

  public IGeometry getUsedSymbolExtent();

  // ---- fin truc package carto

}
